package org.boon.core.reflection;


import org.boon.Exceptions;
import org.boon.Maps;
import org.boon.core.reflection.fields.FieldAccess;
import org.junit.Test;

import java.util.Map;

import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Maps.in;

public class ReflectionTest {

    public static class Employee {
        private String firstName="Rick";
    }

    public static class Dog {
        private int age=7;
        private String name="Mooney";
    }

    public static class Cat {
    }


    public static class Husband {
        private String firstName="Rick";
        private Wife wife = new Wife();
    }

    public static class Wife {
        private String name="Diana";
        private int age = 30;

    }


    public static Map<String, Object> bird = Maps.map(

            "name", (Object)"bird",
            "friends", list("Tweety", "Chicken hawk", "Daffy", "Donald")
    );



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



        ok &=   Reflection.getFirstStringFieldNameEndsWith(employee, "Name")
                .equals("firstName") || die();



        ok &=   Reflection.getFirstStringFieldNameEndsWith( cat, "Name" )
                == null || die();


        ok &=   Reflection.hasStringField(employee, "firstName")
               || die();


        ok &=   !Reflection.hasStringField( employee, "bunnyShit" )
                || die();


        ok &=  Reflection.getSortableField( employee ).equals("firstName") ||
                die();

        ok &=  Reflection.getSortableField( dog ).equals("name") ||
                die();

         try {
                ok &=  Reflection.getSortableField( cat ).equals("name");
                die("Cat does not have a sortable property");
         } catch (Exceptions.SoftenedException se) {
             if (!se.getMessage().contains("Could not find a sortable field for type")) {

                 die ();
             }
         }

        Map<String,FieldAccess> fields = Reflection.getFieldsFromObject( dog );

        ok &= in("name", fields) || die();

        fields = Reflection.getFieldsFromObject( employee );

        ok &= in("firstName", fields) || die();

        fields = Reflection.getFieldsFromObject( cat );

        ok &=  !in("name", fields) || die();


        fields = Reflection.getFieldsFromObject( bird );

        ok &= in("name", fields) || die();


        //Get Property value

        Object value = Reflection.getPropertyValue( bird, "name" );

        ok &= "bird".equals( value ) || die();


        value = Reflection.getPropertyValue( employee, "firstName" );

        ok &= "Rick".equals( value ) || die();



        value = Reflection.getPropertyValue( dog, "name" );

        ok &= "Mooney".equals( value ) || die();


        Husband husband  = new Husband();
        value = Reflection.getPropertyValue( husband, "firstName" );

        ok &= "Rick".equals( value ) || die();


        value = Reflection.getPropertyValue( husband, "wife", "name" );

        ok &= "Diana".equals( value ) || die();

        //idx simple

        value = Reflection.idx( bird, "name" );

        ok &= "bird".equals( value ) || die();


        value = Reflection.idx( employee, "firstName" );

        ok &= "Rick".equals( value ) || die();



        value = Reflection.idx( dog, "name" );

        ok &= "Mooney".equals( value ) || die();


        //idx nested
        value = Reflection.idx( husband, "wife.name" );

        ok &= "Diana".equals( value ) || die();


        value = Reflection.idx( bird, "friends[1]" );

        ok &= "Chicken hawk".equals( value ) || die();


        //idx nested int     left off here.
//        value = Reflection.idxInt( husband, "wife.age" );
//
//        ok &= value.equals(29) || die();

    }







}