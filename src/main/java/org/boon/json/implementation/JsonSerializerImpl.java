package org.boon.json.implementation;

import org.boon.Exceptions;
import org.boon.Sets;
import org.boon.core.Dates;
import org.boon.core.Function;
import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.FieldSerializationData;
import org.boon.json.JsonSerializer;
import org.boon.json.ObjectSerializationData;
import org.boon.primitive.CharBuf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsonSerializerImpl implements JsonSerializer {

    private static final String[] EMPTY_PROPERTIES = new String[0];
    private final boolean outputType;
    private final boolean useProperties;
    private final boolean useFields;
    private final boolean includeNulls;
    private final boolean useAnnotations;
    private final boolean includeEmpty;
    private final boolean handleSimpleBackReference;
    private final boolean handleComplexBackReference;
    private final List<Function<FieldSerializationData, Boolean>> filterProperties;
    private final List<Function<FieldSerializationData, Boolean>> customPropertySerializers;
    private final Map <Class, Function<ObjectSerializationData, Boolean>> customObjectSerializers;
    private final Map <Class,  Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ( );

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


    }

    public JsonSerializerImpl ( final boolean outputType, final boolean useProperties,
                                final boolean useFields, final boolean includeNulls,
                                final boolean useAnnotations, final boolean includeEmpty,
                                final boolean handleSimpleBackReference,
                                final boolean handleComplexBackReference,
                                final List<Function<FieldSerializationData, Boolean>> filterProperties,
                                final List<Function<FieldSerializationData, Boolean>> customSerializer,
                                final Map <Class, Function<ObjectSerializationData, Boolean>> customObjectSerializers ) {
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
        if (handleComplexBackReference) {
            idMap = new IdentityHashMap (  );
        }
    }

    private final void serializeString( String str, CharBuf builder ) {
        builder.addChar( '\"' );
        char[] charArray = str.toCharArray();

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
                /* Encode unicode character. */
                    // if (!asciiEncoder.canEncode(c)){ //This works to but worried
                    // it might be too slow http://en.wikipedia.org/wiki/ASCII
                    if ( c > 0x7F ) { // See if it is out of range of ASCII
                        // I don't like this for performance, I am going to roll my
                        // own.
                        // builder.append(String.format("\\u%4H", c).replace(' ',
                        // '0'));
                        String hexString = Integer.toHexString( c ).toUpperCase();
                        builder.addChar( '\\' ).addChar( 'u' );

                        if ( hexString.length() >= 4 ) {
                            builder.add( hexString );
                        } else {
                            int howMany0 = 4 - hexString.length();
                            for ( int i = 0; i < howMany0; i++ ) {
                                builder.addChar( '0' );
                            }
                            builder.add( hexString );
                        }
                    } else {
                        builder.addChar( c );
                    }

            }
        }
        builder.addChar( '\"' );
    }

    public CharBuf serialize( Object obj ) {
        CharBuf builder = CharBuf.create( 64 );

        try {
            serializeObject( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle( CharBuf.class, "unable to serializeObject", ex );
        }
        return builder;
    }

    private final void serializeObject( Object obj, CharBuf builder ) throws Exception {

        if ( customObjectSerializers != null ) {
            Class<?> parentType = obj.getClass ();
            final Function<ObjectSerializationData, Boolean> function = customObjectSerializers.get ( parentType );
            if ( function != null ) {
                function.apply ( new ObjectSerializationData ( obj, parentType, builder ) );
                return;
            }
        }


        if ( obj == null ) {
            builder.add ( "null" );
        } else if ( obj instanceof Number || obj instanceof Boolean ) {
            builder.add ( obj.toString () );
        } else if ( obj instanceof String ) {
            serializeString ( ( String ) obj, builder );
        } else if ( obj instanceof Collection ) {
            Collection<?> collection = ( Collection<?> ) obj;
            serializeCollection ( collection, builder );
        } else if ( obj.getClass().isArray() ) {
            serializeArray ( ( Object[] ) obj, builder );
        } else if ( obj instanceof Date ) {
            Dates.jsonDateChars ( ( Date ) obj, builder );
        } else if ( obj instanceof Map ) {
            serializeMap( ( Map ) obj, builder );
        } else if ( obj instanceof CharSequence ) {
            serializeString( obj.toString (), builder );
        } else if ( obj instanceof Enum ) {
            serializeString( obj.toString (), builder );
        } else {
            serializeInstance ( obj, builder );
        }
    }

    private final void serializeInstance ( Object obj, CharBuf builder ) throws Exception {
        builder.addChar( '{' );
        if ( outputType ) {
            builder.add( "\"class\":\"" );
            builder.add( obj.getClass().getName() );
            builder.addChar( '"' );
        }


        final Map<String, FieldAccess> fieldAccessors = getFields(obj.getClass ());

        int index = 0;
        Collection<FieldAccess> values = fieldAccessors.values();
        int length = values.size();

        if ( outputType && length > 0 ) {
            builder.addChar( ',' );
        }

        values = filterFields ( obj, values );


        length = values.size ();
        for ( FieldAccess fieldAccess : values ) {

            Object value = fieldAccess.getValue( obj );

            builder.addChar( '\"' );
            builder.add( fieldAccess.getName() );
            builder.addChar( '\"' );
            builder.addChar( ':' );



            if ( this.customPropertySerializers != null ) {
                applyCustomSerializers ( obj, builder, fieldAccess, value );
            }  else {
                serializeObject( value, builder );
            }

            if ( index + 1 != length ) {
                    builder.addChar( ',' );
            }
            index++;
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
            serializeObject( value, builder );
        }
    }

    private Collection<FieldAccess> filterFields ( Object obj, Collection<FieldAccess> values ) {

        if ( this.filterProperties !=  null ) {


            List<FieldAccess> customFilter = new LinkedList<> ( values  );
            for ( FieldAccess fieldAccess : values ) {

                FieldSerializationData data = new FieldSerializationData (
                        fieldAccess.getName (), fieldAccess.getType (), obj.getClass (), fieldAccess.getValue ( obj ), null, obj  );
                for (Function<FieldSerializationData, Boolean> func : filterProperties) {
                    boolean exclude = func.apply ( data );
                    if ( exclude ) {
                        customFilter.remove ( fieldAccess );
                    }
                }
            }
            values = customFilter;
        }

        if (!includeNulls) {


            List<FieldAccess> nonNullValues = new LinkedList<> ( values  );
            for ( FieldAccess fieldAccess : values ) {

                boolean forceKeep = false;
                if (useAnnotations) {
                    final Map<String, Object> jsonInclude = fieldAccess.getAnnotationData( "JsonInclude" );
                    if ( jsonInclude != null ) {
                        String value = (String) jsonInclude.get ( "value" );
                        if (value.equals ( "ALWAYS" )) {
                            forceKeep = true;
                        }
                    }
                }
                Object value = fieldAccess.getValue( obj );
                if ( value == null && !forceKeep) {
                    nonNullValues.remove ( fieldAccess );
                }
            }
            values = nonNullValues;
        }

        if ( !includeEmpty ) {
            List<FieldAccess> nonEmptyValues = new LinkedList<> ( values );
            for ( FieldAccess fieldAccess : values ) {

                boolean forceKeep = false;
                if ( useAnnotations ) {
                    final Map<String, Object> jsonInclude = fieldAccess.getAnnotationData ( "JsonInclude" );
                    if ( jsonInclude != null ) {
                        String value = ( String ) jsonInclude.get ( "value" );
                        if ( value.equals ( "ALWAYS" ) || value.equals ( "NON_NULL" )) {
                            forceKeep = true;
                        }
                    }
                }

                Object value = fieldAccess.getValue ( obj );


                if ( value instanceof Collection ||
                        value instanceof CharSequence ||
                        value instanceof Map ||
                        Reflection.isArray ( obj ) ) {
                    if (  !forceKeep && Reflection.len ( value ) == 0 ) {
                        nonEmptyValues.remove ( fieldAccess );
                    }
                }
            }

            values = nonEmptyValues;
        }
        if ( useAnnotations ) {

            Set<String> propertiesToIgnore  = getProperitesToIgnoreAsSet(obj);


            List<FieldAccess> nonIgnoredValues = new LinkedList<> ( values  );
            for ( FieldAccess fieldAccess : values ) {

                if ( Sets.in ( fieldAccess.getName(), propertiesToIgnore ) ) {
                    nonIgnoredValues.remove ( fieldAccess );
                } else if (fieldAccess.hasAnnotation ( "JsonIgnore" )) {
                    final Map<String, Object> jsonIgnore = fieldAccess.getAnnotationData ( "JsonIgnore" );
                    boolean ignore = (Boolean) jsonIgnore.get ( "value" );
                    if (ignore) {
                        nonIgnoredValues.remove ( fieldAccess );
                    }
                }
            }
            values = nonIgnoredValues;
        }

        if ( handleSimpleBackReference || handleComplexBackReference ) {



            List<FieldAccess> nonBackReferenceValues = new LinkedList<> ( values  );
            for ( FieldAccess fieldAccess : values ) {

                Object value = fieldAccess.getValue( obj );

                /* handle simple back reference. */
                if (handleSimpleBackReference &&  value == obj ) {
                    nonBackReferenceValues.remove ( fieldAccess );
                    continue;
                }

                if ( handleComplexBackReference ) {
                    if ( idMap.containsKey ( value ) ) {
                        nonBackReferenceValues.remove ( fieldAccess );
                    } else {
                        idMap.put ( value, value );
                    }
                }

            }
            values = nonBackReferenceValues;
        }
        return values;
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
            builder.addChar( '\"' );
            builder.add( entry.getKey().toString() );
            builder.addChar( '\"' );
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
                builder.append( ',' );
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
