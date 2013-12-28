package org.boon.json.implementation;


import org.boon.core.Typ;
import org.boon.core.Type;
import org.boon.core.Value;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.value.*;

import java.util.*;


/**
 * Created by rick on 12/12/13.
 */
public class JsonParserLax extends JsonParserCharArray {



    private static ValueBase EMPTY_LIST = new ValueBase( Collections.EMPTY_LIST );


    private final boolean useValues;
    private final boolean chop;
    private final boolean lazyChop;


    public JsonParserLax() {
        useValues = false;
        chop = false;
        lazyChop = true;


    }


    public JsonParserLax( boolean useValues ) {
        this.useValues = useValues;
        chop = false;
        lazyChop = true;

    }


    public JsonParserLax( boolean useValues, boolean chop ) {
        this.useValues = useValues;
        this.chop = chop;
        lazyChop = !chop;

    }


    public JsonParserLax( boolean useValues, boolean chop, boolean lazyChop ) {
        this.useValues = useValues;
        this.chop = chop;
        this.lazyChop = lazyChop;

    }


    private Value decodeJsonObjectLax() {

        if ( __currentChar == '{' )
            this.nextChar();

        ValueMap map =  useValues ? new ValueMapImpl () : new LazyValueMap ( lazyChop );
        Value value  = new ValueBase ( map );


        skipWhiteSpace();
        int startIndexOfKey = __index;
        Value key;
        MapItemValue miv;
        Value item;

        done:
        for (; __index < this.charArray.length; __index++ ) {

            skipWhiteSpace();

            switch ( __currentChar ) {
                case '/': /* */ //
                    handleComment();
                    startIndexOfKey = __index;
                    break;

                case '#':
                    handleBashComment();
                    startIndexOfKey = __index;
                    break;

                case ':':
                    char startChar = charArray[ startIndexOfKey ];
                    if ( startChar == ',' ) {
                        startIndexOfKey++;
                    }


                    key = extractLaxString( startIndexOfKey, __index - 1, false, false );
                    __index++; //skip :


                    item = decodeValueInternal();
                    skipWhiteSpace();

                    miv = new MapItemValue( key, item );

                    map.add( miv );

                    startIndexOfKey = __index;
                    if ( __currentChar == '}' ) {
                        __index++;
                        break done;
                    }

                    break;

                case '\'':
                    key = decodeStringSingle(  );

                    //puts ( "key with quote", key );

                    skipWhiteSpace();

                    if ( __currentChar != ':' ) {

                        complain( "expecting current character to be ':' but got " + charDescription( __currentChar ) + "\n" );
                    }
                    __index++;
                    item = decodeValueInternal();

                    //puts ( "key", "#" + key + "#", value );

                    skipWhiteSpace();

                    miv = new MapItemValue( key, item );


                    map.add( miv );
                    startIndexOfKey = __index;
                    if ( __currentChar == '}' ) {
                        __index++;
                        break done;
                    }

                    break;

                case '"':
                    key = decodeStringDouble(  );

                    //puts ( "key with quote", key );

                    skipWhiteSpace();

                    if ( __currentChar != ':' ) {

                        complain( "expecting current character to be ':' but got " + charDescription( __currentChar ) + "\n" );
                    }
                    __index++;
                    item = decodeValueInternal();

                    //puts ( "key", "#" + key + "#", value );

                    skipWhiteSpace();

                    miv = new MapItemValue( key, item );


                    map.add( miv );
                    startIndexOfKey = __index;
                    if ( __currentChar == '}' ) {
                        __index++;
                        break done;
                    }

                    break;


                case '}':
                    __index++;
                    break done;

            }
        }

        return value;
    }

    private Value extractLaxString( int startIndexOfKey, int end, boolean encoded, boolean checkDate ) {
        char startChar;
        startIndexLookup:
        for (; startIndexOfKey < __index && startIndexOfKey < charArray.length; startIndexOfKey++ ) {
            startChar = charArray[ startIndexOfKey ];
            switch ( startChar ) {
                case ' ':
                case '\n':
                case '\t':
                    continue;

                default:
                    break startIndexLookup;
            }
        }

        char endChar;
        int endIndex = end >= charArray.length ? charArray.length - 1 : end;
        endIndexLookup:
        for (; endIndex >= startIndexOfKey + 1 && endIndex >= 0; endIndex-- ) {
            endChar = charArray[ endIndex ];
            switch ( endChar ) {
                case ' ':
                case '\n':
                case '\t':
                case '}':
                    continue;
                case ',':
                case ';':
                    continue;

                case ']':
                     continue;
                default:
                    break endIndexLookup;
            }
        }
        return new ValueInCharBuf( chop, Type.STRING, startIndexOfKey, endIndex + 1, this.charArray, encoded, checkDate );
    }


    protected final Object decodeValue() {
        return this.decodeValueInternal();
    }

    private Value decodeValueInternal() {
        Value value = null;


        for (; __index < charArray.length; __index++ ) {
            skipWhiteSpace();


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
                    handleComment();
                    break;

                case '#':
                    handleBashComment();
                    break;

                case '"':
                    value = decodeStringDouble(  );
                    break;

                case '\'':
                    value = decodeStringSingle( );
                    break;


                case 't':
                    if ( isTrue() ) {
                        return decodeTrue() == true ? ValueBase.TRUE : ValueBase.FALSE;
                    } else {
                        value = decodeStringLax();
                    }
                    break;

                case 'f':
                    if ( isFalse() ) {
                        return decodeFalse() == false ? ValueBase.FALSE : ValueBase.TRUE;
                    } else {
                        value = decodeStringLax();
                    }
                    break;

                case 'n':
                    if ( isNull() ) {
                        return decodeNull() == null ? ValueBase.NULL : ValueBase.NULL;
                    } else {
                        value = decodeStringLax();
                    }

                    break;

                case '[':
                    value = decodeJsonArrayLax();
                    break;

                case '{':
                    value = decodeJsonObjectLax();
                    break;

                case '1':
                    value = decodeNumberLax();
                    break;

                case '2':
                    value = decodeNumberLax();
                    break;

                case '3':
                    value = decodeNumberLax();
                    break;

                case '4':
                    value = decodeNumberLax();
                    break;

                case '5':
                    value = decodeNumberLax();
                    break;

                case '6':
                    value = decodeNumberLax();
                    break;

                case '7':
                    value = decodeNumberLax();
                    break;

                case '8':
                    value = decodeNumberLax();
                    break;

                case '9':
                    value = decodeNumberLax();
                    break;

                case '0':
                    value = decodeNumberLax();
                    break;

                case '-':
                    value = decodeNumberLax();
                    break;


                default:
                    value = decodeStringLax();

            }

            if ( value != null ) {
                return value;
            }
        }

        return null;
    }

    private void handleBashComment() {
        for (; __index < charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];

            if ( __currentChar == '\n' ) {
                __index++;
                return;
            }
        }
    }

    private void handleComment() {


        if ( hasMore() ) {

            __index++;
            __currentChar = charArray[ __index ];

            switch ( __currentChar ) {
                case '*':
                    for (; __index < charArray.length; __index++ ) {
                        __currentChar = charArray[ __index ];

                        if ( __currentChar == '*' ) {
                            if ( hasMore() ) {
                                __index++;
                                __currentChar = charArray[ __index ];
                                if ( __currentChar == '/' ) {
                                    if ( hasMore() ) {
                                        __index++;
                                        return;
                                    }
                                }
                            } else {
                                complain( "missing close of comment" );
                            }
                        }


                    }

                case '/':
                    for (; __index < charArray.length; __index++ ) {
                        __currentChar = charArray[ __index ];

                        if ( __currentChar == '\n' ) {
                            if ( hasMore() ) {
                                __index++;
                                return;
                            } else {
                                return;
                            }
                        }
                    }
            }

        }


    }

    protected final Value decodeNumberLax() {


        char [] array = charArray;
        char currentChar;
        int index = __index;



        int startIndex = index;

        boolean doubleFloat = false;

        loop:
        for ( ; index < array.length; index++ ) {
            currentChar = array[ index ];

            switch ( currentChar ) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                case ',':
                case ']':
                case '}':
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

            __currentChar = currentChar;
            __index = index;
            complain( "expecting number char but got current char " + charDescription( currentChar ) );
        }


        Type type = doubleFloat ? Type.DOUBLE : Type.INTEGER;

        ValueInCharBuf value = new ValueInCharBuf( chop, type, startIndex, index, array );

        skipWhiteSpace();

        __index = index;

        return value;


    }


    private boolean isNull() {

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


    private boolean isTrue() {

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


    private boolean isFalse() {

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

    private Value decodeStringLax() {

        __currentChar = charArray[ __index ];


        final int startIndex = __index;


        boolean encoded = false;

        int minusCount = 0;
        int colonCount = 0;

        done:
        for (; __index < this.charArray.length; __index++ ) {
            __currentChar = charArray[ __index ];
            switch ( __currentChar ) {


                case ']':
                case '}':
                case ',':
                    break done;

                case '\\':
                    encoded = true;
                    continue;


                case '-':
                    minusCount++;
                    break;
                case ':':
                    colonCount++;
                    break;

            }
        }


        boolean checkDate = !encoded && minusCount >= 2 && colonCount >= 2;


        Value value = this.extractLaxString( startIndex, __index, encoded, checkDate );


        return value;

    }


    private Value decodeStringDouble(  ) {

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




        Value value = new ValueInCharBuf( chop, Type.STRING, startIndex, __index, this.charArray, encoded, true );


        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }

    private Value decodeStringSingle(  ) {

        __currentChar = charArray[ __index ];

        if ( __index < charArray.length && __currentChar == '\'' ) {
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


        Value value = new ValueInCharBuf( chop, Type.STRING, startIndex, __index, this.charArray, encoded, checkDate );


        if ( __index < charArray.length ) {
            __index++;
        }

        return value;
    }

    private Value decodeJsonArrayLax() {

        if ( __currentChar == '[' ) {
            __index++;
        }


        skipWhiteSpace();


        if ( __currentChar == ']' ) {
            __index++;
            return EMPTY_LIST;
        }

        List<Object> list;

        if ( useValues ) {
            list = new ArrayList<>();
        } else {
            list = new ValueList ( lazyChop );
        }

        Value value = new ValueBase( list );


        do {

            skipWhiteSpace();

            Object arrayItem = decodeValueInternal();

            list.add( arrayItem );


            skipWhiteSpace();

            char c = __currentChar;

            if ( c == ',' ) {
                __index++;
                continue;
            } else if ( c == ']' ) {
                __index++;
                break;
            } else {

                String charString = charDescription( c );

                complain(
                        String.format( "expecting a ',' or a ']', " +
                                " but got \nthe current character of  %s " +
                                " on array index of %s \n", charString, list.size() )
                );

            }
        } while ( this.hasMore() );


        return value;

    }


    protected <T> T convert( Class<T> type, Object object ) {
        if ( type == Map.class || type == List.class ) {
            return (T)object;
        } else {
            if ( object instanceof Map ) {
                return MapObjectConversion.fromValueMap ( ( Map<String, org.boon.core.Value> ) object, type );
            } else if ( object instanceof Value &&  Typ.isBasicType ( type )  ) {
                return (T)( (Value) object).toValue ();
            }
            else {
                return (T)object;
            }
        }
    }



    protected final Object decodeFromChars( char[] cs ) {
        Value value =  ( ( Value ) super.decodeFromChars ( cs ) );
        if (value.isContainer ()) {
            return value.toValue ();
        } else {
            return value;
        }
    }


}