package org.boon.slumberdb.service.client;

import org.boon.Exceptions;
import org.boon.Logger;
import org.boon.core.Sys;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.service.config.Bucket;
import org.boon.slumberdb.service.config.Server;
import org.boon.slumberdb.service.protocol.Action;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.protocol.requests.*;
import org.boon.slumberdb.service.results.BatchResult;
import org.boon.slumberdb.service.results.SingleResult;
import org.boon.slumberdb.stores.DataOutputQueue;
import org.boon.slumberdb.stores.DataStoreSource;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static org.boon.Boon.configurableLogger;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 7/5/14.
 */
public class ServerProxy {

    private final static int MAX_MESSAGE_SIZE = Sys.sysProp("org.boon.slumberdb.config.MaxDataSize", 20_000_000);
    private final Server server;
    private final Server backup;
    private final String clientId;
    private final Logger logger = configurableLogger(this.getClass());
    private final Vertx vertx;
    private final boolean verbose;
    private final boolean inVertx;
    private final DataOutputQueue queue;
    private final String uri;
    private final int maxDataSendSize;
    long messageId = 0;
    private volatile boolean connected;
    private WebSocket webSocket;
    private volatile HttpClient clientToServerWeAreAProxyFor;
    private volatile boolean errorConnecting;
    private Server currentServer;
    private LinkedBlockingQueue<String> queueOut = new LinkedBlockingQueue<>(100_000);


    public ServerProxy(Bucket bucket, Vertx vertx, boolean verbose, String uri,
                       DataOutputQueue queue, boolean inVertx, String clientId,
                       int maxDataSendSize) {

        if (maxDataSendSize != 0) {

            this.maxDataSendSize = maxDataSendSize;
        } else {
            this.maxDataSendSize = MAX_MESSAGE_SIZE;
        }

        this.server = new Server(bucket.server(), bucket.serverPort());

        if (bucket.backupServer() == null) {
            this.backup = new Server(bucket.server(), bucket.serverPort());
        } else {
            this.backup = new Server(bucket.backupServer(), bucket.backupServerPort());
        }

        this.vertx = vertx;
        this.verbose = verbose;
        this.queue = queue;
        this.uri = uri;
        this.inVertx = inVertx;

        this.clientId = clientId;
        currentServer = server;
    }

    /**
     * RESPONSES FROM SERVER.
     *
     * @param message
     */
    private void handleMessageFromServer(String message) {


        if (verbose) {
            puts("WEBSOCKET RESPONSE", ProtocolConstants.prettyPrintMessage(message));
            puts("WEBSOCKET RESPONSE");
            puts(ProtocolConstants.prettyPrintMessageWithLinesTabs(message));
        }

        try {
            if (message.startsWith(Action.GET.response().startsWith()) ||
                    message.startsWith(Action.SET_BROADCAST.response().startsWith())) {

                final SingleResult singleResult = SingleResult.fromTextMessage(message);
                queue.put(singleResult);


            } else if (message.startsWith(Action.BATCH_READ.response().startsWith())) {
                queue.put(BatchResult.fromTextMessage(message));

            } else {
                if (verbose) {
                    puts("Unknown action", message);
                }
            }
        } catch (Exception ex) {
            logger.error(ex, "ServerProxy::handleMessageFromServer\n", message);
        }

    }

    private boolean send(String send) {


        if (verbose) {
            puts("WEBSOCKET", ProtocolConstants.prettyPrintMessage(send), "\n");
            puts("WEBSOCKET");
            puts(ProtocolConstants.prettyPrintMessageWithLinesTabs(send));
        }

        if (send.length() > maxDataSendSize) {
            die("You have exceeded the MAX MESSAGE SIZE", send, "\nMax size set to",
                    maxDataSendSize, "You are trying to send this much", send.length(),
                    "Change org.boon.slumberdb.config.MaxDataSize system property");
        }

        if (webSocket() == null) {
            connected = false;
            handleWebSocketDownSend(send);
            return false;
        } else {
            drainSendQueue();
            return doSend(send);
        }

    }

    public void connect() {

        if (verbose) {
            puts("Connect called");
        }
        webSocket(null);

        connectHttpClient();
        connectWebSocket();

        vertx.setPeriodic(30_000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {

                sendPing();
            }
        });
    }

    private void sendPing() {

        final WebSocket webSocket = webSocket();
        if (webSocket != null) {
            webSocket.write(new Buffer(PingRequest.SINGLETON.formTextRequest()));
        }
    }

    public void connectHttpClient() {


        errorConnecting = false;

        if (clientToServerWeAreAProxyFor == null) {

            clientToServerWeAreAProxyFor = vertx.createHttpClient()
                    .setPort(server.port())
                    .setHost(server.host())
                    .setTryUseCompression(true)
                    .setKeepAlive(true)
                    .setTCPNoDelay(true).setSoLinger(-1)
                    .setConnectTimeout(10_000).setMaxWebSocketFrameSize(maxDataSendSize);

            clientToServerWeAreAProxyFor.exceptionHandler(new Handler<Throwable>() {
                @Override
                public void handle(Throwable throwable) {


                    puts("GOT CONNECTION EXCEPTION");
                    throwable.printStackTrace();
                    if (throwable instanceof ConnectException) {

                        if (!inVertx) {
                            synchronized (this) {
                                if (verbose) puts("Detected down connection");
                                if (currentServer == server) {
                                    currentServer = backup;
                                } else if (currentServer == backup) {
                                    currentServer = server;
                                }
                            }
                        } else {
                            if (verbose) puts("Detected down connection");
                            if (currentServer == server) {
                                currentServer = backup;
                            } else if (currentServer == backup) {
                                currentServer = server;
                            }

                        }

                        clientToServerWeAreAProxyFor = null;
                        webSocket(null);
                        connected = false;
                        errorConnecting = true;

                    } else {
                        Exceptions.handle(throwable, server);
                    }
                }
            });
        }

    }

    private void connectWebSocket() {


        if (verbose) puts("Calling connectWebSocket");

        if (clientToServerWeAreAProxyFor == null) {
            this.connectHttpClient();
        }


        webSocket(null);

        clientToServerWeAreAProxyFor.connectWebsocket(uri, new Handler<WebSocket>() {
            @Override
            public void handle(WebSocket webSocket) {

                if (verbose) puts("Connected WebSocket", webSocket);

                webSocket(webSocket);

                connectToFrameStream(webSocket);

                connectToExceptionHandler(webSocket);

                connectToEndHandler(webSocket);

            }
        });

    }

    private synchronized void webSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
        if (webSocket == null) {
            connected = false;
        } else {
            connected = true;
        }
    }

    private synchronized WebSocket webSocket() {
        return this.webSocket;
    }

    public boolean batchSetIfNotExists(String clientId, List<Entry<String, String>> batch) {
        return doBatchSet(null, Action.SET_BATCH_IF_NOT_EXISTS, clientId, batch);
    }

    public boolean batchSetIfNotExists(DataStoreSource source, String clientId, List<Entry<String, String>> batch) {
        return doBatchSet(source, Action.SET_BATCH_IF_NOT_EXISTS, clientId, batch);
    }

    public boolean batchSet(String clientId, List<Entry<String, String>> batch) {
        return doBatchSet(null, Action.SET_BATCH, clientId, batch);
    }

    public boolean batchSet(DataStoreSource source, String clientId, List<Entry<String, String>> batch) {
        return doBatchSet(source, Action.SET_BATCH, clientId, batch);
    }

    public boolean doBatchSet(Action action, String clientId, List<Entry<String, String>> batch) {
        return doBatchSet(null, action, clientId, batch);
    }

    public boolean doBatchSet(DataStoreSource source, Action action, String clientId, List<Entry<String, String>> batch) {

        boolean allSent = true;

        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (Entry<String, String> entry : batch) {
            messageId++;

            keys.add(entry.key());

            values.add(entry.value());
            if (keys.size() >= 10_000) {

                BatchSetRequest batchRequest = new BatchSetRequest(source, action, messageId, clientId, keys, values);

                if ( !send(batchRequest.formTextRequest()) ) {
                    allSent = false;
                }
                keys.clear();
                values.clear();
            }
        }

        if (keys.size() > 0) {
            messageId++;

            BatchSetRequest batchRequest = new BatchSetRequest(source, messageId, clientId, keys, values);
            return send(batchRequest.formTextRequest());
        }

        return allSent;

    }

    public boolean batchGet(final String clientId, Collection<String> batch) {


        boolean sent = false;

        if (batch.size() < 100) {
            messageId++;

            ReadBatchRequest batchRequest = new ReadBatchRequest(messageId, clientId, batch);

            sent = send(batchRequest.formTextRequest());

        } else {
            List<String> list = new ArrayList<>();
            for (String key : batch) {
                messageId++;

                list.add(key);
                if (list.size() >= 100) {

                    ReadBatchRequest batchRequest = new ReadBatchRequest(messageId, clientId, list);


                    sent &= send(batchRequest.formTextRequest());
                    list.clear();
                }

            }


            if (list.size() > 0) {
                messageId++;


                ReadBatchRequest batchRequest = new ReadBatchRequest(messageId, clientId, list);


                return send(batchRequest.formTextRequest());

            }


        }


        return sent;

    }


    private void drainSendQueue() {
        String send = queueOut.poll();

        while (send != null) {

            if (!doSend(send)) {
                break;
            }
            send = queueOut.poll();

        }
    }

    private boolean doSend(String send) {


        try {
            webSocket().write(new Buffer(send));
            return true;
        } catch (Exception ex) {
            puts("UNABLE TO SEND", send);
            ex.printStackTrace();
            webSocket(null);
            handleWebSocketDownSend(send);
            return false;
        }
    }

    private void handleWebSocketDownSend(String send) {
        if (!queueOut.offer(send)) {
            die("Unable able to send, output buffer is full error connecting=", errorConnecting, "connected = ", connected,
                    "output buffer size=", queueOut.size()
            );
        }
    }


    private void connectToExceptionHandler(final WebSocket webSocket) {
        webSocket.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {


                if (verbose) {
                    puts("Exception!", throwable);
                }

                if (webSocket != null) {

                    try {
                        webSocket.close();
                    } catch (Exception ex) {
                        if (verbose) {
                            puts("Unable to close websocket");
                            ex.printStackTrace();
                        }
                    }
                }
                errorConnecting = true;

                webSocket(null);
            }
        });
    }

    private void connectToFrameStream(WebSocket webSocket) {


        webSocket.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer buffer) {

                if (verbose) {
                    puts("Recieved data", buffer.toString());

                }

                handleMessageFromServer(buffer.toString());

            }
        });
    }


    private void connectToEndHandler(final WebSocket webSocket) {
        webSocket.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void aVoid) {

                if (verbose) puts("Websocket connection was disconnected", webSocket);

                webSocket(null);

                connectWebSocket();
            }

        });
    }


    public boolean connected() {
        if (verbose) puts("IS CONNECTED CALLED", connected);
        return connected;
    }

    public boolean errorConnecting() {
        return errorConnecting;
    }

    public boolean broadcastSet(String clientId, String key, String value) {
        return send(
                setRequestPayLoadGeneration(Action.SET_BROADCAST, null, clientId, key, value)
        );

    }

    public boolean set(DataStoreSource source, String clientId, String key, String value) {

        return send(
                setRequestPayLoadGeneration(Action.SET_SOURCE, source, clientId, key, value)
        );
    }

    public boolean set(String clientId, String key, String value) {

        return send(
                setRequestPayLoadGeneration(Action.SET, null, clientId, key, value)
        );
    }


    public boolean setIfNotExists(String clientId, String key, String value) {

        return send(
                setRequestPayLoadGeneration(Action.SET_IF_NOT_EXIST, null, clientId, key, value)
        );
    }

    public void flush() {
        drainSendQueue();
    }


    public boolean get(String clientId, DataStoreSource source, String key) {


        return send(
                getRequestPayLoadGeneration(Action.GET_SOURCE, clientId, source, key)
        );

    }

    public boolean get(String clientId, String key) {


        return send(
                getRequestPayLoadGeneration(Action.GET, clientId, null, key)
        );

    }


    public boolean remove(String clientId, DataStoreSource source, String key) {

        return send(
                source == DataStoreSource.ALL ?
                        removeRequestPayloadGeneration(Action.REMOVE, clientId, null, key) :
                        removeRequestPayloadGeneration(Action.REMOVE_SOURCE, clientId, source, key)
        );
    }

    public boolean getStats(String clientId, DataStoreSource source) {

        return send(

                statRequestPayloadGeneration(Action.GET_STATS, clientId, source)
        );
    }

    public boolean clearStats(String clientId, DataStoreSource source) {

        return send(

                statRequestPayloadGeneration(Action.CLEAR_STATS, clientId, source)
        );
    }


    public boolean getFromMemory(String clientId, String key) {


        return send(
                getRequestPayLoadGeneration(Action.GET_MEM, clientId, null, key)
        );

    }

    public boolean getFromFile(String clientId, String key) {


        return send(
                getRequestPayLoadGeneration(Action.GET_LOCAL_DB, clientId, null, key)
        );

    }

    private String statRequestPayloadGeneration(Action action, String clientId, DataStoreSource source) {

        messageId++;
        return StatsRequest.createTextMessage(source, action, messageId, clientId);
    }


    private String removeRequestPayloadGeneration(Action action, String clientId, DataStoreSource source, String key) {

        messageId++;
        return RemoveRequest.createTextMessage(source, action, messageId, clientId, key);
    }


    private String getRequestPayLoadGeneration(Action action, String clientId, DataStoreSource source, String key) {

        messageId++;
        return GetRequest.createTextMessage(source, action, messageId, clientId, key);
    }

    private String setRequestPayLoadGeneration(Action action, DataStoreSource source, String clientId, String key, String value) {

        messageId++;
        return SetRequest.createTextMessage(action, messageId, source, clientId, key, value);
    }

    public DataStoreAdminClient admin() {
        return new DataStoreAdminClient() {
            @Override
            public void turnOnRequestLogging() {

            }

            @Override
            public void turnOnMetricsTracking() {

            }

            @Override
            public void turnOnSendLogsToClient() {

            }
        };
    }

}
