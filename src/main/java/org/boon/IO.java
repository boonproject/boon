package org.boon;

import org.boon.core.Typ;
import org.boon.primitive.CharBuf;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.CloseableStream;

@SuppressWarnings("unchecked")
public class IO {


    public final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public final static String FILE_SCHEMA = "file";


    @Java8
    public static CloseableStream<Path> list(Path path) {
        CloseableStream<Path> list = null;
        try {
            list = Files.list(path);
        } catch (IOException ex) {
            return Exceptions.handle(CloseableStream.class, ex);
        }
        return list;
    }


    public static String readChild(Path parentDir, String childFileName) {
        try {

            final Path newFilePath = path(parentDir.toString(),
                    childFileName);

            return read(newFilePath);
        } catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
        }
    }

    public static String read(Path path) {
        try {

            return read(Files.newBufferedReader(path, DEFAULT_CHARSET));

        } catch (IOException ex) {
            return Exceptions.handle(String.class, ex);
        }
    }

    public static String read(InputStream inputStream, String charset) {

        try (Reader reader = new InputStreamReader(inputStream, charset)) {
            return read(reader);
        } catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
        }
    }

    public static String read(InputStream inputStream) {

        try (Reader reader = new InputStreamReader(inputStream)) {
            return read(reader);
        } catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
        }

    }

    public static String read(Reader reader) {


        CharBuf builder = CharBuf.create(256);

        try (Reader r = reader) {


            int i;
            while ((i = reader.read()) != -1) {
                builder.add((char) i);
            }


        } catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
        }

        return builder.toString();

    }

    public static String read(File file) {
        try (Reader reader = new FileReader(file)) {
            return read(reader);
        } catch (Exception ex) {
            return Exceptions.handle(String.class, ex);
        }
    }

    public static List<String> readLines(Reader reader) {

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            return readLines(bufferedReader);

        } catch (Exception ex) {

            return Exceptions.handle(List.class, ex);
        }
    }

    public static void eachLine(Reader reader, EachLine eachLine) {

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            eachLine(bufferedReader, eachLine);

        } catch (Exception ex) {

            Exceptions.handle(List.class, ex);
        }
    }

    public static List<String> readLines(InputStream is) {

        try (Reader reader = new InputStreamReader(is, DEFAULT_CHARSET)) {

            return readLines(reader);

        } catch (Exception ex) {

            return Exceptions.handle(List.class, ex);
        }
    }

    public static void eachLine(InputStream is, EachLine eachLine) {

        try (Reader reader = new InputStreamReader(is, DEFAULT_CHARSET)) {

            eachLine(reader, eachLine);

        } catch (Exception ex) {

            Exceptions.handle(ex);
        }
    }


    public static List<String> readLines(BufferedReader reader) {
        List<String> lines = new ArrayList<>(80);

        try (BufferedReader bufferedReader = reader) {


            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }


        } catch (Exception ex) {

            return Exceptions.handle(List.class, ex);
        }
        return lines;
    }

    public static interface EachLine {
        public boolean line(String line, int index);
    }

    public static void eachLine(BufferedReader reader, EachLine eachLine) {

        try (BufferedReader bufferedReader = reader) {


            String line;
            int lineNumber = 0;

            while ((line = bufferedReader.readLine()) != null &&
                    eachLine.line(line, lineNumber++)) { //
                // no op
            }
        } catch (Exception ex) {

            Exceptions.handle(ex);
        }

    }

    public static void eachLine(File file, EachLine eachLine) {
        try (FileReader reader = new FileReader(file)) {
            eachLine(reader, eachLine);
        } catch (Exception ex) {
            Exceptions.handle(List.class, ex);
        }
    }


    public static List<String> readLines(File file) {
        try (FileReader reader = new FileReader(file)) {
            return readLines(reader);
        } catch (Exception ex) {
            return Exceptions.handle(List.class, ex);
        }
    }



    public static List<String> readLines(final String location) {

        final URI uri = URI.create(location);

        return (List<String>) Exceptions.tryIt(Typ.list, new Exceptions.TrialWithReturn<List>() {
            @Override
            public List<String> tryIt() throws Exception {
                if (uri.getScheme() == null) {

                    Path thePath = FileSystems.getDefault().getPath(location);
                    return Files.readAllLines(thePath, DEFAULT_CHARSET);

                } else if (uri.getScheme().equals(FILE_SCHEMA)) {

                    Path thePath = FileSystems.getDefault().getPath(uri.getPath());
                    return Files.readAllLines(thePath, DEFAULT_CHARSET);

                } else {
                    return readLines(location, uri);
                }
            }
        });
    }






    public static void eachLine(final String location, EachLine eachLine) {

        final URI uri = URI.create(location);

        Exceptions.tryIt(new Exceptions.Trial() {
            @Override
            public void tryIt() throws Exception {


                if (uri.getScheme() == null) {

                    Path thePath = FileSystems.getDefault().getPath(location);
                    BufferedReader buf = Files.newBufferedReader(
                            thePath, DEFAULT_CHARSET);
                    eachLine(buf, eachLine);

                } else if (uri.getScheme().equals(FILE_SCHEMA)) {

                    Path thePath = FileSystems.getDefault().getPath(uri.getPath());

                    BufferedReader buf = Files.newBufferedReader(
                            thePath, DEFAULT_CHARSET);
                    eachLine(buf, eachLine);


                } else {
                    eachLine(location, uri, eachLine);
                }

            }

        });
    }

    public static String read(final String location) {
        final URI uri = URI.create(location);

        return Exceptions.tryIt( String.class, new Exceptions.TrialWithReturn<String>() {

            @Override
            public String tryIt() throws Exception {


                if (uri.getScheme() == null) {

                    Path thePath = FileSystems.getDefault().getPath(location);
                    return read(Files.newBufferedReader(thePath, DEFAULT_CHARSET));

                } else if (uri.getScheme().equals(FILE_SCHEMA)) {

                    Path thePath = FileSystems.getDefault().getPath(uri.getPath());
                    return read(Files.newBufferedReader(thePath, DEFAULT_CHARSET));

                } else {
                    return read(location, uri);
                }


            }
        });

    }


private static List<String> readLines(String location, URI uri) throws Exception {
        try {
            FileSystem fileSystem = FileSystems.getFileSystem(uri);
            Path fsPath = fileSystem.getPath(location);

            //Paths.get()
            return Files.readAllLines(fsPath, DEFAULT_CHARSET);
        } catch (ProviderNotFoundException ex) {
            return readLines(uri.toURL().openStream());
        }
    }


    private static void eachLine(String location, URI uri, EachLine eachLine) throws Exception {
        try {
            FileSystem fileSystem = FileSystems.getFileSystem(uri);
            Path fsPath = fileSystem.getPath(location);
            BufferedReader buf = Files.newBufferedReader(fsPath, DEFAULT_CHARSET);
            eachLine(buf, eachLine);


        } catch (ProviderNotFoundException ex) {
            eachLine(uri.toURL().openStream(), eachLine);
        }
    }

    private static String read(String location, URI uri) throws Exception {
        try {
            FileSystem fileSystem = FileSystems.getFileSystem(uri);
            Path fsPath = fileSystem.getPath(location);
            return read(Files.newBufferedReader(fsPath, DEFAULT_CHARSET));
        } catch (ProviderNotFoundException ex) {
            return read(uri.toURL().openStream());
        }
    }


    public static void write(OutputStream out, String content, Charset charset) {

        try (OutputStream o = out) {
            o.write(content.getBytes(charset));
        } catch (Exception ex) {
            Exceptions.handle(ex);
        }

    }

    public static void writeChild(Path parentDir, String childFileName, String childContents) {

        try {

            final Path newFilePath = path(parentDir.toString(),
                    childFileName);

            write(newFilePath, childContents);
        } catch (Exception ex) {
            Exceptions.handle(ex);
        }
    }

    public static Path createChildDirectory(Path parentDir, String childDir) {

        try {


            final Path newDir = path(parentDir.toString(),
                    childDir);


            if (!Files.exists(newDir)) {
                Files.createDirectory(newDir);
            }

            return newDir;

        } catch (Exception ex) {
            return Exceptions.handle(Path.class, ex);
        }
    }

    public static void createDirectory(Path dir) {

        try {


            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }

        } catch (Exception ex) {
            Exceptions.handle(ex);
        }
    }

    public static Path createDirectory(String dir) {

        try {

            final Path newDir = path(dir);
            createDirectory(newDir);

            return newDir;

        } catch (Exception ex) {
            return Exceptions.handle(Path.class, ex);
        }
    }

    public static FileSystem fileSystem() {
        return FileSystems.getDefault();
    }

    public static Path path(String path) {
        return Paths.get(path);
    }

    public static Path path(String path, String... more) {
        return Paths.get(path, more);
    }

    public static void write(Path file, String contents) {
        write(file, contents.getBytes(DEFAULT_CHARSET));
    }

    public static void write(Path file, byte[] contents) {
        try {
            Files.write(file, contents);

        } catch (Exception ex) {
            Exceptions.handle(ex);
        }
    }

    public static void write(OutputStream out, String content) {

        try (OutputStream o = out) {
            o.write(content.getBytes(DEFAULT_CHARSET));
        } catch (Exception ex) {
            Exceptions.handle(ex);
        }

    }


    public static void main(String[] args) throws Throwable {
//        Map<String, String> env = new HashMap<>();
//        env.put("create", "true");
//        // locate file system by using the syntax
//        // defined in java.net.JarURLConnection
//        URI uri = URI.create("jar:file:/codeSamples/zipfs/zipfstest.zip");
//
//        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
//            Path externalTxtFile = Paths.get("/codeSamples/zipfs/SomeTextFile.txt");
//            Path pathInZipfile = zipfs.getPath("/SomeTextFile.txt");
//            // copy a file into the zip file
//            Files.copy( externalTxtFile,pathInZipfile,
//                    StandardCopyOption.REPLACE_EXISTING );
//        }


    }


    //JDK 8 versions of methods on hold


//    public static List<String> readLines(final String location) {
//
//        final URI uri = URI.create(location);
//
//        return Exceptions.tryIt(List.class, () -> {
//
//            if (uri.getScheme() == null) {
//
//                Path thePath = FileSystems.getDefault().getPath(location);
//                return Files.readAllLines(thePath, DEFAULT_CHARSET);
//
//            } else if (uri.getScheme().equals(FILE_SCHEMA)) {
//
//                Path thePath = FileSystems.getDefault().getPath(uri.getPath());
//                return Files.readAllLines(thePath, DEFAULT_CHARSET);
//
//            } else {
//                return readLines(location, uri);
//            }
//
//        });
//
//    }



//    public static void eachLine(final String location, EachLine eachLine) {
//
//        final URI uri = URI.create(location);
//
//        Exceptions.tryIt(() -> {
//
//            if (uri.getScheme() == null) {
//
//                Path thePath = FileSystems.getDefault().getPath(location);
//                BufferedReader buf = Files.newBufferedReader(
//                        thePath, DEFAULT_CHARSET);
//                eachLine(buf, eachLine);
//
//            } else if (uri.getScheme().equals(FILE_SCHEMA)) {
//
//                Path thePath = FileSystems.getDefault().getPath(uri.getPath());
//
//                BufferedReader buf = Files.newBufferedReader(
//                        thePath, DEFAULT_CHARSET);
//                eachLine(buf, eachLine);
//
//
//            } else {
//                eachLine(location, uri, eachLine);
//            }
//
//        });
//
//    }


}
