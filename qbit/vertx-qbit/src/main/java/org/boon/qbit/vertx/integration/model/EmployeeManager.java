package org.boon.qbit.vertx.integration.model;

import java.util.List;

/**
 * Created by Richard on 10/2/14.
 */
public interface EmployeeManager {
    void addEmployee(Employee employee);

    List<Employee> list();
}
