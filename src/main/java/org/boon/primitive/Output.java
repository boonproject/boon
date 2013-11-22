package org.boon.primitive;


public interface Output {

    void write( int b );

    void write( byte[] b );

    void write( byte[] b, int off, int len );

    void writeBoolean( boolean v );

    void writeByte( byte v );

    void writeUnsignedByte( short v );

    void writeShort( short v );

    void writeUnsignedShort( int v );

    void writeChar( char v );

    void writeInt( int v );

    void writeUnsignedInt( long v );

    void writeLong( long v );

    void writeFloat( float v );

    void writeDouble( double v );


    void writeLargeString( String s );

    void writeSmallString( String s );

    void writeMediumString( String s );

    void writeLargeByteArray( byte[] bytes );

    void writeSmallByteArray( byte[] bytes );

    void writeMediumByteArray( byte[] bytes );


    void writeLargeShortArray( short[] values );

    void writeSmallShortArray( short[] values );

    void writeMediumShortArray( short[] values );


    void writeLargeIntArray( int[] values );

    void writeSmallIntArray( int[] values );

    void writeMediumIntArray( int[] values );


    void writeLargeLongArray( long[] values );

    void writeSmallLongArray( long[] values );

    void writeMediumLongArray( long[] values );

    void writeLargeFloatArray( float[] values );

    void writeSmallFloatArray( float[] values );

    void writeMediumFloatArray( float[] values );


    void writeLargeDoubleArray( double[] values );

    void writeSmallDoubleArray( double[] values );

    void writeMediumDoubleArray( double[] values );

    //TODO add unsigned int, unsigned byte, unsigned short array


}
