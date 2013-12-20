package org.boon.criteria;

import org.boon.Lists;
import org.boon.datarepo.ObjectEditor;

import java.io.Serializable;
import java.util.List;


public abstract class Update implements Serializable {

    private String name;

<<<<<<< HEAD
    public String getName () {
=======
    public String getName() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return name;
    }


    public abstract void doSet ( ObjectEditor repo, Object item );

<<<<<<< HEAD
    public static Update set ( final String name, final int value ) {
=======
    public static Update set( final String name, final int value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                repo.modify ( item, name, value );
            }
        };
    }

<<<<<<< HEAD
    public static Update incInt ( final String name ) {
=======
    public static Update incInt( final String name ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                int v = repo.getInt ( item, name );
                v++;
                repo.modify ( item, name, v );
            }
        };
    }

<<<<<<< HEAD
    public static Update incPercent ( final String name, final int percent ) {
=======
    public static Update incPercent( final String name, final int percent ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {

            //Avoid the lookup, pass the fields.
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                int value = repo.getInt ( item, name );
                double dvalue = value;
                double dprecent = percent / 100.0;
                dvalue = dvalue + ( dvalue * dprecent );
                value = ( int ) dvalue;
                repo.modify ( item, name, value );
            }
        };
    }

<<<<<<< HEAD
    public static Update set ( final String name, final long value ) {
=======
    public static Update set( final String name, final long value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                repo.modify ( item, name, value );
            }
        };
    }

<<<<<<< HEAD
    public static Update set ( final String name, final Object value ) {
=======
    public static Update set( final String name, final Object value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                repo.modify ( item, name, value );
            }
        };
    }

<<<<<<< HEAD
    public static Update set ( final String name, final byte value ) {
=======
    public static Update set( final String name, final byte value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                repo.modify ( item, name, value );
            }
        };
    }

<<<<<<< HEAD
    public static Update set ( final String name, final float value ) {
=======
    public static Update set( final String name, final float value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                repo.modify ( item, name, value );
            }
        };
    }

<<<<<<< HEAD
    public static Update set ( final String name, final char value ) {
=======
    public static Update set( final String name, final char value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                repo.modify ( item, name, value );
            }
        };
    }

<<<<<<< HEAD
    public static Update set ( final String name, final String value ) {
=======
    public static Update set( final String name, final String value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new Update () {
            @Override
            public void doSet ( ObjectEditor repo, Object item ) {
                repo.modify ( item, name, value );
            }
        };
    }

    public static List<Update> update ( Update... values ) {
        return Lists.list ( values );
    }


}
