package org.boon.json.implementation.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

import java.util.Collection;

/**
 * Created by rick on 1/1/14.
 */
public interface CollectionSerializer {
    public  void serializeCollection(JsonSerializer serializer, Collection<?> collection, CharBuf builder );

}
