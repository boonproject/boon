package org.boon.validation;

import static org.boon.Boon.puts;
import static org.boon.Maps.map;

import org.boon.Lists;
import org.boon.validation.annotations.Length;
import org.boon.validation.annotations.Phone;
import org.boon.validation.annotations.ProperNoun;
import org.boon.validation.validators.CompositeValidator;
import org.boon.validation.validators.LengthValidator;
import org.boon.validation.validators.RequiredValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import javax.naming.Name;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.boon.validation.Validators.*;

import static org.boon.validation.ValidatorMetaData.*;

import static org.boon.Exceptions.die;

public class SimpleValidationTest {


    @Before
    public void setup () {

        ValidationContext.create ();


    }

    @After
    public void cleanup () {

        ValidationContext.destroy ();

    }

    @Test
    public void testRequired () {

        RequiredValidator required = Validators.required ( "phone number required" );


        ValidatorMessage message = ( ValidatorMessage ) required.validate ( null, "Phone Number" );


        boolean ok = true;

        ok |= message.hasError () || die ( "Phone number is required" );


        message = ( ValidatorMessage ) required.validate ( "", "Phone Number" );


        //Empty strings don't count!
        ok |= message.hasError () || die ( "Phone number is required" );


    }


    @Test
    public void testLengthShowsMustBePresentToBeValidated () {

        LengthValidator length = Validators.length ( 7, 12, "phone number must be 7 to  12 characters long" );


        ValidatorMessage message = ( ValidatorMessage ) length.validate ( null, "Phone Number" );


        boolean ok = true;

        ok |= !message.hasError () || die ( "Phone number must be between 7, and 12" );


        message = ( ValidatorMessage ) length.validate ( "", "Phone Number" );


        ok |= message.hasError () || die ( "Phone number must be between 7 and 12" );


    }


    @Test
    public void testComposite () {


        CompositeValidator validators = Validators.validators ( required ( "phone number required" ),
                length ( 7, 12, "phone number must be 7 to  12 characters long" ) );


        ValidatorMessages messages = ( ValidatorMessages ) validators.validate ( null, "Phone Number" );


        boolean ok = true;

        ok |= messages.hasError () || die ( "required" );


        messages = ( ValidatorMessages ) validators.validate ( "123", "Phone Number" );

        ok |= messages.hasError () || die ( "wrong length" );


        messages = ( ValidatorMessages ) validators.validate ( "1231234567", "Phone Number" );

        ok |= !messages.hasError () || die ( "all good now" );


    }


    public static class Employee {
        String firstName;
        int age;
        String phone;

        public Employee ( String name, int age, String phone ) {
            this.firstName = name;
            this.age = age;
            this.phone = phone;
        }

        public String getFirstName () {
            return firstName;
        }

        public void setFirstName ( String firstName ) {
            this.firstName = firstName;
        }

        public int getAge () {
            return age;
        }

        public void setAge ( int age ) {
            this.age = age;
        }

        public String getPhone () {
            return phone;
        }

        public void setPhone ( String phone ) {
            this.phone = phone;
        }
    }


    Map<String, List<ValidatorMetaData>> rules = map (
            "phone", Lists.list (
            validatorMeta ( "length",
                    map ( "max", ( Object ) 12, "min", 5 )
            )
    ),

            "firstName", Lists.list (
            validatorMeta ( "personName", Collections.EMPTY_MAP )
    )
    );

    Map<Class<Employee>, Map<String, List<ValidatorMetaData>>> classToRulesMap =
            map ( Employee.class, rules );


    class ValidatorReader implements ValidatorMetaDataReader {


        @Override
        public List<ValidatorMetaData> readMetaData ( Class<?> clazz, String propertyName ) {
            if ( classToRulesMap.get ( clazz ).get ( propertyName ) == null ) {
                return Collections.EMPTY_LIST;
            } else {
                return classToRulesMap.get ( clazz ).get ( propertyName );
            }

        }
    }


    @Test
    public void testRecursive () {


        Map<String, Object> objectMap = map (
                "/org/boon/validator/length", ( Object ) new LengthValidator (),
                "/org/boon/validator/personName", Validators.personName ( "", "" )
        );


        RecursiveDescentPropertyValidator validator = new RecursiveDescentPropertyValidator ();

        validator.setValidatorMetaDataReader ( new ValidatorReader () );

        List<RecursiveDescentPropertyValidator.MessageHolder> messageHolders = Collections.EMPTY_LIST;


        messageHolders = validator.validateObject ( new Employee ( "Rick", 43, "555-121-3333" ), objectMap );


        int errors = 0;

        for ( RecursiveDescentPropertyValidator.MessageHolder messageHolder : messageHolders ) {

            puts ( messageHolder.propertyPath );

            puts ( messageHolder.holder.hasError () );


            if ( messageHolder.holder.hasError () ) {
                errors++;
            }

        }

        if ( errors > 0 ) {
            die ( " Not expecting any errors " );
        }


        messageHolders = validator.validateObject ( new Employee ( "123", 50, "A" ), objectMap );

        errors = 0;

        for ( RecursiveDescentPropertyValidator.MessageHolder messageHolder : messageHolders ) {

            puts ( messageHolder.propertyPath );

            puts ( messageHolder.holder.hasError () );


            if ( messageHolder.holder.hasError () ) {
                errors++;
            }

        }

        if ( errors != 2 ) {
            die ( " expecting two errors " + errors );
        }

    }


    public static class Employee2 {


        @ProperNoun (detailMessage = "First Name must be a proper noun")
        String firstName;
        int age;


        @Length (max = 12, min = 5, detailMessage = "Phone number must be a phone number")
        String phone;

        public Employee2 ( String name, int age, String phone ) {
            this.firstName = name;
            this.age = age;
            this.phone = phone;
        }

        public String getFirstName () {
            return firstName;
        }

        public void setFirstName ( String firstName ) {
            this.firstName = firstName;
        }

        public int getAge () {
            return age;
        }

        public void setAge ( int age ) {
            this.age = age;
        }

        public String getPhone () {
            return phone;
        }

        public void setPhone ( String phone ) {
            this.phone = phone;
        }
    }


    @Test
    public void testRecursiveWithAnnotations () {


        Map<String, Object> objectMap = map (
                "/org/boon/validator/length", ( Object ) new LengthValidator (),
                "/org/boon/validator/properNoun", Validators.properNoun ( "", "" )
        );


        RecursiveDescentPropertyValidator validator = new RecursiveDescentPropertyValidator ();


        List<RecursiveDescentPropertyValidator.MessageHolder> messageHolders = Collections.EMPTY_LIST;


        messageHolders = validator.validateObject ( new Employee2 ( "Rick", 43, "555-121-3333" ), objectMap );


        int errors = 0;

        for ( RecursiveDescentPropertyValidator.MessageHolder messageHolder : messageHolders ) {

            puts ( messageHolder.propertyPath );

            puts ( messageHolder.holder.hasError () );


            if ( messageHolder.holder.hasError () ) {
                errors++;
            }

        }

        if ( errors > 0 ) {
            die ( " Not expecting any errors " );
        }


        messageHolders = validator.validateObject ( new Employee2 ( "123", 50, "A" ), objectMap );

        errors = 0;

        for ( RecursiveDescentPropertyValidator.MessageHolder messageHolder : messageHolders ) {

            puts ( messageHolder.propertyPath );

            puts ( messageHolder.holder.hasError () );


            if ( messageHolder.holder.hasError () ) {
                errors++;
            }

        }

        if ( errors != 2 ) {
            die ( " expecting two errors " + errors );
        }

    }
}
