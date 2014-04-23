package org.boon.bugs;
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



import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.ObjectMapperImpl;
import org.junit.Test;

import static org.boon.Boon.puts;
import static org.boon.json.JsonFactory.fromJson;
import static org.boon.json.JsonFactory.toJson;

/**
 * Created by Richard on 4/23/14.
 */
public class BugReport165 {

    static boolean ok;

    public static class Employee {
        String name;
        long birthDate;
    }


    @Test
    public void test2() {

        Employee employee = new Employee();

        ObjectMapper mapper = JsonFactory.create();

        String json = mapper.toJson(employee);

        puts(json);

        employee.name = "Rick Hightower";

        json = mapper.toJson(employee);

        puts(json);

        employee.birthDate = System.currentTimeMillis() - 60 * 1000 * 24 * 7 * 52 * 29;


        json = mapper.toJson(employee);

        puts(json);

        Employee newEmployee = mapper.fromJson(json, Employee.class);

        puts("New Employee", newEmployee.birthDate, newEmployee.name);


        ok = newEmployee.name.equals("Rick Hightower") && newEmployee.birthDate > 0;

    }

    @Test
    public void test1() {

        Employee employee = new Employee();
        String json = toJson(employee);
        puts(json);

        employee.name = "Rick Hightower";

        json = toJson(employee);

        puts(json);

        employee.birthDate = System.currentTimeMillis() - 60 * 1000 * 24 * 7 * 52 * 29;


        json = toJson(employee);

        puts(json);

        Employee newEmployee = fromJson(json, Employee.class);

        puts("New Employee", newEmployee.birthDate, newEmployee.name);


        ok = newEmployee.name.equals("Rick Hightower") && newEmployee.birthDate > 0;

    }



    @Test
    public void test3() {

        Employee employee = new Employee();

        ObjectMapper mapper = new ObjectMapperImpl();
        String json = mapper.toJson(employee);

        puts(json);

        employee.name = "Rick Hightower";

        json = mapper.toJson(employee);

        puts(json);

        employee.birthDate = System.currentTimeMillis() - 60 * 1000 * 24 * 7 * 52 * 29;


        json = mapper.toJson(employee);

        puts(json);

        Employee newEmployee = mapper.fromJson(json, Employee.class);

        puts("New Employee", newEmployee.birthDate, newEmployee.name);


        ok = newEmployee.name.equals("Rick Hightower") && newEmployee.birthDate > 0;



    }
}
