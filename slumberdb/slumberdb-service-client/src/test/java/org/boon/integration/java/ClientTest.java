package org.boon.integration.java;

import org.boon.slumberdb.service.client.DataStoreClient;
import org.boon.slumberdb.service.client.DataStoreFactory;
import org.boon.slumberdb.service.client.DataStoreSimpleHttpClientProvider;
import org.boon.slumberdb.service.client.DataStoreVertxWebSocketClientProvider;
import org.boon.slumberdb.service.config.DataStoreClientConfig;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.stores.queue.MapOutputQueue;
import org.boon.core.Sys;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.http.WebSocketFrame;
import org.vertx.testtools.TestVerticle;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.slumberdb.service.config.Bucket.bucket;
import static org.boon.Boon.puts;
import static org.vertx.testtools.VertxAssert.fail;
import static org.vertx.testtools.VertxAssert.testComplete;

/**
 * Simple integration test which shows tests deploying other verticles, using the Vert.x API etc
 */
public class ClientTest extends TestVerticle {


    MapOutputQueue mapOutputQueue;

    DataStoreClientConfig config;
    Thread thread;

    public void configWebSocket() {


        String server1 = "localhost";

        String server2 = "localhost";

        String server3 = "localhost";


        config = DataStoreClientConfig.config().buckets(
                bucket(server1), bucket(server2), bucket(server3),

                bucket(server1), bucket(server2), bucket(server3),

                bucket(server1), bucket(server2), bucket(server3)

        );

        config.clientId("TEST_1");
        mapOutputQueue = new MapOutputQueue();
        DataStoreVertxWebSocketClientProvider provider = new DataStoreVertxWebSocketClientProvider();
        provider.init(vertx, new MapOutputQueue(), config, true, true);
        provider.registerSupplier();
    }

    //@Test broke probably obsolete see client-integration-test under ds. That is where the testing is happening now.
    public void basicWebSocket() {

        configWebSocket();

        final AtomicBoolean setSent = new AtomicBoolean();


        // Create an HTTP server which just sends back OK response immediately
        vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {
            @Override
            public void handle(final ServerWebSocket serverWebSocket) {
                serverWebSocket.frameHandler(new Handler<WebSocketFrame>() {
                    @Override
                    public void handle(final WebSocketFrame webSocketFrame) {
                        puts(webSocketFrame.textData());
                        if (webSocketFrame.textData().startsWith("get")) {


                            serverWebSocket.writeTextFrame("response" + ProtocolConstants.DELIMITER_STR + "BOB"
                                    + ProtocolConstants.DELIMITER_STR + "BOB");
                        } else if (webSocketFrame.textData().startsWith("set")) {
                            puts("GOT SET");
                            setSent.compareAndSet(false, true);
                        }
                    }
                });
            }
        }).listen(config.buckets().get(0).serverPort());

        Sys.sleep(100);


        final DataStoreClient client = DataStoreFactory.createClient("TEST", null);
        client.set("RICK", "WAS HERE");

        client.get("BOB");

        vertx.setTimer(4_000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {


                if (!setSent.get()) {
                    fail("Failed test");
                } else {
                    testComplete();
                }
            }


        });


    }

    //@Test broke probably obsolete see client-integration-test under ds. That is where the testing is happening now.
    public void basicWebSocketManyRequests() {

        configWebSocket();

        final AtomicBoolean setSent = new AtomicBoolean();


        // Create an HTTP server which just sends back OK response immediately
        vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {
            @Override
            public void handle(final ServerWebSocket serverWebSocket) {
                serverWebSocket.frameHandler(new Handler<WebSocketFrame>() {
                    @Override
                    public void handle(final WebSocketFrame webSocketFrame) {
                        puts(webSocketFrame.textData());
                        if (webSocketFrame.textData().startsWith("get")) {


                            serverWebSocket.writeTextFrame("response" + ProtocolConstants.DELIMITER_STR + "BOB"
                                    + ProtocolConstants.DELIMITER_STR + "BOB");
                        } else if (webSocketFrame.textData().startsWith("set")) {
                            puts("GOT SET");
                            setSent.set(true);
                        }
                    }
                });
            }
        }).listen(config.buckets().get(0).serverPort());


        vertx.setTimer(1_000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {


                final DataStoreClient client = DataStoreFactory.createClient("TEST", null);

                client.get("BOB");

                for (int index = 0; index < 5; index++) {
                    client.set("RICK", "WAS HERE");
                }


            }


        });


        vertx.setTimer(10_000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {


                if (!setSent.get()) {
                    fail("Failed test");
                } else {
                    testComplete();
                }
            }


        });


    }

    @Test
    public void basicREST() {

        mapOutputQueue = new MapOutputQueue();

        DataStoreSimpleHttpClientProvider provider = new DataStoreSimpleHttpClientProvider();
        provider.init("http://localhost:9090" + ProtocolConstants.DEFAULT_REST_URI, "httpTest", mapOutputQueue, true);
        provider.registerSupplier();

        final AtomicBoolean setSent = new AtomicBoolean();


        // Create an HTTP server which just sends back OK response immediately
        final HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest httpServerRequest) {

                if (httpServerRequest.method().equals("POST")) {
                    final Buffer buffer = new Buffer();
                    httpServerRequest.dataHandler(new Handler<Buffer>() {
                                                      @Override
                                                      public void handle(Buffer buff) {
                                                          buffer.appendBuffer(buff);
                                                      }
                                                  }
                    );

                    httpServerRequest.endHandler(new Handler<Void>() {
                        @Override
                        public void handle(Void aVoid) {
                            puts(buffer.toString());
                            System.out.flush();
                            Sys.sleep(100);
                            httpServerRequest.response().end();
                        }
                    });
                } else {
                    httpServerRequest.response().end("response" + ProtocolConstants.DELIMITER_STR + "BOB"
                            + ProtocolConstants.DELIMITER_STR + "BOB");


                }
            }
        });

        httpServer.listen(9090);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final DataStoreClient client = DataStoreFactory.createClient("TEST", null);
                client.set("RICK", "WAS HERE");
                client.get("BOB");

            }
        });

        thread.start();

        vertx.setTimer(9_000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {
                testComplete();
                if (!setSent.compareAndSet(true, false)) {

                    fail("Failed test");
                }


            }
        });


    }

    private void configHttpClient() {

    }

}
