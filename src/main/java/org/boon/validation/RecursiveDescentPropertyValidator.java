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

package org.boon.validation;


import org.boon.Exceptions;
import org.boon.Maps;
import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.Reflection;
import org.boon.validation.readers.AnnotationValidatorMetaDataReader;
import org.boon.validation.validators.CompositeValidator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;


public class RecursiveDescentPropertyValidator {
    protected ValidatorMetaDataReader validatorMetaDataReader = new AnnotationValidatorMetaDataReader();

    public class MessageHolder {
        public final String propertyPath;
        public final ValidatorMessageHolder holder;

        MessageHolder( String propertyPath, ValidatorMessageHolder holder ) {
            this.propertyPath = propertyPath;
            this.holder = holder;
        }
    }


    /**
     * Create the validator by looking it up in the ObjectRegistry and then
     * populating it with values from the meta-data list.
     *
     * @param validationMetaDataList Holds metadataInformation about validation.
     * @return composite validator with all of the validators for this property present.
     */
    protected CompositeValidator createValidator(
            List<ValidatorMetaData> validationMetaDataList ) {

        /*
         * A field (property) can be associated with many validators so we use a
         * CompositeValidator to hold all of the validators associated with this
         * validator.
         */
        CompositeValidator compositeValidator = new CompositeValidator(); // hold
        // all
        // of
        // the
        // validators
        // associated
        // with
        // the
        // field.

        /*
         * Lookup the list of validators for the current field and initialize
         * them with validation meta-data properties.
         */
        List<FieldValidator> validatorsList =
                lookupTheListOfValidatorsAndInitializeThemWithMetaDataProperties( validationMetaDataList );

        compositeValidator.setValidatorList( validatorsList );

        return compositeValidator;
    }


    private List<PropertyDescriptor> getFieldsToValidate( Object object ) {
        List<PropertyDescriptor> properties;
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo( object.getClass() );
        } catch ( IntrospectionException e ) {

            throw new RuntimeException( e );
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        properties = new ArrayList<>( propertyDescriptors.length );
        for ( PropertyDescriptor pd : propertyDescriptors ) {
            if ( !pd.getName().equals( "class" ) ) {
                properties.add( pd );
            }
        }
        return properties;
    }

    protected List<ValidatorMetaData> readMetaData( Class<?> clazz,
                                                    String propertyName ) {
        return validatorMetaDataReader.readMetaData( clazz,
                propertyName );
    }

    private void validateProperty( final Object object, final Object objectProperty, final String property,
                                   List<MessageHolder> vMessageHolders ) {

        List<ValidatorMetaData> metaDataList = readMetaData( object.getClass(),
                property );
        CompositeValidator cv = createValidator( metaDataList );
        ValidatorMessageHolder holder = cv.validate( objectProperty, property );
        vMessageHolders.add( new MessageHolder( ValidationContext.getBindingPath(), holder ) );
    }

    protected boolean shouldFieldBeValidated() {
        return true;
    }


    public List<MessageHolder> validateObject( final Object object ) {


        List<MessageHolder> list = Collections.EMPTY_LIST;
        try {
            ValidationContext.create();
            list = validateObjectWithMessages( object, null );
        } finally {
            ValidationContext.destroy();
        }
        return list;
    }

    public List<MessageHolder> validateObject( final Object object, Map<String, Object> registry ) {


        List<MessageHolder> list = Collections.EMPTY_LIST;
        try {
            ValidationContext.create();
            ValidationContext.get().setObjectRegistry( registry );
            list = validateObjectWithMessages( object, null );
        } finally {
            ValidationContext.destroy();
        }
        return list;
    }


    public List<MessageHolder> validateObjectWithMessages( final Object object, List<MessageHolder> validationMessages ) {
        List<PropertyDescriptor> fieldsToValidate = getFieldsToValidate( object );
        Map<String, Object> objectPropertiesAsMap = Maps.toMap( object );
        if ( validationMessages == null ) {
            validationMessages = new ArrayList<>();
        }

        for ( PropertyDescriptor field : fieldsToValidate ) {

            /* Keep track of the field name and parentObject so the field validators can access them. */
            ValidationContext.get().pushProperty( field.getName() );
            ValidationContext.get().setParentObject( object );
            if ( shouldFieldBeValidated() ) {
                Object propertyObject = objectPropertiesAsMap.get( field.getName() );
                validateProperty( object, propertyObject, field.getName(), validationMessages );

                /* Don't validate if it is not a basic type. */
                if ( propertyObject != null && !Typ.isBasicType( propertyObject ) ) {
                    validateObjectWithMessages( propertyObject, validationMessages );
                }
            }
            ValidationContext.get().pop();
        }

        return validationMessages;

    }


    /**
     * Lookup the list of validators for the current field and initialize them
     * with validation meta-data properties.
     *
     * @param validationMetaDataList list of validation meta-data
     * @return list of field validators.
     */
    private List<FieldValidator>
    lookupTheListOfValidatorsAndInitializeThemWithMetaDataProperties(
            List<ValidatorMetaData> validationMetaDataList ) {

        List<FieldValidator> validatorsList = new ArrayList<>();

        /*
         * Look up the crank validators and then apply the properties from the
         * validationMetaData to them.
         */
        for ( ValidatorMetaData validationMetaData : validationMetaDataList ) {
            /* Look up the FieldValidator. */
            FieldValidator validator = lookupValidatorInRegistry(
                    validationMetaData
                            .getName() );
            /*
             * Apply the properties from the validationMetaData to the
             * validator.
             */
            applyValidationMetaDataPropertiesToValidator( validationMetaData,
                    validator );
            validatorsList.add( validator );
        }
        return validatorsList;
    }

    /**
     * This method looks up the validator in the registry.
     *
     * @param validationMetaDataName The name of the validator that we are looking up.
     * @return field validator
     */
    private FieldValidator lookupValidatorInRegistry(
            String validationMetaDataName ) {


        Map<String, Object> applicationContext = ValidationContext.get().getObjectRegistry();

        Exceptions.requireNonNull( applicationContext );

        return ( FieldValidator ) applicationContext
                .get( "/org/boon/validator/" + validationMetaDataName );
    }

    /**
     * This method applies the properties from the validationMetaData to the
     * validator uses Spring's BeanWrapperImpl.
     *
     * @param metaData  validation meta data
     * @param validator field validator
     */
    private void applyValidationMetaDataPropertiesToValidator(
            ValidatorMetaData metaData, FieldValidator validator ) {
        Map<String, Object> properties = metaData.getProperties();
        ifPropertyBlankRemove( properties, "detailMessage" );
        ifPropertyBlankRemove( properties, "summaryMessage" );

        BeanUtils.copyProperties( validator,
                properties );
    }

    /**
     * Removes a property if it is null or an empty string.
     * This allows the property to have a null or emtpy string in the
     * meta-data but we don't copy it to the validator if the property
     * is not set.
     *
     * @param properties properties
     * @param property   property
     */
    private void ifPropertyBlankRemove( Map<String, Object> properties, String property ) {

        Object object = properties.get( property );
        if ( object == null ) {
            properties.remove( property );
        } else if ( object instanceof String ) {
            String string = ( String ) object;
            if ( "".equals( string.trim() ) ) {
                properties.remove( property );
            }
        }
    }

    public void setValidatorMetaDataReader(
            ValidatorMetaDataReader validatorMetaDataReader ) {
        this.validatorMetaDataReader = validatorMetaDataReader;
    }

}
