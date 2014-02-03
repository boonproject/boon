package org.boon.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public interface JsonParser {
    Object parse(  String jsonString );
    Object parse(  byte[] bytes );
    Object parse(  byte[] bytes, Charset charset );
    Object parse(  CharSequence charSequence );
    Object parse(  char[] chars );
    Object parse(  Reader reader );
    Object parse(  InputStream input );
    Object parse(  InputStream input, Charset charset );
}
