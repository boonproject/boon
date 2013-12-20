package org.boon.validation;

import org.boon.messages.ResourceBundleLocator;

import java.text.MessageFormat;
import java.util.*;

/**
 * This is the validator context.
 * It holds thread local state that is important to validation.
 * It is an interface into underlying component and/or MVC/Model 2 architectures
 * to abstract and simplify access to the context of things we need to perform validation.
 *
 * @author Rick Hightower
 */
public class ValidationContext {


    private ArrayDeque<String> bindingPath = new ArrayDeque<> ();


    /**
     * Used to find the resource bundle. This can vary based on the
     * web framework you are using, and whether you want to use Spring
     * Message sources or not.
     */
    private ResourceBundleLocator resourceBundleLocator;


    /**
     * Holds the parent object of the field. A parent object is an
     * object that contains fields.
     */
    private Object parentObject;

    /**
     * Context for validaiton rule variables.
     */
    private Map<String, Object> params;

    /**
     * Holds the data(context) for the current thread.
     */
    private static ThreadLocal<ValidationContext> holder = new ThreadLocal<> ();


    /**
     * Used to determine if an argument or message refers to an item that
     * should be looked up in the resourceBundle.
     */
    private String i18nMarker = "{";


    private String currentSubject;

    /**
     * Provides access to the ValidationContext.
     *
     * @return xx
     */
<<<<<<< HEAD
    public static ValidationContext getCurrentInstance () {
=======
    public static ValidationContext getCurrentInstance() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return holder.get ();
    }

    /**
     * Allows the subclass to register an instance of itself
     * as the context. The subclass will either be JSF, Struts 2 (WebWork) or
     * Spring MVC aware.
     *
     * @param context xx
     */
    protected void register ( ValidationContext context ) {
        holder.set ( context );
    }

    /**
     * Get the parent object. Allows the FieldValidators to access
     * the parent object.
     *
     * @return xx
     */
<<<<<<< HEAD
    public Object getParentObject () {
=======
    public Object getParentObject() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return parentObject;
    }

    /**
     * Allows our integration piece for JSF or Spring MVC to set the
     * parent object. The parent object is the form bean in Spring MVC speak.
     *
     * @param parentObject xx
     */
    public void setParentObject ( Object parentObject ) {
        this.parentObject = parentObject;
    }

    /**
     * Proivdes a list of parameters that we can access from field validators.
     *
     * @return xx
     */
<<<<<<< HEAD
    public Map<String, Object> getParams () {
=======
    public Map<String, Object> getParams() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return params;
    }

    public void setParams ( Map<String, Object> params ) {
        this.params = params;
    }

    /**
     * Gets the proposed property value.
     * This is the value before it gets applied to the actual bean.
     *
     * @param propertyName xx
     * @return xx
     */
    public Object getProposedPropertyValue ( String propertyName ) {
        return null;
    }

<<<<<<< HEAD
    private String calculateBindingPath () {
=======
    private String calculateBindingPath() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        StringBuilder builder = new StringBuilder ( 255 );
        int index = 0;
        for ( String component : bindingPath ) {
            index++;
            builder.append ( component );
            if ( index != bindingPath.size () ) {
                builder.append ( '.' );
            }
        }
        return builder.toString ();
    }

<<<<<<< HEAD
    public void pop () {
=======
    public void pop() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        bindingPath.pop ();
    }

    public void pushProperty ( final String component ) {
        bindingPath.push ( component );
    }

<<<<<<< HEAD
    public void pushObject ( final Object object ) {
=======
    public void pushObject( final Object object ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        String simpleName = object.getClass ().getSimpleName ();
        simpleName = simpleName.substring ( 0, 1 ).toLowerCase () + simpleName.substring ( 1, simpleName.length () );
        bindingPath.push ( simpleName );
    }

<<<<<<< HEAD
    public static String getBindingPath () {
=======
    public static String getBindingPath() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( getCurrentInstance () != null ) {
            return getCurrentInstance ().calculateBindingPath ();
        }
        return "";
    }

<<<<<<< HEAD
    public static ValidationContext create () {
=======
    public static ValidationContext create() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        holder.set ( new ValidationContext () );
        return get ();
    }

<<<<<<< HEAD
    public static ValidationContext get () {
        return holder.get ();
    }

    public static void destroy () {
=======
    public static ValidationContext get() {
        return holder.get ();
    }

    public static void destroy() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        holder.set ( null );
    }

    Map<String, Object> objectRegistry;

<<<<<<< HEAD
    public Map<String, Object> getObjectRegistry () {
=======
    public Map<String, Object> getObjectRegistry() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return objectRegistry;
    }

    public void setObjectRegistry ( Map<String, Object> objectRegistry ) {
        this.objectRegistry = objectRegistry;
    }

    public String getMessage ( String key ) {

        String message = doGetMessageFromBundle ( key );

        return message == null ? key : message;

    }


<<<<<<< HEAD
    public String getCurrentSubject () {
=======
    public String getCurrentSubject() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return currentSubject;
    }

    public void setCurrentSubject ( String currentSubject ) {
        this.currentSubject = currentSubject;
    }


    public String createMessage ( String message, String subject, Object[] actualArgs ) {
        List argumentList = new ArrayList ( Arrays.asList ( actualArgs ) );

    	/* If the subject is found add it as the first
         * argument to the argument list. */
        if ( subject != null ) {
            argumentList.add ( 0, this.getMessage ( subject ) );

        }
        try {
            /* Attempt to create the message. */
            return MessageFormat.format ( message, argumentList.toArray () );
        } catch ( Exception ex ) {

            return message;
        }

    }


    public void setResourceBundleLocator ( ResourceBundleLocator resourceBundleLocator ) {
        this.resourceBundleLocator = resourceBundleLocator;
    }


<<<<<<< HEAD
    protected ResourceBundleLocator getResourceBundleLocator () {
=======
    protected ResourceBundleLocator getResourceBundleLocator() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return resourceBundleLocator;
    }


    /**
     * The doGetMessageFromBundle does a bit of magic. If the message starts with {
     * than it assumes it is an i18N message and looks it up in the
     * resource bundle. If it starts with #{ it assumes it is an expression
     * and uses OGNL, JSF EL, or the Universal EL to look up the expression
     * in the context.
     */
    private String doGetMessageFromBundle ( String key ) {

        if ( resourceBundleLocator == null ) {
            return null;
        }
        /* Find the resourceBundle. */
        ResourceBundle bundle = this.resourceBundleLocator.getBundle ();

        if ( bundle == null ) {
            return null;
        }


        String message = null; //holds the message

    	/* If the message starts with an i18nMarker look it up
    	 * in the resource bundle.
    	 */
        if ( key.startsWith ( this.i18nMarker ) ) {
            try {
                key = key.substring ( 1, key.length () - 1 );
                message = lookupMessageInBundle ( key, bundle, message );
            } catch ( MissingResourceException mre ) {
                message = key;
            }
        } else {
			/*
			 * If it does not start with those markers see if it has a ".". If
			 * it has a dot, try to look it up. If it is not found then just
			 * return the key as the message.
			 */
            if ( key.contains ( "." ) ) {
                try {
                    message = lookupMessageInBundle ( key, bundle, message );
                } catch ( MissingResourceException mre ) {
                    message = key;
                }
            } else {
                message = key;
            }
        }
        return message;
    }


<<<<<<< HEAD
    private String lookupMessageInBundle ( String key, ResourceBundle bundle,
                                           String message ) {
=======
    private String lookupMessageInBundle( String key, ResourceBundle bundle,
                                          String message ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( getCurrentSubject () != null ) {
            try {
                message = bundle.getString ( key + "." + getCurrentSubject () );

            } catch ( MissingResourceException mre ) {
                message = bundle.getString ( key );
            }
        } else {
            return bundle.getString ( key );
        }
        return message;
    }


}
