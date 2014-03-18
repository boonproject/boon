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
import org.boon.core.Typ;
import org.boon.core.Conversions;
import org.boon.core.Type;
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
    
    

    private String name;
    private Operator operator;
    protected VALUE value;
    protected VALUE[] values;
    private final int hashCode;
    private final String toString;

    private boolean initialized;
    private Criterion nativeDelegate;
    private boolean useDelegate;

    private FieldAccess field;

    private Object objectUnderTest;

    private Map<String, FieldAccess> fields;
    private ThisMap thisFields;

    public Criterion( String name, Operator operator, VALUE... values ) {
        requireNonNull( name, "name cannot be null" );
        requireNonNull( operator, "operator cannot be null" );
        requireNonNull( values, "values cannot be null" );

        this.name = name;
        this.operator = operator;
        this.setValues( values );
        hashCode = doHashCode();
        toString = doToString();
    }


    public String getName() {
        return name;
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
        this.values = values;
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


    public void initByClass( Class clazz ) {

        this.fields = getFieldsInternal( clazz );
        initIfNeeded();
    }

    public void initByFields( Map<String, FieldAccess> fields ) {
        this.fields = fields;
        initIfNeeded();
    }


    //Only called when part of group.
    public void prepareForGroupTest( Map<String, FieldAccess> fields, Object owner ) {

        this.fields = fields;
        this.objectUnderTest = owner;


    }

    public void cleanAfterGroupTest() {
        clean();
    }

    public void clean() {
        this.field = null;
        this.fields = null;
        this.objectUnderTest = null;

    }



    @Override
    public boolean test( Object o ) {


        try {

            requireNonNull( o, "object under test can't be null" );

            this.objectUnderTest = o;

            initIfNeeded();
            if ( this.useDelegate ) {

                return this.nativeDelegate.resolve( fields, o );

            }

            FieldAccess field = fields().get(name);

            if (field == null && o instanceof Map)  {
                    return false;
            }

            boolean result = resolve( fields(), o );


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

    private FieldAccess field() {
        if ( field == null ) {
            field = fields().get( this.name );
        }
        return field;
    }

    private Map<String, FieldAccess> fields() {

        if (this.thisFields !=null) {
            this.thisFields.thisField.thisObject = this.objectUnderTest;
            return this.thisFields;
        }

        if ( fields == null ) {
            fields = getFieldsInternal( this.objectUnderTest );
        }
        return fields;
    }

    public static abstract class PrimitiveCriterion extends Criterion {

        public PrimitiveCriterion( String name, Operator operator, Object... objects ) {
            super( name, operator, objects );
        }

        @Override
        public boolean test( Object o ) {

            Map<String, FieldAccess> fields = getFieldsInternal( o );
            return resolve( fields, o );
        }

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



        if (name.equals("this") || name.contains(".") || name.contains("[")) {

            this.thisFields = new ThisMap(name, this.objectUnderTest);
            return;
        }

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
                nativeDelegate = ObjectFilter.eqByte( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqByte( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltByte( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteByte( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtByte( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteByte( name, ( value ) );
                break;

            case IN:
                nativeDelegate = ObjectFilter.inBytes( name, Conversions.barray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInBytes( name, Conversions.barray( values ) );

                break;


            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenByte( name, ( value ),
                        Conversions.toByte( values[1] ) );
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
                nativeDelegate = ObjectFilter.eqInt( name, v );
                break;

            case NOT_EQUAL:
                nativeDelegate = ObjectFilter.notEqInt( name, v );
                break;

            case LESS_THAN:
                nativeDelegate = ObjectFilter.ltInt( name, v );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = ObjectFilter.lteInt( name, v );
                break;

            case GREATER_THAN:
                nativeDelegate = ObjectFilter.gtInt( name, v );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = ObjectFilter.gteInt( name, v );
                break;

            case BETWEEN:
                nativeDelegate = ObjectFilter.betweenInt( name, v,
                        Conversions.toInt( values[1] ) );
                break;

            case IN:
                nativeDelegate = ObjectFilter.inInts( name, Conversions.iarray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = ObjectFilter.notInInts( name, Conversions.iarray( values ) );

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
        public Type typeEnum() {
            return null;
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
            return null;
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
    }
}
