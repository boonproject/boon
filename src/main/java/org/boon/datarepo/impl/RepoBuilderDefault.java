package org.boon.datarepo.impl;

import org.boon.Str;
import org.boon.*;
import org.boon.datarepo.*;
import org.boon.datarepo.impl.decorators.FilterWithSimpleCache;
import org.boon.datarepo.impl.decorators.ObjectEditorCloneDecorator;
import org.boon.datarepo.impl.decorators.ObjectEditorEventDecorator;
import org.boon.datarepo.impl.decorators.ObjectEditorLogNullCheckDecorator;
import org.boon.datarepo.impl.indexes.NestedKeySearchIndex;
import org.boon.datarepo.impl.indexes.TypeHierarchyIndex;
import org.boon.datarepo.modification.ModificationListener;
import org.boon.predicates.PropertyNameUtils;
import org.boon.datarepo.spi.*;
import org.boon.predicates.Function;
import org.boon.predicates.Supplier;


import java.text.Collator;
import java.util.*;
import java.util.logging.Level;

import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;




public class RepoBuilderDefault implements RepoBuilder {

    Function<Class, SearchIndex> searchIndexFactory;
    Function<Class, LookupIndex> lookupIndexFactory;
    Function<Class, LookupIndex> uniqueLookupIndexFactory;
    Function<Class, SearchIndex> uniqueSearchIndexFactory;
    Supplier<ObjectEditorComposer> objectEditorFactory;
    Supplier<SearchableCollectionComposer> searchableCollectionFactory;


    Supplier<RepoComposer> repoComposerFactory;
    Supplier<Filter> filterFactory;

    String primaryKey;
    Set<String> searchIndexes = new HashSet<>();
    Set<String> lookupIndexes = new HashSet<>();
    Set<String> uniqueSearchIndexes = new HashSet<>();
    Set<String> uniqueLookupIndexes = new HashSet<>();

    Map<String, Function> keyGetterMap = new HashMap();


    boolean useField = true;
    boolean useUnSafe = false;
    boolean nullChecksAndLogging;
    boolean cloneEdits;
    boolean storeKeyInIndexOnly;
    boolean debug;
    Level level = Level.FINER;
    private Map<String, FieldAccess> fields;
    private RepoComposer repo;
    private ObjectEditor editor;
    private SearchableCollectionComposer query;
    private boolean cache = false;
    private Map<String, Comparator> collators = new HashMap<String, Comparator>();
    private Map<String, Function> keyTransformers = new HashMap<>();
    private Map<String, String[]> nestedIndexes = new HashMap<>();
    private boolean indexHierarchy;
    private Map<String, Integer> indexBucketSize = new HashMap<>();
    private boolean hashCodeOptimizationOn;
    private boolean removeDuplication;


    public RepoBuilder usePropertyForAccess(boolean useProperty) {
        this.useField = !useProperty;
        return this;
    }

    public RepoBuilder useFieldForAccess(boolean useField) {
        this.useField = useField;
        return this;

    }

    public RepoBuilder useUnsafe(boolean useUnSafe) {
        this.useUnSafe = useUnSafe;
        return this;

    }

    @Override
    public RepoBuilder nullChecks(boolean nullChecks) {
        this.nullChecksAndLogging = nullChecks;
        return this;
    }

    @Override
    public RepoBuilder addLogging(boolean logging) {
        this.nullChecksAndLogging = logging;
        return this;
    }

    @Override
    public RepoBuilder cloneEdits(boolean cloneEdits) {
        this.cloneEdits = cloneEdits;
        return this;
    }

    @Override
    public RepoBuilder useCache() {
        this.cache = true;
        return this;
    }

    @Override
    public RepoBuilder storeKeyInIndexOnly() {
        this.storeKeyInIndexOnly = true;

        return this;
    }

    boolean events = false;
    ModificationListener[] listeners;

    @Override
    public RepoBuilder events(ModificationListener... listeners) {
        events = true;
        this.listeners = listeners;
        return this;
    }

    @Override
    public RepoBuilder debug() {
        this.debug = true;
        return this;
    }

    @Override
    public RepoBuilder searchIndexFactory(Function<Class, SearchIndex> factory) {
        this.searchIndexFactory = factory;
        return this;
    }

    @Override
    public RepoBuilder uniqueLookupIndexFactory(Function<Class, LookupIndex> factory) {
        this.uniqueLookupIndexFactory = factory;
        return this;
    }

    @Override
    public RepoBuilder uniqueSearchIndexFactory(Function<Class, SearchIndex> factory) {
        this.uniqueSearchIndexFactory = factory;
        return this;
    }

    @Override
    public RepoBuilder lookupIndexFactory(Function<Class, LookupIndex> factory) {
        this.lookupIndexFactory = factory;
        return this;
    }

    @Override
    public RepoBuilder repoFactory(Supplier<RepoComposer> factory) {
        this.repoComposerFactory = factory;
        return this;
    }

    @Override
    public RepoBuilder filterFactory(Supplier<Filter> factory) {
        this.filterFactory = factory;
        return this;
    }

    @Override
    public RepoBuilder primaryKey(String propertyName) {
        this.primaryKey = propertyName;
        return this;
    }

    @Override
    public RepoBuilder lookupIndex(String propertyName) {
        this.lookupIndexes.add(propertyName);
        return this;
    }

    @Override
    public RepoBuilder uniqueLookupIndex(String propertyName) {
        return this.lookupIndex(propertyName, true);
    }

    public RepoBuilder lookupIndex(String propertyName, boolean unique) {
        if (unique) {
            this.lookupIndexes.add(propertyName);
        } else {
            this.uniqueLookupIndexes.add(propertyName);
        }
        return this;
    }

    @Override
    public RepoBuilder searchIndex(String propertyName) {
        this.searchIndexes.add(propertyName);
        return this;
    }

    @Override
    public RepoBuilder uniqueSearchIndex(String propertyName) {
        return searchIndex(propertyName, true);
    }


    @Override
    public RepoBuilder collateIndex(String propertyName) {
        collators.put(propertyName, Collator.getInstance());
        return this;
    }

    @Override
    public RepoBuilder collateIndex(String propertyName, Locale locale) {
        collators.put(propertyName, Collator.getInstance(locale));
        return this;
    }

    @Override
    public RepoBuilder collateIndex(String propertyName, Comparator collator) {
        collators.put(propertyName, collator);
        return this;
    }

    public RepoBuilder searchIndex(String propertyName, boolean unique) {
        if (unique) {
            this.searchIndexes.add(propertyName);
        } else {
            this.uniqueSearchIndexes.add(propertyName);
        }
        return this;
    }

    @Override
    public RepoBuilder keyGetter(String propertyName, Function<?, ?> keyGetter) {
        keyGetterMap.put(propertyName, keyGetter);
        return this;
    }

    private void init() {

        if (this.repoComposerFactory == null) {
            this.repoComposerFactory = SPIFactory.getRepoFactory();
        }
        if (this.lookupIndexFactory == null) {
            this.lookupIndexFactory = SPIFactory.getLookupIndexFactory();
        }
        if (this.searchIndexFactory == null) {
            this.searchIndexFactory = SPIFactory.getSearchIndexFactory();
        }
        if (this.uniqueLookupIndexFactory == null) {
            this.uniqueLookupIndexFactory = SPIFactory.getUniqueLookupIndexFactory();
        }
        if (this.searchableCollectionFactory == null) {
            this.searchableCollectionFactory = SPIFactory.getSearchableCollectionFactory();
        }
        if (this.filterFactory == null) {
            this.filterFactory = SPIFactory.getFilterFactory();
        }

        if (this.objectEditorFactory == null) {
            this.objectEditorFactory = SPIFactory.getObjectEditorFactory();
        }

    }

    @Override
    public <KEY, ITEM> Repo<KEY, ITEM> build(Class<KEY> key, Class<ITEM> clazz, Class<?>... classes) {
        return build(null, key, clazz, classes);
    }

    public <KEY, ITEM> Repo<KEY, ITEM> build(Class<?> primitiveKey, Class<KEY> key, Class<ITEM> clazz, Class<?>... classes) {
        init();

        this.fields = Reflection.getPropertyFieldAccessMap(clazz, useField, useUnSafe);

        for (Class<?> cls : classes) {
            Map<String, FieldAccess> fieldsSubType
                    = Reflection.getPropertyFieldAccessMap(cls, useField, useUnSafe);

            for (String sKey : fieldsSubType.keySet()) {
                if (!fields.containsKey(sKey)) {
                    fields.put(sKey, fieldsSubType.get(sKey));
                }
            }
        }

        /* Construct */
        this.repo = (RepoComposer) this.repoComposerFactory.get();
        this.editor = constructObjectEditor(fields);
        SearchableCollectionComposer query = constructSearchableCollection(primitiveKey, clazz, repo, fields);
        query.setRemoveDuplication(this.removeDuplication);

        /* Inject */
        repo.setSearchableCollection((SearchableCollection<KEY, ITEM>) query);
        ((ObjectEditorComposer) editor).setSearchableCollection((SearchableCollection<KEY, ITEM>) query);

        editor = decorateEditor(editor);
        repo.setObjectEditor((ObjectEditor) editor);

        return (Repo<KEY, ITEM>) repo;
    }

    private SearchableCollectionComposer constructSearchableCollection(Class<?> primitiveKey, Class<?> itemClazz, RepoComposer repo, Map<String, FieldAccess> fields) {

        query = searchableCollectionFactory.get();

        Filter filter = this.filterFactory.get();


        configPrimaryKey(primitiveKey == null ? itemClazz : primitiveKey, fields);
        configIndexes(repo, fields);


        query.setFilter(filter);


        query.setFields(fields);

        query.init();

        if (this.cache) {
            filter = new FilterWithSimpleCache(filter);
        }

        query.setFilter(filter);

        return query;
    }

    private ObjectEditor constructObjectEditor(Map<String, FieldAccess> fields) {
        ObjectEditorComposer editorComposer = this.objectEditorFactory.get();
        if (this.hashCodeOptimizationOn) {
            editorComposer.hashCodeOptimizationOn();
        }

        ObjectEditor editor = (ObjectEditor) editorComposer;
        editorComposer.init();

        if (this.cloneEdits) {
            editorComposer.setLookupAndExcept(true);
        }

        editorComposer.setFields(fields);
        return editor;
    }


    private ObjectEditor decorateEditor(ObjectEditor editor) {
        if (debug || nullChecksAndLogging) {
            ObjectEditorLogNullCheckDecorator logNullCheckDecorator = new ObjectEditorLogNullCheckDecorator(editor);
            logNullCheckDecorator.setLevel(level);
            logNullCheckDecorator.setDebug(debug);

            editor = logNullCheckDecorator;
        }

        if (cloneEdits) {
            editor = new ObjectEditorCloneDecorator(editor);
        }

        if (events) {
            ObjectEditorEventDecorator eventManager = new ObjectEditorEventDecorator(editor);
            for (ModificationListener l : listeners) {
                eventManager.add(l);
            }
            editor = eventManager;
        }
        return editor;
    }

    @Override
    public RepoBuilder level(Level level) {
        this.level = level;
        return this;
    }

    @Override
    public RepoBuilder upperCaseIndex(String property) {
        this.keyTransformers.put(property, PropertyNameUtils.upperCase);
        return this;
    }

    @Override
    public RepoBuilder lowerCaseIndex(String property) {
        this.keyTransformers.put(property, PropertyNameUtils.lowerCase);
        return this;

    }

    @Override
    public RepoBuilder camelCaseIndex(String property) {
        this.keyTransformers.put(property, PropertyNameUtils.camelCase);
        return this;

    }

    @Override
    public RepoBuilder underBarCaseIndex(String property) {
        this.keyTransformers.put(property, PropertyNameUtils.underBarCase);
        return this;
    }

    @Override
    public RepoBuilder nestedIndex(String... propertyPath) {

        this.nestedIndexes.put(Str.join('.', propertyPath), propertyPath);

        return this;

    }

    @Override
    public RepoBuilder indexHierarchy() {
        this.indexHierarchy = true;
        return this;
    }

    @Override
    public RepoBuilder indexBucketSize(String propertyName, int size) {
        this.indexBucketSize.put(propertyName, size);
        return this;
    }

    @Override
    public RepoBuilder hashCodeOptimizationOn() {
        this.hashCodeOptimizationOn = true;
        return this;
    }

    @Override
    public RepoBuilder removeDuplication(boolean removeDuplication) {
        this.removeDuplication = removeDuplication;
        return this;
    }

    private Function createKeyGetter(final FieldAccess field) {
        Objects.requireNonNull(field, "field cannot be null");

        return new Function() {
            @Override
            public Object apply(Object o) {
                return field.getValue(o);
            }
        };
    }

    private void configIndexes(RepoComposer repo,
                               Map<String, FieldAccess> fields) {

        if (this.indexHierarchy) {
            TypeHierarchyIndex index = new TypeHierarchyIndex();
            index.setComparator(this.collators.get("_type"));
            index.setInputKeyTransformer(this.keyTransformers.get("_type"));
            index.init();
            ((SearchableCollection) query).addSearchIndex("_type", index);
        }

        for (String prop : nestedIndexes.keySet()) {
            NestedKeySearchIndex index = new NestedKeySearchIndex(this.nestedIndexes.get(prop));
            configIndex(prop, index);
        }
        for (String prop : searchIndexes) {
            try {

            FieldAccess fieldAccess = fields.get(prop);
            Class<?> type = fieldAccess.getType();

            SearchIndex searchIndex = this.searchIndexFactory.apply(type);
            configSearchIndex(fields, prop, searchIndex);

            } catch (Exception ex) {
                throw new RuntimeException("Unable to load property " + prop);
            }
        }
        for (String prop : uniqueSearchIndexes) {
            SearchIndex searchIndex = this.uniqueSearchIndexFactory.apply(fields.get(prop).getType());
            configSearchIndex(fields, prop, searchIndex);
        }

        for (String prop : lookupIndexes) {
            LookupIndex index = this.lookupIndexFactory.apply(fields.get(prop).getType());
            configLookupIndex(fields, prop, index);
        }
        for (String prop : uniqueLookupIndexes) {
            LookupIndex index = this.uniqueLookupIndexFactory.apply(fields.get(prop).getType());
            configLookupIndex(fields, prop, index);
        }

    }

    private void configLookupIndex(Map<String, FieldAccess> fields, String prop, LookupIndex index) {
        Function kg = getKeyGetterOrCreate(fields, prop);
        index.setInputKeyTransformer(this.keyTransformers.get(prop));
        index.setKeyGetter(kg);
        index.setBucketSize(this.indexBucketSize.get(prop) == null ? 3 : this.indexBucketSize.get(prop));

        index.init();
        ((SearchableCollection) query).addLookupIndex(prop, index);
    }

    private void configSearchIndex(Map<String, FieldAccess> fields, String prop, SearchIndex searchIndex) {
        searchIndex.setComparator(this.collators.get(prop));
        searchIndex.setInputKeyTransformer(this.keyTransformers.get(prop));
        Function kg = getKeyGetterOrCreate(fields, prop);
        searchIndex.setKeyGetter(kg);
        searchIndex.setBucketSize(this.indexBucketSize.get(prop) == null ? 3 : this.indexBucketSize.get(prop));
        searchIndex.init();
        ((SearchableCollection) query).addSearchIndex(prop, searchIndex);
    }

    private void configIndex(String prop, NestedKeySearchIndex index) {
        index.setComparator(this.collators.get(prop));
        index.setInputKeyTransformer(this.keyTransformers.get(prop));
        index.setBucketSize(this.indexBucketSize.get(prop) == null ? 3 : this.indexBucketSize.get(prop));
        index.init();
        ((SearchableCollection) query).addSearchIndex(prop, index);
    }

    private Function getKeyGetterOrCreate(Map<String, FieldAccess> fields, String prop) {
        Objects.requireNonNull(fields, "field cannot be null");
        Objects.requireNonNull(prop, "prop cannot be null");

        Function kg = null;

        kg = this.keyGetterMap.get(prop);

        if (kg == null) {
            FieldAccess field = fields.get(prop);
            kg = createKeyGetter(field);

            keyGetterMap.put(prop, kg);
        }
        return kg;

    }

    private void configPrimaryKey(Class<?> type, Map<String, FieldAccess> fields) {

        Objects.requireNonNull(primaryKey, "primary key cannot be null");

        LookupIndex primaryKeyIndex = this.uniqueLookupIndexFactory.apply(type);



        if (!fields.containsKey(primaryKey)) {
            throw new IllegalStateException(
                    String.format( "Fields does not have primary key %s",
                            primaryKey));
        }


        primaryKeyIndex.setKeyGetter(getKeyGetterOrCreate(fields, this.primaryKey));
        ((SearchableCollectionComposer) query).setPrimaryKeyName(this.primaryKey);
        ((SearchableCollectionComposer) query).setPrimaryKeyGetter(this.keyGetterMap.get(this.primaryKey));
        ((SearchableCollection) query).addLookupIndex(this.primaryKey, primaryKeyIndex);


    }


}
