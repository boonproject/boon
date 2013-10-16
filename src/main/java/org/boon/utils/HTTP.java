package org.boon.utils;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import static org.boon.utils.IO.read;

public class HTTP {

    final static String CHARSET = "UTF-8";


    public static String get(
            final String url) {

        Exceptions.tryIt(() -> {
            URLConnection connection;
            connection = doGet(url, null, null, null);
            return extractResponseString(connection);
        });
        return null;
    }

    public static String getWithHeaders(
            final String url,
            final Map<String, ? extends Object> headers) {
        URLConnection connection;
        try {
            connection = doGet(url, headers, null, null);
            return extractResponseString(connection);
        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }
    }

    public static String getWithContentType(
            final String url,
            final Map<String, ? extends Object> headers,
            String contentType) {
        URLConnection connection;
        try {
            connection = doGet(url, headers, contentType, null);
            return extractResponseString(connection);
        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }
    }
    public static String getWithCharSet(
            final String url,
            final Map<String, ? extends Object> headers,
            String contentType,
            String charSet) {
        URLConnection connection;
        try {
            connection = doGet(url, headers, contentType, charSet);
            return extractResponseString(connection);
        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }
    }

    public static String postBody(
            final String url,
            final String body) {
        URLConnection connection;
        try {
            connection = doPost(url, null, "text/plain", null, body);
            return extractResponseString(connection);
        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }
    }

    public static String postBodyWithHeaders(
            final String url,
            final Map<String, ? extends Object> headers,
            final String body) {
        URLConnection connection;
        try {
            connection = doPost(url, headers, "text/plain", null, body);
            return extractResponseString(connection);
        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }
    }



    public static String postBodyWithContentType(
            final String url,
            final Map<String, ? extends Object> headers,
            final String contentType,
            final String body) {

        URLConnection connection;
        try {
            connection = doPost(url, headers, contentType, null, body);


            return extractResponseString(connection);


        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }


    }


    public static String postBodyWithCharset(
            final String url,
            final Map<String, ? extends Object> headers,
            final String contentType,
            final String charSet,
            final String body) {

        URLConnection connection;
        try {
            connection = doPost(url, headers, contentType, charSet, body);


            return extractResponseString(connection);


        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }


    }

    private static URLConnection doPost(String url, Map<String, ? extends Object> headers,
                                        String contentType, String charset, String body
                                        ) throws IOException {
        URLConnection connection;/* Handle output. */
        connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        manageContentTypeHeaders(contentType, charset, connection);

        manageHeaders(headers, connection);


        IO.write(connection.getOutputStream(), body, CHARSET);
        return connection;
    }

    private static void manageHeaders(Map<String, ? extends Object> headers, URLConnection connection) {
        if (headers != null) {
            for (Map.Entry<String, ? extends Object> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue().toString());
            }
        }
    }

    private static void manageContentTypeHeaders(String contentType, String charset, URLConnection connection) {
        connection.setRequestProperty("Accept-Charset", charset == null ? CHARSET : charset);
        if (contentType!=null && !contentType.isEmpty()) {
            connection.setRequestProperty("Content-Type", contentType);
        }
    }

    private static URLConnection doGet(String url, Map<String, ? extends Object> headers,
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
        String error = charset== null ? read(http.getErrorStream()) :
                read(http.getErrorStream(), charset);
        throw new RuntimeException("STATUS CODE =" + status + "\n\n" + error);
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
        charset = charset == null ?  CHARSET : charset;

        return charset;
    }


}
