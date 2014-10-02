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

import org.boon.Boon;
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
    public ValidatorMessageHolder validate( Object fieldValue, String fieldLabel ) {
        ValidatorMessage validatorMessage = new ValidatorMessage();
        if ( fieldValue == null ) {
            return validatorMessage;
        }

        int len = Boon.len( fieldValue );

        if ( !( len >= min && len <= max ) ) {
            populateMessage( validatorMessage, fieldLabel, min, max );
        }


        return validatorMessage;

    }

    public void setMax( int max ) {
        this.max = max;
    }

    public void setMin( int min ) {
        this.min = min;
    }

}
