package org.boon.qbit.vertx;

import org.qbit.message.MethodCall;
import org.vertx.java.core.http.ServerWebSocket;

/**
 * Created by Richard on 10/2/14.
 */
public interface Factory {

    MethodCall<Object> createMethodCall(ServerWebSocket serverWebSocket);
}
