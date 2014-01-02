package org.boon.json.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

import java.util.Collection;

/**
 * Created by rick on 1/1/14.
 */
public interface CollectionSerializer {
     void serializeCollection(JsonSerializerInternal serializer, Collection<?> collection, CharBuf builder );

}
