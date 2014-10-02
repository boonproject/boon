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

package org.boon.service;

import org.boon.core.Conversions;

import java.util.Map;

/**
 * Created by Richard on 3/3/14.
 */
public class Response {

    private final int status; //200 Ok, 500 error, etc. may not be HTTP could be some other scheme, but most likely HTTP codes
    private final Object headers; //could be map or list or object or JSON string
    private final Object statusMessage; //Could be "OK" or the message from a java exception
    private final Object payload;
    private final Class<? extends Enum> enumStatusClass;


    public Response(int status, Object headers, Object statusMessage, Object payload) {
        this.status = status;
        this.headers = headers;
        this.statusMessage = statusMessage;
        this.payload = payload;
        enumStatusClass = null;
    }


    public Response(int status, Object headers, Object statusMessage, Object payload, Class<? extends Enum> enumStatusClass) {
        this.status = status;
        this.headers = headers;
        this.statusMessage = statusMessage;
        this.payload = payload;
        this.enumStatusClass = enumStatusClass;
    }


    public int status() {
        return status;
    }


    public <E extends Enum> E  statusEnum(Class<E> enumClass) {
        return Conversions.toEnum(enumClass, status);
    }


    public Enum  statusEnum() {
        return Conversions.toEnum(this.enumStatusClass, status);
    }

    public Object headers() {
        return headers;
    }


    public Map<String, Object> headerMap() {
        return Conversions.toMap(headers);
    }

    public Object statusMessage() {
        return statusMessage;
    }


    public String statusMessageAsString() {
        return Conversions.toString(statusMessage);
    }


    public Object payload() {
        return payload;
    }


    public String payloadAsString() {
        return Conversions.toString(payload);
    }

    public static Response response(int status, Map headers, String statusMessage, String payload) {
        return new Response(status, headers, statusMessage, payload);
    }
}
