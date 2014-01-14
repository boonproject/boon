package org.boon.json.implementation;

import org.boon.core.Type;
import org.boon.core.Value;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.value.*;
import org.boon.json.JsonException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This works in index overlay mode or chop mode.
 * Chop mode reduces possibility of memory leak but causes a few more buffer copies as it chops up the buffer.
 */
public class JsonFastParser extends JsonParserCharArray {

    private static ValueContainer EMPTY_LIST = new ValueContainer ( Collections.emptyList() );

    private final boolean useValues;
    private final boolean chop;
    private final boolean lazyChop;
    private final boolean checkDates;

    public JsonFastParser(  ) {
        this( FieldAccessMode.FIELD );
    }

    public JsonFastParser( FieldAccessMode mode ) {
        this( mode, true );
    }

    public JsonFastParser( FieldAccessMode mode, boolean useAnnotations ) {
        this( FieldAccessMode.create(mode, useAnnotations) );
    }

    public JsonFastParser(FieldsAccessor fieldsAccessor) {
        this( fieldsAccessor, false );
    }

    public JsonFastParser( FieldsAccessor fieldsAccessor, boolean useValues ) {
        this( fieldsAccessor, useValues, false );
    }

    public JsonFastParser( FieldsAccessor fieldsAccessor, boolean useValues, boolean chop ) {
        this( fieldsAccessor, useValues, chop, !chop );
    }

    public JsonFastParser( FieldsAccessor fieldsAccessor, boolean useValues, boolean chop, boolean lazyChop ) {
        this( fieldsAccessor, useValues, chop, lazyChop, true );
    }

    public JsonFastParser( FieldsAccessor fieldsAccessor, boolean useValues, boolean chop, boolean lazyChop, boolean checkDates ) {
        super( fieldsAccessor );
        this.useValues = useValues;
        this.chop = chop;
        this.lazyChop = lazyChop;
        this.checkDates = checkDates;
    }

    protected final Value decodeJsonObjectLazyFinalParse() {

        char[] array = charArray;

        if ( __currentChar == '{' )
            __index++;

        ValueMap map =  useValues ? new ValueMapImpl () : new LazyValueMap ( lazyChop );
        Value value  = new ValueContainer ( map );

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

                    map.add ( miv );
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

        switch ( __currentChar ) {

            case '"':
                return decodeStringOverlay ();

            case '{':
                return decodeJsonObjectLazyFinalParse ();

            case 't':
                return decodeTrue () == true ? ValueContainer.TRUE : ValueContainer.FALSE;

            case 'f':
                return decodeFalse () == false ? ValueContainer.FALSE : ValueContainer.TRUE;

            case 'n':
                return decodeNull () == null ? ValueContainer.NULL : ValueContainer.NULL;

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
                return decodeNumberOverlay (false);

            case '-':
                return decodeNumberOverlay (true);

            default:

                throw new JsonException ( exceptionDetails ( "Unable to determine the " +
                        "current character, it is not a string, number, array, or object" ) );
        }
    }

    private final Value decodeNumberOverlay(final boolean minus) {

        char[] array = charArray;

        final int startIndex = __index;
        int index =  __index;
        char currentChar;
        boolean doubleFloat = false;

        if (minus && index + 1 < array.length) {
            index++;
        }

        while (true) {
            currentChar = array[index];
            if ( isNumberDigit ( currentChar )) {
                //noop
            } else if ( currentChar <= 32 ) { //white
                break;
            } else if ( isDelimiter ( currentChar ) ) {
                break;
            } else if ( isDecimalChar (currentChar) ) {
                doubleFloat = true;
            }
            index++;
            if (index   >= array.length) break;
        }

        __index = index;
        __currentChar = currentChar;

        Type type = doubleFloat ? Type.DOUBLE : Type.INTEGER;

        NumberValue value = new NumberValue ( chop, type, startIndex, __index, this.charArray );

        return value;
    }

    private Value decodeStringOverlay() {

        char[] array = charArray;
        int index = __index;
        char currentChar = charArray[index];

        if ( index < array.length && currentChar == '"' ) {
            index++;
        }

        final int startIndex = index;

        boolean encoded = hasEscapeChar ( array, index, indexHolder );
        index = indexHolder[0];

        if (encoded)  {
            index = findEndQuote ( array, index );
        }

        Value value = new CharSequenceValue ( chop, Type.STRING, startIndex, index, array, encoded, checkDates );

        if ( index < array.length ) {
            index++;
        }

        __index = index;
        return value;
    }

    private Value decodeJsonArrayOverlay() {

        char [] array = charArray;
        if ( __currentChar == '[' ) {
            __index++;
        }

        skipWhiteSpace ();

        /* the list might be empty  */
        if ( __currentChar == ']' ) {
            __index++;
            return EMPTY_LIST;
        }

        List<Object> list;

        if ( useValues ) {
            list = new ArrayList<> ();
        } else {
            list = new ValueList ( lazyChop );
        }

        Value value = new ValueContainer ( list );

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

    protected final Object decodeFromChars( char[] cs ) {
        Value value =  ( ( Value ) super.decodeFromChars ( cs ) );
        if (value.isContainer ()) {
            return value.toValue ();
        } else {
            return value;
        }
    }
}
