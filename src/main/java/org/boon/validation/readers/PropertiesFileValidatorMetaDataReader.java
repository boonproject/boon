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

import org.boon.validation.ValidatorMetaData;
import org.boon.validation.ValidatorMetaDataReader;

import java.io.IOException;
import java.util.*;


/**
 * <p>
 * <b>PropertiesFileValidatorMetaDataReader</b> reads validation meta-data from
 * a properties files.
 * </p>
 * <p/>
 * <p>
 * This class reads a properties file as follows: <br />
 * <br />
 * If the class name is com.foo.Foo, then the resource name is
 * com.foo.Foo.properties.
 * </p>
 * <p/>
 * <p>
 * The properties file will contain validation meta-data as follows: <br />
 * <br />
 * <p/>
 * <pre>
 *  firstName=required; length min=10, max=100
 *  age=required; range min=10, max=100
 * </pre>
 * <p/>
 * The <b>firstName</b> corresponds to a property of the Foo class. The
 * <b>firstName</b> is associated with the validation rules <b>required</b>
 * and <b>length</b>. The <b>length</b> validation rule states the minimum and
 * maximum allowed number of characters with the <b>min</b> and <b>max</b>
 * parameters.
 * </p>
 * <p/>
 * <p>
 * Two different frameworks read this meta-data (curently). Our validation
 * framework, which is mostly geared towards server-side validation and our
 * client-side JavaScript framework, which is geared towards producing
 * client-side JavaScript.
 * </p>
 *
 * @author Rick Hightower
 */
public class PropertiesFileValidatorMetaDataReader implements ValidatorMetaDataReader {

    /**
     * Holds a cache of Properties file contents to reduce IO.
     */
    private Map<String, Properties> metaDataPropsCache =
            new HashMap<String, Properties>();

    /**
     * Holds a cache of meta-data to reduce parsing with regex.
     */
    private Map<String, List<ValidatorMetaData>> metaDataCache =
            new HashMap<String, List<ValidatorMetaData>>();

    /**
     * Read the meta-data from a properties file.
     */
    public List<ValidatorMetaData> readMetaData( Class<?> clazz, String propertyName ) {

        /* Load the properties file. */
        Properties props = loadMetaDataPropsFile( clazz );
        /* Get the raw validation data for the given property. */
        String unparsedString = props.getProperty( propertyName );
        /* Parse the string into a list of ValidationMetaData. */
        return extractMetaDataFromString( clazz, propertyName, unparsedString );
    }

    /**
     * This method loads the MetaData properties file. The Properties are cached
     * in <b>metaDataPropsCache</b> and will not be reloaded twice.
     *
     * @param clazzWhoseValidationMetaDataWeAreReading
     *         The class whose property meta-data we are retrieving.
     * @return
     */
    private Properties loadMetaDataPropsFile(
            Class<?> clazzWhoseValidationMetaDataWeAreReading ) {
        String className = clazzWhoseValidationMetaDataWeAreReading.getName();

        /*
         * If the class is proxied there will be a $CGLIB on the end of it.
         * Remove this.
         */
        className = className.split( "[$]" )[ 0 ];
        
        /*
         * The resourceName is as follows: If the class name is com.foo.Foo Then
         * the resource name is com.foo.Foo.properties.
         */
        String[] sourceParts = className.split( "[.]" );
        String resourceName = ( sourceParts[ sourceParts.length - 1 ] ) + ".properties";

        /* Check to see if this properties file was already loaded. */
        Properties validationMetaDataProps = metaDataPropsCache.get( resourceName );

        /* If the properties file was not loaded, then load it. */
        if ( validationMetaDataProps == null ) {
            validationMetaDataProps = new Properties();
            try {
                /*
                 * Try to load the properties file that contains the validation
                 * meta-data.
                 */
                validationMetaDataProps.load( this.getClass()
                        .getClassLoader()
                        .getResourceAsStream( resourceName ) );
            } catch ( IOException ioex ) {
                /*
                 * This can happen and is not an error. It just means there is
                 * no validation for this guy. Maybe we should log this.
                 * Note self... addObject logging capability to this project!.
                 */
            }
            /*
             * Put the properties file into the cache so we don't have to read
             * it again.
             */
            metaDataPropsCache.put( resourceName, validationMetaDataProps );
        }
        assert validationMetaDataProps != null :
                "Properties for validation meta-data were loaded";
        return validationMetaDataProps;
    }

    /**
     * This method extracts meta-data from a string.
     *
     * @param clazz
     * @param propertyName
     * @param unparsedString
     * @return
     */
    private List<ValidatorMetaData> extractMetaDataFromString( Class<?> clazz,
                                                               String propertyName, String unparsedString ) {
        String propertyKey = clazz.getName() + "." + propertyName;

        /* See if we parsed this bad boy already. */
        List<ValidatorMetaData> validatorMetaDataList =
                metaDataCache.get( propertyKey );

        
        /* If we did not find the list, then we have some work to do.*/
        if ( validatorMetaDataList == null ) {
            /* Initialize a new list. */
            validatorMetaDataList = new ArrayList<ValidatorMetaData>();
            
            /* Remember we have a string that looks like this:
             * required; length min=10, max=100
             * So we need to split on semi-colon.
             */
            String[] validatorsParts = unparsedString.split( "[;]" );
            
            /* Now we have the two strings as follows:
             *  ["required",
             *  ["length min=10, max=100"]
             *
             */
            for ( String validatorString : validatorsParts ) {
                ValidatorMetaData validatorMetaData = new ValidatorMetaData();
                validatorMetaDataList.add( validatorMetaData );
                
                /* Now we split one of the string (we will use length) 
                 * as follows: 
                 * parts=["length", "min=10", "max=100"]
                 * */
                String[] parts = validatorString.trim().split( "[ ,]" );
                
                /* The first part is the name of the validation, 
                 * e.g., "length".
                 * 
                 */
                validatorMetaData.setName( parts[ 0 ] );

                /* If the string has more than one part, then there must
                 * be arguments as in: ["min=10", "max=100"]
                 * 
                 * Parse the arguments and addObject them to the list as well.
                 */
                if ( parts.length > 1 ) {

                    /* This line converts:
                     * 
                     * ["length", "min=10", "max=100"]
                     * 
                     * into: 
                     * 
                     * ["min=10", "max=100"]
                     */
                    List<String> values =
                            Arrays.asList( parts ).subList( 1, parts.length );
                    
                    /* For each value convert it into name value pairs. */
                    for ( String value : values ) {

                        if ( value.indexOf( "=" ) != -1 ) {
                            /* Split "min=10" into ["min", "10"] */
                            String[] valueParts = value.split( "[=]" );
                            /* Stick this value into validatorMetaData's
                             * list of properties. 
                             */
                            validatorMetaData.getProperties().put(
                                    valueParts[ 0 ], valueParts[ 1 ] );
                        }
                    }
                }
            }
            metaDataCache.put( propertyKey, validatorMetaDataList );
        }
        return validatorMetaDataList;
    }

}
