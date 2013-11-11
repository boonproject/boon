package org.boon.primitive;


public interface Input {


    void readFully ( byte[] b );

    void readFully ( byte[] b, int off, int len );

    int skipBytes ( int n );

    void location ( int n );

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

    byte[] readBytes ( int size );



    long[] readSmallLongArray ();
    long[] readLargeLongArray ();
    long[] readMediumLongArray ();


}
