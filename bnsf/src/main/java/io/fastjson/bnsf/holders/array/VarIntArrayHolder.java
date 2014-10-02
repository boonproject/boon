package io.fastjson.bnsf.holders.array;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

import java.math.BigInteger;

/**
 * Created by Richard on 9/25/14.
 */
public class VarIntArrayHolder implements WireValueHolder {

    private BigInteger[] value;

    @Override
    public WireValueType type() {
        return WireValueType.VAR_INT_ARRAY;
    }

    public BigInteger[] getValue() {
        return value;
    }

    public void setValue(BigInteger[] value) {
        this.value = value;
    }
}
