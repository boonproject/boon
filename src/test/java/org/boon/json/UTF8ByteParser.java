package org.boon.json;


import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by rick on 12/15/13.
 */
public class UTF8ByteParser extends JsonParserAndMapperBaseTest {


    public JsonParserAndMapper parser () {
        return new JsonParserFactory().createUTF8DirectByteParser();
    }

    public JsonParserAndMapper objectParser () {
        return parser();
    }



}