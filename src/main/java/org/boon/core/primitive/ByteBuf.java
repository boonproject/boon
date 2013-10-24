package org.boon.core.primitive;

public class ByteBuf {

    protected int capacity = 16;


    protected int length = 0;

    protected byte [] buffer;


    public static ByteBuf createExact( final int capacity) {
        return new ByteBuf(capacity) {
            public void add(byte[] chars) {
                Byt._idx( buffer, length, chars );
                length += chars.length;
            }
        };
    }

    public static ByteBuf create( int capacity) {
        return new ByteBuf(capacity);
    }

    protected ByteBuf(int capacity)  {
        this.capacity = capacity;
        init();
    }


    protected ByteBuf()  {
        init();
    }

    private void init() {
        buffer = new byte[capacity];
    }


    public void add(String str) {
            this.add( Byt.bytes(str) );

    }


    public void add(int value) {

        if (4 + length < capacity) {
            Byt.intTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.intTo(buffer, length, value);
        }

        length += 4;


    }


    public void add(float value) {

        if (4 + length < capacity) {
            Byt.floatTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.floatTo(buffer, length, value);
        }

        length += 4;


    }



    public void add(char value) {

        if (2 + length < capacity) {
            Byt.charTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.charTo(buffer, length, value);
        }

        length += 2;


    }



    public void add(short value) {

        if (2 + length < capacity) {
            Byt.shortTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.shortTo(buffer, length, value);
        }

        length += 2;


    }

    public void add(byte value) {

        if (1 + length < capacity) {
            Byt.idx(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.idx(buffer, length, value);
        }

        length += 1;


    }

    public void add(long value) {

        if (8 + length < capacity) {
            Byt.longTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.longTo(buffer, length, value);
        }

        length += 8;

    }

    public void add(double value) {

        if (8 + length < capacity) {
            Byt.doubleTo(buffer, length, value);
        } else {
            buffer = Byt.grow(buffer);
            capacity = buffer.length;

            Byt.doubleTo(buffer, length, value);
        }

        length += 8;

    }

    public void add(byte[] array) {
        if (array.length + length < capacity) {
            Byt._idx( buffer, length, array );
        } else {
            buffer = Byt.grow(buffer);
            Byt._idx( buffer, length, array );
            capacity = buffer.length;
        }
        length += array.length;
    }

    public byte[] readAndReset () {
        byte [] bytes = this.buffer;
        this.buffer = null;
        return bytes;
    }


    public int len() {
        return length;
    }

}
