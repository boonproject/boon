package org.boon.core.reflection;


import org.boon.Exceptions;
import org.boon.Lists;
import org.boon.Maps;
import org.boon.core.reflection.fields.FieldAccess;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.boon.Maps;


import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Maps.in;
import static org.boon.Maps.map;
import static org.boon.Sets.set;
import static org.boon.primitive.Int.array;


public class ReflectionTest {


    public static class Dog {
        private int age = 7;
        private String name = "Mooney";
    }

    public static class Cat {
    }


    public static class TypeTester {
        Types types = new Types ();
    }

    public static class Types {

        boolean boolean0 = true;
        byte byte0 = 1;
        int int0 = 2;
        short short0 = 3;
        long long0 = 3;
        float float0 = 5.0f;
        double double0 = 6.0;
        char char0 = 7;

    }


    public static class Husband {
        private String firstName = "Rick";
        private Wife wife = new Wife ();
    }

    public static class Wife {
        private String name = "Diana";
        private int age = 30;

    }


    public static Map<String, Object> bird = Maps.map (

            "name", ( Object ) "bird",
            "friends", list ( "Tweety", "Chicken hawk", "Daffy", "Donald" )
    );


    public static class Employee {
        Employee ( String firstname ) {
            this.firstName = firstname;

        }

        Employee () {

        }

        private String firstName = "Rick";
    }


    public static class Department {
        String name;
        List<Employee> employees;
        List<Set> employeesSet;

    }


    static Map<String, Object> department =
            map (
                    "name", ( Object ) "engineering",
                    "employees", list ( new Employee ( "Bob" ), new Employee ( "Sue" ) )

            );

    static Map<String, Object> department2 =
            map (
                    "name", ( Object ) "hr",
                    "employees", set ( new Employee ( "Bob" ), new Employee ( "Sue" ), new Employee ( "Sam" ) )

            );

    static Map<String, Object> department3 =
            map (
                    "name", ( Object ) "manufacturing",
                    "employees", set (
                    map ( "firstName", "Rick" ),
                    map ( "firstName", "Tom" ),
                    map ( "firstName", "Chris" ),
                    map ( "firstName", "Diana" )
            )

            );


    @Test
    public void testFromMap () throws Exception {

        final Department engineering =
                Maps.fromMap ( department, Department.class );


        boolean ok = true;

        ok &= engineering.name.equals ( "engineering" ) || die ();

        ok &= engineering.employees.size () == 2 || die ();


        final Department hr =
                Maps.fromMap ( department2, Department.class );


        ok &= hr.name.equals ( "hr" ) || die ();

        ok &= hr.employees.size () == 3 || die ();


        final Department manufacturing =
                Maps.fromMap ( department3, Department.class );


        ok &= manufacturing.name.equals ( "manufacturing" ) || die ();

        ok &= manufacturing.employees.size () == 4 || die ();

        ok &= manufacturing.employees.get ( 0 ).firstName.equals ( "Rick" ) || die ();


        //Add this for Sellwyn
        final Map<String, Object> objectMap = Maps.toMap ( manufacturing );

        ok &= objectMap.get ( "name" ).equals ( "manufacturing" ) || die ();
        ok &= Reflection.len ( objectMap.get ( "employees" ) ) == 4 || die ();

        final Map<String, Object> oMapEmployee = ( Map<String, Object> )
                Reflection.idx ( objectMap.get ( "employees" ), 0 );
        ok &= oMapEmployee.get ( "firstName" ).equals ( "Rick" ) || die ();


    }

    @Test
    public void test () throws Exception {

        Employee employee = new Employee ();

        Dog dog = new Dog ();

        Cat cat = new Cat ();

        boolean ok = true;

        ok &= Reflection.hasField ( employee, "firstName" ) || die ();

        ok &= !Reflection.hasField ( employee, "bacon_bacon" ) || die ();


        ok &= Reflection.getFirstComparableOrPrimitive ( employee )
                .equals ( "firstName" ) || die ();

        ok &= Reflection.getFirstComparableOrPrimitive ( dog )
                .equals ( "age" ) || die ();

        ok &= Reflection.getFirstComparableOrPrimitive ( cat ) == null
                || die ();


        ok &= Reflection.getFirstStringFieldNameEndsWith ( employee, "Name" )
                .equals ( "firstName" ) || die ();


        ok &= Reflection.getFirstStringFieldNameEndsWith ( cat, "Name" )
                == null || die ();


        ok &= Reflection.hasStringField ( employee, "firstName" )
                || die ();


        ok &= !Reflection.hasStringField ( employee, "bunnyShit" )
                || die ();


        ok &= Reflection.getSortableField ( employee ).equals ( "firstName" ) ||
                die ();

        ok &= Reflection.getSortableField ( dog ).equals ( "name" ) ||
                die ();

        try {
            ok &= Reflection.getSortableField ( cat ).equals ( "name" );
            die ( "Cat does not have a sortable property" );
        } catch ( Exceptions.SoftenedException se ) {
            if ( !se.getMessage ().contains ( "Could not find a sortable field for type" ) ) {

                die ();
            }
        }

        Map<String, FieldAccess> fields = BeanUtils.getFieldsFromObject ( dog );

        ok &= in ( "name", fields ) || die ();

        fields = BeanUtils.getFieldsFromObject ( employee );

        ok &= in ( "firstName", fields ) || die ();

        fields = BeanUtils.getFieldsFromObject ( cat );

        ok &= !in ( "name", fields ) || die ();


        fields = BeanUtils.getFieldsFromObject ( bird );

        ok &= in ( "name", fields ) || die ();


        //Get Property value

        Object value = BeanUtils.getPropertyValue ( bird, "name" );

        ok &= "bird".equals ( value ) || die ();


        value = BeanUtils.getPropertyValue ( employee, "firstName" );

        ok &= "Rick".equals ( value ) || die ();


        value = BeanUtils.getPropertyValue ( dog, "name" );

        ok &= "Mooney".equals ( value ) || die ();


        Husband husband = new Husband ();
        value = BeanUtils.getPropertyValue ( husband, "firstName" );

        ok &= "Rick".equals ( value ) || die ();


        value = BeanUtils.getPropertyValue ( husband, "wife", "name" );

        ok &= "Diana".equals ( value ) || die ();

        //idx simple

        value = BeanUtils.idx ( bird, "name" );

        ok &= "bird".equals ( value ) || die ();


        value = BeanUtils.idx ( employee, "firstName" );

        ok &= "Rick".equals ( value ) || die ();


        value = BeanUtils.idx ( dog, "name" );

        ok &= "Mooney".equals ( value ) || die ();


        //idx nested
        value = BeanUtils.idx ( husband, "wife.name" );

        ok &= "Diana".equals ( value ) || die ();


        value = BeanUtils.idx ( bird, "friends[1]" );

        ok &= "Chicken hawk".equals ( value ) || die ();


        //idx nested int     left off here.
        value = BeanUtils.idxInt ( husband, "wife.age" );

        ok &= value.equals ( 30 ) || die ();


        TypeTester typeTest = new TypeTester ();

        ok &= BeanUtils.idxLong ( typeTest, "types.long0" ) == typeTest.types.long0 || die ();
        ok &= BeanUtils.idxByte ( typeTest, "types.byte0" ) == typeTest.types.byte0 || die ();
        ok &= BeanUtils.idxShort ( typeTest, "types.short0" ) == typeTest.types.short0 || die ();
        ok &= BeanUtils.idxDouble ( typeTest, "types.double0" ) == typeTest.types.double0 || die ();
        ok &= BeanUtils.idxFloat ( typeTest, "types.float0" ) == typeTest.types.float0 || die ();
        ok &= BeanUtils.idxChar ( typeTest, "types.char0" ) == typeTest.types.char0 || die ();
        ok &= BeanUtils.idxInt ( typeTest, "types.int0" ) == typeTest.types.int0 || die ();
        ok &= BeanUtils.idxBoolean ( typeTest, "types.boolean0" ) == typeTest.types.boolean0 || die ();


        final List<Integer> list = Lists.list ( 1, 2, 3 );
        final int[] array = array ( 1, 2, 3 );


        final Iterator iterator = Reflection.iterator ( list );

        final Iterator iterator2 = Reflection.iterator ( array );

        iterator.next ();
        iterator2.next ();
        iterator.next ();
        iterator2.next ();

        ok &= iterator.next ().equals ( iterator2.next () ) || die ();
        ok &= iterator.hasNext () == iterator2.hasNext () || die ();


    }


    public static void main ( String[] args ) {
    }


}