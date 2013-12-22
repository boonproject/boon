/**
 *
 */
package org.boon.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the validator name
 * and a list of name value pairs for the validator.
 */
public class ValidatorMetaData {
    private String name = null;

    private Map<String, Object> properties = new HashMap<>();


    public static ValidatorMetaData validatorMeta ( String name, Map<String, Object> properties ) {
        return new ValidatorMetaData( name, properties );
    }

    public ValidatorMetaData () {
    }

    public ValidatorMetaData ( String name, Map<String, Object> properties ) {
        this.name = name;
        this.properties = properties;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    public void setProperties ( Map<String, Object> properties ) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties () {
        return properties;
    }
}