package org.boon.zipfilesystem;

import org.boon.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.boon.Boon.puts;
import static org.boon.Lists.add;
import static org.boon.Maps.idx;

public class ZipFileSystemTest {


    public static void main( String... args ) throws Exception {


        String someResource = args.length > 0 ? args[ 0 ] :
                "classpath:///org/node/";                 //It also works with directories

        URI someResourceURI = URI.create( someResource );

        System.out.println( "URI of resource = " + someResourceURI );

        someResource = someResourceURI.getPath();

        System.out.println( "PATH of resource =" + someResource );


        File file = new File( "files/node-1.0-SNAPSHOT.jar" );


        URL url = file.getAbsoluteFile().toURI().toURL();


        URLClassLoader loader = new URLClassLoader( new URL[]{ url,
                new File( "files/invoke-1.0-SNAPSHOT.jar" ).getAbsoluteFile().toURI().toURL() } );

        final List<String> resourcePaths = Classpaths.pathsFromClassLoader(loader, someResource);


        for ( String path : resourcePaths ) {
            if ( !Files.isDirectory( IO.path(path) ) ) {
                puts( IO.read( path ) );
            } else {
                puts( IO.list( path ) );
            }
        }


    }


}
