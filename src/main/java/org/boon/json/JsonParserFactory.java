package org.boon.json;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonParserFactory {


    boolean preferCharSequence = false;

    boolean useDirectBytes = true;
    Charset charset= null;
    boolean overlay = false;
    int sizeToUseOverlay = 50;
    boolean lax;
    private boolean pllistStyle;


    public JsonParserFactory lax() {
        lax = true;
        return this;
    }

    public JsonParserFactory strict() {
        lax = false;
        return this;
    }

    public JsonParserFactory useDirectBytes() {
         useDirectBytes = true;
        return this;
    }


    public JsonParserFactory neverUseDirectBytes() {
        useDirectBytes = false;
        return this;
    }



    public JsonParserFactory setCharset( Charset charset ) {
        this.charset = charset;
        return this;
    }


    public JsonParserFactory setSizeToUseOverlay( int size ) {
        this.sizeToUseOverlay = size;
        return this;
    }

    public JsonParserFactory preferCharSequence(  ) {
        this.preferCharSequence = true;
        return this;
    }



    public JsonParserFactory neverPreferCharSequence(  ) {
        this.preferCharSequence = false;
        return this;
    }

    public JsonParserFactory useOverlay() {
         overlay = true;
        return this;
    }


    public JsonParserFactory neverUseOverlay() {
        overlay = false;
        return this;
    }



    public JsonParser create() {



        if (lax && pllistStyle && charset == null || charset == StandardCharsets.US_ASCII) {
            this.useDirectBytes = true;

        }

         if ( ( charset==null ) && lax && pllistStyle) {

             charset = StandardCharsets.US_ASCII;
         }else {
             charset = StandardCharsets.UTF_8;
         }

        return new JsonParserImpl ( useDirectBytes, charset, overlay, sizeToUseOverlay,
         preferCharSequence, lax, pllistStyle );
    }


    public JsonParserFactory plistStyle () {
        lax = true;
        pllistStyle = true;
        return this;
    }
}
