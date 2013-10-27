package org.boon.core.reflection.fields;

import org.boon.core.Typ;
import org.boon.core.reflection.Conversions;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.boon.Exceptions.die;
import static org.boon.core.reflection.Conversions.*;


public abstract class UnsafeField implements FieldAccess {


    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    static final Unsafe unsafe = getUnsafe();
    protected final Field field;
    protected long offset;
    protected final boolean isFinal;
    protected final Object base;
    protected final boolean isStatic;
    protected final boolean isVolatile;
    protected final boolean qualified;
    protected final boolean readOnly;
    protected final Class<?> type;
    protected final String name;


    public static UnsafeField createUnsafeField(Field field) {
        Class<?> type = field.getType();
        boolean isVolatile = Modifier.isVolatile(field.getModifiers());
        if (!isVolatile) {
            if (type == Typ.intgr) {
                return new IntUnsafeField(field);
            } else if (type == Typ.lng) {
                return new LongUnsafeField(field);
            } else if (type == Typ.bt) {
                return new ByteUnsafeField(field);
            } else if (type == Typ.shrt) {
                return new ShortUnsafeField(field);
            } else if (type == Typ.chr) {
                return new CharUnsafeField(field);
            } else if (type == Typ.dbl) {
                return new DoubleUnsafeField(field);
            } else if (type == Typ.flt) {
                return new FloatUnsafeField(field);
            } else {
                return new ObjectUnsafeField(field);
            }
        } else {
            if (type == Typ.intgr) {
                return new VolatileIntUnsafeField(field);
            } else if (type == Typ.lng) {
                return new VolatileLongUnsafeField(field);
            } else if (type == Typ.bt) {
                return new VolatileByteUnsafeField(field);
            } else if (type == Typ.shrt) {
                return new VolatileShortUnsafeField(field);
            } else if (type == Typ.chr) {
                return new VolatileCharUnsafeField(field);
            } else if (type == Typ.dbl) {
                return new VolatileDoubleUnsafeField(field);
            } else if (type == Typ.flt) {
                return new VolatileFloatUnsafeField(field);
            } else {
                return new ObjectUnsafeField(field);
            }

        }
    }


    protected UnsafeField(Field f) {
        name = f.getName();
        field = f;

        isFinal = Modifier.isFinal(field.getModifiers());
        isStatic = Modifier.isStatic(field.getModifiers());

        if (isStatic) {
            base = unsafe.staticFieldBase(field);
            offset = unsafe.staticFieldOffset(field);
        } else {
            offset = unsafe.objectFieldOffset(field);
            base = null;
        }
        isVolatile = Modifier.isVolatile(field.getModifiers());
        qualified = isFinal || isVolatile;
        readOnly = isFinal || isStatic;
        type = f.getType();
    }


    @Override
    public Object getValue(Object obj) {
        if (type == Typ.intgr) {
            int i = this.getInt(obj);
            return Integer.valueOf(i);
        } else if (type == Typ.lng) {
            long l = this.getLong(obj);
            return Long.valueOf(l);
        } else if (type == Typ.bt) {
            byte b = this.getByte(obj);
            return Byte.valueOf(b);
        } else if (type == Typ.shrt) {
            short s = this.getShort(obj);
            return Short.valueOf(s);
        } else if (type == Typ.chr) {
            char c = this.getChar(obj);
            return Character.valueOf(c);
        } else if (type == Typ.dbl) {
            double d = this.getDouble(obj);
            return Double.valueOf(d);
        } else if (type == Typ.flt) {
            float f = this.getFloat(obj);
            return Float.valueOf(f);
        } else {
            return this.getObject(obj);
        }
    }


    @Override
    public void setValue(Object obj, Object value) {
        if (obj.getClass() == this.type) {
            this.setObject(obj, value);
            return;
        }


        if (type == Typ.intgr) {
            setInt(obj, toInt(value));
        } else if (type == Typ.lng) {
            setLong(obj, toLong(value));
        } else if (type == Typ.bt) {
            setByte(obj, toByte(value));

        } else if (type == Typ.shrt) {
            setShort(obj, toShort(value));

        } else if (type == Typ.chr) {
            setChar(obj, toChar(value));

        } else if (type == Typ.dbl) {
            setDouble(obj, toDouble(value));

        } else if (type == Typ.flt) {
            setFloat(obj, toFloat(value));

        } else {
            setObject(obj, Conversions.coerce(type, value));
        }

    }


    @Override
    public int getInt(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }

    @Override
    public boolean getBoolean(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return false;
    }


    @Override
    public short getShort(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }


    @Override
    public char getChar(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }


    @Override
    public long getLong(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }


    @Override
    public double getDouble(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }


    @Override
    public float getFloat(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }


    @Override
    public byte getByte(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }


    @Override
    public Object getObject(Object obj) {
        die(String.format("Can't call this method on this type %s", this.type));
        return 0;
    }


    public boolean getStaticBoolean() {
        return getBoolean(base);
    }


    public int getStaticInt() {
        return getInt(base);
    }


    public short getStaticShort() {
        return getShort(base);
    }


    public long getStaticLong() {
        return getLong(base);
    }

    public double getStaticDouble() {
        return getDouble(base);
    }


    public float getStaticFloat() {
        return getFloat(base);
    }


    public byte getStaticByte() {
        return getByte(base);
    }


    public Object getObject() {
        return getObject(base);
    }


    @Override
    public Field getField() {
        return field;
    }


    @Override
    public boolean isFinal() {
        return isFinal;
    }

    public Object getBase() {
        return base;
    }


    @Override
    public boolean isStatic() {
        return isStatic;
    }


    @Override
    public boolean isVolatile() {
        return isVolatile;
    }


    @Override
    public boolean isQualified() {
        return qualified;
    }


    @Override
    public boolean isReadOnly() {
        return readOnly;
    }


    @Override
    public Class<?> getType() {
        return type;
    }


    @Override
    public String getName() {
        return name;
    }


    @Override
    public void setBoolean(Object obj, boolean value) {

        die(String.format("Can't call this method on this type %s", this.type));

    }


    @Override
    public void setInt(Object obj, int value) {
        die(String.format("Can't call this method on this type %s", this.type));

    }


    @Override
    public void setShort(Object obj, short value) {
        die(String.format("Can't call this method on this type %s", this.type));


    }


    @Override
    public void setChar(Object obj, char value) {
        die(String.format("Can't call this method on this type %s", this.type));

    }


    @Override
    public void setLong(Object obj, long value) {
        die(String.format("Can't call this method on this type %s", this.type));

    }


    @Override
    public void setDouble(Object obj, double value) {
        die(String.format("Can't call this method on this type %s", this.type));

    }


    @Override
    public void setFloat(Object obj, float value) {
        die(String.format("Can't call this method on this type %s", this.type));
    }


    @Override
    public void setByte(Object obj, byte value) {
        die(String.format("Can't call this method on this type %s", this.type));
    }


    @Override
    public void setObject(Object obj, Object value) {
        die(String.format("Can't call this method on this type %s", this.type));

    }


    @Override
    public String toString() {
        return "UnsafeField [field=" + field + ", offset=" + offset
                + ", isFinal=" + isFinal + ", base=" + base + ", isStatic="
                + isStatic + ", isVolatile=" + isVolatile + ", qualified="
                + qualified + ", readOnly=" + readOnly + ", type=" + type
                + ", name=" + name + "]";
    }


    private static final class IntUnsafeField extends UnsafeField {

        protected IntUnsafeField(Field f) {
            super(f);
        }

        @Override
        public final void setInt(Object obj, int value) {
            unsafe.putInt(obj, offset, value);
        }

        @Override
        public final int getInt(Object obj) {
            return unsafe.getInt(obj, offset);
        }
    }

    private static class LongUnsafeField extends UnsafeField {

        protected LongUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setLong(Object obj, long value) {
            unsafe.putLong(obj, offset, value);
        }

        @Override
        public long getLong(Object obj) {
            return unsafe.getLong(obj, offset);
        }
    }

    private static class CharUnsafeField extends UnsafeField {

        protected CharUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setChar(Object obj, char value) {
            unsafe.putChar(obj, offset, value);
        }

        @Override
        public char getChar(Object obj) {
            return unsafe.getChar(obj, offset);
        }
    }

    private static class ByteUnsafeField extends UnsafeField {

        protected ByteUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setByte(Object obj, byte value) {
            unsafe.putByte(obj, offset, value);
        }

        @Override
        public byte getByte(Object obj) {
            return unsafe.getByte(obj, offset);
        }
    }

    private static class ShortUnsafeField extends UnsafeField {

        protected ShortUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setShort(Object obj, short value) {
            unsafe.putShort(obj, offset, value);
        }

        @Override
        public short getShort(Object obj) {
            return unsafe.getShort(obj, offset);
        }
    }

    private static class ObjectUnsafeField extends UnsafeField {

        protected ObjectUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setObject(Object obj, Object value) {
            unsafe.putObject(obj, offset, value);
        }

        @Override
        public Object getObject(Object obj) {
            return unsafe.getObject(obj, offset);
        }
    }

    private static class FloatUnsafeField extends UnsafeField {

        protected FloatUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setFloat(Object obj, float value) {
            unsafe.putFloat(obj, offset, value);
        }

        @Override
        public float getFloat(Object obj) {
            return unsafe.getFloat(obj, offset);
        }
    }

    private static class DoubleUnsafeField extends UnsafeField {

        protected DoubleUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setDouble(Object obj, double value) {
            unsafe.putDouble(obj, offset, value);
        }

        @Override
        public double getDouble(Object obj) {
            return unsafe.getDouble(obj, offset);
        }
    }


    private static class VolatileIntUnsafeField extends UnsafeField {

        protected VolatileIntUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setInt(Object obj, int value) {
            unsafe.putIntVolatile(obj, offset, value);
        }

        @Override
        public int getInt(Object obj) {
            return unsafe.getIntVolatile(obj, offset);
        }
    }

    private static class VolatileLongUnsafeField extends UnsafeField {

        protected VolatileLongUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setLong(Object obj, long value) {
            unsafe.putLongVolatile(obj, offset, value);
        }

        @Override
        public long getLong(Object obj) {
            return unsafe.getLongVolatile(obj, offset);
        }
    }

    private static class VolatileCharUnsafeField extends UnsafeField {

        protected VolatileCharUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setChar(Object obj, char value) {
            unsafe.putCharVolatile(obj, offset, value);
        }

        @Override
        public char getChar(Object obj) {
            return unsafe.getCharVolatile(obj, offset);
        }
    }

    private static class VolatileByteUnsafeField extends UnsafeField {

        protected VolatileByteUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setByte(Object obj, byte value) {
            unsafe.putByteVolatile(obj, offset, value);
        }

        @Override
        public byte getByte(Object obj) {
            return unsafe.getByteVolatile(obj, offset);
        }
    }

    private static class VolatileShortUnsafeField extends UnsafeField {

        protected VolatileShortUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setShort(Object obj, short value) {
            unsafe.putShortVolatile(obj, offset, value);
        }

        @Override
        public short getShort(Object obj) {
            return unsafe.getShortVolatile(obj, offset);
        }
    }

    private static class VolatileObjectUnsafeField extends UnsafeField {

        protected VolatileObjectUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setObject(Object obj, Object value) {
            unsafe.putObjectVolatile(obj, offset, value);
        }

        @Override
        public Object getObject(Object obj) {
            return unsafe.getObjectVolatile(obj, offset);
        }
    }

    private static class VolatileFloatUnsafeField extends UnsafeField {

        protected VolatileFloatUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setFloat(Object obj, float value) {
            unsafe.putFloatVolatile(obj, offset, value);
        }

        @Override
        public float getFloat(Object obj) {
            return unsafe.getFloatVolatile(obj, offset);
        }
    }

    private static class VolatileDoubleUnsafeField extends UnsafeField {

        protected VolatileDoubleUnsafeField(Field f) {
            super(f);
        }

        @Override
        public void setDouble(Object obj, double value) {
            unsafe.putDoubleVolatile(obj, offset, value);
        }

        @Override
        public double getDouble(Object obj) {
            return unsafe.getDoubleVolatile(obj, offset);
        }
    }


}
