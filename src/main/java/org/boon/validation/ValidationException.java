package org.boon.validation;

public class ValidationException extends Exception {
    private String field;

    public ValidationException( String message, String field ) {
        super( message );
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField( String field ) {
        this.field = field;
    }

}