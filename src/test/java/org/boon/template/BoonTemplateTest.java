package org.boon.template;

import org.boon.Str;
import org.boon.core.Fn;
import org.boon.json.JsonFactory;
import org.junit.Test;

import java.util.List;

import static org.boon.Arrays.slc;
import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Lists.mapBy;
import static org.boon.Maps.map;
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
        ok  = replace.equals("Rick") || die(replace);

        replace = template().replace("{{name|Bob}}", map("name", "Rick"));
        ok &= replace.equals("Rick") || die(replace);



        replace = template().replace("{{name|Bob}}", niceJson("{'name':'Rick'}"));
        ok &= replace.equals("Rick") || die(replace);

    }

    @Test
    public void testDefault2() {


        replace = template().replace("{{this.name.firstName|Bob}}",
                                 new Person(new Name("Rick", "Hightower")));

        ok &= replace.equals("Rick") || die(replace);


        replace = template().replace("{{this/name/firstName|Bob}}",
                                 new Person(new Name("Sam", "Hightower")));

        ok &= replace.equals("Sam") || die(replace);


        replace = template().replace("{{name/firstName|Bob}}",
                new Person(new Name("RickyBobby", "Hightower")));

        ok &= replace.equals("RickyBobby") || die(replace);



        replace = template().replace("{{name/firstName|Bob}}",
                niceJson("{ 'name': { 'firstName':'RickyBobby', 'lastName':'Hightower' } }"));

        ok &= replace.equals("RickyBobby") || die(replace);

    }


    @Test
    public void testDefault3() {


        replace = template().replace("{{[1]/name/firstName|Bob}}",
                list(map("a", 1), list(
                        new Person(new Name("Diana", "Hightower")),
                        new Person(new Name("Rick", "Hightower"))

                ))
        );

        ok &= replace.equals("Rick") || die(replace);

    }

    @Test
    public void testIteration() {


        replace = template().replace(listTemplate, map("items", list("apple", "oranges", "pears")));

        puts (replace);

        replace = template().replace(listTemplate, map("items", map("apple", 1, "oranges", 2, "pears", 4)));

        puts (replace);


        replace = template().replace(listTemplate, niceJson("{'items' : ['apple', 'oranges', 'pears']}"));

        puts (replace);

        replace = template().replace(listTemplate, niceJson("{'items' : {'apple': 1, 'oranges': 2, 'pears': 4)}}"));

        puts (replace);

    }


    @Test
    public void test() {
        BoonTemplateTest.main();
    }


    public static void main (String... args) {
        CharSequence replace;

        replace = template().replace("Hello {{name}}! \n How are you {{name}}?", map("name", "Rick"));
        puts(replace);

        replace = template("${", "}").replace("Hello ${name}! \n How are you ${name}?", map("name", "Rick"));
        puts(replace);


        replace = template("$", " ").replace("Hello $name ! \n How are you $name ?", map("name", "Rick"));
        puts(replace);

        replace = template("[[", "]]").replace("Hello [[name]]! \n How are you [[name]]?", map("name", "Rick"));
        puts(replace);


        replace = template().replace("Hello {{{name}}}! \n How are you {{{name}}}?", map("name", "Rick"));
        puts(replace);


        replace = template().replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}?\n" +
                "{{/if}}\n Glad to hear it", map("name", "Rick"));
        puts (replace);




        replace = template().replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}? more text here\n" +
                "{{/if}}\n" +
                "" +
                "{{#each fruits}}\n" +
                "       {{this}}\n" +
                "{{/each}}"
                ,

                map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));


        puts(replace);

        puts("----");

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


        puts(replace);

        puts("----");


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


        puts(replace);

        puts("----");




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


        puts(replace);

        puts("----");



        replace = templateWithFunctions(
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


        puts(replace);

        puts("----");

        replace = templateWithFunctions(
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


        puts(replace);

        puts("----");


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


        String arguments = template("${", "}").replace("[${a},${b}]", map("a", 1, "b", 2)).toString();

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


        String contextJson =
                "{                                                     \n" +
                        "  'people': [                                         \n" +
                        "    {'firstName': 'Yehuda', 'lastName': 'Katz'},      \n" +
                        "    {'firstName': 'Carl',   'lastName': 'Lerche'},    \n" +
                        "    {'firstName': 'Alan',   'lastName': 'Johnson'}    \n" +
                        "  ]                                                   \n" +
                        "}                                                     ";

        contextJson = contextJson.replace('\'', '"');
        puts (contextJson);

        String mainTemplate = "HELLO HOW ARE YOU!                  \n" +
                "     {{#table people firstName lastName }}        \n" +
                "                                                  \n" +
                "     {{/table}}                                   \n";

        final String listTemplate =
                "<table>                                              \n" +
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


        replace = templateWithDynamicFunctions(
                new Object() {

                    String table(String arguments, String block, final Object context) {

                        String[] args;
                        String[] itemProperties;
                        String itemsNameInContext;
                        String listItemBody;
                        String newBodyTemplate;

                        List listOfTemplates;

                        args =          Str.splitBySpace(arguments);
                        itemsNameInContext =      args[0];
                        itemProperties =    slc(args, 1);

                        listOfTemplates = mapBy(itemProperties, new Fn() {
                            String function(String property) {
                                return jstl().replace(listItemTemplate,
                                        map("body",
                                                "{{" + property + "}}")
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


        puts(replace);

        puts("----");

    }

}
