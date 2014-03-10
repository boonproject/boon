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


import org.boon.validation.ValidationContext;
import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;

/**
 * <p>
 * <small>
 * AbstractCompareValidator is the base class for all validators that compare
 * two values to perform validation.
 * <p/>
 * Future versions of this class will deal with other values besides
 * simple properties like expressions.
 * <p/>
 * AbstractCompareValidator
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public abstract class AbstractCompareValidator extends BaseValidator {

    /**
     * Holds the name of the variable we are getting out of the validation
     * context. This variable could be another property.
     */
    private String compareToProperty;

    /**
     *
     */
    public ValidatorMessageHolder validate( Object value, String fieldLabel ) {
        
        /* Create the validator message. */
        ValidatorMessage message = new ValidatorMessage();

        if ( value == null ) {
            return message;
        }
        
        /* Get the comparison value. */
        Object compareToPropertyValue = lookupCompareToPropertyValue();
        
        /* Check to see if this is valid. */
        boolean valid = checkValidity( value, compareToPropertyValue );
        
        /* If it is not valid, then populate the message and 
         * return it regardless.
         */
        if ( !valid ) {
            populateMessage( message, fieldLabel );
        }

        return message;
    }


    /**
     * Check Validity of the value compared to the property value.
     * This class uses the jstl design pattern. Subclasses are suppose
     * to override this method and this class uses IoC to delegate
     * validity checking to the subclass.
     *
     * @param object
     * @param compareToPropertyValue
     * @return
     */
    protected abstract boolean checkValidity( Object object,
                                              Object compareToPropertyValue );


    /**
     * This method looks of the ValidationContext to get the
     * compareToProperty.
     *
     * @return
     */
    protected Object lookupCompareToPropertyValue() {
        return ValidationContext.getCurrentInstance()
                .getProposedPropertyValue( compareToProperty );
    }


    /**
     * @param compareToProperty
     */
    public void setCompareToProperty( String compareToProperty ) {
        this.compareToProperty = compareToProperty;
    }


    protected String getCompareToProperty() {
        return compareToProperty;
    }


}
