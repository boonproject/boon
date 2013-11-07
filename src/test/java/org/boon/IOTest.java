package org.boon;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.boon.core.Sys;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static javax.xml.bind.DatatypeConverter.parseInt;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Lists.idx;
import static org.boon.Lists.len;
import static org.boon.Lists.list;
import static org.junit.Assert.assertEquals;

public class IOTest {

    File testDir ;
    File testFile ;

    @Before
    public void init() {
        //move testFile and testDir up here for all of the other tests

        if (Sys.isWindows())  {
            testDir = new File("src\\test\\resources");
        } else {
            testDir = new File("src/test/resources");
        }
        testFile = new File(testDir, "testfile.txt");


    }

    @Test
    public void testReadLinesFromFileAsBufferedReader() throws Exception {


        List<String> lines = IO.readLines(new BufferedReader(new FileReader(testFile)));

        assertLines(lines);

    }

    @Test
    public void testReadLinesFromFileAsInputStream() throws Exception {

        List<String> lines = IO.readLines(new FileInputStream(testFile));

        assertLines(lines);

    }


    //    @Test //TODO Test breaks under windows, probably an issue with split line.
    //public void testReadFromFileAsInputStreamCharSet() throws Exception {
    //    File testDir = new File("src/test/resources");
    //    File testFile = new File(testDir, "testfile.txt");


    //    String buf = IO.read(new FileInputStream(testFile), "UTF-8");

    //    assertLines( list ( Str.splitLines(buf) ) );

    //}

    @Test
    public void testReadLines() {
        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");


        List<String> lines = IO.readLines(testFile);

        assertLines(lines);

    }

    @Test
    public void testReadEachLine() {


        IO.eachLine("src/test/resources/testfile.txt", new IO.EachLine() {
            @Override
            public boolean line(String line, int index) {
                System.out.println(index + " " + line);

                if (index == 0) {

                    assertEquals(
                            "line 1", line
                    );

                } else if (index == 3) {


                    assertEquals(
                            "grapes", line
                    );
                }

                return true;
            }
        });

        //assertLines(lines);

    }


    @Test
    public void testReadEachLineByURI() {
        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");



        IO.eachLine(testFile.toURI().toString(), new IO.EachLine() {
            @Override
            public boolean line(String line, int index) {
                System.out.println(index + " " + line);

                if (index == 0) {

                    assertEquals(
                            "line 1", line
                    );

                } else if (index == 3) {


                    assertEquals(
                            "grapes", line
                    );
                }

                return true;
            }
        });

        //assertLines(lines);

    }



    @Test
    public void testReadFromHttp() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9666), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(10);

        List<String> lines = IO.readLines("http://localhost:9666/test");
        assertLines(lines);

    }


    @Test
    public void testReadEachLineHttp() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9668), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(10);

        IO.eachLine( "http://localhost:9668/test",
                new IO.EachLine() {
                    @Override
                    public boolean line(String line, int index) {

                        if (index == 0) {

                            assertEquals(
                                    "line 1", line
                            );

                        } else if (index == 3) {


                            assertEquals(
                                    "grapes", line
                            );
                        }

                        return true;
                    }
                });

    }


    @Test
    public void testReadEachLineReader() throws Exception {
        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");



        IO.eachLine( new FileReader( testFile ),
                new IO.EachLine() {
                    @Override
                    public boolean line(String line, int index) {

                        if (index == 0) {

                            assertEquals(
                                    "line 1", line
                            );

                        } else if (index == 3) {


                            assertEquals(
                                    "grapes", line
                            );
                        }

                        return true;
                    }
                });

        //assertLines(lines);

    }


    @Test
    public void testReadEachLineInputStream() throws Exception {
        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");



        IO.eachLine( new FileInputStream( testFile ),
                new IO.EachLine() {
                    @Override
                    public boolean line(String line, int index) {

                        if (index == 0) {

                            assertEquals(
                                    "line 1", line
                            );

                        } else if (index == 3) {


                            assertEquals(
                                    "grapes", line
                            );
                        }

                        return true;
                    }
                });

        //assertLines(lines);

    }


    @Test
    public void testReadAll() {
        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");


        String content = IO.read(testFile);
        List<String> lines = IO.readLines(new StringReader(content));
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
        //changed "src/test/resources/testfile.txt" to testFile.toString
        List<String> lines = IO.readLines(testFile.toString());
        assertLines(lines);
    }

    @Test
    public void testReadAllFromPath() {
        String content = IO.read(testFile.toString());
        List<String> lines = IO.readLines( new StringReader(content) );
        assertLines(lines);
    }



    @Test
    public void testReadWriteLines() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        IO.write(bos, "line 1\n");
        IO.write(bos, "apple\n");
        IO.write(bos, "pear\n");
        IO.write(bos, "grapes\n");

        List<String> lines = IO.readLines(new ByteArrayInputStream(bos.toByteArray()));

        assertLines(lines);


    }

    @Test
    public void testReadWriteLinesCharSet() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        IO.write(bos, "line 1\n", Charset.forName("UTF-8"));
        IO.write(bos, "apple\n", IO.DEFAULT_CHARSET);
        IO.write(bos, "pear\n", IO.DEFAULT_CHARSET);
        IO.write(bos, "grapes\n", IO.DEFAULT_CHARSET);

        List<String> lines = IO.readLines(new ByteArrayInputStream(bos.toByteArray()));

        assertLines(lines);


    }

    @Test
    public void testReadLinesURI() {

        URI uri = testFile.toURI();


        System.out.println(uri);
        //"file:///....src/test/resources/testfile.txt"
        List<String> lines = IO.readLines(uri.toString());
        assertLines(lines);


    }

    @Test
    public void testReadAllLinesURI() {

        File testDir = new File("src/test/resources");
        File testFile = new File(testDir, "testfile.txt");
        URI uri = testFile.toURI();


        System.out.println(uri);

        String content = IO.read(uri.toString());


        List<String> lines = IO.readLines( new StringReader(content) );
        assertLines(lines);

    }



    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            File testDir = new File("src/test/resources");
            File testFile = new File(testDir, "testfile.txt");
            String body = IO.read(testFile);
            t.sendResponseHeaders(200, body.length());
            OutputStream os = t.getResponseBody();
            os.write(body.getBytes(StandardCharsets.UTF_8.displayName()));
            os.close();
        }
    }



    @Test
    public void testReadAllFromHttp() throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9777), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        Thread.sleep(10);

        String content = IO.read("http://localhost:9777/test");


        List<String> lines = IO.readLines(new StringReader(content));
        assertLines(lines);

    }

    @SuppressWarnings("unchecked")
    public static class ProxyLoader {
        private static final String DATA_FILE = "./files/proxy.txt";


        private List<Proxy> proxyList = Collections.EMPTY_LIST;
        private final String dataFile;

        public ProxyLoader() {
            this.dataFile = DATA_FILE;
            init();
        }

        public ProxyLoader(String dataFile) {
            this.dataFile = DATA_FILE;
            init();
        }

        private void init() {
            List <String> lines = IO.readLines(dataFile);
            proxyList = new ArrayList<>(lines.size());

            for (String line : lines) {
                proxyList.add(Proxy.createProxy(line));
            }
        }

        public String getDataFile() {
            return this.dataFile;
        }

        public static List<Proxy> loadProxies() {
                return new ProxyLoader().getProxyList();
        }

        public List<Proxy> getProxyList() {
            return proxyList;
        }

    }

    public static class Proxy {
        private final String address;
        private final int port;

        public Proxy(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public static Proxy createProxy(String line) {
            String[] lineSplit = line.split(":");
            String address = lineSplit[0];
            int port =  parseInt(lineSplit[1]);
            return new Proxy(address, port);
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }


    public static final class Proxy2 {
        private final String address;
        private final int port;
        private static final String DATA_FILE = "./files/proxy.txt";

        private static final Pattern addressPattern = Pattern.compile("^(\\d{1,3}[.]{1}){3}[0-9]{1,3}$");

        private Proxy2(String address, int port) {

            /* Validate address in not null.*/
            Objects.requireNonNull(address, "address should not be null");

            /* Validate port is in range. */
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Port is not in range port=" + port);
            }

            /* Validate address is of the form 123.12.1.5 .*/
            if (!addressPattern.matcher(address).matches()) {
                throw new IllegalArgumentException("Invalid Inet address");
            }

            /* Now initialize our address and port. */
            this.address = address;
            this.port = port;
        }

        private static Proxy2 createProxy(String line) {
            String[] lineSplit = line.split(":");
            String address = lineSplit[0];
            int port =  parseInt(lineSplit[1]);
            return new Proxy2(address, port);
        }

        public final String getAddress() {
            return address;
        }

        public final int getPort() {
            return port;
        }

        public static List<Proxy2> loadProxies() {
            List <String> lines = IO.readLines(DATA_FILE);
            List<Proxy2> proxyList  = new ArrayList<>(lines.size());

            for (String line : lines) {
                proxyList.add(createProxy(line));
            }
            return proxyList;
        }

    }

    @Test public void proxyTest() {
        List<Proxy> proxyList = ProxyLoader.loadProxies();
        assertEquals(
                5, len(proxyList)
        );


        assertEquals(
                "127.0.0.1", idx(proxyList, 0).getAddress()
        );



        assertEquals(
                8080, idx(proxyList, 0).getPort()
        );


        //192.55.55.57:9091
        assertEquals(
                "192.55.55.57", idx(proxyList, -1).getAddress()
        );



        assertEquals(
                9091, idx(proxyList, -1).getPort()
        );


    }

    @Test public void proxyTest2() {
        List<Proxy2> proxyList = Proxy2.loadProxies();
        assertEquals(
                5, len(proxyList)
        );


        assertEquals(
                "127.0.0.1", idx(proxyList, 0).getAddress()
        );



        assertEquals(
                8080, idx(proxyList, 0).getPort()
        );


        //192.55.55.57:9091
        assertEquals(
                "192.55.55.57", idx(proxyList, -1).getAddress()
        );



        assertEquals(
                9091, idx(proxyList, -1).getPort()
        );


    }


    @Test
    public void readClasspathResource() {

        boolean ok = true;

        ok |= Str.in ("apple", IO.read ( "classpath://testfile.txt" ))
                || die( "two slashes should work" );


        //Proper URL
        ok |= Str.in ("apple", IO.read ( "classpath:///testfile.txt" ))
                || die( "two slashes should work" );


        //Proper URL
        ok |= Str.in ("apple", IO.read ( "classpath:testfile.txt" ))
                || die( "two slashes should work" );

        //Proper URL
        ok |= Str.in ("apple", IO.readFromClasspath ( this.getClass (), "testfile.txt" ))
                || die( "two slashes should work" );

        //Proper URL
        ok |= Str.in ("apple", IO.readFromClasspath ( this.getClass (), "/testfile.txt" ))
                || die( "two slashes should work" );


        puts(IO.list ( "classpath:/org/node" )) ;

        //Proper URL
        ok |=

                Lists.idx (IO.list ( "classpath:/org/node" ), 0).endsWith ( "org/node/file1.txt" )

                || die( );


    }



}