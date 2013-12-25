package org.boon.json;

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
        BaseJsonParser jsonParser = new JsonFastParser( false, true );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }


    public JsonParser createCharSequenceParser() {
        BaseJsonParser jsonParser = new JsonParserCharSequence(  );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;

    }


    public JsonParser createUTFDirectByteParser() {
        BaseJsonParser jsonParser = new JsonUTF8Parser(  );
        jsonParser.setCharset ( StandardCharsets.UTF_8 );
        return (JsonParser)jsonParser;

    }

    public JsonParser createASCIIParser() {
        BaseJsonParser jsonParser = new JsonAsciiParser (  );
        jsonParser.setCharset ( StandardCharsets.US_ASCII );
        return (JsonParser)jsonParser;

    }


    public JsonParser createLaxParser() {
        BaseJsonParser jsonParser = new JsonParserLax ( false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser createPlistParser() {
        BaseJsonParser jsonParser = new PlistParser (false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser createLazyFinalParser() {
        BaseJsonParser jsonParser = new JsonFastParser ( false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;

    }

    public JsonParser createJsonParserForJsonPath() {

        BaseJsonParser jsonParser = new JsonFastParser ( false, chop, lazyChop );
        jsonParser.setCharset ( charset );
        return (JsonParser)jsonParser;
    }

    public JsonParser create() {



        if ( ( charset == null ) && lax && plistStyle ) {
            charset = StandardCharsets.US_ASCII;
        } else if ( charset == null ) {
            charset = StandardCharsets.UTF_8;
        }

        return new JsonParserImpl( charset,
                preferCharSequence, lax, plistStyle );
    }


    public JsonParserFactory plistStyle() {
        plistStyle = true;
        return this;
    }
}
