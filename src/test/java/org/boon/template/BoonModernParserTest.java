package org.boon.template;

import org.boon.Str;
import org.boon.template.support.Token;
import org.boon.template.support.TokenTypes;
import org.junit.Test;

import java.util.List;

import static org.boon.Boon.equalsOrDie;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 9/18/14.
 */
public class BoonModernParserTest {

    boolean ok;

    @Test
    public void simpleExpression() {
        BoonModernTemplateParser parser = new BoonModernTemplateParser();

        String text = "{{mom}}";
        //.............01234567
        parser.parse(text);

        final List<Token> tokenList = parser.getTokenList();

        ok = tokenList!=null || die();

        parser.displayTokens(text);

        Token token = tokenList.get(0);

        equalsOrDie("Token starts at 2", 2, token.start());
        equalsOrDie("Token stops at 5", 5, token.stop());
        equalsOrDie("Token is EXPRESSION", TokenTypes.EXPRESSION, token.type());
        equalsOrDie("TEXT is ", "mom", Str.sliceOf(text, token.start(), token.stop()));



    }



    @Test
    public void twoExpressions() {
        BoonModernTemplateParser parser = new BoonModernTemplateParser();

        String text = "{{mom}} {{dad}}";
        //.............01234567
        parser.parse(text);

        final List<Token> tokenList = parser.getTokenList();

        ok = tokenList!=null || die();

        parser.displayTokens(text);

        Token token = tokenList.get(0);

        equalsOrDie("Token starts at 2", 2, token.start());
        equalsOrDie("Token stops at 5", 5, token.stop());
        equalsOrDie("Token is EXPRESSION", TokenTypes.EXPRESSION, token.type());
        equalsOrDie("TEXT is ", "mom", Str.sliceOf(text, token.start(), token.stop()));

        token = tokenList.get(1);

        equalsOrDie("Token starts at 7", 7, token.start());
        equalsOrDie("Token stops at 8", 8, token.stop());
        equalsOrDie("Token is TEXT", TokenTypes.TEXT, token.type());
        equalsOrDie("TEXT is ", " ", Str.sliceOf(text, token.start(), token.stop()));


        token = tokenList.get(2);

        equalsOrDie("Token starts at 10", 10, token.start());
        equalsOrDie("Token stops at 13", 13, token.stop());
        equalsOrDie("Token is EXPRESSION", TokenTypes.EXPRESSION, token.type());
        equalsOrDie("TEXT is ", "dad", Str.sliceOf(text, token.start(), token.stop()));
    }


    @Test
    public void simpleCommand() {
        BoonModernTemplateParser parser = new BoonModernTemplateParser();

        String text = "{{#if test}} {{fine}} {{/if}}";
        //.............01234567
        parser.parse(text);

        final List<Token> tokenList = parser.getTokenList();

        ok = tokenList!=null || die();

        parser.displayTokens(text);

        Token token = tokenList.get(0);

        equalsOrDie("Token starts at 3", 3, token.start());
        equalsOrDie("Token stops at 10", 10, token.stop());
        equalsOrDie("Token is COMMAND", TokenTypes.COMMAND, token.type());
        equalsOrDie("TEXT is ", "if test", Str.sliceOf(text, token.start(), token.stop()));

        token = tokenList.get(1);

        equalsOrDie("Token starts at 12", 12, token.start());
        equalsOrDie("Token stops at 22", 22, token.stop());
        equalsOrDie("Token is COMMAND_BODY", TokenTypes.COMMAND_BODY, token.type());
        String test = Str.add("#",
                Str.sliceOf(text, token.start(), token.stop()), "#");
        equalsOrDie("TEXT is ", "# {{fine}} #", test);


        token = tokenList.get(2);

        equalsOrDie("Token starts at 12", 12, token.start());
        equalsOrDie("Token stops at 13", 13, token.stop());
        equalsOrDie("Token is TEXT", TokenTypes.TEXT, token.type());
        test = Str.add("#",
                Str.sliceOf(text, token.start(), token.stop()), "#");
        equalsOrDie("TEXT is ", "# #", test);



        token = tokenList.get(3);

        equalsOrDie("Token starts at 15", 15, token.start());
        equalsOrDie("Token stops at 19", 19, token.stop());
        equalsOrDie("Token is EXPRESSION", TokenTypes.EXPRESSION, token.type());
        test = Str.add("#",
                Str.sliceOf(text, token.start(), token.stop()), "#");
        equalsOrDie("TEXT is ", "#fine#", test);


        token = tokenList.get(4);

        equalsOrDie("Token starts at 21", 21, token.start());
        equalsOrDie("Token stops at 22", 22, token.stop());
        equalsOrDie("Token is TEXT", TokenTypes.TEXT, token.type());
        test = Str.add("#",
                Str.sliceOf(text, token.start(), token.stop()), "#");
        equalsOrDie("TEXT is ", "# #", test);

    }

    @Test
    public void simpleCommandNoSpace() {
        BoonModernTemplateParser parser = new BoonModernTemplateParser();

        String text = "{{#if test}} {{fine}} {{/if}}";
        //.............01234567
        parser.parse(text);

        final List<Token> tokenList = parser.getTokenList();

        ok = tokenList!=null || die();

        parser.displayTokens(text);

        Token token = tokenList.get(0);

        equalsOrDie("Token starts at 3", 3, token.start());
        equalsOrDie("Token stops at 10", 10, token.stop());
        equalsOrDie("Token is COMMAND", TokenTypes.COMMAND, token.type());
        equalsOrDie("TEXT is ", "if test", Str.sliceOf(text, token.start(), token.stop()));
    }


}

