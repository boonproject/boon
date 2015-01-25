package org.boon.slumberdb.handlers;

import org.boon.slumberdb.MultiMapUtil;
import org.boon.slumberdb.service.protocol.ProtocolConstants;
import org.boon.slumberdb.service.server.DataStoreServer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import java.util.Map;

public class AdminSearchUsers implements Handler<HttpServerRequest> {
    private DataStoreServer dataStoreServer;

    public AdminSearchUsers(DataStoreServer dataStoreServer) {
        this.dataStoreServer = dataStoreServer;
    }

    @Override
    public void handle(HttpServerRequest request) {
        String ipAddress = request.remoteAddress().toString();
        Map<String, String> map = MultiMapUtil.toMap(request.params());
        map.put(ProtocolConstants.Search.HANDLER_KEY, "org.boon.slumberdb.search.BaseSearchHandler");
        map.put(ProtocolConstants.Search.LIMIT_KEY, Integer.toString(ProtocolConstants.Search.LIMIT_VALUE));

        dataStoreServer.handleCallWithMap(ipAddress, map, request.uri(), request.response());
    }
}
