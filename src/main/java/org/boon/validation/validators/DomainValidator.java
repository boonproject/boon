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

import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;
import org.boon.Maps;
import org.boon.core.reflection.BeanUtils;
import org.boon.validation.ValidationContext;
import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This validator is designed for domain-driven validation
 * of a group of child objects from a parent.  For instance, a parent
 * with a OneToMany relationship with a child may need to validate
 * the child against the other children associated with the parent.
 * In this scenario, the validation framework will invoke this
 * validator when the @DomainValidation annotation is used on a field
 * in the child object by invoking the parent object validation
 * method identified by the "method" attribute.  The parent object
 * is identified by the "parentProperty" attribute.  If the parent
 * attribute is null or missing, then the validation method
 * is presumed to exist in the child.
 * <p/>
 * The validation method will be passed the child object and the
 * field value as method.invoke(child, fieldValue)
 *
 * @author Paul Tabor
 */
public class DomainValidator extends BaseValidator {
    private static final long serialVersionUID = 1L;

    private Object rootObject;

    public void setRootObject( Object rootObject ) {
        this.rootObject = rootObject;
    }

    private static Set<String> allowedPackages = new HashSet<String>();

    static {
        allowedPackages.add( "org.boon.annotation.validation" );
    }

    public DomainValidator() {
    }

    @SuppressWarnings ( "unchecked" )
    public ValidatorMessageHolder validate( Object fieldValue, String fieldLabel ) {
        // So, we already know that this field has been decorated with @DomainValidation annotation.
        // That's why we're here. We need to read the validation attributes to find the appropriate
        // jstl method (parent or child) to invoke for validation.

        String detailMessage = "";
        String summaryMessage = "";
        this.noMessages = true;
        boolean error = false;
        Object child;

        // The first "parent" is actually the child decorated with the validation annotation
        if ( rootObject == null ) {
            // Grab it from the validation context if it hasn't been provided
            child = ValidationContext.getCurrentInstance().getParentObject();
        } else {
            // Useful if the rootObject is injected, i.e. from a Unit test
            child = rootObject;
        }


        List<AnnotationData> annotationDataForProperty = Annotations.getAnnotationDataForProperty( child.getClass(), fieldLabel, false, allowedPackages );
        if ( annotationDataForProperty.size() == 0 ) {
            annotationDataForProperty = Annotations.getAnnotationDataForField( child.getClass(), fieldLabel, allowedPackages );
        }

        Map map = Maps.toMap( "name", annotationDataForProperty );

        boolean found = map.get( "domainValidation" ) != null;
        boolean sameLevel = false;
        boolean noArgs = false;

        if ( found ) {
            AnnotationData ad = ( AnnotationData ) map.get( "domainValidation" );
            Object parentReference = ad.getValues().get( "parentProperty" );
            Object validator = null;

            if ( ( parentReference != null ) && ( !"".equals( parentReference ) ) ) {
                // If the parentProperty is specified, then find the parent class

                BeanUtils.getPropertyValue( child, ( String ) parentReference );

            } else {
                // Otherwise, the validation method is assumed to be in the child
                validator = child;
                sameLevel = true;
            }

            noArgs = ( Boolean ) ad.getValues().get( "global" );

            // Make sure the method exists
            Method m = null;

            if ( validator != null ) {
                try {

                    String methodName = ( String ) ad.getValues().get( "method" );

                    if ( noArgs ) {
                        m = validator.getClass().getDeclaredMethod( methodName );
                    } else if ( sameLevel ) {
                        Class[] parameters = new Class[ 1 ];

                        parameters[ 0 ] = BeanUtils.getPropertyType( child, fieldLabel );
                        m = validator.getClass().getDeclaredMethod( methodName, parameters );
                    } else {
                        Class[] parameters = new Class[ 2 ];
                        parameters[ 0 ] = child.getClass();


                        parameters[ 1 ] = BeanUtils.getPropertyType( child, fieldLabel );

                        m = validator.getClass().getDeclaredMethod( methodName, parameters );
                    }

                } catch ( NoSuchMethodException nsme ) {
                    detailMessage = nsme.getMessage();
                    summaryMessage = nsme.getMessage();
                    error = true;
                } catch ( Exception e ) {
                    detailMessage = e.getMessage();
                    summaryMessage = e.getMessage();
                    error = true;
                }
            }

            // Invoke the validation method and watch for any exceptions
            try {
                if ( noArgs ) {
                    m.invoke( validator );
                } else if ( sameLevel ) {
                    m.invoke( validator, new Object[]{ fieldValue } );
                } else {
                    m.invoke( validator, new Object[]{ child, fieldValue } );
                }
            } catch ( IllegalAccessException iae ) {
                detailMessage = iae.getCause().getMessage();
                summaryMessage = iae.getCause().getMessage();
                error = true;
            } catch ( InvocationTargetException ite ) {
                detailMessage = ite.getCause().getMessage();
                summaryMessage = ite.getCause().getMessage();
                error = true;
            } catch ( Exception e ) {
                detailMessage = e.getCause().getMessage();
                summaryMessage = e.getCause().getMessage();
                error = true;
            }
        }

        ValidatorMessage message = new ValidatorMessage();

        // If there were any errors, populate the validation message
        if ( error ) {
            message = new ValidatorMessage( summaryMessage, detailMessage );
            populateMessage( message, ( noArgs ? null : fieldLabel ) );
        }

        return message;
    }

}
