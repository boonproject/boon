package org.boon.slumberdb.handlers;

import org.boon.core.Sys;
import org.boon.slumberdb.ResponseUtil;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.Map;

import static org.boon.Boon.toPrettyJson;
import static org.boon.Maps.map;

public class AdminOptions implements Handler<HttpServerRequest> {
    private Map<String, String> system;
    protected DataStoreServerConfig dataStoreServerConfig;
    protected DataStoreConfig dataStoreConfig;

    public AdminOptions(DataStoreServerConfig dataStoreServerConfig, DataStoreConfig dataStoreConfig) {
        this.dataStoreServerConfig = dataStoreServerConfig;
        this.dataStoreConfig = dataStoreConfig;
    }

    @Override
    public void handle(HttpServerRequest request) {
        request.response().putHeader("Content-Type", "text/plain");
        ResponseUtil.setAllowOriginHeaders(request);
        request.response().setChunked(true);
        addContent(request);
        request.response().end();
    }

    protected void addContent(HttpServerRequest request) {
        system = heartMap();
        request.response().write(toPrettyJson(this));
    }

    protected Map<String, String> heartMap() {
        return map("ok", "" + true,
                "sequence", "" + 99,
                "description", "Slumber DB",
                "cpus", "" + Runtime.getRuntime().availableProcessors(),
                "free memory", "" + Runtime.getRuntime().freeMemory(),
                "total memory", "" + Runtime.getRuntime().totalMemory(),
                "JDK 1.7 or later", "" + Sys.is1_7OrLater(),
                "OS", System.getProperty("os.name"),
                "Java version", System.getProperty("java.version")
        );
    }
}
