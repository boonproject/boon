package org.boon.validation.validators;


import org.boon.validation.FieldValidator;
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
public class StopOnRuleValidator implements FieldValidator {
    private String ruleName;

<<<<<<< HEAD
    public String getRuleName () {
=======
    public String getRuleName() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return ruleName;
    }

    public void setRuleName ( String ruleName ) {
        this.ruleName = ruleName;
    }

<<<<<<< HEAD
    public ValidatorMessageHolder validate ( Object fieldValue, String fieldLabel ) {
=======
    public ValidatorMessageHolder validate( Object fieldValue, String fieldLabel ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new ValidatorMessage ();
    }

}
