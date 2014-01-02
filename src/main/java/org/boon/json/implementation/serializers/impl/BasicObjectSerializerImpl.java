package org.boon.json.implementation.serializers.impl;

import org.boon.core.Type;
import org.boon.json.JsonSerializer;
import org.boon.json.implementation.serializers.ObjectSerializer;
import org.boon.primitive.CharBuf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by rick on 1/1/14.
 */
public class BasicObjectSerializerImpl implements ObjectSerializer {
    @Override
    public final void serializeObject (JsonSerializer jsonSerializer, Object obj, CharBuf builder )  {

        Type type = Type.getInstanceType (obj);

        switch ( type ) {

            case NULL:
                return;
            case INT:
                builder.addInt ( int.class.cast ( obj ) );
                return;
            case BOOLEAN:
                builder.addBoolean ( boolean.class.cast ( obj ) );
                return;
            case BYTE:
                builder.addByte ( byte.class.cast ( obj ) );
                return;
            case LONG:
                builder.addLong ( long.class.cast ( obj ) );
                return;
            case DOUBLE:
                builder.addDouble ( double.class.cast ( obj ) );
                return;
            case FLOAT:
                builder.addFloat ( float.class.cast ( obj ) );
                return;
            case SHORT:
                builder.addShort ( short.class.cast ( obj ) );
                return;
            case CHAR:
                builder.addChar ( char.class.cast ( obj ) );
                return;
            case BIG_DECIMAL:
                builder.addBigDecimal ( ( BigDecimal ) obj );
                return;
            case BIG_INT:
                builder.addBigInteger ( ( BigInteger ) obj );
                return;
            case DATE:
                jsonSerializer.serializeDate ( ( Date ) obj, builder );
                return;
            case STRING:
                jsonSerializer.serializeString ( ( String ) obj, builder );
                return;
            case CHAR_SEQUENCE:
                jsonSerializer.serializeString ( obj.toString (), builder );
                return;
            case BOOLEAN_WRAPPER:
                builder.addBoolean ( ( Boolean ) obj );
                return;
            case INTEGER_WRAPPER:
                builder.addInt ( (Integer) obj);
                return;
            case LONG_WRAPPER:
                builder.addLong ( (Long) obj);
                return;
            case FLOAT_WRAPPER:
                builder.addFloat ( (Float) obj);
                return;
            case DOUBLE_WRAPPER:
                builder.addDouble ( (Double) obj);
                return;
            case SHORT_WRAPPER:
                builder.addShort ( (Short) obj);
                return;
            case BYTE_WRAPPER:
                builder.addByte ( (Byte) obj);
                return;
            case CHAR_WRAPPER:
                builder.addChar ( (Character) obj);
                return;
            case ENUM:
                builder.addQuoted ( obj.toString () );
                return;

            case COLLECTION:
            case LIST:
            case SET:
                jsonSerializer.serializeCollection ( ( Collection ) obj, builder );
                return;
            case MAP:
                jsonSerializer.serializeMap ( ( Map ) obj, builder );
                return;
            case ARRAY:
                jsonSerializer.serializeArray ( obj, builder );
                return;
            case INSTANCE:
                jsonSerializer.serializeInstance ( obj, builder );
                return;
            default:
                jsonSerializer.serializeUnknown ( obj, builder );

        }


    }
}
