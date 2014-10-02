package io.fastjson.bnsf.holders.array;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

/**
 * Created by Richard on 9/25/14.
 */
public class OctetArrayHolder implements WireValueHolder {


    private byte[] value;

    @Override
    public WireValueType type() {
        return WireValueType.OCTET_ARRAY;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }



}
