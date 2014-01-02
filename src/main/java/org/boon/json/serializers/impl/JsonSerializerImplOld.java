package org.boon.json.serializers.impl;

import org.boon.Exceptions;
import org.boon.Sets;
import org.boon.cache.Cache;
import org.boon.cache.CacheType;
import org.boon.cache.SimpleCache;
import org.boon.core.Dates;
import org.boon.core.Function;
import org.boon.core.Type;
import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.ObjectSerializationData;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsonSerializerImplOld implements JsonSerializerInternal {

    private static final String[] EMPTY_PROPERTIES = new String[0];
    private final boolean outputType;
    private final boolean useProperties;
    private final boolean jsonFormatForDates;
    private final boolean useFields;
    private final boolean includeNulls;
    private final boolean includeDefault;
    private final boolean useAnnotations;
    private final boolean includeEmpty;
    private final boolean handleSimpleBackReference;
    private final boolean handleComplexBackReference;
    private final boolean useInstanceCache;

    private final Map <Class<?>, Function<ObjectSerializationData, Boolean>> customObjectSerializers;
    private final Map <Class<?>,  Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ( );


    private CharBuf builder = CharBuf.create( 4000 );

    private IdentityHashMap idMap;

    public JsonSerializerImplOld ()  {
        this.outputType = false;
        this.useProperties = true;
        this.useFields = true;
        this.includeNulls = false;
        this.useAnnotations = true;
        this.includeEmpty = false;
        this.handleSimpleBackReference = true;
        this.handleComplexBackReference = false;
        this.customObjectSerializers = null;
        this.jsonFormatForDates = false;
        this.includeDefault = false;
        this.useInstanceCache = true;


    }

    public JsonSerializerImplOld ( final boolean outputType, final boolean useProperties,
                                   final boolean useFields, final boolean includeNulls,
                                   final boolean useAnnotations, final boolean includeEmpty,
                                   final boolean handleSimpleBackReference,
                                   final boolean handleComplexBackReference,
                                   final boolean jsonFormatForDates,
                                   final boolean includeDefault,
                                   final boolean useInstanceCache,
                                   final Map<Class<?>, Function<ObjectSerializationData, Boolean>> customObjectSerializers ) {
        this.outputType = outputType;
        this.useFields = useFields;
        this.useProperties = useProperties;
        this.includeNulls = includeNulls;
        this.useAnnotations = useAnnotations;
        this.includeEmpty = includeEmpty;
        this.handleSimpleBackReference = handleSimpleBackReference;
        this.handleComplexBackReference = handleComplexBackReference;
        this.useInstanceCache = useInstanceCache;

        this.customObjectSerializers =  customObjectSerializers;
        this.jsonFormatForDates = jsonFormatForDates;
        this.includeDefault = includeDefault;
        if (handleComplexBackReference) {
            idMap = new IdentityHashMap (  );
        }
    }


    public final void serializeString( String str, CharBuf builder ) {




        char[] charArray = FastStringUtils.toCharArray ( str );

        boolean foundControlChar = false;

        loop:
        for ( int index = 0; index < charArray.length; index++ ) {
            char c = charArray[ index ];
            switch ( c ) {
            case '\"':
            case '\\':
            case '/':
            case '\b':
            case '\f':
            case '\n':
            case '\r':
            case '\t':
                foundControlChar = true;
                break loop;
            }

        }

        if (foundControlChar) {
            builder.add ( '"' );
            for ( int index = 0; index < charArray.length; index++ ) {
                char c = charArray[ index ];

                switch ( c ) {
                    case '\"':
                        builder.addChar( '\\' ).addChar( '\"' );
                        break;
                    case '\\':
                        builder.addChar( '\\' ).addChar( '\\' );
                        break;
                    case '/':
                        builder.addChar( '\\' ).addChar( '/' );
                        break;
                    case '\b':
                        builder.addChar( '\\' ).addChar( 'b' );
                        break;
                    case '\f':
                        builder.addChar( '\\' ).addChar( 'f' );
                        break;
                    case '\n':
                        builder.addChar( '\\' ).addChar( 'n' );
                        break;
                    case '\r':
                        builder.addChar( '\\' ).addChar( 'r' );
                        break;
                    case '\t':
                        builder.addChar( '\\' ).addChar( 't' );
                        break;

                    default:
                            builder.addChar( c );
                }

            }
            builder.addChar( '"' );

        } else {
             builder.addQuoted ( charArray );
        }
    }


    public CharBuf serialize( Object obj ) {

        builder.readForRecycle ();
        try {
            serializeObject( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle( CharBuf.class, "unable to serializeObject", ex );
        }
        return builder;
    }


    public final void serializeFieldObject (Object parent, FieldAccess fieldAccess, CharBuf builder)  {
        if ( this.includeNulls ) {
           serializeFieldObjectAllowNulls ( parent, fieldAccess, builder );
        } else {
            serializeFieldObjectNoNullCheck ( parent, fieldAccess, builder );
        }
    }


    private final void serializeFieldObjectNoNullCheck (Object parent, FieldAccess fieldAccess, CharBuf builder)  {

            switch ( fieldAccess.typeEnum () ) {
                case INT:
                    builder.addInt ( fieldAccess.getInt ( parent ) );
                    return;
                case BOOLEAN:
                    builder.addBoolean ( fieldAccess.getBoolean ( parent ) );
                    return;
                case BYTE:
                     builder.addByte ( fieldAccess.getByte ( parent ) );
                     return;
                case LONG:
                    builder.addLong ( fieldAccess.getLong ( parent ) );
                    return;
                case DOUBLE:
                    builder.addDouble ( fieldAccess.getDouble ( parent ) );
                    return;
                case FLOAT:
                    builder.addFloat ( fieldAccess.getFloat ( parent ) );
                    return;
                case SHORT:
                    builder.addShort ( fieldAccess.getShort ( parent ) );
                    return;
                case CHAR:
                    builder.addChar ( fieldAccess.getChar ( parent ) );
                    return;
                case BIG_DECIMAL:
                    builder.addBigDecimal ( ( BigDecimal ) fieldAccess.getObject ( parent ) );
                    return;
                case BIG_INT:
                    builder.addBigInteger ( ( BigInteger ) fieldAccess.getObject ( parent ) );
                    return;
                case DATE:
                    serializeDate ( ( Date ) fieldAccess.getObject ( parent ), builder );
                    return;
                case STRING:
                    serializeString ( ( String ) fieldAccess.getObject ( parent ), builder );
                    return;
                case CHAR_SEQUENCE:
                    serializeString ( fieldAccess.getObject ( parent ).toString (), builder );
                    return;
                case INTEGER_WRAPPER:
                    builder.addInt ( ( Integer ) fieldAccess.getObject ( parent ) );
                    return;
                case LONG_WRAPPER:
                    builder.addLong ( ( Long ) fieldAccess.getObject ( parent ) );
                    return;
                case FLOAT_WRAPPER:
                    builder.addFloat ( ( Float ) fieldAccess.getObject ( parent ) );
                    return;
                case DOUBLE_WRAPPER:
                    builder.addDouble ( ( Double ) fieldAccess.getObject ( parent ) );
                    return;
                case SHORT_WRAPPER:
                    builder.addShort ( ( Short ) fieldAccess.getObject ( parent ) );
                    return;
                case BYTE_WRAPPER:
                    builder.addByte ( ( Byte ) fieldAccess.getObject ( parent ) );
                    return;
                case CHAR_WRAPPER:
                    builder.addChar ( ( Character ) fieldAccess.getObject ( parent ) );
                    return;
                case ENUM:
                    builder.addQuoted ( fieldAccess.getObject ( parent ).toString () );
                    return;
                case COLLECTION:
                case LIST:
                case SET:
                    this.serializeCollection ( (Collection) fieldAccess.getObject ( parent ), builder );
                    return;
                case MAP:
                    this.serializeMap ( (Map) fieldAccess.getObject ( parent ), builder );
                    return;
                case ARRAY:
                    this.serializeArray ( ( Object[] ) fieldAccess.getObject ( parent ), builder );
                    return;

                default:
                    serializeInstance ( fieldAccess.getObject ( parent ), builder );
            }
    }

    private final void serializeFieldObjectAllowNulls (Object parent, FieldAccess fieldAccess, CharBuf builder)  {

        final Type typeEnum = fieldAccess.typeEnum ();
        switch ( typeEnum ) {
            case INT:
                builder.addInt ( fieldAccess.getInt ( parent ) );
                return;
            case BOOLEAN:
                builder.addBoolean ( fieldAccess.getBoolean ( parent ) );
                return;
            case BYTE:
                builder.addByte ( fieldAccess.getByte ( parent ) );
                return;
            case LONG:
                builder.addLong ( fieldAccess.getLong ( parent ) );
                return;
            case DOUBLE:
                builder.addDouble ( fieldAccess.getDouble ( parent ) );
                return;
            case FLOAT:
                builder.addFloat ( fieldAccess.getFloat ( parent ) );
                return;
            case SHORT:
                builder.addShort ( fieldAccess.getShort ( parent ) );
                return;
            case CHAR:
                builder.addChar ( fieldAccess.getChar ( parent ) );
                return;

        }

        Object value = fieldAccess.getObject ( parent );

        if ( value == null ) {
            builder.addNull ();
            return;
        }

        switch ( typeEnum )  {
            case BIG_DECIMAL:
                builder.addBigDecimal ( (BigDecimal) value );
                return;
            case BIG_INT:
                builder.addBigInteger ( ( BigInteger ) value );
                return;
            case DATE:
                serializeDate ( ( Date ) value, builder );
                return;
            case STRING:
                serializeString ( ( String ) value, builder );
                return;
            case CHAR_SEQUENCE:
                serializeString ( value.toString (), builder );
                return;
            case INTEGER_WRAPPER:
                builder.addInt ( ( Integer ) value );
                return;
            case LONG_WRAPPER:
                builder.addLong ( ( Long ) value );
                return;
            case FLOAT_WRAPPER:
                builder.addFloat ( ( Float ) value );
                return;
            case DOUBLE_WRAPPER:
                builder.addDouble ( ( Double ) value );
                return;
            case SHORT_WRAPPER:
                builder.addShort ( ( Short ) value );
                return;
            case BYTE_WRAPPER:
                builder.addByte ( ( Byte ) value );
                return;
            case CHAR_WRAPPER:
                builder.addChar ( ( Character ) value );
                return;
            case ENUM:
                builder.addQuoted ( value.toString () );
                return;
            case COLLECTION:
            case LIST:
            case SET:
                this.serializeCollection ( (Collection) value, builder );
                return;
            case MAP:
                this.serializeMap ( (Map) value, builder );
                return;
            case ARRAY:
                this.serializeArray ( ( Object[] ) value, builder );
                return;

            default:
                serializeInstance ( value, builder );
        }
    }



    public final void serializeDate ( Date date, CharBuf builder ) {
        if ( jsonFormatForDates ) {
            serializeDateJsonString ( date, builder );
        } else {
            serializeDateAsLong ( date , builder );
        }
    }

    private final Cache<Object, char[]> dateCache = new SimpleCache<> (200, CacheType.LRU);

    private final void serializeDateJsonString ( Date date, CharBuf builder ) {

        char [] chars = dateCache.get ( date );
        if ( chars == null) {
            CharBuf buf =  CharBuf.create ( Dates.JSON_TIME_LENGTH );
            Dates.jsonDateChars ( date, buf );
            chars = buf.toCharArray ();
            dateCache.put ( date, chars );

        }
        builder.addChars ( chars );
    }


    private void serializeDateAsLong ( Date date, CharBuf builder ) {
            builder.addLong(date.getTime ());
    }

    public final void serializeObject( Object obj, CharBuf builder )  {


        Type type = Type.getInstanceType (obj);

        switch ( type ) {

            case NULL:
                builder.addNull ();
                return;
            case INT:
                builder.addInt ( int.class.cast ( obj ) );
                return;
            case BOOLEAN:
                builder.addBoolean ( boolean.class.cast ( obj ) );
                return;
            case BYTE:
                builder.addByte ( byte.class.cast ( obj ) );
                return;
            case LONG:
                builder.addLong ( long.class.cast ( obj ) );
                return;
            case DOUBLE:
                builder.addDouble ( double.class.cast ( obj ) );
                return;
            case FLOAT:
                builder.addFloat ( float.class.cast ( obj ) );
                return;
            case SHORT:
                builder.addShort ( short.class.cast ( obj ) );
                return;
            case CHAR:
                builder.addChar ( char.class.cast ( obj ) );
                return;
            case BIG_DECIMAL:
                builder.addBigDecimal ( ( BigDecimal ) obj );
                return;
            case BIG_INT:
                builder.addBigInteger ( ( BigInteger ) obj );
                return;
            case DATE:
                serializeDate ( ( Date ) obj, builder );
                return;
            case STRING:
                serializeString ( ( String ) obj, builder );
                return;
            case CHAR_SEQUENCE:
                serializeString ( obj.toString(), builder );
                return;
            case BOOLEAN_WRAPPER:
                builder.addBoolean ( ( Boolean ) obj );
                return;
            case INTEGER_WRAPPER:
                builder.addInt ( (Integer) obj);
                return;
            case LONG_WRAPPER:
                builder.addLong ( (Long) obj);
                return;
            case FLOAT_WRAPPER:
                builder.addFloat ( (Float) obj);
                return;
            case DOUBLE_WRAPPER:
                builder.addDouble ( (Double) obj);
                return;
            case SHORT_WRAPPER:
                builder.addShort ( (Short) obj);
                return;
            case BYTE_WRAPPER:
                builder.addByte ( (Byte) obj);
                return;
            case CHAR_WRAPPER:
                builder.addChar ( (Character) obj);
                return;
            case ENUM:
                builder.addQuoted ( obj.toString () );

            case COLLECTION:
            case LIST:
            case SET:
                this.serializeCollection ( (Collection) obj, builder );
                return;
            case MAP:
                this.serializeMap ( (Map) obj, builder );
                return;
            case ARRAY:
                this.serializeArray ( ( Object[] ) obj, builder );
                return;

            default:
                serializeInstance ( obj, builder );
        }


    }

    Cache<Object, char[]> cache = new SimpleCache<> (20, CacheType.LRU);

    private final void serializeInstanceWithCache ( Object obj, CharBuf builder )  {

        char [] chars = cache.get ( obj );
        if ( chars == null ) {
            CharBuf buffer = CharBuf.create ( 256 );
            if (outputType)  {
                doSerializeInstanceWithType ( obj, buffer );
            } else {
                doSerializeInstance ( obj, buffer );
            }
            chars = FastStringUtils.toCharArray (buffer.toString ());
            cache.put ( obj, chars );
        }

        builder.addChars ( chars );


    }
    public final void serializeInstance ( Object obj, CharBuf builder )  {

        if ( useInstanceCache )  {
            serializeInstanceWithCache ( obj, builder );
        } else {
            serializeInstanceWithoutCache ( obj, builder );
        }
    }

    @Override
    public void serializeUnknown ( Object obj, CharBuf builder ) {

    }

    private void serializeInstanceWithoutCache ( Object obj, CharBuf builder )  {
        if (outputType)  {
            doSerializeInstanceWithType ( obj, builder );
        } else {
            doSerializeInstance ( obj, builder );
        }

    }


    private final void doSerializeInstance ( Object obj, CharBuf builder )  {
           if (this.includeNulls) {
               doSerializeInstanceAllowNulls ( obj, builder );
           } else {
               doSerializeInstanceNoNulls ( obj, builder );
           }

    }


    private final void doSerializeInstanceAllowNulls ( Object obj, CharBuf builder )  {
        if ( customObjectSerializers != null ) {
            Class<?> parentType = obj.getClass ();
            final Function<ObjectSerializationData, Boolean> function = customObjectSerializers.get ( parentType );
            if ( function != null ) {
                function.apply ( new ObjectSerializationData ( obj, parentType, builder ) );
                return;
            }
        }


        builder.addChar( '{' );

        final Map<String, FieldAccess> fieldAccessors = getFields(obj.getClass ());

        int index = 0;
        Collection<FieldAccess> values = fieldAccessors.values();


        values = filterFields ( obj, values );



            for ( FieldAccess fieldAccess : values ) {
                serializeNameValue ( obj, builder, fieldAccess );
                index++;
            }
        if ( index > 0 ) {
            builder.removeLastChar();
        }
        builder.addChar( '}' );


    }

    private void serializeNameValueCustomSerializers ( Object obj, CharBuf builder, FieldAccess fieldAccess )  {
        builder.addQuoted ( fieldAccess.getName () );
        builder.addChar( ':' );
        Object value = fieldAccess.getValue( obj );
    }


    private void serializeNameValueCustomSerializersNoNull ( Object obj, CharBuf builder, FieldAccess fieldAccess )  {
        if ( !fieldAccess.isPrimitive () ) {
            if (fieldAccess.getObject ( obj ) == null) {
                return;
            }
        }
        builder.addQuoted ( fieldAccess.getName () );
        builder.addChar( ':' );
        Object value = fieldAccess.getValue( obj );
    }

    private final void doSerializeInstanceNoNulls ( Object obj, CharBuf builder )  {

        if ( customObjectSerializers != null ) {
            Class<?> parentType = obj.getClass ();
            final Function<ObjectSerializationData, Boolean> function = customObjectSerializers.get ( parentType );
            if ( function != null ) {
                function.apply ( new ObjectSerializationData ( obj, parentType, builder ) );
                return;
            }
        }


        builder.addChar( '{' );

        final Map<String, FieldAccess> fieldAccessors = getFields(obj.getClass ());

        int index = 0;
        Collection<FieldAccess> values = fieldAccessors.values();


        values = filterFields ( obj, values );



            for ( FieldAccess fieldAccess : values ) {
                serializeNameValueNoNulls ( obj, builder, fieldAccess );
                index++;
            }
        if ( index > 0 ) {
            builder.removeLastChar();
        }
        builder.addChar( '}' );


    }

    private void serializeNameValueNoNulls ( Object obj, CharBuf builder,  FieldAccess fieldAccess )  {
        if ( !fieldAccess.isPrimitive () ) {
            if (fieldAccess.getObject ( obj ) == null) {
                return;
            }
        }
        builder.addQuoted ( FastStringUtils.toCharArray ( fieldAccess.getName () ) );
        builder.addChar ( ':' );
        serializeFieldObject ( obj, fieldAccess, builder );
        builder.addChar ( ',' );
    }


    private void serializeNameValue ( Object obj, CharBuf builder,  FieldAccess fieldAccess )  {
        if ( !fieldAccess.isPrimitive () ) {
            if (fieldAccess.getObject ( obj ) == null) {
                return;
            }
        }
        builder.addQuoted ( FastStringUtils.toCharArray ( fieldAccess.getName () ) );
        builder.addChar ( ':' );
        serializeFieldObject ( obj, fieldAccess, builder );
        builder.addChar ( ',' );
    }

    private final void doSerializeInstanceWithType ( Object obj, CharBuf builder ) {

        if ( customObjectSerializers != null ) {
            Class<?> parentType = obj.getClass ();
            final Function<ObjectSerializationData, Boolean> function = customObjectSerializers.get ( parentType );
            if ( function != null ) {
                function.apply ( new ObjectSerializationData ( obj, parentType, builder ) );
                return;
            }
        }

        builder.addString( "{\"class\":" );
        builder.addQuoted ( obj.getClass ().getName () );
        final Map<String, FieldAccess> fieldAccessors = getFields(obj.getClass ());

        int index = 0;
        Collection<FieldAccess> values = fieldAccessors.values();
        int length = values.size();

        if ( length > 0 ) {
            builder.addChar( ',' );
        }

        values = filterFields ( obj, values );


        length = values.size ();
            for ( FieldAccess fieldAccess : values ) {
                builder.addQuoted ( fieldAccess.getName () );
                builder.addChar( ':' );
                serializeFieldObject ( obj, fieldAccess, builder );
                index++;
                if ( index < length ) {
                    builder.addChar( ',' );
                }
            }

        builder.addChar( '}' );
    } 
    

    private Collection<FieldAccess> filterFields ( Object obj, Collection<FieldAccess> v ) {

        List <FieldAccess> fields = new LinkedList<> ( v );

        if ( !includeEmpty ) {
            excludeEmptyIfNeeded ( obj, fields );
        }

        if ( useAnnotations ) {
            filterWithAnnotationsIfNeeded ( obj, fields );
        }

        if (!includeDefault) {
            excludeDefaultIfNeeded (obj, fields);
        }

        if ( handleSimpleBackReference || handleComplexBackReference ) {
            filterForBackReferencesIfNeeded ( obj, fields );
        }

        return fields;
    }

    private void filterForBackReferencesIfNeeded ( Object obj, List<FieldAccess> fields ) {

            ListIterator<FieldAccess> listIterator = fields.listIterator ();

            while ( listIterator.hasNext () ) {
                FieldAccess fieldAccess = listIterator.next ();

                Object value = fieldAccess.getValue( obj );

                /* handle simple back reference. */
                if (handleSimpleBackReference &&  value == obj ) {
                    listIterator.remove (  );
                    continue;
                }

                if ( handleComplexBackReference ) {
                    if ( idMap.containsKey ( value ) ) {
                        listIterator.remove (  );
                    } else {
                        idMap.put ( value, value );
                    }
                }

            }
    }

    private void filterWithAnnotationsIfNeeded ( Object obj, List<FieldAccess> fields ) {

            Set<String> propertiesToIgnore  = getProperitesToIgnoreAsSet(obj);


            ListIterator<FieldAccess> listIterator = fields.listIterator ();

            while ( listIterator.hasNext () ) {
                FieldAccess fieldAccess = listIterator.next ();

                if ( Sets.in ( fieldAccess.getName (), propertiesToIgnore ) ) {
                    listIterator.remove (  );
                } else if (fieldAccess.hasAnnotation ( "JsonIgnore" )) {
                    final Map<String, Object> jsonIgnore = fieldAccess.getAnnotationData ( "JsonIgnore" );
                    boolean ignore = (Boolean) jsonIgnore.get ( "value" );
                    if (ignore) {
                        listIterator.remove (  );
                    }
                }
            }
    }

    private void excludeEmptyIfNeeded ( Object obj, List<FieldAccess> fields ) {
            ListIterator<FieldAccess> listIterator = fields.listIterator ();

            while ( listIterator.hasNext () ) {
                FieldAccess fieldAccess = listIterator.next ();


                final Type typeEnum = fieldAccess.typeEnum ();

                if ( fieldAccess.isPrimitive() ) {
                    continue;
                }

                if (useAnnotations && alwaysInclude ( fieldAccess )) {
                    continue;
                }


                Object value = fieldAccess.getValue ( obj );

                if ( value == null ) {
                    listIterator.remove();
                    continue;
                }


                switch ( typeEnum ) {
                    case LIST:
                    case MAP:
                    case COLLECTION:
                    case SET:
                        Collection collection = (Collection) value;
                        if (collection.size () == 0) {
                           listIterator.remove();
                        }
                        continue;

                    case STRING:
                    case CHAR_SEQUENCE:
                        CharSequence seq = (CharSequence) value;
                        if (seq.length () == 0) {
                            listIterator.remove();
                        }
                        continue;

                    case ARRAY:
                        if ( Array.getLength( obj ) == 0 ) {
                            listIterator.remove();
                        }
                        continue;

                }

            }

    }




    private boolean alwaysInclude ( FieldAccess fieldAccess ) {
        boolean forceKeep = false;
        if ( useAnnotations ) {
            final Map<String, Object> jsonInclude = fieldAccess.getAnnotationData ( "JsonInclude" );
            if ( jsonInclude != null ) {
                String value = ( String ) jsonInclude.get ( "value" );
                if ( value.equals ( "ALWAYS" ) ) {
                    forceKeep = true;
                }
            }
        }
        return forceKeep;
    }

    private void excludeDefaultIfNeeded ( Object parent, List<FieldAccess> fields ) {

            ListIterator<FieldAccess> listIterator = fields.listIterator ();

            while ( listIterator.hasNext () ) {
                FieldAccess fieldAccess = listIterator.next ();

                if ( !fieldAccess.isPrimitive () ) {
                    continue;
                }

                if (useAnnotations && alwaysInclude ( fieldAccess )) {
                    continue;
                }


                switch ( fieldAccess.typeEnum() ) {
                    case INT:
                        int i = fieldAccess.getInt ( parent ) ;
                        if (i == 0) {
                            listIterator.remove ();
                        }
                        break;
                    case BOOLEAN:
                        if (!fieldAccess.getBoolean ( parent )) {
                                    listIterator.remove ();
                        }
                        break;

                    case BYTE:
                        byte b = fieldAccess.getByte ( parent ) ;
                        if ( b == 0 ) {
                           listIterator.remove ();
                        }
                        break;
                    case LONG:
                        long l = fieldAccess.getLong ( parent ) ;

                        if ( l == 0L ) {
                            listIterator.remove ();
                        }
                        break;
                    case DOUBLE:
                        double d = fieldAccess.getDouble ( parent ) ;
                        if ( d == 0d ) {
                            listIterator.remove ();
                        }
                        break;
                    case FLOAT:
                        float f = fieldAccess.getFloat ( parent ) ;
                        if ( f == 0f ) {
                            listIterator.remove ();
                        }
                        break;
                    case SHORT:
                        short s = fieldAccess.getShort ( parent ) ;
                        if ( s == 0 ) {
                            listIterator.remove ();
                        }
                        break;
                    case CHAR:
                        char c = fieldAccess.getChar ( parent ) ;
                        if ( c == 0 ) {
                            listIterator.remove ();
                        }
                        break;

                }
            }

    }



    private String[] getProperitesToIgnore ( Object obj ) {

        final Map<String, AnnotationData> classAnnotations =
                Annotations.getAnnotationDataForClassAsMap( obj.getClass () );

        final AnnotationData jsonIgnoreProperties = classAnnotations.get( "JsonIgnoreProperties" );

        if (jsonIgnoreProperties == null) {
            return EMPTY_PROPERTIES;
        } else {
            return (String[])jsonIgnoreProperties.getValues().get( "value" );
        }
    }




    Map <Class, Set<String>> properitesToIgnoreMap;

    private Set<String> getProperitesToIgnoreAsSet ( Object obj ) {
        if (properitesToIgnoreMap==null){
            properitesToIgnoreMap = new ConcurrentHashMap<>();
        }

        Set<String> set = properitesToIgnoreMap.get ( obj.getClass () );
        if ( set == null ) {
             final String[] properitesToIgnore = getProperitesToIgnore ( obj );
             set = Sets.set ( properitesToIgnore );
            properitesToIgnoreMap.put(obj.getClass (), set);
        }
        return set;
    }

    public final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get( aClass );
        if (map == null) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
    }

    @Override
    public boolean serializeField ( Object instance, FieldAccess fieldAccess, CharBuf builder ) {
        return false;
    }

    private final Map<String, FieldAccess> doGetFields ( Class<? extends Object> aClass ) {


        if ( useProperties && !useFields ) {
            return Reflection.getPropertyFieldAccessors ( aClass );
        } else if ( !useProperties && useFields ) {
            return Reflection.getAllAccessorFields ( aClass );
        } else {
            return Reflection.getPropertyFieldAccessMapFieldFirst ( aClass );
        }
    }

    public final void serializeMap( Map<String, Object> map, CharBuf builder )  {
        final Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for ( Map.Entry<String, Object> entry : entrySet ) {
            builder.addQuoted ( entry.getKey ().toString () );
            builder.addChar( ':' );
            serializeObject( entry.getValue(), builder );
        }
    }

    @Override
    public void serializeArray ( Object array, CharBuf builder ) {

    }

    public final void serializeCollection( Collection<?> collection, CharBuf builder )  {
        builder.addChar( '[' );

        final int length = collection.size ();
        int index = 0;
        for ( Object o : collection ) {
            serializeObject( o, builder );

            if ( index != length - 1 ) {
                builder.addChar( ',' );
            }

            index++;
        }
        builder.addChar( ']' );

    }

    public final void serializeArray( Object[] array, CharBuf builder )  {
        builder.addChar( '[' );
        for ( int index = 0; index < array.length; index++ ) {
            serializeObject( array[ index ], builder );
            if ( index != array.length - 1 ) {
                builder.addChar ( ',' );
            }
        }
        builder.addChar( ']' );
    }

}
