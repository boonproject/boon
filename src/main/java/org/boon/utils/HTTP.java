package org.boon.utils;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import static org.boon.utils.IO.read;

public class HTTP {

    final static String CHARSET = "UTF-8";


    public static String postBody(
            final String url,
            final Map<String, ? extends Object> headers,
            final String contentType,
            final String body) {

        URLConnection connection;
        try {
            /* Handle output. */
            connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", CHARSET);


            for (Map.Entry<String, ? extends Object> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue().toString());
            }
            IO.write(connection.getOutputStream(), body, CHARSET);

            /* Handle input. */
            HttpURLConnection http = (HttpURLConnection)connection;
            int status = http.getResponseCode();
            String charset = getCharset(connection.getHeaderField("Content-Type"));

            if (status==200) {
                if (charset != null) {
                    return read(http.getInputStream(), charset);
                } else {
                    return read(http.getInputStream());
                }
            } else {
                String error = charset== null ? read(http.getErrorStream()) :
                        read(http.getErrorStream(), charset);
                throw new RuntimeException("STATUS CODE =" + status + "\n\n" + error);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
