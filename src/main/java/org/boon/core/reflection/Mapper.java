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

package org.boon.core.reflection;

import org.boon.core.Value;
import org.boon.core.reflection.fields.FieldsAccessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Richard on 2/17/14.
 */
public class Mapper {

    final FieldsAccessor fieldsAccessor;
    final Set<String> ignoreSet;
    final String view;
    final boolean respectIgnore;

    public Mapper( FieldsAccessor fieldsAccessor, Set<String> ignoreSet, String view, boolean respectIgnore ) {
        this.fieldsAccessor = fieldsAccessor;
        this.ignoreSet = ignoreSet;
        this.view = view;
        this.respectIgnore = respectIgnore;
    }

    public  <T> T fromMap( Map<String, Object> map, Class<T> cls ) {
        return MapObjectConversion.fromMap(respectIgnore, view, this.fieldsAccessor, map, cls, ignoreSet);
    }

    public  <T> T fromList( List<?> list, Class<T> cls  ) {
        return MapObjectConversion.fromList( respectIgnore, view, this.fieldsAccessor, list, cls, ignoreSet );
    }



    public  <T> T fromValueMap( final Map<String, Value> map, Class<T> cls  ) {
        return MapObjectConversion.fromValueMap( respectIgnore, view, this.fieldsAccessor,  map, cls, ignoreSet) ;
    }


    public  <T> List<T> convertListOfMapsToObjects(Class<T> componentType, List<?> list) {
         return MapObjectConversion.convertListOfMapsToObjects( respectIgnore, view, fieldsAccessor,
                 componentType, list, ignoreSet );

    }

}