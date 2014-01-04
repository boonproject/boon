package org.boon.core.reflection.fields;


public enum FieldAccessMode {
    PROPERTY,
    FIELD,
    FIELD_THEN_PROPERTY,
    PROPERTY_THEN_FIELD;


    public static FieldsAccessor create(FieldAccessMode fieldAccessType) {
        FieldsAccessor fieldsAccessor = null;

        switch ( fieldAccessType )  {
            case FIELD:
                fieldsAccessor = new FieldFieldsAccessor();
                break;
            case PROPERTY:
                fieldsAccessor = new PropertyFieldAccesstor();
                break;
            case FIELD_THEN_PROPERTY:
                fieldsAccessor = new FieldsAccessorFieldThenProp();
                break;
            case PROPERTY_THEN_FIELD:
                fieldsAccessor = new FieldsAccessorsPropertyThenField();
                break;
            default:
                fieldsAccessor = new FieldFieldsAccessor();

        }

        return fieldsAccessor;


    }
}
