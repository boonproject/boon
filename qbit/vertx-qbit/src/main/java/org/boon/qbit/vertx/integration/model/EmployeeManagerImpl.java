package org.boon.qbit.vertx.integration.model;

import org.boon.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmployeeManagerImpl implements EmployeeManager {


    Map<Long, Employee> employeeMap = new HashMap<>();

    @Override
    public void addEmployee(Employee employee) {

        employeeMap.put(employee.getEmployeeId(), employee);
    }

    @Override
    public List<Employee> list() {
        return Lists.list(employeeMap.values());
    }
}
