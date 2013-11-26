package org.boon.validation;

import org.boon.validation.RecursiveDescentPropertyValidator.MessageHolder;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.boon.Maps.map;
import org.boon.validation.readers.PropertiesFileValidatorMetaDataReader;
import org.boon.validation.validators.LengthValidator;
import org.boon.validation.validators.LongRangeValidator;
import org.boon.validation.validators.RequiredValidator;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author Selwyn Lehmann
 */
public class PropertiesFileValidationTest {

    @Before
    public void setup() {
        ValidationContext.create();
    }

    @After
    public void cleanup() {
        ValidationContext.destroy();
    }
    
    
    @Test
    public void testRecursiveWithPropertyFile() {
        
        Map<String, Object> objectMap = map(
            "/org/boon/validator/required", (Object) new RequiredValidator(),
            "/org/boon/validator/range", (Object) new LongRangeValidator(),
            "/org/boon/validator/length" , (Object) new LengthValidator(),
            "/org/boon/validator/personName" , Validators.personName("", "")
        );
        
        RecursiveDescentPropertyValidator validator = new RecursiveDescentPropertyValidator();
        validator.setValidatorMetaDataReader(new PropertiesFileValidatorMetaDataReader());
        
        List<MessageHolder> messageHolders = Collections.EMPTY_LIST;
        messageHolders  = validator.validateObject(new Employee("Selwyn", 21, "555-555-5555"), objectMap);


        int errors = 0;

        for (MessageHolder messageHolder : messageHolders) {
            puts(messageHolder.propertyPath);
            puts(messageHolder.holder.hasError());

            if (messageHolder.holder.hasError()) {
                errors ++;
            }
        }

        if ( errors > 0 ) {
            die (" Not expecting any errors ");
        }
    }
}
