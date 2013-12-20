package org.boon.validation.validators;

/**
 * This class checks to see if the values are Equal.
 */
public class EqualsCompareValidator extends AbstractCompareValidator {

    @Override
    protected boolean checkValidity ( Object object, Object compareToPropertyValue ) {
        boolean valid = object.equals ( compareToPropertyValue );
        return valid;
    }

}
