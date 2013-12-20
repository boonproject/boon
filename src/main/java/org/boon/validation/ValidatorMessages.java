package org.boon.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <p>
 * A collection of messages.
 * </p>
 *
 * @author Rick Hightower
 */
public class ValidatorMessages implements Serializable, ValidatorMessageHolder, Iterable<ValidatorMessage> {
    private List<ValidatorMessage> messages = new ArrayList<ValidatorMessage> ();

<<<<<<< HEAD
    public Iterator<ValidatorMessage> iterator () {
=======
    public Iterator<ValidatorMessage> iterator() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.messages.iterator ();
    }

    public void add ( ValidatorMessage message ) {
        messages.add ( message );
    }

    @Override
<<<<<<< HEAD
    public boolean hasError () {
=======
    public boolean hasError() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        for ( ValidatorMessage message : messages ) {
            if ( message.hasError () ) {
                return true;
            }

        }
        return false;
    }
}
