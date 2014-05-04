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

package com.examples;

import org.boon.Maps;
import org.boon.Sets;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Maps.copy;
import static org.boon.Maps.fromMap;
import static org.boon.json.JsonFactory.fromJson;
import static org.boon.json.JsonFactory.toJson;

/**
 * Created by Richard on 5/1/14.
 */
public class ValidatePropsArePresent {

    public static class Employee {
        String firstName;
        String lastName;
        List<String> todo;

        public Employee(String firstName, String lastName, List<String> todo) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.todo = todo;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", todo=" + todo +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Employee)) return false;

            Employee employee = (Employee) o;

            if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
            if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;
            if (todo != null ? !todo.equals(employee.todo) : employee.todo != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            result = 31 * result + (todo != null ? todo.hashCode() : 0);
            return result;
        }
    }


    public static void  main(String... args) {

        //Happy case
        Employee hovik = new Employee("Hovik", "Gambino",
                list("Eat Salad", "Complain about US politics"));

        String json = toJson(hovik);
        puts( "hovik json", json );

        puts( "hovik object", hovik );


        Employee hovik2 = fromJson(json, Employee.class);


        boolean ok = hovik.equals(hovik2) || die("Hovik is not hovik");


        JsonParserAndMapper mapper = new JsonParserFactory().strict().create();


        // Missing todos
        Map<String, Object> map = mapper.parseMap("{\"firstName\":\"Hovik\",\"lastName\":\"Gambino\"} ");


        //Validate
        boolean hasTodos = map.containsKey("todo");

        if (hasTodos) {
            puts ("It has todo");
        } else {
            puts ("No todos!!!!");
        }

        //You can still convert to an object, in fact there is almost no overhead, the map is not a real map it is an parsed index overlay that looks like a map :)

        Employee hovik3WithNoTodos = fromMap(map, Employee.class);

        puts ("hovik3WithNoTodos", hovik3WithNoTodos);

        //You can manipulate the map too
        map = copy(map);
        map.put("todo", list("Read Scala Book", "Learn Boon", "Learn Vertx and Groovy",
                "Buy Rick Lunch", "Buy Rick Beer"));

        hovik3WithNoTodos = fromMap(map, Employee.class);

        puts ("hovik3WithTodos!", hovik3WithNoTodos);


        List<String> stringList = mapper.parseList(String.class, "[\"ARZ\", \"PIT\", \"SF\"]");

        puts(stringList);


        Set<String> set = Sets.set(mapper.parseList(String.class, "[\"ARZ\", \"PIT\", \"SF\"]"));


        puts(set);

    }
}
