package org.boon.slumberdb;

import org.boon.Logger;
import org.boon.Str;
import org.boon.StringScanner;
import org.boon.core.Sys;
import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.MethodAccess;
import org.boon.slumberdb.handlers.AdminFile;
import org.boon.slumberdb.handlers.AdminHandler;
import org.boon.slumberdb.handlers.AdminOptions;
import org.boon.slumberdb.handlers.AdminSearchUsers;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.protocol.requests.PingRequest;
import org.boon.slumberdb.service.server.DataStoreServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.*;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.slumberdb.ResponseUtil.setAllowOriginHeaders;

public class DataStoreVerticle extends Verticle {

    private final static int MAX_MESSAGE_SIZE = Sys.sysProp("org.boon.slumberdb.config.MaxDataSize", 20_000_000);
    private final boolean debug = org.boon.slumberdb.config.GlobalConfig.DEBUG;

    DataStoreServer dataStoreServer = new DataStoreServer() {
        @Override
        protected void sendToAllClients(String reply) {
            DataStoreVerticle.this.sendToAllClients(reply);
        }

        @Override
        protected void sendMessageToClientId(String clientId, String reply) {
            if (debug) {
                logger.info("DataStoreServer::sendMessageToClientId()", clientId, ProtocolConstants.prettyPrintMessageWithLinesTabs(reply));
            }
            DataStoreVerticle.this.sendMessageToClientId(clientId, reply);
        }

        @Override
        public boolean clientExistsStill(String clientId) {
            if (debug) {
                logger.info("DataStoreServer::clientExistsStill()", clientId);
            }
            return clientWebSocketMap.containsKey(clientId) || clientHttpMap.containsKey(clientId);
        }

        @Override
        protected void registerOutputHandler(String clientId, Object commChannel) {

            if (debug) {
                logger.info("DataStoreServer::registerOutputHandler()", clientId, commChannel);
            }
            DataStoreVerticle.this.registerOutputHandler(clientId, commChannel);
        }
    };

    protected Map<String, ServerWebSocket> clientWebSocketMap = new ConcurrentHashMap<>();
    protected Map<String, HttpServerResponse> clientHttpMap = new ConcurrentHashMap<>();
    private Logger logger = configurableLogger(DataStoreVerticle.class);
    private DataStoreServerConfig config;
    private int maxMessageSize;

    public void start() {
        logger.info("Data Store Service Starting");
        try {
            config = DataStoreServerConfig.load();
            dataStoreServer.init(config);
            JsonObject configOverrides = container.config();
            if (configOverrides.containsField("port")) {
                config.port(configOverrides.getInteger("port"));
            }
            puts("SERVER CONFIG", config.port());
            configureAndStartHttpServer(dataStoreServer.getServicesDefinition());
        } catch (Throwable ex) {

            logger.error(ex, "Data Store Service Starting FAILED");

        }
    }

    private void configureAndStartHttpServer(Map<String, ClassMeta<?>> servicesDefinition) {
        HttpServer server = vertx.createHttpServer();
        server.setTCPKeepAlive(true);
        server.setTCPNoDelay(true);
        server.setSoLinger(0);
        server.setCompressionSupported(config.httpCompression());

        if (config.maxFrameSize() == -1) {
            server.setMaxWebSocketFrameSize(MAX_MESSAGE_SIZE);
            maxMessageSize = MAX_MESSAGE_SIZE;
        } else {
            server.setMaxWebSocketFrameSize(config.maxFrameSize());
            maxMessageSize = config.maxFrameSize();
        }
        if (debug) {
            logger.info("DataStoreServer::configureAndStartHttpServer()", config);
        }

        server.websocketHandler(websocketHandler())
                .requestHandler(clientRequestHandler())
                .listen(config.port());
        HttpServer admin = vertx.createHttpServer();
        admin.setTCPKeepAlive(true);
        admin.setTCPNoDelay(true);
        admin.setSoLinger(0);
        admin.setCompressionSupported(config.httpCompression());
        admin.requestHandler(adminRouteMatchers(servicesDefinition));
        admin.listen(config.adminPort());

        puts("Admin port on", config.adminPort());
    }

    private Handler<HttpServerRequest> clientRequestHandler() {
        final String restURI = config.restURI();

        return new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest request) {
                handleRESTCall(restURI, request);
            }
        };
    }

    private Handler<ServerWebSocket> websocketHandler() {

        final String websocketURI = config.websocketURI();
        return new Handler<ServerWebSocket>() {
            public void handle(final ServerWebSocket ws) {
                if (debug) {
                    logger.info("websocket call", websocketURI, ws.path());
                }
                if (ws.path().equals(websocketURI)) {

                    ws.dataHandler(new Handler<Buffer>() {
                        @Override
                        public void handle(final Buffer buffer) {
                            handleWebSocketCall(ws, buffer);
                        }
                    }).closeHandler(new Handler<Void>() {
                        @Override
                        public void handle(Void aVoid) {
                            if (debug) {
                                logger.info("DataStoreServer::closeHandler()");

                            }
                            final Set<Map.Entry<String, ServerWebSocket>> entries = clientWebSocketMap.entrySet();
                            for (Map.Entry<String, ServerWebSocket> entry : entries) {

                                if (entry.getValue() == ws) {
                                    entries.remove(entry);
                                }
                            }
                        }
                    });
                } else {

                    if (debug) {
                        logger.info("BAD websocket call", ws.path());
                    }
                    ws.reject();
                }
            }
        };
    }

    private void routeMatch(RouteMatcher routeMatcher, AdminOptions adminOptions, String pattern, Handler<HttpServerRequest> handler) {
        routeMatcher.options(pattern, adminOptions);
        routeMatcher.get(pattern, handler);
        routeMatcher.post(pattern, handler);
        routeMatcher.options(pattern + "/", adminOptions);
        routeMatcher.get(pattern + "/", handler);
        routeMatcher.post(pattern + "/", handler);
    }

    private RouteMatcher adminRouteMatchers(Map<String, ClassMeta<?>> servicesDefinition) {
        RouteMatcher routeMatcher = new RouteMatcher();

        AdminOptions adminOptions = new AdminOptions();
        AdminHandler adminHandler = new AdminHandler(config, dataStoreServer.dataStoreConfig());
        for (String pattern : adminHandler.patterns() ) {
            routeMatch(routeMatcher, adminOptions, pattern, adminHandler);
        }
        routeMatch(routeMatcher, adminOptions, "/admin/search/users", new AdminSearchUsers(dataStoreServer));

        List<AdminFile> adminFiles = AdminFile.handlers(config);
        for (AdminFile adminFile : adminFiles) {
            routeMatcher.get(adminFile.file(), adminFile);
        }

        final Set<Map.Entry<String, ClassMeta<?>>> entries = servicesDefinition.entrySet();

        for (final Map.Entry<String, ClassMeta<?>> entry : entries) {

            String longName = entry.getValue().longName();
            longName = StringScanner.substringAfter(longName, "org.boon.slumberdb");
            longName = longName.replace('.', '/');

            for (MethodAccess methodAccess : entry.getValue().methods()) {
                if (methodAccess.hasAnnotation("serviceMethod")) {

                    puts("SERVICE REGISTERED UNDER", longName, "\n");

                    final String methodName = methodAccess.name();

                    String uri = Str.add(longName, "/", methodName);

                    final Handler<HttpServerRequest> handler = new Handler<HttpServerRequest>() {
                        public void handle(HttpServerRequest request) {

                            String ipAddress = request.remoteAddress().toString();

                            Map<String, String> map = MultiMapUtil.toMap(request.params());
                            map.put("method", methodName);
                            map.put("object", entry.getKey());
                            map.put("action", Action.METHOD_CALL.verb());
                            dataStoreServer.handleCallWithMap(ipAddress, map, request.uri(), request.response());
                        }
                    };
                    puts("    SERVICE METHOD REGISTERED UNDER", uri, uri.toLowerCase());
                    routeMatcher.get(uri, handler);
                    routeMatcher.get(uri.toLowerCase(), handler);
                    uri = Str.add("/slumberdb/", entry.getKey(), "/", methodName);
                    puts("    SERVICE METHOD ALSO REGISTERED UNDER", uri, uri.toLowerCase());
                    routeMatcher.get(uri, handler);
                    routeMatcher.get(uri.toLowerCase(), handler);

                    puts("\n\n");

                }
            }
        }

        return routeMatcher;
    }

    private void handleWebSocketCall(ServerWebSocket webSocket, Buffer buffer) {
        String payload = buffer.toString();
        if (PingRequest.isPing(payload)) {
            handlePingFromClient(payload, webSocket);
            return;
        }
        dataStoreServer.handleCallFromClient(buffer.toString(), webSocket);

    }

    public void stop() {

        dataStoreServer.stop();

    }

    private void handleRESTCall(final String restURI, final HttpServerRequest request) {
        if (debug) {
            logger.info("DataStoreVerticle::handleRESTCall()", restURI, request);

        }

        if (request.path().equals(restURI)) {

            if (request.method().equals("GET")) {
                handleRestGET(request);
            }
            else if (request.method().equals("POST")) {
                setAllowOriginHeaders(request);

                /* Create a buffer. */
                final Buffer body = new Buffer();
                /* Register a callback to get notified when we get the next block
                of gak from the POST. */
                request.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer buffer) {
                        body.appendBuffer(buffer);
                    }
                });

                /* Register a callback to notify us that we got all of the body. */
                request.endHandler(new VoidHandler() {
                    public void handle() {
                        final String textData = body.toString();
                        handleRestPOST(textData, request);

                    }
                });
            }
            else if (request.method().equals("OPTIONS")) {
                int status = setAllowOriginHeaders(request) ? 200 : 400;
                request.response().setStatusCode(status).end();
            }
        }
        else {
            request.response().setStatusCode(404).end();
        }
    }

    private void handleRestPOST(String textData, HttpServerRequest request) {

        try {
            dataStoreServer.handleCallFromClient(textData, request.response());
        }
        catch (Exception ex) {
            logger.error(ex, "Failed REST call", textData);
            request.response().end("[\"failed\"]");
        }

        if (    textData.startsWith(ProtocolConstants.SET_MASK)
                || textData.startsWith(ProtocolConstants.REMOVE_MASK)
                || textData.startsWith(ProtocolConstants.CLEAR_MASK)
        ) {
            request.response().end("[\"ok\"]");
        }
    }

    private void handleRestGET(HttpServerRequest request) {

        String ipAddress = request.remoteAddress().toString();

        dataStoreServer.handleCallWithMap(ipAddress, MultiMapUtil.toMap(request.params()), request.uri(), request.response());

        if (request.params().get(ProtocolConstants.ACTION_MAP_KEY).startsWith(ProtocolConstants.SET_VERB)) {
            request.response().end("[\"ok\"]");
        }

    }

    private void handlePingFromClient(String textData, Object object) {

        if (object instanceof ServerWebSocket) {

            ServerWebSocket ws = (ServerWebSocket) object;
            final InetSocketAddress inetSocketAddress = ws.remoteAddress();
            if (debug) logger.debug("GOT PING", textData, " sending pong to ", inetSocketAddress);
            ws.write(new Buffer("pong"));
        }
    }

    private void sendToAllClients(String reply) {
        if (debug)
            logger.info("sendToAllClients()::", reply);
        final Set<Map.Entry<String, ServerWebSocket>> entries = clientWebSocketMap.entrySet();
        for (Map.Entry<String, ServerWebSocket> entry : entries) {

            if (debug) logger.info("Sending BROADCAST to", entry.getKey());

            try {

                entry.getValue().write(new Buffer(reply));
            } catch (Exception ex) {
                logger.info(ex, "connection was found to be closed for client messageId", entry.getKey(), "when sending broadcast for key");
            }
        }
    }

    public void registerOutputHandler(String clientId, Object commChannel) {
        if (commChannel instanceof ServerWebSocket) {
            clientWebSocketMap.put(clientId, (ServerWebSocket) commChannel);
        } else if (commChannel instanceof HttpServerResponse) {
            clientHttpMap.put(clientId, (HttpServerResponse) commChannel);
        } else {
            die("registerOutputHandler failed wrong object for communication channel or null", clientId, commChannel);
        }
    }

    private void sendMessageToClientId(final String clientId, final String reply) {
        if (debug) {
            logger.info("DataStoreVerticle::sendMessageToClientId", clientId, reply);
        }

        final ServerWebSocket webSocket = clientWebSocketMap.get(clientId);

        if (webSocket != null) {
            try {
                webSocket.write(new Buffer(reply));
                if (debug) {
                    logger.info("DataStoreVerticle::sendMessageToClientId::DONE\n", clientId, reply, "\nDONE");
                }
            } catch (Exception ex) {
                if (reply.length() > maxMessageSize) {
                    die("You have exceeded the MAX MESSAGE SIZE", reply, "\nMax size set to",
                            maxMessageSize, "You are trying to send this much", reply.length(),
                            "Change org.boon.slumberdb.config.MaxDataSize system property");
                } else {
                    logger.error(ex, "DataStoreVerticle::sendMessageToClientId");
                }

            }

        } else {

            try {
                HttpServerResponse response = clientHttpMap.get(clientId);
                if (response != null) {
                    response.end(reply);
                }
            } catch (Exception ex) {

                if (debug) logger.debug(ex, "Unable to send out response from data store to http client", reply);

            }
        }
    }

}
