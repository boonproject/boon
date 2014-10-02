package io.fastjson.bnsf.holders.basic;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

/**
 * Created by Richard on 9/25/14.
 */
public class UintHolder  extends Number implements WireValueHolder {
    private char value;

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    @Override
    public WireValueType type() {
        return WireValueType.UINT;
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
