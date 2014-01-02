package org.boon.core.reflection.fields;

import org.boon.Exceptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

import static org.boon.Boon.sputs;

public class PropertyField extends BaseField {
    private final Method getter;
    private final Method setter;


    public PropertyField( String name, Method setter, Method getter ) {

        super (  name,  getter,  setter);
            this.setter = setter;
            this.getter = getter;

    }

    @Override
    public Object getObject( Object obj ) {
        try {
            return getter.invoke( obj );
        } catch ( Exception e ) {
            return Exceptions.handle( Object.class, sputs( "unable to call getObject for property ", this.name,
                    "for class ", this.type ), e );
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
            this.setObject( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException( e );

        }

    }

    @Override
    public final void setObject( Object obj, Object value ) {
        try {
            setter.invoke( obj, value );
        } catch ( Exception e ) {
            Exceptions.handle( String.format( "You tried to modify property %s of %s for instance %s " +
                    "with set %s using %s, and this property read only status is %s",
                    name, obj.getClass().getSimpleName(), obj, value, setter.getName(), isReadOnly () ), e );

        }

    }

}
