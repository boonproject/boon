package org.boon.expression;

public interface ObjectContext {

    char idxChar(String property);

    byte idxByte(String property);

    short idxShort(String property);

    String idxString(String property);

    int idxInt(String property);

    float idxFloat(String property);

    double idxDouble(String property);

    long idxLong(String property);

    Object idx(String property);

    <T> T idx(Class<T> type, String property);

}
