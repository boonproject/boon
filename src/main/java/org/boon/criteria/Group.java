package org.boon.criteria;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

import java.util.Arrays;
import java.util.Map;

public abstract class Group extends Criteria {

    protected Criteria[] expressions;

    private final int hashCode;
    private String toString;

    private Grouping grouping = Grouping.AND;


    //TODO there is an opportunity to optimize this so Group holds on to fields for subgroups.
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

    public Group ( Grouping grouping, Criteria... expressions ) {
        this.grouping = grouping;
        this.expressions = expressions;
        hashCode = doHashCode ();

    }

<<<<<<< HEAD
    private int doHashCode () {
=======
    private int doHashCode() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        int result = expressions != null ? Arrays.hashCode ( expressions ) : 0;
        result = 31 * result + ( grouping != null ? grouping.hashCode () : 0 );
        return result;

    }

<<<<<<< HEAD
    private String doToString () {
=======
    private String doToString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( toString == null ) {


            CharBuf builder = CharBuf.create ( 80 );
            builder.add ( "{" );
            builder.add ( "\"expressions\":" );
            builder.add ( Arrays.toString ( expressions ) );
            builder.add ( ", \"grouping\":" );
            builder.add ( String.valueOf ( grouping ) );
            builder.add ( '}' );
            toString = builder.toString ();
        }
        return toString;

    }

<<<<<<< HEAD
    public Grouping getGrouping () {
=======
    public Grouping getGrouping() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return grouping;
    }


<<<<<<< HEAD
    public Criteria[] getExpressions () {
=======
    public Criteria[] getExpressions() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return expressions;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass () != o.getClass () ) return false;

        Group group = ( Group ) o;

        if ( !Arrays.equals ( expressions, group.expressions ) ) return false;
        if ( grouping != group.grouping ) return false;

        return true;
    }

    @Override
<<<<<<< HEAD
    public int hashCode () {
=======
    public int hashCode() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return hashCode;
    }

    @Override
<<<<<<< HEAD
    public String toString () {
=======
    public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return doToString ();
    }

    public static class And extends Group {

        public And ( Criteria... expressions ) {
            super ( Grouping.AND, expressions );
        }


        @Override
        public boolean resolve ( Map<String, FieldAccess> fields, Object owner ) {
            for ( Criteria c : expressions ) {
                c.prepareForGroupTest ( fields, owner );
                if ( !c.test ( owner ) ) {
                    return false;
                }
                c.cleanAfterGroupTest ();
            }
            return true;
        }
    }

    public static class Or extends Group {

        public Or ( Criteria... expressions ) {
            super ( Grouping.OR, expressions );
        }

        @Override
        public void prepareForGroupTest ( Map<String, FieldAccess> fields, Object owner ) {

        }

        @Override
        public boolean resolve ( Map<String, FieldAccess> fields, Object owner ) {
            for ( Criteria c : expressions ) {
                c.prepareForGroupTest ( fields, owner );
                if ( c.test ( owner ) ) {
                    return true;
                }
                c.cleanAfterGroupTest ();
            }
            return false;
        }
    }

}
