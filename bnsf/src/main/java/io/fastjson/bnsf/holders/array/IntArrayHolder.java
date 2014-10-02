package io.fastjson.bnsf.holders.array;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

/**
 * Created by Richard on 9/25/14.
 */
public class IntArrayHolder implements WireValueHolder {

    private int [] value;

    @Override
    public WireValueType type() {
        return WireValueType.INT_ARRAY;
    }

    public int[] getValue() {
        return value;
    }

    public void setValue(int[] value) {
        this.value = value;
    }
}
