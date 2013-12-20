package org.boon.primitive;


public interface Input {


    void readFully ( byte[] b );

    void readFully ( byte[] b, int off, int len );

    int skipBytes ( int n );

    void location ( int n );

<<<<<<< HEAD
    int location ();

    void reset ();

    boolean readBoolean ();

    byte readByte ();

    short readUnsignedByte ();

    short readShort ();

    int readUnsignedShort ();

    char readChar ();

    int readInt ();

    long readUnsignedInt ();

    long readLong ();

    float readFloat ();

    double readDouble ();

    String readSmallString ();

    String readLargeString ();

    String readMediumString ();


    byte[] readSmallByteArray ();

    byte[] readLargeByteArray ();

    byte[] readMediumByteArray ();

    short[] readSmallShortArray ();

    short[] readLargeShortArray ();

    short[] readMediumShortArray ();


    int[] readSmallIntArray ();

    int[] readLargeIntArray ();

    int[] readMediumIntArray ();
=======
    int location();

    void reset();

    boolean readBoolean();

    byte readByte();

    short readUnsignedByte();

    short readShort();

    int readUnsignedShort();

    char readChar();

    int readInt();

    long readUnsignedInt();

    long readLong();

    float readFloat();

    double readDouble();

    String readSmallString();

    String readLargeString();

    String readMediumString();


    byte[] readSmallByteArray();

    byte[] readLargeByteArray();

    byte[] readMediumByteArray();

    short[] readSmallShortArray();

    short[] readLargeShortArray();

    short[] readMediumShortArray();


    int[] readSmallIntArray();

    int[] readLargeIntArray();

    int[] readMediumIntArray();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    byte[] readBytes ( int size );


<<<<<<< HEAD
    long[] readSmallLongArray ();

    long[] readLargeLongArray ();

    long[] readMediumLongArray ();


    float[] readSmallFloatArray ();

    float[] readLargeFloatArray ();

    float[] readMediumFloatArray ();


    double[] readSmallDoubleArray ();

    double[] readLargeDoubleArray ();

    double[] readMediumDoubleArray ();
=======
    long[] readSmallLongArray();

    long[] readLargeLongArray();

    long[] readMediumLongArray();


    float[] readSmallFloatArray();

    float[] readLargeFloatArray();

    float[] readMediumFloatArray();


    double[] readSmallDoubleArray();

    double[] readLargeDoubleArray();

    double[] readMediumDoubleArray();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    //TODO add unsigned int, unsigned byte, unsigned short array


}
