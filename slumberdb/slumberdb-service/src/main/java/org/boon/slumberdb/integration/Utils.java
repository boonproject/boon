package org.boon.slumberdb.integration;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 7/6/14.
 */
public class Utils {

    public static void runDataStoreServer(int port) throws IOException {
        runDataStoreServer("org.boon.slumberdb.DataStoreVerticle", port);
    }

    public static void runDataStoreServer(String main) throws IOException {
        runDataStoreServer(main, null);
    }

    public static void runDataStoreServer(String main, Integer port) throws IOException {

        PlatformManager platformManager = PlatformLocator.factory.createPlatformManager();

        JsonObject jsonObject = null;
        if (port != null) {
            jsonObject = new JsonObject();
            jsonObject.putNumber("port", port);
        }

        URL url = new File(".", "target/classes").getCanonicalFile().toURL();
        platformManager.deployVerticle(main, jsonObject, new URL[]{url}, 1, null,
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
