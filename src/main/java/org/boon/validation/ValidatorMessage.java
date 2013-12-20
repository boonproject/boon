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


    public ValidatorMessage( String summary, String detail ) {
        this.summary = summary;
        this.detail = detail;
        hasError = true;
    }

    public ValidatorMessage( String message ) {
        this.summary = message;
        this.detail = message;
        hasError = true;
    }

    public ValidatorMessage() {
        this.summary = "Message not setup!";
        this.detail = "Message not setup!";
        hasError = false;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail( String detail ) {
        this.detail = detail;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary( String summary ) {
        this.summary = summary;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public void setHasError( boolean aHasError ) {
        this.hasError = aHasError;
    }


    @Override
    public String toString() {
        return "ValidatorMessage{" +
                "detail='" + detail + '\'' +
                ", summary='" + summary + '\'' +
                ", hasError=" + hasError +
                '}';
    }
}
