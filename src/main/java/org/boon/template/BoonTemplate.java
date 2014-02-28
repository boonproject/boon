package org.boon.template;

import org.boon.Boon;
import org.boon.Lists;
import org.boon.Str;
import org.boon.core.Conversions;

import static java.util.Arrays.copyOfRange;


import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.MethodAccess;
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;

import java.util.*;

import static org.boon.Lists.list;
import static org.boon.Maps.map;
import static org.boon.core.reflection.BeanUtils.*;
import static org.boon.core.reflection.FastStringUtils.noCopyStringFromChars;
import static org.boon.json.JsonFactory.fromJson;
import static org.boon.primitive.CharScanner.findChar;
import static org.boon.primitive.CharScanner.findChars;
import static org.boon.primitive.CharScanner.findString;

/**
 * Created by Richard on 2/27/14.
 *
 * TODO
 * Improve error handling and tests.
 * Improve toJson, fromJson support
 *
 * TODO add this:
 *
 * Helpers receive the current context as the this context of the function.
 <ul>
 {{#each items}}
 <li>{{agree_button}}</li>
 {{/each}}
 </ul>
 when using this context and helpers:
using functions

 TODO
 add factory or make template more factory like, add helper.

 TODO
 implement handlebar templates
 Template comments with {{! }} or {{!-- --}}.

 TODO
 handle different forms of this

 Handlebars also allows for name conflict resolution between helpers and data fields via a this reference:
 <p>{{./name}} or {{this/name}} or {{this.name}}</p>
 Any of the above would cause the name field on the current context to be used rather than a helper of the same name.

 TODO improve path support
 Nested handlebars paths can also include ../ segments, which evaluate their paths against a parent context.
 <h1>Comments</h1>

 <div id="comments">
 {{#each comments}}
 <h2><a href="/posts/{{../permalink}}#{{id}}">{{title}}</a></h2>
 <div>{{body}}</div>
 {{/each}}
 </div>

 * <pre>
 * TODO add this
 * When looping through items in each, you can optionally reference the current loop index via {{@index}}
 {{#each array}}
 {{@index}}: {{this}}
 {{/each}}
 Additionally for object iteration, {{@key}} references the current key name:
 {{#each object}}
 {{@key}}: {{this}}
 {{/each}}
 </pre>
 <p>
 The first and last steps of iteration are noted via the @first and @last
 variables then iterating over an array. When iterating over an object only the @first is available.
 </p>
 <p>
 TODO
 If you find a "function" (Provider or Function), call it instead of evaluating it and converting it toString.
 Function gets passed context as argument to apply.

 TODO loading templates by name

 template.setPrefix("/templates");
 template.setSuffix(".html");

 always search classpath first

 TODO
 https://github.com/jknack/handlebars.java

 block and partial

 Block and partial helpers work together to provide you Template Inheritance.

 Usage:

 {{#block "title"}}
 ...
 {{/block}}
 context: A string literal which define the region's name.

 Usage:

 {{#partial "title"}}
 ...
 {{/partial}}
 context: A string literal which define the region's name.

 //

 he uses embedded for include {{embedded "user"}}
 I think I will stick with include but support embedded

 TODO
 //i18n ??? maybe

 Might implement some of these

 //https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/helper/StringHelpers.java
 I have some too. :) //lower, upper, rpad, etc. https://github.com/jknack/handlebars.java/blob/master/handlebars/src/main/java/com/github/jknack/handlebars/helper/StringHelpers.java



 TODO implement default value

 TODO
 https://github.com/elving/swag
 </p>
 */
public class BoonTemplate {

    //Config
    char[] expressionStart = "{{".toCharArray();
    String endBlockStart = "{{/";
    char[] unescapedExpressionStart = "{{{".toCharArray();
    char[] unescapedExpressionEnd = "}}}".toCharArray();
    final boolean sameStart;
    String endBlockEnd = "}}";
    char[] expressionEnd = "}}".toCharArray();
    char expressionStart1stChar = expressionStart[0];
    char unescapedExpressionStartChar = unescapedExpressionStart[0];
    String commandMarker = "#";

    //State
    int lineIndex;
    char[][] lines;
    Object context;
    boolean escaped;

    Map<String, Command> commandMap;


    public BoonTemplate(char[] expressionStart, char[] expressionEnd, Object functions) {
        this.expressionStart = expressionStart;
        this.expressionEnd = expressionEnd;
        this.expressionStart1stChar = expressionStart[0];

        this.unescapedExpressionStartChar = unescapedExpressionStart[0];

        sameStart = expressionStart1stChar == unescapedExpressionStartChar;

        if (functions!=null) {
            extractFunctions(functions, false);
        }
    }


    private void extractFunctions(Object functions, boolean all) {
        commandMap = new HashMap<>();

        ClassMeta<?> classMeta = ClassMeta.classMetaEither(functions);

        Iterable<MethodAccess> methods = classMeta.methods();

        if (!all) {
            for (MethodAccess methodAccess : methods) {
                if (methodAccess.respondsTo(CharBuf.class, String.class, CharSequence.class, Object.class)) {
                    commandMap.put(methodAccess.name(), new InvokeCommand(functions, methodAccess));
                } else if (methodAccess.respondsTo(String.class, CharSequence.class, Object.class)) {
                    commandMap.put(methodAccess.name(), new InvokeCommand(functions, methodAccess));
                }
            }
        } else {
            for (MethodAccess methodAccess : methods) {
                commandMap.put(methodAccess.name(), new InvokeCommand(functions, methodAccess));
            }
        }
    }

    public BoonTemplate() {

        sameStart = expressionStart1stChar == unescapedExpressionStartChar;

    }

    public static BoonTemplate template() {
        return new BoonTemplate();
    }


    public static BoonTemplate jstl() {
        return template("${", "}");
    }


    public static BoonTemplate template(String expStart, String expEnd) {
        return new BoonTemplate(expStart.toCharArray(), expEnd.toCharArray(), null);
    }


    public static BoonTemplate template(char[] expStart, char[] expEnd) {
        return new BoonTemplate(expStart, expEnd, null);
    }


    public static BoonTemplate template(char[] expStart, char[] expEnd, Object functions) {
        return new BoonTemplate(expStart, expEnd, functions);
    }


    public static BoonTemplate templateWithFunctions(Object functions) {
        BoonTemplate boonTemplate = new BoonTemplate();
        boonTemplate.extractFunctions(functions, false);
        return boonTemplate;
    }


    public static BoonTemplate templateWithDynamicFunctions(Object functions) {
        BoonTemplate boonTemplate = new BoonTemplate();
        boonTemplate.extractFunctions(functions, true);
        return boonTemplate;

    }


    public CharSequence replace (CharSequence template, Object context) {

        if (context instanceof CharSequence) {
            try {
                this.context = fromJson(context.toString());
            } catch (Exception ex) {
                this.context = context;
            }
        } else {
            this.context = context;
        }
        char[] chars = FastStringUtils.toCharArray(template);
        CharBuf output = CharBuf.create(template.length());

        lines = CharScanner.splitLines(chars);

        for ( lineIndex = 0; lineIndex < lines.length; lineIndex++ ) {
            char [] line = lines[lineIndex];

            if (lineHasCommand()) {
                processLineCommand(output, line); //for JSTL like commands
            }

            int index = findExpression(line);
            if (index == -1) {
                output.addLine(line);
            } else {

                output.add(copyOfRange(line, 0, index));
                processCommandOrExpression(output, line, index);
            }
        }

        output.removeLastChar();
        return output;
    }


    private int findExpression(char[] line) {

        return findExpressionFromIndex(line, 0);
    }

    private int findExpressionFromIndex(char[] line, int startIndex) {

        if (sameStart) {
            if (findChar(expressionStart1stChar, startIndex, line) == -1) {
                return -1;
            }
        }

        int index;
        index =  findChars(expressionStart, startIndex, line);

        if (index != -1) {
                escaped =  true;
        } else {
                escaped =  false;
                index =  findChars(unescapedExpressionStart, startIndex, line);
        }
        return index;

    }

    /** These two are here so I can add JSTL style support later. */
    protected boolean lineHasCommand() {
        return false;
    }

    protected void processLineCommand(CharBuf output, char[] line ){

    }


    protected String escape(String charSequence ){
        if (escaped) {
            return charSequence; //No op
        } else {
            return charSequence;
        }
    }

    private void processCommandOrExpression(CharBuf output, char[] line, int index) {
        int startIndex;

        char [] expressionStart;
        char [] expressionEnd;

        if (escaped ) {

            expressionStart= this.expressionStart;
            expressionEnd = this.expressionEnd;
        } else {
            expressionStart= this.unescapedExpressionStart;
            expressionEnd = this.unescapedExpressionEnd;
        }

        startIndex = index + expressionStart.length;

        index = findChars(expressionEnd, startIndex, line);

        if (index == -1) {
            output.addLine(line);
            return;
        }

        String command = noCopyStringFromChars( copyOfRange(line, startIndex, index) );
        processCommand(output, command);

        index = index + expressionEnd.length;


        int findIndex = findExpressionFromIndex(line, index);
        if (findIndex == -1) {
            output.addLine(copyOfRange(line, index, line.length));
        } else {

            output.add(copyOfRange(line, index, findIndex));
            processCommandOrExpression(output, line, findIndex);
        }
    }



    private void processCommand(CharBuf output, String command ) {

        if (!command.startsWith(commandMarker)) {
            handleExpression(output, command);
        } else {
            handleCommand(output, command);
        }
    }

    private void handleCommand(CharBuf output, String command) {
        String cmd = Str.slc(command, commandMarker.length(), command.indexOf(' '));
        String arguments = Str.slc(command, commandMarker.length() + cmd.length() +1 );
        CharSequence block;

        String endOfBlock = Str.add(endBlockStart, cmd, endBlockEnd);
        block = readBlock( endOfBlock );

        switch (Commands.command(cmd)) {

            case IF:
                processIf(output, arguments, block);
                break;
            case EACH:
                processEach(output, arguments, block);
                break;

            case WITH:
                  processWith(output, arguments, block);
                  break;

            default:
                if (commandMap!=null) {
                    Command commandObject = commandMap.get(cmd);
                    if (commandObject!=null) {
                        commandObject.processCommand(output, arguments, block, context);
                    } else {
                        commandObject = commandMap.get("missingCommand");
                        if (commandObject!=null) {
                            commandObject.processCommand(output, arguments, block, Lists.list(
                                    map("command", cmd),
                                    context));
                        }
                    }
                }

          }
    }

    private void handleExpression(CharBuf output, String command) {
        Object object;

        object = lookup(command);
        String str = Str.toString(object, command);
        output.add( escape(str) );
    }

    private Object lookup(String command) {
        return findProperty(context, command);
    }

    private void processEach(CharBuf output, String arguments, CharSequence block) {

        Object object = lookup(arguments);

        if (object instanceof Map) {
            eachMapProperty(output, block, object);
        } else {

            eachListItem(output, block, object);
        }

    }

    private void eachListItem(CharBuf output, CharSequence block, Object object) {
        Iterator iterator = Conversions.iterator(object);
        int len = Boon.len(object);
        int index = 0;
        CharSequence blockOutput;

        Map<String, Object> map = map("@length", len, "@array", object);


        while (iterator.hasNext()) {

            Object item = iterator.next();
            map.put("@index", index );
            map.put("@first", index == 0 );
            map.put("@last",  index == len-1 );
            map.put("@even",  index % 2 == 0 );
            map.put("@odd",   index % 2 != 0 );
            map.put("@this",   item );
            map.put("this",   item );
            blockOutput = template(expressionStart, expressionEnd)
                    .replace(block, list(item, map, context));
            output.add(blockOutput);
            index++;
        }
        output.removeLastChar();
    }

    private void eachMapProperty(CharBuf output, CharSequence block, Object object) {
        Map<Object, Object> objectMap = (Map<Object, Object>) object;
        Set<Map.Entry<Object, Object>> entries = objectMap.entrySet();
        int len = objectMap.size();
        int index = 0;
        CharSequence blockOutput;


        Map <String, Object> map = map("@length", len, "@array", object);


        for (Map.Entry<Object, Object> entry : entries) {
            map.put("@index", index );
            map.put("@first", index == 0 );
            map.put("@last",  index == len-1 );
            map.put("@even",  index % 2 == 0 );
            map.put("@odd",   index % 2 != 0 );
            map.put("@value",  entry.getValue() );
            map.put("@this",  entry.getValue() );
            map.put("@key",  entry.getKey() );
            map.put("this",  entry.getValue() );

            blockOutput = template(expressionStart, expressionEnd)
                    .replace(block, list(entry.getValue(), map, context));
            output.add(blockOutput);



            index++;
        }
    }


    private void processWith(CharBuf output, String arguments, CharSequence block) {
        Object object = idx(context, arguments);
        CharSequence blockOutput = template(this.expressionStart, this.expressionEnd)
                    .replace(block, list(object, context));
        output.add(blockOutput);
    }


    private void processIf(CharBuf output, String arguments, CharSequence block) {

        Object oTest = lookup(arguments);
        boolean test;
        test = Conversions.toBoolean(oTest);

        if (test) {

            CharSequence blockOutput = template(this.expressionStart, this.expressionEnd).replace(block, context);
            output.add(blockOutput);
        }
        output.removeLastChar();
    }

    private CharSequence readBlock(String endBlock) {
        CharBuf buf = CharBuf.create(80);

        lineIndex++;

        for (; lineIndex<lines.length; lineIndex++) {

            int index = findString(endBlock, lines[lineIndex]);
            if (index != -1) {
                return buf;
            } else {
                buf.addLine(lines[lineIndex]);
            }
        }

        lineIndex++;
        return buf;
    }



}
