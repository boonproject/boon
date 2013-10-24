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
