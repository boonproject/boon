package org.boon.json.implementation;


import org.boon.json.internal.*;

import java.util.*;


/**
 * Created by rick on 12/12/13.
 */
public class JsonParserLax extends JsonParserCharArray {


    private final char OBJECT_ITEM_DELIMITER_TOKEN;

    private final char START_ARRAY_TOKEN;


    private final char END_ARRAY_TOKEN;


    private final char KEY_ASSIGNMENT_OPERATOR;


    private static ValueBase EMPTY_LIST = new ValueBase ( Collections.EMPTY_LIST );


    private final boolean useValues;
    private final boolean chop;
    private final boolean lazyChop;


    public JsonParserLax () {
        useValues = false;
        chop = false;
        lazyChop = true;
        OBJECT_ITEM_DELIMITER_TOKEN = ',';
        START_ARRAY_TOKEN = '[';
        END_ARRAY_TOKEN = ']';
        KEY_ASSIGNMENT_OPERATOR = ':';


    }


    public JsonParserLax ( boolean useValues ) {
        this.useValues = useValues;
        chop = false;
        lazyChop = true;
        OBJECT_ITEM_DELIMITER_TOKEN = ',';
        START_ARRAY_TOKEN = '[';
        END_ARRAY_TOKEN = ']';
        KEY_ASSIGNMENT_OPERATOR = ':';

    }


    public JsonParserLax ( boolean useValues, boolean chop ) {
        this.useValues = useValues;
        this.chop = chop;
        lazyChop = !chop;
        OBJECT_ITEM_DELIMITER_TOKEN = ',';
        START_ARRAY_TOKEN = '[';
        END_ARRAY_TOKEN = ']';
        KEY_ASSIGNMENT_OPERATOR = ':';

    }


    public JsonParserLax ( boolean useValues, boolean chop, boolean lazyChop ) {
        this.useValues = useValues;
        this.chop = chop;
        this.lazyChop = lazyChop;
        OBJECT_ITEM_DELIMITER_TOKEN = ',';
        START_ARRAY_TOKEN = '[';
        END_ARRAY_TOKEN = ']';
        KEY_ASSIGNMENT_OPERATOR = ':';

    }


    public JsonParserLax ( boolean useValues, boolean chop, boolean lazyChop, boolean plist ) {
        this.useValues = useValues;
        this.chop = chop;
        this.lazyChop = lazyChop;


        if ( plist ) {
            OBJECT_ITEM_DELIMITER_TOKEN = ';';
            START_ARRAY_TOKEN = '(';
            END_ARRAY_TOKEN = ')';

            KEY_ASSIGNMENT_OPERATOR = '=';

        } else {
            OBJECT_ITEM_DELIMITER_TOKEN = ',';
            START_ARRAY_TOKEN = '[';
            END_ARRAY_TOKEN = ']';
            KEY_ASSIGNMENT_OPERATOR = ':';
        }


    }

    protected final Object decodeFromChars ( char[] cs ) {
        return ( ( Value ) super.decodeFromChars ( cs ) ).toValue ();
    }



    protected void init () {
        super.init ();
    }


    private Value decodeJsonObjectLax () {

        if ( __currentChar == '{' )
            this.nextChar ();

        JsonMap map = null;
        JsonValueMap valueMap = null;
        org.boon.json.internal.Value value;
        if ( useValues ) {
            valueMap = new JsonValueMap ();
            value = new ValueBase ( ( Map ) valueMap );
        } else {
            map = new JsonMap ( lazyChop );
            value = new ValueBase ( map );
        }


        skipWhiteSpace ();
        int startIndexOfKey = __index;
        Value key;
        MapItemValue miv;
        Value item;

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

                    if ( startChar == OBJECT_ITEM_DELIMITER_TOKEN ) {
                        startIndexOfKey++;
                    }

                    key = new ValueInCharBuf ( chop, Type.STRING, startIndexOfKey, __index - 1, this.charArray, false, false );


                    __index++; //skip :

                    item = ( Value ) decodeValueInternal ();
                    skipWhiteSpace ();

                    miv = new MapItemValue ( key, item );


                    if ( useValues ) {
                        valueMap.add ( miv );
                    } else {
                        map.add ( miv );
                    }
                    //puts ( "key no quote", "#" + key + "#", value );

                    startIndexOfKey = __index;
                    if ( __currentChar == '}' ) {
                        __index++;
                        break done;
                    }

                    break;

                case '\'':
                case '"':
                    key = decodeString ( __currentChar );

                    //puts ( "key with quote", key );

                    skipWhiteSpace ();

                    if ( __currentChar != KEY_ASSIGNMENT_OPERATOR ) {

                        complain ( "expecting current character to be " + KEY_ASSIGNMENT_OPERATOR + " but got " + charDescription ( __currentChar ) + "\n" );
                    }
                    __index++;
                    item = decodeValueInternal ();

                    //puts ( "key", "#" + key + "#", value );

                    skipWhiteSpace ();

                    miv = new MapItemValue ( key, item );


                    if ( useValues ) {
                        valueMap.add ( miv );
                    } else {
                        map.add ( miv );
                    }
                    startIndexOfKey = __index;
                    if ( __currentChar == '}' ) {
                        __index++;
                        if ( hasMore () && OBJECT_ITEM_DELIMITER_TOKEN == ';' ) {
                            if ( charArray[ __index ] == ';' ) {
                                __index++;
                            }
                        }
                        break done;
                    }

                    break;


                case '}':
                    __index++;
                    if ( hasMore () && OBJECT_ITEM_DELIMITER_TOKEN == ';' ) {
                        if ( charArray[ __index ] == ';' ) {
                            __index++;
                        }
                    }
                    break done;

            }
        }

        return value;
    }


    protected Object decodeValue () {
        return this.decodeValueInternal ();
    }

    private Value decodeValueInternal () {
        Value value = null;


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
                        return decodeTrue () == true ? Value.TRUE : Value.FALSE;
                    } else {
                        value = decodeStringLax ();
                    }
                    break;

                case 'f':
                    if ( isFalse () ) {
                        return decodeFalse () == false ? Value.FALSE : Value.TRUE;
                    } else {
                        value = decodeStringLax ();
                    }
                    break;

                case 'n':
                    if ( isNull () ) {
                        return decodeNull () == null ? Value.NULL : Value.NULL;
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

    protected Value decodeNumberLax () {

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

        Type type = doubleFloat ? Type.DOUBLE : Type.INTEGER;

        ValueInCharBuf value = new ValueInCharBuf ( chop, type, startIndex, __index, this.charArray );

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

    private Value decodeStringLax () {

        __currentChar = charArray[ __index ];

        if ( __index < charArray.length && __currentChar == '"' ) {
            __index++;

        }


        final int startIndex = __index;


        boolean escape = false;
        boolean encoded = false;
        boolean skip = false;

        int minusCount = 0;
        int colonCount = 0;


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
                    if ( __currentChar == END_ARRAY_TOKEN ) {

                        skip = false;
                        break done;
                    }

                case '}':
                    skip = false;
                    break done;


                case ';':
                case ',':
                    if ( __currentChar == OBJECT_ITEM_DELIMITER_TOKEN || __currentChar == ',' ) {
                        skip = false;
                        break done;
                    } else {
                        complain ( "unexpected token " + __currentChar );
                    }

                case '\\':
                    encoded = true;
                    escape = true;
                    continue;


                case '-':
                    minusCount++;
                    break;
                case ':':
                    colonCount++;
                    break;


            }
            escape = false;
        }


        boolean checkDate = !encoded && minusCount >= 2 && colonCount >= 2;


        Value value = new ValueInCharBuf ( chop, Type.STRING, startIndex, __index - 1, this.charArray, encoded, checkDate );


        if ( __index < charArray.length && skip ) {
            __index++;
        }

        return value;

    }


    private Value decodeString ( final char terminator ) {

        __currentChar = charArray[ __index ];

        if ( __index < charArray.length && __currentChar == terminator ) {
            __index++;

        }


        final int startIndex = __index;


        boolean escape = false;
        boolean encoded = false;
        int minusCount = 0;
        int colonCount = 0;


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
                    encoded = true;
                    escape = true;
                    continue;

                case '-':
                    minusCount++;
                    break;
                case ':':
                    colonCount++;
                    break;

            }
            escape = false;
        }


        boolean checkDate = !encoded && minusCount >= 2 && colonCount >= 2;


        Value value = new ValueInCharBuf ( chop, Type.STRING, startIndex, __index - 1, this.charArray, encoded, checkDate );


        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }

    private Value decodeJsonArrayLax () {

        if ( __currentChar == START_ARRAY_TOKEN ) {
            this.nextChar ();
        }


        skipWhiteSpace ();


                /* the list might be empty  */
        if ( __currentChar == END_ARRAY_TOKEN ) {
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


        return value;

    }


}