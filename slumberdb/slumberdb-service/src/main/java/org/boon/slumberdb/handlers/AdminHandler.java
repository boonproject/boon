package org.boon.slumberdb.handlers;

import org.boon.core.Sys;
import org.boon.slumberdb.ResponseUtil;
import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.slumberdb.service.config.DataStoreServerConfig;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.Map;

import static org.boon.Boon.toJson;
import static org.boon.Boon.toPrettyJson;
import static org.boon.Maps.map;

public class AdminHandler implements Handler<HttpServerRequest> {
    private Map<String, String> system;
    protected DataStoreServerConfig dataStoreServerConfig;
    protected DataStoreConfig dataStoreConfig;

    public AdminHandler(DataStoreServerConfig dataStoreServerConfig, DataStoreConfig dataStoreConfig) {
        this.dataStoreServerConfig = dataStoreServerConfig;
        this.dataStoreConfig = dataStoreConfig;
    }

    public String[] patterns() {
        return new String[] {
                "/admin/heartbeat",
                "/admin/heartbeat/system",
                "/admin/heartbeat/dataStoreServerConfig",
                "/admin/heartbeat/dataStoreConfig"
        };
    }

    @Override
    public void handle(HttpServerRequest request) {
        request.response().putHeader("Content-Type", "text/plain");
        ResponseUtil.setAllowOriginHeaders(request);
        request.response().setChunked(true);
        switch (request.uri()) {
            case "/admin/heartbeat":
            case "/admin/heartbeat/":
                handleHeartbeat(request);
                break;

            case "/admin/heartbeat/system":
            case "/admin/heartbeat/system/":
                handleSystem(request);
                break;

            case "/admin/heartbeat/dataStoreServerConfig":
            case "/admin/heartbeat/dataStoreServerConfig/":
                handleDataStoreServerConfig(request);
                break;

            case "/admin/heartbeat/dataStoreConfig":
            case "/admin/heartbeat/dataStoreConfig/":
                handleDataStoreConfig(request);
                break;
        }
        request.response().end();
    }

    private void handleSystem(HttpServerRequest request) {
        request.response().write(toPrettyJson(new SysBean()));
    }

    private void handleDataStoreServerConfig(HttpServerRequest request) {
        request.response().write(toPrettyJson(dataStoreServerConfig));
    }

    private void handleDataStoreConfig(HttpServerRequest request) {
        request.response().write(toPrettyJson(dataStoreConfig));
    }

    private void handleHeartbeat(HttpServerRequest request) {
        request.response().write(toJson(heartMap()));
        request.response().write(dataStoreServerConfig.toString());
        request.response().write(dataStoreConfig.toString());
    }

    protected Map<String, String> heartMap() {
        SysBean sysBean = new SysBean();
        return map("ok", "" + true,
                "sequence", "99",
                "description", sysBean.description,
                "cpus", sysBean.cpus.toString(),
                "free memory", sysBean.freeMemory.toString(),
                "total memory", sysBean.totalMemory.toString(),
                "JDK 1.7 or later", sysBean.jdk17OrLater.toString(),
                "OS", sysBean.os,
                "Java version", sysBean.javaVersion
        );
    }

    /* inner */ class SysBean {
        Long freeMemory = Runtime.getRuntime().freeMemory();
        Long totalMemory = Runtime.getRuntime().totalMemory();
        Integer cpus = Runtime.getRuntime().availableProcessors();
        String os = System.getProperty("os.name");
        Boolean jdk17OrLater = Sys.is1_7OrLater();
        String javaVersion = System.getProperty("java.version");
        String description = "Slumber DB";
    }
}
