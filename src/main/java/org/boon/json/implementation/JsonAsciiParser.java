package org.boon.json.implementation;

import org.boon.json.JsonParser;

import java.nio.charset.Charset;

/**
 * Created by rick on 12/15/13.
 */
public class JsonAsciiParser extends JsonBaseByteArrayParser implements JsonParser {


    protected final void addChar() {
        builder.addChar( __currentChar );
    }

}
