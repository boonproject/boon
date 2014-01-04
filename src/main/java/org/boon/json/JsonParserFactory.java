package org.boon.json;

import org.boon.core.reflection.fields.*;
import org.boon.json.implementation.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonParserFactory {


    private boolean preferCharSequence = false;
    private Charset charset = null;
    private boolean lax;
    private boolean plistStyle;
    private boolean chop = false;
    private boolean lazyChop = true;
    private FieldAccessMode fieldAccessType = FieldAccessMode.FIELD;


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


    public JsonParserFactory preferCharSequence() {
        this.preferCharSequence = true;
        return this;
    }


    public JsonParserFactory neverPreferCharSequence() {
        this.preferCharSequence = false;
        return this;
    }



    public JsonParser createFastParser() {
        BaseJsonParser jsonParser = new JsonFastParser(  FieldAccessMode.create( fieldAccessType ), false, true );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }




    public JsonParser createUTF8DirectByteParser() {
        BaseJsonParser jsonParser = new JsonUTF8Parser( FieldAccessMode.create( fieldAccessType ) );
        jsonParser.setCharset ( StandardCharsets.UTF_8 );
        return (JsonParser)jsonParser;

    }

    public JsonParser createASCIIParser() {
        BaseJsonParser jsonParser = new JsonAsciiParser ( FieldAccessMode.create( fieldAccessType ) );
        jsonParser.setCharset ( StandardCharsets.US_ASCII );
        return (JsonParser)jsonParser;

    }


    public JsonParser createLaxParser() {
        BaseJsonParser jsonParser = new JsonParserLax ( FieldAccessMode.create( fieldAccessType ),  false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser createPlistParser() {
        BaseJsonParser jsonParser = new PlistParser ( FieldAccessMode.create( fieldAccessType ), false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser createLazyFinalParser() {
        BaseJsonParser jsonParser = new JsonFastParser ( FieldAccessMode.create( fieldAccessType ),  false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;

    }

    public JsonParser createJsonParserForJsonPath() {

        BaseJsonParser jsonParser = new JsonFastParser (  FieldAccessMode.create( fieldAccessType ), false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser create() {




        if ( ( charset == null ) && lax && plistStyle ) {
            charset = StandardCharsets.US_ASCII;
        } else if ( charset == null ) {
            charset = StandardCharsets.UTF_8;
        }

        return new JsonParserImpl( FieldAccessMode.create( fieldAccessType ), charset,
                 lax, plistStyle, chop, lazyChop );
    }


    public JsonParserFactory plistStyle() {
        plistStyle = true;
        return this;
    }
}
