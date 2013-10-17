package org.boon.utils;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class IO {

    public final static String CHARSET = "UTF-8";


    public static String read(InputStream inputStream, String charset) {

        try (Reader reader = new InputStreamReader(inputStream, charset)) {
               return read(reader);
        }catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
        }
    }

    public static String read(InputStream inputStream) {

        try (Reader reader = new InputStreamReader(inputStream)) {
            return read(reader);
        }catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
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
            return Exceptions.handle(String.class, ex);
        }

        return builder.toString();

    }

    public static String read(File file) {
        try  (Reader reader = new FileReader(file)) {
            return read(reader);
        } catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
        }
    }

    public static String read(String file) {
        return read(new File(file));
    }

    public static List<String> readLines(Reader reader) {

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

              return readLines(bufferedReader);

        } catch (Exception ex) {

            return Exceptions.handle(List.class, ex);
        }
    }

    public static List<String> readLines(InputStream is) {

        try (Reader reader = new InputStreamReader(is, CHARSET)) {

            return readLines(reader);

        } catch (Exception ex) {

            return Exceptions.handle(List.class, ex);
        }
    }

    public static List<String> readLines(BufferedReader reader) {
        List<String> lines = new ArrayList<>(80);

        try (BufferedReader bufferedReader = reader) {


            String line = null;
            while ( (line = bufferedReader.readLine()) != null) {
            lines.add(line);
            }

        } catch (Exception ex) {

            return Exceptions.handle(List.class, ex);
        }
        return lines;
    }

    public static List<String> readLines(File file) {
        try (FileReader reader = new FileReader(file)) {
            return readLines(reader);
        } catch (Exception ex) {
            return Exceptions.handle(List.class, ex);
        }
    }

    public static List<String> readLines(String location) {
        URI uri =  URI.create(location);

        try {

            if ( uri.getScheme()==null ) {

                Path thePath = FileSystems.getDefault().getPath(location);
                return Files.readAllLines(thePath, Charset.forName("UTF-8"));

            } else if ( uri.getScheme().equals("file") ) {

                Path thePath = FileSystems.getDefault().getPath(uri.getPath());
                return Files.readAllLines(thePath, Charset.forName("UTF-8"));

            } else {
                return readLines(location, uri);
            }

        } catch (Exception ex) {
             return Exceptions.handle(List.class, ex);
        }

    }

    private static List<String> readLines(String location, URI uri) throws Exception {
        try {
            FileSystem fileSystem = FileSystems.getFileSystem(uri);
            Path fsPath = fileSystem.getPath(location);
            return Files.readAllLines(fsPath, Charset.forName("UTF-8"));
        } catch (ProviderNotFoundException ex) {
             return readLines(uri.toURL().openStream());
        }
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
            Exceptions.handle(ex);
        }

    }

}
