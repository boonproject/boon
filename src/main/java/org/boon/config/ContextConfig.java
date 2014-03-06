package org.boon.config;

import org.boon.IO;
import org.boon.di.Context;
import org.boon.di.DependencyInjection;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;

import java.nio.file.Path;
import java.util.*;


public enum ContextConfig {


    JSON {


        @Override
        public Context createContext( String configNamespace, boolean startsWith, MetaConfigEvents events, List<String> resources ) {
            return createContext( configNamespace, startsWith, events, resources.toArray( new String[resources.size()] ) );
        }

        public Context createContext( String... resources ) {
            return createContext( null, false, null, resources );
        }


        public Context createContext( List<String> resources ) {
            return createContext( null, false, null, resources.toArray( new String[resources.size()] ) );
        }



        public Context createContext(  String namespace, boolean startsWith,  String... resources ) {
            return createContext( namespace, startsWith, null, resources );
        }

        @Override
        public Context createContext( MetaConfigEvents events,  String... resources ) {
            return createContext( null, false, events, resources );
        }

        public Context createContext( String namespace, boolean startsWith, MetaConfigEvents events, String... resources ) {
            return DependencyInjection.fromMap(createConfigMap(namespace, startsWith, events, resources));
        }


        private Map<String, Object> createConfigMap( String namespace, boolean startsWith,
                                                     MetaConfigEvents events, String... resources ) {
            return createConfigMap( namespace, startsWith, events, Arrays.asList( resources ) );
        }



        private Map<String, Object> createConfigMap( String namespace, boolean startsWith,
                                                     MetaConfigEvents events,
                                                     List<String> resources ) {

            Map<String, Object> all = new HashMap<>(  );

            Map<String, Object> child;

            for ( String resource : resources ) {
                if (startsWith) {
                    if (!resource.startsWith( namespace + "." ) ){
                       continue;
                    }
                }
                if ( resource.endsWith( "/" ) ) {
                    child = createMapFromDir( namespace, startsWith, events, resource );
                    all.putAll( child );
                } else if ( resource.endsWith( ".json" ) ) {
                    child = createMapFromFile( namespace, startsWith, events, IO.path(resource) );
                    all.putAll( child );
                }

            }
            return all;
        }

        private Map<String,Object> createMapFromFile( String namespace, boolean startsWith, MetaConfigEvents events, Path resource ) {
            NamespaceEventHandler jsonCreatorEventHandler = new NamespaceEventHandler( namespace, events );
            JsonParserAndMapper laxParser = new JsonParserFactory().createParserWithEvents( jsonCreatorEventHandler );


            Map<String, Object> all;

            Map<String, Object> fileConfig = laxParser.parseMap( IO.read( resource ) );
            if ( fileConfig.containsKey( "META" ) ) {
                fileConfig.remove( "META" );
            }

            if (jsonCreatorEventHandler.include().size() > 0) {
                all = createConfigMap( namespace, startsWith, events, jsonCreatorEventHandler.include() );
                all.putAll( fileConfig );
            } else {
               all = fileConfig;
            }

            return all;
        }


        private Map<String, Object> createMapFromDir( String namespace, boolean startWith, MetaConfigEvents events, String resource ) {

            Map<String, Object> all = new HashMap<>(  );

            Map<String, Object> child;

            List<Path> jsonFiles = IO.pathsByExt(resource, ".json");
            for ( Path jsonFile : jsonFiles ) {
                child = createMapFromFile( namespace, startWith, events, jsonFile );
                all.putAll( child );
            }
            return all;
        }

    };


    public abstract Context createContext( String... resources );


    public abstract Context createContext( List<String> resources );



    public abstract Context createContext( String configNamespace, boolean startsWith, String... resources );

    public abstract Context createContext(  MetaConfigEvents events,  String... resources );

    public abstract Context createContext(  String configNamespace,  boolean startsWith, MetaConfigEvents events, String... resources );

    public abstract Context createContext(  String configNamespace,  boolean startsWith, MetaConfigEvents events, List<String> resources );

}
