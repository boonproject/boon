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
import org.boon.etcd.exceptions.ConnectionException;
import org.boon.etcd.exceptions.TimeoutException;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.*;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.boon.Boon.isEmpty;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by rhightower on 10/8/14.
 */
public class EtcdClient implements Etcd{



    /** Vertx which is the http lib we use. */
    private final Vertx vertx;


    private HttpClient httpClient;

    /** Are we closed.*/
    private volatile boolean closed;

    /** Host to connect to. */
    private final String host;
    /** Port of host to connect to. */
    private final int port;


    private final SSLContext sslContext;


    private final boolean useSSL;
    private final int poolSize;

    private final int timeOutInMilliseconds;


    private final String sslTrustStorePath;
    private final String sslTrustStorePassword;

    private final String sslKeyStorePath;
    private final String sslKeyStorePassword;



    private final boolean sslAuthRequired;

    private final boolean sslTrustAll;


    private ThreadLocal<JsonParserAndMapper> jsonParserAndMapperThreadLocal = new ThreadLocal<JsonParserAndMapper>(){
        @Override
        protected JsonParserAndMapper initialValue() {
            return new JsonParserFactory().create();
        }
    };


    protected EtcdClient(Vertx vertx, ClientBuilder builder, int index) {

        this.vertx = vertx==null ? VertxFactory.newVertx() : vertx;

        this.host = builder.hosts().get(index).getHost();
        this.port = builder.hosts().get(index).getPort();

        this.sslAuthRequired = builder.sslAuthRequired();
        this.sslTrustAll = builder.sslTrustAll();
        this.sslKeyStorePassword = builder.sslKeyStorePassword();
        this.sslTrustStorePassword = builder.sslTrustStorePassword();
        this.sslKeyStorePath = builder.sslKeyStorePath();
        this.sslTrustStorePath = builder.sslTrustStorePath();

        this.timeOutInMilliseconds = builder.timeOutInMilliseconds();
        this.useSSL = builder.useSSL();
        this.poolSize = builder.poolSize();

        this.sslContext = builder.sslContext();


        connect();

    }


    protected EtcdClient(Vertx vertx, ClientBuilder builder) {
        this(vertx, builder, 0);
    }

    protected  EtcdClient(ClientBuilder builder) {

        this (null, builder, 0);

    }

    @Override
    public Response delete(String key) {

        return request(Request.request().methodDELETE().key(key));

    }

    @Override
    public void delete(org.boon.core.Handler<Response> responseHandler, String key) {

        request(responseHandler, Request.request().methodDELETE().key(key));
    }

    @Override
    public Response deleteDir(String key) {


        return request(Request.request().methodDELETE().key(key).dir(true));

    }

    @Override
    public void deleteDir(org.boon.core.Handler<Response> responseHandler, String key) {

         request(responseHandler, Request.request().methodDELETE().key(key).dir(true));

    }

    @Override
    public Response deleteDirRecursively(String key) {


        return request(Request.request().methodDELETE().key(key).dir(true).recursive(true));

    }

    @Override
    public void deleteDirRecursively(org.boon.core.Handler<Response> responseHandler, String key) {
         request(responseHandler, Request.request().methodDELETE().key(key).dir(true).recursive(true));

    }

    @Override
    public Response deleteIfAtIndex(String key, long index) {

        return request( Request.request().methodDELETE().key(key).prevIndex(index));

    }

    @Override
    public void deleteIfAtIndex(org.boon.core.Handler<Response> responseHandler, String key, long index) {

         request( responseHandler, Request.request().methodDELETE().key(key).prevIndex(index));

    }

    @Override
    public Response deleteIfValue(String key, String prevValue) {


        return request( Request.request().methodDELETE().key(key).prevValue(prevValue));

    }

    @Override
    public void deleteIfValue(org.boon.core.Handler<Response> responseHandler, String key, String prevValue) {

        request( responseHandler, Request.request().methodDELETE().key(key).prevValue(prevValue));

    }


    @Override
    public void request(org.boon.core.Handler<Response> responseHandler, Request request) {
        sendHttpRequest(request, responseHandler);
    }

    @Override
    public Response request(final Request request) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        request(new org.boon.core.Handler<Response>() {
            @Override
            public void handle(Response event) {
                responseBlockingQueue.offer(event);
            }
        }, request);

        return getResponse(request.key(), responseBlockingQueue);
    }


    public Response requestForever(final Request request) {


        final BlockingQueue<Response> responseBlockingQueue = new ArrayBlockingQueue<>(1);

        request(new org.boon.core.Handler<Response>() {
            @Override
            public void handle(Response event) {
                responseBlockingQueue.offer(event);
            }
        }, request);

        return getResponseWaitForever(request.key(), responseBlockingQueue);
    }

    @Override
    public void createDir(org.boon.core.Handler<Response> responseHandler, String key) {

        request(responseHandler, Request.request().methodPUT().key(key).dir(true));

    }

    @Override
    public Response createDir(String key) {

        return request(Request.request().methodPUT().key(key).dir(true));

    }

    @Override
    public Response createTempDir(String key, long ttl) {

        return request(Request.request().methodPUT().key(key).ttl(ttl).dir(true));

    }

    @Override
    public void createTempDir(org.boon.core.Handler<Response> responseHandler, String key, long ttl) {


        request(responseHandler, Request.request().methodPUT().key(key).ttl(ttl).dir(true));

    }

    @Override
    public Response updateDirTTL(String key, long ttl) {

        return request(Request.request().methodPUT().key(key).ttl(ttl).dir(true).prevExist(true));

    }

    @Override
    public void updateDirTTL(org.boon.core.Handler<Response> responseHandler, String name, long ttl) {

        request(responseHandler, Request.request().methodPUT().key(name).ttl(ttl).dir(true).prevExist(true));

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

        return request(Request.request().key(key).recursive(true));

    }

    @Override
    public void listRecursive(org.boon.core.Handler<Response> responseHandler, String key) {


        request(responseHandler, Request.request().key(key).recursive(true));


    }

    @Override
    public Response listSorted(String key) {
        return request(Request.request().key(key).recursive(true).sorted(true));

    }

    @Override
    public void listSorted(org.boon.core.Handler<Response> responseHandler, String key) {
        request(responseHandler, Request.request().key(key).recursive(true).sorted(true));

    }

    /**
     * This actually sends the request.
     * @param request request
     * @param responseHandler handler
     */
    private void sendHttpRequest(Request request, org.boon.core.Handler<Response> responseHandler) {

        HttpClientRequest httpClientRequest = httpClient.request(request.getMethod(), request.uri(),
                handleResponse(request, responseHandler));

        if (!request.getMethod().equals("GET")) {
            httpClientRequest.putHeader("Content-Type", "application/x-www-form-urlencoded").end(request.paramBody());
        } else {
            httpClientRequest.end();
        }
    }

    @Override
    public Response addToDir(String dirName, String key, String value) {

        return request(Request.request().methodPOST().key(Str.add(dirName, "/", key)).value(value));
    }

    @Override
    public void addToDir(org.boon.core.Handler<Response> responseHandler, String dirName, String key, String value) {
         request(responseHandler, Request.request().methodPOST().key(Str.add(dirName, "/", key)).value(value));
    }

    public Response set(String key, String value) {

        return request(Request.request().methodPUT().key(Str.add(key)).value(value));


    }

    @Override
    public void set(org.boon.core.Handler<Response> responseHandler, String key, String value) {

        request(responseHandler, Request.request().methodPUT().key(Str.add(key)).value(value));

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


        Request request = Request.request().methodPUT().key(key).value(value).prevExist(true);

        return request(request);

    }

    @Override
    public void setIfExists(org.boon.core.Handler<Response> responseHandler, String key, String value) {


        Request request = Request.request().methodPUT().key(key).value(value).prevExist(true);

        request(responseHandler, request);

    }

    @Override
    public Response setIfNotExists(String key, String value) {


        Request request = Request.request().methodPUT().key(key).value(value).prevExist(false);

        return request(request);

    }

    @Override
    public void setIfNotExists(org.boon.core.Handler<Response> responseHandler, String key, String value) {


        Request request = Request.request().methodPUT().key(key).value(value).prevExist(false);

        request(responseHandler, request);

    }

    @Override
    public Response compareAndSwapByValue(String key, String prevValue, String value) {


        Request request = Request.request().methodPUT().key(key).value(value).prevValue(prevValue);

        return request(request);
    }

    @Override
    public void compareAndSwapByValue(org.boon.core.Handler<Response> responseHandler, String key, String prevValue, String value) {


        Request request = Request.request().methodPUT().key(key).value(value).prevValue(prevValue);


        request(responseHandler, request);



    }

    @Override
    public Response compareAndSwapByModifiedIndex(String key, long prevIndex, String value) {
        Request request = Request.request().methodPUT().key(key).value(value).prevIndex(prevIndex);


        return request(request);

    }

    @Override
    public void compareAndSwapByModifiedIndex(org.boon.core.Handler<Response> responseHandler, String key, long prevIndex, String value) {

        Request request = Request.request().methodPUT().key(key).value(value).prevIndex(prevIndex);
        request(responseHandler, request);
    }



    @Override
    public Response setTemp(String key, String value, int ttl) {

        Request request = Request.request().methodPUT().key(key).value(value).ttl(ttl);
        return request(request);

    }

    @Override
    public void setTemp(org.boon.core.Handler<Response> responseHandler, String key, String value, int ttl) {

        Request request = Request.request().methodPUT().key(key).value(value).ttl(ttl);

        request(responseHandler, request);
    }

    @Override
    public Response removeTTL(String key, String value) {


        Request request = Request.request().methodPUT().key(key).value(value).emptyTTL().prevExist(true);
        return request(request);
    }

    @Override
    public void removeTTL(org.boon.core.Handler<Response> responseHandler, String key, String value) {

        Request request = Request.request().methodPUT().key(key).value(value).emptyTTL().prevExist(true);
        request(responseHandler, request);

    }

    @Override
    public Response get(String key) {

        Request request = Request.request().key(key);
        return request(request);

    }

    @Override
    public void get(org.boon.core.Handler<Response> responseHandler, String key) {

        Request request = Request.request().key(key);
        request(responseHandler, request);

    }

    @Override
    public Response getConsistent(String key) {


        Request request = Request.request().key(key).consistent(true);
        return request(request);

    }

    @Override
    public void getConsistent(org.boon.core.Handler<Response> responseHandler, String key) {

        Request request = Request.request().key(key).consistent(true);

        request(responseHandler, request);

    }

    @Override
    public Response wait(String key) {

        Request request = Request.request().key(key).wait(true);

        return requestForever(request);


    }

    @Override
    public void wait(org.boon.core.Handler<Response> responseHandler, String key) {


        Request request = Request.request().key(key).wait(true);

        request(responseHandler, request);
    }

    @Override
    public Response wait(String key, long index) {

        Request request = Request.request().key(key).wait(true).waitIndex(index);

        return requestForever(request);

    }

    @Override
    public void wait(org.boon.core.Handler<Response> responseHandler, String key, long index) {

        Request request = Request.request().key(key).wait(true).waitIndex(index);

        request(responseHandler, request);

    }

    @Override
    public Response waitRecursive(String key) {


        Request request = Request.request().key(key).wait(true).recursive(true);

        return requestForever(request);

    }

    @Override
    public void waitRecursive(org.boon.core.Handler<Response> responseHandler, String key) {

        Request request = Request.request().key(key).wait(true).recursive(true);
        request(responseHandler, request);
    }

    @Override
    public Response waitRecursive(String key, long index) {

        Request request = Request.request().key(key).wait(true).recursive(true).waitIndex(index);

        return requestForever(request);

    }

    @Override
    public void waitRecursive(org.boon.core.Handler<Response> responseHandler, String key, long index) {


        Request request = Request.request().key(key).wait(true).recursive(true).waitIndex(index);

        request(responseHandler, request);
    }

    private Response getResponse(String key, BlockingQueue<Response> responseBlockingQueue) {
        try {
            Response response = responseBlockingQueue.poll(this.timeOutInMilliseconds, TimeUnit.MILLISECONDS);
            if (response == null) {
                if (this.closed) {
                    throw new ConnectionException(Str.add("Connection exception for key ", key, " host ", host, " port ", ""+port));
                }
                throw new TimeoutException(Str.add("Response timeout for get request key=", key));
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


    private Response createResponseFromException(String action, String key, Throwable throwable) {

        if (throwable instanceof ConnectException) {
            closed = true;

            Error error = new Error(-1, throwable.getClass().getName(), Str.add("Unable to connect to host ", this.host, " port ", ""+this.port), 0L);
            return new Response(action, -1, error);


        }
        Error error = new Error(-1, throwable.getClass().getName(), Str.add(throwable.getMessage(),
                " action ", action, " key ", key, " host ", this.host, " port ", ""+this.port), 0L);
        return new Response(action, -1, error);
    }

    private Handler<HttpClientResponse> handleResponse(final Request request, final org.boon.core.Handler<Response> handler) {
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
                        Response response = parseResponse(json, request.toString(), request.key(), httpClientResponse);
                        handler.handle(response);
                    }
                }).exceptionHandler(new Handler<Throwable>() {
                    @Override
                    public void handle(Throwable event) {

                        Response response = createResponseFromException(request.toString(), request.key(), event);
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

                case 307:
                    response = new RedirectResponse(httpClientResponse.headers().get("Location"));
                    return response;

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
                    } else if (!isEmpty(json)){

                        response = jsonParserAndMapperThreadLocal.get().parse(Response.class, json);
                        response.setHttpStatusCode(httpClientResponse.statusCode());
                        return response;
                    } else {
                        die(httpClientResponse.statusCode(), httpClientResponse.headers().entries());
                        return null;
                    }

            }

        } catch (Exception ex) {

            if (!Str.isEmpty(json)) {
                return createResponseFromException(action + "\n" + json + "\n", key, ex);
            } else {

                return createResponseFromException(action + " blank response", key, ex);
            }
        }
    }

    private void connect() {
        httpClient = vertx.createHttpClient().setHost(host).setPort(port)
                .setConnectTimeout(this.timeOutInMilliseconds).setMaxPoolSize(poolSize).exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable throwable) {

                if (throwable instanceof ConnectException) {
                    closed = true;
                } else {
                    puts(throwable); //add logging soon.
                    throwable.printStackTrace();
                }
            }
        });

        configureSSL(httpClient);

    }

    private void configureSSL(HttpClient httpClient) {
        if (!useSSL) {
            return;
        }

        if (sslAuthRequired) {

            httpClient.setKeyStorePassword(sslKeyStorePassword);
            httpClient.setKeyStorePath(sslKeyStorePath);
        }


        if (!Str.isEmpty(this.sslTrustStorePath)) {
            httpClient.setTrustStorePassword(this.sslTrustStorePassword);

            httpClient.setTrustStorePassword(this.sslTrustStorePath);
        }

        if (sslTrustAll) {
            httpClient.setTrustAll(true);
        }

        if (sslContext==null) {
            httpClient.setSSLContext(this.sslContext);
        }

    }

    public boolean isClosed() {
        return closed;
    }

}
