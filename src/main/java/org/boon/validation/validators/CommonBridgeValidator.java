package org.boon.validation.validators;



import java.lang.reflect.Method;

import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;


/**
 * Invokes commons validator validators.
 *
 * <p>
 * <small>
 * </small>
 * </p>
 * @author Rick Hightower
 */
public class CommonBridgeValidator extends BaseValidator {

    private Class<?> validatorClass;
    private String methodName="isValid";
    private String factoryMethod="getInstance";
    private Object validator;
    private Method validateMethod;

    public ValidatorMessageHolder validate(Object object, String fieldLabel) {
        ValidatorMessage message = new ValidatorMessage();
        if (object == null) {
        	return message;
        }
        boolean valid = false;
        try {
            initValidatorIfNeeded();
            initValidateMethodIfNeeded();
            valid = (Boolean) validateMethod.invoke(validator, new Object[]{(String) object});

        } catch (Exception exception) {
            throw new RuntimeException("Fatal exception trying to "
                    + "create validator, probably a missing jar or bad "
                    +        "class name in spring context", exception);
        }
        if (!valid) {
            populateMessage(message, fieldLabel);
        }
        return message;
    }

    private void initValidateMethodIfNeeded() throws Exception {
        if (validateMethod == null)  {
            validateMethod = validatorClass.getMethod(methodName, new Class[]{String.class});
        }
    }

    /**
     * 
     * @throws Exception if something goes wrong
     */
    private void initValidatorIfNeeded() throws Exception {
        if (validator == null) {
            if (factoryMethod == null) {
                validator = validatorClass.newInstance();
            } else {
                Method method = validatorClass.getMethod(factoryMethod);
                validator = method.invoke(null, (Object []) null);
            }
        }
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setValidatorClass(Class<?> validatorClass) {
        this.validatorClass = validatorClass;
    }

    public void setFactoryMethod(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

}
