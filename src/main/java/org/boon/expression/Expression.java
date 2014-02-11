package org.boon.expression;

import org.boon.criteria.ObjectFilter;
import org.boon.criteria.internal.Grouping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 2/10/14.
 */
public class Expression {


    private Grouping grouping = Grouping.AND;


    private ObjectContext context = null;


    private List<ExpressionBinding> expressions = new ArrayList<>(  );

    public Grouping grouping() {
        return grouping;
    }

    public Expression grouping( Grouping grouping ) {
        this.grouping = grouping;
        return this;
    }


    public Expression equals( String pathToRoot, String property, Object value) {

        ExpressionBinding binding =
                new ExpressionBinding( ObjectFilter.eq( property, value ),
                new ExperssionSupplier( Object.class, pathToRoot, context ) );

        expressions.add( binding );
        return this;
    }






}
