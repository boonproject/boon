package org.boon.datarepo.spi;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.datarepo.SearchableCollection;

import java.util.Map;

public interface ObjectEditorComposer<KEY, ITEM> {
    void setFields ( Map<String, FieldAccess> fields );

    void setSearchableCollection ( SearchableCollection<KEY, ITEM> searchableCollection );

<<<<<<< HEAD
    void init ();

    void hashCodeOptimizationOn ();
=======
    void init();

    void hashCodeOptimizationOn();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    public void setLookupAndExcept ( boolean lookupAndExcept );

}
