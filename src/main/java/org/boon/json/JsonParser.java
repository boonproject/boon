package org.boon.json;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public interface JsonParser {


<<<<<<< HEAD
    <T> T parse ( Class<T> type, String str );


    <T> T parse ( Class<T> type, byte[] bytes );


    <T> T parse ( Class<T> type, CharSequence charSequence );


    <T> T parse ( Class<T> type, char[] chars );

    <T> T parse ( Class<T> type, Reader reader );

    <T> T parse ( Class<T> type, InputStream input );

    <T> T parse ( Class<T> type, InputStream input, Charset charset );
=======
    <T> T parse( Class<T> type, String str );


    <T> T parse( Class<T> type, byte[] bytes );


    <T> T parse( Class<T> type, CharSequence charSequence );


    <T> T parse( Class<T> type, char[] chars );

    <T> T parse( Class<T> type, Reader reader );

    <T> T parse( Class<T> type, InputStream input );

    <T> T parse( Class<T> type, InputStream input, Charset charset );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    <T> T parseDirect ( Class<T> type, byte[] value );

    <T> T parseAsStream ( Class<T> type, byte[] value );


}
