package org.boon.utils;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import static org.boon.utils.IO.read;

public class HTTP {



    public static String get(
            final String url) {

        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doGet(url, null, null, null);
                return extractResponseString(connection);
            }
        });

    }

    public static String getWithHeaders(
            final String url,
            final Map<String, ?> headers) {

        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doGet(url, headers, null, null);
                return extractResponseString(connection);
            }
        });

    }

    public static String getWithContentType(
            final String url,
            final Map<String, ? > headers,
            final String contentType) {

        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doGet(url, headers, contentType, null);
                return extractResponseString(connection);
            }
        });

    }
    public static String getWithCharSet(
            final String url,
            final Map<String, ? > headers,
            final String contentType,
            final String charSet) {


        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doGet(url, headers, contentType, charSet);
                return extractResponseString(connection);
            }
        });

    }

    public static String postBody(
            final String url,
            final String body) {


        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doPost(url, null, "text/plain", null, body);
                return extractResponseString(connection);
            }
        });

    }

    public static String postBodyWithHeaders(
            final String url,
            final Map<String, ?> headers,
            final String body) {

        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doPost(url, headers, "text/plain", null, body);
                return extractResponseString(connection);
            }
        });

    }



    public static String postBodyWithContentType(
            final String url,
            final Map<String, ?> headers,
            final String contentType,
            final String body) {


        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doPost(url, headers, contentType, null, body);
                return extractResponseString(connection);
            }
        });

    }


    public static String postBodyWithCharset(
            final String url,
            final Map<String, ?> headers,
            final String contentType,
            final String charSet,
            final String body) {


        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doPost(url, headers, contentType, charSet, body);
                return extractResponseString(connection);
            }
        });

    }

    private static URLConnection doPost(String url, Map<String, ?> headers,
                                        String contentType, String charset, String body
                                        ) throws IOException {
        URLConnection connection;/* Handle output. */
        connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        manageContentTypeHeaders(contentType, charset, connection);

        manageHeaders(headers, connection);


        IO.write(connection.getOutputStream(), body, IO.CHARSET);
        return connection;
    }

    private static void manageHeaders(Map<String, ?> headers, URLConnection connection) {
        if (headers != null) {
            for (Map.Entry<String, ?> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue().toString());
            }
        }
    }

    private static void manageContentTypeHeaders(String contentType, String charset, URLConnection connection) {
        connection.setRequestProperty("Accept-Charset", charset == null ? IO.CHARSET : charset);
        if (contentType!=null && !contentType.isEmpty()) {
            connection.setRequestProperty("Content-Type", contentType);
        }
    }

    private static URLConnection doGet(String url, Map<String, ?> headers,
                                        String contentType, String charset) throws IOException {
        URLConnection connection;/* Handle output. */
        connection = new URL(url).openConnection();
        manageContentTypeHeaders(contentType, charset, connection);

        manageHeaders(headers, connection);

        return connection;
    }

    private static String extractResponseString(URLConnection connection) throws IOException {

        /* Handle input. */
        HttpURLConnection http = (HttpURLConnection)connection;
        int status = http.getResponseCode();
        String charset = getCharset(connection.getHeaderField("Content-Type"));

        if (status==200) {
            return readResponseBody(http, charset);
        } else {
            return readErrorResponseBody(http, status, charset);
        }
    }

    private static String readErrorResponseBody(HttpURLConnection http, int status, String charset) {
        InputStream errorStream = http.getErrorStream();
        if ( errorStream!=null ) {
            String error = charset== null ? read( errorStream ) :
                read( errorStream, charset );
            return Exceptions.die(String.class, "STATUS CODE =" + status + "\n\n" + error);
        } else {
            return Exceptions.die(String.class, "STATUS CODE =" + status);
        }
    }

    private static String readResponseBody(HttpURLConnection http, String charset) throws IOException {
        if (charset != null) {
            return read(http.getInputStream(), charset);
        } else {
            return read(http.getInputStream());
        }
    }

    private static String getCharset(String contentType) {
        if (contentType==null)  {
            return null;
        }
        String charset = null;
        for (String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                charset = param.split("=", 2)[1];
                break;
            }
        }
        charset = charset == null ?  IO.CHARSET : charset;

        return charset;
    }


}
