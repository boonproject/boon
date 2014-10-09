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

package org.boon.etcd;

import org.boon.Boon;

/**
 * Created by rhightower on 10/8/14.
 */
public class Response {
    private final String action;
    private final Node node;
    private final Error error;

    private  int responseCode;
    private boolean created;


    public Response(String action, int responseCode, Node node) {
        this.action = action;
        this.node = node;
        this.error = null;

        this.responseCode = responseCode;
    }


    public Response(String action, int responseCode, Error error) {
        this.action = action;
        this.node = null;
        this.error = error;

        this.responseCode = responseCode;
    }



    public boolean wasError() {
        return error !=null;
    }
    public String action() {
        return action;
    }

    public Node node() {
        return node;
    }


    public int responseCode() {
        return responseCode;
    }


    @Override
    public String toString() {
        return Boon.toPrettyJson(this);
    }

    public void setHttpStatusCode(int httpStatusCode) {

        responseCode = httpStatusCode;
    }

    public void setCreated() {
        this.created = true;
    }
}
