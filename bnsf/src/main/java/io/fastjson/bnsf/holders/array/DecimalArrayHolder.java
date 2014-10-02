package io.fastjson.bnsf.holders.array;

import io.fastjson.bnsf.WireValueType;
import io.fastjson.bnsf.holders.WireValueHolder;

import java.math.BigDecimal;

/**
 * Created by Richard on 9/25/14.
 */
public class DecimalArrayHolder implements WireValueHolder {

    private BigDecimal[] value;


    @Override
    public WireValueType type() {
        return WireValueType.DECIMAL_ARRAY;
    }


    public BigDecimal[] getValue() {
        return value;
    }

    public void setValue(BigDecimal[] value) {
        this.value = value;
    }
}
