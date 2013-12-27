package org.boon.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface Value {


    byte byteValue();

    short shortValue();

    int intValue();

    long longValue();

    BigDecimal bigDecimalValue();

    BigInteger bigIntegerValue();

    float floatValue();

    double doubleValue();

    boolean booleanValue();

    Date dateValue();

    String stringValue();

    String stringValueEncoded();

    Object toValue();

    <T extends Enum> T toEnum( Class<T> cls );

    boolean isContainer(); //either a map or a collection

    public void chop();

}
