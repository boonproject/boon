package org.boon.json.implementation;

import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.json.JsonParser;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created by rick on 12/15/13.
 */
public class JsonAsciiParser extends JsonBaseByteArrayParser implements JsonParser {


    public JsonAsciiParser(  ) {
        super( FieldAccessMode.create( FieldAccessMode.FIELD ) );
        this.charset = StandardCharsets.US_ASCII;

    }

    public JsonAsciiParser( FieldAccessMode mode ) {
        super( FieldAccessMode.create(mode) );
        this.charset = StandardCharsets.US_ASCII;

    }

    public JsonAsciiParser( FieldsAccessor fieldsAccessor ) {
        super( fieldsAccessor );
        this.charset = StandardCharsets.US_ASCII;

    }

    protected final void addChar() {
        builder.addChar( __currentChar );
    }

}
