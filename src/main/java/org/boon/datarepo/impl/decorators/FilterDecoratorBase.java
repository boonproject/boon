package org.boon.datarepo.impl.decorators;

import org.boon.datarepo.Filter;
import org.boon.datarepo.ResultSet;
import org.boon.criteria.Criteria;

/**
 * Checking
 */
public class FilterDecoratorBase implements Filter {

    Filter delegate;

    FilterDecoratorBase(Filter delegate) {
        this.delegate = delegate;
    }

    @Override
    public ResultSet filter(Criteria... expressions) {
        return delegate.filter(expressions);
    }

    @Override
    public void invalidate() {
        delegate.invalidate();
    }
}
