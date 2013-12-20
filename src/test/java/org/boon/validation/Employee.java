package org.boon.validation;

/**
 * @author Selwyn Lehmann
 */
public class Employee {
    String firstName;
    int age;
    String phone;

    public Employee ( String name, int age, String phone ) {
        this.firstName = name;
        this.age = age;
        this.phone = phone;
    }

    public String getFirstName () {
        return firstName;
    }

    public void setFirstName ( String firstName ) {
        this.firstName = firstName;
    }

    public int getAge () {
        return age;
    }

    public void setAge ( int age ) {
        this.age = age;
    }

    public String getPhone () {
        return phone;
    }

    public void setPhone ( String phone ) {
        this.phone = phone;
    }
}
