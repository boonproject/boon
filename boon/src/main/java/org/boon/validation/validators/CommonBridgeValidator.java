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

import java.lang.reflect.Method;


/**
 * Invokes commons validator validators.
 * <p/>
 * <p>
 * <small>
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public class CommonBridgeValidator extends BaseValidator {

    private Class<?> validatorClass;
    private String methodName = "isValid";
    private String factoryMethod = "getInstance";
    private Object validator;
    private Method validateMethod;

    public ValidatorMessageHolder validate( Object object, String fieldLabel ) {
        ValidatorMessage message = new ValidatorMessage();
        if ( object == null ) {
            return message;
        }
        boolean valid = false;
        try {
            initValidatorIfNeeded();
            initValidateMethodIfNeeded();
            valid = ( Boolean ) validateMethod.invoke( validator, new Object[]{ ( String ) object } );

        } catch ( Exception exception ) {
            throw new RuntimeException( "Fatal exception trying to "
                    + "create validator, probably a missing jar or bad "
                    + "class name in spring context", exception );
        }
        if ( !valid ) {
            populateMessage( message, fieldLabel );
        }
        return message;
    }

    private void initValidateMethodIfNeeded() throws Exception {
        if ( validateMethod == null ) {
            validateMethod = validatorClass.getMethod( methodName, new Class[]{ String.class } );
        }
    }

    /**
     * @throws Exception if something goes wrong
     */
    private void initValidatorIfNeeded() throws Exception {
        if ( validator == null ) {
            if ( factoryMethod == null ) {
                validator = validatorClass.newInstance();
            } else {
                Method method = validatorClass.getMethod( factoryMethod );
                validator = method.invoke( null, ( Object[] ) null );
            }
        }
    }

    public void setMethodName( String methodName ) {
        this.methodName = methodName;
    }

    public void setValidatorClass( Class<?> validatorClass ) {
        this.validatorClass = validatorClass;
    }

    public void setFactoryMethod( String factoryMethod ) {
        this.factoryMethod = factoryMethod;
    }

}
