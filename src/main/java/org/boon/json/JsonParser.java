package org.boon.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public interface JsonParser {


    <T> T parse ( Class<T> type, String str );


    <T> T parse ( Class<T> type, byte[] bytes );


    <T> T parse ( Class<T> type, CharSequence charSequence );


    <T> T parse ( Class<T> type, char[] chars );

    <T> T parse ( Class<T> type, Reader reader );

    <T> T parse ( Class<T> type, InputStream input );

    <T> T parse ( Class<T> type, InputStream input, Charset charset );

    <T> T parseDirect ( Class<T> type, byte[] value );

    <T> T parseAsStream ( Class<T> type, byte[] value );


}
