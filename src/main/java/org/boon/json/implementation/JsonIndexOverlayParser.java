package org.boon.json.implementation;

import org.boon.json.JsonException;
import org.boon.json.internal.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Converts an input JSON String into Java objects works with String or char array
 * as input. Produces an Object which can be any of the basic JSON types mapped
 * to Java.
 */
public class JsonIndexOverlayParser extends JsonParserCharArray {


    private static ValueBase EMPTY_LIST = new ValueBase ( Collections.EMPTY_LIST );


    private final boolean useValues;

    public JsonIndexOverlayParser () {
        useValues = false;

    }


    public JsonIndexOverlayParser ( boolean useValues ) {
        this.useValues = useValues;

    }


    protected final Object decodeFromChars ( char[] cs ) {
        return ( ( Value ) super.decodeFromChars ( cs ) ).toValue ();
    }


    protected final Value decodeJsonObjectOverlay () {

        if ( __currentChar == '{' )
            this.nextChar ();

        JsonMap map = null;
        JsonValueMap valueMap = null;
        Value value;
        if ( useValues ) {
            valueMap = new JsonValueMap ();
            value = new ValueBase ( ( Map ) valueMap );
        } else {
            map = new JsonMap ();
            value = new ValueBase ( map );
        }


        for (; __index < this.charArray.length; __index++ ) {

            skipWhiteSpace ();


            if ( __currentChar == '"' ) {
                Value key = decodeStringOverlay ();
                skipWhiteSpace ();

                if ( __currentChar != ':' ) {

                    complain ( "expecting current character to be " + charDescription ( __currentChar ) + "\n" );
                }
                this.nextChar (); // skip past ':'

                Value item = decodeValueOverlay ();

                skipWhiteSpace ();


                MapItemValue miv = new MapItemValue ( key, item );


                if ( useValues ) {
                    valueMap.add ( miv );
                } else {
                    map.add ( miv );
                }


            }
            if ( __currentChar == '}' ) {
                __index++;
                break;
            } else if ( __currentChar == ',' ) {
                continue;
            } else {
                complain (
                        "expecting '}' or ',' but got current char " + charDescription ( __currentChar ) );

            }
        }
        return value;
    }

    protected Value decodeValue () {
        return decodeValueOverlay ();
    }

    private Value decodeValueOverlay () {
        skipWhiteSpace ();

        label:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];

            switch ( __currentChar ) {

                case '\n':
                    continue label;

                case '\r':
                    continue label;

                case ' ':
                    continue label;

                case '\t':
                    continue label;


                case '"':
                    return decodeStringOverlay ();


                case 't':
                    return decodeTrue () == true ? Value.TRUE : Value.FALSE;

                case 'f':
                    return decodeFalse () == false ? Value.FALSE : Value.TRUE;


                case 'n':
                    return decodeNull () == null ? ValueBase.NULL : Value.NULL;

                case '[':
                    return decodeJsonArrayOverlay ();

                case '{':
                    return decodeJsonObjectOverlay ();

                case '1':
                    return decodeNumberOverlay ();

                case '2':
                    return decodeNumberOverlay ();

                case '3':
                    return decodeNumberOverlay ();

                case '4':
                    return decodeNumberOverlay ();

                case '5':
                    return decodeNumberOverlay ();

                case '6':
                    return decodeNumberOverlay ();

                case '7':
                    return decodeNumberOverlay ();

                case '8':
                    return decodeNumberOverlay ();

                case '9':
                    return decodeNumberOverlay ();

                case '0':
                    return decodeNumberOverlay ();

                case '-':
                    return decodeNumberOverlay ();


                default:

                    throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                            "current character, it is not a string, number, array, or object" ) );


            }
        }

        throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                "current character, it is not a string, number, array, or object" ) );

    }


    private Value decodeNumberOverlay () {

        int startIndex = __index;

        boolean doubleFloat = false;

        int index;

        loop:
        for ( index = __index; index < charArray.length; index++ ) {
            __currentChar = charArray[ index ];

            switch ( __currentChar ) {
                case ' ':
                    __index = index + 1;
                    break loop;

                case '\t':
                    __index = index + 1;
                    break loop;

                case '\n':
                    __index = index + 1;
                    break loop;

                case '\r':
                    __index = index + 1;
                    break loop;

                case ',':
                    break loop;

                case ']':
                    break loop;

                case '}':
                    break loop;

                case '1':
                    continue loop;

                case '2':
                    continue loop;

                case '3':
                    continue loop;

                case '4':
                    continue loop;

                case '5':
                    continue loop;

                case '6':
                    continue loop;

                case '7':
                    continue loop;

                case '8':
                    continue loop;

                case '9':
                    continue loop;

                case '0':
                    continue loop;

                case '-':
                    continue loop;


                case '+':
                    doubleFloat = true;
                    continue loop;

                case 'e':
                    doubleFloat = true;
                    continue loop;

                case 'E':
                    doubleFloat = true;
                    continue loop;

                case '.':
                    doubleFloat = true;
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( __currentChar ) );
        }

        __index = index;

        ValueInCharBuf value = new ValueInCharBuf ();
        value.buffer = this.charArray;
        value.startIndex = startIndex;
        value.endIndex = __index;

        if ( doubleFloat ) {
            value.type = Type.DOUBLE;
        } else {
            value.type = Type.INTEGER;
        }

        skipWhiteSpace ();

        return value;

    }


    private Value decodeStringOverlay () {
        ValueInCharBuf value = new ValueInCharBuf ( Type.STRING );


        __currentChar = charArray[ __index ];

        if ( __index < charArray.length && __currentChar == '"' ) {
            __index++;
        }

        final int startIndex = __index;


        boolean escape = false;

        boolean encoded = false;

        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];
            switch ( __currentChar ) {

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

        value.startIndex = startIndex;
        value.endIndex = __index;
        value.buffer = charArray;
        value.decodeStrings = encoded;

        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }


    private Value decodeJsonArrayOverlay () {
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
            list = new JsonList ();
        }

        Value value = new ValueBase ( list );

        int arrayIndex = 0;

        do {
            Value arrayItem = decodeValueOverlay ();

            if ( arrayItem == null ) {
                list.add ( ValueBase.NULL ); //JSON null detected
            } else {
                list.add ( arrayItem );
            }

            arrayIndex++;

            skipWhiteSpace ();

            char c = __currentChar;

            if ( c == ',' ) {
                this.nextChar ();
                continue;
            } else if ( c == ']' ) {
                this.nextChar ();
                break;
            } else {
                String charString = charDescription ( c );

                complain (
                        String.format ( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, arrayIndex )
                );

            }
        } while ( this.hasMore () );
        return value;
    }


}