package org.boon.config;


import org.boon.di.Context;
import org.boon.di.Inject;

public class ContextConfigReader {

    private static final String CONFIG_DIR = System.getProperty( "BOON_CONFIG_DIR", "/etc/boon/");

    private static final String RUNTIME_MODULE_NAME = System.getProperty( "BOON_RUNTIME_MODULE_NAME", "boon");

    private static final String MODULE_CONFIG_DIR = System.getProperty( "BOON_MODULE_CONFIG_DIR", CONFIG_DIR + RUNTIME_MODULE_NAME + "/");

    @Inject
    private ContextConfig contextConfig;


    public Context read() {
        return contextConfig.createContext( MODULE_CONFIG_DIR  );
    }


    public Context read( String namespace ) {
        return contextConfig.createContextUsingNamespace( namespace, MODULE_CONFIG_DIR  );
    }



    public Context read( String namespace, boolean configFileMustStartWithNameSpace ) {
        return contextConfig.createContext( namespace, MODULE_CONFIG_DIR  );
    }
}
