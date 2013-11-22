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

    public String getRuleName( ) {
        return ruleName;
    }

    public void setRuleName( String ruleName ) {
        this.ruleName = ruleName;
    }

    public ValidatorMessageHolder validate( Object fieldValue, String fieldLabel ) {
        return new ValidatorMessage ( );
    }

}
