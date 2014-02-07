package org.boon.criteria.internal;

import org.boon.core.reflection.fields.FieldAccess;

import java.util.Map;

public class Not extends Criteria {

    private final Criteria expression;

    public Not( Criteria expression ) {
        this.expression = expression;
    }

    @Override
    public void prepareForGroupTest( Map<String, FieldAccess> fields, Object owner ) {

    }

    @Override
    public void cleanAfterGroupTest() {

    }

    @Override
    public boolean resolve( Map<String, FieldAccess> fields, Object owner ) {
        return !this.expression.resolve( fields, owner );
    }
}
