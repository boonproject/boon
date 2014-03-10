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
import org.boon.Str;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.Chr;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.boon.core.reflection.BeanUtils.atIndex;

/**
 * Created by Richard on 3/8/14.
 */
public final class UniversalComparator implements Comparator<Object> {


    final String sortBy;
    final Map<String, FieldAccess> fields;
    final SortType sortType;
    final List<Comparator> comparators;
    private final boolean byPath;


    public UniversalComparator(String sortBy, Map<String, FieldAccess> fields,
                               SortType sortType, List<Comparator> comparators
                               ) {
        this.sortBy = sortBy;
        this.fields = fields;
        this.sortType = sortType;
        this.comparators = comparators;

        this.byPath = Str.in(Chr.array('.', '[', ']', '/'), sortBy);


    }

    @Override
    final public int compare( Object o1, Object o2 ) {

        Object value1;
        Object value2;

        /** Compare by this. */
        if (byPath || o1 instanceof Map) {
                        /* Grab the values of the sort field. */
            if ( sortType == SortType.ASCENDING ) {
                value1 = atIndex(o1, sortBy);
                value2 = atIndex(o2, sortBy);
            } else {
                value1 = atIndex(o2, sortBy);
                value2 = atIndex(o1, sortBy);
            }
        }

        else if ( sortBy.equals( "this" ) && o1 instanceof Comparable ) {
            if ( sortType == SortType.ASCENDING ) {
                value1 = o1;
                value2 = o2;
            } else {
                value1 = o2;
                value2 = o1;
            }
        }

        else {
            /* Compare by sort field. */
            FieldAccess field = fields.get( sortBy );
            if ( field == null ) {
                Exceptions.die(Str.lines(
                        "The fields was null for sortBy " + sortBy,
                        String.format("fields = %s", fields),
                        String.format("Outer object type = %s", o1.getClass().getName()),
                        String.format("Outer object is %s", o1)
                ));
            }
            /* Grab the values of the sort field. */
            if ( sortType == SortType.ASCENDING ) {
                value1 = field.getValue( o1 );
                value2 = field.getValue( o2 );
            } else {
                value1 = field.getValue( o2 );
                value2 = field.getValue( o1 );
            }
        }


        int compare = Sorting.compare(value1, value2);
        if ( compare == 0 ) {
            for ( Comparator comparator : comparators ) {
                compare = comparator.compare( o1, o2 );
                if ( compare != 0 ) {
                    break;
                }
            }
        }
        return compare;
    }


    public static Comparator universalComparator( final String sortBy, final Map<String, FieldAccess> fields,
                                                  final SortType sortType, final List<Comparator> comparators ) {
        return new UniversalComparator(sortBy, fields, sortType, comparators) ;
    }
}

