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
public class MatchAnyRegexValidator extends BaseValidator {

    private String[] matches;

    private Map<String, Pattern> compiledRegexCache = new HashMap<String, Pattern> ();


    public ValidatorMessageHolder validate ( Object object, String fieldLabel ) {
        ValidatorMessage message = new ValidatorMessage ();
        if ( object == null ) {
            return message;
        }
        String string = object.toString ();
        int validCount = 0;

        for ( String match : matches ) {
            Pattern pattern = compileRegex ( match );
            if ( pattern.matcher ( string ).matches () ) {
                validCount++;
            }
        }

        if ( validCount == 0 ) {
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
    private Pattern compileRegex ( String match ) {

        Pattern pattern = compiledRegexCache.get ( match );
        if ( pattern == null ) {
            pattern = Pattern.compile ( match );
            compiledRegexCache.put ( match, pattern );
        }
        return pattern;
    }

    public void setMatches ( String[] matches ) {
        this.matches = matches;
    }

}
