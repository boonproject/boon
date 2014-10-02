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


import org.boon.validation.FieldValidator;
import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;
import org.boon.validation.ValidatorMessages;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Combines a bunch of validators into one.
 * </p>
 *
 * @author Rick Hightower
 */
public class CompositeValidator implements FieldValidator {
    private List<FieldValidator> validatorList = new ArrayList<>();
    private RequiredValidator requiredValidator = null;
    private List<String> detailArgs;
    private List<String> summaryArgs;
    private String stopOnRule = "";
    private boolean stopOnFirstRule = false;
    private boolean stopOnBlank = true;

    public void setValidatorList( List<FieldValidator> list ) {
        this.validatorList = list;
        StopOnRuleValidator stopOnRuleValidator = null;
        for ( FieldValidator validator : list ) {
            if ( validator instanceof RequiredValidator ) {
                requiredValidator = ( RequiredValidator ) validator;
            }
            if ( validator instanceof StopOnRuleValidator ) {
                stopOnRuleValidator = ( StopOnRuleValidator ) validator;
            }
        }


        if ( stopOnRuleValidator != null ) {
            validatorList.remove( stopOnRule );
            String ruleName = stopOnRuleValidator.getRuleName();
            if ( "first".equals( ruleName ) || ruleName == null ) {
                stopOnFirstRule = true;
            } else {
                stopOnRule = ruleName;
            }
        }
        if ( requiredValidator != null ) {
            validatorList.remove( requiredValidator );
        }
    }

    public ValidatorMessageHolder validate( Object object, String fieldLabel ) {

        ValidatorMessages messages = new ValidatorMessages(); //holds error messages.
        
        /* Validate with the requiredValidator if it is present. */
        ValidatorMessage requiredMessage = validateWithRequriedIfPresent( object, fieldLabel, messages );

        boolean proceed = !( stopOnBlank && ( object == null || object.toString().trim().length() == 0 ) );

        /* If the requiredMessage from the requiredValidator is null, then there was not a required validator present. */
        /* If the requiredMessage is present then check to see if it has errors, only validate further if
         * the requiredMessage has no error. */
        if ( requiredMessage == null || !requiredMessage.hasError() ) {
            if ( proceed ) {
                runValidationRules( object, fieldLabel, messages );
            }
        }

        return messages;
    }

    private void runValidationRules( Object object, String fieldLabel, ValidatorMessages messages ) {
        for ( FieldValidator validator : validatorList ) {
            putArgs( validator );
            ValidatorMessage message = ( ValidatorMessage ) validator.validate( object, fieldLabel );
            if ( message.hasError() ) {
                messages.add( message );
                if ( this.stopOnFirstRule ) {
                    break;
                } else if ( validator.getClass().getSimpleName().equalsIgnoreCase( stopOnRule ) ) {
                    break;
                }
            }
        }
    }

    private ValidatorMessage validateWithRequriedIfPresent( Object object, String fieldLabel, ValidatorMessages messages ) {
        ValidatorMessage requiredMessage = null;
        if ( requiredValidator != null ) {
            putArgs( requiredValidator );
            requiredMessage = ( ValidatorMessage ) requiredValidator.validate( object, fieldLabel );
            if ( requiredMessage.hasError() ) {
                messages.add( requiredMessage );
            }
        }
        return requiredMessage;
    }

    private void putArgs( FieldValidator validator ) {
        if ( validator instanceof BaseValidator ) {
            BaseValidator aValidator = ( BaseValidator ) validator;
            aValidator.setDetailArgs( this.detailArgs );
            aValidator.setSummaryArgs( this.summaryArgs );
        }
    }


    public void setDetailArgs( List<String> detailArgKeys ) {
        this.detailArgs = detailArgKeys;
    }


    public void setSummaryArgs( List<String> summaryArgKeys ) {
        this.summaryArgs = summaryArgKeys;
    }

    public void setStopOnBlank( boolean stopOnBlank ) {
        this.stopOnBlank = stopOnBlank;
    }

}
