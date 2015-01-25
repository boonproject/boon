package org.boon.slumberdb.handlers;

import org.boon.core.Sys;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.*;

import static org.boon.Boon.*;
import static org.boon.Exceptions.die;
import static org.boon.Maps.map;

public class AdminSearchUsers implements Handler<HttpServerRequest> {
    private DataStoreServerConfig config;

    public AdminSearchUsers(DataStoreServerConfig config) {
        this.config = config;
    }

    @Override
    public void handle(HttpServerRequest event) {
        event.response().putHeader("Content-Type", "text/plain");
        event.response().setChunked(true);
        event.response().write(toJson(
                map("ok", true,
                        "sequence", 99,
                        "description", "Slumber DB",
                        "cpus", Runtime.getRuntime().availableProcessors(),
                        "free memory", Runtime.getRuntime().freeMemory(),
                        "total memory", Runtime.getRuntime().totalMemory(),
                        "JDK 1.7 or later", Sys.is1_7OrLater(),
                        "OS", System.getProperty("os.name"),
                        "Java version", System.getProperty("java.version")
                )
        ));
        event.response().write(config.toString());
        event.response().write(DataStoreConfig.load().toString());
        event.response().end();
    }
}
