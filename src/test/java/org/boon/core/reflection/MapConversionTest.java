package org.boon.core.reflection;

import org.boon.Lists;
import org.boon.Maps;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static org.boon.Boon.puts;
import static org.boon.Lists.*;
import static org.boon.Maps.*;
import static org.boon.json.JsonFactory.*;


import static org.boon.Exceptions.die;
import static org.boon.Str.lpad;
import static org.boon.Str.rpad;

/**
 * Created by Richard on 2/12/14.
 */
public class MapConversionTest {

    public static Class<Employee> employee = Employee.class;

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

        @Override
        public String toString() {
            return "Employee{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", boss=" + (boss == null ? "executive" : boss) +
//                    ", reports=" + reports +
                    '}';
        }
    }

    @Test
    public void testMain() {
        MapConversionTest.main();
    }

    public static void main( String... args ) {
        List<Object> rickList;
        Map<String, Object> rickMap;


        /** Creating a list. */
        rickList = list( "Rick", 29, list( "Jason", 21 ) );
        /** Creating a map. */
        rickMap = map(
                "name", "Rick",
                "age", 29,
                "boss", map( "name", "Jason", "age", 21 ) );

        /** Showing the list and map. */
        puts( "Rick List", rickList );
        puts( "Rick Map ", rickMap );


        /** Indexing the list and map with boon slice index operator. */
        puts( "Rick's Name",  idx( rickList,     0 ), "from", rickList );
        puts( "Rick's Name",  idx( rickMap, "name" ), "from", rickMap  );

        puts( "Boss Name",    idx( idxList( rickList,    2 ),      0 ) );
        puts( "Boss Name",    idx( idxMap( rickMap, "boss" ), "name" ) );




        String rickJsonList = toJson( rickList );
        String rickJsonMap  = toJson( rickMap );
        puts( "Rick JSON List", rpad( rickJsonList, 60 ), "len", rickJsonList.length() );
        puts( "Rick JSON Map ", rpad( rickJsonMap,  60 ), "len", rickJsonMap.length()  );
        puts ( "LEFT PAD really shows the difference");
        puts( "Rick JSON List", lpad( rickJsonList, 60 ), "len", rickJsonList.length() );
        puts( "Rick JSON Map ", lpad( rickJsonMap,  60 ), "len", rickJsonMap.length()  );

        /** Converting from List to objects */
        Employee rickEmployee =  fromList( rickList,  employee);
        puts (                   "Rick Employee From List      ", rickEmployee);

        /** Converting Maps to objects */
        rickEmployee =  fromMap( rickMap, employee );
        puts (                   "Rick Employee From Map       ", rickEmployee);



        /** Converting from JSON List to objects */
        rickEmployee =  fromJson( rickJsonList, employee );
        puts (                   "Rick Employee From JSON LIST ", rickEmployee);

        /** Converting from JSON Map to objects */
        rickEmployee =  fromJson( rickJsonList,  employee);
        puts (                   "Rick Employee From JSON LIST ", rickEmployee);
 
    }

    @Test
    public void test() {
        Employee emp = MapObjectConversion.fromList( Lists.list( ( Object ) "Rick", 29 ), Employee.class );

        boolean ok = emp != null || die();
        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();

    }


    @Test
    public void test2() {
        Employee emp = MapObjectConversion.fromMap( Maps.map( "name", ( Object ) "Rick", "age", 29 ), Employee.class );
        boolean ok = emp != null || die();
        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();
    }


    @Test
    public void test3() {
        List<Object> list = Lists.list( ( Object ) "Jason", 21 );
        Employee emp = MapObjectConversion.fromMap( Maps.map( "name", ( Object ) "Rick", "age", 29, "boss", list ), Employee.class );
        boolean ok = emp != null || die();
        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();
        ok = emp.age == 29 || die();

        ok = emp.boss != null || die();
        ok = emp.boss.name.equals( "Jason" ) || die();
        ok = emp.boss.age == 21 || die();

    }


    @Test
    public void test4() {

        List<Object> list = Lists.list( ( Object ) "Jason", 21 );
        Employee emp = MapObjectConversion.fromList( Lists.list( ( Object ) "Rick", 29, list ), Employee.class );

        boolean ok = emp != null || die();
        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();

        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();
        ok = emp.age == 29 || die();

        ok = emp.boss != null || die();
        ok = emp.boss.name.equals( "Jason" ) || die();
        ok = emp.boss.age == 21 || die();

    }


    @Test
    public void test6() {

        List<Object> boss = Lists.list( ( Object ) "Jason", 21 );
        List<Object> report = Lists.list( ( Object ) "Lucas", 10 );


        List<Object> reports = new ArrayList<>();
        reports.add( report );

        Employee emp = MapObjectConversion.fromList( Lists.list( "Rick", 29, boss, reports ), Employee.class );

        boolean ok = emp != null || die();
        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();

        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();
        ok = emp.age == 29 || die();

        ok = emp.boss != null || die();
        ok = emp.boss.name.equals( "Jason" ) || die();
        ok = emp.boss.age == 21 || die();


        ok = emp.reports != null || die();

        ok = emp.reports.size() == 1 || die();
        ok = emp.reports.get( 0 ).name.equals( "Lucas" ) || die();
        ok = emp.reports.get( 0 ).age == 10 || die();

    }


    @Test
    public void test7() {

        List<Object> boss = Lists.list( ( Object ) "Jason", 21 );
        List<Object> report = Lists.list( ( Object ) "Lucas", 10 );


        List<Object> reports = new ArrayList<>();
        reports.add( report );

        Employee emp = MapObjectConversion.fromListUsingFields( Lists.list( "Rick", 29, boss, reports ), Employee.class );

        boolean ok = emp != null || die();
        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();

        ok = emp.age == 29 || die();

        ok = emp.name != null || die();
        ok = emp.name.equals( "Rick" ) || die();
        ok = emp.age == 29 || die();

        ok = emp.boss != null || die();
        ok = emp.boss.name.equals( "Jason" ) || die();
        ok = emp.boss.age == 21 || die();


        ok = emp.reports != null || die();

        ok = emp.reports.size() == 1 || die();
        ok = emp.reports.get( 0 ).name.equals( "Lucas" ) || die();
        ok = emp.reports.get( 0 ).age == 10 || die();

    }
}
