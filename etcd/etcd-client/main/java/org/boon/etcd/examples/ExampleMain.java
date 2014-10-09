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

package org.boon.etcd.examples;

import org.boon.core.Sys;
import org.boon.etcd.EtcdClient;
import org.boon.etcd.Response;

import static org.boon.Boon.puts;

/**
 * Created by rhightower on 10/8/14.
 */
public class ExampleMain {


    public static void main(String... args) {

        Response response;

        EtcdClient client = new EtcdClient("localhost", 4003);
        response = client.get("foo");

        puts(response);

        response = client.set("foo", "Rick Was here");

        puts("SET RESPONSE", response);


        response = client.get("foo");



        puts("GET FOO", response);


        response = client.delete("foo");


        puts(response);


        client.setTemp("tempKey", "tempValue", 5);
        Sys.sleep(1000);

        puts(client.get("tempKey").node().getValue());
        Sys.sleep(1000);


        puts(client.get("tempKey").node().getValue());

        Sys.sleep(1000);


        puts(client.get("tempKey").node().getValue());

        Sys.sleep(4000);


        puts(client.get("tempKey"));


        Response waitOnKey = client.wait("waitOnKey");

        puts("GOT KEY WE ARE WAITING ONE", waitOnKey);

        puts("Create a dir");

        client.createDir("conf");


        client.createDir("conf/foo1");

        client.createDir("conf/foo2");


        client.createDir("conf/foo3");


        response = client.listRecursive("");

        puts(response);


        response = client.deleteDir("conf");


        puts(response);

        response = client.deleteDirRecursively("conf");


        puts(response);


        response = client.listRecursive("");

        puts(response);

        response = client.createDir("queue");
        puts(response);

        response = client.createDir("queue/job1");
        puts(response);


        response = client.set("queue/job1/mom", "mom");
        puts(response);

        response = client.createDir("queue/job29");
        puts(response);


        response = client.createDir("queue/job3");
        puts(response);


        response = client.listSorted("queue");
        puts(response);





    }

}
