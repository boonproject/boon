package org.boon.validation;

public class ValidationException extends Exception {
    private String field;

    public ValidationException ( String message, String field ) {
        super ( message );
        this.field = field;
    }

<<<<<<< HEAD
    public String getField () {
=======
    public String getField() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return field;
    }

    public void setField ( String field ) {
        this.field = field;
    }

}