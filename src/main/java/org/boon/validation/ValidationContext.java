/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

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


    private ArrayDeque<String> bindingPath = new ArrayDeque<>();


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
    private static ThreadLocal<ValidationContext> holder = new ThreadLocal<>();


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
    public static ValidationContext getCurrentInstance() {
        return holder.get();
    }

    /**
     * Allows the subclass to register an instance of itself
     * as the context. The subclass will either be JSF, Struts 2 (WebWork) or
     * Spring MVC aware.
     *
     * @param context xx
     */
    protected void register( ValidationContext context ) {
        holder.set( context );
    }

    /**
     * Get the parent object. Allows the FieldValidators to access
     * the parent object.
     *
     * @return xx
     */
    public Object getParentObject() {
        return parentObject;
    }

    /**
     * Allows our integration piece for JSF or Spring MVC to set the
     * parent object. The parent object is the form bean in Spring MVC speak.
     *
     * @param parentObject xx
     */
    public void setParentObject( Object parentObject ) {
        this.parentObject = parentObject;
    }

    /**
     * Proivdes a list of parameters that we can access from field validators.
     *
     * @return xx
     */
    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams( Map<String, Object> params ) {
        this.params = params;
    }

    /**
     * Gets the proposed property value.
     * This is the value before it gets applied to the actual bean.
     *
     * @param propertyName xx
     * @return xx
     */
    public Object getProposedPropertyValue( String propertyName ) {
        return null;
    }

    private String calculateBindingPath() {
        StringBuilder builder = new StringBuilder( 255 );
        int index = 0;
        for ( String component : bindingPath ) {
            index++;
            builder.append( component );
            if ( index != bindingPath.size() ) {
                builder.append( '.' );
            }
        }
        return builder.toString();
    }

    public void pop() {
        bindingPath.pop();
    }

    public void pushProperty( final String component ) {
        bindingPath.push( component );
    }

    public void pushObject( final Object object ) {
        String simpleName = object.getClass().getSimpleName();
        simpleName = simpleName.substring( 0, 1 ).toLowerCase() + simpleName.substring( 1, simpleName.length() );
        bindingPath.push( simpleName );
    }

    public static String getBindingPath() {
        if ( getCurrentInstance() != null ) {
            return getCurrentInstance().calculateBindingPath();
        }
        return "";
    }

    public static ValidationContext create() {
        holder.set( new ValidationContext() );
        return get();
    }

    public static ValidationContext get() {
        return holder.get();
    }

    public static void destroy() {
        holder.set( null );
    }

    Map<String, Object> objectRegistry;

    public Map<String, Object> getObjectRegistry() {
        return objectRegistry;
    }

    public void setObjectRegistry( Map<String, Object> objectRegistry ) {
        this.objectRegistry = objectRegistry;
    }

    public String getMessage( String key ) {

        String message = doGetMessageFromBundle( key );

        return message == null ? key : message;

    }


    public String getCurrentSubject() {
        return currentSubject;
    }

    public void setCurrentSubject( String currentSubject ) {
        this.currentSubject = currentSubject;
    }


    public String createMessage( String message, String subject, Object[] actualArgs ) {
        List argumentList = new ArrayList( Arrays.asList( actualArgs ) );

    	/* If the subject is found addObject it as the first
         * argument to the argument list. */
        if ( subject != null ) {
            argumentList.add( 0, this.getMessage( subject ) );

        }
        try {
            /* Attempt to create the message. */
            return MessageFormat.format( message, argumentList.toArray() );
        } catch ( Exception ex ) {

            return message;
        }

    }


    public void setResourceBundleLocator( ResourceBundleLocator resourceBundleLocator ) {
        this.resourceBundleLocator = resourceBundleLocator;
    }


    protected ResourceBundleLocator getResourceBundleLocator() {
        return resourceBundleLocator;
    }


    /**
     * The doGetMessageFromBundle does a bit of magic. If the message starts with {
     * than it assumes it is an i18N message and looks it up in the
     * resource bundle. If it starts with #{ it assumes it is an expression
     * and uses OGNL, JSF EL, or the Universal EL to look up the expression
     * in the context.
     */
    private String doGetMessageFromBundle( String key ) {

        if ( resourceBundleLocator == null ) {
            return null;
        }
        /* Find the resourceBundle. */
        ResourceBundle bundle = this.resourceBundleLocator.getBundle();

        if ( bundle == null ) {
            return null;
        }


        String message = null; //holds the message

    	/* If the message starts with an i18nMarker look it up
         * in the resource bundle.
    	 */
        if ( key.startsWith( this.i18nMarker ) ) {
            try {
                key = key.substring( 1, key.length() - 1 );
                message = lookupMessageInBundle( key, bundle, message );
            } catch ( MissingResourceException mre ) {
                message = key;
            }
        } else {
            /*
             * If it does not start with those markers see if it has a ".". If
			 * it has a dot, try to look it up. If it is not found then just
			 * return the key as the message.
			 */
            if ( key.contains( "." ) ) {
                try {
                    message = lookupMessageInBundle( key, bundle, message );
                } catch ( MissingResourceException mre ) {
                    message = key;
                }
            } else {
                message = key;
            }
        }
        return message;
    }


    private String lookupMessageInBundle( String key, ResourceBundle bundle,
                                          String message ) {
        if ( getCurrentSubject() != null ) {
            try {
                message = bundle.getString( key + "." + getCurrentSubject() );

            } catch ( MissingResourceException mre ) {
                message = bundle.getString( key );
            }
        } else {
            return bundle.getString( key );
        }
        return message;
    }


}
