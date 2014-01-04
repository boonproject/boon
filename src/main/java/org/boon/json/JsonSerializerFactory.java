package org.boon.json;

import org.boon.json.serializers.*;
import org.boon.json.serializers.impl.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rick on 12/26/13.
 */
public class JsonSerializerFactory {

    private boolean outputType = false;

    private SerializationAccess fieldAccessType = SerializationAccess.FIELD;
    private boolean includeNulls = false;
    private boolean useAnnotations = false;
    private boolean includeEmpty = false;
    private boolean jsonFormatForDates = false;
    private boolean handleSimpleBackReference = true;
    private boolean handleComplexBackReference = false;
    private boolean includeDefault = false;
    private boolean cacheInstances = true;

    private List<FieldFilter> filterProperties = null;
    private List<CustomFieldSerializer> customFieldSerializers = null;
    private Map<Class, CustomObjectSerializer> customObjectSerializers = null;


    public JsonSerializer create () {

        if ( !outputType && !includeEmpty && !includeNulls && !useAnnotations &&
                !jsonFormatForDates && handleSimpleBackReference &&
                !handleComplexBackReference && !includeDefault && filterProperties == null
                && customFieldSerializers == null && customObjectSerializers == null ) {
            return new JsonSimpleSerializerImpl ();
        } else {

            InstanceSerializer instanceSerializer;
            CollectionSerializer collectionSerializer;
            ArraySerializer arraySerializer;
            UnknownSerializer unknownSerializer;
            DateSerializer dateSerializer;
            FieldsAccessor fieldsAccessor;

            ObjectSerializer objectSerializer;
            StringSerializer stringSerializer;
            MapSerializer mapSerializer;
            FieldSerializer fieldSerializer;


            instanceSerializer = new InstanceSerializerImpl ();
            objectSerializer = new BasicObjectSerializerImpl ();
            stringSerializer = new StringSerializerImpl ();
            mapSerializer = new MapSerializerImpl ();

            if ( useAnnotations || includeNulls || includeEmpty || handleComplexBackReference || !includeDefault ) {
                fieldSerializer = new FieldSerializerUseAnnotationsImpl (
                        includeNulls,
                        includeDefault, useAnnotations,
                        includeEmpty, handleSimpleBackReference,
                        handleComplexBackReference,
                        customObjectSerializers,
                        filterProperties,
                        null,
                        customFieldSerializers );
            } else {
                fieldSerializer = new FieldSerializerImpl ();
            }
            collectionSerializer = new CollectionSerializerImpl ();
            arraySerializer = ( ArraySerializer ) collectionSerializer;
            unknownSerializer = new UnknownSerializerImpl ();
            dateSerializer = new DateSerializerImpl ();


            switch ( fieldAccessType )  {
                case FIELD:
                    fieldsAccessor = new FieldFieldsAccessor();
                    break;
                case PROPERTY:
                    fieldsAccessor = new PropertyFieldAccesstor();
                    break;
                case FIELD_THEN_PROPERTY:
                    fieldsAccessor = new FieldsAccessorFieldThenProp();
                    break;
                case PROPERTY_THEN_FIELD:
                    fieldsAccessor = new FieldsAccessorsPropertyThenField();
                    break;
                default:
                    fieldsAccessor = new FieldFieldsAccessor();

            }

            return new JsonSerializerImpl (
                    objectSerializer,
                    stringSerializer,
                    mapSerializer,
                    fieldSerializer,
                    instanceSerializer,
                    collectionSerializer,
                    arraySerializer,
                    unknownSerializer,
                    dateSerializer,
                    fieldsAccessor
            );
        }

    }


    public JsonSerializerFactory addFilter ( FieldFilter filter ) {
        if ( filterProperties == null ) {
            filterProperties = new CopyOnWriteArrayList<> ();
        }
        filterProperties.add ( filter );
        return this;
    }

    public JsonSerializerFactory addPropertySerializer ( CustomFieldSerializer serializer ) {
        if ( customFieldSerializers == null ) {
            customFieldSerializers = new CopyOnWriteArrayList<> ();
        }
        customFieldSerializers.add ( serializer );
        return this;
    }

    public JsonSerializerFactory addTypeSerializer ( Class<?> type, CustomObjectSerializer serializer ) {

        if ( customObjectSerializers == null ) {
            customObjectSerializers = new ConcurrentHashMap<> ();
        }
        customObjectSerializers.put ( type, serializer );
        return this;
    }

    public boolean isOutputType () {
        return outputType;
    }

    public JsonSerializerFactory setOutputType ( boolean outputType ) {
        this.outputType = outputType;
        return this;
    }

    public boolean isUsePropertiesFirst () {
        return fieldAccessType == SerializationAccess.PROPERTY_THEN_FIELD;
    }


    public JsonSerializerFactory usePropertiesFirst () {
        fieldAccessType = SerializationAccess.PROPERTY_THEN_FIELD;
        return this;
    }

    public boolean isUseFieldsFirst () {
        return this.fieldAccessType == SerializationAccess.FIELD_THEN_PROPERTY;

    }


    public JsonSerializerFactory useFieldsFirst () {
        this.fieldAccessType  = SerializationAccess.FIELD_THEN_PROPERTY;
        return this;
    }


    public JsonSerializerFactory useFieldsOnly () {
        this.fieldAccessType  = SerializationAccess.FIELD;
        return this;
    }



    public JsonSerializerFactory usePropertyOnly () {
        this.fieldAccessType  = SerializationAccess.PROPERTY;
        return this;
    }

    public boolean isIncludeNulls () {
        return includeNulls;
    }

    public JsonSerializerFactory setIncludeNulls ( boolean includeNulls ) {
        this.includeNulls = includeNulls;
        return this;
    }


    public JsonSerializerFactory includeNulls () {
        this.includeNulls = true;
        return this;
    }

    public boolean isUseAnnotations () {
        return useAnnotations;
    }

    public JsonSerializerFactory setUseAnnotations ( boolean useAnnotations ) {
        this.useAnnotations = useAnnotations;
        return this;
    }


    public JsonSerializerFactory useAnnotations () {
        this.useAnnotations = true;
        return this;
    }


    public boolean isIncludeEmpty () {
        return includeEmpty;
    }

    public JsonSerializerFactory setIncludeEmpty ( boolean includeEmpty ) {
        this.includeEmpty = includeEmpty;
        return this;
    }


    public JsonSerializerFactory includeEmpty () {
        this.includeEmpty = true;
        return this;
    }

    public boolean isHandleSimpleBackReference () {
        return handleSimpleBackReference;
    }

    public JsonSerializerFactory setHandleSimpleBackReference ( boolean handleSimpleBackReference ) {
        this.handleSimpleBackReference = handleSimpleBackReference;
        return this;
    }

    public boolean isHandleComplexBackReference () {
        return handleComplexBackReference;
    }

    public JsonSerializerFactory setHandleComplexBackReference ( boolean handleComplexBackReference ) {
        this.handleComplexBackReference = handleComplexBackReference;
        return this;
    }


    public JsonSerializerFactory handleComplexBackReference () {
        this.handleComplexBackReference = true;
        return this;
    }


    public boolean isJsonFormatForDates () {
        return jsonFormatForDates;
    }

    public JsonSerializerFactory setJsonFormatForDates ( boolean jsonFormatForDates ) {
        this.jsonFormatForDates = jsonFormatForDates;
        return this;
    }


    public JsonSerializerFactory useJsonFormatForDates () {
        this.jsonFormatForDates = true;
        return this;
    }


    public boolean isIncludeDefault () {
        return includeDefault;
    }

    public JsonSerializerFactory setIncludeDefault ( boolean includeDefault ) {
        this.includeDefault = includeDefault;
        return this;
    }


    public JsonSerializerFactory includeDefaultValues () {
        this.includeDefault = true;
        return this;
    }


    public boolean isCacheInstances () {
        return cacheInstances;
    }

    public JsonSerializerFactory setCacheInstances ( boolean cacheInstances ) {
        this.cacheInstances = cacheInstances;
        return this;
    }


    public JsonSerializerFactory usedCacheInstances () {
        this.cacheInstances = true;
        return this;
    }

}
