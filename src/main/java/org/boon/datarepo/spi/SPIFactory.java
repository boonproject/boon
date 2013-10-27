package org.boon.datarepo.spi;

import org.boon.core.Typ;
import org.boon.datarepo.Filter;
import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.RepoBuilder;
import org.boon.datarepo.impl.*;
import org.boon.datarepo.impl.indexes.LookupIndexDefault;
import org.boon.datarepo.impl.indexes.SearchIndexDefault;
import org.boon.datarepo.impl.indexes.UniqueLookupIndex;
import org.boon.datarepo.impl.indexes.UniqueSearchIndex;
import org.boon.datarepo.impl.maps.MapCreatorImpl;
import org.boon.predicates.Function;
import org.boon.predicates.Supplier;


/**
 * Helper class for SPIFactory interface.
 */
public class SPIFactory {

    static Supplier<MapCreator> mapCreatorFactory = null;
    static Supplier<RepoBuilder> repoBuilderFactory = null;
    static Function<Class, SearchIndex> searchIndexFactory = null;
    static Function<Class, LookupIndex> uniqueLookupIndexFactory = null;
    static Function<Class, SearchIndex> uniqueSearchIndexFactory = null;
    static Function<Class, LookupIndex> lookupIndexFactory = null;
    static Supplier<RepoComposer> repoFactory = null;
    static Supplier<Filter> filterFactory = null;
    static Supplier<SearchableCollectionComposer> searchableCollectionFactory = null;
    static Supplier<ObjectEditorComposer> objectEditorFactory;

    public static Supplier<MapCreator> getMapCreatorFactory() {
        return mapCreatorFactory;
    }

    public static Supplier<SearchableCollectionComposer> getSearchableCollectionFactory() {
        return searchableCollectionFactory;
    }

    public static Supplier<RepoBuilder> getRepoBuilderFactory() {
        return repoBuilderFactory;
    }

    public static Function<Class, SearchIndex> getSearchIndexFactory() {
        return searchIndexFactory;
    }

    public static Function<Class, SearchIndex> getUniqueSearchIndexFactory() {
        return uniqueSearchIndexFactory;
    }

    public static Function<Class, LookupIndex> getLookupIndexFactory() {
        return lookupIndexFactory;
    }

    public static Function<Class, LookupIndex> getUniqueLookupIndexFactory() {
        return uniqueLookupIndexFactory;
    }

    public static Supplier<RepoComposer> getRepoFactory() {
        return repoFactory;
    }

    public static Supplier<Filter> getFilterFactory() {
        return filterFactory;
    }

    public static void init() {

        if (mapCreatorFactory == null) {
            mapCreatorFactory = new Supplier<MapCreator>() {
                @Override
                public MapCreator get() {
                    return new MapCreatorImpl();
                }
            };
        }
        if (repoBuilderFactory == null) {
            repoBuilderFactory = new Supplier<RepoBuilder>() {
                @Override
                public RepoBuilder get() {
                    return new RepoBuilderDefault();
                }
            };
        }
        if (searchIndexFactory == null) {
            searchIndexFactory = new Function<Class, SearchIndex>() {
                public SearchIndex apply(Class keyType) {
                    if (keyType == Typ.string) {
                        return new SearchIndexDefault(keyType);
                    } else {
                        return new SearchIndexDefault(keyType);
                    }
                }
            };
        }
        if (lookupIndexFactory == null) {
            lookupIndexFactory = new Function<Class, LookupIndex>() {
                @Override
                public LookupIndex apply(Class keyType) {
                    return new LookupIndexDefault(keyType);
                }
            };
        }
        if (uniqueLookupIndexFactory == null) {
            uniqueLookupIndexFactory = new Function<Class, LookupIndex>() {
                @Override
                public LookupIndex apply(Class keyType) {
                    return new UniqueLookupIndex(keyType);
                }
            };
        }
        if (uniqueSearchIndexFactory == null) {
            uniqueSearchIndexFactory = new Function<Class, SearchIndex>() {
                @Override
                public SearchIndex apply(Class keyType) {
                    return new UniqueSearchIndex(keyType);
                }
            };
        }

        if (repoFactory == null) {
            repoFactory = new Supplier<RepoComposer>() {
                @Override
                public RepoComposer get() {
                    return new RepoDefault<>();
                }
            };
        }

        if (filterFactory == null) {
            filterFactory = new Supplier<Filter>() {
                @Override
                public Filter get() {
                    return new FilterDefault();
                }
            };
        }

        if (searchableCollectionFactory == null) {
            searchableCollectionFactory = new Supplier<SearchableCollectionComposer>() {
                @Override
                public SearchableCollectionComposer get() {
                    return new SearchableCollectionDefault();
                }
            };
        }

        if (objectEditorFactory == null) {
            objectEditorFactory = new Supplier<ObjectEditorComposer>() {
                @Override
                public ObjectEditorComposer get() {
                    return new ObjectEditorDefault();
                }
            };
        }

    }

    static {
        init();
    }


    public static void setMapCreatorFactory(Supplier<MapCreator> mapCreatorFactory) {
        SPIFactory.mapCreatorFactory = mapCreatorFactory;
    }

    public static void setRepoBuilderFactory(Supplier<RepoBuilder> repoBuilderFactory) {
        SPIFactory.repoBuilderFactory = repoBuilderFactory;
    }

    public static void setSearchIndexFactory(Function<Class, SearchIndex> searchIndexFactory) {
        SPIFactory.searchIndexFactory = searchIndexFactory;
    }

    public static void setLookupIndexFactory(Function<Class, LookupIndex> lookupIndexFactory) {
        SPIFactory.lookupIndexFactory = lookupIndexFactory;
    }


    public static void setUniqueLookupIndexFactory(Function<Class, LookupIndex> lookupIndexFactory) {
        SPIFactory.uniqueLookupIndexFactory = lookupIndexFactory;
    }

    public static void setUniqueSearchIndexFactory(Function<Class, SearchIndex> factory) {
        SPIFactory.uniqueSearchIndexFactory = factory;
    }

    public static void setRepoFactory(Supplier<RepoComposer> repoFactory) {
        SPIFactory.repoFactory = repoFactory;
    }

    public static void setFilterFactory(Supplier<Filter> filterFactory) {
        SPIFactory.filterFactory = filterFactory;
    }

    public static Supplier<ObjectEditorComposer> getObjectEditorFactory() {
        return objectEditorFactory;
    }
}
