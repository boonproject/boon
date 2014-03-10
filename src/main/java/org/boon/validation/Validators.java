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

import org.boon.Lists;
import org.boon.validation.validators.*;

public class Validators {


    public static RequiredValidator required( String detailMessage, String summaryMessage ) {
        RequiredValidator validator = new RequiredValidator();
        init( detailMessage, summaryMessage, validator );
        return validator;
    }

    public static RequiredValidator required( String detailMessage ) {
        RequiredValidator validator = new RequiredValidator();
        init( detailMessage, validator );
        return validator;
    }

    private static void init( String detailMessage, BaseValidator validator ) {
        validator.setDetailMessage( detailMessage );
        validator.setNoSummary( true );
        validator.init();
    }

    private static void init( String detailMessage, String summaryMessage, BaseValidator validator ) {
        validator.setDetailMessage( detailMessage );
        validator.setSummaryMessage( summaryMessage );
        validator.init();
    }


    public static LengthValidator length( int min, int max, String message ) {
        LengthValidator v = new LengthValidator();
        v.setMax( max );
        v.setMin( min );
        init( message, v );
        return v;
    }


    public static LengthValidator length( String message, String summary ) {
        LengthValidator eq = new LengthValidator();
        init( message, summary, eq );
        return eq;
    }


    public static CompositeValidator validators( FieldValidator... validators ) {
        CompositeValidator compositeValidator = new CompositeValidator();
        compositeValidator.setValidatorList( Lists.list( validators ) );
        return compositeValidator;

    }


    public static RegexValidator match( String match, String message, String summary ) {
        RegexValidator v = new RegexValidator();
        v.setMatch( match );
        init( message, summary, v );
        return v;
    }


    public static RegexValidator dontMatch( String match, String message, String summary ) {
        RegexValidator v = new RegexValidator();
        v.setMatch( match );
        v.setNegate( true );
        init( message, summary, v );
        return v;
    }

    public static RegexValidator noIdenticalThreeDigits( String message, String summary ) {
        return dontMatch( "000|111|222|333|444|555|666|777|888|999", message, summary );
    }

    public static RegexValidator mustBeNumeric( String message, String summary ) {
        return match( "^[0-9]*$", message, summary );
    }


    public static RegexValidator mustBeThreeDigitNumeric( String message, String summary ) {
        return match( "^[0-9]{3}$", message, summary );
    }

    public static RegexValidator mustBeFourDigitNumeric( String message, String summary ) {
        return match( "^[0-9]{4}$", message, summary );
    }

    public static RegexValidator personName( String message, String summary ) {
        return match( "^([a-zA-Z]|[ -])*$", message, summary );
    }

    public static RegexValidator properNoun( String message, String summary ) {
        return match( "^([a-zA-Z]|[ -])*$", message, summary );
    }

    public static RegexValidator address( String message, String summary ) {
        return match( "^(\\d+ \\w+)|(\\w+ \\d+)$", message, summary );
    }


}
