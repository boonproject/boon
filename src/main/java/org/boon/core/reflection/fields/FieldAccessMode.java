package org.boon.core.reflection.fields;


public enum FieldAccessMode {
    PROPERTY,
    FIELD,
    FIELD_THEN_PROPERTY,
    PROPERTY_THEN_FIELD;


    public FieldsAccessor create ( boolean useAlias ) {
        return FieldAccessMode.create (this,  useAlias);
    }

    public static FieldsAccessor create(FieldAccessMode fieldAccessType, boolean useAlias) {
        FieldsAccessor fieldsAccessor = null;

        switch ( fieldAccessType )  {
            case FIELD:
                fieldsAccessor = new FieldFieldsAccessor( useAlias );
                break;
            case PROPERTY:
                fieldsAccessor = new PropertyFieldAccessor ( useAlias );
                break;
            case FIELD_THEN_PROPERTY:
                fieldsAccessor = new FieldsAccessorFieldThenProp( useAlias );
                break;
            case PROPERTY_THEN_FIELD:
                fieldsAccessor = new FieldsAccessorsPropertyThenField( useAlias  );
                break;
            default:
                fieldsAccessor = new FieldFieldsAccessor( useAlias );

        }

        return fieldsAccessor;


    }
}
