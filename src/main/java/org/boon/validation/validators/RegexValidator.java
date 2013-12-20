package org.boon.validation.validators;


import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * <p>
 * <small>
 * Regex validator.
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public class RegexValidator extends BaseValidator {

    private String match;
    private boolean negate;
    private Map<String, Pattern> compiledRegexCache = new HashMap<> ();

    public boolean isNegate () {
        return this.negate;
    }

    public void setNegate ( boolean negate ) {
        this.negate = negate;
    }

    /**
     * The match.
     *
     * @return the regular expression
     */
    protected String getMatch () {
        return this.match;
    }

    public void setMatch ( String regex ) {
        this.match = regex;
    }

    public ValidatorMessageHolder validate ( Object object, String fieldLabel ) {
        ValidatorMessage message = new ValidatorMessage ();
        if ( object == null ) {
            return message;
        }
        String string = object.toString ();
        Pattern pattern = compileRegex ();
        boolean valid;
        if ( negate ) {
            valid = !pattern.matcher ( string ).matches ();
        } else {
            valid = pattern.matcher ( string ).matches ();
        }

        if ( !valid ) {
            populateMessage ( message, fieldLabel );
            return message;
        }

        return message;
    }

    /**
     * Compiles a match.
     *
     * @return the resulting pattern object
     */
    private Pattern compileRegex () {

        Pattern pattern = compiledRegexCache.get ( getMatch () );
        if ( pattern == null ) {
            pattern = Pattern.compile ( getMatch () );
            compiledRegexCache.put ( getMatch (), pattern );
        }
        return pattern;
    }

}
