package org.boon.messages;

import org.boon.validation.ValidationContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Contains information about how to generate a message.
 * This class knows how to create a message.
 * It will look up the message in the resource bundle if it
 * starts with a "{".
 * <p/>
 * Future: It will look up the message in the
 * EL context if it starts with a "#{"
 */
public class MessageSpecification implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * The detailMessage part of the message.
     */
    private String detailMessage = "detailMessage";
    /**
     * The summaryMessage part of the message.
     */
    private String summaryMessage = "summaryMessage";
    /**
     * Arguments that get passed to the detailMessage message.
     */
    private List<String> detailArgs;
    /**
     * Arguments that get passed to the summaryMessage message.
     */
    private List<String> summaryArgs;

    /**
     * The name of this message specification, used to look up information
     * in the resource bundle if needed.
     */
    private String name;
    /**
     * Used to create messages that inherit properties from their parents.
     */
    private String parent;


    /**
     * Who is this message about? For example for field validation
     * the subject is the name of the field.
     */
    private String subject = "";

    private boolean noSummary;

    private static final String SUMMARY_KEY = ".summary";
    private static final String DETAIL_KEY = ".detail";

    /**
     * The init method tries to generate the message keys.
     * You should only call the init method if you don't inject
     * values into the detailMessage and summaryMessage.
     */
    public void init () {
        /* If the parent and name are equal to null,
         * use the classname to load resources.
    	 * */
        if ( name == null && parent == null ) {
            this.setDetailMessage ( "{" + this.getClass ().getName () + DETAIL_KEY + "}" );
            this.setSummaryMessage ( "{" + this.getClass ().getName () + SUMMARY_KEY + "}" );
        /* If the parent is null and the name is not,
         * use the name to load resources.
         */
        } else if ( name != null && parent == null ) {
            this.setDetailMessage ( "{" + "message." + getName () + DETAIL_KEY + "}" );
            this.setSummaryMessage ( "{" + "message." + getName () + SUMMARY_KEY + "}" );
        /* If the parent is present, initialize the message keys
         * with the parent name.
         */
        } else if ( parent != null ) {
            this.setDetailMessage ( "{" + "message." + parent + DETAIL_KEY + "}" );
            this.setSummaryMessage ( "{" + "message." + parent + SUMMARY_KEY + "}" );
        }
    }

    public boolean isNoSummary () {
        return noSummary;
    }

    public void setNoSummary ( boolean noSummary ) {
        this.noSummary = noSummary;
    }

    /**
     * Create the summaryMessage message.
     */
    public String createSummaryMessage ( Object... args ) {
        return createMessage ( summaryMessage, summaryArgs, args );
    }

    /**
     * Create the detailMessage message.
     */
    public String createDetailMessage ( Object... args ) {
        return createMessage ( detailMessage, detailArgs, args );
    }

    /**
     * Creates a message.
     *
     * @param argKeys arguments to lookup that were configured.
     * @param args    Arguments that were passed via the object that wants to
     *                generate the message
     */
    public String createMessage ( String key, List<String> argKeys, Object... args ) {
    	/* Look up the message. */
        String message = getMessage ( key );
    	
    	/* Holds the actual arguments. */
        Object[] actualArgs;
    	
    	/* If they passed arguments, 
    	 * then use this as the actual arguments. */
        if ( args.length > 0 ) {
            actualArgs = args;
    	/* If they did not pass arguments, use the configured ones. */
        } else if ( argKeys != null ) {
    		/* Convert the keys to values. */
            actualArgs = keysToValues ( argKeys );
        } else {
            actualArgs = new Object[]{ };
        }

        return doCreateMessage ( message, actualArgs );

    }

    /**
     * Actually creates the message.
     *
     * @param message    The message that was looked up.
     * @param actualArgs Arguments to the message.
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    private String doCreateMessage ( String message, Object[] actualArgs ) {

        return ValidationContext.get ().createMessage ( message, getSubject (), actualArgs );
    }

    private String getMessage ( String key ) {

        return ValidationContext.get ().getMessage ( key );
    }


    /**
     * Convert the keys to values.
     */
    private Object[] keysToValues ( List<String> argKeys ) {
        List<String> values = new ArrayList<> ();
        for ( String key : argKeys ) {
            values.add ( getMessage ( key ) );
        }
        return values.toArray ();
    }

    /**
     * Allows client objects to set the subject for the current thread
     * per instance of the MessageSpecification.
     */
    public void setCurrentSubject ( String subject ) {
        ValidationContext.get ().setCurrentSubject ( subject );
    }

    /**
     * Gets the current subject or the configured subject if the
     * current subject is not found.
     */
    public String getSubject () {
        return ValidationContext.get ().getCurrentSubject () == null ? this.subject :
                ValidationContext.get ().getCurrentSubject ();
    }


    protected String getDetailMessage () {
        return this.detailMessage;
    }

    public void setDetailMessage ( String detailKey ) {
        this.detailMessage = detailKey;
    }

    protected String getSummaryMessage () {
        return this.summaryMessage;
    }

    public void setSummaryMessage ( String summaryKey ) {
        this.summaryMessage = summaryKey;
    }

    protected List<String> getDetailArgs () {
        return this.detailArgs;
    }

    public void setDetailArgs ( List<String> argKeys ) {
        this.detailArgs = argKeys;
    }

    protected List<String> getSummaryArgs () {
        return this.summaryArgs;
    }

    public void setSummaryArgs ( List<String> summaryArgKeys ) {
        this.summaryArgs = summaryArgKeys;
    }

    public void setName ( String aName ) {
        this.name = aName;
    }

    public String getName () {
        return this.name;
    }


    public void setParent ( String parent ) {
        this.parent = parent;
    }


    public void setSubject ( String subject ) {
        this.subject = subject;
    }

}
