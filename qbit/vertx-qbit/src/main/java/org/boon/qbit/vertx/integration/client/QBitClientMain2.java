package org.boon.qbit.vertx.integration.client;

import org.boon.Boon;
import org.boon.core.Handler;
import org.boon.qbit.vertx.QBitClient;
import org.boon.qbit.vertx.integration.model.Employee;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;

import java.util.List;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 10/3/14.
 */
public class QBitClientMain2 {

    public static void main(String... args) throws InterruptedException {


        /* Create a new instance of Vertx. */
        Vertx vertx = VertxFactory.newVertx();


        final QBitClient qBitClient = new QBitClient("localhost", 8080, "/services", vertx);

        qBitClient.startReturnProcessing();

        final EmployeeManagerProxy remoteProxy = qBitClient.createProxy(EmployeeManagerProxy.class,
                "employeeService");


        remoteProxy.addEmployee(new Employee("Rick", "Hightower", 10, 1L));


        remoteProxy.list(new Handler<List<Employee>>() {
            @Override
            public void handle(List<Employee> employees) {
                puts(employees);

            }
        });

        Boon.gets();

    }


}
