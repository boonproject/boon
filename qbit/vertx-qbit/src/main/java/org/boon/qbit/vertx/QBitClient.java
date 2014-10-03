package org.boon.qbit.vertx;

import org.boon.Boon;
import org.boon.Logger;
import org.boon.Str;
import org.boon.StringScanner;
import org.boon.core.Conversions;
import org.boon.core.HandlerWithErrorHandling;
import org.boon.core.Handlers;
import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.MethodAccess;
import org.boon.primitive.Arry;
import org.qbit.QBit;
import org.qbit.message.MethodCall;
import org.qbit.message.Response;
import org.qbit.proxy.Sender;
import org.qbit.queue.Queue;
import org.qbit.queue.ReceiveQueue;
import org.qbit.queue.SendQueue;
import org.qbit.queue.impl.BasicQueue;
import org.qbit.service.BeforeMethodCall;
import org.qbit.service.method.impl.MethodCallImpl;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.WebSocket;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static org.boon.Exceptions.die;
import static org.qbit.service.Protocol.PROTOCOL_ARG_SEPARATOR;

/**
 * Created by Richard on 10/2/14.
 */
public class QBitClient {


    private volatile boolean closed;

    private final String host;
    private final int port;
    private final String uri;
    private Vertx vertx;
    private final BlockingQueue<WebSocket> connectionQueue = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<String> queueToServer = new ArrayBlockingQueue<>(1000);
    private final Queue<String> queueFromServer;


    ScheduledFuture<?> scheduledFuture;

    public void startReturnProcessing() {
        final ReceiveQueue<String> receiveQueue = queueFromServer.receiveQueue();

        scheduledFuture = Executors.newScheduledThreadPool(2).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {


                try {

                    while (true) {
                        String poll = receiveQueue.pollWait();

                        while (poll != null) {

                            final Response<Object> response = QBit.factory().createResponse(poll);

                            final String[] split = StringScanner.split(response.returnAddress(), (char)PROTOCOL_ARG_SEPARATOR);
                            HandlerKey key = new HandlerKey(split[1], response.id());



                            final org.boon.core.Handler handler = handlers.get(key);

                            if (handler instanceof HandlerWithErrorHandling) {
                                HandlerWithErrorHandling handling = (HandlerWithErrorHandling) handler;


                                if (response.wasErrors()) {
                                    handling.errorHandler().handle(response.body());
                                } else {
                                    handling.handle(response.body());
                                }
                            } else if (handler instanceof org.boon.core.Handler) {
                                handler.handle(response.body());
                            }

                            poll = receiveQueue.pollWait();
                        }
                    }
                }catch (Exception ex) {
                    logger.error(ex, "Problem handling queue");
                }
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }


    private class HandlerKey {
        final String returnAddress;
        final long messageId;

        private HandlerKey(String returnAddress, long messageId) {
            this.returnAddress = returnAddress;
            this.messageId = messageId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HandlerKey that = (HandlerKey) o;

            if (messageId != that.messageId) return false;
            if (returnAddress != null ? !returnAddress.equals(that.returnAddress) : that.returnAddress != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = returnAddress != null ? returnAddress.hashCode() : 0;
            result = 31 * result + (int) (messageId ^ (messageId >>> 32));
            return result;
        }
    }

    private Map<HandlerKey, org.boon.core.Handler> handlers = new ConcurrentHashMap<>();


    private Logger logger = Boon.logger(QBitClient.class);

    private WebSocket webSocket;

    public QBitClient(String host, int port, String uri, Vertx vertx){

        this.host = host;
        this.port = port;
        this.uri = uri;
        this.vertx = vertx==null ? VertxFactory.newVertx() : vertx;

        connect();

        queueFromServer = new BasicQueue<>(
                Boon.joinBy('-', "QBitClient", host, port, uri), 5, TimeUnit.MILLISECONDS, 20);


    }



    private WebSocket webSocket() {
        if (webSocket==null) {
            try {
                webSocket = connectionQueue.poll(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.interrupted();
                die("QBitClient::Unable to connect", host, port);
            }

        }
        return webSocket;
    }

    public void send(String newMessage) {
        webSocket();
        if (webSocket==null || closed) {
            webSocket = null;
            if (queueToServer.add(newMessage)) {
                die("QBitClient::not connected and output queueToServer is full");
            }
        } else {
            try {
                String message = queueToServer.poll();

                while (message != null) {
                    webSocket.writeTextFrame(message);
                }

                webSocket.writeTextFrame(newMessage);
            } catch (Exception ex) {
                queueToServer.add(newMessage);
                closed = true;
                webSocket = null;
                connect();
            }

        }


    }



    public <T> T createProxy(final Class<T> serviceInterface,
                             final String serviceName) {


        return createProxy(serviceInterface, serviceName,
                Str.join('-', uri, serviceName,
                UUID.randomUUID().toString()));
    }

    public <T> T createProxy(final Class<T> serviceInterface,
                            final String serviceName,
                            String returnAddressArg
                            ) {

        if (!serviceInterface.isInterface()) {
            die("QBitClient:: The service interface must be an interface");
        }

        BeforeMethodCall beforeMethodCall = new BeforeMethodCall() {
            @Override
            public boolean before(final MethodCall call) {

                final Object body = call.body();
                if (body instanceof Object[]) {

                    Object[] list = (Object[]) body;

                    if (list.length>0) {
                        final Object o = list[0];
                        if (o instanceof org.boon.core.Handler) {
                            handlers.put(new HandlerKey(call.returnAddress(), call.id()),
                                    createHandler(serviceInterface, call, (org.boon.core.Handler)o));
                        }
                        list = Arry.slc(list, 1);
                        if (call instanceof MethodCallImpl) {
                            MethodCallImpl impl = (MethodCallImpl) call;
                            impl.setBody(list);
                        }

                    }
                }

                return true;
            }
        };
        return QBit.factory().createRemoteProxy(serviceInterface,
                uri,
                serviceName, returnAddressArg, new Sender<String>() {
                    @Override
                    public void send(String returnAddress, String buffer) {
                        QBitClient.this.send(buffer);
                    }
                }, beforeMethodCall
        );
    }

    private <T> org.boon.core.Handler createHandler(final Class<T> serviceInterface, final MethodCall call, final org.boon.core.Handler handler) {

        final ClassMeta<T> clsMeta = ClassMeta.classMeta(serviceInterface);
        final MethodAccess method = clsMeta.method(call.name());

        Class<?> returnType = null;

        Class<?> compType = null;
        if (method.parameterTypes().length>0) {
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            ParameterizedType parameterizedType = genericParameterTypes.length > 0 ?
                    (ParameterizedType) genericParameterTypes[0] : null;

            Type type =  (parameterizedType.getActualTypeArguments().length > 0 ? parameterizedType.getActualTypeArguments()[0] : null);

            if (type instanceof ParameterizedType) {
                returnType = (Class) ((ParameterizedType) type).getRawType();
                final Type type1 = ((ParameterizedType) type).getActualTypeArguments()[0];

                if (type1 instanceof Class) {
                    compType = (Class)type1;
                }
            } else if (type instanceof Class) {
                returnType = (Class<?>) type;
            }

        }
        final Class<?> actualReturnType = returnType;

        final Class<?> componentClass = compType;

        org.boon.core.Handler<Object> returnHandler = new org.boon.core.Handler<Object>() {
            @Override
            public void handle(Object event) {

                if (actualReturnType !=null) {

                    if (componentClass!=null && actualReturnType == List.class) {
                        event = MapObjectConversion.convertListOfMapsToObjects(componentClass, (List) event);
                    } else {
                        event = Conversions.coerce(actualReturnType, event);
                    }
                    handler.handle(event);
                }

            }
        };


        org.boon.core.Handler<Throwable> exceptionHandler = new org.boon.core.Handler<Throwable>() {
            @Override
            public void handle(Throwable event) {

               logger.error(event, "QBitClient exception from client");

            }
        };

        return Handlers.handler(returnHandler, exceptionHandler);
    }


    public final ReceiveQueue<String> receiveQueue() {
        return queueFromServer.receiveQueue();
    }

    private void connect() {

        vertx.createHttpClient().setHost(host).setPort(port)
                .connectWebsocket(uri,
                        new Handler<WebSocket>() {
                            @Override
                            public void handle(WebSocket event) {

                                connectionQueue.add(event);
                                closed = false;


                                final SendQueue<String> sendQueueFromServer = queueFromServer.sendQueue();

                                event.dataHandler(new Handler<Buffer>() {
                                    @Override
                                    public void handle(Buffer event) {

                                        sendQueueFromServer.sendAndFlush(event.toString());
                                    }
                                });

                                event.exceptionHandler(new Handler<Throwable>() {
                                    @Override
                                    public void handle(Throwable event) {
                                        logger.error(event, "Exception handling web socket connection");

                                    }
                                });

                                event.closeHandler(new Handler<Void>() {
                                    @Override
                                    public void handle(Void event) {

                                        closed = true;
                                    }
                                });
                            }
                        }
                );

    }
}
