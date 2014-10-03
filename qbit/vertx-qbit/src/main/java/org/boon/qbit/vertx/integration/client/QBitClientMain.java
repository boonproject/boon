package org.boon.qbit.vertx.integration.client;

import org.boon.Boon;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.qbit.vertx.integration.model.Employee;
import org.boon.qbit.vertx.integration.model.EmployeeManager;
import org.qbit.QBit;
import org.qbit.message.Response;
import org.qbit.proxy.Sender;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.WebSocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 10/2/14.
 */
public class QBitClientMain {



    public static void main (String... args) throws InterruptedException {


        /* Create a new instance of Vertx. */
        Vertx vertx = VertxFactory.newVertx();


        final BlockingQueue<WebSocket> blockingQueue = new ArrayBlockingQueue<>(1);
        final BlockingQueue<String> fromServer = new LinkedTransferQueue<>();

        vertx.createHttpClient().setHost("localhost").setPort(8080)
                .connectWebsocket("/services/employeeManager",
                        new Handler<WebSocket>() {
                            @Override
                            public void handle(WebSocket event) {
                                blockingQueue.add(event);
                                event.dataHandler(new Handler<Buffer>() {
                                    @Override
                                    public void handle(Buffer event) {

                                        fromServer.offer(event.toString());
                                    }
                                });

                                event.exceptionHandler(new Handler<Throwable>() {
                                    @Override
                                    public void handle(Throwable event) {
                                        puts(event);
                                        event.printStackTrace();
                                        ;
                                    }
                                });
                            }
                        }
                );

        final WebSocket webSocket = blockingQueue.take();

        final EmployeeManager remoteProxy = QBit.factory().createRemoteProxy(EmployeeManager.class,
                "/services",
                "employeeService", "myReturnAddress", new Sender<String>() {
                    @Override
                    public void send(String returnAddress, String buffer) {
                        webSocket.writeTextFrame(buffer);
                    }
                }
        );

        remoteProxy.addEmployee(new Employee("Rick", "Hightower", 10, 1L));


        remoteProxy.list();


        final String message = fromServer.take();


        puts (message);

        final Response<Object> response = QBit.factory().createResponse(message);

        final List<Employee> employees = MapObjectConversion.convertListOfMapsToObjects(Employee.class, (List<Map>) response.body());


        puts(employees);

        Boon.gets();

    }




}
