package org.boon.json;

import org.boon.IO;
import org.boon.core.Value;
import org.boon.core.reflection.Reflection;
import org.boon.json.implementation.JsonIndexOverlayParser;
import org.boon.json.implementation.JsonParserCharArray;
import org.boon.json.implementation.JsonParserCharSequence;
import org.boon.json.implementation.JsonUTF8Parser;
import org.boon.primitive.CharBuf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;


public class JsonParserImpl implements JsonParser {


    private final boolean useDirectBytes;
    private final Charset charset;
    private final boolean overlay;
    private final int sizeToUseDirectBytes;
    private final boolean preferCharSequence;


    private final JsonParser objectParser;
    private final JsonParser basicParser;
    private final JsonParser directByteParser;
    private final JsonParser charSequenceParser;


    public JsonParserImpl( boolean useDirectBytes, Charset charset, boolean overlay, int sizeToUseDirectBytes,
                           boolean preferCharSequence) {
        this.useDirectBytes = useDirectBytes;
        this.charset = charset;
        this.overlay = overlay;
        this.sizeToUseDirectBytes = sizeToUseDirectBytes;
        this.preferCharSequence = preferCharSequence;

        if (overlay) {
            this.basicParser = new JsonIndexOverlayParser (  );
        } else {
            this.basicParser = new JsonParserCharArray ( );
        }

        this.objectParser = new JsonIndexOverlayParser ( true );

        if ( useDirectBytes ) {
            directByteParser =  new JsonUTF8Parser ();
        } else {
            directByteParser = basicParser;
        }




        if (preferCharSequence) {
             this.charSequenceParser = new JsonParserCharSequence ();
        } else {
            this.charSequenceParser = basicParser;
        }






    }

    @Override
    public <T> T parse( Class<T> type, String value ) {

        if (type == Map.class || type == List.class ) {
            return basicParser.parse ( type, value );
        } else {
            Map<String, Value> objectMap = (Map<String, Value>) objectParser.parse ( Map.class, value );
            return Reflection.fromValueMap (objectMap, type);
        }
    }

    @Override
    public <T> T parse( Class<T> type, byte[] value ) {


        if (type == Map.class || type == List.class ) {
//            if (value.length > this.sizeToUseDirectBytes) {
//                return directByteParser.parse ( type, value );
//            } else {
//                return basicParser.parse ( type, value );
//            }
              return this.parse ( type, new ByteArrayInputStream ( value ) );

        } else {
            Map<String, Value> objectMap = (Map<String, Value>) objectParser.parse ( Map.class, value );
            return Reflection.fromValueMap (objectMap, type);
        }
    }

    @Override
    public <T> T parse( Class<T> type, CharSequence value ) {
        if (type == Map.class || type == List.class ) {
            return charSequenceParser.parse ( type, value );
        } else {
            Map<String, Value> objectMap = (Map<String, Value>) objectParser.parse ( Map.class, value );
            return Reflection.fromValueMap (objectMap, type);
        }
    }

    @Override
    public <T> T parse( Class<T> type, char[] value ) {
        if (type == Map.class || type == List.class ) {
            return basicParser.parse ( type, value );
        } else {
            Map<String, Value> objectMap = (Map<String, Value>) objectParser.parse ( Map.class, value );
            return Reflection.fromValueMap (objectMap, type);
        }

    }


    CharBuf charBuf;

    @Override
    public <T> T parse( Class<T> type, Reader reader ) {

        charBuf = IO.read ( reader, charBuf );
        return parse ( type,  charBuf.readForRecycle () );

    }

    @Override
    public <T> T parse( Class<T> type, InputStream input ) {
        charBuf = IO.read ( input, charBuf, this.charset );
        return parse ( type,  charBuf.readForRecycle () );
    }

    @Override
    public <T> T parse( Class<T> type, InputStream input, Charset charset ) {
        charBuf = IO.read ( input, charBuf, charset );
        return parse ( type,  charBuf.readForRecycle () );
    }

}
