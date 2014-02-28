package org.boon.handlebars;

import org.boon.Lists;
import org.boon.Str;
import org.boon.core.Conversions;

import static java.util.Arrays.copyOfRange;
import static org.boon.Boon.sputs;
import static org.boon.core.reflection.BeanUtils.idx;

import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.MethodAccess;
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Lists.list;
import static org.boon.Maps.map;
import static org.boon.core.reflection.FastStringUtils.noCopyStringFromChars;
import static org.boon.json.JsonFactory.fromJson;
import static org.boon.json.JsonFactory.toJson;
import static org.boon.primitive.CharScanner.findChar;
import static org.boon.primitive.CharScanner.findChars;
import static org.boon.primitive.CharScanner.findString;

/**
 * Created by Richard on 2/27/14.
 */
public class HandleBarTemplateParser {

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


    public HandleBarTemplateParser(char[] expressionStart, char[] expressionEnd, Object functions) {
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

    public HandleBarTemplateParser() {

        sameStart = expressionStart1stChar == unescapedExpressionStartChar;

    }

    public static HandleBarTemplateParser template() {
        return new HandleBarTemplateParser();
    }


    public static HandleBarTemplateParser template(String expStart, String expEnd) {
        return new HandleBarTemplateParser(expStart.toCharArray(), expEnd.toCharArray(), null);
    }


    public static HandleBarTemplateParser template(char[] expStart, char[] expEnd) {
        return new HandleBarTemplateParser(expStart, expEnd, null);
    }


    public static HandleBarTemplateParser template(char[] expStart, char[] expEnd, Object functions) {
        return new HandleBarTemplateParser(expStart, expEnd, functions);
    }


    public static HandleBarTemplateParser templateWithFunctions(Object functions) {
        HandleBarTemplateParser handleBarTemplateParser = new HandleBarTemplateParser();
        handleBarTemplateParser.extractFunctions(functions, false);
        return handleBarTemplateParser;
    }


    private static HandleBarTemplateParser templateWithDynamicFunctions(Object functions) {
        HandleBarTemplateParser handleBarTemplateParser = new HandleBarTemplateParser();
        handleBarTemplateParser.extractFunctions(functions, true);
        return handleBarTemplateParser;

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
        Object object = null;
        Iterator iterator = Conversions.iterator(context);

        if (command.equals("this")) {
            object = iterator.next();
        } else {
            while (iterator.hasNext()) {
                Object ctx = iterator.next();
                object = idx(ctx, command);
                if (object != null) {
                    break;
                }
            }
        }

        return object;

    }

    private void processEach(CharBuf output, String arguments, CharSequence block) {

        Object object = lookup(arguments);

        Iterator iterator = Conversions.iterator(object);
        while (iterator.hasNext()) {
            CharSequence blockOutput = template(this.expressionStart, this.expressionEnd)
                    .replace(block, list(iterator.next(), context));
            output.add(blockOutput);

        }
        output.removeLastChar();

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


    public static void main (String... args) {
        CharSequence replace;

//        replace = template().replace("Hello {{name}}! \n How are you {{name}}?", map("name", "Rick"));
//        puts(replace);
//
//        replace = template("${", "}").replace("Hello ${name}! \n How are you ${name}?", map("name", "Rick"));
//        puts(replace);
//
//
//        replace = template("$", " ").replace("Hello $name ! \n How are you $name ?", map("name", "Rick"));
//        puts(replace);
//
//        replace = template("[[", "]]").replace("Hello [[name]]! \n How are you [[name]]?", map("name", "Rick"));
//        puts(replace);
//
//
//        replace = template().replace("Hello {{{name}}}! \n How are you {{{name}}}?", map("name", "Rick"));
//        puts(replace);
//
//
//        replace = template().replace("{{#if name}}\n" +
//                    "Hello {{name}}!\n" +
//                    "How are you {{name}}?\n" +
//                "{{/if}}\n Glad to hear it", map("name", "Rick"));
//        puts (replace);
//
//
//
//
//        replace = template().replace("{{#if name}}\n" +
//                "Hello {{name}}!\n" +
//                "How are you {{name}}? more text here\n" +
//                "{{/if}}\n" +
//                "" +
//                "{{#each fruits}}\n" +
//                "       {{this}}\n" +
//                "{{/each}}"
//                ,
//
//                map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));
//
//
//        puts(replace);
//
//        puts("----");
//
//        replace = template().replace("{{#if name}}\n" +
//                "Hello {{name}}!\n" +
//                "How are you {{name}}? more text here\n" +
//                "{{/if}}\n" +
//                "<ol>\n" +
//                "{{#each foods}}\n" +
//                "       <li>{{name}}</li>\n" +
//                "{{/each}}\n" +
//                "</o>"
//                ,
//
//                map("name", "Rick", "foods", list(
//                        map("name", "pizza"),
//                        map("name", "fish"),
//                        map("name", "fruit")
//                )));
//
//
//        puts(replace);
//
//        puts("----");
//
//
//        replace = template().replace("{{#if name}}\n" +
//                "Hello {{name}}!\n" +
//                "How are you {{name}}? more text here\n" +
//                "{{/if}}\n" +
//                "<ol>\n" +
//                "{{#each foodszzzz}}\n" +
//                "       <li>{{name}}</li>\n" +
//                "{{/each}}\n" +
//                "</o>"
//                ,
//
//                map("name", "Rick", "foods", list(
//                        map("name", "pizza"),
//                        map("name", "fish"),
//                        map("name", "fruit")
//                )));
//
//
//        puts(replace);
//
//        puts("----");
//
//
//
//
//        replace = template().replace("{{#if name}}\n" +
//                "Hello {{name}}!\n" +
//                "How are you {{name}}? more text here\n" +
//                "{{/if}}\n" +
//                "<ol>\n" +
//                "{{#each foods}}\n" +
//                "       <li>{{name}}</li>\n" +
//                "{{/each}}\n" +
//                "</o>\n" +
//                "{{#with rick}}\n" +
//                "{{name}}\n" +
//                "{{/with}}"
//                ,
//
//                map("name", "Rick", "foods",
//                        list(
//                                map("name", "pizza"),
//                                map("name", "fish"),
//                                map("name", "fruit")
//                        ),
//                        "rick", map("name", "Rick Hightower")
//               ));
//
//
//        puts(replace);
//
//        puts("----");
//
//
//
//        replace = templateWithFunctions(
//                new Object() {
//
//                    String by(String arguments, CharSequence block, Object context) {
//                        Object object = idx(context, arguments);
//                        CharSequence blockOutput = template()
//                                .replace(block, list(object, context));
//                        return blockOutput.toString();
//
//                    }
//                }
//        ).replace("{{#if name}}\n" +
//                "Hello {{name}}!\n" +
//                "How are you {{name}}? more text here\n" +
//                "{{/if}}\n" +
//                "<ol>\n" +
//                "{{#each foods}}\n" +
//                "       <li>{{name}}</li>\n" +
//                "{{/each}}\n" +
//                "</o>\n" +
//                "{{#by rick}}\n" +
//                "By: {{name}}\n" +
//                "{{/by}}"
//                ,
//
//                map("name", "Rick", "foods",
//                        list(
//                                map("name", "pizza"),
//                                map("name", "fish"),
//                                map("name", "fruit")
//                        ),
//                        "rick", map("name", "Rick Hightower")
//                ));
//
//
//        puts(replace);
//
//        puts("----");
//
//        replace = templateWithFunctions(
//                new Object() {
//
//                    String by(String arguments, CharSequence block, Object context) {
//                        Object object = idx(context, arguments);
//                        CharSequence blockOutput = template()
//                                .replace(block, list(object, context));
//                        return blockOutput.toString();
//
//                    }
//                }
//        ).replace("{{#if name}}\n" +
//                "Hello {{name}}!\n" +
//                "How are you {{name}}? more text here\n" +
//                "{{/if}}\n" +
//                "<ol>\n" +
//                "{{#each foods}}\n" +
//                "       <li>{{name}}</li>\n" +
//                "{{/each}}\n" +
//                "</o>\n" +
//                "{{#by rick}}\n" +
//                "By: {{name}}\n" +
//                "{{/by}}"
//                ,
//
//                map("name", "Rick", "foods",
//                        list(
//                                map("name", "pizza"),
//                                map("name", "fish"),
//                                map("name", "fruit")
//                        ),
//                        "rick", map("name", "Rick Hightower")
//                ));
//
//
//        puts(replace);
//
//        puts("----");


        replace = templateWithDynamicFunctions(
                new Object() {

                    String add(int a, int b, String var, CharSequence block, Object context) {
                        CharSequence blockOutput = template()
                                .replace(block, list(map(var, a + b), context));
                        return blockOutput.toString();

                    }
                }
        ).replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>\n" +
                "{{#add [1,2,'out']}}\n" +
                "By: {{out}}\n" +
                "{{/add}}"
                ,

                map("name", "Rick", "foods",
                        list(
                                map("name", "pizza"),
                                map("name", "fish"),
                                map("name", "fruit")
                        ),
                        "rick", map("name", "Rick Hightower")
                ));


        puts(replace);

        puts("----");


        String arguments = HandleBarTemplateParser.template("${", "}").replace("[${a},${b}]", map("a", 1, "b", 2)).toString();

        puts (arguments);


        replace = templateWithDynamicFunctions(
                new Object() {

                    String add(int a, int b, String var, CharSequence block, Object context) {
                        CharSequence blockOutput = template()
                                .replace(block, list(map(var, a + b), context));
                        return blockOutput.toString();

                    }
                }
        ).replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>\n" +
                "{{#add [${a} ,${b} ,'out']}}\n" +
                "ADD: {{out}}\n" +
                "{{/add}}",

                map(    "name", "Rick",
                        "a", 1,
                        "b", 5,
                        "foods",
                        list(
                                map("name", "pizza"),
                                map("name", "fish"),
                                map("name", "fruit")
                        ),
                        "rick", map("name", "Rick Hightower")
                ));


        puts(replace);

        puts("----");


        replace = templateWithDynamicFunctions(
                new Object() {

                    String add(int a, int b, String var, String block, Object context) {
                        CharSequence blockOutput = template()
                                .replace(block, list(map(var, a + b), context));
                        return blockOutput.toString();

                    }
                }
        ).replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>\n" +
                "{{#add [${a} ,${b} ,'out']}}\n" +
                "ADD: {{out}}\n" +
                "{{/add}}",

                map(    "name", "Rick",
                        "a", 1,
                        "b", 5,
                        "foods",
                        list(
                                map("name", "pizza"),
                                map("name", "fish"),
                                map("name", "fruit")
                        ),
                        "rick", map("name", "Rick Hightower")
                ));


        puts(replace);

        puts("----");


        replace = templateWithDynamicFunctions(
                new Object() {

                    String add(int a, int b, String var, String block) {

                        return sputs(var, "=", (a+b));
                    }
                }
        ).replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>\n" +
                "{{#add [${a} ,${b} ,'out']}}\n" +
                "ADD: {{out}}\n" +
                "{{/add}}",

                map(    "name", "Rick",
                        "a", 1,
                        "b", 5,
                        "foods",
                        list(
                                map("name", "pizza"),
                                map("name", "fish"),
                                map("name", "fruit")
                        ),
                        "rick", map("name", "Rick Hightower")
                ));


        puts(replace);

        puts("----");



        String json = toJson(map(    "name", "Rick",
                "a", 50,
                "b", 5,
                "foods",
                list(
                        map("name", "pizza"),
                        map("name", "fish"),
                        map("name", "fruit")
                ),
                "rick", map("name", "Rick Hightower")
        ));

        puts (json);

        replace = templateWithDynamicFunctions(
                new Object() {

                    String add(int a, int b, String var, String block) {

                        return sputs(var, "=", (a+b));
                    }
                }
        ).replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>\n" +
                "{{#add [${a} ,${b} ,'out']}}\n" +
                "ADD: {{out}}\n" +
                "{{/add}}",

                json
                );




        puts(replace);

        puts("----");

    }


}
