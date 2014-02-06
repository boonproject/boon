package org.boon.config;

import org.boon.IO;
import org.boon.di.Context;
import org.boon.di.ContextFactory;
import org.boon.di.Module;
import org.boon.di.impl.ContextImpl;
import org.boon.di.modules.SupplierModule;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;

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

        public Context createContextUsingNamespace( String namespace, String... resources ) {
            return ContextFactory.fromMap( createMapUsingNameSpace(namespace, resources)  );
        }

        private void setName( String name, Context context ) {
            ContextImpl i = ( ContextImpl ) context;
            i.setName( name );
        }

        private Map<String, Object> createMapUsingNameSpace( String namespace,  String... resources ) {

            return createMapUsingNameSpace( namespace, Arrays.asList(resources) );
        }

        private Map<String, Object> createMapUsingNameSpace( String namespace,  List<String> resources ) {

            Map<String, Object> all = new HashMap<>(  );

            Map<String, Object> child;

            for ( String resource : resources ) {
                if ( resource.endsWith( "/" ) ) {
                    child = createMapFromDir( namespace, resource );
                    all.putAll( child );
                } else if ( resource.endsWith( ".json" ) ) {
                    child = createMapFromFile( namespace, resource );
                    all.putAll( child );
                }

            }
            return all;
        }

        private Map<String,Object> createMapFromFile( String namespace, String resource ) {
            JsonCreatorEventHandler jsonCreatorEventHandler = new JsonCreatorEventHandler( namespace );
            JsonParserAndMapper laxParser = new JsonParserFactory().createParserWithEvents( jsonCreatorEventHandler );


            Map<String, Object> all;

            Map<String, Object> fileConfig = laxParser.parseMap( IO.read( resource ) );
            if ( fileConfig.containsKey( "META" ) ) {
                fileConfig.remove( "META" );
            }

            if (jsonCreatorEventHandler.include().size() > 0) {
                all = createMapUsingNameSpace(namespace, jsonCreatorEventHandler.include());
                all.putAll( fileConfig );
            } else {
               all = fileConfig;
            }

            return all;
        }


        private Map<String, Object> createMapFromDir( String namespace, String resource ) {

            Map<String, Object> all = new HashMap<>(  );

            Map<String, Object> child;

            List<String> jsonFiles = IO.listByExt( resource, ".json" );
            for ( String jsonFile : jsonFiles ) {
                child = createMapFromFile( namespace, jsonFile );
                all.putAll( child );
            }
            return all;
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
