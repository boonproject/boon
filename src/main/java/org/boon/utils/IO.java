package org.boon.utils;

import java.io.*;

public class IO {

    final static String CHARSET = "UTF-8";


    public static String read(InputStream inputStream, String charset) {

        try (Reader reader = new InputStreamReader(inputStream, charset)) {
               return read(reader);
        }catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }
    }

    public static String read(InputStream inputStream) {

        try (Reader reader = new InputStreamReader(inputStream)) {
            return read(reader);
        }catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }

    }

    public static String read(Reader reader) {


        StringBuilder builder = new StringBuilder();

        try (Reader r = reader) {

            int i = 0;
            while ((i = reader.read()) !=-1) {
                builder.append((char)i);
            }

        } catch (Exception ex) {
            Exceptions.handle(ex);
            return null;
        }

        return builder.toString();

    }

    public static void write(OutputStream out, String content, String charset) {

        try (OutputStream o = out) {
            o.write(content.getBytes(charset));
        } catch (Exception ex) {
            Exceptions.handle(ex);
        }

    }



    public static void write(OutputStream out, String content) {

        try (OutputStream o = out) {
            o.write(content.getBytes(CHARSET));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

}
