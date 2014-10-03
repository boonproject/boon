package org.boon.qbit.vertx.integration.client;

import org.boon.core.Handler;
import org.boon.qbit.vertx.integration.model.Employee;

import java.util.List;

public interface EmployeeManagerProxy {

    void addEmployee(Employee employee);

    void list(Handler<List<Employee>> employees );
}

