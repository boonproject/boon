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

    private Map<String, Object> properties = new HashMap<> ();


    public static ValidatorMetaData validatorMeta ( String name, Map<String, Object> properties ) {
        return new ValidatorMetaData ( name, properties );
    }

<<<<<<< HEAD
    public ValidatorMetaData () {
=======
    public ValidatorMetaData() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
    }

    public ValidatorMetaData ( String name, Map<String, Object> properties ) {
        this.name = name;
        this.properties = properties;
    }

    public void setName ( String name ) {
        this.name = name;
    }

<<<<<<< HEAD
    public String getName () {
=======
    public String getName() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return name;
    }

    public void setProperties ( Map<String, Object> properties ) {
        this.properties = properties;
    }

<<<<<<< HEAD
    public Map<String, Object> getProperties () {
=======
    public Map<String, Object> getProperties() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return properties;
    }
}