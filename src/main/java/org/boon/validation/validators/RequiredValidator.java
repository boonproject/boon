package org.boon.validation.validators;


import org.boon.Str;
import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;

/**
 * <p>
 * <small>
 * Required validator.
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public class RequiredValidator extends BaseValidator {

<<<<<<< HEAD
    public void init () {
=======
    public void init() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( Str.isEmpty ( this.getDetailMessage () ) ) {
            this.setDetailMessage ( "{validator.required.detail}" );
        }

        if ( !this.isNoSummary () ) {
            if ( Str.isEmpty ( this.getSummaryMessage () ) ) {
                this.setSummaryMessage ( "{validator.required.summary}" );
            }
        }
    }

<<<<<<< HEAD
    public ValidatorMessageHolder validate ( Object object, String fieldLabel ) {
=======
    public ValidatorMessageHolder validate( Object object, String fieldLabel ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        ValidatorMessage message = new ValidatorMessage ();

        if ( object instanceof String ) {
            String string = ( String ) object;
            boolean valid = string != null && !string.trim ().equals ( "" );
            if ( !valid ) {
                populateMessage ( message, fieldLabel );
            }

        } else {
            if ( object == null ) {
                populateMessage ( message, fieldLabel );
            }
        }

        return message;
    }


}
