package org.boon.json.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

import java.util.Date;

/**
 * Created by rick on 1/1/14.
 */
public interface DateSerializer {

      void serializeDate (JsonSerializerInternal jsonSerializer, Date date, CharBuf builder );

}
