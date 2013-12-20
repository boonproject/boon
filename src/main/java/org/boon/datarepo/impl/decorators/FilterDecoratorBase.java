package org.boon.datarepo.impl.decorators;

import org.boon.criteria.Criteria;
import org.boon.datarepo.Filter;
import org.boon.datarepo.ResultSet;

/**
 * Checking
 */
public class FilterDecoratorBase implements Filter {

    Filter delegate;

    FilterDecoratorBase ( Filter delegate ) {
        this.delegate = delegate;
    }

    @Override
    public ResultSet filter ( Criteria... expressions ) {
        return delegate.filter ( expressions );
    }

    @Override
<<<<<<< HEAD
    public void invalidate () {
=======
    public void invalidate() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        delegate.invalidate ();
    }
}
