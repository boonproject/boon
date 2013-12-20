package org.boon.datarepo.spi;

import org.boon.datarepo.ObjectEditor;
import org.boon.datarepo.SearchableCollection;

/**
 * Used by RepoBuilder to add indexes to Repo.
 */
public interface RepoComposer<KEY, ITEM> {


    void setSearchableCollection ( SearchableCollection<KEY, ITEM> searchableCollection );

<<<<<<< HEAD
    void init ();
=======
    void init();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    void setObjectEditor ( ObjectEditor editor );
}
