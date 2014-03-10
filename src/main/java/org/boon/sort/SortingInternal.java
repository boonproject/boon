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

package org.boon.sort;

import org.boon.Exceptions;
import org.boon.core.reflection.fields.FieldAccess;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.boon.sort.Sorting.thisUniversalComparator;

/**
 * Created by Richard on 3/8/14.
 */
public class SortingInternal {


    /**
     * This is the work horse. It does all of the sorting work for the simple cases.
     * Nulls are last by default.
     * @param list the list you want to sort.
     * @param sortBy what you want to sort the list by.
     * @param fields the reflection fields
     * @param ascending true for ascending
     */
    public static void sort( List list, String sortBy, Map<String, FieldAccess> fields, boolean ascending) {

        sort(list, sortBy, fields, ascending, false);

    }

    /**
     * This is the work horse. It does all of the sorting work for the simple cases.
     * @param list the list you want to sort.
     * @param sortBy what you want to sort the list by.
     * @param fields the reflection fields
     * @param ascending true for ascending
     */
    public static void sort( List list, String sortBy, Map<String, FieldAccess> fields, boolean ascending,
                             boolean nullsFirst) {

        try {


            /* If this list is null or empty, we have nothing to do so return. */
            if ( list == null || list.size() == 0 ) {
                return;
            }

            /* Grab the first item in the list and see what it is. */
            Object o = list.get( 0 );

            /* if the sort by string is is this, and the object is comparable then use the objects
            themselves for the sort.
             */
            if ( sortBy.equals( "this" )  ) {

                Collections.sort(list, thisUniversalComparator(ascending, nullsFirst));
                return;
            }

            /* If you did not sort by "this", then sort by the field. */

            final FieldAccess field = fields.get( sortBy );

            if ( field != null ) {

                Collections.sort( list, Sorting.universalComparator(field, ascending, nullsFirst) );

            }

        } catch (Exception ex) {
            Exceptions.handle(ex, "list", list, "\nsortBy", sortBy, "fields", fields, "ascending", ascending,
            "nullFirst", nullsFirst);
        }
    }

}
