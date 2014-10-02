/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.criteria;

import org.boon.Lists;
import org.boon.collections.IntList;
import org.boon.core.reflection.BeanUtils;
import org.boon.primitive.Int;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.boon.Boon.puts;
import static org.boon.Lists.lazyAdd;
import static org.boon.Lists.list;
import static org.boon.Lists.toIntList;
import static org.boon.Maps.map;
import static org.boon.core.reflection.BeanUtils.atIndex;
import static org.boon.criteria.Selector.collectFrom;
import static org.boon.criteria.Selector.selects;
import static org.boon.primitive.Int.equalsOrDie;

/**
 * Created by Richard on 3/19/14.
 */
public class IntCollectorTest {


    @Test
    public void test() {

        List<Department> departments;
        List<Employee> employees;

        /** Copy list of departments. */
        departments = Lists.deepCopy(departmentsList);

        /** Get all employees in every department. */

        /** Get all employees in every department. */
        employees =  BeanUtils.idxList(Employee.class, departments, "employees");

        IntCollector salaryCollection = new IntCollector("salary");

        collectFrom( selects( salaryCollection ), employees);

        IntList list = salaryCollection.list();

        puts("sum", list.sum());
        puts("max", list.max());
        puts("min", list.min());
        puts("mean", list.mean());
        puts("median", list.median());
        puts("st.dev", list.standardDeviation());

        equalsOrDie(2000, list.sum());
        equalsOrDie(400, list.max());
        equalsOrDie(100, list.min());
        equalsOrDie(250, list.mean());
        equalsOrDie(250, list.median());
        equalsOrDie(112, list.standardDeviation());


        /**
         * If you are only selecting one then this is faster
         */
        list = toIntList(employees, "salary");


        puts("sum", list.sum());
        puts("max", list.max());
        puts("min", list.min());
        puts("mean", list.mean());
        puts("median", list.median());
        puts("st.dev", list.standardDeviation());

        equalsOrDie(2000, list.sum());
        equalsOrDie(400, list.max());
        equalsOrDie(100, list.min());
        equalsOrDie(250, list.mean());
        equalsOrDie(250, list.median());
        equalsOrDie(112, list.standardDeviation());



        puts("sum", list.sum());
        puts("max", list.max());
        puts("min", list.min());
        puts("mean", list.mean());
        puts("median", list.median());
        puts("st.dev", list.standardDeviation());



        equalsOrDie(2000, (int) Int.sum(employees, "salary"));
        //equalsOrDie(400, Int.max(employees, "salary"));
        //equalsOrDie(100, list.min());
        //equalsOrDie(250, (int) Int.mean(employees, "salary") );
        equalsOrDie(250, Int.median(employees, "salary"));
        equalsOrDie(112, (int) Int.standardDeviation(employees, "salary"));


        puts("sum", (int) Int.sum(employees, "salary"));
        puts("median", Int.median(employees, "salary"));
        puts("stdev", Int.standardDeviation(employees, "salary"));




    }



    public static class ContactInfo {
        String address;
        List<String> phoneNumbers;


    }

    public static class Employee implements Comparable<Employee> {
        int id;
        int salary;
        String firstName;
        String lastName;

        ContactInfo contactInfo = new ContactInfo();
        Department department;

        public Employee() {
        }

        public Employee(int id, int salary, String firstName, String lastName,
                        String... phoneNumbers) {
            this.id = id;
            this.salary = salary;
            this.firstName = firstName;
            this.lastName = lastName;

            for (String phone : phoneNumbers) {
                contactInfo.phoneNumbers = lazyAdd(contactInfo.phoneNumbers, phone);
            }
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSalary() {
            return salary;
        }

        public void setSalary(int salary) {
            this.salary = salary;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Employee employee = (Employee) o;

            if (id != employee.id) return false;
            if (salary != employee.salary) return false;
            if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
            if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + salary;
            result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", salary=" + salary +
                    ", department=" + (department == null ? "NONE" : department.getName()) +
                    ", phone number=" + atIndex(this, "contactInfo.phoneNumbers[0]") +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    "}";
        }

        @Override
        public int compareTo(Employee otherEmployee) {
            return this.firstName.compareTo(otherEmployee.firstName);
        }

        public void setDepartment(Department department) {
            this.department =  department;
        }
    }

    public static class Department {
        private String name;

        private List<Employee> employees;

        public Department() {
        }

        public Department(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Department add(Employee... employees) {

            for (Employee employee : employees) {
                employee.setDepartment(this);
            }
            this.employees = lazyAdd(this.employees, employees);
            return this;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Department that = (Department) o;

            if (employees != null ? !employees.equals(that.employees) : that.employees != null) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (employees != null ? employees.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Department{" +
                    "name='" + name + '\'' +
                    ", employees=" + atIndex(employees, "id") +
                    '}';
        }
    }


    static List<Department> departmentsList = list(
            new Department("Engineering").add(
                    new Employee(1, 100, "Rick", "Hightower", "555-555-1000"),
                    new Employee(2, 200, "John", "Smith", "555-555-1215", "555-555-1214", "555-555-1213"),
                    new Employee(3, 300, "Drew", "Donaldson", "555-555-1216"),
                    new Employee(4, 400, "Nick", "LaySacky", "555-555-1217")

            ),
            new Department("HR").add(
                    new Employee(5, 100, "Dianna", "Hightower", "555-555-1218"),
                    new Employee(6, 200, "Derek", "Smith", "555-555-1219"),
                    new Employee(7, 300, "Tonya", "Donaldson", "555-555-1220"),
                    new Employee(8, 400, "Sue", "LaySacky", "555-555-9999")

            ), new Department("Manufacturing").add(),
            new Department("Sales").add(),
            new Department("Marketing").add()

    );

    static List<?> departmentObjects = list(
            map("name", "Engineering",
                    "employees", list(
                    map("id", 1, "salary", 100, "firstName", "Rick", "lastName", "Hightower",
                            "contactInfo", map("phoneNumbers",
                            list("555-555-0000")
                    )
                    ),
                    map("id", 2, "salary", 200, "firstName", "John", "lastName", "Smith",
                            "contactInfo", map("phoneNumbers", list("555-555-1215",
                            "555-555-1214", "555-555-1213"))),
                    map("id", 3, "salary", 300, "firstName", "Drew", "lastName", "Donaldson",
                            "contactInfo", map("phoneNumbers", list("555-555-1216"))),
                    map("id", 4, "salary", 400, "firstName", "Nick", "lastName", "LaySacky",
                            "contactInfo", map("phoneNumbers", list("555-555-1217")))

            )
            ),
            map("name", "HR",
                    "employees", list(
                    map("id", 5, "salary", 100, "firstName", "Dianna", "lastName", "Hightower",
                            "contactInfo",
                            map("phoneNumbers", list("555-555-1218"))),
                    map("id", 6, "salary", 200, "firstName", "Derek", "lastName", "Smith",
                            "contactInfo",
                            map("phoneNumbers", list("555-555-1219"))),
                    map("id", 7, "salary", 300, "firstName", "Tonya", "lastName", "Donaldson",
                            "contactInfo", map("phoneNumbers", list("555-555-1220"))),
                    map("id", 8, "salary", 400, "firstName", "Sue", "lastName", "LaySacky",
                            "contactInfo", map("phoneNumbers", list("555-555-9999")))

            )
            ),
            map("name", "Manufacturing", "employees", Collections.EMPTY_LIST),
            map("name", "Sales", "employees", Collections.EMPTY_LIST),
            map("name", "Marketing", "employees", Collections.EMPTY_LIST)
    );
}
