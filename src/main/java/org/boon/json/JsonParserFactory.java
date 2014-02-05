package org.boon.json;

import org.boon.core.reflection.fields.*;
import org.boon.json.implementation.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonParserFactory {


    private Charset charset = StandardCharsets.UTF_8;
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







    public JsonParserAndMapper createFastParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonFastParser (  false, chop, lazyChop ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));
        jsonParser.setCharset ( charset );
        return jsonParser;
    }



    public JsonParserAndMapper createFastObjectMapperParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonFastParser (  true ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));
        jsonParser.setCharset ( charset );
        return jsonParser;
    }




    public JsonParserAndMapper createUTF8DirectByteParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonUTF8Parser (  ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));

        jsonParser.setCharset ( StandardCharsets.UTF_8 );
        return jsonParser;

    }

    public JsonParserAndMapper createASCIIParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonAsciiParser (  ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));

        jsonParser.setCharset ( StandardCharsets.US_ASCII );
        return jsonParser;

    }


    public JsonParserAndMapper createLaxParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserLax ( false, chop, lazyChop  ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }



    public JsonParserAndMapper createParserWithEvents(JsonParserEvents events) {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserLax ( false, chop, lazyChop, false, events  ),
                FieldAccessMode.create( fieldAccessType, useAnnotations ));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }


    public JsonParserAndMapper createCharacterSourceParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserUsingCharacterSource ( ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }

    public JsonParserAndMapper createJsonCharArrayParser() {
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new JsonParserCharArray( ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }

    public JsonParserAndMapper createPlistParser() {

        if (charset==null) {
           charset= StandardCharsets.US_ASCII;
        }
        BaseJsonParserAndMapper jsonParser = new BaseJsonParserAndMapper( new PlistParser ( false, chop, lazyChop  ),  FieldAccessMode.create( fieldAccessType, useAnnotations ));

        jsonParser.setCharset ( charset );
        return jsonParser;
    }

    public JsonParserAndMapper createLazyFinalParser() {
        return createFastParser();
    }

    public JsonParserAndMapper createJsonParserForJsonPath() {
        return createFastParser();
    }

    public JsonParserAndMapper create() {



        if ( charset == null ) {
            charset = StandardCharsets.UTF_8;
        }

        return new JsonMappingParser ( FieldAccessMode.create( fieldAccessType, useAnnotations ), charset,
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
