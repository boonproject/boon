package org.boon.validation;

/**
 * The validate method invoked by the phase listener for domain-driven validation
 *
 * @author Paul
 */
public interface Validatable {
    void validate( ) throws ValidationException;
}
