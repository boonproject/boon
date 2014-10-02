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


/**
 * Abstract class for range validation.
 */
@SuppressWarnings ( "unchecked" )
public abstract class AbstractRangeValidator extends BaseValidator {

    /* The min value for comparison. */
    private Comparable realMin;
    /* The max value for comparison. */
    private Comparable realMax;
    /* Has this been initialized? */
    private boolean isInitialized;

    /* Allows this base class to be initialized by the subclasses. */
    protected void init( Comparable min, Comparable max ) {
        this.realMin = min;
        this.realMax = max;
        assert min.compareTo( max ) < 0;
        isInitialized = true;
    }

    /* Checks to see if the value is less than the min. */
    protected boolean isValueGreaterThanMin( Comparable value ) {
        if ( realMin == null ) {
            return true;
        }
        return value.compareTo( realMin ) >= 0;
    }

    /* Checks to see if the value is greater than the max. */
    protected boolean isValueLessThanMax( Comparable value ) {
        if ( realMax == null ) {
            return true;
        }
        return value.compareTo( realMax ) <= 0;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
