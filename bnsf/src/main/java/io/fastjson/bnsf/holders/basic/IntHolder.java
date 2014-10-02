package io.fastjson.bnsf.holders.basic;


import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

public class IntHolder extends Number implements WireValueHolder {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public WireValueType type() {
        return WireValueType.INT;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

}
