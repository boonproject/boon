package org.boon.json.implementation;

import org.boon.core.reflection.Reflection;
import org.boon.json.JsonException;
import org.boon.json.internal.*;
import org.boon.json.internal.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * This works in index overlay mode or chop mode.
 * Chop mode reduces possibility of memory leak but causes a few more buffer copies as it chops up the buffer.
 */
public class JsonFastParser extends JsonParserCharArray {


    private static ValueBase EMPTY_LIST = new ValueBase ( Collections.EMPTY_LIST );


    private final boolean useValues;
    private final boolean chop;
    private final boolean lazyChop;

    public JsonFastParser() {
        useValues = false;
        chop = false;
        lazyChop = true;

    }


    public JsonFastParser( boolean useValues ) {
        this.useValues = useValues;
        chop = false;
        lazyChop = true;
    }


    public JsonFastParser( boolean useValues, boolean chop ) {
        this.useValues = useValues;
        this.chop = chop;
        lazyChop = !chop;

    }


    public JsonFastParser( boolean useValues, boolean chop, boolean lazyChop ) {
        this.useValues = useValues;
        this.chop = chop;
        this.lazyChop = lazyChop;

    }


    protected final Value decodeJsonObjectLazyFinalParse() {

        char[] array = charArray;

        if ( __currentChar == '{' )
            __index++;

        JsonMap map = null;
        JsonValueMap valueMap = null;
        Value value;
        if ( useValues ) {
            valueMap = new JsonValueMap ();
            value = new ValueBase ( ( Map ) valueMap );
        } else {
            map = new JsonMap ( lazyChop );
            value = new ValueBase ( map );
        }

        objectLoop:
        for (; __index < array.length; __index++ ) {
            skipWhiteSpace ();
            switch ( __currentChar ) {

                case '"':
                    Value key = decodeStringOverlay ();
                    skipWhiteSpace ();

                    if ( __currentChar != ':' ) {

                        complain ( "expecting current character to be " + charDescription ( __currentChar ) + "\n" );
                    }
                    __index++;

                    Value item = decodeValueOverlay ();

                    skipWhiteSpace ();


                    MapItemValue miv = new MapItemValue ( key, item );


                    if ( useValues ) {
                        valueMap.add ( miv );
                    } else {
                        map.add ( miv );
                    }
            }

            switch ( __currentChar ) {
                case '}':
                    __index++;
                    break objectLoop;

                case ',':
                    continue;

                default:

                    complain (
                            "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );

            }
        }
        return value;
    }

    protected Value decodeValue() {
        return decodeValueOverlay ();
    }

    private Value decodeValueOverlay() {
        skipWhiteSpace ();

        __currentChar = charArray[__index];

        switch ( __currentChar ) {

            case '"':
                return decodeStringOverlay ();

            case '{':
                return decodeJsonObjectLazyFinalParse ();

            case 't':
                return decodeTrue () == true ? Value.TRUE : Value.FALSE;

            case 'f':
                return decodeFalse () == false ? Value.FALSE : Value.TRUE;

            case 'n':
                return decodeNull () == null ? ValueBase.NULL : Value.NULL;

            case '[':
                return decodeJsonArrayOverlay ();


            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
            case '-':
                return decodeNumberOverlay ();


            default:

                throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                        "current character, it is not a string, number, array, or object" ) );


        }
    }


    private static final boolean isNumberDigit (char c)  {
        switch ( c ) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                case '-':
                return true;
            default:
                return false;
        }

    }

    private static final int  first8Nums(int index, char [] array) {
        if ( index + 8 < array.length && isNumberDigit ( array[index] ) ) {
            if ( isNumberDigit ( array[++index] ) ) {
                if ( isNumberDigit ( array[++index] ) ) {
                    if ( isNumberDigit ( array[++index] ) ) {
                        if ( isNumberDigit ( array[++index] ) ) {
                            if ( isNumberDigit ( array[++index] ) ) {
                                if ( isNumberDigit ( array[++index] ) ) {
                                    ++index ;
                                }
                            }
                        }
                    }
                }
            }
        }
       return index;
    }

    private final Value decodeNumberOverlay() {

        char[] array = charArray;
        int index = __index;
        char currentChar;
        final int startIndex = index;
        boolean doubleFloat = false;
        index = first8Nums ( index, array );


        loop:
        for (; index < array.length; index++ ) {
            currentChar = array[index];

            switch ( currentChar ) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                case ',':
                case ']':
                case '}':
                    __index = index + 1;
                    break loop;

                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                case '-':
                    continue loop;


                case '+':
                case 'e':
                case 'E':
                case '.':
                    doubleFloat = true;
                    continue loop;

            }

            __index = index;
            __currentChar = currentChar;
            complain ( "expecting number char but got current char " + charDescription ( currentChar ) );
        }

        __index = index;

        Type type = doubleFloat ? Type.DOUBLE : Type.INTEGER;

        ValueInCharBuf value = new ValueInCharBuf ( chop, type, startIndex, __index, this.charArray );

        skipWhiteSpace ();

        return value;

    }


    private static final boolean isCharOk (char c) {
        switch ( c ) {
            case '"':
            case '\\':
                return false;
            default:
                return true;
        }
    }

    private static final int first8 (int index, char [] array) {
        if ( index + 8 < array.length && isCharOk ( array[index] ) ) {
            if (isCharOk ( array[++index] ))  {
                if (isCharOk ( array[++index] ))  {
                    if (isCharOk ( array[++index] ))  {
                        if (isCharOk ( array[++index] ))  {
                            if (isCharOk ( array[++index] ))  {
                                if (isCharOk ( array[++index] ))  {
                                    if (isCharOk ( array[++index] ))  {
                                        index++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return index;
    }

    private Value decodeStringOverlay() {

        char[] array = charArray;
        int index = __index;
        char currentChar = charArray[index];

        if ( index < array.length && currentChar == '"' ) {
            index++;
        }

        final int startIndex = index;

        //index = first8 ( index, array );


        boolean escape = false;

        boolean encoded = false;

        done:
        for (; index < array.length; index++ ) {
            currentChar = array[index];
            switch ( currentChar ) {

                case '"':
                    if ( !escape ) {
                        break done;
                    } else {
                        escape = false;
                        continue;
                    }


                case '\\':
                    encoded = true;
                    escape = true;
                    continue;

            }
            escape = false;
        }

        Value value = new ValueInCharBuf ( chop, Type.STRING, startIndex, index, array, encoded, true );


        if ( index < array.length ) {
            index++;
        }

        __index = index;
        return value;
    }

    private Value decodeJsonArrayOverlay() {

        char [] array = charArray;
        if ( __currentChar == '[' ) {
            this.nextChar ();
        }

        skipWhiteSpace ();




        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            this.nextChar ();
            return EMPTY_LIST;
        }


        List<Object> list;

        if ( useValues ) {
            list = new ArrayList<> ();
        } else {
            list = new JsonList ( lazyChop );
        }

        Value value = new ValueBase ( list );


        Value item;

        arrayLoop:
        for ( ;__index < array.length; __index++) {
            item = decodeValueOverlay ();

            list.add ( item );


            skipWhiteSpace ();

            switch (__currentChar) {
                case ',':
                    continue;
                case ']':
                    __index++;
                    break arrayLoop;
                default:
                    complain (
                        String.format ( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array size of %s \n", charDescription ( __currentChar ), list.size () )
                );

            }
        }
        return value;
    }


    protected <T> T convert( Class<T> type, T object ) {
        if ( type == Map.class || type == List.class ) {
            return object;
        } else {
            if ( object instanceof Map ) {
                return Reflection.fromValueMap ( ( Map<String, org.boon.core.Value> ) object, type );
            } else {
                return object;
            }
        }
    }


    protected final Object decodeFromChars( char[] cs ) {
        return ( ( Value ) super.decodeFromChars ( cs ) ).toValue ();
    }


}