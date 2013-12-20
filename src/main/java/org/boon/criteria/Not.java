package org.boon.criteria;

import org.boon.core.reflection.fields.FieldAccess;

import java.util.Map;

public class Not extends Criteria {

    private final Criteria expression;

    public Not ( Criteria expression ) {
        this.expression = expression;
    }

    @Override
    public void prepareForGroupTest ( Map<String, FieldAccess> fields, Object owner ) {

    }

    @Override
<<<<<<< HEAD
    public void cleanAfterGroupTest () {
=======
    public void cleanAfterGroupTest() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    }

    @Override
    public boolean resolve ( Map<String, FieldAccess> fields, Object owner ) {
        return !this.expression.resolve ( fields, owner );
    }
}
