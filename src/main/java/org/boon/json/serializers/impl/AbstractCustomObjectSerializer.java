package org.boon.json.serializers.impl;

import org.boon.json.serializers.CustomObjectSerializer;

public abstract class AbstractCustomObjectSerializer<T> implements CustomObjectSerializer<T> {

    protected Class <T> clazz;
    public AbstractCustomObjectSerializer (Class <T> clazz) {
          this.clazz = clazz;
    }

    @Override
    public Class<T> type () {
        return clazz;
    }


}
