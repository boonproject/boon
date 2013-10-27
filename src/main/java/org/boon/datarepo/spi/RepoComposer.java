package org.boon.datarepo.spi;

import org.boon.datarepo.ObjectEditor;
import org.boon.datarepo.SearchableCollection;

/**
 * Used by RepoBuilder to add indexes to Repo.
 */
public interface RepoComposer<KEY, ITEM> {


    void setSearchableCollection(SearchableCollection<KEY, ITEM> searchableCollection);

    void init();

    void setObjectEditor(ObjectEditor editor);
}
