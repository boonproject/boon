package org.boon.datarepo.spi;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.datarepo.Filter;
import org.boon.core.Function;

import java.util.Map;

public interface SearchableCollectionComposer {
    void setPrimaryKeyName ( String primaryKey );


    void setPrimaryKeyGetter ( Function function );

    void init ();

    void setFields ( Map<String, FieldAccess> fields );

    void setFilter ( Filter filter );

    void setRemoveDuplication ( boolean b );
}
