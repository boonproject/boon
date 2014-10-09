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

import org.boon.core.Handler;
import org.boon.core.Sys;
import org.boon.etcd.EtcdClient;
import org.boon.etcd.Response;

import static org.boon.Boon.puts;


/**
 * Created by rhightower on 10/8/14.
 */
public class ExampleMain2 {


    public static void main(String... args) {

        Handler<Response> handler = new Handler<Response>() {
            @Override
            public void handle(Response event) {

                if (event.node() != null) {
                    puts(event.action(), event.node().key(), event);
                } else {
                    puts(event);
                }
            }
        };

        EtcdClient client = new EtcdClient("localhost", 4001);
        client.get("foo");


        client.set(handler, "foo", "Rick Was here");

        Sys.sleep(1_000);


        client.get(handler, "foo");

        Sys.sleep(1_000);


        client.delete(handler, "foo");

        Sys.sleep(1_000);



        client.setTemp(handler, "tempKey", "tempValue", 5);

        Sys.sleep(1_000);

        client.get(handler, "tempKey");

        Sys.sleep(1000);


        client.get(handler, "tempKey");

        Sys.sleep(1000);


        client.get(handler, "tempKey");

        Sys.sleep(4000);


        client.get(handler, "tempKey");


        Sys.sleep(1000);

        client.get(handler, "tempKey");

        Sys.sleep(1000);


        puts("WAITING ON KEY");

        client.wait(handler, "waitOnKey");

        Sys.sleep(10_000);

        client.createDir(handler, "conf");

        Sys.sleep(1000);



        client.createDir(handler, "conf/foo1");
        client.createDir(handler, "conf/foo2");
        client.createDir(handler, "conf/foo3");

        puts ("LIST RECURSIVE");
        client.listRecursive(handler, "");


        Sys.sleep(3_000);

        client.deleteDir(handler, "conf");

        Sys.sleep(1_000);


        client.deleteDirRecursively(handler, "conf");
        Sys.sleep(1_000);


        client.listRecursive(handler, "");

        Sys.sleep(1_000);

        client.createDir(handler, "queue");
        Sys.sleep(1_000);


        client.createDir(handler, "queue");
        Sys.sleep(1_000);


        client.createDir(handler, "queue/job1");
        Sys.sleep(1_000);


        client.set(handler, "queue/job1/mom", "mom");
        Sys.sleep(1_000);

        client.createDir(handler, "queue/job29");
        Sys.sleep(1_000);


        client.createDir(handler, "queue/job3");
        Sys.sleep(1_000);


        client.listSorted(handler, "queue");
        Sys.sleep(1_000);





    }

}
