package org.boon.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface Value {


<<<<<<< HEAD
    byte byteValue ();

    short shortValue ();
=======
    byte byteValue();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    int intValue ();

    long longValue ();

    BigDecimal bigDecimalValue ();

    BigInteger bigIntegerValue ();

    float floatValue ();

    double doubleValue ();

    boolean booleanValue ();

    Date dateValue ();

    String stringValue ();

    String stringValueEncoded ();

    Object toValue ();

<<<<<<< HEAD
    Enum toEnum ( Class<? extends Enum> cls );

    boolean isContainer (); //either a map or a collection
=======
    Object toValue();

    Enum toEnum( Class<? extends Enum> cls );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    public void chop ();

}
