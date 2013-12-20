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

    public void init () {
        if ( Str.isEmpty ( this.getDetailMessage () ) ) {
            this.setDetailMessage ( "{validator.required.detail}" );
        }

        if ( !this.isNoSummary () ) {
            if ( Str.isEmpty ( this.getSummaryMessage () ) ) {
                this.setSummaryMessage ( "{validator.required.summary}" );
            }
        }
    }

    public ValidatorMessageHolder validate ( Object object, String fieldLabel ) {
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
