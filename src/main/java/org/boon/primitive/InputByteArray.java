package org.boon.primitive;

import java.nio.charset.StandardCharsets;

public class InputByteArray implements Input {

    private final byte[] array;

    private int location;

    public InputByteArray ( byte[] array ) {

        this.array = array;

    }

    @Override
    public void readFully ( byte[] readToThis ) {
        Byt._idx ( readToThis, 0, array, location, readToThis.length );
        location += readToThis.length;
    }

    @Override
    public void readFully ( byte[] readToThis, int off, int len ) {
        Byt._idx ( readToThis, off, array, location, len );
        location += readToThis.length;
    }

    @Override
    public int skipBytes ( int n ) {
        return location += n;
    }

    @Override
    public void location ( int n ) {
        location = n;
    }

    @Override
    public int location () {
        return location;
    }

    @Override
    public void reset () {
        location = 0;
    }

    @Override
    public boolean readBoolean () {
        final byte val = Byt.idx ( array, location );

        location += 1;

        if ( val == 0 ) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public byte readByte () {

        byte value = Byt.idx ( array, location );
        location += 1;
        return value;

    }

    @Override
    public short readUnsignedByte () {

        short value = Byt.idxUnsignedByte ( array, location );
        location += 1;
        return value;

    }

    @Override
    public short readShort () {

        short value =  Byt.idxShort ( array, location );
        location += 2;

        return value;
    }

    @Override
    public int readUnsignedShort () {

        int value =  Byt.idxUnsignedShort ( array, location );
        location+=2;

        return value;
    }

    @Override
    public char readChar () {

        char value = Byt.idxChar ( array, location );
        location += 2;

        return value;
    }

    @Override
    public int readInt () {

        int value = Byt.idxInt ( array, location );
        location += 4;

        return value;
    }

    @Override
    public long readUnsignedInt () {

        long value = Byt.idxUnsignedInt ( array, location );
        location += 4;

        return value;
    }

    @Override
    public long readLong () {

        long value = Byt.idxLong ( array, location );
        location += 8;

        return value;
    }

    @Override
    public float readFloat () {

        float value = Byt.idxFloat ( array, location );
        location += 4;
        return value;

    }

    @Override
    public double readDouble () {

        double value = Byt.idxDouble ( array, location );
        location += 8;
        return value;

    }


    @Override
    public String readSmallString () {
        short size = this.readUnsignedByte ();

        byte[] bytes = this.readBytes ( size );
        return new String ( bytes, StandardCharsets.UTF_8 );
    }


    @Override
    public String readMediumString () {
        int size = this.readUnsignedShort ();

        byte[] bytes = this.readBytes ( size );

        return new String ( bytes, StandardCharsets.UTF_8 );
    }


    @Override
    public String readLargeString () {
        int size = this.readInt ();


        byte[] bytes = this.readBytes ( size );

        return new String ( bytes, StandardCharsets.UTF_8 );
    }



    @Override
    public byte[] readSmallByteArray () {
        short size = this.readUnsignedByte ();

        byte[] bytes = this.readBytes ( size );
        return bytes;
    }


    @Override
    public byte[] readMediumByteArray () {
        int size = this.readUnsignedShort ();

        byte[] bytes = this.readBytes ( size );

        return bytes;
    }

    @Override
    public short[] readSmallShortArray () {

        short size = this.readUnsignedByte ();

        return doReadShortArray ( size );


    }

    private short [] doReadShortArray ( int size ) {
        short [] values = new short[size];

        for ( int index=0; index < values.length; index++ ) {
            values[index] = this.readShort ();
        }
        return values;
    }

    @Override
    public short[] readLargeShortArray () {

        int size = this.readInt ();

        return doReadShortArray ( size );

    }

    @Override
    public short[] readMediumShortArray () {

        int size = this.readUnsignedShort ();

        return doReadShortArray ( size );

    }


    @Override
    public byte[] readLargeByteArray () {
        int size = this.readInt ();


        byte[] bytes = this.readBytes ( size );

        return bytes;
    }

    @Override
    public byte[] readBytes ( int size ) {
        byte[] bytes = new byte[ size ];
        this.readFully ( bytes );
        return bytes;
    }





    @Override
    public int[] readSmallIntArray () {

        short size = this.readUnsignedByte ();

        return doReadIntArray ( size );


    }

    private int [] doReadIntArray ( int size ) {
        int [] values = new int[size];

        for ( int index=0; index < values.length; index++ ) {
            values[index] = this.readInt ();
        }
        return values;
    }

    @Override
    public int[] readLargeIntArray () {

        int size = this.readInt ();

        return doReadIntArray ( size );

    }

    @Override
    public int[] readMediumIntArray () {

        int size = this.readUnsignedShort ();

        return doReadIntArray ( size );

    }

}
