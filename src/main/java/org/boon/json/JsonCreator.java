package org.boon.json;

import org.boon.IO;
import org.boon.di.Context;
import org.boon.di.ContextFactory;
import org.boon.di.Creator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 2/4/14.
 */
public class JsonCreator {

    public static <T> T createFromJsonMap(Class<T> type, String str) {
        Map<String,Object> config = new JsonParserFactory().createLaxParser().parseMap( str );
        return Creator.create( type, config );
    }

    public static <T> T createFromJsonMapResource(Class<T> type, String resource) {
        Map<String,Object> config = null;
        JsonParserAndMapper laxParser = new JsonParserFactory().createLaxParser();

        if (resource.endsWith( ".json" )) {
            config = laxParser.parseMap( IO.read(resource) );
        } else if (resource.endsWith( "/" )) {
             config = new LinkedHashMap<>(  );
             handleDir( config, laxParser, resource );
        }
        return Creator.create( type, config );
    }

    public static Context createContextFromJSONResources(String... resources) {
        Map<String,Object> config = new LinkedHashMap<>(  );
        JsonParserAndMapper laxParser = new JsonParserFactory().createLaxParser();
        Map<String,Object> fileConfig;
        for (String resource : resources) {
            Path path = IO.path(resource);
            if ( Files.isDirectory(path)) {
                handleDir( config, laxParser, resource );
            } else {
                fileConfig  = laxParser.parseMap( IO.read( resource ) );
                config.putAll( fileConfig );
            }
        }
        return ContextFactory.context( ContextFactory.fromMap( config ) );
    }

    public static Context configFromNameSpace(String configNamespace, String... resources) {
        Map<String,Object> config = new LinkedHashMap<>(  );
        Map<String,Object> fileConfig;
        for (String resource : resources) {
            if ( resource.endsWith( "/" )) {
                handleDirWithNamespace( config, configNamespace, resource );
            } else if (resource.endsWith( ".json" )) {
                fileConfig = handleConfigWithNamespace( configNamespace, resource );
                config.putAll( fileConfig );
            }
        }
        return ContextFactory.context( ContextFactory.fromMap( config ) );
    }

    private static void handleDirWithNamespace( Map<String, Object> config, String namespace, String resource ) {
        Map<String, Object> fileConfig;List<String> jsonFiles = IO.listByExt( resource, ".json" );
        for (String jsonFile : jsonFiles) {
            fileConfig  = handleConfigWithNamespace( namespace, jsonFile );
            if (fileConfig!=null && fileConfig.size()!=0) {

                puts ("file config", namespace, fileConfig );
                config.putAll( fileConfig );
            }
        }

    }

    private static Map<String,Object> handleConfigWithNamespace( String namespace, String resource ) {

        puts (namespace, resource);
        JsonCreatorEventHandler jsonCreatorEventHandler = new JsonCreatorEventHandler(namespace);
        JsonParserAndMapper laxParser = new JsonParserFactory().createParserWithEvents(jsonCreatorEventHandler);


        Map<String,Object> fileConfig = laxParser.parseMap( IO.read( resource ) );
        if (fileConfig.containsKey( "META" )) {
            puts("found META");
            fileConfig.remove( "META" );
            return fileConfig.size() == 0 ? null : fileConfig;
        } else {
            puts("no META");
            /* No meta then assume it is a global config. */
            return null;
        }

    }

    private static void handleDir( Map<String, Object> config, JsonParserAndMapper laxParser, String resource ) {
        Map<String, Object> fileConfig;
        List<String> jsonFiles = IO.listByExt( resource, ".json" );
        for (String jsonFile : jsonFiles) {
            String contents = IO.read(jsonFile);
            fileConfig  = laxParser.parseMap( contents );
            config.putAll( fileConfig );
        }
    }

}
