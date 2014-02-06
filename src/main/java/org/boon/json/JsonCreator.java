package org.boon.json;

import org.boon.IO;
import org.boon.config.ContextConfig;
import org.boon.di.Creator;

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


    private static void handleDir( Map<String, Object> config, JsonParserAndMapper laxParser, String resource ) {
        Map<String, Object> fileConfig;
        List<String> jsonFiles = IO.listByExt( resource, ".json" );
        for ( String jsonFile : jsonFiles ) {
            String contents = IO.read( jsonFile );
            fileConfig = laxParser.parseMap( contents );
            config.putAll( fileConfig );
        }
    }


}
