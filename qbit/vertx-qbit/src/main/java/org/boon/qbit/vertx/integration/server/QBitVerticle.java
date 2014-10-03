package org.boon.qbit.vertx.integration.server;

import org.boon.qbit.vertx.integration.model.EmployeeManagerImpl;
import org.qbit.QBit;
import org.qbit.message.MethodCall;
import org.qbit.message.Response;
import org.qbit.queue.ReceiveQueue;
import org.qbit.service.ServiceBundle;
import org.qbit.spi.ProtocolEncoder;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Boon.puts;

public class QBitVerticle extends Verticle {

    private ServiceBundle serviceBundle;

    private  HttpServer httpServer;

    private ReceiveQueue<Response<Object>> responses;

    private ProtocolEncoder encoder;


    private Map<String, ServerWebSocket> webSocketMap = new ConcurrentHashMap<>();

    public void start() {


        container.logger().info("QBitVerticle started");

        serviceBundle = QBit.factory().createBundle("/services");

        serviceBundle.addService("/employeeService", new EmployeeManagerImpl());

        encoder = QBit.factory().createEncoder();

        httpServer = vertx.createHttpServer();
        httpServer.setTCPKeepAlive(true);
        httpServer.setTCPNoDelay(true);
        httpServer.setSoLinger(0);
        httpServer.setMaxWebSocketFrameSize(100_000_000);


        httpServer.websocketHandler(new Handler<ServerWebSocket>() {
            @Override
            public void handle(ServerWebSocket event) {

                puts("GOT CONNECTION", event.path(), event.uri(), serviceBundle.address());

                if (event.uri().startsWith(serviceBundle.address())) {
                    handleWebSocket(event);
                }
            }
        }).requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(HttpServerRequest event) {

                event.response().end("pong\n");
            }
        });

        httpServer.listen(8080);

        vertx.setPeriodic(50, new Handler<Long>() {
            @Override
            public void handle(Long event) {
                handleServiceBundleFlush();
            }
        });

        vertx.setPeriodic(5, new Handler<Long>() {
            @Override
            public void handle(Long event) {
                drainServiceQueue();
            }
        });


        responses = serviceBundle.responses();



    }

    private void drainServiceQueue() {
        final Iterable<Response<Object>> responsesBatch = responses.readBatch();

        for (Response<Object> response : responsesBatch) {
            final ServerWebSocket serverWebSocket = webSocketMap.get(response.returnAddress());

            if (serverWebSocket != null) {
                String responseAsText = encoder.encodeAsString(response);
                serverWebSocket.writeTextFrame(responseAsText);
            }
        }

    }


    private  void handleWebSocket(final ServerWebSocket websocket) {

        websocket.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer event) {
                handleWebSocketData(websocket, event.toString());
            }
        });

        websocket.closeHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {

                handleWebSocketClosed(websocket);
            }
        });

    }

    private  void handleWebSocketClosed(ServerWebSocket websocket) {

    }

    private  void handleWebSocketData(ServerWebSocket websocket, String message) {


        final MethodCall<Object> methodCall = QBit.factory().createMethodCallToBeParsedFromBody(websocket.remoteAddress().toString(), message);
        serviceBundle.call(methodCall);

        puts("Websocket data", methodCall.returnAddress(), websocket, message);

        webSocketMap.put(methodCall.returnAddress(), websocket);


    }


    private void handleServiceBundleFlush() {
        serviceBundle.flushSends();
    }

}
