package org.boon.slumberdb;

import org.vertx.java.core.http.HttpServerRequest;

public class ResponseUtil {

    public static boolean setAllowOriginHeaders(HttpServerRequest request) {
        String origin = request.headers().get("Origin");
        if (origin != null) {
            request.response().putHeader("Access-Control-Allow-Origin", origin);
            request.response().putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            request.response().putHeader("Access-Control-Allow-Headers", "Content-Type");

            return true;
        }
        return false;
    }
}
