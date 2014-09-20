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

package org.boon.datarepo.impl;

import org.boon.Exceptions;
import org.boon.Str;
import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.datarepo.*;
import org.boon.datarepo.impl.decorators.FilterWithSimpleCache;
import org.boon.datarepo.impl.decorators.ObjectEditorCloneDecorator;
import org.boon.datarepo.impl.decorators.ObjectEditorEventDecorator;
import org.boon.datarepo.impl.decorators.ObjectEditorLogNullCheckDecorator;
import org.boon.datarepo.impl.indexes.NestedKeySearchIndex;
import org.boon.datarepo.impl.indexes.TypeHierarchyIndex;
import org.boon.datarepo.modification.ModificationListener;
import org.boon.datarepo.spi.*;
import org.boon.core.Function;
import org.boon.functions.PropertyNameUtils;
import org.boon.core.Supplier;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.logging.Level;

import static org.boon.Exceptions.requireNonNull;


/**
 * This class is used to build Repo objects.
 *
 * @author Rick Hightower
 */
public class RepoBuilderDefault implements RepoBuilder {

    /**
     * Search index factory.
     *
     * @see org.boon.datarepo.impl.indexes.SearchIndexDefault
     */
    Function<Class, SearchIndex> searchIndexFactory;
    /**
     * Lookup index factory.
     *
     * @see org.boon.datarepo.impl.indexes.LookupIndexDefault
     */
    Function<Class, LookupIndex> lookupIndexFactory;
    /**
     * Unique lookupWithDefault index factory.
     *
     * @see org.boon.datarepo.impl.indexes.UniqueLookupIndex
     */
    Function<Class, LookupIndex> uniqueLookupIndexFactory;
    /**
     * Unique search index factory.
     *
     * @see org.boon.datarepo.impl.indexes.UniqueSearchIndex
     */
    Function<Class, SearchIndex> uniqueSearchIndexFactory;

    /**
     * Sets the Object Editor Factory
     *
     * @see ObjectEditor
     */
    Supplier<ObjectEditorComposer> objectEditorFactory;

    /**
     * Sets the searchableCollectionFactory.
     *
     * @see SearchableCollectionComposer
     */
    Supplier<SearchableCollectionComposer> searchableCollectionFactory;

    /**
     * Repo composer factory
     *
     * @see RepoComposer
     */
    Supplier<RepoComposer> repoComposerFactory;

    /**
     * @see Filter
     */
    Supplier<Filter> filterFactory;

    /**
     * Primary key for this object.
     */
    String primaryKey;


    /**
     * This holds the set of search indexes that the Repo will manage
     */
    Set<String> searchIndexes = new HashSet<>();
    /**
     * This holds the set of lookupIndexes that the Repo will manage.
     */
    Set<String> lookupIndexes = new HashSet<>();
    /**
     * This holds the uniqueSearchIndexes that this repo will manage.
     */
    Set<String> uniqueSearchIndexes = new HashSet<>();
    /**
     * This holds the uniqueLookupIndexes that this repo will manager.
     */
    Set<String> uniqueLookupIndexes = new HashSet<>();

    /**
     * If we are dealing with a complex key, like a derived key or some sort of
     * fancy nested and/or composite key, then we just tell the Repo
     * how to access the key with this here map.
     * <p/>
     * This maps property names to a Function (just like a method pointer that
     * returns something.
     * <p/>
     * In our case, they give us the key and the Function gives them the value of the key.
     */
    Map<String, Function> keyGetterMap = new HashMap();


    /**
     * Sets if we should use the field directly.
     */
    boolean useField = true;

    /**
     * Sets if we should use unsafe (fast) access to the field.
     */
    boolean useUnSafe = false;

    /**
     * Checks to see if we should enable runtime null checking and logging
     * This essentially means that we will not allow null keys or values.
     */
    boolean nullChecksAndLogging;


    /**
     * If you turn this on, then every time an object is sent to be
     * edited or added it is cloned.
     * In a multi-threaded application, this should prevent MT from
     * accessing objects that is in the data repo.
     */
    boolean cloneEdits;

    /**
     * This is not implemented yet.
     * It would allow to have only keys in the repo and the actual data could live
     * elsewhere like a cache or an off JVM heap RAM location.
     * It is is in the plans.
     */
    boolean storeKeyInIndexOnly;

    /**
     * This would turn on extra debugging.
     */
    boolean debug;

    /**
     * This sets up the log level for the builder.
     */
    Level level = Level.FINER;


    /**
     * This keeps a listStream of fields that we are reading per object.
     *
     * @see FieldAccess
     */
    private Map<String, FieldAccess> fields;


    /**
     * This provides an interface to the composable nature of a Repo.
     *
     * @see RepoComposer
     */
    private RepoComposer repo;

    /**
     * This is the Object editor that we are constructing.
     *
     * @see ObjectEditor
     */
    private ObjectEditor editor;


    /**
     * This is the Searchable Collection that the data repo uses
     * that we are constructing.
     *
     * @see SearchableCollection
     */
    private SearchableCollectionComposer query;


    /**
     * Turns caching on. Caching is very rudimentary at this point.
     * This can cache complex queries and hold on the results until
     * there is an update.
     */
    private boolean cache = false;

    /**
     * Holds a collection of comparators that will be used per property for the Repo.
     */
    private Map<String, Comparator> collators = new HashMap<>();


    /**
     * Holds a collection of key transformers per property for the Repo.
     * <p/>
     * Key transformers are used to for example make a case insensitive search.
     * It determines how the key is indexed.
     */
    private Map<String, Function> keyTransformers = new HashMap<>();


    /**
     * This holds a set of nestedIndexes.
     * <p/>
     * Employee.address.zip would be a nested index.
     */
    @Deprecated
    private Map<String, String[]> nestedIndexes = new HashMap<>();


    /**
     * Turning on this flag, turns on indexing type hierarchy.
     */
    private boolean indexHierarchy;

    /**
     * For non-unique indexes, this sets up how many values you would like
     * the index to hold initially. (It expands automatically).
     */
    private Map<String, Integer> indexBucketSize = new HashMap<>();


    /**
     * Turns on hashCode Optimization.
     * This means we will try to store the hashCode, and hold on to it
     * unless the object changes.
     */
    private boolean hashCodeOptimizationOn;

    /**
     * If you want duplicates removed from query results.
     */
    private boolean removeDuplication;

    /**
     * turn on modification events listenting.
     */
    boolean events = false;

    /**
     * Listen to modification changes.
     */
    ModificationListener[] listeners;
    private Class<?> itemClass;


    /**
     * Turns on property access instead of field access.
     * Field is the default.
     *
     * @param useProperty do you want property access or not
     * @return RepoBuilder
     */
    public RepoBuilder usePropertyForAccess( boolean useProperty ) {
        this.useField = !useProperty;
        return this;
    }

    /**
     * Turns on field access instead of property access.
     * Field is the default.
     *
     * @param useField do you want field access or not
     * @return RepoBuilder
     */
    public RepoBuilder useFieldForAccess( boolean useField ) {
        this.useField = useField;
        return this;

    }

    /**
     * Turns on field unsafe access instead of reflection.
     * Reflection is the default.
     *
     * @param useUnSafe use unsafe
     * @return RepoBuilder
     */
    public RepoBuilder useUnsafe( boolean useUnSafe ) {
        this.useUnSafe = useUnSafe;
        return this;

    }

    /**
     * Turns on logging and null checking for the Repo.
     *
     * @param nullChecks do you want null checks?
     * @return RepoBuilder
     * @see ObjectEditorLogNullCheckDecorator
     */
    @Override
    public RepoBuilder nullChecks( boolean nullChecks ) {
        this.nullChecksAndLogging = nullChecks;
        return this;
    }

    /**
     * Turns on logging and null checking for the Repo.
     *
     * @param logging do you want null checks?
     * @return RepoBuilder
     * @see ObjectEditorLogNullCheckDecorator
     */
    @Override
    public RepoBuilder addLogging( boolean logging ) {
        this.nullChecksAndLogging = logging;
        return this;
    }

    /**
     * Clones the object in the repo before editing and also
     * clones returns values.
     * This should limit two threads from getting the same object that is in
     * the repo.
     *
     * @param cloneEdits do you want cloning?
     * @return RepoBuilder
     * @see ObjectEditorCloneDecorator
     */
    @Override
    public RepoBuilder cloneEdits( boolean cloneEdits ) {
        this.cloneEdits = cloneEdits;
        return this;
    }

    /**
     * This caches query results until there is an update.
     *
     * @return RepoBuilder
     */
    @Override
    public RepoBuilder useCache() {
        this.cache = true;
        return this;
    }

    /**
     * Stores only the keys in the index. The data is stored elsewhere.
     * This is not implemented yet.
     *
     * @return RepoBuilder
     */
    @Override
    public RepoBuilder storeKeyInIndexOnly() {
        this.storeKeyInIndexOnly = true;

        return this;
    }

    /**
     * Register event listeners for modification changes.
     *
     * @param listeners listStream of event listeners
     * @return RepoBuilder
     * @see ObjectEditorEventDecorator
     */
    @Override
    public RepoBuilder events( ModificationListener... listeners ) {
        events = true;
        this.listeners = listeners;
        return this;
    }


    /**
     * @return
     */
    @Override
    public RepoBuilder debug() {
        this.debug = true;
        return this;
    }

    /**
     * @param factory
     * @return
     */
    @Override
    public RepoBuilder searchIndexFactory( Function<Class, SearchIndex> factory ) {
        this.searchIndexFactory = factory;
        return this;
    }

    /**
     * @param factory
     * @return
     */
    @Override
    public RepoBuilder uniqueLookupIndexFactory( Function<Class, LookupIndex> factory ) {
        this.uniqueLookupIndexFactory = factory;
        return this;
    }

    /**
     * @param factory
     * @return
     */
    @Override
    public RepoBuilder uniqueSearchIndexFactory( Function<Class, SearchIndex> factory ) {
        this.uniqueSearchIndexFactory = factory;
        return this;
    }

    /**
     * @param factory
     * @return
     */
    @Override
    public RepoBuilder lookupIndexFactory( Function<Class, LookupIndex> factory ) {
        this.lookupIndexFactory = factory;
        return this;
    }

    /**
     * @param factory
     * @return
     */
    @Override
    public RepoBuilder repoFactory( Supplier<RepoComposer> factory ) {
        this.repoComposerFactory = factory;
        return this;
    }

    /**
     * @param factory
     * @return
     */
    @Override
    public RepoBuilder filterFactory( Supplier<Filter> factory ) {
        this.filterFactory = factory;
        return this;
    }

    /**
     * @param propertyName
     * @return
     */
    @Override
    public RepoBuilder primaryKey( String propertyName ) {
        this.primaryKey = propertyName;
        return this;
    }

    /**
     * @param propertyName
     * @return
     */
    @Override
    public RepoBuilder lookupIndex( String propertyName ) {
        this.lookupIndexes.add( propertyName );
        return this;
    }

    /**
     * @param propertyName
     * @return
     */
    @Override
    public RepoBuilder uniqueLookupIndex( String propertyName ) {
        return this.lookupIndex( propertyName, true );
    }

    /**
     * @param propertyName
     * @param unique
     * @return
     */
    public RepoBuilder lookupIndex( String propertyName, boolean unique ) {
        if ( unique ) {
            this.uniqueLookupIndexes.add( propertyName );
        } else {
            this.lookupIndexes.add( propertyName );
        }
        return this;
    }

    /**
     * @param propertyName
     * @return
     */
    @Override
    public RepoBuilder searchIndex( String propertyName ) {
        this.searchIndexes.add( propertyName );
        return this;
    }

    /**
     * @param propertyName
     * @return
     */
    @Override
    public RepoBuilder uniqueSearchIndex( String propertyName ) {
        return searchIndex( propertyName, true );
    }


    /**
     * @param propertyName
     * @return
     */
    @Override
    public RepoBuilder collateIndex( String propertyName ) {
        collators.put( propertyName, Collator.getInstance() );
        return this;
    }

    /**
     * @param propertyName
     * @param locale
     * @return
     */
    @Override
    public RepoBuilder collateIndex( String propertyName, Locale locale ) {
        collators.put( propertyName, Collator.getInstance( locale ) );
        return this;
    }

    /**
     * @param propertyName
     * @param collator
     * @return
     */
    @Override
    public RepoBuilder collateIndex( String propertyName, Comparator collator ) {
        collators.put( propertyName, collator );
        return this;
    }

    /**
     * @param propertyName
     * @param unique
     * @return
     */
    public RepoBuilder searchIndex( String propertyName, boolean unique ) {
        if ( unique ) {
            this.uniqueSearchIndexes.add( propertyName );
        } else {
            this.searchIndexes.add( propertyName );
        }
        return this;
    }

    /**
     * @param propertyName
     * @param keyGetter
     * @return
     */
    @Override
    public RepoBuilder keyGetter( String propertyName, Function<?, ?> keyGetter ) {
        keyGetterMap.put( propertyName, keyGetter );
        return this;
    }

    /**
     *
     */
    private void initializeTheFactories() {

        if ( this.repoComposerFactory == null ) {
            this.repoComposerFactory = SPIFactory.getRepoFactory();
        }
        if ( this.lookupIndexFactory == null ) {
            this.lookupIndexFactory = SPIFactory.getLookupIndexFactory();
        }
        if ( this.searchIndexFactory == null ) {
            this.searchIndexFactory = SPIFactory.getSearchIndexFactory();
        }
        if ( this.uniqueLookupIndexFactory == null ) {
            this.uniqueLookupIndexFactory = SPIFactory.getUniqueLookupIndexFactory();
        }
        if ( this.uniqueSearchIndexFactory == null ) {
            this.uniqueSearchIndexFactory = SPIFactory.getUniqueSearchIndexFactory();
        }
        if ( this.searchableCollectionFactory == null ) {
            this.searchableCollectionFactory = SPIFactory.getSearchableCollectionFactory();
        }
        if ( this.filterFactory == null ) {
            this.filterFactory = SPIFactory.getFilterFactory();
        }

        if ( this.objectEditorFactory == null ) {
            this.objectEditorFactory = SPIFactory.getObjectEditorFactory();
        }

    }

    /**
     * @param key
     * @param clazz
     * @param classes
     * @param <KEY>
     * @param <ITEM>
     * @return
     */
    @Override
    public <KEY, ITEM> Repo<KEY, ITEM> build( Class<KEY> key, Class<ITEM> clazz, Class<?>... classes ) {
        return build( null, key, clazz, classes );
    }

    /**
     * @param primitiveKey
     * @param key
     * @param clazz
     * @param classes
     * @param <KEY>
     * @param <ITEM>
     * @return
     */
    public <KEY, ITEM> Repo<KEY, ITEM> build( Class<?> primitiveKey, Class<KEY> key, Class<ITEM> clazz, Class<?>... classes ) {

        /* Initialize factories. */
        initializeTheFactories();


        /* Reflect and load all of the fields. */
        loadFields( clazz, classes );

        this.itemClass = clazz;



        /* Construct */
        this.repo = this.repoComposerFactory.get();
        this.editor = constructObjectEditor( fields );
        SearchableCollectionComposer query = constructSearchableCollection( primitiveKey, clazz, repo, fields );
        query.setRemoveDuplication( this.removeDuplication );

        /* Inject */
        repo.setSearchableCollection( ( SearchableCollection<KEY, ITEM> ) query );
        ( ( ObjectEditorComposer ) editor ).setSearchableCollection( ( SearchableCollection<KEY, ITEM> ) query );

        editor = decorateEditor( editor );
        repo.setObjectEditor( ( ObjectEditor ) editor );

        return ( Repo<KEY, ITEM> ) repo;
    }

    /**
     * @param clazz
     * @param classes
     * @param <ITEM>
     */
    private <ITEM> void loadFields( Class<ITEM> clazz, Class<?>[] classes ) {



        if (Typ.isMap(clazz)) {
            return;
        }
        /**
         * Load all of the fields that we need.
         */
        this.fields = BeanUtils.getFieldsFromObject( clazz );

        for ( Class<?> cls : classes ) {
            Map<String, FieldAccess> fieldsComponentType
                    = BeanUtils.getFieldsFromObject( cls );

            for ( String sKey : fieldsComponentType.keySet() ) {
                if ( !fields.containsKey( sKey ) ) {
                    fields.put( sKey, fieldsComponentType.get( sKey ) );
                }
            }
        }
    }

    /**
     * @param primitiveKey
     * @param itemClazz
     * @param repo
     * @param fields
     * @return
     */
    private SearchableCollectionComposer constructSearchableCollection( Class<?> primitiveKey, Class<?> itemClazz, RepoComposer repo, Map<String, FieldAccess> fields ) {

        /* Create the searchable collection. */
        query = searchableCollectionFactory.get();

        /* Create the filter object. */
        Filter filter = this.filterFactory.get();


        configPrimaryKey( primitiveKey == null ? itemClazz : primitiveKey, fields );


        configIndexes( repo, fields );


        query.setFilter( filter );


        query.setFields( fields );

        query.init();

        if ( this.cache ) {
            filter = new FilterWithSimpleCache( filter );
        }

        query.setFilter( filter );

        return query;
    }

    private ObjectEditor constructObjectEditor( Map<String, FieldAccess> fields ) {
        ObjectEditorComposer editorComposer = this.objectEditorFactory.get();
        if ( this.hashCodeOptimizationOn ) {
            editorComposer.hashCodeOptimizationOn();
        }

        ObjectEditor editor = ( ObjectEditor ) editorComposer;
        editorComposer.init();

        if ( this.cloneEdits ) {
            editorComposer.setLookupAndExcept( true );
        }

        editorComposer.setFields( fields );
        return editor;
    }


    private ObjectEditor decorateEditor( ObjectEditor editor ) {
        if ( debug || nullChecksAndLogging ) {
            ObjectEditorLogNullCheckDecorator logNullCheckDecorator = new ObjectEditorLogNullCheckDecorator( editor );
            logNullCheckDecorator.setLevel( level );
            logNullCheckDecorator.setDebug( debug );

            editor = logNullCheckDecorator;
        }

        if ( cloneEdits ) {
            editor = new ObjectEditorCloneDecorator( editor );
        }

        if ( events ) {
            ObjectEditorEventDecorator eventManager = new ObjectEditorEventDecorator( editor );
            for ( ModificationListener l : listeners ) {
                eventManager.add( l );
            }
            editor = eventManager;
        }
        return editor;
    }

    @Override
    public RepoBuilder level( Level level ) {
        this.level = level;
        return this;
    }

    @Override
    public RepoBuilder upperCaseIndex( String property ) {
        this.keyTransformers.put( property, PropertyNameUtils.upperCase );
        return this;
    }

    @Override
    public RepoBuilder lowerCaseIndex( String property ) {
        this.keyTransformers.put( property, PropertyNameUtils.lowerCase );
        return this;

    }

    @Override
    public RepoBuilder camelCaseIndex( String property ) {
        this.keyTransformers.put( property, PropertyNameUtils.camelCase );
        return this;

    }

    @Override
    public RepoBuilder underBarCaseIndex( String property ) {
        this.keyTransformers.put( property, PropertyNameUtils.underBarCase );
        return this;
    }

    @Deprecated
    @Override
    public RepoBuilder nestedIndex( String... propertyPath ) {

        this.nestedIndexes.put( Str.join( '.', propertyPath ), propertyPath );

        return this;

    }

    @Override
    public RepoBuilder indexHierarchy() {
        this.indexHierarchy = true;
        return this;
    }

    @Override
    public RepoBuilder indexBucketSize( String propertyName, int size ) {
        this.indexBucketSize.put( propertyName, size );
        return this;
    }

    @Override
    public RepoBuilder hashCodeOptimizationOn() {
        this.hashCodeOptimizationOn = true;
        return this;
    }

    @Override
    public RepoBuilder removeDuplication( boolean removeDuplication ) {
        this.removeDuplication = removeDuplication;
        return this;
    }

    private Function createKeyGetter( final FieldAccess field ) {
        Exceptions.requireNonNull( field, "field cannot be null" );

        return new Function() {
            @Override
            public Object apply( Object o ) {
                return field.getValue( o );
            }
        };
    }

    /**
     * @param repo
     * @param fields
     */
    private void configIndexes( RepoComposer repo,
                                Map<String, FieldAccess> fields ) {

        if ( this.indexHierarchy ) {
            TypeHierarchyIndex index = new TypeHierarchyIndex();
            index.setComparator( this.collators.get( "_type" ) );
            index.setInputKeyTransformer( this.keyTransformers.get( "_type" ) );
            index.init();
            ( ( SearchableCollection ) query ).addSearchIndex( "_type", index );
        }

        for ( String prop : nestedIndexes.keySet() ) {
            NestedKeySearchIndex index = new NestedKeySearchIndex( this.nestedIndexes.get( prop ) );
            configIndex( prop, index );
        }
        for ( String prop : searchIndexes ) {
            SearchIndex searchIndex = null;

            if (!Typ.isMap(itemClass) && !isPropPath(prop)) {
                FieldAccess fieldAccess = fields.get( prop );

                requireNonNull( fieldAccess, "Field access for property was null. " + prop );

                Class<?> type = fieldAccess.type();

                searchIndex = this.searchIndexFactory.apply( type );
            } else {
                searchIndex = this.searchIndexFactory.apply( null );

            }
            configSearchIndex( fields, prop, searchIndex );

        }
        for ( String prop : uniqueSearchIndexes ) {

            if (!Typ.isMap(itemClass) && !isPropPath(prop)) {

                FieldAccess fieldAccess = fields.get( prop );
                requireNonNull( fieldAccess, "Field access for property was null. " + prop );

                SearchIndex searchIndex = this.uniqueSearchIndexFactory.apply( fieldAccess.type() );
                configSearchIndex( fields, prop, searchIndex );
            } else {

                SearchIndex searchIndex = this.uniqueSearchIndexFactory.apply( Object.class );
                configSearchIndex( fields, prop, searchIndex );

            }
        }

        for ( String prop : lookupIndexes ) {

            if ( !Typ.isMap(itemClass) && !isPropPath(prop) ) {


                FieldAccess fieldAccess = fields.get( prop );
                Exceptions.requireNonNull( fieldAccess, "Field access for property was null. " + prop );

                LookupIndex index = this.lookupIndexFactory.apply( fieldAccess.type() );
                configLookupIndex( fields, prop, index );
            } else {


                LookupIndex index = this.lookupIndexFactory.apply( Object.class );
                configLookupIndex( fields, prop, index );

            }
        }
        for ( String prop : uniqueLookupIndexes ) {


            if (!Typ.isMap(itemClass) && !isPropPath(prop) ) {


                FieldAccess fieldAccess = fields.get( prop );
                Exceptions.requireNonNull( fieldAccess, "Field access for property was null. " + prop );


                LookupIndex index = this.uniqueLookupIndexFactory.apply( fieldAccess.type() );
                configLookupIndex( fields, prop, index );
            } else {

                LookupIndex index = this.uniqueLookupIndexFactory.apply( Object.class );
                configLookupIndex( fields, prop, index );

            }
        }

    }

    private boolean isPropPath(String prop) {
        if (prop.contains(".")) return true;
        if (prop.equals("this")) return true;
        if (prop.equals("[")) return true;

        return false;
    }

    private void configLookupIndex( Map<String, FieldAccess> fields, String prop, LookupIndex index ) {
        Function kg = getKeyGetterOrCreate( fields, prop );



        index.setInputKeyTransformer( this.keyTransformers.get( prop ) );
        index.setKeyGetter( kg );
        index.setBucketSize( this.indexBucketSize.get( prop ) == null ? 3 : this.indexBucketSize.get( prop ) );

        index.init();
        ( ( SearchableCollection ) query ).addLookupIndex( prop, index );
    }

    private void configSearchIndex( Map<String, FieldAccess> fields, String prop, SearchIndex searchIndex ) {

        FieldAccess fieldAccess = fields == null ? null : fields.get(prop);

        Comparator comparator = this.collators.get(prop);
        if (comparator==null) {
            if (fieldAccess!=null && fieldAccess.type() == Typ.number) {
                comparator = new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {

                        if (o1 instanceof Long) {
                            long long1 = o1.longValue();
                            long long2 = o2.longValue();
                            if (long1 > long2) {
                                return 1;
                            } else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (o1 instanceof Double) {
                            double long1 = o1.doubleValue();
                            double long2 = o2.doubleValue();
                            if (long1 > long2) {
                                return 1;
                            } else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else if (o1 instanceof BigDecimal) {
                            double long1 = o1.doubleValue();
                            double long2 = o2.doubleValue();
                            if (long1 > long2) {
                                return 1;
                            } else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else {
                            double long1 = o1.doubleValue();
                            double long2 = o2.doubleValue();
                            if (long1 > long2) {
                                return 1;
                            } else if (long1 < long2) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }

                    }
                };
            }
        }

        searchIndex.setComparator( comparator );
        searchIndex.setInputKeyTransformer( this.keyTransformers.get( prop ) );
        Function kg = getKeyGetterOrCreate( fields, prop );
        searchIndex.setKeyGetter( kg );
        searchIndex.setBucketSize( this.indexBucketSize.get( prop ) == null ? 3 : this.indexBucketSize.get( prop ) );

        searchIndex.init();
        ( ( SearchableCollection ) query ).addSearchIndex( prop, searchIndex );
    }

    private void configIndex( String prop, NestedKeySearchIndex index ) {
        index.setComparator( this.collators.get( prop ) );
        index.setInputKeyTransformer( this.keyTransformers.get( prop ) );
        index.setBucketSize( this.indexBucketSize.get( prop ) == null ? 3 : this.indexBucketSize.get( prop ) );
        index.init();
        ( ( SearchableCollection ) query ).addSearchIndex( prop, index );
    }

    private Function getKeyGetterOrCreate( Map<String, FieldAccess> fields,
                                           final String prop ) {
        if (Typ.isMap(itemClass))  {


            Function kg = null;

            kg = this.keyGetterMap.get( prop );

            if ( kg == null ) {

                keyGetterMap.put( prop, new Function() {
                    @Override
                    public Object apply(Object o) {
                        return BeanUtils.atIndex(o, prop);
                    }
                } );
            }
            return this.keyGetterMap.get( prop );

        }


        Exceptions.requireNonNull( fields, "fields cannot be null" );
        Exceptions.requireNonNull( prop, "prop cannot be null" );

        Function kg = null;

        kg = this.keyGetterMap.get( prop );

        if ( kg == null ) {

            if (prop.contains(".") || prop.contains("this") || prop.contains("[")) {
                keyGetterMap.put(prop, new Function() {
                    @Override
                    public Object apply(Object o) {
                        return BeanUtils.atIndex(o, prop);
                    }
                } );
                kg = this.keyGetterMap.get( prop );

            } else {
                FieldAccess field = fields.get( prop );
                kg = createKeyGetter( field );
                keyGetterMap.put( prop, kg );
            }
        }
        return kg;

    }

    private void configPrimaryKey( Class<?> type, Map<String, FieldAccess> fields ) {

        Exceptions.requireNonNull( primaryKey, "primary key cannot be null" );

        LookupIndex primaryKeyIndex = this.uniqueLookupIndexFactory.apply( type );


        if ( !Typ.isMap(itemClass) && !fields.containsKey( primaryKey ) ) {
            throw new IllegalStateException(
                    String.format( "Fields does not have primary key %s",
                            primaryKey ) );
        }


        primaryKeyIndex.setKeyGetter( getKeyGetterOrCreate( fields, this.primaryKey ) );
        query.setPrimaryKeyName( this.primaryKey );
        query.setPrimaryKeyGetter( this.keyGetterMap.get( this.primaryKey ) );


        ( ( SearchableCollection ) query ).addLookupIndex( this.primaryKey, primaryKeyIndex );


    }


}
