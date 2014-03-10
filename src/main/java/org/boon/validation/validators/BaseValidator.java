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


import org.boon.core.NameAware;
import org.boon.messages.MessageSpecification;
import org.boon.validation.FieldValidator;
import org.boon.validation.ValidatorMessage;


/**
 * <p>
 * <small>
 * Base class for some validators.
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public abstract class BaseValidator extends MessageSpecification implements NameAware, FieldValidator {


    public boolean noMessages = false;

    public boolean isNoMessages() {
        return noMessages;
    }

    public void setNoMessages( boolean noMessages ) {
        this.noMessages = noMessages;
    }

    protected void populateMessage( ValidatorMessage message, String fieldLabel, Object... args ) {
        populateMessage( null, message, fieldLabel, args );
    }

    protected void populateMessage( MessageSpecification ms, ValidatorMessage message, String fieldLabel, Object... args ) {
        if ( ms == null ) {
            ms = this;
        }

        ms.setCurrentSubject( fieldLabel );
        if ( !noMessages ) {
            message.setSummary( ms.createSummaryMessage( args ) );
            message.setDetail( ms.createDetailMessage( args ) );
        }
        ms.setCurrentSubject( null );
        message.setHasError( true );

    }


}
