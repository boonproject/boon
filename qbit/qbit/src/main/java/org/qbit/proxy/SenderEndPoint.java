package org.qbit.proxy;

import org.qbit.message.MethodCall;
import org.qbit.service.BeforeMethodCall;
import org.qbit.service.EndPoint;
import org.qbit.service.impl.NoOpBeforeMethodCall;
import org.qbit.spi.ProtocolEncoder;

/**
 * Created by Richard on 10/1/14.
 */
public class SenderEndPoint implements EndPoint {


    final ProtocolEncoder encoder;
    final String address;
    private final Sender<String> sender;
    private final BeforeMethodCall beforeMethodCall;


    public SenderEndPoint(ProtocolEncoder encoder, String address, Sender<String> sender, BeforeMethodCall beforeMethodCall) {
        this.encoder = encoder;
        this.address = address;

        this.beforeMethodCall = beforeMethodCall == null ? new NoOpBeforeMethodCall() : beforeMethodCall;
        this.sender = sender;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public void call(MethodCall<Object> methodCall) {

        beforeMethodCall.before(methodCall);
        sender.send(methodCall.returnAddress(), encoder.encodeAsString(methodCall));
    }
}
