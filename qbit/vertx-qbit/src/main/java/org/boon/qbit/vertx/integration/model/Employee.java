package org.boon.qbit.vertx.integration.model;

/**
 * Created by Richard on 10/2/14.
 */
public class Employee {

    private  String firstName;
    private  String lastName;
    private  int salary;
    private  long employeeId;


    public Employee() {

    }

    public Employee(String firstName, String lastName, int salary, long employeeId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getSalary() {
        return salary;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary=" + salary +
                ", employeeId=" + employeeId +
                '}';
    }
}
