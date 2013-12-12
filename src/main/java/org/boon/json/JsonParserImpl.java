package org.boon.json;

import org.boon.IO;
import org.boon.core.Value;
import org.boon.core.reflection.Reflection;
import org.boon.json.implementation.JsonIndexOverlayParser;
import org.boon.json.implementation.JsonParserCharArray;
import org.boon.json.implementation.JsonParserCharSequence;
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
    private final int sizeSmallerUseOverlayAlways;
    private final boolean preferCharSequence;
    private final boolean lax;


    private final JsonParser objectParser;
    private final JsonParser basicParser;
    private final JsonParser charSequenceParser;
    private final JsonParser overlayParser;

    int bufSize = 256;


    public JsonParserImpl( boolean useDirectBytes, Charset charset, boolean overlay, int sizeSmallerUseOverlayAlways,
                           boolean preferCharSequence, boolean lax) {

        this.lax = lax;

        if (lax)  {
            this.useDirectBytes = false;
            this.overlay = false;
            this.sizeSmallerUseOverlayAlways = 0;
            this.preferCharSequence = false;

        }   else {
            this.useDirectBytes = useDirectBytes;
            this.overlay = overlay;
            this.sizeSmallerUseOverlayAlways = sizeSmallerUseOverlayAlways;
            this.preferCharSequence = preferCharSequence;
        }

        this.charset = charset;

        if (lax )  {
            //For now there is only one lax parser so force it to that if they are using lax.
            this.basicParser = new JsonParserCharArray ( true );
            this.overlayParser = this.basicParser;
            this.objectParser = this.basicParser;
            this.charSequenceParser = this.basicParser;
        }  else {
            this.overlayParser = new JsonIndexOverlayParser (  );

            if (overlay) {
                this.basicParser = overlayParser;
            } else {
                this.basicParser = new JsonParserCharArray ( );
            }

            this.objectParser = new JsonIndexOverlayParser ( true );

            if (preferCharSequence) {
                this.charSequenceParser = new JsonParserCharSequence ();
            } else {
                this.charSequenceParser = basicParser;
            }

        }








    }

    @Override
    public <T> T parse( Class<T> type, String value ) {

        if (type == Map.class || type == List.class ) {
            return basicParser.parse ( type, value );
        } else {

            if (!lax) {
                Map<String, Value> objectMap = (Map<String, Value>) objectParser.parse ( Map.class, value );
                return Reflection.fromValueMap (objectMap, type);
            } else {
                Map<String, Object> objectMap = (Map<String, Object>) objectParser.parse ( Map.class, value );
                return Reflection.fromMap ( objectMap, type );
            }
        }
    }

    @Override
    public <T> T parse( Class<T> type, byte[] value ) {


        if (type == Map.class || type == List.class ) {
            if (value.length < this.sizeSmallerUseOverlayAlways ) {
                return overlayParser.parse ( type, value );
            } else {
                this.bufSize = value.length;
                return this.parse ( type, new ByteArrayInputStream ( value ) );
            }

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

        charBuf = IO.read ( reader, charBuf, bufSize );
        return parse ( type,  charBuf.readForRecycle () );

    }

    @Override
    public <T> T parse( Class<T> type, InputStream input ) {
        charBuf = IO.read ( input, charBuf, this.charset, bufSize );
        return parse ( type,  charBuf.readForRecycle () );
    }

    @Override
    public <T> T parse( Class<T> type, InputStream input, Charset charset ) {
        charBuf = IO.read ( input, charBuf, charset, bufSize );
        return parse ( type,  charBuf.readForRecycle () );
    }

}
