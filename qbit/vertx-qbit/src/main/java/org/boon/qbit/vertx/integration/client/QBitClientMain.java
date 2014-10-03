package org.boon.qbit.vertx.integration.client;

import org.boon.Boon;
import org.boon.core.Sys;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.qbit.vertx.QBitClient;
import org.boon.qbit.vertx.integration.model.Employee;
import org.boon.qbit.vertx.integration.model.EmployeeManager;
import org.qbit.QBit;
import org.qbit.message.Response;
import org.qbit.queue.ReceiveQueue;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 10/2/14.
 */
public class QBitClientMain {



    public static void main (String... args) throws InterruptedException {


        /* Create a new instance of Vertx. */
        Vertx vertx = VertxFactory.newVertx();


        final QBitClient qBitClient = new QBitClient("localhost", 8080, "/services", vertx);

        final EmployeeManager remoteProxy = qBitClient.createProxy(EmployeeManager.class,
                "employeeService");


        remoteProxy.addEmployee(new Employee("Rick", "Hightower", 10, 1L));


        remoteProxy.list();

        final ReceiveQueue<String> receiveQueue = qBitClient.receiveQueue();

        Sys.sleep(1000);

        final String message = receiveQueue.pollWait();

        puts(message);

        final Response<Object> response = QBit.factory().createResponse(message);

        final List<Employee> employees = MapObjectConversion.convertListOfMapsToObjects(Employee.class, (List<Map>) response.body());


        puts(employees);

        Boon.gets();

    }




}
