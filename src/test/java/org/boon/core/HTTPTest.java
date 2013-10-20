package org.boon.core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import static org.boon.core.Maps.copy;
import static org.boon.core.Maps.map;
import static org.junit.Assert.assertTrue;

public class HTTPTest {



    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            InputStream requestBody = t.getRequestBody();
            String body = IO.read(requestBody);
            Headers requestHeaders = t.getRequestHeaders();
            body = body + "\n" + copy(requestHeaders).toString();
            t.sendResponseHeaders(200, body.length());
            OutputStream os = t.getResponseBody();
            os.write(body.getBytes());
            os.close();
        }
    }


    @Test
    public void testHappy() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9212), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(10);


        Map<String,String> headers = map("foo", "bar", "fun", "sun");

        String response = HTTP.postBodyWithContentType("http://localhost:9212/test", headers, "text/plain", "hi mom");

        System.out.println(response);

        assertTrue(response.contains("hi mom"));
        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));


        response = HTTP.postBodyWithCharset("http://localhost:9212/test", headers, "text/plain", "UTF-8", "hi mom");

        System.out.println(response);

        assertTrue(response.contains("hi mom"));
        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));

        response = HTTP.postBodyWithHeaders("http://localhost:9212/test", headers, "hi mom");

        System.out.println(response);

        assertTrue(response.contains("hi mom"));
        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));


        response = HTTP.get("http://localhost:9212/test");

        System.out.println(response);


        response = HTTP.getWithHeaders("http://localhost:9212/test", headers);

        System.out.println(response);

        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));



        response = HTTP.getWithContentType("http://localhost:9212/test", headers, "text/plain");

        System.out.println(response);

        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));



        response = HTTP.getWithCharSet("http://localhost:9212/test", headers, "text/plain", "UTF-8");

        System.out.println(response);

        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));

        Thread.sleep(10);

        server.stop(0);


    }

    @Test
    public void testPostBody() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9220), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(10);



        String response = HTTP.postBody("http://localhost:9220/test", "hi mom");

        assertTrue(response.contains("hi mom"));


        Thread.sleep(10);

        server.stop(0);


    }

    @Test(expected = RuntimeException.class)
    public void testSad() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9213), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(10);


        Map<String,String> headers = map("foo", "bar", "fun", "sun");

        String response = HTTP.postBodyWithContentType("http://localhost:9213/foo", headers, "text/plain", "hi mom");

        System.out.println(response);

        assertTrue(response.contains("hi mom"));
        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));

        Thread.sleep(10);

        server.stop(0);


    }

}
