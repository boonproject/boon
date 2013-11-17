package org.boon;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.boon.IO;
import org.boon.primitive.ByteBuf;

public class HTTP {

    public static final int DEFAULT_TIMEOUT_SECONDS =
            Integer.parseInt(System.getProperty("org.boon.HTTP.timeout", "5"));

    public static String get(
            final String url) {

        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doGet(url, null, null, null, false);
                return extractResponseString(connection);
            }
        });

    }

    public static byte[] getBytes(
            final String url, final String contentType) {

        return Exceptions.tryIt(byte[].class, new Exceptions.TrialWithReturn<byte[]>() {
            @Override
            public byte[] tryIt() throws Exception {
                URLConnection connection;
                connection = doGet(url, null, contentType, null, true);
                return extractResponseBytes (connection);
            }
        });

    }

    public static byte[] getBytesWithHeaders(
            final String url, final String contentType, final Map<String, ?> headers) {

        return Exceptions.tryIt(byte[].class, new Exceptions.TrialWithReturn<byte[]>() {
            @Override
            public byte[] tryIt() throws Exception {
                URLConnection connection;
                connection = doGet(url, headers, contentType, null, true);
                return extractResponseBytes (connection);
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
                connection = doGet(url, headers, null, null, false);
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
                connection = doGet(url, headers, contentType, null, false);
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
                connection = doGet(url, headers, contentType, charSet, false);
                return extractResponseString(connection);
            }
        });

    }

    public static String postText(
            final String url,
            final String body) {
            return postBodyTextWithContentType(url, "text/plain", body);
    }

    public static String postBodyTextWithContentType(
            final String url,
            final String contentType,
            final String body) {


        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doPost(url, null, contentType, null, body);
                return extractResponseString(connection);
            }
        });

    }

    public static String post(
            final String url,
            final String body) {

        return postBodyTextWithContentType(url, "text/plain", body);
    }

    public static String postJSON(
            final String url,
            final String jsonString) {

        return postBodyTextWithContentType(url, "application/json", jsonString);
    }

    public static String postXML(
            final String url,
            final String jsonString) {

        return postBodyTextWithContentType(url, "text/xml", jsonString);
    }

    public static String postWithHeaders(
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



    public static String postWithContentType(
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


    public static String postWithCharset(
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
        HttpURLConnection connection;/* Handle output. */


        connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);

        connection.setDoOutput(true);
        manageContentTypeHeaders(contentType, charset, connection);

        manageHeaders(headers, connection);


        IO.write(connection.getOutputStream(), body, IO.DEFAULT_CHARSET);
        return connection;
    }

    public static String postForm(final String url, final Map<String, ?> headers,
                                                final Map<String, Object> formData
    ) {
        return Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn<String>() {
            @Override
            public String tryIt() throws Exception {
                URLConnection connection;
                connection = doPostFormData(url, headers, formData);
                return extractResponseString(connection);
            }
        });

    }

    private static URLConnection doPostFormData(String url, Map<String, ?> headers,
                                        Map<String, Object> formData
    ) throws IOException {
        HttpURLConnection connection;/* Handle output. */


        connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000);

        connection.setDoOutput(true);

        connection.addRequestProperty ( "Content-Type", "application/x-www-form-urlencoded" );

        ByteBuf buf = ByteBuf.create ( 244 );

        final Set<String> keys = formData.keySet ();

        int index = 0;
        for ( String key : keys )  {

            Object value = formData.get ( key );

            if (index > 0) {
                buf.addByte ( '&' );
            }


            buf.addUrlEncoded (  key  );
            buf.addByte ( '=' );

            if ( ! ( value instanceof byte[] ) ) {
                buf.addUrlEncoded ( value.toString () );
            } else {
                buf.addUrlEncodedByteArray((byte[]) value);
            }
            index++;
        }

        manageContentTypeHeaders ( "application/x-www-form-urlencoded",
                StandardCharsets.UTF_8.name (), connection );

        manageHeaders(headers, connection);


        int len = buf.len ();
        IO.write(connection.getOutputStream(),
                new String(buf.readForRecycle (), 0, len, StandardCharsets.UTF_8), IO.DEFAULT_CHARSET);
        return connection;
    }

    private static void manageHeaders(Map<String, ?> headers, URLConnection connection) {
        if (headers != null) {
            for (Map.Entry<String, ?> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue().toString());
            }
        }
    }


    private static void manageContentTypeHeaders ( String contentType, String charset, URLConnection connection, boolean binary ) {

        if (!binary) {
            connection.setRequestProperty("Accept-Charset", charset == null ? StandardCharsets.UTF_8.displayName() : charset);
        }
        if (contentType!=null && !contentType.isEmpty()) {
            connection.setRequestProperty("Content-Type", contentType);
        }

    }


    private static void manageContentTypeHeaders(String contentType, String charset, URLConnection connection) {
        manageContentTypeHeaders ( contentType, charset, connection, false );
    }

    private static URLConnection doGet(String url, Map<String, ?> headers,
                                        String contentType, String charset, boolean binary) throws IOException {
        URLConnection connection;/* Handle output. */
        connection = new URL(url).openConnection();
        manageContentTypeHeaders(contentType, charset, connection, binary);

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

    private static byte[] extractResponseBytes(URLConnection connection) throws IOException {

        /* Handle input. */
        HttpURLConnection http = (HttpURLConnection)connection;
        int status = http.getResponseCode();

        //System.out.println("CONTENT-TYPE" + connection.getHeaderField("Content-Type"));


        if (status==200) {
            return readResponseBodyAsBytes ( http  );
        } else {
            String charset = getCharset(connection.getHeaderField("Content-Type"));

            readErrorResponseBody(http, status, charset);
            return null;
        }
    }

    private static byte[] readResponseBodyAsBytes ( HttpURLConnection http ) {
        try {
            return IO.input ( http.getInputStream () );
        } catch ( IOException e ) {
            return Exceptions.handle ( byte[].class, e );
        }

    }

    private static String readErrorResponseBody(HttpURLConnection http, int status, String charset) {
        InputStream errorStream = http.getErrorStream();
        if ( errorStream!=null ) {
            String error = charset== null ? IO.read(errorStream) :
                IO.read(errorStream, charset);
            return Exceptions.die(String.class, "STATUS CODE =" + status + "\n\n" + error);
        } else {
            return Exceptions.die(String.class, "STATUS CODE =" + status);
        }
    }

    private static String readResponseBody(HttpURLConnection http, String charset) throws IOException {
        if (charset != null) {
            return IO.read(http.getInputStream(), charset);
        } else {
            return IO.read(http.getInputStream());
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
        charset = charset == null ?  StandardCharsets.UTF_8.displayName() : charset;

        return charset;
    }


}
