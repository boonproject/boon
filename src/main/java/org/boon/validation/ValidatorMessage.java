package org.boon.validation;

import java.io.Serializable;


/**
 * <p>
 * <small>
 * BaseValidator MessageSpecification.
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public class ValidatorMessage implements Serializable, ValidatorMessageHolder {
    private String detail;
    private String summary;
    private boolean hasError = false;


    public ValidatorMessage ( String summary, String detail ) {
        this.summary = summary;
        this.detail = detail;
        hasError = true;
    }

    public ValidatorMessage ( String message ) {
        this.summary = message;
        this.detail = message;
        hasError = true;
    }

<<<<<<< HEAD
    public ValidatorMessage () {
=======
    public ValidatorMessage() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.summary = "Message not setup!";
        this.detail = "Message not setup!";
        hasError = false;
    }

<<<<<<< HEAD
    public String getDetail () {
=======
    public String getDetail() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.detail;
    }

    public void setDetail ( String detail ) {
        this.detail = detail;
    }

<<<<<<< HEAD
    public String getSummary () {
=======
    public String getSummary() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.summary;
    }

    public void setSummary ( String summary ) {
        this.summary = summary;
    }

<<<<<<< HEAD
    public boolean hasError () {
=======
    public boolean hasError() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.hasError;
    }

    public void setHasError ( boolean aHasError ) {
        this.hasError = aHasError;
    }


    @Override
<<<<<<< HEAD
    public String toString () {
=======
    public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return "ValidatorMessage{" +
                "detail='" + detail + '\'' +
                ", summary='" + summary + '\'' +
                ", hasError=" + hasError +
                '}';
    }
}
