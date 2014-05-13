/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.primitive;

import org.boon.Str;
import org.boon.cache.Cache;
import org.boon.cache.CacheType;
import org.boon.cache.SimpleCache;
import org.boon.core.Dates;
import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import static org.boon.Exceptions.die;
import static org.boon.Lists.toListOrSingletonList;
import static org.boon.primitive.CharScanner.*;

public class CharBuf extends PrintWriter implements CharSequence {
    protected int capacity = 16;
    protected int location = 0;

    protected char[] buffer;


    public CharBuf( char[] buffer ) {
        super(writer());
        this.buffer = buffer;
        this.capacity = buffer.length;
    }

    public CharBuf( byte[] bytes ) {

        super(writer());

        String str = new String(bytes, StandardCharsets.UTF_8);
        this.buffer = FastStringUtils.toCharArray(str);
        this.location = buffer.length;
        this.capacity = buffer.length;

    }

    public static CharBuf createExact( final int capacity ) {
        return new CharBuf( capacity ) {
            public CharBuf add( char[] chars ) {
                Chr._idx( buffer, location, chars );
                location += chars.length;
                return this;
            }
        };
    }

    public static CharBuf create( int capacity ) {
        return new CharBuf( capacity );
    }

    public static CharBuf createCharBuf( int capacity ) {
        return new CharBuf( capacity );
    }


    public static CharBuf createCharBuf(  ) {
        return new CharBuf( 100 );
    }

    public static CharBuf create( char[] buffer ) {
        return new CharBuf( buffer );
    }

    public static CharBuf createFromUTF8Bytes( byte[] buffer ) {
        return new CharBuf( buffer );
    }

    protected CharBuf( int capacity ) {

        super(writer());
        this.capacity = capacity;
        init();
    }


    protected CharBuf() {
        super(writer());
        init();
    }

    @Override
    public void write( char[] cbuf, int off, int len ) {

        if ( off == 0 && cbuf.length == len ) {
            this.add( cbuf );
        } else {
            char[] buffer = Arrays.copyOfRange( cbuf, off, off + len );
            this.add( buffer );
        }

    }

    @Override
    public void flush()  {
    }

    @Override
    public void close()  {
    }

    public void init() {
        buffer = new char[ capacity ];
    }


    public final CharBuf add( Object str ) {
        add( FastStringUtils.toCharArray( Str.str(str) ) );
        return this;
    }

    public final CharBuf add( String str ) {
        add( FastStringUtils.toCharArray( str ) );
        return this;
    }


    public final CharBuf add( CharSequence str ) {
        add( FastStringUtils.toCharArray( str ) );
        return this;
    }

    public final CharBuf addString( String str ) {
        add( FastStringUtils.toCharArray( str ) );
        return this;
    }

    public final CharBuf addObject ( Object object ) {
        String str = object.toString();
        addString ( str );
        return this;
    }

    public final CharBuf add( int i ) {

        add ( Integer.toString ( i ));
        return this;
    }


    private Cache <Integer, char[]> icache;

    public final CharBuf addInt( int i ) {
        switch (i) {
            case 0:
                addChar('0');
                return this;
            case 1:
                addChar('1');
                return this;
            case -1:
                addChar('-');
                addChar('1');
                return this;
        }

        addInt( Integer.valueOf( i ) );
        return this;
    }
    public final CharBuf addInt( Integer key ) {


        if (icache == null) {
            icache = new SimpleCache<> ( 1000, CacheType.LRU );
        }
        char [] chars = icache.get ( key );

        if ( chars ==  null ) {
            String str = Integer.toString ( key );
            chars = FastStringUtils.toCharArray ( str );
            icache.put ( key, chars );
        }

        addChars ( chars );
        return this;
    }


    final char [] trueChars = "true".toCharArray ();
    final char [] falseChars = "false".toCharArray ();

    public final CharBuf add( boolean b ) {
        addChars ( b ? trueChars : falseChars );
        return this;
    }


    public final CharBuf addBoolean( boolean b ) {

        add ( Boolean.toString ( b ));
        return this;
    }

    public final  CharBuf add( byte i ) {

        add ( Byte.toString ( i ));
        return this;
    }

    public final CharBuf addByte( byte i ) {

        addInt ( i );
        return this;
    }


    public final CharBuf add( short i ) {

        add ( Short.toString ( i ));
        return this;
    }


    public final CharBuf addShort( short i ) {

        addInt ( i );
        return this;
    }

    public final CharBuf add( long l ) {
        add ( Long.toString ( l ));
        return this;
    }



    public final  CharBuf add( double d ) {
        add ( Double.toString ( d ) );
        return this;
    }


    private Cache <Double, char[]> dcache;



    public final  CharBuf addDouble( double d ) {
        addDouble( Double.valueOf( d ) );
        return this;
    }

    public final  CharBuf addDouble( Double key ) {

        if (dcache == null) {
            dcache = new SimpleCache<> ( 100, CacheType.LRU );
        }
        char [] chars = dcache.get ( key );

        if ( chars ==  null ) {
            String str = Double.toString ( key );
            chars = FastStringUtils.toCharArray ( str );
            dcache.put ( key, chars );
        }

        add ( chars );
        return this;
    }


    public final  CharBuf add( float d ) {
        add ( Float.toString ( d ));
        return this;
    }


    private Cache <Float, char[]> fcache;

    public final  CharBuf addFloat( float d ) {
        addFloat( Float.valueOf( d ) );
        return this;
    }


    public final  CharBuf addFloat( Float key ) {

        if (fcache == null) {
            fcache = new SimpleCache<> ( 100, CacheType.LRU );
        }
        char [] chars = fcache.get ( key );

        if ( chars ==  null ) {
            String str = Float.toString ( key );
            chars = FastStringUtils.toCharArray ( str );
            fcache.put ( key, chars );
        }

        add ( chars );

        return this;
    }

    public final CharBuf addChar( byte i ) {
        add( ( char ) i );
        return this;
    }


    public final CharBuf addChar( int i ) {
        add( ( char ) i );
        return this;
    }


    public final CharBuf addChar( short i ) {
        add( ( char ) i );
        return this;
    }


    public final CharBuf addChar( final char ch ) {


        if ( 1 + location > capacity ) {
            buffer = Chr.grow( buffer );
            capacity = buffer.length;

        }

        buffer [location] = ch;
        location ++;

        return this;
    }

    public final CharBuf add( char ch ) {

        if ( 1 + location > capacity ) {
            buffer = Chr.grow( buffer );
            capacity = buffer.length;

        }

        buffer [location] = ch;
        location ++;

        return this;
    }

    public CharBuf addLine( String str ) {
        add( FastStringUtils.toCharArray(str));
        add( '\n' );
        return this;
    }

    public CharBuf addLine( Object str ) {
        add( FastStringUtils.toCharArray(
                Str.str(str)));
        add( '\n' );
        return this;
    }



    public CharBuf addLine(  ) {
        add( '\n' );
        return this;
    }


    public CharBuf addLine( char[] chars ) {
        add( chars );
        add( '\n' );
        return this;
    }


    public CharBuf addLine( CharSequence str ) {
        add( str.toString() );
        add( '\n' );
        return this;
    }

    public CharBuf add( char[] chars ) {
        if ( chars.length + location > capacity ) {
            buffer = Chr.grow( buffer, buffer.length * 2 + chars.length );
            capacity = buffer.length;
        }

        Chr._idx( buffer, location, chars );
        location += chars.length;
        return this;
    }



    public final CharBuf addChars( char[] chars ) {
        if ( chars.length + location > capacity ) {
            buffer = Chr.grow( buffer, buffer.length * 2 + chars.length );
            capacity = buffer.length;
        }

        System.arraycopy ( chars, 0, buffer, location, chars.length );
        location += chars.length;
        return this;
    }


    public final CharBuf addQuoted( char[] chars ) {


        int sizeNeeded = chars.length + 2 + location;
        if (  sizeNeeded > capacity ) {
            buffer = Chr.grow( buffer, sizeNeeded * 2  );
            capacity = buffer.length;
        }
        buffer [location] = '"';
        location++;

        System.arraycopy( chars, 0, buffer, location, chars.length );
        location += (chars.length);
        buffer [location] = '"';
        location++;

        return this;
    }



    public final CharBuf asJsonString(String jsonString) {
        char[] charArray = FastStringUtils.toCharArray ( jsonString );
        return addJsonEscapedString ( charArray );

    }



    private static  boolean isJSONControlOrUnicode( int c ) {

        return (c < 30 || c == 34 || c == 92 || c > 126) ? true : false;

    }

    int jsonControlCount;

    private final  boolean hasAnyJSONControlOrUnicodeChars( final char[] charArray ) {
        int index = 0;
        char c;
        while ( true ) {
            c = charArray[ index ];
            if ( isJSONControlOrUnicode( c )) {
                jsonControlCount++;

            }
            if ( ++index >= charArray.length) break;
        }

        return (jsonControlCount>0) ? true : false;

    }



    public final CharBuf addJsonEscapedString( final char[] charArray ) {
        jsonControlCount = 0;

        if ( charArray.length > 0 && hasAnyJSONControlOrUnicodeChars( charArray )) {
            return doAddJsonEscapedString(charArray);
        } else {
            return this.addQuoted ( charArray );
        }
    }


    final byte[] encoded = new byte[2];

    final byte[] charTo = new byte[2];

    private final CharBuf doAddJsonEscapedString( char[] charArray ) {


        char [] _buffer = buffer;
        int _location =  this.location;

        final byte[] _encoded = encoded;

        final byte[] _charTo = charTo;
        /* We are making a bet that not all chars will be unicode. */
        int  ensureThisMuch = charArray.length +  ((jsonControlCount +1) * 5);

        int sizeNeeded =  (ensureThisMuch) + _location;
        if ( sizeNeeded  > capacity ) {

            int growBy =   ( _buffer.length * 2 ) <  sizeNeeded  ? sizeNeeded : (_buffer.length*2);
            _buffer = Chr.grow( _buffer, growBy);
            capacity = _buffer.length;
        }




        _buffer[_location] = '"';
        _location ++;

        int index = 0;
        while ( true ) {

                char c = charArray[ index ];


                if ( isJSONControlOrUnicode( c )) {

                    switch ( c ) {
                        case '\"':
                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  '"';
                            _location ++;
                            break;
                        case '\\':
                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  '\\';
                            _location ++;
                            break;
                        case '\b':
                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  'b';
                            _location ++;
                            break;
                        case '\f':
                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  'f';
                            _location ++;
                            break;
                        case '\n':
                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  'n';
                            _location ++;
                            break;
                        case '\r':
                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  'r';
                            _location ++;
                            break;

                        case '\t':
                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  't';
                            _location ++;
                            break;

                        default:

                            _buffer[_location] = '\\';
                            _location ++;
                            _buffer[_location] =  'u';
                            _location ++;
                            if (c <= 255) {
                                _buffer[_location] =  '0';
                                _location ++;
                                _buffer[_location] =  '0';
                                _location ++;
                                ByteScanner.encodeByteIntoTwoAsciiCharBytes( c, _encoded );
                                for (int b : _encoded) {
                                    _buffer [_location] = (char)b;
                                    _location ++;

                                }
                            } else {
                                Byt.charTo( _charTo, c );

                                for (int charByte : _charTo) {
                                    ByteScanner.encodeByteIntoTwoAsciiCharBytes( charByte, _encoded );
                                    for (int b : _encoded) {
                                        _buffer [_location] = (char)b;
                                        _location ++;
                                    }
                                }

                            }

                    }
                }else {

                        _buffer[_location] = c;
                        _location ++;

                }


                if ( ++index >= charArray.length) break;


        }
        _buffer[_location] = '"';
        _location ++;


        buffer = _buffer;
        location = _location;

        return this;
    }




    public final CharBuf addJsonFieldName( String str ) {
        return addJsonFieldName ( FastStringUtils.toCharArray ( str ) );
    }

    public final CharBuf addJsonFieldName( char[] chars ) {
            int sizeNeeded = chars.length + 4 + location;
            if (  sizeNeeded > capacity ) {
                buffer = Chr.grow( buffer, sizeNeeded * 2  );
                capacity = buffer.length;
            }
            buffer [location] = '"';
            location++;

            System.arraycopy( chars, 0, buffer, location, chars.length );

            location += (chars.length);
            buffer [location] = '"';
            location++;
            buffer [location] = ':';
            location++;

            return this;
     }

    public final CharBuf addQuoted( String str ) {
        final char[] chars = FastStringUtils.toCharArray ( str );
        addQuoted ( chars );
        return this;
    }


    public CharBuf add( char[] chars, final int length ) {
        if ( length + location < capacity ) {
            Chr._idx( buffer, location, chars, length );
        } else {
            buffer = Chr.grow( buffer, buffer.length * 2 + length );
            Chr._idx( buffer, location, chars );
            capacity = buffer.length;
        }
        location += length;
        return this;
    }

    public CharBuf add( byte[] chars ) {
        if ( chars.length + location < capacity ) {
            Chr._idx( buffer, location, chars );
        } else {
            buffer = Chr.grow( buffer, buffer.length * 2 + chars.length );
            Chr._idx( buffer, location, chars );
            capacity = buffer.length;
        }
        location += chars.length;
        return this;
    }




    public CharBuf add( byte[] bytes, int start, int end ) {
        int charsLength =  end - start ;
        if ( charsLength + location > capacity ) {
            buffer = Chr.grow( buffer, buffer.length * 2 + charsLength );
        }
        Chr._idx( buffer, location, bytes, start, end );
        capacity = buffer.length;
        location += charsLength;
        return this;
    }



    @Override
    public int length () {
        return len();
    }

    @Override
    public char charAt ( int index ) {
        return buffer [ index ];
    }

    @Override
    public CharSequence subSequence ( int start, int end ) {
        return new String (buffer, start, end - start);
    }

    public String toString() {
        return FastStringUtils.noCopyStringFromCharsNoCheck(buffer, location);
    }

    public String toDebugString() {
        return "CharBuf{" +
                "capacity=" + capacity +
                ", location=" + location +
                '}';
    }

    public String toStringAndRecycle() {

        String str = new String( buffer, 0, location );
        location = 0;
        return str;
    }

    public int len() {
        return location;
    }

    public char[] toCharArray() {
        return this.buffer;
    }

    public void _len( int location ) {
        this.location = location;
    }


    public char[] readForRecycle() {
        this.location = 0;
        return this.buffer;
    }


    public void recycle() {
        this.location = 0;
    }





    public double doubleValue() {
        return CharScanner.parseDouble( this.buffer, 0, location );
    }


    public float floatValue() {
        return CharScanner.parseFloat( this.buffer, 0, location );
    }

    public int intValue() {
        return CharScanner.parseIntFromTo( buffer, 0, location );
    }

    public long longValue() {
        return CharScanner.parseLongFromTo( buffer, 0, location );
    }


    public byte byteValue() {
        return ( byte ) intValue();
    }

    public short shortValue() {
        return ( short ) intValue();
    }


    public Number toIntegerWrapper() {

         if ( isInteger( buffer, 0, location  ) ) {
            return intValue() ;
        } else {
            return longValue();
        }

    }


    public void addAsUTF( byte[] value ) {

        String str = new String(value, StandardCharsets.UTF_8);
        final char[] chars = FastStringUtils.toCharArray(str);
        this.add(chars);


    }




    final static char [] nullChars = "null".toCharArray ();
    public final void addNull () {
        this.add ( nullChars );
    }

    public void removeLastChar () {
        location--;
        if (location < 0) {
            location = 0;
        }
    }


    private Cache <BigDecimal, char[]> bigDCache;
    public CharBuf addBigDecimal( BigDecimal key ) {
        if (bigDCache == null) {
            bigDCache = new SimpleCache<> ( 100, CacheType.LRU );
        }
        char [] chars = bigDCache.get ( key );

        if ( chars ==  null ) {
            String str = key.toString();
            chars = FastStringUtils.toCharArray ( str );
            bigDCache.put ( key, chars );
        }

        add ( chars );

        return this;


    }

    private Cache <BigInteger, char[]> bigICache;

    public CharBuf addBigInteger( BigInteger key ) {
        if (bigICache == null) {
            bigICache = new SimpleCache<> ( 100, CacheType.LRU );
        }
        char [] chars = bigICache.get ( key );

        if ( chars ==  null ) {
            String str = key.toString();
            chars = FastStringUtils.toCharArray ( str );
            bigICache.put ( key, chars );
        }

        add ( chars );

        return this;

    }


    private Cache <Long, char[]> lcache;



    public final  CharBuf addLong( long l ) {

        addLong(Long.valueOf( l ));
        return this;
    }

    public final  CharBuf addLong( Long key ) {

        if (lcache == null) {
            lcache = new SimpleCache<> ( 100, CacheType.LRU );
        }
        char [] chars = lcache.get ( key );

        if ( chars ==  null ) {
            String str = Long.toString ( key );
            chars = FastStringUtils.toCharArray ( str );
            lcache.put ( key, chars );
        }

        add ( chars );

        return this;
    }

    private Cache <Currency, char[]> currencyCache;
    public CharBuf addCurrency( Currency key ) {
        if (currencyCache == null) {
            currencyCache = new SimpleCache<> ( 100, CacheType.LRU );
        }
        char [] chars = currencyCache.get ( key );

        if ( chars ==  null ) {
            String str = '"' + key.toString() + '"';
            chars = FastStringUtils.toCharArray ( str );
            currencyCache.put ( key, chars );
        }

        add ( chars );

        return this;


    }


    /**
     * Turn a single bytes into two hex character representation.
     *
     * @param decoded the byte to serializeObject.
     */
    public  CharSequence addHex( final int decoded  ) {



        int _location = location;
        char [] _buffer = buffer;
        int _capacity = capacity;

        if ( 2 + _location > _capacity ) {
            _buffer = Chr.grow( _buffer );
            _capacity = _buffer.length;
        }

        _buffer [_location] = (char) encodeNibbleToHexAsciiCharByte( ( decoded >> 4 ) & 0x0F );
        _location ++;


        _buffer [_location] = (char) encodeNibbleToHexAsciiCharByte( decoded & 0x0F );;
        _location ++;

        location = _location;
        buffer = _buffer;
        capacity = _capacity;
        return this;
    }

    /**
     * Turns a single nibble into an ascii HEX digit.
     *
     * @param nibble the nibble to serializeObject.
     * @return the encoded nibble (1/2 byte).
     */
    protected static int encodeNibbleToHexAsciiCharByte( final int nibble ) {

        switch ( nibble ) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
            case 0x08:
            case 0x09:
                return nibble + 0x30; // 0x30('0') - 0x39('9')
            case 0x0A:
            case 0x0B:
            case 0x0C:
            case 0x0D:
            case 0x0E:
            case 0x0F:
                return nibble + 0x57; // 0x41('a') - 0x46('f')
            default:
                die( "illegal nibble: " + nibble );
                return -1;
        }
    }

    public final CharBuf decodeJsonString ( char[] chars ) {
        return decodeJsonString ( chars, 0, chars.length );
    }
    public final CharBuf decodeJsonString ( char[] chars, int start, int to ) {
        int len = to - start;


        char [] buffer = this.buffer;
        int location = this.location;



        if (len > capacity) {
            buffer =  Chr.grow ( buffer, (buffer.length * 2) + len );
            capacity = buffer.length;
            this.buffer = buffer;
        }

        for ( int index = start; index < to; index++ ) {
            char c = chars[ index ];
            if ( c == '\\' ) {
                if ( index < to -1  ) {
                    index++;
                    c = chars[ index ];
                    switch ( c ) {

                        case 'n':
                            buffer[location++]='\n';
                            break;

                        case '/':
                            buffer[location++]='/';
                            break;

                        case '"':
                            buffer[location++]='"';
                            break;

                        case 'f':
                            buffer[location++]='\f';
                            break;

                        case 't':
                            buffer[location++]='\t';
                            break;

                        case '\\':
                            buffer[location++]='\\';
                            break;

                        case 'b':
                            buffer[location++]='\b';
                            break;

                        case 'r':
                            buffer[location++]='\r';
                            break;

                        case 'u':

                            if ( index + 4 < to ) {
                                String hex = new String( chars, index + 1, 4 );
                                char unicode = ( char ) Integer.parseInt( hex, 16 );
                                buffer[location++]=unicode;
                                index += 4;
                            }
                            break;
                        default:
                            throw new JsonException ( "Unable to decode string" );
                    }
                }
            } else {
                buffer[location++]=c;
            }
        }
//        }
//        catch (ArrayIndexOutOfBoundsException axe) {
//
//            handle(axe, "Got ARRAY INDEX OUT OF BOUNDS \n",
//                    "the buffer is this big", this.buffer.length,
//                    "\nwe are at this location ", location,
//                    "\nthe length of the span is ", len, "input buffer length", chars.length);
//
//        }


        this.buffer = buffer;
        this.location = location;

        return this;

    }




    protected static final int DOUBLE_QUOTE = '"';

    protected static final int ESCAPE = '\\';


    protected static final int LETTER_N = 'n';


    protected static final int LETTER_U = 'u';


    protected static final int LETTER_T = 't';

    protected static final int LETTER_R = 'r';

    protected static final int LETTER_B = 'b';

    protected static final int LETTER_F = 'f';

    protected static final int FORWARD_SLASH = '/';


    public final CharBuf decodeJsonString ( byte[] bytes, int start, int to ) {


        String str = new String(bytes, start, to - start, StandardCharsets.UTF_8);
        final char[] chars = FastStringUtils.toCharArray(str);
        this.decodeJsonString(chars);
        return this;
    }

    public void ensure( int i ) {
        if ( i + location > capacity ) {
            buffer = Chr.grow( buffer, i * 2 );
            capacity = buffer.length;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null ) {
            return false;
        }

        if (o instanceof CharSequence) {
            return this.toString().equals(o.toString());
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    public CharBuf  multiply(char c, int len) {
        for (int index=0; index<len; index++) {
            this.add(c);
        }
        return this;
    }


    public CharBuf  multiply(CharSequence str, int len) {

        for (int index=0; index<len; index++) {
            this.add(str.toString());
        }
        return this;
    }

    public void puts(Object... messages) {

        for ( Object message : messages ) {


            if ( message == null ) {
                add( "<NULL>" );
            } else if (message instanceof char[]) {
                add((char[])message);
            } else if ( message.getClass().isArray() ) {
                add( toListOrSingletonList( message ).toString() );
            } else {
                add( message.toString() );
            }
            add( ' ' );
        }
        addLine();
    }


    /**
     * Prints an Object and then terminates the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param message  The <code>Object</code> to be printed.
     */
    public void println(String message) {
        this.addLine(message);
    }



    /**
     * Prints a String and then terminates the line.  This method behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param object the <code>String</code> value to be printed
     */
    public void println(Object object) {
        this.addLine(Str.toString(object));
    }

    public CharBuf indent(int i) {
        return multiply(' ', i);
    }

    public void jsonDate(long millis) {
        Dates.jsonDateChars(new Date(millis), this);
    }




    public PrintWriter append(CharSequence csq)  {
            return super.append(csq);
    }

    private static  Writer writer() {
        return new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {

            }

            @Override
            public void flush() throws IOException {

            }

            @Override
            public void close() throws IOException {

            }
        };
    }


        /* Methods that do not terminate lines */

    /**
     * Prints a boolean value.  The string produced by <code>{@link
     * java.lang.String#valueOf(boolean)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      b   The <code>boolean</code> to be printed
     */
    public void print(boolean b) {
        this.addBoolean(b);
    }

    /**
     * Prints a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      c   The <code>char</code> to be printed
     */
    public void print(char c) {
        this.add(c);
    }

    /**
     * Prints an integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(int)}</code> is translated into bytes according
     * to the platform's default character encoding, and these bytes are
     * written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      i   The <code>int</code> to be printed
     * @see        java.lang.Integer#toString(int)
     */
    public void print(int i) {
        this.addInt(i);
    }

    /**
     * Prints a long integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      l   The <code>long</code> to be printed
     * @see        java.lang.Long#toString(long)
     */
    public void print(long l) {
        this.addLong(l);
    }

    /**
     * Prints a floating-point number.  The string produced by <code>{@link
     * java.lang.String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      f   The <code>float</code> to be printed
     * @see        java.lang.Float#toString(float)
     */
    public void print(float f) {
        this.add(f);
    }

    /**
     * Prints a double-precision floating-point number.  The string produced by
     * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      d   The <code>double</code> to be printed
     * @see        java.lang.Double#toString(double)
     */
    public void print(double d) {

        this.add(d);
    }

    /**
     * Prints an array of characters.  The characters are converted into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      s   The array of chars to be printed
     *
     * @throws  NullPointerException  If <code>s</code> is <code>null</code>
     */
    public void print(char s[]) {
        this.addChars(s);
    }

    /**
     * Prints a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      s   The <code>String</code> to be printed
     */
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        this.addString(s);
    }

    /**
     * Prints an object.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      obj   The <code>Object</code> to be printed
     * @see        java.lang.Object#toString()
     */
    public void print(Object obj) {
        write(String.valueOf(obj));
    }

    /* Methods that do terminate lines */

    /**
     * Terminates the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public void println() {
        this.addLine();
    }

    /**
     * Prints a boolean value and then terminates the line.  This method behaves
     * as though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>boolean</code> value to be printed
     */
    public void println(boolean x) {
        this.addBoolean(x).addLine();
    }

    /**
     * Prints a character and then terminates the line.  This method behaves as
     * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
     * #println()}</code>.
     *
     * @param x the <code>char</code> value to be printed
     */
    public void println(char x) {
        this.addChar(x).addLine();
    }

    /**
     * Prints an integer and then terminates the line.  This method behaves as
     * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
     * #println()}</code>.
     *
     * @param x the <code>int</code> value to be printed
     */
    public void println(int x) {
        this.addInt(x).addLine();

    }

    /**
     * Prints a long integer and then terminates the line.  This method behaves
     * as though it invokes <code>{@link #print(long)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>long</code> value to be printed
     */
    public void println(long x) {

        this.addLong(x).addLine();
    }

    /**
     * Prints a floating-point number and then terminates the line.  This method
     * behaves as though it invokes <code>{@link #print(float)}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the <code>float</code> value to be printed
     */
    public void println(float x) {
        add(x).addLine();
    }

    /**
     * Prints a double-precision floating-point number and then terminates the
     * line.  This method behaves as though it invokes <code>{@link
     * #print(double)}</code> and then <code>{@link #println()}</code>.
     *
     * @param x the <code>double</code> value to be printed
     */
    public void println(double x) {
        add(x).addLine();
    }

    /**
     * Prints an array of characters and then terminates the line.  This method
     * behaves as though it invokes <code>{@link #print(char[])}</code> and then
     * <code>{@link #println()}</code>.
     *
     * @param x the array of <code>char</code> values to be printed
     */
    public void println(char x[]) {
        add(x).addLine();

    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     *
     * <p> An invocation of this method of the form <tt>out.printf(format,
     * args)</tt> behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.format(format, args) </pre>
     *
     * @param  format
     *         A format string as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     *
     * @throws  NullPointerException
     *          If the <tt>format</tt> is <tt>null</tt>
     *
     * @return  This writer
     *
     */
    public PrintWriter printf(String format, Object ... args) {
        this.addLine(String.format(format, args));
        return this;
    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     *
     * <p> An invocation of this method of the form <tt>out.printf(l, format,
     * args)</tt> behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.format(l, format, args) </pre>
     *
     * @param  l
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
     *         is applied.
     *
     * @param  format
     *         A format string as described in <a
     *         href="../util/Formatter.html#syntax">Format string syntax</a>.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         <tt>null</tt> argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     *
     * @throws  NullPointerException
     *          If the <tt>format</tt> is <tt>null</tt>
     *
     * @return  This writer
     *
     * @since  1.5
     */
    public PrintWriter printf(Locale l, String format, Object ... args) {

        this.addLine(String.format(l, format, args));
        return this;

    }

    public void display() {

        System.out.println(this.toString());
    }
}

