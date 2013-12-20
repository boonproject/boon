package org.boon.validation.validators;

import org.boon.core.reflection.Reflection;
import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;


/**
 * LongRangeValidator works with all integer ranges.
 */
public class LengthValidator extends BaseValidator {


    /**
     * The min value.
     */
    private int min = 0;
    /**
     * The max value.
     */
    private int max = Integer.MAX_VALUE;

    /**
     * Perform the actual validation.
     *
     * @param fieldValue the value to validate
     * @param fieldLabel the logical name of the value used for generating error messages
     */
<<<<<<< HEAD
    public ValidatorMessageHolder validate ( Object fieldValue, String fieldLabel ) {
=======
    public ValidatorMessageHolder validate( Object fieldValue, String fieldLabel ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        ValidatorMessage validatorMessage = new ValidatorMessage ();
        if ( fieldValue == null ) {
            return validatorMessage;
        }

        int len = Reflection.len ( fieldValue );

        if ( !( len >= min && len <= max ) ) {
            populateMessage ( validatorMessage, fieldLabel, min, max );
        }


        return validatorMessage;

    }

    public void setMax ( int max ) {
        this.max = max;
    }

    public void setMin ( int min ) {
        this.min = min;
    }

}
