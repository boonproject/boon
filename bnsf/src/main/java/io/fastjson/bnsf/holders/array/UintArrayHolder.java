package io.fastjson.bnsf.holders.array;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

/**
 * Created by Richard on 9/25/14.
 */
public class UintArrayHolder implements WireValueHolder {

    private char [] value;

    @Override
    public WireValueType type() {
        return WireValueType.INT_ARRAY;
    }

    public char[] getValue() {
        return value;
    }

    public void setValue(char[] value) {
        this.value = value;
    }
}
