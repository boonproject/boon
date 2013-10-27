package org.boon.datarepo.modification;

public enum ModificationType {

    BEFORE_INCREMENT,  //update a single property
    AFTER_INCREMENT,  //update a single property

    BEFORE_UPDATE,  //update a single property
    BEFORE_MODIFY,  //modify an entire object, remove and re-add it
    BEFORE_ADD,      //Add a new object
    AFTER_UPDATE,  //update a single property
    AFTER_MODIFY,  //modify an entire object, remove and re-add it
    AFTER_ADD,      //Add a new object

    BEFORE_MODIFY_BY_VALUE_SETTERS,
    AFTER_MODIFY_BY_VALUE_SETTERS,
    BEFORE_UPDATE_BY_VALUE_SETTERS,
    AFTER_UPDATE_BY_VALUE_SETTERS,

}
