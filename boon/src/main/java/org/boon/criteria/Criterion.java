/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.criteria;

import org.boon.Exceptions;
import org.boon.Str;
import org.boon.core.Typ;
import org.boon.core.Conversions;
import org.boon.core.TypeType;
import org.boon.core.Value;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.criteria.internal.Criteria;
import org.boon.criteria.internal.Operator;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static org.boon.Boon.sputl;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.*;



public abstract class Criterion<VALUE> extends Criteria {
    
    

    private Object name;
    private Operator operator;
    protected VALUE value;
    protected VALUE value2;

    protected VALUE[] values;
    private final int hashCode;
    private final String toString;

    private boolean initialized;
    private Criterion nativeDelegate;
    private boolean useDelegate;

    private FieldAccess field;

    protected Object objectUnderTest;

    private Map<String, FieldAccess> fields;

    private ThisField fakeField = null;

    private boolean path;

    public Criterion( String name, Operator operator, VALUE... values ) {
        requireNonNull( name, "name cannot be null" );
        requireNonNull( operator, "operator cannot be null" );
        requireNonNull( values, "values cannot be null" );


        path = isPropPath(name);

        this.name = name;
        this.operator = operator;
        this.setValues( values );
        hashCode = doHashCode();
        toString = doToString();
    }


    /**
     * Gets the field value.
     * @return the value of the field
     */
    public FieldAccess field(  ) {


        FieldAccess field;

        if (path) {
            field = BeanUtils.idxField(objectUnderTest, name.toString());


            if (field == null) {
                return fakeField();
            }

            return field;
        }

        if (name instanceof Enum) {
            name = Str.camelCaseLower(name.toString());
        }

        field = fields().get(name);

        return field;
    }

    private FieldAccess fakeField() {

        if (fakeField == null) {
            fakeField = new ThisField(this.name.toString(), objectUnderTest);
        }

        fakeField.thisObject = objectUnderTest;

        return fakeField;
    }


    /**
     * Gets the field value.
     * @return the value of the field
     */
    public Object fieldValue(  ) {

        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getValue(objectUnderTest);
        } else {
            return BeanUtils.atIndex(objectUnderTest, name.toString());
        }
    }

    public int fieldInt(  ) {
        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getInt(objectUnderTest);
        } else {
            return BeanUtils.idxInt(objectUnderTest, name.toString());

        }
    }


    public short fieldShort(  ) {

        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getShort(objectUnderTest);
        } else {
            return BeanUtils.idxShort(objectUnderTest, name.toString());

        }

    }



    public long fieldLong(  ) {
        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getLong(objectUnderTest);
        } else {
            return BeanUtils.idxLong(objectUnderTest, name.toString());

        }


    }


    public float fieldFloat(  ) {

        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getFloat(objectUnderTest);
        } else {
            return BeanUtils.idxFloat(objectUnderTest, name.toString());

        }

    }


    public double fieldDouble(  ) {



        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getDouble(objectUnderTest);
        } else {
            return BeanUtils.idxDouble(objectUnderTest, name.toString());

        }
    }


    public boolean fieldBoolean(  ) {


        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getBoolean(objectUnderTest);
        } else {
            return BeanUtils.idxBoolean(objectUnderTest, name.toString());

        }

    }


    public byte fieldByte(  ) {


        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getByte(objectUnderTest);
        } else {
            return BeanUtils.idxByte(objectUnderTest, name.toString());

        }
    }

    public char fieldChar(  ) {
        if (!path) {
            FieldAccess field1 = this.field();
            return field1.getChar(objectUnderTest);
        } else {
            return BeanUtils.idxChar(objectUnderTest, name.toString());

        }

    }




    public Set<Object> valueSet(  ) {

        if (!convert1st) {

            HashSet<Object> set = new HashSet<>(values.length);
            FieldAccess field = this.field();
            Class<?> classType = field.type();

            for (Object v : values) {
                v =  Conversions.coerce(classType, v);
                set.add(v);
            }

            value = (VALUE)set;

            convert1st = true;
        }
        return (Set<Object>)value;
    }


    boolean convert1st;

    /**
     * Gets the field value.
     * @return the value of the field
     */
    public Object value(  ) {
        if (!convert1st) {
            FieldAccess field = field();
            if (field != null) {
                switch (field.typeEnum()) {


                    case NUMBER:

                        this.value = (VALUE) Conversions.coerce(field.type(), this.value);
                        return new MyNumber(this.value);


                    case ARRAY:
                    case ARRAY_INT:
                    case ARRAY_BYTE:
                    case ARRAY_SHORT:
                    case ARRAY_FLOAT:
                    case ARRAY_DOUBLE:
                    case ARRAY_LONG:
                    case ARRAY_STRING:
                    case ARRAY_OBJECT:
                    case COLLECTION:
                    case SET:
                    case LIST:
                        this.value = (VALUE) Conversions.coerce(field.getComponentClass(), this.value);
                        break;
                    default:

                        this.value = (VALUE) Conversions.coerce(field.type(), this.value);
                }
            }
            convert1st = true;
        }
        return value;
    }

    boolean convert2nd;

    /**
     * Gets the field value.
     * @return the value of the field
     */
    public Object value2(  ) {
        if (!convert2nd) {
            FieldAccess field = this.field();
            this.value2 = (VALUE) Conversions.coerce(field.type(), this.value2);
            convert2nd = true;
        }
        return value2;
    }





    public String getName() {
        return name.toString();
    }

    public Operator getOperator() {
        return operator;
    }


    public VALUE getValue() {
        return value;
    }


    public VALUE[] getValues() {
        return values;
    }

    public void setValues( VALUE[] values ) {
        if ( values.length > 0 ) {
            this.value = values[ 0 ];
        }
        if ( values.length > 1 ) {
            this.value2 = values[ 1 ];
        }

        this.values = values;
    }


    /**
     * Is this a property path?
     * @param prop property
     * @return true or false
     */
    private static boolean isPropPath(String prop) {
        return BeanUtils.isPropPath(prop);
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Criterion ) ) return false;

        Criterion criterion = ( Criterion ) o;

        if ( name != null ? !name.equals( criterion.name ) : criterion.name != null ) return false;
        if ( operator != criterion.operator ) return false;
        if ( value != null ? !value.equals( criterion.value ) : criterion.value != null ) return false;
        if ( !Arrays.equals( values, criterion.values ) ) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return hashCode;
    }

    public int doHashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( operator != null ? operator.hashCode() : 0 );
        result = 31 * result + ( value != null ? value.hashCode() : 0 );
        result = 31 * result + ( values != null ? Arrays.hashCode( values ) : 0 );
        return result;
    }

    @Override
    public String toString() {
        return toString;
    }


    public String doToString() {
        CharBuf builder = CharBuf.create( 80 );
        builder.add( "c{" );
        builder.add( "\"name\":'" );
        builder.add( String.valueOf( name ) );
        builder.add( ", \"operator\":" );
        builder.add( String.valueOf( operator ) );
        builder.add( ", \"set\":" );
        builder.add( String.valueOf( value ) );
        builder.add( ", \"update\":" );
        builder.add( Arrays.toString( values ) );
        builder.add( "}" );
        return builder.toString();
    }

    public boolean isInitialized() {
        return initialized;
    }



    public void clean() {
        this.field = null;
        this.fields = null;
        this.objectUnderTest = null;

    }


    public abstract boolean resolve( Object o );

    @Override
    public boolean test( Object o ) {


        FieldAccess field = null;

        try {

            requireNonNull( o, "object under test can't be null" );

            this.objectUnderTest = o;

            initIfNeeded();
            if ( this.useDelegate ) {

                this.nativeDelegate.fields = this.fields;
                this.nativeDelegate.objectUnderTest = this.objectUnderTest;
                return this.nativeDelegate.resolve(  o );

            }

            field = field();


            if ( !path && field == null && o instanceof Map) {
                return false;
            }

            boolean result = resolve( o );


            return result;

        } catch ( Exception ex ) {

            return Exceptions.handle( Typ.bool,
                    sputl( "In class " + this.getClass().getName(),
                            "the test method is unable to test the following criteria operator",
                            String.valueOf( this.getOperator() ),
                            sputs( "The field name is          :          ", this.getName() ),
                            sputs( "The value is               :          ", this.getValue() ),
                            sputs( "The value type is          :          ", this.getValue().getClass().getName() ),
                            sputs( "The object under test      :          ", this.objectUnderTest ),
                            sputs( "The object under test type :          ",
                                    this.objectUnderTest == null ? "null" : this.objectUnderTest.getClass().getName() ),
                            sputs( "Field                      :          ",
                                    field ),
                            sputs( "Fields                     :          ",
                                    fields ),

                            sputs()

                    )
                    , ex );
        }
    }


    private Map<String, FieldAccess> fields() {


        if (objectUnderTest instanceof Map) {
            return BeanUtils.getFieldsFromObject(objectUnderTest);
        }

        if (fields == null) {
            fields =
             BeanUtils.getFieldsFromObject(objectUnderTest);
        }

        return fields;


//        if (objectUnderTest instanceof Map) {
//            final Map<String, Object> map = (Map<String, Object>) objectUnderTest;
//            return new Map<String, FieldAccess>() {
//
//                @Override
//                public int size() {
//                    return map.size();
//                }
//
//                @Override
//                public boolean isEmpty() {
//                    return map.isEmpty();
//                }
//
//                @Override
//                public boolean containsKey(Object key) {
//                    return map.containsKey(key);
//                }
//
//                @Override
//                public boolean containsValue(Object value) {
//                    return map.containsValue(value);
//                }
//
//                @Override
//                public FieldAccess get(Object okey) {
//
//                    final String key = okey.toString();
//
//                    return new FieldAccess() {
//                        @Override
//                        public boolean injectable() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean requiresInjection() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isNamed() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean hasAlias() {
//                            return false;
//                        }
//
//                        @Override
//                        public String alias() {
//                            return key;
//                        }
//
//                        @Override
//                        public String named() {
//                            return key;
//                        }
//
//                        @Override
//                        public String name() {
//                            return key;
//                        }
//
//                        @Override
//                        public Object getValue(Object obj) {
//
//                            Map<String, Object> map = (Map<String, Object>) obj;
//
//                            return map.get(key);
//                        }
//
//                        @Override
//                        public void setValue(Object obj, Object value) {
//
//                            Map<String, Object> map = (Map<String, Object>) obj;
//
//                            map.put(key, value);
//
//                        }
//
//                        @Override
//                        public void setFromValue(Object obj, Value value) {
//                            Map<String, Object> map = (Map<String, Object>) obj;
//
//                            map.put(key, value.toValue());
//
//                        }
//
//                        @Override
//                        public boolean getBoolean(Object obj) {
//                            return false;
//                        }
//
//                        @Override
//                        public void setBoolean(Object obj, boolean value) {
//
//                        }
//
//                        @Override
//                        public int getInt(Object obj) {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setInt(Object obj, int value) {
//
//                        }
//
//                        @Override
//                        public short getShort(Object obj) {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setShort(Object obj, short value) {
//
//                        }
//
//                        @Override
//                        public char getChar(Object obj) {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setChar(Object obj, char value) {
//
//                        }
//
//                        @Override
//                        public long getLong(Object obj) {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setLong(Object obj, long value) {
//
//                        }
//
//                        @Override
//                        public double getDouble(Object obj) {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setDouble(Object obj, double value) {
//
//                        }
//
//                        @Override
//                        public float getFloat(Object obj) {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setFloat(Object obj, float value) {
//
//                        }
//
//                        @Override
//                        public byte getByte(Object obj) {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setByte(Object obj, byte value) {
//
//                        }
//
//                        @Override
//                        public Object getObject(Object obj) {
//                            Map<String, Object> map = (Map<String, Object>) obj;
//
//                            return map.get(key);
//
//                        }
//
//                        @Override
//                        public void setObject(Object obj, Object value) {
//                            Map<String, Object> map = (Map<String, Object>) obj;
//
//                            map.put(key, value);
//
//
//                        }
//
//
//                        @Override
//                        public Class<?> type() {
//
//                            Object value = map.get(key);
//                            if (value==null) {
//                                return Object.class;
//                            } else {
//                                return value.getClass();
//                            }
//
//                        }
//
//                        @Override
//                        public TypeType typeEnum() {
//
//                            Object value = map.get(key);
//                            if (value==null) {
//                                return TypeType.OBJECT;
//                            } else {
//                                return TypeType.getInstanceType(value);
//                            }
//                        }
//
//                        @Override
//                        public boolean isPrimitive() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isFinal() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isStatic() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isVolatile() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isQualified() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isReadOnly() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean isWriteOnly() {
//                            return false;
//                        }
//
//
//                        @Override
//                        public Class<?> declaringParent() {
//                            return Object.class;
//                        }
//
//                        @Override
//                        public Object parent() {
//                            return map;
//                        }
//
//                        @Override
//                        public Field getField() {
//                            return null;
//                        }
//
//                        @Override
//                        public boolean include() {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean ignore() {
//                            return false;
//                        }
//
//                        @Override
//                        public ParameterizedType getParameterizedType() {
//                            return null;
//                        }
//
//                        @Override
//                        public Class<?> getComponentClass() {
//                            return null;
//                        }
//
//                        @Override
//                        public boolean hasAnnotation(String annotationName) {
//                            return false;
//                        }
//
//                        @Override
//                        public Map<String, Object> getAnnotationData(String annotationName) {
//                            return null;
//                        }
//
//                        @Override
//                        public boolean isViewActive(String activeView) {
//                            return false;
//                        }
//
//                        @Override
//                        public void setStaticValue(Object newValue) {
//
//                        }
//
//                        @Override
//                        public TypeType componentType() {
//                            return null;
//                        }
//                    };
//                }
//
//                @Override
//                public FieldAccess put(String key, FieldAccess value) {
//                    return null;
//                }
//
//                @Override
//                public FieldAccess remove(Object key) {
//                    return null;
//                }
//
//                @Override
//                public void putAll(Map<? extends String, ? extends FieldAccess> m) {
//
//                }
//
//                @Override
//                public void clear() {
//
//                }
//
//                @Override
//                public Set<String> keySet() {
//                    return map.keySet();
//                }
//
//                @Override
//                public Collection<FieldAccess> values() {
//                    return null;
//                }
//
//                @Override
//                public Set<Entry<String, FieldAccess>> entrySet() {
//                    return null;
//                }
//            };
//        } else {
//
//        return BeanUtils.getFieldsFromObject(objectUnderTest);
//        }
    }



    private void initForShortValue( short v ) {

        this.value = ( VALUE ) ( Short ) v;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = ObjectFilter.eqShort( name, v );
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqShort( name, v );
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltShort( name, v );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteShort( name, v );
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtShort( name, v );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteShort( name, v );
                break;

            case IN:
                nativeDelegate = ObjectFilter.inShorts( name, Conversions.sarray( values ) );
                break;


            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenShort( name, ( v ),
                        Conversions.toShort( values[1] ) );
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInShorts( name, Conversions.sarray( values ) );

                break;

            default:
                useDelegate = false;
        }
    }


    private void initIfNeeded() {

        if ( initialized ) return;
        initialized = true;

        String name = this.name.toString();

        FieldAccess field = field();
        if ( field == null ) {
            return;
        }

        Class type = field.type();


        if ( !type.isPrimitive() && type != Typ.date ) {
            return;
        }


        if ( type == Typ.date ) {

            if ( !( value instanceof Date ) ) {
                initForDate();
            }
            return;
        }


        useDelegate = true;


        if ( type == Typ.intgr ) {
            int v = Conversions.toInt( value );
            initForInt( v );
        } else if ( type == Typ.bt ) {

            byte v = Conversions.toByte( value );

            initForByte( v );

        } else if ( type == Typ.shrt ) {

            short v = Conversions.toShort( value );

            initForShortValue( v );

        } else if ( type == Typ.lng ) {

            long v = Conversions.toLong( value );

            initForLong( v );


        } else if ( type == Typ.flt ) {

            float v = Conversions.toFloat( value );


            initForFloat( v );

        } else if ( type == Typ.dbl ) {

            double v = Conversions.toDouble( value );

            initForDouble( v );


        } else if ( type == Typ.bln ) {


            switch ( operator ) {
                case EQUAL:
                    nativeDelegate = ObjectFilter.eqBoolean( name, Conversions.toBoolean( value ) );
                    break;

                case NOT_EQUAL:
                    nativeDelegate = ObjectFilter.notEqBoolean( name, Conversions.toBoolean( value ) );
                    break;


                default:
                    useDelegate = false;
            }

        } else if ( type == Typ.chr ) {

            char v = Conversions.toChar( value );
            initForChar( v );

        }


    }

    private void initForChar( char value ) {


        this.value = ( VALUE ) ( Character ) value;

        switch ( operator ) {


            case EQUAL:
                nativeDelegate = ObjectFilter.eqChar( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqChar( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltChar( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteChar( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtChar( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteChar( name, ( value ) );
                break;

            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenChar( name, ( value ),
                        Conversions.toChar( values[1] ) );
                break;

            case IN:
                nativeDelegate = ObjectFilter.inChars( name, Conversions.carray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInChars( name, Conversions.carray( values ) );
                break;

            default:
                useDelegate = false;
        }
    }

    private void initForDouble( double value ) {

        this.value = ( VALUE ) ( Double ) value;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = ObjectFilter.eqDouble( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqDouble( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltDouble( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteDouble( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtDouble( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteDouble( name, ( value ) );
                break;

            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenDouble( name, ( value ),
                        Conversions.toDouble( values[1] ) );
                break;

            case IN:
                nativeDelegate = ObjectFilter.inDoubles( name,
                        Conversions.darray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInDoubles( name,
                        Conversions.darray( values ) );
                break;

            default:
                useDelegate = false;
        }
    }

    private void initForFloat( float value ) {

        this.value = ( VALUE ) ( Float ) value;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = ObjectFilter.eqFloat( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqFloat( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltFloat( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteFloat( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtFloat( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteFloat( name, ( value ) );
                break;

            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenFloat( name, ( value ),
                        Conversions.toFloat( values[1] ) );
                break;

            case IN:
                nativeDelegate = ObjectFilter.inFloats( name, Conversions.farray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInFloats( name, Conversions.farray( values ) );

                break;

            default:
                useDelegate = false;
        }
    }

    private void initForLong( long value ) {

        this.value = ( VALUE ) ( Long ) value;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = ObjectFilter.eqLong( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqLong( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltLong( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteLong( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtLong( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteLong( name, ( value ) );
                break;

            case IN:
                nativeDelegate = ObjectFilter.inLongs( name, Conversions.larray( values ) );
                break;


            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenLong( name, ( value ),
                        Conversions.toLong( values[1] ) );
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInLongs( name, Conversions.larray( values ) );

                break;

            default:
                useDelegate = false;
        }
    }

    private void initForByte( byte value ) {

        this.value = ( VALUE ) ( Byte ) value;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = ObjectFilter.eqByte(name, (value));
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqByte(name, (value));
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltByte(name, (value));
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteByte(name, (value));
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtByte(name, (value));
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteByte(name, (value));
                break;

            case IN:
                nativeDelegate = ObjectFilter.inBytes(name, Conversions.barray(values));
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInBytes(name, Conversions.barray(values));

                break;


            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenByte(name, (value),
                        Conversions.toByte(values[1]));
                break;


            default:
                useDelegate = false;
        }
    }

    private void initForDate() {
        value = ( VALUE ) Conversions.toDate( value );

        if ( operator == Operator.BETWEEN ) {
            values[ 0 ] = ( VALUE ) Conversions.toDate( values[ 0 ] );

            values[ 1 ] = ( VALUE ) Conversions.toDate( values[ 1 ] );

        }

    }

    private void initForInt( int v ) {
        this.value = ( VALUE ) ( Integer ) v;


        switch ( operator ) {
            case EQUAL:
                nativeDelegate = ObjectFilter.eqInt(name, v);
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqInt(name, v);
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltInt(name, v);
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteInt(name, v);
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtInt(name, v);
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteInt(name, v);
                break;

            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenInt(name, v,
                        Conversions.toInt(values[1]));
                break;

            case IN:
                nativeDelegate = ObjectFilter.inInts(name, Conversions.iarray(values));
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInInts(name, Conversions.iarray(values));

                break;

            default:
                useDelegate = false;
        }
    }



    static class ThisMap implements Map<String, FieldAccess> {

        ThisField thisField;
        private final String name;


        ThisMap (String name, Object thisObject) {
            this.name = name;
            this.thisField = new ThisField(name, thisObject);
        }


        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public FieldAccess get(Object key) {
                return thisField;

        }

        @Override
        public FieldAccess put(String key, FieldAccess value) {
            return null;
        }

        @Override
        public FieldAccess remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends String, ? extends FieldAccess> m) {

        }

        @Override
        public void clear() {

        }

        @Override
        public Set<String> keySet() {
            return null;
        }

        @Override
        public Collection<FieldAccess> values() {
            return null;
        }

        @Override
        public Set<Entry<String, FieldAccess>> entrySet() {
            return null;
        }
    }

    static class ThisField implements FieldAccess {


        Object thisObject;
        private final String name;

        ThisField(String name, Object thisObject) {
            this.name = name;
            this.thisObject = thisObject;


        }

        @Override
        public boolean injectable() {
            return false;
        }

        @Override
        public boolean requiresInjection() {
            return false;
        }

        @Override
        public boolean isNamed() {
            return false;
        }

        @Override
        public boolean hasAlias() {
            return false;
        }

        @Override
        public String alias() {
            return null;
        }

        @Override
        public String named() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public Object getValue(Object obj) {
            if (name.equals("this")) {
                return thisObject;
            } else {
                return BeanUtils.atIndex(thisObject, name);
            }
        }

        @Override
        public void setValue(Object obj, Object value) {

        }

        @Override
        public void setFromValue(Object obj, Value value) {

        }

        @Override
        public boolean getBoolean(Object obj) {
            return false;
        }

        @Override
        public void setBoolean(Object obj, boolean value) {

        }

        @Override
        public int getInt(Object obj) {
            return 0;
        }

        @Override
        public void setInt(Object obj, int value) {

        }

        @Override
        public short getShort(Object obj) {
            return 0;
        }

        @Override
        public void setShort(Object obj, short value) {

        }

        @Override
        public char getChar(Object obj) {
            return 0;
        }

        @Override
        public void setChar(Object obj, char value) {

        }

        @Override
        public long getLong(Object obj) {
            return 0;
        }

        @Override
        public void setLong(Object obj, long value) {

        }

        @Override
        public double getDouble(Object obj) {
            return 0;
        }

        @Override
        public void setDouble(Object obj, double value) {

        }

        @Override
        public float getFloat(Object obj) {
            return 0;
        }

        @Override
        public void setFloat(Object obj, float value) {

        }

        @Override
        public byte getByte(Object obj) {
            return 0;
        }

        @Override
        public void setByte(Object obj, byte value) {

        }

        @Override
        public Object getObject(Object obj) {
            return thisObject;
        }

        @Override
        public void setObject(Object obj, Object value) {

        }

        @Override
        public TypeType typeEnum() {
            Object o = this.getValue(this.thisObject);
            return TypeType.getInstanceType(o);
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }

        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public boolean isVolatile() {
            return false;
        }

        @Override
        public boolean isQualified() {
            return false;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public boolean isWriteOnly() {
            return false;
        }

        @Override
        public Class<?> type() {
            if (this.name.equals("this")) {
                return this.thisObject != null ? this.thisObject.getClass() : Object.class;
            } else {
                return Object.class;
            }
        }

        @Override
        public Class<?> declaringParent() {
            return null;
        }

        @Override
        public Object parent() {
            return null;
        }

        @Override
        public Field getField() {
            return null;
        }

        @Override
        public boolean include() {
            return false;
        }

        @Override
        public boolean ignore() {
            return false;
        }

        @Override
        public ParameterizedType getParameterizedType() {
            return null;
        }

        @Override
        public Class<?> getComponentClass() {
            Object o = this.getValue(this.thisObject);
            if (o instanceof Collection) {
                Collection c = (Collection) o;
                if (c.iterator().hasNext()) {
                    return c.iterator().next().getClass();
                }

            }
            return Object.class;

        }

        @Override
        public boolean hasAnnotation(String annotationName) {
            return false;
        }

        @Override
        public Map<String, Object> getAnnotationData(String annotationName) {
            return null;
        }

        @Override
        public boolean isViewActive(String activeView) {
            return false;
        }

        @Override
        public void setStaticValue(Object newValue) {

        }

        @Override
        public TypeType componentType() {
            return null;
        }
    }

    private static class MyNumber extends Number implements Comparable<Number> {

        Object value;

        MyNumber(Object value) {

            this.value = value;
        }

        @Override
        public int intValue() {
            return Conversions.toInt(value);
        }

        @Override
        public long longValue() {

            return Conversions.toLong(value);
        }

        @Override
        public float floatValue() {

            return Conversions.toFloat(value);
        }

        @Override
        public double doubleValue() {
            return Conversions.toDouble(value);
        }

        @Override
        public int compareTo(Number number) {
            double thisDouble = this.doubleValue();

            double thatValue = number.doubleValue();

            if (thisDouble > thatValue) {
                return 1;
            } else if(thisDouble < thatValue) {
                return -1;
            } else {
                return 0;
            }

        }
    }
}
