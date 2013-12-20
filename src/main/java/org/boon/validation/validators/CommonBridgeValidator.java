package org.boon.validation.validators;


import org.boon.validation.ValidatorMessage;
import org.boon.validation.ValidatorMessageHolder;

import java.lang.reflect.Method;


/**
 * Invokes commons validator validators.
 * <p/>
 * <p>
 * <small>
 * </small>
 * </p>
 *
 * @author Rick Hightower
 */
public class CommonBridgeValidator extends BaseValidator {

    private Class<?> validatorClass;
    private String methodName = "isValid";
    private String factoryMethod = "getInstance";
    private Object validator;
    private Method validateMethod;

<<<<<<< HEAD
    public ValidatorMessageHolder validate ( Object object, String fieldLabel ) {
=======
    public ValidatorMessageHolder validate( Object object, String fieldLabel ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        ValidatorMessage message = new ValidatorMessage ();
        if ( object == null ) {
            return message;
        }
        boolean valid = false;
        try {
            initValidatorIfNeeded ();
            initValidateMethodIfNeeded ();
<<<<<<< HEAD
            valid = ( Boolean ) validateMethod.invoke ( validator, new Object[]{ ( String ) object } );
=======
            valid = ( Boolean ) validateMethod.invoke ( validator, new Object[]{( String ) object} );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        } catch ( Exception exception ) {
            throw new RuntimeException ( "Fatal exception trying to "
                    + "create validator, probably a missing jar or bad "
                    + "class name in spring context", exception );
        }
        if ( !valid ) {
            populateMessage ( message, fieldLabel );
        }
        return message;
    }

<<<<<<< HEAD
    private void initValidateMethodIfNeeded () throws Exception {
=======
    private void initValidateMethodIfNeeded() throws Exception {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( validateMethod == null ) {
            validateMethod = validatorClass.getMethod ( methodName, new Class[]{ String.class } );
        }
    }

    /**
     * @throws Exception if something goes wrong
     */
<<<<<<< HEAD
    private void initValidatorIfNeeded () throws Exception {
=======
    private void initValidatorIfNeeded() throws Exception {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( validator == null ) {
            if ( factoryMethod == null ) {
                validator = validatorClass.newInstance ();
            } else {
                Method method = validatorClass.getMethod ( factoryMethod );
                validator = method.invoke ( null, ( Object[] ) null );
            }
        }
    }

    public void setMethodName ( String methodName ) {
        this.methodName = methodName;
    }

    public void setValidatorClass ( Class<?> validatorClass ) {
        this.validatorClass = validatorClass;
    }

    public void setFactoryMethod ( String factoryMethod ) {
        this.factoryMethod = factoryMethod;
    }

}
