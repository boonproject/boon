package org.boon.datarepo.spi;

public interface TypedMap<K, V> {


    boolean put(K key, boolean i);

    boolean getBoolean(K key);

    V put(byte key, V v);

    byte put(K key, byte i);

    byte getByte(K key);

    V put(short key, V v);

    short put(K key, short i);

    short getShort(K key);

    V put(int key, V v);

    int put(K key, int i);

    int getInt(K key);

    V put(long key, V v);

    long put(K key, long i);

    long getLong(K key);

    V put(float key, V v);

    float put(K key, float i);

    float getFloat(K key);

    V put(double key, V v);

    double put(K key, double i);

    double getDouble(K key);

    V put(char key, V v);

    char put(K key, char i);

    char getChar(K key);


}
