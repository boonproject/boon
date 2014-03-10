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

import org.boon.messages.MessageSpecification;
import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;

/**
 * LongRangeValidator works with all integer ranges.
 */
public class LongRangeValidator extends AbstractRangeValidator {

    /**
     * The min value.
     */
    private Long min;
    /**
     * The max value.
     */
    private Long max;
    /* What type are we working with: Integer, Long, etc. */
    private Class<?> type;
    /* The underMin message used if the value is under the min. */
    private MessageSpecification underMin;
    /* The overMax message used if the value is over the max. */
    private MessageSpecification overMax;


    /**
     * Perform the actual validation.
     *
     * @param fieldValue the value to validate
     * @param fieldLabel the logical name of the value used for generating error messages
     */
    @SuppressWarnings ( "unchecked" )
    public ValidatorMessageHolder validate( Object fieldValue, String fieldLabel ) {
        ValidatorMessage validatorMessage = new ValidatorMessage();
        if ( fieldValue == null ) {
            return validatorMessage;
        }

        dynamicallyInitIfNeeded( fieldValue );

        if ( !super.isValueGreaterThanMin( ( Comparable ) fieldValue ) ) {
            populateMessage( underMin, validatorMessage, fieldLabel, min );
        } else if ( !super.isValueLessThanMax( ( Comparable ) fieldValue ) ) {
            populateMessage( overMax, validatorMessage, fieldLabel, max );
        }

        return validatorMessage;

    }


    /* Initialize this instance. */
    public void init() {
        /* If the underMin message was not injected, create a default. */
        if ( underMin == null ) {
            underMin = new MessageSpecification();
            underMin.setDetailMessage( "{validator.range.underMin.detail}" );
            underMin.setSummaryMessage( "{validator.range.underMin.summary}" );
        }
        /* If the overMax message was not injected, create a default. */
        if ( overMax == null ) {
            overMax = new MessageSpecification();
            overMax.setDetailMessage( "{validator.range.overMax.detail}" );
            overMax.setSummaryMessage( "{validator.range.overMax.summary" );
        }
        /* If the type was not injected, stop initialization. */
        if ( type == null ) {
            return;
        }
        /* Initialize based on type for all Integer value
         * so that LongRangeValidator can be used
    	 * for int, short, byte, and long. */
        if ( !isInitialized() ) {
            if ( type.equals( Integer.class ) ) {
                init( new Integer( min.intValue() ), new Integer( max.intValue() ) );
            } else if ( type.equals( Byte.class ) ) {
                init( new Byte( min.byteValue() ), new Byte( max.byteValue() ) );
            } else if ( type.equals( Short.class ) ) {
                init( new Short( min.byteValue() ), new Short( max.byteValue() ) );
            } else {
                init( min, max );
            }
        }
    }


    /**
     * If the type was not initialized, we can still figure it out at runtime.
     *
     * @param value
     */
    private void dynamicallyInitIfNeeded( Object value ) {
        /* Check to see if this class was already initialized,
         * if not, initialize it based on the type of the value.
		 */
        if ( !isInitialized() ) {
            if ( value instanceof Integer ) {
                init( new Integer( min.intValue() ), new Integer( max.intValue() ) );
            } else if ( value instanceof Byte ) {
                init( new Byte( min.byteValue() ), new Byte( max.byteValue() ) );
            } else if ( value instanceof Short ) {
                init( new Short( min.shortValue() ), new Short( max.shortValue() ) );
            } else {
                init( min, max );
            }
        }
    }

    public void setMax( Long max ) {
        this.max = max;
    }


    public void setMin( Long min ) {
        this.min = min;
    }

    public void setType( Class<?> type ) {
        this.type = type;
    }

    protected void setOverMax( MessageSpecification overMax ) {
        this.overMax = overMax;
    }

    protected void setUnderMin( MessageSpecification underMin ) {
        this.underMin = underMin;
    }

}
