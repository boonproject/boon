package org.boon.utils;

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

import static org.boon.utils.Maps.copy;
import static org.boon.utils.Maps.map;
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

        String response = HTTP.postBody("http://localhost:9212/test", headers, "text/plain", "hi mom");

        System.out.println(response);

        assertTrue(response.contains("hi mom"));
        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));

        Thread.sleep(10);

        server.stop(0);


    }


    @Test(expected = RuntimeException.class)
    public void testSad() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9212), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(10);


        Map<String,String> headers = map("foo", "bar", "fun", "sun");

        String response = HTTP.postBody("http://localhost:9212/foo", headers, "text/plain", "hi mom");

        System.out.println(response);

        assertTrue(response.contains("hi mom"));
        assertTrue(response.contains("Fun=[sun], Foo=[bar]"));

        Thread.sleep(10);

        server.stop(0);


    }

}
