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

<<<<<<< HEAD
    public boolean isNegate () {
=======
    public boolean isNegate() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
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
<<<<<<< HEAD
    protected String getMatch () {
=======
    protected String getMatch() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.match;
    }

    public void setMatch ( String regex ) {
        this.match = regex;
    }

<<<<<<< HEAD
    public ValidatorMessageHolder validate ( Object object, String fieldLabel ) {
=======
    public ValidatorMessageHolder validate( Object object, String fieldLabel ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
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
<<<<<<< HEAD
    private Pattern compileRegex () {
=======
    private Pattern compileRegex() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        Pattern pattern = compiledRegexCache.get ( getMatch () );
        if ( pattern == null ) {
            pattern = Pattern.compile ( getMatch () );
            compiledRegexCache.put ( getMatch (), pattern );
        }
        return pattern;
    }

}
