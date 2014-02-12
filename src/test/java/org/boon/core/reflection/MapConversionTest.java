package org.boon.core.reflection;

import org.boon.Lists;
import org.boon.Maps;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.boon.Exceptions.die;

/**
 * Created by Richard on 2/12/14.
 */
public class MapConversionTest {

    public static class Employee {
        String name;
        int age;
        Employee boss;

        List<Employee> reports;

        public Employee( String name, int age ) {
            this.name = name;
            this.age = age;
        }

        public Employee( String name, int age, Employee boss ) {
            this.name = name;
            this.age = age;
            this.boss = boss;
        }


        public Employee( String name, int age, Employee boss, List<Employee> reports ) {
            this.name = name;
            this.age = age;
            this.boss = boss;
            this.reports = reports;
        }

    }

    @Test
    public void test() {
        Employee emp = MapObjectConversion.fromList( Lists.list( (Object)"Rick", 29 ), Employee.class );

        boolean ok = emp !=null || die();
        ok = emp.name !=null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();

    }



    @Test
    public void test2() {
        Employee emp = MapObjectConversion.fromMap( Maps.map( "name", ( Object ) "Rick", "age", 29 ), Employee.class );
        boolean ok = emp !=null || die();
        ok = emp.name !=null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();
    }



    @Test
    public void test3() {
        List<Object> list = Lists.list( (Object)"Jason", 21 );
        Employee emp = MapObjectConversion.fromMap( Maps.map( "name", ( Object ) "Rick", "age", 29, "boss", list ), Employee.class );
        boolean ok = emp !=null || die();
        ok = emp.name !=null || die();
        ok = emp.name.equals( "Rick" ) || die();
        ok = emp.age == 29 || die();

        ok = emp.boss !=null || die();
        ok = emp.boss.name.equals( "Jason" ) || die();
        ok = emp.boss.age == 21 || die();

    }



    @Test
    public void test4() {

        List<Object> list = Lists.list( (Object)"Jason", 21 );
        Employee emp = MapObjectConversion.fromList( Lists.list( (Object)"Rick", 29 , list), Employee.class );

        boolean ok = emp !=null || die();
        ok = emp.name !=null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();

        ok = emp.name !=null || die();
        ok = emp.name.equals( "Rick" ) || die();
        ok = emp.age == 29 || die();

        ok = emp.boss !=null || die();
        ok = emp.boss.name.equals( "Jason" ) || die();
        ok = emp.boss.age == 21 || die();

    }



    @Test
    public void test6() {

        List<Object> boss = Lists.list( (Object)"Jason", 21 );
        List<Object> report = Lists.list( (Object)"Lucas", 10 );


        List<Object> reports = new ArrayList<>();
        reports.add( report );

        Employee emp = MapObjectConversion.fromList( Lists.list( "Rick", 29 , boss, reports), Employee.class );

        boolean ok = emp !=null || die();
        ok = emp.name !=null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();

        ok = emp.name !=null || die();
        ok = emp.name.equals( "Rick" ) || die();
        ok = emp.age == 29 || die();

        ok = emp.boss !=null || die();
        ok = emp.boss.name.equals( "Jason" ) || die();
        ok = emp.boss.age == 21 || die();


        ok = emp.reports !=null || die();

        ok = emp.reports.size() == 1 || die();
        ok = emp.reports.get(0).name.equals( "Lucas" ) || die();
        ok = emp.reports.get(0).age == 10 || die();

    }
}
