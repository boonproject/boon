package org.boon.core.reflection.fields;

import org.boon.Exceptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.boon.Boon.sputs;

public class PropertyField extends BaseField {
    final Method getter;
    final Method setter;


    public PropertyField( String name, Method setter, Method getter ) {

        super (  name,  getter,  setter);

        this.getter = getter;
        this.setter = setter;



//        MethodHandles.Lookup lookup = MethodHandles.lookup();
//        MethodType methodType
//                = MethodType.methodType ( this.type );
//        MethodHandle methodHandle = null;
//        CallSite callSiteMethod;
//
//        if ( parentType != null && getter != null ) {
//            try {
//                methodHandle = lookup.findVirtual ( this.parentType, getter.getName (), methodType );
//            } catch ( NoSuchMethodException e ) {
//               Exceptions.handle ( e );
//            } catch ( IllegalAccessException e ) {
//                Exceptions.handle ( e );
//            }
//            callSiteMethod = new ConstantCallSite(methodHandle);
//            this.getter = callSiteMethod.dynamicInvoker();
//
//        }  else {
//            this.getter = null;
//        }
//
//
//        if ( parentType != null && setter != null ) {
//
//            methodType
//                    = MethodType.methodType ( void.class, this.getType() );
//
//
//            try {
//                methodHandle = lookup.findVirtual ( this.parentType, setter.getName(), methodType );
//            } catch ( NoSuchMethodException e ) {
//                Exceptions.handle ( e );
//            } catch ( IllegalAccessException e ) {
//                Exceptions.handle ( e );
//            }
//
//            callSiteMethod = new ConstantCallSite(methodHandle);
//            this.setter = callSiteMethod.dynamicInvoker ();
//        } else {
//            this.setter = null;
//        }
    }

    @Override
    public Object getObject( Object obj ) {
        try {
            return getter.invoke ( obj );
        } catch ( Throwable e ) {
            return Exceptions.handle( Object.class, sputs( "unable to call getObject for property ", this.name,
                    "for class ", this.type ), e );
        }
    }




    @Override
    public final void setObject( Object obj, Object value ) {
        try {
            setter.invoke ( obj, value );
        } catch ( Throwable e ) {
            Exceptions.handle( String.format( "You tried to modify property %s of %s for instance %s " +
                    "with set %s using %s, and this property read only status is %s",
                    name, obj.getClass().getSimpleName(), obj, value, getName (), isReadOnly () ), e );

        }

    }
    public final boolean getBoolean( Object obj ) {
        try {
            return ( Boolean ) this.getObject ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle( boolean.class, sputs( "unable to call getObject for property", this.name ), e );
        }

    }

    @Override
    public final int getInt( Object obj ) {
        try {
            return ( Integer ) this.getObject ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle( int.class, sputs( "unable to call getObject for property", this.name ), e );
        }
    }

    @Override
    public final short getShort( Object obj ) {
        try {
            return ( Short ) this.getObject ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle( short.class, sputs( "unable to call getObject for property", this.name ), e );
        }
    }

    @Override
    public final char getChar( Object obj ) {
        try {
            return ( Character ) this.getObject ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle( char.class, sputs( "unable to call getObject for property", this.name ), e );
        }
    }

    @Override
    public final long getLong( Object obj ) {
        try {
            return ( Long ) this.getObject ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public final double getDouble( Object obj ) {
        try {
            return ( Double ) this.getObject ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final float getFloat( Object obj ) {
        try {
            return ( Float ) this.getObject ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public final byte getByte( Object obj ) {
        try {
            return ( Byte ) this.getObject ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }


    @Override
    public final Field getField() {
        return null;
    }


    @Override
    public final void setBoolean( Object obj, boolean value ) {
        try {
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final void setInt( Object obj, int value ) {
        try {
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final void setShort( Object obj, short value ) {
        try {
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final void setChar( Object obj, char value ) {
        try {
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final void setLong( Object obj, long value ) {
        try {
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final void setDouble( Object obj, double value ) {
        try {
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final void setFloat( Object obj, float value ) {
        try {
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

    }

    @Override
    public final void setByte( Object obj, byte value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );

        }

    }

}
