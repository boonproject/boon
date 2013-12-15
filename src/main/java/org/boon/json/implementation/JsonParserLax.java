package org.boon.json.implementation;

import org.boon.Dates;

import org.boon.json.internal.JsonLazyLinkedMap;
import org.boon.primitive.CharScanner;
import org.boon.primitive.Chr;

import java.util.*;


import static org.boon.primitive.CharScanner.isInteger;
import static org.boon.primitive.CharScanner.parseInt;
import static org.boon.primitive.CharScanner.parseLong;

/**
 * Created by rick on 12/12/13.
 */
public class JsonParserLax extends JsonParserCharArray  {



    boolean inList;
    boolean inObject;



    private final char OBJECT_ITEM_DELIMETER_TOKEN;

    private final char START_ARRAY_TOKEN;


    private final char END_ARRAY_TOKEN;


    private final char KEY_ASSIGNMENT_OPERATOR;



    public JsonParserLax () {
        OBJECT_ITEM_DELIMETER_TOKEN = ',';
        START_ARRAY_TOKEN = '[';
        END_ARRAY_TOKEN = ']';
        KEY_ASSIGNMENT_OPERATOR = ':';

    }


    public JsonParserLax ( boolean plist ) {

        if ( plist ) {
            OBJECT_ITEM_DELIMETER_TOKEN = ';';
            START_ARRAY_TOKEN = '(';
            END_ARRAY_TOKEN = ')';

            KEY_ASSIGNMENT_OPERATOR = '=';

        } else {
            OBJECT_ITEM_DELIMETER_TOKEN = ',';
            START_ARRAY_TOKEN = '[';
            END_ARRAY_TOKEN = ']';
            KEY_ASSIGNMENT_OPERATOR = ':';
        }


    }



    protected void  init() {
        super.init ();
        inList = false;
        inObject = false;
    }


    private Object decodeJsonObjectLax () {

        if ( __currentChar == '{' )
            this.nextChar ();

        JsonLazyLinkedMap  map = null;
        if (heavyCache) {
            map  = createMap ();
        } else {
            map = new JsonLazyLinkedMap (  );
        }


        inObject = true;


        skipWhiteSpace ();
        int startIndexOfKey = __index;
        String key;
        Object value;

        done:
        for (; __index < this.charArray.length; __index++ ) {

            skipWhiteSpace ();

            switch ( __currentChar ) {
                case '/': /* */ //
                    handleComment ();
                    startIndexOfKey = __index;
                    break;

                case '#':
                    handleBashComment ();
                    startIndexOfKey = __index;
                    break;

                case '=':
                case ':':
                    if ( __currentChar != KEY_ASSIGNMENT_OPERATOR ) {
                        continue;
                    }

                    char startChar = charArray[ startIndexOfKey ];

                    if ( startChar == OBJECT_ITEM_DELIMETER_TOKEN ) {
                        startIndexOfKey++;
                    }

                    char[] chars = Chr.trim ( charArray, startIndexOfKey, __index );
                    key = new String ( chars );

                    //puts ( "key no quote", "#" + key + "#" );
                    if ( internKeys ) {
                        key = key.intern ();
                    }

                    __index++; //skip :

                    value = decodeValueInternal ();
                    skipWhiteSpace ();
                    map.put ( key, value );
                    //puts ( "key no quote", "#" + key + "#", value );

                    startIndexOfKey = __index;
                    if ( __currentChar == '}' ) {
                        __index++;
                        break done;
                    }

                    break;

                case '\'':
                case '"':
                    key = ( String ) decodeString ( __currentChar );

                    //puts ( "key with quote", key );

                    skipWhiteSpace ();

                    if ( __currentChar != KEY_ASSIGNMENT_OPERATOR ) {

                        complain ( "expecting current character to be " + KEY_ASSIGNMENT_OPERATOR + " but got " + charDescription ( __currentChar ) + "\n" );
                    }
                    __index++;
                    value = decodeValueInternal ();

                    //puts ( "key", "#" + key + "#", value );

                    skipWhiteSpace ();
                    map.put ( key, value );
                    startIndexOfKey = __index;
                    if ( __currentChar == '}' ) {
                        __index++;
                        if ( hasMore () && OBJECT_ITEM_DELIMETER_TOKEN == ';' ) {
                            if ( charArray[ __index ] == ';' ) {
                                __index++;
                            }
                        }
                        break done;
                    }

                    break;


                case '}':
                    __index++;
                    if ( hasMore () && OBJECT_ITEM_DELIMETER_TOKEN == ';' ) {
                        if ( charArray[ __index ] == ';' ) {
                            __index++;
                        }
                    }
                    break done;

            }
        }


        inObject = false;

        if (heavyCache) {
            return prepareMap ( map );
        } else {
            return map;
        }
    }


    protected Object decodeValue () {
        return this.decodeValueInternal ();
    }

    private Object decodeValueInternal () {
        Object value = null;


        for (; __index < charArray.length; __index++ ) {
            skipWhiteSpace ();


            switch ( __currentChar ) {
                case '\n':
                    break;

                case '\r':
                    break;

                case ' ':
                    break;

                case '\t':
                    break;

                case '\b':
                    break;

                case '\f':
                    break;

                case '/': /* */ //
                    handleComment ();
                    break;

                case '#':
                    handleBashComment ();
                    break;

                case '"':
                    value = decodeString ( '"' );
                    break;

                case '\'':
                    value = decodeString ( '\'' );
                    break;


                case 't':
                    if ( isTrue () ) {
                        value = decodeTrue ();
                    } else {
                        value = decodeStringLax ();
                    }
                    break;

                case 'f':
                    if ( isFalse () ) {
                        value = decodeFalse ();
                    } else {
                        value = decodeStringLax ();
                    }
                    break;

                case 'n':
                    if ( isNull () ) {
                        value = decodeNull ();
                        return value;
                    } else {
                        value = decodeStringLax ();
                    }

                    break;

                case '(':
                case '[':
                    value = decodeJsonArrayLax ();
                    break;

                case '{':
                    value = decodeJsonObjectLax ();
                    break;

                case '1':
                    value = decodeNumberLax ();
                    break;

                case '2':
                    value = decodeNumberLax ();
                    break;

                case '3':
                    value = decodeNumberLax ();
                    break;

                case '4':
                    value = decodeNumberLax ();
                    break;

                case '5':
                    value = decodeNumberLax ();
                    break;

                case '6':
                    value = decodeNumberLax ();
                    break;

                case '7':
                    value = decodeNumberLax ();
                    break;

                case '8':
                    value = decodeNumberLax ();
                    break;

                case '9':
                    value = decodeNumberLax ();
                    break;

                case '0':
                    value = decodeNumberLax ();
                    break;

                case '-':
                    value = decodeNumberLax ();
                    break;

                default:
                    value = decodeStringLax ();

            }

            if ( value != null ) {
                return value;
            }
        }

        return null;
    }

    private void handleBashComment () {
        for (; __index < charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];

            if ( __currentChar == '\n' ) {
                return;
            }
        }
    }

    private void handleComment () {


        if ( hasMore () ) {

            __index++;
            __currentChar = charArray[ __index ];

            switch ( __currentChar ) {
                case '*':
                    for (; __index < charArray.length; __index++ ) {
                        __currentChar = charArray[ __index ];

                        if ( __currentChar == '*' ) {
                            if ( hasMore () ) {
                                __index++;
                                __currentChar = charArray[ __index ];
                                if ( __currentChar == '/' ) {
                                    if ( hasMore () ) {
                                        __index++;
                                        return;
                                    }
                                }
                            } else {
                                complain ( "missing close of comment" );
                            }
                        }


                    }

                case '/':
                    for (; __index < charArray.length; __index++ ) {
                        __currentChar = charArray[ __index ];

                        if ( __currentChar == '\n' ) {
                            return;
                        }
                    }
            }

        }


    }

    protected Object decodeNumberLax () {


        boolean doubleFloat = false;

        boolean minus = false;

        boolean simple = true;

        int sign = 1;

        int index;

        int digitsPastPoint = 0;


        __currentChar = charArray[ __index ];

        if ( __currentChar == '-' ) {
            minus = true;
            __index++;
            sign = -1;
        }

        int startIndex = __index;


        loop:
        for ( index = __index; index < charArray.length; index++ ) {
            __currentChar = charArray[ index ];

            if ( doubleFloat ) {
                digitsPastPoint++;
            }


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

                case ';':
                case ',':
                        break loop;

                case ')':
                case ']':
                    if ( __currentChar == END_ARRAY_TOKEN ) {
                        break loop;
                    } else {
                        complain ( "unexpected token " + __currentChar );
                    }

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

                    simple = false;
                    doubleFloat = true;
                    continue loop;

                case 'e':
                    simple = false;
                    doubleFloat = true;
                    continue loop;

                case 'E':

                    simple = false;
                    doubleFloat = true;
                    continue loop;

                case '.':
                    doubleFloat = true;
                    continue loop;

            }

            complain ( "expecting number char but got current char " + charDescription ( __currentChar ) );
        }

        __index = index;


        Object value;
        if ( doubleFloat ) {
            value = CharScanner.simpleDouble ( this.charArray, simple, minus, digitsPastPoint - 1, startIndex, __index );
        } else {

            if ( isInteger ( this.charArray, startIndex, __index - startIndex, minus ) ) {
                value = parseInt ( charArray, startIndex, __index - startIndex ) * sign;
            } else {
                value = parseLong ( charArray, startIndex, __index - startIndex ) * sign;
            }

        }

        skipWhiteSpace ();

        return value;

    }


    private boolean isNull () {

        if ( __index + NULL.length <= charArray.length ) {
            if ( charArray[ __index ] == 'n' &&
                    charArray[ __index + 1 ] == 'u' &&
                    charArray[ __index + 2 ] == 'l' &&
                    charArray[ __index + 3 ] == 'l' ) {
                return true;
            }
        }
        return false;
    }


    private boolean isTrue () {

        if ( __index + TRUE.length <= charArray.length ) {
            if ( charArray[ __index ] == 't' &&
                    charArray[ __index + 1 ] == 'r' &&
                    charArray[ __index + 2 ] == 'u' &&
                    charArray[ __index + 3 ] == 'e' ) {
                return true;

            }
        }

        return false;
    }



    private boolean isFalse () {

        if ( __index + FALSE.length <= charArray.length ) {
            if ( charArray[ __index ] == 'f' &&
                    charArray[ __index + 1 ] == 'a' &&
                    charArray[ __index + 2 ] == 'l' &&
                    charArray[ __index + 3 ] == 's' &&
                    charArray[ __index + 4 ] == 'e' ) {
                return true;
            }
        }
        return false;
    }

    private Object decodeStringLax () {

        __currentChar = charArray[ __index ];

        if ( __index < charArray.length && __currentChar == '"' ) {
            __index++;

        }


        final int startIndex = __index;


        boolean escape = false;
        boolean hasEscaped = false;
        boolean skip = false;


        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];
            switch ( __currentChar ) {

                case '"':
                    if ( !escape ) {
                        skip = true;
                        break done;
                    } else {
                        escape = false;
                        continue;
                    }

                case ']':
                case ')':
                    if ( __currentChar ==  END_ARRAY_TOKEN ) {

                        skip = false;
                        break done;
                    }

                case '}':
                        skip = false;
                        break done;


                case ';':
                case ',':
                    if ( __currentChar == OBJECT_ITEM_DELIMETER_TOKEN || __currentChar == ',' ) {
                            skip = false;
                            break done;
                    } else {
                        complain ( "unexpected token " + __currentChar );
                    }

                case '\\':
                    hasEscaped = true;
                    escape = true;
                    continue;

            }
            escape = false;
        }


        Object value = null;
        if ( hasEscaped ) {
            final char[] chars = Chr.trim ( charArray, startIndex, __index );
            value = JsonStringDecoder.decodeForSure ( chars );
        } else {
            final char[] chars = Chr.trim ( charArray, startIndex, __index );
            value = new String ( chars );
        }

        if ( __index < charArray.length && skip ) {
            __index++;
        }

        return value;
    }


    private Object decodeString ( final char terminator ) {

        __currentChar = charArray[ __index ];

        if ( __index < charArray.length && __currentChar == terminator ) {
            __index++;

        }


        final int startIndex = __index;


        boolean escape = false;
        boolean hasEscaped = false;

        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];
            switch ( __currentChar ) {

                case '\'':
                case '"':
                    if ( terminator == __currentChar ) {
                        if ( !escape ) {
                            break done;
                        } else {
                            escape = false;
                            continue;
                        }
                    }


                case '\\':
                    hasEscaped = true;
                    escape = true;
                    continue;

            }
            escape = false;
        }

        Object value = null;
        if ( hasEscaped ) {
            value = JsonStringDecoder.decodeForSure ( charArray, startIndex, __index );
        } else {

            if ( Dates.isISO8601QuickCheck ( charArray, startIndex, __index ) ) {
                value = Dates.fromISO8601DateLoose ( charArray, startIndex, __index );
            } else {

                value = new String ( charArray, startIndex, ( __index - startIndex ) );

            }

        }

        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }

    private List decodeJsonArrayLax () {

        if ( __currentChar == START_ARRAY_TOKEN ) {
            this.nextChar ();
        }


        inList = true;

        skipWhiteSpace ();


                /* the list might be empty  */
        if ( __currentChar == END_ARRAY_TOKEN ) {
            this.nextChar ();
            return Collections.EMPTY_LIST;
        }

        ArrayList<Object> list;

        if (heavyCache) {
            list = createList ();
        } else {
            list = new ArrayList (  );
        }


        skipWhiteSpace ();

        do {

            skipWhiteSpace ();

            Object arrayItem = decodeValueInternal ();

            list.add ( arrayItem );



            skipWhiteSpace ();

            char c = __currentChar;

            if ( c == ',' ) {
                this.nextChar ();
                continue;
            } else if ( c == END_ARRAY_TOKEN ) {
                this.nextChar ();
                break;
            } else {

                String charString = charDescription ( c );

                complain (
                        String.format ( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, list.size () )
                );

            }
        } while ( this.hasMore () );

        inList = false;


        if (heavyCache) {
            return prepareList ( list );
        }   else {
            return list;
        }
    }


}