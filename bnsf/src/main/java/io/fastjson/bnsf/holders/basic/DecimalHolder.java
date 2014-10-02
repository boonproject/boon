package io.fastjson.bnsf.holders.basic;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

import java.math.BigDecimal;

/**
 * Created by Richard on 9/25/14.
 */
public class DecimalHolder extends Number implements WireValueHolder {

    private BigDecimal value;

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public WireValueType type() {
        return WireValueType.DECIMAL;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
