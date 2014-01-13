package org.boon.json;

import org.boon.core.reflection.fields.*;
import org.boon.json.implementation.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonParserFactory {


    private Charset charset = null;
    private boolean lax;
    private boolean chop = false;
    private boolean lazyChop = true;
    private FieldAccessMode fieldAccessType = FieldAccessMode.FIELD;
    private boolean useAnnotations;


    public FieldAccessMode getFieldAccessType() {
        return fieldAccessType;
    }


    public boolean isChop() {
        return chop;
    }

    public JsonParserFactory setChop( boolean chop ) {
        this.chop = chop;
        return this;
    }

    public boolean isLazyChop() {
        return lazyChop;
    }

    public JsonParserFactory setLazyChop( boolean lazyChop ) {
        this.lazyChop = lazyChop;
        return this;
    }

    public JsonParserFactory lax() {
        lax = true;
        return this;
    }

    public JsonParserFactory strict() {
        lax = false;
        return this;
    }


    public JsonParserFactory setCharset( Charset charset ) {
        this.charset = charset;
        return this;
    }







    public JsonParser createFastParser() {
        BaseJsonParser jsonParser = new JsonFastParser(  FieldAccessMode.create( fieldAccessType, useAnnotations ), false, true );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }




    public JsonParser createUTF8DirectByteParser() {
        BaseJsonParser jsonParser = new JsonUTF8Parser( FieldAccessMode.create( fieldAccessType, useAnnotations ) );
        jsonParser.setCharset ( StandardCharsets.UTF_8 );
        return (JsonParser)jsonParser;

    }

    public JsonParser createASCIIParser() {
        BaseJsonParser jsonParser = new JsonAsciiParser ( FieldAccessMode.create( fieldAccessType, useAnnotations ) );
        jsonParser.setCharset ( StandardCharsets.US_ASCII );
        return (JsonParser)jsonParser;

    }


    public JsonParser createLaxParser() {
        BaseJsonParser jsonParser = new JsonParserLax ( FieldAccessMode.create( fieldAccessType, useAnnotations ),
                false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser createPlistParser() {

        if (charset==null) {
           charset= StandardCharsets.US_ASCII;
        }
        BaseJsonParser jsonParser = new PlistParser ( FieldAccessMode.create( fieldAccessType, useAnnotations ), false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser createLazyFinalParser() {
        BaseJsonParser jsonParser = new JsonFastParser ( FieldAccessMode.create( fieldAccessType, useAnnotations ),  false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;

    }

    public JsonParser createJsonParserForJsonPath() {

        BaseJsonParser jsonParser = new JsonFastParser (  FieldAccessMode.create( fieldAccessType, useAnnotations ), false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser create() {



        if ( charset == null ) {
            charset = StandardCharsets.UTF_8;
        }

        return new JsonParserImpl( FieldAccessMode.create( fieldAccessType, useAnnotations ), charset,
                 lax,  chop, lazyChop );
    }


    public boolean isUsePropertiesFirst () {
        return fieldAccessType == FieldAccessMode.PROPERTY_THEN_FIELD;
    }


    public JsonParserFactory usePropertiesFirst () {
        fieldAccessType = FieldAccessMode.PROPERTY_THEN_FIELD;
        return this;
    }

    public boolean isUseFieldsFirst () {
        return this.fieldAccessType == FieldAccessMode.FIELD_THEN_PROPERTY;

    }


    public JsonParserFactory useFieldsFirst () {
        this.fieldAccessType  = FieldAccessMode.FIELD_THEN_PROPERTY;
        return this;
    }


    public JsonParserFactory useFieldsOnly () {
        this.fieldAccessType  = FieldAccessMode.FIELD;
        return this;
    }



    public JsonParserFactory usePropertyOnly () {
        this.fieldAccessType  = FieldAccessMode.PROPERTY;
        return this;
    }



    public JsonParserFactory useAnnotations () {
        this.useAnnotations  = true;
        return this;
    }

    public boolean isUseAnnotations() {
        return useAnnotations;
    }

    public JsonParserFactory setUseAnnotations( boolean useAnnotations ) {
        this.useAnnotations = useAnnotations;
        return this;

    }
}
