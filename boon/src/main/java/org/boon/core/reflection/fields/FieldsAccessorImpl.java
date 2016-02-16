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

import org.boon.Lists;
import org.boon.Maps;
import org.boon.core.reflection.Reflection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FieldsAccessorImpl implements FieldsAccessor {


    private final Map <Class<?>, FieldAccess[]> fieldMap = new ConcurrentHashMap<> ( );
    private final Map <Class<?>, Map<String, FieldAccess>> fieldMapMap = new ConcurrentHashMap<> ( );
    private final FieldAccessMode fieldAccessMode;
    private final boolean useAlias;
    private final boolean caseInsensitive;


    public FieldsAccessorImpl(boolean useAlias, FieldAccessMode fieldAccessMode) {
        this(fieldAccessMode, useAlias, false);
    }

    public FieldsAccessorImpl(FieldAccessMode fieldAccessMode, boolean useAlias, boolean caseInsensitive) {
        this.fieldAccessMode = fieldAccessMode;
        this.useAlias = useAlias;
        this.caseInsensitive = caseInsensitive;
    }


    @Override
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    @Override
    public Map<String, FieldAccess> getFieldsAsMap(Class<?> aClass) {


        Map<String, FieldAccess> fieldMap = fieldMapMap.get(aClass);
        if (fieldMap == null) {
            FieldAccess[] fields = getFields(aClass);
            fieldMap = new LinkedHashMap<>(fields.length);
            fieldMapMap.put(aClass, fieldMap);
        }

        return fieldMap;
    }

    public FieldAccess[] getFields(Class<? extends Object> aClass ) {
        FieldAccess[] fieldAccesses = fieldMap.get(aClass);
        if (fieldAccesses == null) {
            fieldAccesses = doGetFields ( aClass );
            fieldMap.put ( aClass, fieldAccesses );
        }
        return fieldAccesses;

    }

    private final FieldAccess[] doGetFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> fieldAccessMap =  null;


        switch (fieldAccessMode) {
            case PROPERTY:
                fieldAccessMap = Maps.copy ( Reflection.getPropertyFieldAccessors ( aClass ) );
                break;
            case FIELD:
                fieldAccessMap = Maps.copy ( Reflection.getAllAccessorFields ( aClass ) );
                break;
            case PROPERTY_THEN_FIELD:
                fieldAccessMap = Maps.copy ( Reflection.getPropertyFieldAccessMapPropertyFirstForSerializer ( aClass ) );
                break;
            case FIELD_THEN_PROPERTY:
                fieldAccessMap = Maps.copy ( Reflection.getPropertyFieldAccessMapFieldFirstForSerializer ( aClass ) );
                break;
        }

        List<FieldAccess> removeFields = new ArrayList<>();

        for (FieldAccess field : fieldAccessMap.values()) {
            if (field.isWriteOnly ())  {
                removeFields.add(field);
            }
        }

        for (FieldAccess fieldAccess : removeFields) {
            fieldAccessMap.remove(fieldAccess.name());
        }


        if (caseInsensitive) {

            List<Map.Entry<String, FieldAccess>> entryList = Lists.list(fieldAccessMap.entrySet());

            for (Map.Entry<String, FieldAccess> entry : entryList) {
                if (entry.getValue().isStatic()) {
                    continue;
                }

                fieldAccessMap.put(entry.getKey().toLowerCase(), entry.getValue());

                fieldAccessMap.put(entry.getKey().toUpperCase(), entry.getValue());

                fieldAccessMap.put(entry.getKey(), entry.getValue());
            }
        }

        if ( useAlias ) {
            Map<String, FieldAccess> fieldAccessMap2 = new LinkedHashMap<> ( fieldAccessMap.size () );

            for (FieldAccess fa : fieldAccessMap.values ()) {
                if (fa.isStatic()) {
                    continue;
                }
                String alias = fa.alias();
                if (caseInsensitive) {
                    alias = alias.toLowerCase();
                }
                fieldAccessMap2.put (alias, fa );
            }
            fieldAccessMap = fieldAccessMap2;
        } else {
            fieldAccessMap = fieldAccessMap;
        }

        return fieldAccessMap.values().toArray(new FieldAccess[fieldAccessMap.size()]);
    }



}
