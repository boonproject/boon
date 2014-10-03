package org.boon.qbit.vertx.impl;

import org.boon.qbit.vertx.Factory;
import org.qbit.message.MethodCall;
import org.qbit.service.method.impl.MethodCallImpl;
import org.vertx.java.core.http.ServerWebSocket;


public class FactoryImpl implements Factory{
    @Override
    public MethodCall<Object> createMethodCall(ServerWebSocket serverWebSocket) {

        MethodCallImpl impl = new MethodCallImpl();
        return null;
    }
}
