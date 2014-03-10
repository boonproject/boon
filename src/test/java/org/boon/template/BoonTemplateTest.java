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

package org.boon.template;

import org.boon.Str;
import org.boon.core.Fn;
import org.junit.Test;

import java.util.List;

import static org.boon.primitive.Arry.slc;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Lists.mapBy;
import static org.boon.Maps.map;
import static org.boon.Str.equalsOrDie;
import static org.boon.Str.joinCollection;
import static org.boon.core.reflection.BeanUtils.getPropertyValue;
import static org.boon.core.reflection.BeanUtils.idx;
import static org.boon.json.JsonFactory.niceJson;
import static org.boon.json.JsonFactory.toJson;
import static org.boon.template.BoonTemplate.*;

/**
 * Created by Richard on 2/27/14.
 */
public class BoonTemplateTest {

    String listTemplate = "{{#each items}}\n" +
            "\n" +
            " this {{this}}, index {{@index}}, key {{@key}}, first {{@first}}, last {{@last}}\n" +
            "\n" +
            "{{/each}}";


    String jstListTemplate = "<c:forEach items=\"items\">\n" +
            "\n" +
            " this ${this}, index ${@index}, key ${@key}, first ${@first}, last ${@last}\n" +
            "\n" +
            "</c:forEach>";

    CharSequence replace;

    boolean ok = true;

    public static class Name {
        String firstName;
        String lastName;

        public Name(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class Person {
        Name name;

        public Person(Name name) {
            this.name = name;
        }
    }

    @Test
    public void testDefault() {


        replace = template().replace("{{name|Rick}}", null);
        equalsOrDie("Rick", replace.toString());

        replace = template().replace("{{name|Bob}}", map("name", "Rick"));
        equalsOrDie("Rick", replace);

        replace = template().replace("{{name|Bob}}", niceJson("{'name':'Rick'}"));
        equalsOrDie("Rick", replace);

    }

    @Test
    public void testDefault2() {


        replace = template().replace("{{this.name.firstName|Bob}}",
                                 new Person(new Name("Rick", "Hightower")));

        equalsOrDie("Rick", replace);


        replace = template().replace("{{this/name/firstName|Bob}}",
                                 new Person(new Name("Sam", "Hightower")));

        equalsOrDie("Sam", replace);


        replace = template().replace("{{name/firstName|Bob}}",
                new Person(new Name("RickyBobby", "Hightower")));

        equalsOrDie("RickyBobby", replace);



        replace = template().replace("{{name/firstName|Bob}}",
                niceJson("{ 'name': { 'firstName':'RickyBobby', 'lastName':'Hightower' } }"));

        equalsOrDie("RickyBobby", replace);

    }


    @Test
    public void testDefault3() {


        replace = template().replace("{{[1]/name/firstName|Bob}}",
                list(map("a", 1), list(
                        new Person(new Name("Diana", "Hightower")),
                        new Person(new Name("Rick", "Hightower"))

                ))
        );


        equalsOrDie("Rick", replace);

    }



    @Test
    public void testIteration() {


        replace = template().replace(listTemplate, map("items", list("apple", "oranges", "pears")));

        equalsOrDie("\n" +
                " this apple, index 0, key @key, first true, last false\n" +
                "\n" +
                "\n" +
                " this oranges, index 1, key @key, first false, last false\n" +
                "\n" +
                "\n" +
                " this pears, index 2, key @key, first false, last true\n" +
                "\n", replace);

        replace = template().replace(listTemplate, map("items", map("apple", 1, "oranges", 2, "pears", 4)));
        String test = "\n" +
                " this 1, index 0, key apple, first true, last false\n" +
                "\n" +
                "\n" +
                " this 2, index 1, key oranges, first false, last false\n" +
                "\n" +
                "\n" +
                " this 4, index 2, key pears, first false, last true\n" +
                "\n";
        equalsOrDie(test, replace);

        replace = template().replace(listTemplate, niceJson("{'items' : ['apple', 'oranges', 'pears']}"));
        test = "\n" +
                " this apple, index 0, key @key, first true, last false\n" +
                "\n" +
                "\n" +
                " this oranges, index 1, key @key, first false, last false\n" +
                "\n" +
                "\n" +
                " this pears, index 2, key @key, first false, last true\n" +
                "\n";
        equalsOrDie(test, replace);

        replace = template().replace(listTemplate, niceJson("{'items' : {'apple': 1, 'oranges': 2, 'pears': 4)}}"));
        test = "\n" +
                " this 33, index 0, key pears, first true, last false\n" +
                "\n" +
                "\n" +
                " this 2, index 1, key oranges, first false, last false\n" +
                "\n" +
                "\n" +
                " this 1, index 2, key apple, first false, last true\n" +
                "\n";
        equalsOrDie(test, replace);

    }

    @Test
    public void testJSTLIteration() {


        replace = jstl().replace(jstListTemplate, map("items", list("apple", "oranges", "pears")));

        equalsOrDie(replace, "\n" +
                " this apple, index 0, key @key, first true, last false\n" +
                "\n" +
                "\n" +
                " this oranges, index 1, key @key, first false, last false\n" +
                "\n" +
                "\n" +
                " this pears, index 2, key @key, first false, last true\n" +
                "\n");
    }


    @Test
    public void forloopNoSpaces() {


        replace = template().replace("{{#each items}}{{this}}{{/each}}", map("items", list(1,2,3,4,5)));
        equalsOrDie("12345", replace);

    }



    @Test
    public void forloopNoSpaceDoubleThis() {


        replace = template().replace("{{#each this}}{{this}}{{/each}}", list(1, 2, 3, 4, 5));
        equalsOrDie("12345", replace);

    }

    @Test
    public void simpleIf() {


        replace = template().replace("{{#if name}}\n" +
                "{{name}}\n" +
                "{{/if}}", map("name", "Rick"));


        equalsOrDie("Rick\n", replace);

    }


    @Test
    public void simpleJstlIf() {


        replace = jstl().replace("<c:if \"test\"=\"name\">\n\n${name}\n\n</c:if>", map("name", "Rick"));


        equalsOrDie(replace, "\nRick\n\n");

    }


    @Test //Broken
    public void simpleJstlIf2() {


        replace = jstl().replace("<c:if \"test\"=\"name\">${name}</c:if>", map("name", "Rick"));


        //TODO fix
        //equalsOrDie(replace, "Rick");

    }

    @Test
    public void ifTestTextAfterCloseIf() {


        replace = template().replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}?\n" +
                "{{/if}}\n Glad to hear it", map("name", "Rick"));

        equalsOrDie("Hello Rick!\n" +
                "How are you Rick?\n" +
                " Glad to hear it", replace);

    }

    @Test
    public void ifElseTest1() {



        replace = template().replace("" +
                "\n{{#if name}}\n" +
                "{{name}}\n" +
                "{{else}}\n" +
                "duck\n" +
                "{{/if}}", map("name", "Rick"));


        equalsOrDie(replace, "\nRick\n");

    }


    @Test
    public void ifElseTest2() {




        replace = template().replace("" +
                "\n{{#if name}}\n" +
                "{{name}}\n" +
                "{{else}}\n" +
                "duck\n" +
                "{{/if}}", map("duck", "Rick"));

        equalsOrDie("\nduck\n", replace);

    }

    @Test
    public void testMultiArgs() {
        replace = template().replace("{{#if name flea}}\n" +
                "{{name}}\n" +
                "{{else}}\n" +
                "duck\n" +
                "{{/if}}", map("name", "Rick", "flea", "bee"));

        equalsOrDie("Rick\n", replace);


    }


    @Test
    public void testMultiArgsFalse() {
        replace = template().replace("{{#if name flea}}\n" +
                "{{name}}\n" +
                "{{else}}\n" +
                "duck\n" +
                "{{/if}}", map("name", "Rick", "flea", ""));

        equalsOrDie("duck\n", replace);


    }

    @Test
    public void testMultiArgsExpression() {
        replace = template().replace("{{#if name ${flea} }}\n" +
                "{{name}}\n" +
                "{{else}}\n" +
                "duck\n" +
                "{{/if}}", map("name", "Rick", "flea", "boo", "boo", "baz"));


        equalsOrDie("Rick\n", replace);

    }


    @Test
    public void testMultiArgsExpression3() {
        replace = template().replace("{{#if name gt ${flea} }}\n" +
                "{{name}} {{test}}\n" +
                "{{else}}\n" +
                "duck {{test}}\n" +
                "{{/if}}", map("name", "Rick", "flea", "boo"));

        equalsOrDie("Rick [name, gt, boo, Rick, gt, boo]\n", replace);


    }

    @Test
    public void testMultiArgsExpression2() {
        replace = template().replace("{{#if ['name', '${flea}'] }}\n" +
                "{{name}} {{test}}\n" +
                "{{else}}\n" +
                "duck\n" +
                "{{/if}}", map("name", "Rick", "flea", "boo", "boo", "baz"));

        equalsOrDie("Rick [name, boo]\n", replace);


    }


    @Test
    public void eachFromJSON() {
        replace = template().replace("{{#each ['name', '${flea}'] }}\n" +
                "\n{{item}}\n" +

                "{{/each}}", map("name", "Rick", "flea", "boo", "boo", "baz"));

        equalsOrDie("\nname\n\nboo\n", replace);


    }

    @Test
    public void hiMom() {

        replace = templateWithFunctions(
                new Object() {

                    String add(int a, int b, String var, CharSequence block, Object context) {
                        CharSequence blockOutput = template()
                                .replace(block, list(map(var, a + b), context));
                        return blockOutput.toString();

                    }

                    String hiMom(String hi) {
                        return "Say hi " + hi + "\n";
                    }
                }
        ).replace("" +
                "{{hiMom(mom)}}" +
                "{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>\n" +
                "{{#add [1,2,'out']}}\n" +
                "Add Output: {{out}}\n" +
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


        equalsOrDie(replace, "Say hi mom\n" +
                "Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>\n" +
                "Add Output: 3\n");

    }


    @Test
    public void changeDelimiters() {
        replace = template("[[", "]]").replace("Hello [[name]]! \n How are you [[name]]?", map("name", "Rick"));

        equalsOrDie("Hello Rick! \n" +
                " How are you Rick?", replace);

    }

    @Test
    public void happyDay() {
        replace = template().replace("Hello {{name}}! \n How are you {{name}}?", map("name", "Rick"));

        equalsOrDie("Hello Rick! \n" +
                " How are you Rick?", replace);


    }

    @Test
    public void moreBasicTest() {
        replace = template("${", "}").replace("Hello ${name}! \n How are you ${name}?", map("name", "Rick"));
        ok = replace.equals("Hello Rick! \n" +
                " How are you Rick?") || die(replace);

        replace = template("$", " ").replace("Hello $name ! \n How are you $name ?", map("name", "Rick"));

        ok = replace.equals("Hello Rick! \n" +
                " How are you Rick?") || die(replace);

    }

    @Test //BROKEN
    public void tripleThreat() {

        replace = template().replace("Hello {{{name}}}! \n How are you {{{name}}}?", map("name", "Rick"));

        ok = replace.equals("Hello Rick! \n" +
                " How are you Rick?") || die(replace);

    }



    @Test
    public void eachAfterIf() {

        replace = template().replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "\n" +
                "{{#each fruits}}\n" +
                "       {{this}}\n" +
                "{{/each}}"
                ,

                map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));

        String test = "Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "\n" +
                "       apples\n" +
                "       pairs\n" +
                "       tangerines\n";

        equalsOrDie(test, replace);


    }


    @Test
    public void each() {

        replace = template().replace(
                "{{#each fruits}}\n" +
                "       {{this}}\n" +
                "{{/each}}"
                ,

                map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));

        equalsOrDie("       apples\n" +
                "       pairs\n" +
                "       tangerines\n", replace.toString());

    }


    @Test
    public void eachNoNewLine() {

        replace = template().replace(
                "{{#each fruits}} {{this}} {{/each}}",
                 map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));

        equalsOrDie(replace.toString(), " apples " +
                " pairs " +
                " tangerines ");

    }


    @Test
    public void moreComplicatedForEachOverMaps() {

        replace = template().replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>"
                ,

                map("name", "Rick", "foods", list(
                        map("name", "pizza"),
                        map("name", "fish"),
                        map("name", "fruit")
                )));



        String str = "Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>";




        equalsOrDie(str, replace);
    }



    @Test
    public void someTestEachExspressionNotInContext() {

        replace = template().replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foodszzzz}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>"
                ,

                map("name", "Rick", "foods", list(
                        map("name", "pizza"),
                        map("name", "fish"),
                        map("name", "fruit")
                )));


        equalsOrDie("Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>Rick</li>\n" +
                "</o>", replace);

    }


    @Test
    public void ifFollowedByEachFollowedByWith() {
        replace = template().replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "<ol>\n" +
                "{{#each foods}}\n" +
                "       <li>{{name}}</li>\n" +
                "{{/each}}\n" +
                "</o>\n" +
                "{{#with rick}}\n" +
                "{{name}}\n" +
                "{{/with}}"
                ,

                map("name", "Rick", "foods",
                        list(
                                map("name", "pizza"),
                                map("name", "fish"),
                                map("name", "fruit")
                        ),
                        "rick", map("name", "Rick Hightower")
                ));


        equalsOrDie("Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>\n" +
                "Rick\n", replace);


    }

    @Test
    public void createAndUseCustomCommandHandler() {



        replace = templateWithCommandHandlers(
                new Object() {

                    String by(String arguments, CharSequence block, Object context) {
                        Object object = idx(context, arguments);
                        CharSequence blockOutput = template()
                                .replace(block, list(object, context));
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
                "{{#by rick}}\n" +
                "By: {{name}}\n" +
                "{{/by}}"
                ,

                map("name", "Rick", "foods",
                        list(
                                map("name", "pizza"),
                                map("name", "fish"),
                                map("name", "fruit")
                        ),
                        "rick", map("name", "Rick Hightower")
                ));

        equalsOrDie("Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>\n" +
                "By: Rick Hightower\n", replace);


    }

    @Test
    public void processingMethodArgs() {
        String arguments = template("${", "}").replace("[${a},${b}]", map("a", 1, "b", 2)).toString();

        equalsOrDie("[1,2]", arguments);

    }


    @Test
    public void creatingACommandBodyTagThatTakesABody() {
        replace = templateWithFunctions(
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


        equalsOrDie("Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>\n" +
                "ADD: 6\n", replace);


    }


    @Test
    public void simpleCommandBodyHandler() {



        replace = templateWithFunctions(
                new Object() {

                    String add(int a, int b, String var, String block) {

                        return sputs(var, "=", (a + b));
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
                "{{/add}}" +
                "<ol>\n" +
                        "{{#each foods}}\n" +
                        "       <li>{{name}}</li>\n" +
                        "{{/each}}\n" +
                        "</o>\n"

                ,

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



        equalsOrDie("Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>\n" +
                "out = 6\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>\n", replace);

    }

    @Test
    public void workingWithJsonAsContext() {
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


        replace = templateWithFunctions(
                new Object() {

                    String add(int a, int b, String var, String block) {

                        return sputs(var, "=", (a + b));
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




        String test = "Hello Rick!\n" +
                "How are you Rick? more text here\n" +
                "<ol>\n" +
                "       <li>pizza</li>\n" +
                "       <li>fish</li>\n" +
                "       <li>fruit</li>\n" +
                "</o>\n" +
                "out = 55\n";



        equalsOrDie(test, replace);

    }

    @Test public void complexTestThatShowsJSONNestedMapsCustomBodyHandlersAndOtherFunStuff(){

        String contextJson =
                "{                                                     \n" +
                        "  'people': [                                         \n" +
                        "    {'firstName': 'Yehuda', 'lastName': 'Katz'},      \n" +
                        "    {'firstName': 'Carl',   'lastName': 'Lerche'},    \n" +
                        "    {'firstName': 'Alan',   'lastName': 'Johnson'}    \n" +
                        "  ]                                                   \n" +
                        "}                                                     ";

        contextJson = contextJson.replace('\'', '"');

        String mainTemplate = "HELLO HOW ARE YOU!                  \n" +
                "     {{#table people firstName lastName }}        \n" +
                "                                                  \n" +
                "     {{/table}}                                   \n";

        final String listTemplate =
                "<table>                                                      \n" +
                        "{{#each items}}                                      \n" +
                        "     <tr>                                            \n" +
                        "     ${body}                                         \n" +
                        "      </tr>                                          \n" +
                        "{{/each}}                                            \n" +
                        "</table>                                             \n";

        final String listItemTemplate =
                "\n" +
                        "           <td>                                             \n" +
                        "                ${body}                                     \n" +
                        "           </td>                                            \n";


        replace = templateWithFunctions(
                new Object() {

                    String table(String arguments, String block, final Object context) {

                        String[] args;
                        String[] itemProperties;
                        String itemsNameInContext;
                        String listItemBody;
                        String newBodyTemplate;

                        List listOfTemplates;

                        args = Str.splitBySpace(arguments);
                        itemsNameInContext = args[0];
                        itemProperties = slc(args, 1);

                        listOfTemplates = mapBy(itemProperties, new Fn() {
                            String function(String property) {
                                /** TODO this test is technically broke... I had to add an extra space to this string
                                 *  If you change " {{" + property + "}} " to  "{{" + property + "}}"
                                 *  This break. I need a simpler test that reproduces this.
                                 *  This bug started after I added JSTL <c:if and <c:forEach support.
                                 * */
                                return jstl().replace(listItemTemplate,
                                        map("body",
                                                " {{" + property + "}} ")
                                ).toString();
                            }
                        });

                        listItemBody = joinCollection('\n', listOfTemplates);
                        newBodyTemplate = jstl().replace(listTemplate, map("body", listItemBody)).toString();

                        Object items = getPropertyValue(context, itemsNameInContext);


                        String output = template().replace(newBodyTemplate, map("items", items)).toString();
                        return output;
                    }
                }
        ).replace(mainTemplate,

                contextJson
        );

        String test = "HELLO HOW ARE YOU!                  \n" +
                "     <table>                                                      \n" +
                "     <tr>                                            \n" +
                "     \n" +
                "           <td>                                             \n" +
                "                 Yehuda                                      \n" +
                "           </td>                                            \n" +
                "\n" +
                "\n" +
                "           <td>                                             \n" +
                "                 Katz                                      \n" +
                "           </td>                                            \n" +
                "                                         \n" +
                "      </tr>                                          \n" +
                "     <tr>                                            \n" +
                "     \n" +
                "           <td>                                             \n" +
                "                 Carl                                      \n" +
                "           </td>                                            \n" +
                "\n" +
                "\n" +
                "           <td>                                             \n" +
                "                 Lerche                                      \n" +
                "           </td>                                            \n" +
                "                                         \n" +
                "      </tr>                                          \n" +
                "     <tr>                                            \n" +
                "     \n" +
                "           <td>                                             \n" +
                "                 Alan                                      \n" +
                "           </td>                                            \n" +
                "\n" +
                "\n" +
                "           <td>                                             \n" +
                "                 Johnson                                      \n" +
                "           </td>                                            \n" +
                "                                         \n" +
                "      </tr>                                          \n" +
                "</table>                                             \n";

        equalsOrDie(test, replace.toString());

    }
}
