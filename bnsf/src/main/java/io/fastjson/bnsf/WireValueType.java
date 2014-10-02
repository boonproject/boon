package io.fastjson.bnsf;

/**
 * Created by Richard on 9/25/14.
 */
public enum WireValueType {

    /* Concepts. */
    ROOT(null, -1, -1, true),
    TYPE(ROOT, 0, 0, true),


    /* Root types. */
    NUMERIC(TYPE, 0, 0, true),
    ARRAY(TYPE, 0, 0, true),
    STREAM(TYPE, 0, 0, true),
    PAIR(TYPE, 0, 0, true),


    /* Basic Values */

    /* Numeric types */
    OCTET       (NUMERIC, 1, -127), //8 bit signed
    UINT        (NUMERIC, 2, -126), //16 bit unsigned
    INT         (NUMERIC, 3, -125), //32 bit signed
    VAR_INT     (NUMERIC, 4, -124), //Variable size INT
    DECIMAL     (NUMERIC, 5, -123), //Variable precision and scale

    /* String, String starts with a size. */
    STRING      (TYPE, 6, -122),



    /** Numeric and basic Array types. Arrays start with a size.  */
    OCTET_ARRAY       (ARRAY, 7,  -121, OCTET),
    UINT_ARRAY        (ARRAY, 8,  -120, UINT),
    INT_ARRAY         (ARRAY, 9,  -119, INT),
    VAR_INT_ARRAY     (ARRAY, 10, -118, VAR_INT),
    DECIMAL_ARRAY     (ARRAY, 11, -117, DECIMAL),
    STRING_ARRAY      (ARRAY, 12, -116, STRING),


    /** Numeric and basic STREAM types. Streams have a size and a last chunk flag.  */
    OCTET_STREAM       (STREAM, 7,  -115, OCTET),
    UINT_STREAM        (STREAM, 8,  -114, UINT),
    INT_STREAM         (STREAM, 9,  -113, INT),
    VAR_INT_STREAM     (STREAM, 10, -112, VAR_INT),
    DECIMAL_STREAM     (STREAM, 11, -111, DECIMAL),
    STRING_STREAM      (STREAM, 12, -110, STRING),

    /** Key value pair, where the key is a string.
    Used for object properties and map associative arrays.
    String -> TYPE -> VALUE
    */
    STRING_PAIR        (PAIR, 13, -109, STRING),

    /** Key value pair, where the key is a string.
    Used for object properties and to map associative arrays.
    Also used to use hash-code instead of String for mappings.
    INT -> TYPE -> VALUE
    */
    INT_PAIR           (PAIR, 14, -108, INT),


    /** Key value pair, where the key is a string.
    Used for object properties and to map associative arrays.
    Also used to use enums instead of String for mappings.
    OCTET -> TYPE -> VALUE
    */
    OCTET_PAIR           (PAIR, 15, -107, INT),


    /**
     * ARRAY_OF_STRING_PAIRS->SIZE->STRING0->TYPE0->VALUE0->STRING1->TYPE1->VALUE1
     * Or
     * ARRAY_OF_STRING_PAIRS->SIZE->STRING_PAIR0->STRING_PAIR1->STRING_PAIR2
     *
     */
    ARRAY_OF_STRING_PAIRS          (ARRAY, 16,  -106, STRING_PAIR),
    ARRAY_OF_INT_PAIRS             (ARRAY, 17,  -105, INT_PAIR),
    ARRAY_OF_OCTET_PAIRS           (ARRAY, 18,  -104, OCTET_PAIR),

    STREAM_OF_STRING_PAIRS          (STREAM, 19,  -103, STRING_PAIR),
    STREAM_OF_INT_PAIRS             (STREAM, 20,  -102, INT_PAIR),
    STREAM_OF_OCTET_PAIRS           (STREAM, 21,  -101, OCTET_PAIR),


    /*
        A LIST is like an array in that it has a size,
        but each element in the list can be any type.

        LIST_TYPE->SIZE->TYPE0->VALUE0->TYPE1->VALUE1

     */
    LIST(TYPE, 22, -100, true),
    ARRAY_OF_LISTS(ARRAY, 23, -99, true),
    STREAM_OF_LISTS(ARRAY, 24, -98, true),

    /* User defined type 1. For extension. */
    UDT1(TYPE, 25, -97, true),
    /* User defined type 2. For extension. */
    UDT2(TYPE, 26, -97, true),
    /* User defined type 3. For extension. */
    UDT3(TYPE, 27, -97, true),
    /* User defined type 4. For extension. */
    UDT4(TYPE, 28, -97, true),

    /* Some sort of extension. For extension. */
    EXT(TYPE, 29, -96, true),


    /* String containing JSON which can be parsed for legacy integration. */
    JSON(STRING, 30, -95, true),
    ARRAY_OF_JSON(ARRAY,    31, -94, JSON),
    STREAM_OF_JSON(STREAM,  32, -93, JSON),

    /*
            MIME_TYPE -> MIME(String which holds mime type)->OCTET-ARRAY (holds data in mime type)
     */
    MIME(STRING,  33, -92, true),

    /*
            MIME_TYPE->STRING (which holds mime type)->OCTET-ARRAY (holds data in mime type)
     */
    ARRAY_OF_MIME(ARRAY,    34, -91, MIME),
    STREAM_OF_MIME(STREAM,  35, -90, MIME),


    LAST(null, -1, -1, true),
    ;

    private final WireValueType parent;
    private final byte enumValue;
    private final byte wireValue;
    private final WireValueType componentType;
    private final boolean concept;


    WireValueType(WireValueType parent, int enumValue, int wireValue, WireValueType composes) {

        this.parent = parent;
        this.enumValue = (byte) enumValue;
        this.wireValue = (byte) wireValue;
        this.componentType = composes;

        concept=false;
    }


    WireValueType(WireValueType parent, int enumValue, int wireValue, boolean concept) {


        this.parent = parent;
        this.enumValue = (byte) enumValue;
        this.wireValue = (byte) wireValue;
        this.componentType = null;
        this.concept = concept;

    }


    WireValueType(WireValueType parent, int enumValue, int wireValue) {

        this.parent = parent;
        this.enumValue = (byte) enumValue;
        this.wireValue = (byte) wireValue;
        this.componentType = null;
        concept=false;

    }

    public boolean isArray() {
        return ARRAY == this.parent;
    }


    public boolean isStream() {
        return STREAM == this.parent;
    }


    public boolean isNumeric() {
        return NUMERIC == this.parent;
    }


    public boolean isConcept() {
        return concept;
    }


    public boolean isValue() {
        return !concept;
    }


    public WireValueType componentType() {
        return this.componentType;
    }

    public byte enumValue() {
        return enumValue;
    }

    public byte wireValue() {
        return wireValue;
    }
}
