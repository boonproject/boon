package org.boon.json.implementation;

import org.boon.IO;
import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonParser;
import org.boon.primitive.CharBuf;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Richard on 2/2/14.
 */
public abstract class BaseJsonParser implements JsonParser {


        protected static final int COLON = ':';
        protected static final int COMMA = ',';
        protected static final int CLOSED_CURLY = '}';
        protected static final int CLOSED_BRACKET = ']';

        protected static final int LETTER_E = 'e';
        protected static final int LETTER_BIG_E = 'E';

        protected static final int MINUS = '-';
        protected static final int PLUS = '+';

        protected static final int DECIMAL_POINT = '.';


        protected static final int ALPHA_0 = '0';
        protected static final int ALPHA_1 = '1';
        protected static final int ALPHA_2 = '2';
        protected static final int ALPHA_3 = '3';
        protected static final int ALPHA_4 = '4';
        protected static final int ALPHA_5 = '5';
        protected static final int ALPHA_6 = '6';
        protected static final int ALPHA_7 = '7';
        protected static final int ALPHA_8 = '8';
        protected static final int ALPHA_9 = '9';


        protected static final int DOUBLE_QUOTE = '"';

        protected static final int ESCAPE = '\\';

        protected static final boolean internKeys = Boolean.parseBoolean( System.getProperty( "org.boon.json.implementation.internKeys", "false" ) );
        protected static ConcurrentHashMap<String, String> internedKeysCache;

        protected Charset charset  = StandardCharsets.UTF_8;


        protected int bufSize  = 256;


        static {
            if ( internKeys ) {
                internedKeysCache = new ConcurrentHashMap<>();
            }
        }



        protected String charDescription( int c ) {
            String charString;
            if ( c == ' ' ) {
                charString = "[SPACE]";
            } else if ( c == '\t' ) {
                charString = "[TAB]";

            } else if ( c == '\n' ) {
                charString = "[NEWLINE]";

            } else {
                charString = "'" + (char)c + "'";
            }

            charString = charString + " with an int value of " + ( ( int ) c );
            return charString;
        }





        public void setCharset( Charset charset ) {
            this.charset = charset;
        }


        @Override
        public Object parse ( String jsonString ) {
            return parse ( FastStringUtils.toCharArray ( jsonString ) );
        }

        @Override
        public Object parse ( byte[] bytes ) {
            return parse ( bytes, charset );
        }


        @Override
        public Object parse ( CharSequence charSequence ) {
            return parse ( FastStringUtils.toCharArray ( charSequence ) );
        }

        @Override
        public  Object parse(  Reader reader ) {

            fileInputBuf = IO.read ( reader, fileInputBuf, bufSize );
            return parse( fileInputBuf.readForRecycle() );

        }

        @Override
        public Object parse ( InputStream input ) {
            return parse ( input, charset );
        }

        @Override
        public Object parse ( InputStream input, Charset charset ) {
            return parse ( new InputStreamReader ( input, charset ) );
        }

        private final CharBuf builder = CharBuf.create( 20 );



        private CharBuf fileInputBuf;

//
//        protected static final boolean isNumberDigit (int c)  {
//            return c >= ALPHA_0 && c <= ALPHA_9;
//        }



    int[] indexHolder = new int[1];


    }
