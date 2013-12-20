package org.boon.json;

import org.boon.json.implementation.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonParserFactory {


    boolean preferCharSequence = false;

    boolean useDirectBytes = true;
    Charset charset = null;
    boolean lazyFinalParse = false;
    int sizeToForceLazyFinalParse = 50;
    boolean lax;
    private boolean plistStyle;


    public JsonParserFactory lax () {
        lax = true;
        return this;
    }

    public JsonParserFactory strict () {
        lax = false;
        return this;
    }

    public JsonParserFactory useDirectBytes () {
        useDirectBytes = true;
        return this;
    }


    public JsonParserFactory neverUseDirectBytes () {
        useDirectBytes = false;
        return this;
    }


    public JsonParserFactory setCharset ( Charset charset ) {
        this.charset = charset;
        return this;
    }


    public JsonParserFactory setSizeToForceLazyFinalParse ( int size ) {
        this.sizeToForceLazyFinalParse = size;
        return this;
    }

    public JsonParserFactory preferCharSequence () {
        this.preferCharSequence = true;
        return this;
    }


    public JsonParserFactory neverPreferCharSequence () {
        this.preferCharSequence = false;
        return this;
    }

    public JsonParserFactory useLazyFinalParse () {
        lazyFinalParse = true;
        return this;
    }


    public JsonParserFactory neverUseLazyFinalParse () {
        lazyFinalParse = false;
        return this;
    }


    public JsonParser createFastParser () {
        return new JsonFastParser ( false, true );
    }


    public JsonParser createCharSequenceParser () {
        return new JsonParserCharSequence ();
    }


    public JsonParser createUTFDirectByteParser () {
        return new JsonUTF8Parser ();
    }

    public JsonParser createASCIIParser () {
        return new JsonAsciiParser ();
    }


    public JsonParser createLaxParser () {
        return new JsonParserLax ();
    }

    public JsonParser createLazyFinalParser () {
        return new JsonFastParser ();
    }

    public JsonParser createJsonParserForJsonPath () {
        return new JsonFastParser ();
    }

    public JsonParser create () {


        if ( lax && plistStyle && ( charset == null || charset == StandardCharsets.US_ASCII ) ) {
            this.useDirectBytes = true;

        }

        if ( ( charset == null ) && lax && plistStyle ) {

            charset = StandardCharsets.US_ASCII;
        } else if ( charset == null ) {
            charset = StandardCharsets.UTF_8;
        }

        return new JsonParserImpl ( useDirectBytes, charset, lazyFinalParse, sizeToForceLazyFinalParse,
                preferCharSequence, lax, plistStyle );
    }


    public JsonParserFactory plistStyle () {
        lax = true;
        plistStyle = true;
        return this;
    }
}
