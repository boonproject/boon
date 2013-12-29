package org.boon.json.implementation;

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



    public JsonAsciiParser() {
        this.charset = StandardCharsets.US_ASCII;
    }

    protected final void addChar() {
        builder.addChar( __currentChar );
    }

}
