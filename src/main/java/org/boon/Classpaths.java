package org.boon;


import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

import static org.boon.Boon.sputs;
import static org.boon.Lists.add;
import static org.boon.Lists.isEmpty;
import static org.boon.Lists.list;
import static org.boon.Maps.idx;
import static org.boon.Maps.in;

public class Classpaths {


    private static FileSystem zipFileSystem ( URI fileJarURI ) {



        final Map<String, Object> env = Maps.map ( "create", ( Object ) "true" );

        FileSystemProvider provider = null;
        for (FileSystemProvider p : FileSystemProvider.installedProviders () ) {
            if ( "jar".equals ( p.getScheme () ) ) {
                provider = p;
                break;
            }
        }

        Objects.requireNonNull ( provider, "Zip file provider not found" );

        FileSystem fs =  null;

        try {
            fs =   provider.getFileSystem ( fileJarURI );
        } catch (Exception ex) {
            if ( provider != null ) {
                try {
                    fs = provider.newFileSystem ( fileJarURI, env );
                } catch ( IOException ex2 ) {
                    Exceptions.handle ( FileSystem.class,
                            sputs("unable to load", fileJarURI, "as zip file system"),
                            ex2 );
                }
            }
        }

        Objects.requireNonNull ( provider, "Zip file system was not found" );

        return fs;
    }


    public static List<URL> classpathResources ( ClassLoader loader, String resource ) {
        try {

            Enumeration<URL> resources = loader.getResources ( resource );
            List<URL> list = list ( resources );

            if ( isEmpty ( list ) && resource.startsWith ( "/" ) ) {
                resource = resource.substring ( 1 );
                return classpathResources ( loader, resource );
            }

            return list;


        } catch ( Exception ex ) {

            return Exceptions.handle ( List.class, sputs ( "Unable to load resources for", resource ),
                    ex );
        }


    }

    public static List<URL> classpathResources ( Class<?> clazz, String resource ) {


        List<URL> list = classpathResources ( Thread.currentThread ().getContextClassLoader (), resource );

        if ( isEmpty ( list ) ) {
            list = classpathResources ( clazz.getClassLoader (), resource );
        }


        if ( isEmpty ( list ) && resource.startsWith ( "/" ) ) {
            resource = resource.substring ( 1 );
            return classpathResources ( clazz, resource );
        }

        return list;
    }

    public static List<Path> resources ( Class<?> clazz, String resource ) {


        List<Path> list = resources ( Thread.currentThread().getContextClassLoader(), resource );

        if ( isEmpty ( list ) ) {
            list = resources ( clazz.getClassLoader (), resource );
        }


        if ( isEmpty ( list ) && resource.startsWith ( "/" ) ) {
            resource = resource.substring ( 1 );
            return resources ( clazz, resource );
        }

        return list;
    }

    public static List<Path> resources ( ClassLoader loader, String resource ) {
        final List<URL> resourceURLs = Classpaths.classpathResources ( loader, resource );
        final List<Path> resourcePaths = Lists.list ( Path.class );
        final Map<URI, FileSystem> pathToZipFileSystems = new HashMap<> ();
        for ( URL resourceURL : resourceURLs ) {

            if ( resourceURL.getProtocol ().equals ( "jar" ) ) {
                resourcesFromJar ( resourcePaths, resourceURL, pathToZipFileSystems );

            } else {
                resourcesFromFileSystem ( resourcePaths, resourceURL );
            }
        }
        return resourcePaths;
    }

    private static void resourcesFromFileSystem ( List<Path> resourcePaths, URL u ) {
        URI fileURI = URI.create ( u.toString () );
        add ( resourcePaths, FileSystems.getDefault ().getPath ( fileURI.getPath () ) );
    }


    private static void resourcesFromJar ( List<Path> resourcePaths, URL resourceURL, Map<URI, FileSystem> pathToZipFileSystems ) {

        String str = resourceURL.toString ();

        final String[] strings = StringScanner.split ( str, '!' );

        URI fileJarURI = URI.create ( strings[ 0 ] );
        String resourcePath = strings[ 1 ];

        if ( !pathToZipFileSystems.containsKey ( fileJarURI ) ) {
            pathToZipFileSystems.put ( fileJarURI, zipFileSystem( fileJarURI ) );
        }

        FileSystem fileSystem = pathToZipFileSystems.get(fileJarURI);
        add ( resourcePaths, fileSystem.getPath ( resourcePath ) );
    }

}
