package org.boon.config;

import org.boon.IO;
import org.boon.di.Context;
import org.boon.di.ContextFactory;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 2/5/14.
 */
public enum ContextConfig {


    JSON {
        public Context createContext( String... resources ) {
            Map<String, Object> config = new LinkedHashMap<>();
            JsonParserAndMapper laxParser = new JsonParserFactory().createLaxParser();
            Map<String, Object> fileConfig;
            for ( String resource : resources ) {
                Path path = IO.path( resource );
                if ( Files.isDirectory( path ) ) {
                    handleDir( config, laxParser, resource );
                } else {
                    fileConfig = laxParser.parseMap( IO.read( resource ) );
                    config.putAll( fileConfig );
                }
            }
            return ContextFactory.context( ContextFactory.fromMap( config ) );
        }

        public Context createContextUsingNamespace( String configNamespace, String... resources ) {
            Context parent = ContextFactory.context();

            for ( String resource : resources ) {
                Context child;
                if ( resource.endsWith( "/" ) ) {
                    child = handleDirWithNamespace( configNamespace, resource );
                    parent.add( child );
                } else if ( resource.endsWith( ".json" ) ) {
                    child = handleConfigWithNamespace( configNamespace, resource );
                    parent.add( child );
                }

            }
            return parent;
        }

        private Context handleDirWithNamespace( String namespace, String resource ) {
            Context parent = ContextFactory.context();
            List<String> jsonFiles = IO.listByExt( resource, ".json" );
            for ( String jsonFile : jsonFiles ) {
                Context child = handleConfigWithNamespace( namespace, jsonFile );
                parent.add( child );
            }
            return parent;
        }


        private Context handleConfigWithNamespace( String namespace, String resource ) {

            JsonCreatorEventHandler jsonCreatorEventHandler = new JsonCreatorEventHandler( namespace );
            JsonParserAndMapper laxParser = new JsonParserFactory().createParserWithEvents( jsonCreatorEventHandler );


            Map<String, Object> fileConfig = laxParser.parseMap( IO.read( resource ) );
            if ( fileConfig.containsKey( "META" ) ) {
                fileConfig.remove( "META" );
            }

            Context parent = ContextFactory.context( ContextFactory.fromMap( fileConfig ) );

            List<String> include = jsonCreatorEventHandler.include();


            Context child = createContextUsingNamespace( namespace, include.toArray( new String[include.size()] ) );

            parent.add( child );

            return parent;

        }

        private void handleDir( Map<String, Object> config, JsonParserAndMapper laxParser, String resource ) {
            Map<String, Object> fileConfig;
            List<String> jsonFiles = IO.listByExt( resource, ".json" );
            for ( String jsonFile : jsonFiles ) {
                String contents = IO.read( jsonFile );
                fileConfig = laxParser.parseMap( contents );
                config.putAll( fileConfig );
            }
        }

    };


    public abstract Context createContext( String... resources );

    public abstract Context createContextUsingNamespace( String configNamespace, String... resources );


}
