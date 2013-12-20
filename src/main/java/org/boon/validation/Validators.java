package org.boon.validation;

import org.boon.Lists;
import org.boon.validation.validators.*;

public class Validators {


<<<<<<< HEAD
    public static RequiredValidator required ( String detailMessage, String summaryMessage ) {
=======
    public static RequiredValidator required( String detailMessage, String summaryMessage ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        RequiredValidator validator = new RequiredValidator ();
        init ( detailMessage, summaryMessage, validator );
        return validator;
    }

<<<<<<< HEAD
    public static RequiredValidator required ( String detailMessage ) {
=======
    public static RequiredValidator required( String detailMessage ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        RequiredValidator validator = new RequiredValidator ();
        init ( detailMessage, validator );
        return validator;
    }

    private static void init ( String detailMessage, BaseValidator validator ) {
        validator.setDetailMessage ( detailMessage );
        validator.setNoSummary ( true );
        validator.init ();
    }

    private static void init ( String detailMessage, String summaryMessage, BaseValidator validator ) {
        validator.setDetailMessage ( detailMessage );
        validator.setSummaryMessage ( summaryMessage );
        validator.init ();
    }


<<<<<<< HEAD
    public static LengthValidator length ( int min, int max, String message ) {
=======
    public static LengthValidator length( int min, int max, String message ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        LengthValidator v = new LengthValidator ();
        v.setMax ( max );
        v.setMin ( min );
        init ( message, v );
        return v;
    }


<<<<<<< HEAD
    public static LengthValidator length ( String message, String summary ) {
=======
    public static LengthValidator length( String message, String summary ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        LengthValidator eq = new LengthValidator ();
        init ( message, summary, eq );
        return eq;
    }


<<<<<<< HEAD
    public static CompositeValidator validators ( FieldValidator... validators ) {
=======
    public static CompositeValidator validators( FieldValidator... validators ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        CompositeValidator compositeValidator = new CompositeValidator ();
        compositeValidator.setValidatorList ( Lists.list ( validators ) );
        return compositeValidator;

    }


<<<<<<< HEAD
    public static RegexValidator match ( String match, String message, String summary ) {
=======
    public static RegexValidator match( String match, String message, String summary ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        RegexValidator v = new RegexValidator ();
        v.setMatch ( match );
        init ( message, summary, v );
        return v;
    }


<<<<<<< HEAD
    public static RegexValidator dontMatch ( String match, String message, String summary ) {
=======
    public static RegexValidator dontMatch( String match, String message, String summary ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        RegexValidator v = new RegexValidator ();
        v.setMatch ( match );
        v.setNegate ( true );
        init ( message, summary, v );
        return v;
    }

    public static RegexValidator noIdenticalThreeDigits ( String message, String summary ) {
        return dontMatch ( "000|111|222|333|444|555|666|777|888|999", message, summary );
    }

    public static RegexValidator mustBeNumeric ( String message, String summary ) {
        return match ( "^[0-9]*$", message, summary );
    }


    public static RegexValidator mustBeThreeDigitNumeric ( String message, String summary ) {
        return match ( "^[0-9]{3}$", message, summary );
    }

    public static RegexValidator mustBeFourDigitNumeric ( String message, String summary ) {
        return match ( "^[0-9]{4}$", message, summary );
    }

    public static RegexValidator personName ( String message, String summary ) {
        return match ( "^([a-zA-Z]|[ -])*$", message, summary );
    }

    public static RegexValidator properNoun ( String message, String summary ) {
        return match ( "^([a-zA-Z]|[ -])*$", message, summary );
    }

    public static RegexValidator address ( String message, String summary ) {
        return match ( "^(\\d+ \\w+)|(\\w+ \\d+)$", message, summary );
    }


}
