package io.fastjson.bnsf.holders.basic;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

public class StringHolder implements WireValueHolder, CharSequence  {

    private String value;

    @Override
    public WireValueType type() {
        return WireValueType.STRING;
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(CharSequence value) {
        this.value = value.toString();
    }
}
