package org.boon.json.implementation;

import org.boon.Exceptions;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.JsonSerializer;
import org.boon.json.implementation.serializers.*;
import org.boon.json.implementation.serializers.impl.*;
import org.boon.primitive.CharBuf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rick on 1/1/14.
 */
public class JsonSerializerImpl implements JsonSerializer {

    private final InstanceSerializer instanceSerializer = new InstanceSerializerImpl ();
    private final ObjectSerializer objectSerializer;
    private final StringSerializer stringSerializer;
    private final MapSerializer mapSerializer;
    private final FieldSerializer fieldSerializer;
    private final CollectionSerializer collectionSerializer = new CollectionSerializerImpl ();
    private final ArraySerializer arraySerializer = (ArraySerializer) collectionSerializer;

    private final Map <Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ( );


    private CharBuf builder = CharBuf.create( 4000 );

    public JsonSerializerImpl ()  {


        objectSerializer = new BasicObjectSerializerImpl ();
        stringSerializer = new StringSerializerImpl ();
        mapSerializer = new MapSerializerImpl ();
        fieldSerializer = new FieldSerializerImpl ();

    }

    public JsonSerializerImpl ( final ObjectSerializer objectSerializer,
                                final StringSerializer stringSerializer,
                                final MapSerializer mapSerializer,
                                final FieldSerializer fieldSerializer) {

        if (objectSerializer == null) {
            this.objectSerializer = new BasicObjectSerializerImpl ();
        } else {
            this.objectSerializer = objectSerializer;
        }

        if (stringSerializer == null) {
            this.stringSerializer = new StringSerializerImpl ();
        } else {
            this.stringSerializer = stringSerializer;
        }

        if (mapSerializer == null) {
            this.mapSerializer = new MapSerializerImpl();
        } else {
            this.mapSerializer = mapSerializer;
        }

        if (fieldSerializer == null) {
            this.fieldSerializer = new FieldSerializerImpl();
        } else {
            this.fieldSerializer = fieldSerializer;
        }

    }







    public final CharBuf serialize( Object obj ) {

        builder.readForRecycle ();
        try {
            serializeObject( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle ( CharBuf.class, "unable to serializeObject", ex );
        }
        return builder;
    }


    public final boolean serializeField ( Object parent, FieldAccess fieldAccess, CharBuf builder )  {

        return fieldSerializer.serializeField ( this, parent, fieldAccess, builder );
    }

    public  final void serializeObject( Object obj, CharBuf builder )  {

        objectSerializer.serializeObject ( this, obj, builder );

    }

    public final  void serializeString( String str, CharBuf builder ) {
        this.stringSerializer.serializeString ( this, str, builder );
    }


    public final void serializeMap( Map<String, Object> map, CharBuf builder )  {
        this.mapSerializer.serializeMap ( this, map, builder );

    }

    public final void serializeCollection( Collection<?> collection, CharBuf builder )  {

        this.collectionSerializer.serializeCollection ( this, collection, builder );
    }



    public final void serializeArray( Object obj, CharBuf builder ) {
        this.arraySerializer.serializeArray ( this, obj, builder );
    }



    public final void serializeDate ( Date date, CharBuf builder ) {
        builder.addLong(date.getTime ());
    }



    public void serializeUnknown ( Object obj, CharBuf builder ) {
        builder.addQuoted ( obj.toString () );
    }



    public final void serializeInstance ( Object obj, CharBuf builder )  {
           this.instanceSerializer.serializeInstance ( this, obj, builder );

    }

    public final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get( aClass );
        if (map == null) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
    }

    private final Map<String, FieldAccess> doGetFields ( Class<? extends Object> aClass ) {
        return Reflection.getPropertyFieldAccessors ( aClass );
    }




}

