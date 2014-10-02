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

import org.boon.datarepo.spi.SearchIndex;
import org.boon.core.Function;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;


/**
 * Allows more complex indexes.
 *
 * @see NestedKeySearchIndex
 * @see TypeHierarchyIndex
 */
public abstract class BaseIndexWrapper implements SearchIndex {
    protected final String[] path;
    protected SearchIndexDefault index = new SearchIndexDefault( Object.class );

    public BaseIndexWrapper( String... path ) {
        this.path = path;
    }

    @Override
    public Object findFirst() {
        return index.findFirst();
    }

    @Override
    public Object findLast() {
        return index.findLast();
    }

    @Override
    public Object findFirstKey() {
        return index.findFirstKey();
    }

    @Override
    public Object findLastKey() {
        return index.findLastKey();
    }

    @Override
    public List findEquals( Object o ) {
        return index.findEquals( o );
    }

    @Override
    public List findStartsWith( Object keyFrag ) {
        return index.findEquals( keyFrag );
    }

    @Override
    public List findEndsWith( Object keyFrag ) {
        return index.findEndsWith( keyFrag );
    }

    @Override
    public List findContains( Object keyFrag ) {
        return index.findContains( keyFrag );
    }

    @Override
    public List findBetween( Object start, Object end ) {
        return index.findBetween( start, end );
    }

    @Override
    public List findGreaterThan( Object o ) {
        return index.findGreaterThan( o );
    }

    @Override
    public List findLessThan( Object o ) {
        return index.findLessThan( o );
    }

    @Override
    public List findGreaterThanEqual( Object o ) {
        return index.findGreaterThanEqual( o );

    }

    @Override
    public List findLessThanEqual( Object o ) {
        return index.findLessThanEqual( o );
    }

    @Override
    public Object min() {
        return index.min();
    }

    @Override
    public Object max() {
        return index.max();
    }

    @Override
    public int count( Object o ) {
        return index.count( o );
    }

    @Override
    public void setComparator( Comparator collator ) {
        index.setComparator( collator );
    }

    @Override
    public Object get( Object o ) {
        return index.get( o );
    }

    @Override
    public void setKeyGetter( Function keyGetter ) {
        index.setKeyGetter( keyGetter );
    }

    @Override
    public List getAll( Object o ) {
        return index.getAll( o );
    }

    @Override
    public boolean deleteByKey( Object o ) {
        return index.deleteByKey( o );
    }

    @Override
    public boolean isPrimaryKeyOnly() {
        return index.isPrimaryKeyOnly();
    }

    @Override
    public void init() {
        index.init();
    }

    @Override
    public void setInputKeyTransformer( Function func ) {
        index.setInputKeyTransformer( func );
    }

    @Override
    public abstract boolean add( Object o );

    protected abstract List getKeys( Object o );

    @Override
    public abstract boolean delete( Object o );

    @Override
    public List all() {
        return index.all();
    }

    @Override
    public int size() {
        return index.size();
    }

    @Override
    public Collection toCollection() {
        return index.toCollection();
    }

    @Override
    public void clear() {
        index.clear();
    }

    public void setBucketSize( int size ) {
        index.setBucketSize( size );
    }


    public boolean has( Object key ) {
        return index.has( key );
    }
}
