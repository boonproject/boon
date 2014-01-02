package org.boon.json;

import org.boon.core.Function;
import org.boon.json.implementation.JsonSerializerImplOld;
import org.boon.json.implementation.JsonSimpleSerializerImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rick on 12/26/13.
 */
public class JsonSerializerFactory {

    private  boolean outputType = false;
    private  boolean useProperties = false;
    private  boolean useFields = true;
    private  boolean includeNulls = false;
    private  boolean useAnnotations = false;
    private  boolean includeEmpty = false;
    private  boolean jsonFormatForDates = false;
    private boolean  handleSimpleBackReference = true;
    private boolean  handleComplexBackReference = false;
    private boolean  includeDefault = false;
    private  boolean cacheInstances = true;

    private List<Function<FieldSerializationData, Boolean>> filterProperties = null;
    private List<Function<FieldSerializationData, Boolean>> customSerializers = null;
    private Map<Class<?>, Function<ObjectSerializationData, Boolean>> customObjectSerializers = null;



    public JsonSerializer create() {

        if (!outputType && !includeEmpty && !includeNulls && !useAnnotations &&
                !jsonFormatForDates && handleSimpleBackReference &&
                !handleComplexBackReference && !includeDefault && filterProperties == null
                && customSerializers == null && customObjectSerializers == null) {
            return new JsonSimpleSerializerImpl ();
        } else {
            return new JsonSerializerImplOld (outputType, useProperties, useFields,
                includeNulls, useAnnotations, includeEmpty,
                handleSimpleBackReference, handleComplexBackReference, jsonFormatForDates, includeDefault,
                cacheInstances,
                filterProperties, customSerializers, customObjectSerializers);
        }

     }



    public JsonSerializerFactory addFilter(Function<FieldSerializationData, Boolean> filter) {
           if ( filterProperties == null ) {
               filterProperties = new CopyOnWriteArrayList<> (  );
           }
           filterProperties.add ( filter );
           return this;
    }

    public JsonSerializerFactory addPropertySerializer(Function<FieldSerializationData, Boolean> serializer) {
        if ( customSerializers == null ) {
            customSerializers = new CopyOnWriteArrayList<> (  );
        }
        customSerializers.add ( serializer );
        return this;
    }

    public JsonSerializerFactory addTypeSerializer(Class<?> type, Function<ObjectSerializationData, Boolean> serializer) {

        if ( customObjectSerializers == null ) {
            customObjectSerializers = new ConcurrentHashMap<> (  );
        }
        customObjectSerializers.put(type, serializer);
        return this;
    }

    public boolean isOutputType () {
        return outputType;
    }

    public JsonSerializerFactory setOutputType ( boolean outputType ) {
        this.outputType = outputType;
        return this;
    }

    public boolean isUseProperties () {
        return useProperties;
    }

    public JsonSerializerFactory setUseProperties ( boolean useProperties ) {
        this.useProperties = useProperties;
        return this;
    }

    public JsonSerializerFactory useProperties (  ) {
        this.useProperties = true;
        return this;
    }

    public boolean isUseFields () {
        return useFields;

    }

    public JsonSerializerFactory setUseFields ( boolean useFields ) {
        this.useFields = useFields;
        return this;
    }


    public JsonSerializerFactory useFields (  ) {
        this.useFields = true;
        return this;
    }

    public boolean isIncludeNulls () {
        return includeNulls;
    }

    public JsonSerializerFactory setIncludeNulls ( boolean includeNulls ) {
        this.includeNulls = includeNulls;
        return this;
    }


    public JsonSerializerFactory includeNulls (  ) {
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


    public JsonSerializerFactory useAnnotations (  ) {
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


    public JsonSerializerFactory includeEmpty (  ) {
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


    public JsonSerializerFactory handleComplexBackReference ( ) {
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


    public JsonSerializerFactory useJsonFormatForDates (  ) {
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


    public JsonSerializerFactory includeDefaultValues (  ) {
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


    public JsonSerializerFactory usedCacheInstances ( ) {
        this.cacheInstances = true;
        return this;
    }

}
