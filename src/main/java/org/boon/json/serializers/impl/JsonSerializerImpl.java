package org.boon.json.serializers.impl;

import org.boon.Exceptions;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.serializers.*;
import org.boon.primitive.CharBuf;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by rick on 1/1/14.
 */
public class JsonSerializerImpl implements JsonSerializerInternal {

    private final ObjectSerializer objectSerializer;
    private final StringSerializer stringSerializer;
    private final MapSerializer mapSerializer;
    private final FieldSerializer fieldSerializer;

    private final InstanceSerializer instanceSerializer;
    private final CollectionSerializer collectionSerializer;
    private final ArraySerializer arraySerializer;
    private final UnknownSerializer unknownSerializer;
    private final DateSerializer dateSerializer;
    private final FieldsAccessor fieldsAccessor;


    private CharBuf builder = CharBuf.create( 4000 );

    public JsonSerializerImpl ()  {

        instanceSerializer = new InstanceSerializerImpl ();
        objectSerializer = new BasicObjectSerializerImpl ();
        stringSerializer = new StringSerializerImpl ();
        mapSerializer = new MapSerializerImpl ();
        fieldSerializer = new FieldSerializerImpl ();
        collectionSerializer = new CollectionSerializerImpl ();
        arraySerializer = (ArraySerializer) collectionSerializer;
        unknownSerializer = new UnknownSerializerImpl ();
        dateSerializer = new DateSerializerImpl ();
        fieldsAccessor = new FieldsAccessorFieldThenProp ();

    }

    public JsonSerializerImpl ( final ObjectSerializer objectSerializer,
                                final StringSerializer stringSerializer,
                                final MapSerializer mapSerializer,
                                final FieldSerializer fieldSerializer,
                                final InstanceSerializer instanceSerializer,
                                final CollectionSerializer collectionSerializer,
                                final ArraySerializer arraySerializer,
                                final UnknownSerializer unknownSerializer,
                                final DateSerializer dateSerializer,
                                final FieldsAccessor fieldsAccessor

    ) {




        if (fieldsAccessor == null) {
            this.fieldsAccessor = new FieldsAccessorFieldThenProp ();
        } else {
            this.fieldsAccessor = fieldsAccessor;
        }


        if (dateSerializer == null) {
            this.dateSerializer = new DateSerializerImpl ();
        } else {
            this.dateSerializer = dateSerializer;
        }

        if (unknownSerializer == null) {
            this.unknownSerializer = new UnknownSerializerImpl();
        } else {
            this.unknownSerializer = unknownSerializer;
        }


        if (arraySerializer == null) {
            this.arraySerializer = new CollectionSerializerImpl ();
        } else {
            this.arraySerializer = arraySerializer;
        }

        if (collectionSerializer == null) {
            this.collectionSerializer = new CollectionSerializerImpl ();
        } else {
            this.collectionSerializer = collectionSerializer;
        }


        if (instanceSerializer == null) {
            this.instanceSerializer = new InstanceSerializerImpl ();
        } else {
            this.instanceSerializer = instanceSerializer;
        }

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



    public final void serializeUnknown ( Object obj, CharBuf builder ) {
        this.unknownSerializer.serializeUnknown ( this, obj, builder );
    }

    public final void serializeDate ( Date date, CharBuf builder ) {

        this.dateSerializer.serializeDate ( this, date, builder );

    }





    public final void serializeInstance ( Object obj, CharBuf builder )  {
           this.instanceSerializer.serializeInstance ( this, obj, builder );

    }

    public final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        return fieldsAccessor.getFields ( aClass );
    }


}

