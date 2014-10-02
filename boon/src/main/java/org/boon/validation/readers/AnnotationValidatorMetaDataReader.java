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

package org.boon.validation.readers;

import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;
import org.boon.validation.ValidatorMetaData;
import org.boon.validation.ValidatorMetaDataReader;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * <b>AnnotationValidatorMetaDataReader</b> reads validation meta-data from
 * annotation.
 * </p>
 * <p/>
 * <p>
 * This class reads a annotation as follows: You pass in the base package of the
 * annotation it defaults to "org.boon.annotation.validation". It then takes
 * the <code>name</code> of the <code>ValidatorMetaData</code> and
 * capitalizes the first letter. Thus if you pass the package
 * "com.my.company.annotation", and
 * <code>ValidatorMetaData.name = "required"</code>, then it will look for an
 * annotation called com.my.company.annotation.Required. The idea behind this is
 * that you can use annotation without polluting your model classes with Boon
 * annotation.
 * </p>
 * <p/>
 * <p>
 * The parent class that owns the annotation should have annotation as follows:
 * <p/>
 * <pre>
 *   @Required @Length (min=10, max=100)
 *   public String getFirstName(){...
 *
 *   @Required @Range (min=10, max=100)
 *   public void setAge() {...
 * </pre>
 * <p/>
 * The <b>firstName</b> corresponds to a property of the Foo class. The
 * <b>firstName</b> is associated with the validation rules <b>required</b>
 * and <b>length</b>. The <b>length</b> validation rule states the minimum and
 * maximum allowed number of characters with the <b>min</b> and <b>max</b>
 * parameters.
 * </p>
 * <p>
 * This was take from the crank project and the crank version was also written by
 * Rick Hightower.
 * </p>
 * <p/>
 * <p>
 * Two different frameworks read this meta-data (currently).
 * </p>
 *
 * @author Rick Hightower
 */
public class AnnotationValidatorMetaDataReader implements ValidatorMetaDataReader, Serializable {

    /**
     * Holds a cache of meta-data to reduce parsing with regex and to avoid
     * reflection.
     * Since this could get hit by multiple threads.
     */
    private Map<String, List<ValidatorMetaData>> metaDataCache =
            new ConcurrentHashMap<>();

    /**
     * Holds a list of packages that contain annotation that we will process.
     * If the annotation package is not in this list, it will not be processed.
     */
    private Set<String> validationAnnotationPackages = new HashSet<>();

    {
            /* By default, we only process our own annotation. */
        validationAnnotationPackages.add( "org.boon.validation.annotation" );
    }

    /**
     * Read the meta-data from annotation. This copies the meta-data
     * from the annotation into a POJO. It first checks the meta-data cache,
     * if the meta data is not found in the cache it then reads it from the
     * class.
     *
     * @param clazz        The class that contains the annotation.
     * @param propertyName The name of the property that we are reading
     *                     the annotation meta-data from.
     */
    public List<ValidatorMetaData> readMetaData( Class<?> clazz, String propertyName ) {

        /* Generate a key to the cache based on the classname and the propertyName. */
        String propertyKey = clazz.getName() + "." + propertyName;

        /* Look up the validation meta data in the cache. */
        List<ValidatorMetaData> validatorMetaDataList = metaDataCache.get( propertyKey );

        /* If the meta-data was not found, then generate it. */
        if ( validatorMetaDataList == null ) { // if not found
            validatorMetaDataList = extractValidatorMetaData( clazz, propertyName, validatorMetaDataList );
            /* Put it in the cache to avoid the processing in the future.
             * Design notes: The processing does a lot of reflection, there
             * is no need to do this each time.
             */
            metaDataCache.put( propertyKey, validatorMetaDataList );
        }

        return validatorMetaDataList;

    }

    /**
     * Extract BaseValidator Meta Data.
     *
     * @param clazz                 class
     * @param propertyName          property name
     * @param validatorMetaDataList validatorMetaDataList
     * @return validator meta data
     */
    private List<ValidatorMetaData> extractValidatorMetaData( Class<?> clazz, String propertyName, List<ValidatorMetaData> validatorMetaDataList ) {
        /* If the meta-data was not found, then generate it. */
        if ( validatorMetaDataList == null ) { // if not found
            /* Read the annotation from the class based on the property name. */
            Collection<AnnotationData> annotations = Annotations.getAnnotationDataForFieldAndProperty( clazz, propertyName, this.validationAnnotationPackages );

            /* Extract the POJO based meta-data from the annotation. */
            validatorMetaDataList =
                    extractMetaDataFromAnnotations( annotations );

        }
        return validatorMetaDataList;
    }

    /**
     * Extract meta-data from the annotationData we collected thus far.
     *
     * @param annotations The annotationData (preprocessed annotation).
     * @return list of validation meta data.
     */
    private List<ValidatorMetaData> extractMetaDataFromAnnotations(
            Collection<AnnotationData> annotations ) {
        List<ValidatorMetaData> list = new ArrayList<ValidatorMetaData>();

        for ( AnnotationData annotationData : annotations ) {
            ValidatorMetaData validatorMetaData = convertAnnotationDataToValidatorMetaData( annotationData );
            list.add( validatorMetaData );
        }

        return list;
    }

    /**
     * Converts an AnnotationData into a ValidatorMetaData POJO.
     *
     * @param annotationData annotationData
     * @return validator meta data
     *         <p/>
     *         TODO
     * @NeedsRefactoring("This method shows we are calling annotationData.getValues a lot. " +
     * "Therefore, we must cache the results of getValues as the annoationData is static " +
     * "per property per class. ")
     */
    private ValidatorMetaData convertAnnotationDataToValidatorMetaData(
            AnnotationData annotationData ) {

        ValidatorMetaData metaData = new ValidatorMetaData();
        metaData.setName( annotationData.getName() );

        metaData.setProperties( annotationData.getValues() );

        return metaData;
    }

    /**
     * We allow a set of validation annotation packages to be configured.
     *
     * @param validationAnnotationPackages validationAnnotationPackages
     */
    public void setValidationAnnotationPackages( Set<String> validationAnnotationPackages ) {
        this.validationAnnotationPackages = validationAnnotationPackages;
    }

}
