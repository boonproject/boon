package org.boon.qbit.vertx.integration.server;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

import java.io.File;
import java.net.URL;

import static org.boon.Boon.puts;

public class QBitServiceMain {


    public static void main(String... args) throws Exception {


        PlatformManager platformManager = PlatformLocator.factory.createPlatformManager();

        JsonObject jsonObject = new JsonObject();

        URL url = new File(".", "target/classes").getCanonicalFile().toURL();
        platformManager.deployVerticle("org.boon.qbit.vertx.integration.server.QBitVerticle", jsonObject, new URL[]{url}, 1, null,
                new Handler<AsyncResult<String>>() {
                    @Override
                    public void handle(AsyncResult<String> stringAsyncResult) {
                        if (stringAsyncResult.succeeded()) {
                            puts("Launched verticle");
                        }
                    }
                }
        );

        // Prevent the JVM from exiting
        System.in.read();
    }
}
