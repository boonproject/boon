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

package org.boon.datarepo.impl.indexes;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal class support class.
 * It tries to hide single values and nulls from the parent class.
 *
 * @param <T> Value we are holding
 */
class MultiValue<T> {
    List<T> values = null;

    public static <T> MultiValue<T> add( MultiValue<T> org, T newItem, int bucketSize ) {
        if ( org == null ) {
            return new MultiValue<>( newItem, bucketSize );
        } else {

            org.add( newItem );
        }
        return org;
    }

    public static <T> MultiValue<T> remove( MultiValue<T> org, T removeItem ) {
        if ( org == null ) {
            return null;
        }

        if ( removeItem != null ) {
            org.remove( removeItem );
        }

        return org.size() == 0 ? null : org;
    }

    private MultiValue() {

    }

    private MultiValue( T item, int bucketSize ) {
        values = new ArrayList( bucketSize );
        values.add( item );

    }

    private void add( T item ) {

        values.add( item );
    }

    private void remove( T item ) {
        values.remove( item );
    }

    T getValue() {

        return ( values.size() > 0 ) ? values.get( 0 ) : null;
    }

    final List<T> getValues() {
        return values;
    }


    int size() {
        return values.size();
    }

    void addTo( List<T> results ) {
        results.addAll( values );
    }


}
