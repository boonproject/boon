package org.boon.json;

import org.boon.json.implementation.JsonSerializerImpl;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 12/26/13.
 */
public class JsonSerializerFactory {

    private  boolean outputType;
    private  boolean useProperties;
    private  boolean useFields = true;
    private  boolean includeNulls;


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

    public JsonSerializer create() {
        return new JsonSerializerImpl (outputType, useProperties, useFields, includeNulls );
    }
}
