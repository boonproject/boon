package org.boon.datarepo.spi;


import org.boon.core.reflection.fields.FieldAccess;
import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.SearchableCollection;

import java.util.Map;

public interface FilterComposer {

    public void setSearchableCollection ( SearchableCollection searchableCollection );

    public void setFields ( Map<String, FieldAccess> fields );

    public void setSearchIndexMap ( Map<String, SearchIndex> searchIndexMap );

    public void setLookupIndexMap ( Map<String, LookupIndex> lookupIndexMap );

<<<<<<< HEAD
    public void init ();
=======
    public void init();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
}
