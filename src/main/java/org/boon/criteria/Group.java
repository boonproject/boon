package org.boon.criteria;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public abstract class Group extends Criteria {

    protected Criteria[] expressions;

    private final int hashCode;
    private String toString;

    private Grouping grouping = Grouping.AND;

    public Group(Grouping grouping, Criteria... expressions) {
        this.grouping = grouping;
        this.expressions = expressions;
        hashCode = doHashCode();

    }

    private int doHashCode() {
        int result = expressions != null ? Arrays.hashCode(expressions) : 0;
        result = 31 * result + (grouping != null ? grouping.hashCode() : 0);
        return result;

    }

    private String doToString() {

        if (toString == null) {


            CharBuf builder = CharBuf.create(80);
            builder.add("{");
            builder.add("\"expressions\":");
            builder.add(Arrays.toString(expressions));
            builder.add(", \"grouping\":");
            builder.add(String.valueOf(grouping));
            builder.add('}');
            toString = builder.toString();
        }
        return toString;

    }

    public Grouping getGrouping() {
        return grouping;
    }


    public Criteria[] getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (!Arrays.equals(expressions, group.expressions)) return false;
        if (grouping != group.grouping) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return doToString();
    }

    public static class And extends Group {

        public And(Criteria... expressions) {
            super(Grouping.AND, expressions);
        }


        @Override
        public void prepare ( Map<String, FieldAccess> fields, Object owner ) {

        }

        @Override
        public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
            for (Criteria c : expressions) {
                c.prepare ( fields, owner );
                if (!c.resolve ( fields, owner )) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class Or extends Group {

        public Or(Criteria... expressions) {
            super(Grouping.OR, expressions);
        }

        @Override
        public void prepare ( Map<String, FieldAccess> fields, Object owner ) {

        }

        @Override
        public boolean resolve(Map<String, FieldAccess> fields, Object owner) {
            for (Criteria c : expressions) {
                c.prepare ( fields, owner );
                if (c.resolve(fields, owner)) {
                    return true;
                }
            }
            return false;
        }
    }

}
