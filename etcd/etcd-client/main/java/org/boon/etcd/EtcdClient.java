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

import org.boon.Exceptions;
import org.boon.IO;
import org.boon.Str;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.boon.Boon.isEmpty;
import static org.boon.Exceptions.die;

/**
 * Created by rhightower on 10/8/14.
 */
public class EtcdClient implements Etcd{

    public static final String APPLICATION_JSON = "application/json";


    /** Vertx which is the websocket lib we use. */
    private final Vertx vertx;


    private HttpClient httpClient;

    /** Are we closed.*/
    private volatile boolean closed;

    /** Host to connect to. */
    private final String host;
    /** Port of host to connect to. */
    private final int port;

    private ThreadLocal<JsonParserAndMapper> jsonParserAndMapperThreadLocal = new ThreadLocal<JsonParserAndMapper>(){
        @Override
        protected JsonParserAndMapper initialValue() {
            return new JsonParserFactory().create();
        }
    };


    public EtcdClient(Vertx vertx, String host, int port) {

        this.vertx = vertx==null ? VertxFactory.newVertx() : vertx;
        this.host = host;
        this.port = port;
        connect();

    }


    public EtcdClient(String host, int port) {
        this(null, host, port);
    }

    @Override
    public Response delete(String key) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key), getResponseHandler("delete", key, responseBlockingQueue));

        httpClientRequest.end();

        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void delete(org.boon.core.Handler<Response> responseHandler, String key) {

    }

    @Override
    public Response deleteDir(String key) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key, "?dir=true"),
                getResponseHandler("delete", key, responseBlockingQueue));

        httpClientRequest.end();

        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void deleteDir(org.boon.core.Handler<Response> responseHandler, String name) {

    }

    @Override
    public Response deleteDirRecursively(String key) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key, "?dir=true&recursive=true"),
                getResponseHandler("delete", key, responseBlockingQueue));

        httpClientRequest.end();

        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void deleteDirRecursively(org.boon.core.Handler<Response> responseHandler, String key) {


        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key, "?dir=true&recursive=true"),
                handleResponse("delete", key, responseHandler));

        httpClientRequest.end();

    }

    @Override
    public Response deleteIfAtIndex(String key, long index) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key), getResponseHandler("delete", key, responseBlockingQueue));

        Buffer buffer = new Buffer(20);
        addField(buffer, "prevIndex", index);
        httpClientRequest.end(buffer);

        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void deleteIfAtIndex(org.boon.core.Handler<Response> responseHandler, String key, long index) {
        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key),
                handleResponse("delete", key, responseHandler));

        Buffer buffer = new Buffer(20);
        addField(buffer, "prevIndex", index);
        httpClientRequest.end(buffer);


    }

    @Override
    public Response deleteIfValue(String key, String prevValue) {
        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key),
                getResponseHandler("delete", key, responseBlockingQueue));

        Buffer buffer = new Buffer(20);
        addField(buffer, "prevValue", prevValue);
        httpClientRequest.end(buffer);

        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void deleteIfValue(org.boon.core.Handler<Response> responseHandler, String key, String prevValue) {

        HttpClientRequest httpClientRequest = httpClient.delete(Str.add("/v2/keys/", key), handleResponse("delete", key,
                responseHandler));

        Buffer buffer = new Buffer(20);
        addField(buffer, "prevValue", prevValue);
        httpClientRequest.end(buffer);


    }


    @Override
    public void createDir(org.boon.core.Handler<Response> responseHandler, String key) {

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(key.length());

        buffer.appendString("dir=true");

        sendToEtcd(httpClientRequest, buffer);


    }

    @Override
    public Response createDir(String key) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(key.length());

        buffer.appendString("dir=true");

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public Response createTempDir(String key, long ttl) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(key.length());

        buffer.appendString("dir=true&ttl=" + ttl);

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void createTempDir(org.boon.core.Handler<Response> responseHandler, String key, long ttl) {


        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(key.length());

        buffer.appendString("dir=true&ttl=" + ttl);

        sendToEtcd(httpClientRequest, buffer);

    }

    @Override
    public Response updateDirTTL(String key, long ttl) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(key.length());

        buffer.appendString("prevExist=true&dir=true&ttl=" + ttl);

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void updateDirTTL(org.boon.core.Handler<Response> responseHandler, String name, long ttl) {


        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", name), handleResponse("set", name, responseHandler));

        Buffer buffer = new Buffer(name.length());

        buffer.appendString("prevExist=true&dir=true&ttl=" + ttl);

        sendToEtcd(httpClientRequest, buffer);

    }

    @Override
    public Response list(String key) {
        return get(key);
    }

    @Override
    public void list(org.boon.core.Handler<Response> responseHandler, String key) {

        get(responseHandler, key);
    }

    @Override
    public Response listRecursive(String key) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?recursive=true"),

                getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void listRecursive(org.boon.core.Handler<Response> responseHandler, String key) {


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?recursive=true"),

                handleResponse("get", key, responseHandler));

        httpClientRequest.end();


    }

    @Override
    public Response listSorted(String key) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?recursive=true&sorted=true"),

                getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void listSorted(org.boon.core.Handler<Response> responseHandler, String key) {


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?recursive=true&sorted=true"),

                handleResponse("get", key, responseHandler));

        httpClientRequest.end();



    }

    public Response set(String key, String value) {



        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(value.length());

        addField(buffer, "value", value);

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void set(org.boon.core.Handler<Response> responseHandler, String key, String value) {


        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(value.length());

        addField(buffer, "value", value);

        sendToEtcd(httpClientRequest, buffer);

    }

    @Override
    public Response setConfigFile(String key, String fileName) {
        if (!IO.exists(fileName)) {
            die("setConfigFile", "file name does not exist", fileName);
        }
        return this.set(key, IO.read(fileName));
    }

    @Override
    public void setConfigFile(org.boon.core.Handler<Response> responseHandler, String key, String fileName) {
        if (!IO.exists(fileName)) {
            die("setConfigFile", "file name does not exist", fileName);
        }
        this.set(responseHandler, key, IO.read(fileName));
    }

    @Override
    public Response setIfExists(String key, String value) {



        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(value.length() + 50);

        buffer.appendString("prevExist=true");
        addField(buffer, "&value", value);

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void setIfExists(org.boon.core.Handler<Response> responseHandler, String key, String value) {

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(value.length() + 50);

        buffer.appendString("prevExist=true");
        addField(buffer, "&value", value);

        sendToEtcd(httpClientRequest, buffer);

    }

    @Override
    public Response setIfNotExists(String key, String value) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(value.length() + 50);

        buffer.appendString("prevExist=false");
        addField(buffer, "&value", value);

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);

    }

    @Override
    public void setIfNotExists(org.boon.core.Handler<Response> responseHandler, String key, String value) {
        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(value.length() + 50);

        buffer.appendString("prevExist=false");
        addField(buffer, "&value", value);

        sendToEtcd(httpClientRequest, buffer);


    }

    @Override
    public Response compareAndSwapByValue(String key, String prevValue, String value) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(50 + prevValue.length() + value.length());

        addField(buffer, "prevValue", prevValue);

        buffer.appendString("&value", value);


        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void compareAndSwapByValue(org.boon.core.Handler<Response> responseHandler, String key, String prevValue, String value) {

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(50 + prevValue.length() + value.length());

        addField(buffer, "prevValue", prevValue);

        buffer.appendString("&value", value);


        sendToEtcd(httpClientRequest, buffer);


    }

    @Override
    public Response compareAndSwapByModifiedIndex(String key, long prevIndex, String value) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(20 + value.length());

        addField(buffer, "prevIndex",  prevIndex);

        buffer.appendString("&value", value);


        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void compareAndSwapByModifiedIndex(org.boon.core.Handler<Response> responseHandler, String key, long prevIndex, String value) {

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(20 + value.length());

        addField(buffer, "prevIndex",  prevIndex);

        buffer.appendString("&value", value);


        sendToEtcd(httpClientRequest, buffer);
    }


    private void addField(Buffer buffer, String fieldName, String value) {

        try {
            buffer.appendString(fieldName).appendString("=").appendString(URLEncoder.encode(value, StandardCharsets.UTF_8.displayName()));

        } catch (UnsupportedEncodingException e) {
            Exceptions.handle(e);
        }
    }


    private void addField(Buffer buffer, String fieldName, long value) {
            buffer.appendString(fieldName).appendString("=").appendString(Long.toString(value));

    }


    private void addField(Buffer buffer, String fieldName, boolean value) {
        buffer.appendString(fieldName).appendString("=").appendString(Boolean.toString(value));

    }

    @Override
    public Response setTemp(String key, String value, int ttl) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(value.length());

        addField(buffer, "value", value);

        buffer.appendString("&ttl=").appendString(Integer.toString(ttl));

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void setTemp(org.boon.core.Handler<Response> responseHandler, String key, String value, int ttl) {

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(value.length());

        addField(buffer, "value", value);

        buffer.appendString("&ttl=").appendString(Integer.toString(ttl));

        sendToEtcd(httpClientRequest, buffer);

    }

    @Override
    public Response removeTTL(String key, String value) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key), getResponseHandler("set", key, responseBlockingQueue));

        Buffer buffer = new Buffer(value.length());

        addField(buffer, "value", value);

        buffer.appendString("&ttl=&");

        addField(buffer, "prevExist", true);

        sendToEtcd(httpClientRequest, buffer);


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void removeTTL(org.boon.core.Handler<Response> responseHandler, String key, String value) {


        HttpClientRequest httpClientRequest = httpClient.put(Str.add("/v2/keys/", key),
                handleResponse("set", key, responseHandler));

        Buffer buffer = new Buffer(value.length());

        addField(buffer, "value", value);

        buffer.appendString("&ttl=&");

        addField(buffer, "prevExist", true);

        sendToEtcd(httpClientRequest, buffer);

    }

    private void sendToEtcd(HttpClientRequest httpClientRequest, Buffer buffer) {
        httpClientRequest.putHeader("Content-Type", "application/x-www-form-urlencoded").end(buffer);
    }

    @Override
    public Response get(String key) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key), getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void get(org.boon.core.Handler<Response> responseHandler, String key) {


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key),
                handleResponse("get", key, responseHandler));

        httpClientRequest.end();



    }

    @Override
    public Response getConsistent(String key) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?consistent=true"),

                getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponse(key, responseBlockingQueue);
    }

    @Override
    public void getConsistent(org.boon.core.Handler<Response> responseHandler, String key) {


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?consistent=true"),

                handleResponse("get", key, responseHandler));

        httpClientRequest.end();


    }

    @Override
    public Response wait(String key) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?wait=true"),
                getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponseWaitForever(key, responseBlockingQueue);
    }

    @Override
    public void wait(org.boon.core.Handler<Response> responseHandler, String key) {

        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?wait=true"),
                handleResponse("get", key, responseHandler));

        httpClientRequest.end();


    }

    @Override
    public Response wait(String key, long index) {

        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?wait=true&waitIndex=", Long.toString(index)),
                getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponseWaitForever(key, responseBlockingQueue);
    }

    @Override
    public void wait(org.boon.core.Handler<Response> responseHandler, String key, long index) {


        HttpClientRequest httpClientRequest = httpClient.get(
                Str.add("/v2/keys/", key, "?wait=true&waitIndex=", Long.toString(index)),
                handleResponse("get", key, responseHandler));

        httpClientRequest.end();


    }

    @Override
    public Response waitRecursive(String key) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(
                Str.add("/v2/keys/", key, "?wait=true&recursive=true"),
                getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponseWaitForever(key, responseBlockingQueue);
    }

    @Override
    public void waitRecursive(org.boon.core.Handler<Response> responseHandler, String key) {

        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key, "?wait=true&recursive=true"),
                handleResponse("get", key, responseHandler));

        httpClientRequest.end();

    }

    @Override
    public Response waitRecursive(String key, long index) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);


        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key,
                        "?wait=true&recursive=true&index=", Long.toString(index)),
                getResponseHandler("get", key, responseBlockingQueue));

        httpClientRequest.end();


        return getResponseWaitForever(key, responseBlockingQueue);
    }

    @Override
    public void waitRecursive(org.boon.core.Handler<Response> responseHandler, String key, long index) {

        HttpClientRequest httpClientRequest = httpClient.get(Str.add("/v2/keys/", key,
                        "?wait=true&recursive=true&index=", Long.toString(index)),
                handleResponse("get", key, responseHandler));

        httpClientRequest.end();

    }

    private Response getResponse(String key, BlockingQueue<Response> responseBlockingQueue) {
        try {
            Response response = responseBlockingQueue.poll(5000, TimeUnit.MILLISECONDS);
            if (response == null) {
                die("Response timeout for get request key=", key);
            }

            return response;
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        return null;
    }

    private Response getResponseWaitForever(String key, BlockingQueue<Response> responseBlockingQueue) {
        try {
            Response response = responseBlockingQueue.take();
            if (response == null) {
                die("Response timeout for get request key=", key);
            }

            return response;
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

        return null;
    }

    private Handler<HttpClientResponse> getResponseHandler(final String action, final String key, final BlockingQueue<Response> responseBlockingQueue) {
        return new Handler<HttpClientResponse>() {
            @Override
            public void handle(final HttpClientResponse httpClientResponse) {

                final Buffer buffer = new Buffer(1000);

                httpClientResponse.dataHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer partialBuf) {

                        buffer.appendBuffer(partialBuf);
                    }
                }).endHandler(new Handler<Void>() {
                    @Override
                    public void handle(Void aVoid) {

                        String json = buffer.toString();

                        Response response = parseResponse(json, action, key, httpClientResponse);
                        responseBlockingQueue.offer(response);

                    }
                });
            }
        };
    }

    private Handler<HttpClientResponse> handleResponse(final String action, final String key, final org.boon.core.Handler<Response> handler) {
        return new Handler<HttpClientResponse>() {
            @Override
            public void handle(final HttpClientResponse httpClientResponse) {

                final Buffer buffer = new Buffer(1000);

                httpClientResponse.dataHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer partialBuf) {

                        buffer.appendBuffer(partialBuf);
                    }
                }).endHandler(new Handler<Void>() {
                    @Override
                    public void handle(Void aVoid) {
                        String json = buffer.toString();
                        Response response = parseResponse(json, action, key, httpClientResponse);
                        handler.handle(response);
                    }
                });
            }
        };
    }

    private Response parseResponse(String json, String action, String key, HttpClientResponse httpClientResponse) {
        try {


            Response response;


            switch (httpClientResponse.statusCode()) {
                case 200:
                    response = jsonParserAndMapperThreadLocal.get().parse(Response.class, json);
                    response.setHttpStatusCode(httpClientResponse.statusCode());
                    return response;

                case 201:
                    response = jsonParserAndMapperThreadLocal.get().parse(Response.class, json);
                    response.setHttpStatusCode(httpClientResponse.statusCode());
                    response.setCreated();
                    return response;

                case 404:

                    Error notFound = jsonParserAndMapperThreadLocal.get().parse(Error.class, json);

                    response = new Response(action, httpClientResponse.statusCode(), notFound);
                    return response;

                default:

                    if (!isEmpty(json) && (json.contains("cause") || json.contains("errorCode") ) ) {

                        Error error = jsonParserAndMapperThreadLocal.get().parse(Error.class, json);

                        response = new Response(action, httpClientResponse.statusCode(), error);
                        return response;
                    } else {

                        response = jsonParserAndMapperThreadLocal.get().parse(Response.class, json);
                        response.setHttpStatusCode(httpClientResponse.statusCode());
                        return response;
                    }

            }

        } catch (Exception ex) {
            Exceptions.handle(ex, "Unable to parse response for key", key, json);
            return null;
        }
    }

    private void connect() {
        httpClient = vertx.createHttpClient().setHost(host).setPort(port).setConnectTimeout(5_000).setKeepAlive(true);

    }
}
