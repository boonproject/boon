package org.boon.slumberdb.handlers;

import org.boon.slumberdb.ResponseUtil;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public class AdminOptions implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        request.response().putHeader("Content-Type", "text/plain");
        ResponseUtil.setAllowOriginHeaders(request);
        request.response().end();
    }
}
