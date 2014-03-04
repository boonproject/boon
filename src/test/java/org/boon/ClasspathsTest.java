package org.boon;


import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.boon.Exceptions.die;

public class ClasspathsTest {

    @Test
    public void test() throws Exception {
        final List<URL> urls = Classpaths.classpathResources( this.getClass(), "testfile.txt" );

        URL url = urls.get( 0 );

        boolean ok = true;

        ok |= Str.in( "apple", IO.read( url.openStream() ) ) || die();

    }

    @Test
    public void test1() throws Exception {
        final List<URL> urls = Classpaths.classpathResources( this.getClass(), "org/node/file1.txt" );

        URL url = urls.get( 0 );

        boolean ok = true;

        ok |= Str.in( "abc", IO.read( url.openStream() ) ) || die();

    }


    @Test
    public void test2() throws Exception {
        final List<URL> urls = Classpaths.classpathResources( this.getClass(), "/org/node/file1.txt" );

        URL url = urls.get( 0 );

        boolean ok = true;

        ok |= Str.in( "abc", IO.read( url.openStream() ) ) || die();

    }

    @Test
    public void test2NoRoot() throws Exception {
        final List<URL> urls = Classpaths.classpathResources( this.getClass(), "org/node/file1.txt" );

        URL url = urls.get( 0 );

        boolean ok = true;

        ok |= Str.in( "abc", IO.read( url.openStream() ) ) || die();

    }

    @Test
    public void testResourcesFromPath() throws Exception {
        final List<String> paths = Classpaths.resources( this.getClass(), "/org/node/file1.txt" );

        String path = paths.get( 0 );

        boolean ok = true;

        ok |= Str.in( "abc", IO.read( path ) ) || die();

    }

    @Test   //not root
    public void testResourcesFromPathNoRoot() throws Exception {
        final List<String> paths = Classpaths.resources( this.getClass(), "org/node/file1.txt" );

        String path = paths.get( 0 );

        boolean ok = true;

        ok |= Str.in( "abc", IO.read( path ) ) || die();

    }

    @Test
    public void testDirectory() throws Exception {
        String someResource = "/org/node/";

        File file = new File( "files/node-1.0-SNAPSHOT.jar" );
        URL url1 = file.getAbsoluteFile().toURI().toURL();
        URL url2 = new File( "files/invoke-1.0-SNAPSHOT.jar" ).getAbsoluteFile().toURI().toURL();


        URLClassLoader loader = new URLClassLoader( new URL[]{ url1, url2 } );

        final List<String> resourcePaths = Classpaths.pathsFromClassLoader(loader, someResource);

        int directoryCount = 0;
        for ( String path : resourcePaths ) {
            if ( !Files.isDirectory( IO.path(path) ) ) {
                die();
            } else {
                directoryCount++;
            }
        }

        boolean ok = true;


        ok |= directoryCount == 3 || die();


    }


    @Test
    public void testFileResources() throws Exception {
        String someResource = "/org/node/resource.txt";

        File file = new File( "files/node-1.0-SNAPSHOT.jar" );
        URL url1 = file.getAbsoluteFile().toURI().toURL();
        URL url2 = new File( "files/invoke-1.0-SNAPSHOT.jar" ).getAbsoluteFile().toURI().toURL();


        URLClassLoader loader = new URLClassLoader( new URL[]{ url1, url2 } );

        final List<String> resourcePaths = Classpaths.pathsFromClassLoader(loader, someResource);

        int fileCount = 0;
        int dirCount = 0;
        for ( String path : resourcePaths ) {
            if ( !Files.isDirectory( IO.path(path) ) ) {
                fileCount++;
            } else {
                dirCount++;
            }
        }

        boolean ok = true;

        ok |= dirCount == 0 || die();


        ok |= fileCount == 2 || die();


    }

}
