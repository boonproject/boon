package org.boon.template;

import junit.framework.TestCase;
import org.boon.Str;
import org.junit.Test;

import java.util.List;

import static org.boon.Boon.equalsOrDie;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 9/14/14.
 */
public class JSTLCoreParserTest extends TestCase {

    boolean ok;

    @Test
    public void testSimple() {
        JSTLCoreParser parser = new JSTLCoreParser();
        String text = "  blah   <c:if test='foo'>  body </c:if> more text";
                   //  012345678901234567890123456789012345678901234567890
                   //  0         10        20        30        40        50
        parser.parse(text);

        final List<JSTLCoreParser.Token> tokenList = parser.getTokenList();

        JSTLCoreParser.Token token = tokenList.get(0);

        equalsOrDie("Token starts at 0", 0, token.start);
        equalsOrDie("Token stops at 9", 9, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  blah   ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(1);

        equalsOrDie("Token starts at 12", 12, token.start);
        equalsOrDie("Token stops at 25", 25, token.stop);
        equalsOrDie("Token is COMMAND", JSTLCoreParser.TokenTypes.COMMAND, token.type);
        equalsOrDie("TEXT is ", "if test='foo'", Str.sliceOf(text, token.start, token.stop));

        token = tokenList.get(2);

        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 33", 33, token.stop);
        equalsOrDie("Token is COMMAND_BODY", JSTLCoreParser.TokenTypes.COMMAND_BODY, token.type);
        equalsOrDie("TEXT is ", "  body ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(3);

        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 33", 33, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  body ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(4);

        equalsOrDie("Token starts at 40", 40, token.start);
        equalsOrDie("Token stops at 50", 50, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", " more text", Str.sliceOf(text, token.start, token.stop));

    }



    @Test
    public void testNested() {
        JSTLCoreParser parser = new JSTLCoreParser();
        String text = "  blah   <c:if test='foo'>  body  <c:if test='bar'> body2 </c:if> body3 </c:if>more text";
                   //  01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                   //  0         10        20        30        40        50        60        70        80        90
        parser.parse(text);

        final List<JSTLCoreParser.Token> tokenList = parser.getTokenList();

        JSTLCoreParser.Token token = tokenList.get(0);

        equalsOrDie("Token starts at 0", 0, token.start);
        equalsOrDie("Token stops at 9", 9, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  blah   ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(1);

        equalsOrDie("Token starts at 12", 12, token.start);
        equalsOrDie("Token stops at 25", 25, token.stop);
        equalsOrDie("Token is COMMAND", JSTLCoreParser.TokenTypes.COMMAND, token.type);
        equalsOrDie("TEXT is ", "if test='foo'", Str.sliceOf(text, token.start, token.stop));

        token = tokenList.get(2);

        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 71", 72, token.stop);
        equalsOrDie("Token is COMMAND_BODY", JSTLCoreParser.TokenTypes.COMMAND_BODY, token.type);
        equalsOrDie("TEXT is ", "  body  <c:if test='bar'> body2 </c:if> body3 ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(3);

        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 34", 34, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  body  ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(4);

        puts(tokenList.size());
        equalsOrDie("Token starts at 37", 37, token.start);
        equalsOrDie("Token stops at 50", 50, token.stop);
        equalsOrDie("Token is COMMAND", JSTLCoreParser.TokenTypes.COMMAND, token.type);
        equalsOrDie("TEXT is ", "#if test='bar'#", "#"+Str.sliceOf(text, token.start, token.stop)+"#");



        token = tokenList.get(5);
        equalsOrDie("Token starts at 51", 51, token.start);
        equalsOrDie("Token stops at 58", 58, token.stop);
        equalsOrDie("Token is COMMAND_BODY", JSTLCoreParser.TokenTypes.COMMAND_BODY, token.type);
        equalsOrDie("TEXT is ", "# body2 #", "#"+Str.sliceOf(text, token.start, token.stop)+"#");



        token = tokenList.get(6);
        equalsOrDie("Token starts at 51", 51, token.start);
        equalsOrDie("Token stops at 58", 58, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "# body2 #", "#"+Str.sliceOf(text, token.start, token.stop)+"#");


        token = tokenList.get(7);
        equalsOrDie("Token starts at 65", 65, token.start);
        equalsOrDie("Token stops at 72", 72, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "# body3 #", "#"+Str.sliceOf(text, token.start, token.stop)+"#");



        token = tokenList.get(8);
        equalsOrDie("Token starts at 79", 79, token.start);
        equalsOrDie("Token stops at 88", 88, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "#more text#", "#"+Str.sliceOf(text, token.start, token.stop)+"#");
    }




    @Test
    public void testSimpleExpression() {
        JSTLCoreParser parser = new JSTLCoreParser();
        String text = "  blah   <c:if test='foo'>  ${body} </c:if> more text";
                   //  012345678901234567890123456789012345678901234567890
                   //  0         10        20        30        40        50
        parser.parse(text);

        final List<JSTLCoreParser.Token> tokenList = parser.getTokenList();

        JSTLCoreParser.Token token = tokenList.get(0);

        equalsOrDie("Token starts at 0", 0, token.start);
        equalsOrDie("Token stops at 9", 9, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  blah   ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(1);

        equalsOrDie("Token starts at 12", 12, token.start);
        equalsOrDie("Token stops at 25", 25, token.stop);
        equalsOrDie("Token is COMMAND", JSTLCoreParser.TokenTypes.COMMAND, token.type);
        equalsOrDie("TEXT is ", "if test='foo'", Str.sliceOf(text, token.start, token.stop));

        token = tokenList.get(2);

        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 36", 36, token.stop);
        equalsOrDie("Token is COMMAND_BODY", JSTLCoreParser.TokenTypes.COMMAND_BODY, token.type);
        equalsOrDie("TEXT is ", "  ${body} ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(3);
        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 28", 28, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  ", Str.sliceOf(text, token.start, token.stop));



        token = tokenList.get(4);
        equalsOrDie("Token starts at 30", 30, token.start);
        equalsOrDie("Token stops at 34", 34, token.stop);
        equalsOrDie("Token is EXPRESSION", JSTLCoreParser.TokenTypes.EXPRESSION, token.type);
        equalsOrDie("TEXT is ", "body", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(5);
        equalsOrDie("Token starts at 35", 35, token.start);
        equalsOrDie("Token stops at 36", 36, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", " ", Str.sliceOf(text, token.start, token.stop));



        token = tokenList.get(6);
        equalsOrDie("Token starts at 43", 43, token.start);
        equalsOrDie("Token stops at 53", 53, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", " more text", Str.sliceOf(text, token.start, token.stop));

    }


    @Test
    public void testTwoExpressions() {
        JSTLCoreParser parser = new JSTLCoreParser();
        String text = "  blah   <c:if test='foo'>  ${body}abc${bacon}zzz</c:if> more text";
                   //  01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                   //  0         10        20        30        40        50       60
        parser.parse(text);

        final List<JSTLCoreParser.Token> tokenList = parser.getTokenList();

        JSTLCoreParser.Token token = tokenList.get(0);

        equalsOrDie("Token starts at 0", 0, token.start);
        equalsOrDie("Token stops at 9", 9, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  blah   ", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(1);

        equalsOrDie("Token starts at 12", 12, token.start);
        equalsOrDie("Token stops at 25", 25, token.stop);
        equalsOrDie("Token is COMMAND", JSTLCoreParser.TokenTypes.COMMAND, token.type);
        equalsOrDie("TEXT is ", "if test='foo'", Str.sliceOf(text, token.start, token.stop));

        token = tokenList.get(2);

        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 49", 49, token.stop);
        equalsOrDie("Token is COMMAND_BODY", JSTLCoreParser.TokenTypes.COMMAND_BODY, token.type);
        equalsOrDie("TEXT is ", "  ${body}abc${bacon}zzz", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(3);
        equalsOrDie("Token starts at 26", 26, token.start);
        equalsOrDie("Token stops at 28", 28, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "  ", Str.sliceOf(text, token.start, token.stop));



        token = tokenList.get(4);
        equalsOrDie("Token starts at 30", 30, token.start);
        equalsOrDie("Token stops at 34", 34, token.stop);
        equalsOrDie("Token is EXPRESSION", JSTLCoreParser.TokenTypes.EXPRESSION, token.type);
        equalsOrDie("TEXT is ", "body", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(5);
        equalsOrDie("Token starts at 35", 35, token.start);
        equalsOrDie("Token stops at 38", 38, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "abc", Str.sliceOf(text, token.start, token.stop));



        token = tokenList.get(6);
        equalsOrDie("Token starts at 40", 40, token.start);
        equalsOrDie("Token stops at 45", 45, token.stop);
        equalsOrDie("Token is EXPRESSION", JSTLCoreParser.TokenTypes.EXPRESSION, token.type);
        equalsOrDie("TEXT is ", "bacon", Str.sliceOf(text, token.start, token.stop));



        token = tokenList.get(7);
        equalsOrDie("Token starts at 46", 46, token.start);
        equalsOrDie("Token stops at 49", 49, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", "zzz", Str.sliceOf(text, token.start, token.stop));


        token = tokenList.get(8);
        equalsOrDie("Token starts at 46", 56, token.start);
        equalsOrDie("Token stops at 49", 66, token.stop);
        equalsOrDie("Token is TEXT", JSTLCoreParser.TokenTypes.TEXT, token.type);
        equalsOrDie("TEXT is ", " more text", Str.sliceOf(text, token.start, token.stop));

    }

}
