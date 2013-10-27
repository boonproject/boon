package org.boon.datarepo.impl.maps;

import org.boon.datarepo.spi.TypedMap;

import java.util.concurrent.ConcurrentHashMap;

import static org.boon.core.reflection.Conversions.wrapAsObject;

public class JavaUtilMap<K, V> extends ConcurrentHashMap<K, V> implements TypedMap<K, V> {

    public JavaUtilMap() {
        super();
    }

    @Override
    public final boolean put(K key, boolean i) {
        return (Boolean) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final boolean getBoolean(K key) {
        return (Boolean) super.get(key);
    }

    @Override
    public V put(byte key, V v) {
        return super.put((K) wrapAsObject(key), v);
    }

    @Override
    public final byte put(K key, byte i) {
        return (Byte) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final byte getByte(K key) {
        return (Byte) super.get(key);
    }

    @Override
    public V put(short key, V v) {
        return super.put((K) wrapAsObject(key), v);
    }

    @Override
    public final short put(K key, short i) {
        return (Short) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final short getShort(K key) {
        return (Short) super.get(key);
    }

    @Override
    public V put(int key, V v) {
        return super.put((K) wrapAsObject(key), v);
    }

    @Override
    public final int put(K key, int i) {
        return (Integer) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final int getInt(K key) {
        return (Integer) super.get(key);
    }

    @Override
    public V put(long key, V v) {
        return super.put((K) wrapAsObject(key), v);
    }

    @Override
    public final long put(K key, long i) {
        return (Long) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final long getLong(K key) {
        return (Long) super.get(key);
    }

    @Override
    public V put(float key, V v) {
        return super.put((K) wrapAsObject(key), v);
    }

    @Override
    public final float put(K key, float i) {
        return (Float) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final float getFloat(K key) {
        return (Float) super.get(key);
    }

    @Override
    public V put(double key, V v) {
        return super.put((K) wrapAsObject(key), v);
    }

    @Override
    public final double put(K key, double i) {
        return (Double) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final double getDouble(K key) {
        return (Double) super.get(key);
    }

    @Override
    public final V put(char key, V v) {
        return super.put((K) wrapAsObject(key), v);
    }

    @Override
    public final char put(K key, char i) {
        return (Character) super.put(key, (V) wrapAsObject(i));
    }

    @Override
    public final char getChar(K key) {
        return (Character) super.get(key);
    }
}
