package org.boon.slumberdb.leveldb;

import org.boon.Maps;
import org.boon.Str;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 4/5/14.
 */
public class SimplyKyroKeyValueStoreLevelDBTest {

    private SimpleKryoKeyValueStoreLevelDB<Employee> store;
    private boolean ok;

    @Before
    public void setup() {


        File file = new File("target/test-data");
        file = file.getAbsoluteFile();
        file.mkdirs();
        file = new File(file, "employee-leveldb-rocksdb.dat");
        store = new SimpleKryoKeyValueStoreLevelDB(file.toString(), Employee.class);

    }

    @After
    public void close() {


        store.close();
    }

    @Test
    public void test() {
        store.put("123",
                new Employee("Rick", "Hightower")
        );

        Employee employee = store.load("123");
        Str.equalsOrDie("Rick", employee.getFirstName());

        Str.equalsOrDie("Hightower", employee.getLastName());
    }

    @Test
    public void testBulkPut() {

        Map<String, Employee> map = Maps.map(

                "123", new Employee("Rick", "Hightower"),
                "456", new Employee("Paul", "Tabor"),
                "789", new Employee("Jason", "Daniel")

        );


        store.putAll(map);


        Employee employee;


        employee = store.load("789");
        Str.equalsOrDie("Jason", employee.getFirstName());
        Str.equalsOrDie("Daniel", employee.getLastName());


        employee = store.load("456");
        Str.equalsOrDie("Paul", employee.getFirstName());
        Str.equalsOrDie("Tabor", employee.getLastName());

        employee = store.load("123");
        Str.equalsOrDie("Rick", employee.getFirstName());
        Str.equalsOrDie("Hightower", employee.getLastName());

    }

    @Test
    public void testBulkRemove() {


        Map<String, Employee> map = Maps.map(

                "123", new Employee("Rick", "Hightower"),
                "456", new Employee("Paul", "Tabor"),
                "789", new Employee("Jason", "Daniel")

        );


        store.putAll(map);


        Employee employee;


        employee = store.load("789");
        Str.equalsOrDie("Jason", employee.getFirstName());
        Str.equalsOrDie("Daniel", employee.getLastName());


        employee = store.load("456");
        Str.equalsOrDie("Paul", employee.getFirstName());
        Str.equalsOrDie("Tabor", employee.getLastName());

        employee = store.load("123");
        Str.equalsOrDie("Rick", employee.getFirstName());
        Str.equalsOrDie("Hightower", employee.getLastName());


        store.removeAll(map.keySet());


        employee = store.load("123");

        ok = employee == null || die();

        employee = store.load("456");


        ok = employee == null || die();


    }

    @Test
    public void testSearch() {
        for (int index = 0; index < 100; index++) {

            store.put("key." + index, new Employee("Rick" + index, "Hightower"));
        }

        KeyValueIterable<String, Employee> entries = store.search("key.50");

        int count = 0;

        for (Entry<String, Employee> entry : entries) {
            puts(entry.key(), entry.value());
            count++;
        }


        ok = (count > 20 && count < 60) || die(count);
        entries.close();
    }

    @Test
    public void testIteration() {

        for (int index = 0; index < 100; index++) {

            store.put("iter." + index, new Employee("Rick" + index, "Hightower"));
        }


        KeyValueIterable<String, Employee> entries = store.loadAll();

        int count = 0;

        for (Entry<String, Employee> entry : entries) {
            puts(entry.key(), entry.value());
            count++;
        }

        ok = (count >= 100) || die(count);

        entries.close();

    }

    @Test
    public void sillyTestForCodeCoverage() {

        KeyValueIterable<String, Employee> entries = store.loadAll();

        Iterator<Entry<String, Employee>> iterator = entries.iterator();


        try {
            while (iterator.hasNext()) {
                iterator.remove();
            }


        } catch (Exception ex) {

        }
    }

    {
        new Entry<>();
    }

    public static class Employee implements Serializable {
        String firstName;
        String lastName;
        String id;

        public Employee() {
        }

        public Employee(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Employee)) return false;

            Employee employee = (Employee) o;

            if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
            if (id != null ? !id.equals(employee.id) : employee.id != null) return false;
            if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
}
