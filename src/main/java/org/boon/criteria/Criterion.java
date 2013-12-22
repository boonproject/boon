package org.boon.criteria;

import org.boon.Exceptions;
import org.boon.core.Typ;
import org.boon.core.reflection.Conversions;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static org.boon.Boon.sputl;
import static org.boon.Boon.sputs;


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

    public Criterion( String name, Operator operator, VALUE... values ) {
        Objects.requireNonNull( name, "name cannot be null" );
        Objects.requireNonNull( operator, "operator cannot be null" );
        Objects.requireNonNull( values, "values cannot be null" );

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

            Objects.requireNonNull( o, "object under test can't be null" );

            this.objectUnderTest = o;

            initIfNeeded();
            if ( this.useDelegate ) {

                return this.nativeDelegate.resolve( fields, o );
            }


            boolean result = resolve( fields, o );

            return result;

        } catch ( Exception ex ) {
            return Exceptions.handle( Typ.bool,
                    sputl( "In class " + this.getClass().getName(),
                            "the test method is unable to test the following criteria operator",
                            Objects.toString( this.getOperator() ),
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
                nativeDelegate = CriteriaFactory.eqShort( name, v );
                break;

            case NOT_EQUAL:
                nativeDelegate = CriteriaFactory.notEqShort( name, v );
                break;

            case LESS_THAN:
                nativeDelegate = CriteriaFactory.ltShort( name, v );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.lteShort( name, v );
                break;

            case GREATER_THAN:
                nativeDelegate = CriteriaFactory.gtShort( name, v );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.gteShort( name, v );
                break;

            case IN:
                nativeDelegate = CriteriaFactory.inShorts( name, Conversions.sarray( values ) );
                break;


            case BETWEEN:
                nativeDelegate = CriteriaFactory.betweenShort( name, ( v ),
                        Conversions.toShort( values[ 1 ] ) );
                break;


            case NOT_IN:
                nativeDelegate = CriteriaFactory.notInShorts( name, Conversions.sarray( values ) );

                break;

            default:
                useDelegate = false;
        }
    }


    private void initIfNeeded() {

        if ( initialized ) return;
        initialized = true;

        FieldAccess field = field();
        if ( field == null ) {
            return;
        }

        Class type = field.getType();


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
                    nativeDelegate = CriteriaFactory.eqBoolean( name, Conversions.toBoolean( value ) );
                    break;

                case NOT_EQUAL:
                    nativeDelegate = CriteriaFactory.notEqBoolean( name, Conversions.toBoolean( value ) );
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
                nativeDelegate = CriteriaFactory.eqChar( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = CriteriaFactory.notEqChar( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = CriteriaFactory.ltChar( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.lteChar( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = CriteriaFactory.gtChar( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.gteChar( name, ( value ) );
                break;

            case BETWEEN:
                nativeDelegate = CriteriaFactory.betweenChar( name, ( value ),
                        Conversions.toChar( values[ 1 ] ) );
                break;

            case IN:
                nativeDelegate = CriteriaFactory.inChars( name, Conversions.carray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = CriteriaFactory.notInChars( name, Conversions.carray( values ) );
                break;

            default:
                useDelegate = false;
        }
    }

    private void initForDouble( double value ) {

        this.value = ( VALUE ) ( Double ) value;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = CriteriaFactory.eqDouble( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = CriteriaFactory.notEqDouble( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = CriteriaFactory.ltDouble( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.lteDouble( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = CriteriaFactory.gtDouble( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.gteDouble( name, ( value ) );
                break;

            case BETWEEN:
                nativeDelegate = CriteriaFactory.betweenDouble( name, ( value ),
                        Conversions.toDouble( values[ 1 ] ) );
                break;

            case IN:
                nativeDelegate = CriteriaFactory.inDoubles( name,
                        Conversions.darray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = CriteriaFactory.notInDoubles( name,
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
                nativeDelegate = CriteriaFactory.eqFloat( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = CriteriaFactory.notEqFloat( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = CriteriaFactory.ltFloat( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.lteFloat( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = CriteriaFactory.gtFloat( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.gteFloat( name, ( value ) );
                break;

            case BETWEEN:
                nativeDelegate = CriteriaFactory.betweenFloat( name, ( value ),
                        Conversions.toFloat( values[ 1 ] ) );
                break;

            case IN:
                nativeDelegate = CriteriaFactory.inFloats( name, Conversions.farray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = CriteriaFactory.notInFloats( name, Conversions.farray( values ) );

                break;

            default:
                useDelegate = false;
        }
    }

    private void initForLong( long value ) {

        this.value = ( VALUE ) ( Long ) value;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = CriteriaFactory.eqLong( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = CriteriaFactory.notEqLong( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = CriteriaFactory.ltLong( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.lteLong( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = CriteriaFactory.gtLong( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.gteLong( name, ( value ) );
                break;

            case IN:
                nativeDelegate = CriteriaFactory.inLongs( name, Conversions.larray( values ) );
                break;


            case BETWEEN:
                nativeDelegate = CriteriaFactory.betweenLong( name, ( value ),
                        Conversions.toLong( values[ 1 ] ) );
                break;


            case NOT_IN:
                nativeDelegate = CriteriaFactory.notInLongs( name, Conversions.larray( values ) );

                break;

            default:
                useDelegate = false;
        }
    }

    private void initForByte( byte value ) {

        this.value = ( VALUE ) ( Byte ) value;

        switch ( operator ) {
            case EQUAL:
                nativeDelegate = CriteriaFactory.eqByte( name, ( value ) );
                break;

            case NOT_EQUAL:
                nativeDelegate = CriteriaFactory.notEqByte( name, ( value ) );
                break;

            case LESS_THAN:
                nativeDelegate = CriteriaFactory.ltByte( name, ( value ) );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.lteByte( name, ( value ) );
                break;

            case GREATER_THAN:
                nativeDelegate = CriteriaFactory.gtByte( name, ( value ) );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.gteByte( name, ( value ) );
                break;

            case IN:
                nativeDelegate = CriteriaFactory.inBytes( name, Conversions.barray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = CriteriaFactory.notInBytes( name, Conversions.barray( values ) );

                break;


            case BETWEEN:
                nativeDelegate = CriteriaFactory.betweenByte( name, ( value ),
                        Conversions.toByte( values[ 1 ] ) );
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
                nativeDelegate = CriteriaFactory.eqInt( name, v );
                break;

            case NOT_EQUAL:
                nativeDelegate = CriteriaFactory.notEqInt( name, v );
                break;

            case LESS_THAN:
                nativeDelegate = CriteriaFactory.ltInt( name, v );
                break;

            case LESS_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.lteInt( name, v );
                break;

            case GREATER_THAN:
                nativeDelegate = CriteriaFactory.gtInt( name, v );
                break;

            case GREATER_THAN_EQUAL:
                nativeDelegate = CriteriaFactory.gteInt( name, v );
                break;

            case BETWEEN:
                nativeDelegate = CriteriaFactory.betweenInt( name, v,
                        Conversions.toInt( values[ 1 ] ) );
                break;

            case IN:
                nativeDelegate = CriteriaFactory.inInts( name, Conversions.iarray( values ) );
                break;


            case NOT_IN:
                nativeDelegate = CriteriaFactory.notInInts( name, Conversions.iarray( values ) );

                break;

            default:
                useDelegate = false;
        }
    }


}
