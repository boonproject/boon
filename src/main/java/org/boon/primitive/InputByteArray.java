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
<<<<<<< HEAD
    public int location () {
=======
    public int location() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return location;
    }

    @Override
<<<<<<< HEAD
    public void reset () {
=======
    public void reset() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        location = 0;
    }

    @Override
<<<<<<< HEAD
    public boolean readBoolean () {
=======
    public boolean readBoolean() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        final byte val = Byt.idx ( array, location );

        location += 1;

        if ( val == 0 ) {
            return false;
        } else {
            return true;
        }
    }

    @Override
<<<<<<< HEAD
    public byte readByte () {
=======
    public byte readByte() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        byte value = Byt.idx ( array, location );
        location += 1;
        return value;

    }

    @Override
<<<<<<< HEAD
    public short readUnsignedByte () {
=======
    public short readUnsignedByte() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        short value = Byt.idxUnsignedByte ( array, location );
        location += 1;
        return value;

    }

    @Override
<<<<<<< HEAD
    public short readShort () {
=======
    public short readShort() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        short value = Byt.idxShort ( array, location );
        location += 2;

        return value;
    }

    @Override
<<<<<<< HEAD
    public int readUnsignedShort () {
=======
    public int readUnsignedShort() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int value = Byt.idxUnsignedShort ( array, location );
        location += 2;

        return value;
    }

    @Override
<<<<<<< HEAD
    public char readChar () {
=======
    public char readChar() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        char value = Byt.idxChar ( array, location );
        location += 2;

        return value;
    }

    @Override
<<<<<<< HEAD
    public int readInt () {
=======
    public int readInt() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int value = Byt.idxInt ( array, location );
        location += 4;

        return value;
    }

    @Override
<<<<<<< HEAD
    public long readUnsignedInt () {
=======
    public long readUnsignedInt() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        long value = Byt.idxUnsignedInt ( array, location );
        location += 4;

        return value;
    }

    @Override
<<<<<<< HEAD
    public long readLong () {
=======
    public long readLong() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        long value = Byt.idxLong ( array, location );
        location += 8;

        return value;
    }

    @Override
<<<<<<< HEAD
    public float readFloat () {
=======
    public float readFloat() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        float value = Byt.idxFloat ( array, location );
        location += 4;
        return value;

    }

    @Override
<<<<<<< HEAD
    public double readDouble () {
=======
    public double readDouble() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        double value = Byt.idxDouble ( array, location );
        location += 8;
        return value;

    }


    @Override
<<<<<<< HEAD
    public String readSmallString () {
=======
    public String readSmallString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        short size = this.readUnsignedByte ();

        byte[] bytes = this.readBytes ( size );
        return new String ( bytes, StandardCharsets.UTF_8 );
    }


    @Override
<<<<<<< HEAD
    public String readMediumString () {
=======
    public String readMediumString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int size = this.readUnsignedShort ();

        byte[] bytes = this.readBytes ( size );

        return new String ( bytes, StandardCharsets.UTF_8 );
    }


    @Override
<<<<<<< HEAD
    public String readLargeString () {
=======
    public String readLargeString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int size = this.readInt ();


        byte[] bytes = this.readBytes ( size );

        return new String ( bytes, StandardCharsets.UTF_8 );
    }


    @Override
<<<<<<< HEAD
    public byte[] readSmallByteArray () {
=======
    public byte[] readSmallByteArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        short size = this.readUnsignedByte ();

        byte[] bytes = this.readBytes ( size );
        return bytes;
    }


    @Override
<<<<<<< HEAD
    public byte[] readMediumByteArray () {
=======
    public byte[] readMediumByteArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int size = this.readUnsignedShort ();

        byte[] bytes = this.readBytes ( size );

        return bytes;
    }

    @Override
<<<<<<< HEAD
    public short[] readSmallShortArray () {
=======
    public short[] readSmallShortArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        short size = this.readUnsignedByte ();

        return doReadShortArray ( size );


    }

    private short[] doReadShortArray ( int size ) {
        short[] values = new short[ size ];

        for ( int index = 0; index < values.length; index++ ) {
<<<<<<< HEAD
            values[ index ] = this.readShort ();
=======
            values[index] = this.readShort ();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public short[] readLargeShortArray () {
=======
    public short[] readLargeShortArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int size = this.readInt ();

        return doReadShortArray ( size );

    }

    @Override
<<<<<<< HEAD
    public short[] readMediumShortArray () {
=======
    public short[] readMediumShortArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int size = this.readUnsignedShort ();

        return doReadShortArray ( size );

    }


    @Override
<<<<<<< HEAD
    public byte[] readLargeByteArray () {
=======
    public byte[] readLargeByteArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
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
<<<<<<< HEAD
    public int[] readSmallIntArray () {
=======
    public int[] readSmallIntArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        short size = this.readUnsignedByte ();

        return doReadIntArray ( size );


    }

    private int[] doReadIntArray ( int size ) {
        int[] values = new int[ size ];

        for ( int index = 0; index < values.length; index++ ) {
<<<<<<< HEAD
            values[ index ] = this.readInt ();
=======
            values[index] = this.readInt ();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public int[] readLargeIntArray () {
=======
    public int[] readLargeIntArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int size = this.readInt ();

        return doReadIntArray ( size );

    }

    @Override
<<<<<<< HEAD
    public int[] readMediumIntArray () {
=======
    public int[] readMediumIntArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int size = this.readUnsignedShort ();

        return doReadIntArray ( size );

    }


    ///


    @Override
<<<<<<< HEAD
    public long[] readSmallLongArray () {
=======
    public long[] readSmallLongArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        short size = this.readUnsignedByte ();

        return doReadLongArray ( size );


    }

    private long[] doReadLongArray ( int size ) {
        long[] values = new long[ size ];

        for ( int index = 0; index < values.length; index++ ) {
<<<<<<< HEAD
            values[ index ] = this.readLong ();
=======
            values[index] = this.readLong ();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        return values;
    }

    @Override
<<<<<<< HEAD
    public long[] readLargeLongArray () {
=======
    public long[] readLargeLongArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int size = this.readInt ();

        return doReadLongArray ( size );

    }

    @Override
<<<<<<< HEAD
    public long[] readMediumLongArray () {
=======
    public long[] readMediumLongArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        int size = this.readUnsignedShort ();

        return doReadLongArray ( size );

    }

    @Override
<<<<<<< HEAD
    public float[] readSmallFloatArray () {
=======
    public float[] readSmallFloatArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        short size = this.readUnsignedByte ();
        return doReadFloatArray ( size );
    }

    @Override
<<<<<<< HEAD
    public float[] readLargeFloatArray () {
=======
    public float[] readLargeFloatArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int size = this.readInt ();
        return doReadFloatArray ( size );
    }

    @Override
<<<<<<< HEAD
    public float[] readMediumFloatArray () {
=======
    public float[] readMediumFloatArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int size = this.readUnsignedShort ();
        return doReadFloatArray ( size );
    }

    private float[] doReadFloatArray ( int size ) {
        float[] values = new float[ size ];
        for ( int index = 0; index < values.length; index++ ) {
<<<<<<< HEAD
            values[ index ] = this.readFloat ();
=======
            values[index] = this.readFloat ();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        return values;
    }


    @Override
<<<<<<< HEAD
    public double[] readSmallDoubleArray () {
=======
    public double[] readSmallDoubleArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        short size = this.readUnsignedByte ();
        return doReadDoubleArray ( size );
    }

    @Override
<<<<<<< HEAD
    public double[] readLargeDoubleArray () {
=======
    public double[] readLargeDoubleArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int size = this.readInt ();
        return doReadDoubleArray ( size );
    }

    @Override
<<<<<<< HEAD
    public double[] readMediumDoubleArray () {
=======
    public double[] readMediumDoubleArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int size = this.readUnsignedShort ();
        return doReadDoubleArray ( size );
    }


    private double[] doReadDoubleArray ( int size ) {
        double[] values = new double[ size ];
        for ( int index = 0; index < values.length; index++ ) {
<<<<<<< HEAD
            values[ index ] = this.readDouble ();
=======
            values[index] = this.readDouble ();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        return values;
    }

    //TODO add unsigned int, unsigned byte, unsigned short

}
