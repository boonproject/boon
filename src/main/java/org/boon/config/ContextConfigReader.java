package org.boon.config;


import org.boon.di.Context;
import org.boon.di.Inject;
import org.boon.core.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextConfigReader {

    private static final String CONFIG_DIR = System.getProperty( "BOON_CONFIG_DIR", "/etc/boon/");

    private static final String RUNTIME_MODULE_NAME = System.getProperty( "BOON_RUNTIME_MODULE_NAME", "boon");

    private static final String MODULE_CONFIG_DIR = System.getProperty( "BOON_MODULE_CONFIG_DIR", CONFIG_DIR + RUNTIME_MODULE_NAME + "/");



    @Inject
    private boolean useNameSpacePrefix = false;


    @Inject
    private ContextConfig contextConfig = ContextConfig.JSON;


    @Inject
    private String namespace;


    @Inject
    private List<String> resources = new ArrayList<>(  );


    @Inject
    private Predicate rules;

    public Context read() {

        if (resources.size() == 0 ) {
            resources.add( MODULE_CONFIG_DIR );
        }

        MetaConfigEvents metaConfigEvents = null;
        if ( rules != null  ) {

             metaConfigEvents = new MetaConfigEvents() {
                @Override
                public boolean parsedMeta( Map<String, Object> meta ) {
                    return handleMeta(meta);
                }
            };
        }

        return contextConfig.createContext( namespace, useNameSpacePrefix, metaConfigEvents, resources  );

    }

    private boolean handleMeta( Map<String,Object> meta ) {
        return rules.test(meta);
    }


    public ContextConfigReader resource(String resource) {
        resources.add( resource );
        return this;
    }


    public ContextConfigReader resources(String... resources) {
        for ( String resource : resources ) {
            this.resources.add( resource );
        }
        return this;
    }



    public ContextConfigReader userNamespacePrefix() {
        useNameSpacePrefix = true;
        return this;
    }



    public ContextConfigReader rule( Predicate criteria ) {
        this.rules = criteria;
        return this;
    }


    public ContextConfigReader namespace( String namespace ) {
        this.namespace = namespace;
        return this;
    }


    public static ContextConfigReader config() {
        return new ContextConfigReader();
    }


}
