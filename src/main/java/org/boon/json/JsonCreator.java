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

    private static void handleDir( Map<String, Object> config, JsonParserAndMapper laxParser, String resource ) {
        Map<String, Object> fileConfig;List<String> jsonFiles = IO.listByExt( resource, ".json" );
        for (String jsonFile : jsonFiles) {
            String contents = IO.read(jsonFile);
            fileConfig  = laxParser.parseMap( contents );
            config.putAll( fileConfig );
        }
    }

}
