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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class allows you to chain validator readers. This way you can read
 * validation data from more than one source.
 * <p/>
 * For example, you could read validation meta data from properties files, and
 * annotation.
 * <p/>
 * The last one configured in the chain wins if you have the same type.
 * <p/>
 * You can have a required validation rule
 * in a properties file and a regex in an annotation for the same property and both
 * applied. Thus, it also merges as long as they are different types.
 *
 * @author Rick Hightower
 */
public class ChainValidatorMetaDataReader implements ValidatorMetaDataReader {

    public static final String OVERRIDE_NAME = "validator.override.name";

    private List<ValidatorMetaDataReader> chain;

    public void setChain( final List<ValidatorMetaDataReader> chain ) {
        this.chain = chain;
    }

    public List<ValidatorMetaData> readMetaData( final Class<?> clazz, final String propertyName ) {
        Map<String, ValidatorMetaData> overrideMap = new LinkedHashMap<String, ValidatorMetaData>();

		/* Iterate through the chain of readers, read the validation data, put the validation data in a 
         * linked hash map based on the name of the validation data. As you addObject another rule with the same name
		 * it overrides the last. Thus, the last reader in the chain has precedence.
		 */
        for ( ValidatorMetaDataReader reader : chain ) {
            /* Read the validation meta-data from the current reader in the chain. */
            List<ValidatorMetaData> list = reader.readMetaData( clazz, propertyName );

			/* Put the validation rules in the linked hash map by name so the last reader can override
             * the previous reader.
			 */
            for ( ValidatorMetaData data : list ) {
                String overrideName = data.getName();
                if ( ( data.getProperties() != null ) && ( data.getProperties().get( OVERRIDE_NAME ) != null ) ) {
                    overrideName = ( String ) data.getProperties().get( OVERRIDE_NAME );
                    data.getProperties().remove( OVERRIDE_NAME );
                }
                overrideMap.put( overrideName, data );
            }
        }
        /* Turn the map into a list. */
        return new ArrayList<ValidatorMetaData>( overrideMap.values() );
    }

}
