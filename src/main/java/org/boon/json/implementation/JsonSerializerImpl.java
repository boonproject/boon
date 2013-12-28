package org.boon.json.implementation;

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
import org.boon.json.FieldSerializationData;
import org.boon.json.JsonSerializer;
import org.boon.json.ObjectSerializationData;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Boon.puts;

public class JsonSerializerImpl implements JsonSerializer {

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
    private final List<Function<FieldSerializationData, Boolean>> filterProperties;
    private final List<Function<FieldSerializationData, Boolean>> customPropertySerializers;
    private final Map <Class<?>, Function<ObjectSerializationData, Boolean>> customObjectSerializers;
    private final Map <Class<?>,  Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ( );


    private CharBuf builder = CharBuf.create( 4000 );

    private IdentityHashMap idMap;

    public JsonSerializerImpl ( )  {
        this.outputType = false;
        this.useProperties = true;
        this.useFields = true;
        this.includeNulls = false;
        this.useAnnotations = true;
        this.includeEmpty = false;
        this.handleSimpleBackReference = true;
        this.handleComplexBackReference = false;
        this.customObjectSerializers = null;
        this.filterProperties = null;
        this.customPropertySerializers = null;
        this.jsonFormatForDates = false;
        this.includeDefault = false;


    }

    public JsonSerializerImpl ( final boolean outputType, final boolean useProperties,
                                final boolean useFields, final boolean includeNulls,
                                final boolean useAnnotations, final boolean includeEmpty,
                                final boolean handleSimpleBackReference,
                                final boolean handleComplexBackReference,
                                final boolean jsonFormatForDates,
                                final boolean includeDefault,
                                final List<Function<FieldSerializationData, Boolean>> filterProperties,
                                final List<Function<FieldSerializationData, Boolean>> customSerializer,
                                final Map <Class<?>, Function<ObjectSerializationData, Boolean>> customObjectSerializers ) {
        this.outputType = outputType;
        this.useFields = useFields;
        this.useProperties = useProperties;
        this.includeNulls = includeNulls;
        this.useAnnotations = useAnnotations;
        this.includeEmpty = includeEmpty;
        this.handleSimpleBackReference = handleSimpleBackReference;
        this.handleComplexBackReference = handleComplexBackReference;

        this.filterProperties = filterProperties;
        this.customPropertySerializers = customSerializer;
        this.customObjectSerializers =  customObjectSerializers;
        this.jsonFormatForDates = jsonFormatForDates;
        this.includeDefault = includeDefault;
        if (handleComplexBackReference) {
            idMap = new IdentityHashMap (  );
        }
    }


    private final void serializeString( String str, CharBuf builder ) {




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




    private final void serializeFieldObject (Object parent, FieldAccess fieldAccess, CharBuf builder) throws Exception {

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

    private void serializeDate ( Date date, CharBuf builder ) {
        if (jsonFormatForDates) {
            Dates.jsonDateChars ( date, builder );
        } else {
            builder.addLong(date.getTime ());
        }
    }

    private final void serializeObject( Object obj, CharBuf builder ) throws Exception {


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

    Cache<Object, char[]> cache = new SimpleCache<Object, char[]> (20, CacheType.LRU);

    private final void serializeInstance ( Object obj, CharBuf builder ) throws Exception {



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

    private final void doSerializeInstance ( Object obj, CharBuf builder ) throws Exception {

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



        if ( this.customPropertySerializers != null ) {
            for ( FieldAccess fieldAccess : values ) {
                builder.addQuoted ( fieldAccess.getName () );
                builder.addChar( ':' );
                Object value = fieldAccess.getValue( obj );
                applyCustomSerializers ( obj, builder, fieldAccess, value );
                builder.addChar( ',' );
                index++;
            }
        } else {
            for ( FieldAccess fieldAccess : values ) {
                builder.addQuoted ( FastStringUtils.toCharArray ( fieldAccess.getName () ) );
                builder.addChar ( ':' );
                serializeFieldObject ( obj, fieldAccess, builder );
                builder.addChar ( ',' );
                index++;
            }
        }
        if ( index > 0 ) {
           builder.removeLastChar();
        }
        builder.addChar( '}' );
    }




    private final void doSerializeInstanceWithType ( Object obj, CharBuf builder ) throws Exception {

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
        if ( this.customPropertySerializers != null ) {
            for ( FieldAccess fieldAccess : values ) {
                builder.addQuoted ( fieldAccess.getName () );
                builder.addChar( ':' );
                Object value = fieldAccess.getValue( obj );
                applyCustomSerializers ( obj, builder, fieldAccess, value );
                index++;
                if ( index < length ) {
                    builder.addChar( ',' );
                }
            }
        } else {
            for ( FieldAccess fieldAccess : values ) {
                builder.addQuoted ( fieldAccess.getName () );
                builder.addChar( ':' );
                serializeFieldObject ( obj, fieldAccess, builder );
                index++;
                if ( index < length ) {
                    builder.addChar( ',' );
                }
            }
        }

        builder.addChar( '}' );
    } 
    
    private void applyCustomSerializers ( Object obj, CharBuf builder, FieldAccess fieldAccess, Object value ) throws Exception {
        boolean handled = false;
        for (Function<FieldSerializationData, Boolean> function : this.customPropertySerializers ) {
            FieldSerializationData data = new FieldSerializationData (
                    fieldAccess.getName (), fieldAccess.getType (), obj.getClass (), fieldAccess.getValue ( obj ), builder, obj  );
            handled = function.apply( data );
            if ( handled ) {
                break;
            }

        }

        if (!handled ) {
            serializeFieldObject ( obj,  fieldAccess, builder );
        }
    }

    private Collection<FieldAccess> filterFields ( Object obj, Collection<FieldAccess> v ) {

        List <FieldAccess> fields = new LinkedList<> ( v );

        if (this.filterProperties !=  null) {
            filterByCustomPropertyFiltersIfNeeded ( obj, fields );
        }

        if (!includeNulls) {
            excludeNullsIfNeeded ( obj, fields );
        }

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
    private void excludeNullsIfNeeded ( Object obj, List<FieldAccess> fields ) {

            ListIterator<FieldAccess> listIterator = fields.listIterator ();

            while ( listIterator.hasNext () ) {
                FieldAccess fieldAccess = listIterator.next ();

                if (fieldAccess.isPrimitive ()) {
                    continue;
                }

                if (useAnnotations && alwaysInclude ( fieldAccess )) {
                    continue;
                }

                Object value = fieldAccess.getObject ( obj );
                if ( value == null ) {
                    listIterator.remove (  );
                }
            }

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

    private void filterByCustomPropertyFiltersIfNeeded ( Object obj, List<FieldAccess> fields ) {
        if ( this.filterProperties.size () > 0) {

            ListIterator<FieldAccess> listIterator = fields.listIterator ();

            while ( listIterator.hasNext () ) {
                FieldAccess fieldAccess = listIterator.next ();
                FieldSerializationData data = new FieldSerializationData (
                        fieldAccess.getName (), fieldAccess.getType (), obj.getClass (), fieldAccess.getValue ( obj ), null, obj  );
                for (Function<FieldSerializationData, Boolean> func : filterProperties) {
                    boolean exclude = func.apply ( data );
                    if ( exclude ) {
                        listIterator.remove ();
                    }
                }
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

    private final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get( aClass );
        if (map == null) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
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

    private final void serializeMap( Map<Object, Object> map, CharBuf builder ) throws Exception {
        final Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
        for ( Map.Entry<Object, Object> entry : entrySet ) {
            builder.addQuoted ( entry.getKey ().toString () );
            builder.addChar( ':' );
            serializeObject( entry.getValue(), builder );
        }
    }

    private final void serializeCollection( Collection<?> collection, CharBuf builder ) throws Exception {
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

    private final void serializeArray( Object[] array, CharBuf builder ) throws Exception {
        builder.addChar( '[' );
        for ( int index = 0; index < array.length; index++ ) {
            serializeObject( array[ index ], builder );
            if ( index != array.length - 1 ) {
                builder.append( ',' );
            }
        }
        builder.addChar( ']' );
    }

}
