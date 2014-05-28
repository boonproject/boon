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

package org.boon.core.reflection.fields;

import org.boon.core.reflection.Reflection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rick on 1/3/14.
 */
public class PropertyFieldAccessor implements FieldsAccessor {

    private final Map<Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ();


    private final boolean useAlias;
    private final boolean caseInsensitive;

    public PropertyFieldAccessor(boolean useAlias) {
        this(useAlias, false);
    }

    public PropertyFieldAccessor(boolean useAlias, boolean caseInsensitive) {
        this.useAlias = useAlias;
        this.caseInsensitive = caseInsensitive;
    }


    public final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get ( aClass );
        if ( map == null ) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
    }

    @Override
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    private final Map<String, FieldAccess> doGetFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> fieldAccessMap =Reflection.getPropertyFieldAccessors ( aClass );
        if (caseInsensitive) {
            Map<String, FieldAccess> mapOld = fieldAccessMap;
            fieldAccessMap = new LinkedHashMap<>();
            for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
                fieldAccessMap.put(entry.getKey().toLowerCase(), entry.getValue());
            }
        }

        if ( useAlias ) {
            Map<String, FieldAccess> fieldAccessMap2 = new LinkedHashMap<> ( fieldAccessMap.size () );

            for (FieldAccess fa : fieldAccessMap.values ()) {
                String alias = fa.alias();
                if(caseInsensitive) {
                    alias = alias.toLowerCase();
                }
                fieldAccessMap2.put (alias, fa );
            }
            return fieldAccessMap2;
        } else {
            return fieldAccessMap;
        }

    }


}
