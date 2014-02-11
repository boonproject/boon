package org.boon.expression;

import org.boon.Exceptions;
import org.boon.core.Supplier;
import org.boon.criteria.internal.Criteria;

/**
 * Created by Richard on 2/10/14.
 */
public class ExpressionBinding<T> {

    Criteria criteria;
    Supplier<T> supplier;


    public ExpressionBinding( Criteria criteria, Supplier<T> supplier ) {
        Exceptions.requireNonNulls("ExpressionBinding construction", criteria, supplier);
        this.criteria = criteria;
        this.supplier = supplier;
    }

    public boolean test( ) {
        return criteria.test(supplier.get());
    }
}
