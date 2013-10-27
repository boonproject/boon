package org.boon.core.reflection;


import org.junit.Test;

import static org.boon.Exceptions.die;

public class ReflectionTest {

    public static class Employee {
        private String firstName;
    }

    public static class Dog {
        private int age;
        private String name;
    }

    public static class Cat {
    }

    @Test
    public void test() throws Exception {

        Employee employee = new Employee();

        Dog dog = new Dog();

        Cat cat = new Cat();

        boolean ok = true;

        ok &=   Reflection.hasField(employee, "firstName") || die();

        ok &=   !Reflection.hasField(employee, "bacon_bacon") || die();


        ok &=   Reflection.getFirstComparableOrPrimitive(employee)
                .equals("firstName") || die();

        ok &=   Reflection.getFirstComparableOrPrimitive(dog)
                .equals("age") || die();

        ok &=   Reflection.getFirstComparableOrPrimitive( cat ) == null
                || die();



        ok &=   Reflection.getFirstStringFieldNameEndsWith( employee, "Name" )
                .equals("firstName") || die();



        ok &=   Reflection.getFirstStringFieldNameEndsWith( cat, "Name" )
                == null || die();



    }

}