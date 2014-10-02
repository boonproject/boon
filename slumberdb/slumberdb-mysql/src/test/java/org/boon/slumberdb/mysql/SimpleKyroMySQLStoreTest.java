package org.boon.slumberdb.mysql;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.boon.Maps;
import org.boon.Str;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 4/5/14.
 */
public class SimpleKyroMySQLStoreTest {

    String url = "jdbc:mysql://localhost:3306/slumberdb";
    String userName = "slumber";
    String password = "slumber1234";
    String table = "kyro-emp-test";
    boolean ok;
    private SimpleKryoKeyValueStoreMySQL<Employee> store;

    @Test
    public void testKyro() {

        Employee employee = new Employee("Rick", "Hightower");
        Kryo kryo = new Kryo();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);

        kryo.writeClassAndObject(output, employee);

        output.close();

        final byte[] bytes = outputStream.toByteArray();


        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        Input input = new Input(inputStream);

        Object object = kryo.readClassAndObject(input);

        puts(object);


    }

    @Before
    public void setup() {

        store = new SimpleKryoKeyValueStoreMySQL(url, userName, password, table, Employee.class, 40);

    }

    @After
    public void close() {


        store.close();
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

            store.put("key." + index, new Employee("Rick" + index, "Hightower"));
        }

        KeyValueIterable<String, Employee> entries = store.loadAll();

        int count = 0;

        for (Entry<String, Employee> entry : entries) {
            puts(entry.key(), entry.value());
            count++;
        }

        ok = (count == 100) || die(count);

        entries.close();

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
    public void testKeys() {


        Map<String, Employee> map = Maps.map(

                "123", new Employee("Rick", "Hightower"),
                "456", new Employee("Paul", "Tabor"),
                "789", new Employee("Jason", "Daniel")

        );


        store.putAll(map);

        final Collection<String> strings = store.loadAllKeys();

        ok = strings.size() > 3 || die();


    }

    @Test
    public void testLoadAllKeys() {

        List<String> keys38 = new ArrayList<>();
        List<String> keys41 = new ArrayList<>();
        List<String> keys77 = new ArrayList<>();
        List<String> keys83 = new ArrayList<>();
        List<String> all = new ArrayList<>();

        for (int index = 0; index < 100; index++) {

            String key = "key.load.all" + index;
            all.add(key);

            if (keys38.size() < 38 + 1) {
                keys38.add(key);
            }

            if (keys41.size() < 41 + 1) {
                keys41.add(key);
            }

            if (keys77.size() < 77 + 1) {
                keys77.add(key);
            }


            if (keys83.size() < 83 + 1) {
                keys83.add(key);
            }

            store.put(key, new Employee(key, key));
        }

        Map<String, ?> results = store.loadAllByKeys(keys38);

        puts(results);

        ok = results.containsKey("key.load.all38") || die();

        ok = !results.containsKey("key.load.all39") || die();


        results = store.loadAllByKeys(keys41);

        puts(results);

        ok = results.containsKey("key.load.all39") || die();
        ok = results.containsKey("key.load.all40") || die();
        ok = results.containsKey("key.load.all41") || die();
        ok = !results.containsKey("key.load.all42") || die();


        results = store.loadAllByKeys(keys77);

        puts(results);

        ok = results.containsKey("key.load.all70") || die();
        ok = results.containsKey("key.load.all75") || die();
        ok = results.containsKey("key.load.all77") || die();
        ok = !results.containsKey("key.load.all78") || die();


        results = store.loadAllByKeys(keys83);

        puts(results);

        ok = results.containsKey("key.load.all80") || die();
        ok = results.containsKey("key.load.all81") || die();
        ok = results.containsKey("key.load.all83") || die();
        ok = !results.containsKey("key.load.all84") || die();


        store.removeAll(all);
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

