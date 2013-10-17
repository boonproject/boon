package org.boon.utils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.boon.utils.Lists.idx;
import static org.boon.utils.Lists.len;
import static org.boon.utils.Maps.copy;
import static org.boon.utils.Maps.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IOTest {


    @Test
    public void testReadLines() {
        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");


        List<String> lines = IO.readLines(testFile);

        assertLines(lines);

    }

    private void assertLines(List<String> lines) {

        assertEquals(
                4, len(lines)
        );


        assertEquals(
                "line 1", idx(lines, 0)
        );



        assertEquals(
                "grapes", idx(lines, 3)
        );
    }

    @Test
    public void testReadLinesFromPath() {


        List<String> lines = IO.readLines("src/test/resources/testfile.txt");

        assertLines(lines);



    }


    @Test
    public void testReadLinesURI() {

        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");
        URI uri = testFile.toURI();


        System.out.println(uri);
        //"file:///....src/test/resources/testfile.txt"
        List<String> lines = IO.readLines(uri.toString());
        assertLines(lines);


    }


    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            File testDir = new File("src/test/resources");
            File testFile = new File(testDir, "testfile.txt");
            String body = IO.read(testFile);
            t.sendResponseHeaders(200, body.length());
            OutputStream os = t.getResponseBody();
            os.write(body.getBytes(IO.CHARSET));
            os.close();
        }
    }


    @Test
    public void testReadFromHttp() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9666), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(1000);

        List<String> lines = IO.readLines("http://localhost:9666/test");
        assertLines(lines);

    }

}