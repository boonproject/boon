/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

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
    private Map<String, Pattern> compiledRegexCache = new HashMap<>();

    public boolean isNegate() {
        return this.negate;
    }

    public void setNegate( boolean negate ) {
        this.negate = negate;
    }

    /**
     * The match.
     *
     * @return the regular expression
     */
    protected String getMatch() {
        return this.match;
    }

    public void setMatch( String regex ) {
        this.match = regex;
    }

    public ValidatorMessageHolder validate( Object object, String fieldLabel ) {
        ValidatorMessage message = new ValidatorMessage();
        if ( object == null ) {
            return message;
        }
        String string = object.toString();
        Pattern pattern = compileRegex();
        boolean valid;
        if ( negate ) {
            valid = !pattern.matcher( string ).matches();
        } else {
            valid = pattern.matcher( string ).matches();
        }

        if ( !valid ) {
            populateMessage( message, fieldLabel );
            return message;
        }

        return message;
    }

    /**
     * Compiles a match.
     *
     * @return the resulting pattern object
     */
    private Pattern compileRegex() {

        Pattern pattern = compiledRegexCache.get( getMatch() );
        if ( pattern == null ) {
            pattern = Pattern.compile( getMatch() );
            compiledRegexCache.put( getMatch(), pattern );
        }
        return pattern;
    }

}
