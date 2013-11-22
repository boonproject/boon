package org.boon.validation.validators;





import org.boon.core.NameAware;
import org.boon.messages.MessageSpecification;
import org.boon.validation.FieldValidator;
import org.boon.validation.ValidatorMessage;


/**
 *
 * <p>
 * <small>
 * Base class for some validators.
 * </small>
 * </p>
 * @author Rick Hightower
 */
public abstract class BaseValidator extends MessageSpecification implements NameAware, FieldValidator {

    
    public boolean noMessages = false;
    
    public boolean isNoMessages() {
        return noMessages;
    }

    public void setNoMessages(boolean noMessages) {
        this.noMessages = noMessages;
    }

    protected void populateMessage(ValidatorMessage message, String fieldLabel, Object... args) {
    	populateMessage(null, message, fieldLabel, args);
    }

    protected void populateMessage(MessageSpecification ms, ValidatorMessage message, String fieldLabel, Object... args) {
        if (ms==null) {
        	ms = this;
        }
        
        ms.setCurrentSubject(fieldLabel);
        if (!noMessages) {
            message.setSummary(ms.createSummaryMessage(args));
            message.setDetail(ms.createDetailMessage(args));
        }
        ms.setCurrentSubject(null);
        message.setHasError(true);
        
    }
    

}
