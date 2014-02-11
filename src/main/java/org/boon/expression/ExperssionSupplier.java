package org.boon.expression;

import org.boon.core.Supplier;
import org.boon.core.reflection.BeanUtils;

/**
 * Created by Richard on 2/10/14.
 */
public class ExperssionSupplier <T> implements Supplier<T> {

    private final ObjectContext context;
    private final String path;

    public ExperssionSupplier (Class <T> cls,  String path, ObjectContext context) {
         this.path = path;
         this.context = context;


    }

    @Override
    public T get() {
        return (T) BeanUtils.idx( context, path  );
    }
}
