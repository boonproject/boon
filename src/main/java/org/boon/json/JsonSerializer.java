package org.boon.json;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by rick on 12/26/13.
 */
public interface JsonSerializer {

    CharBuf serialize( Object obj );
    void serializeDate ( Date date, CharBuf builder );


    void serializeString ( String obj, CharBuf builder );

    void serializeCollection ( Collection<?> collection, CharBuf builder );

    void serializeMap( Map<String, Object> map, CharBuf builder );

    void serializeArray ( Object array, CharBuf builder );

    void serializeInstance ( Object obj, CharBuf builder );

    void serializeUnknown ( Object obj, CharBuf builder );

    void serializeObject ( Object value, CharBuf builder );

    Map<String, FieldAccess> getFields ( Class<? extends Object> aClass );

    boolean serializeField ( Object instance, FieldAccess fieldAccess, CharBuf builder );
}
