/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.config;


import org.boon.di.Context;
import org.boon.di.Inject;
import org.boon.core.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextConfigReader {


    private static final String CONFIG_CLASSPATH_RESOURCE = System.getProperty( "BOON_CONFIG_CLASSPATH_RESOURCE", "classpath://etc/boon/");

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
            resources.add( CONFIG_CLASSPATH_RESOURCE );
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
