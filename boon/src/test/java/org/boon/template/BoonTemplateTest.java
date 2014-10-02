package org.boon.template;

import org.boon.Boon;
import org.boon.Lists;
import org.boon.Maps;
import org.boon.Str;
import org.boon.primitive.Arry;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Maps.map;
import static org.boon.Str.equalsOrDie;
import static org.boon.json.JsonFactory.niceJson;


/**
 * Created by Richard on 9/15/14.
 */
public class BoonTemplateTest {


    Company company;

    BoonTemplate template;

    String replace;
    private boolean ok;


    List<Employee> employees;


    @Before
    public void setup() {

        final List<Dept> list = Lists.list(
                new Dept("Engineering", new Employee("Rick", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES)),
                new Dept("HR", new Employee("Diana", "Hightower", 2, new Phone("320", "555", "1212")))
        );

        company = new Company("Mammatus", list);
        template = new BoonTemplate();

        employees = Lists.list(
                new Employee("Rick", "Hightower", 1,
                    new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Adam", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Zed", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Ryan", "Hightower", 1,
                    new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES)
        );

    }

    @Test
    public void jsonList() {

        final String results = template.replace(
                "<c:for ['apple', 'orange', 'kiwi']> $item </c:for>"
                );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  kiwi #", "#"+results+"#");

    }

    @Test
    public void jsonListMustache() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace(
                "{{#for ['apple', 'orange', 'kiwi']}} {{item}} {{/for}}"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  kiwi #", "#"+results+"#");

    }

    @Test
    public void jsonList2() {

        final String results = template.replace(
                "<c:each items=\"['apple', 'orange', 'kiwi']\"> $item </c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  kiwi #", "#"+results+"#");

    }

    @Test
    public void jsonList2Mustache() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace(
                "{{#each items=\"['apple', 'orange', 'kiwi']\"}} {{item}} {{/each}}"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  kiwi #", "#"+results+"#");

    }

    @Test
    public void methodCall() {

        final String results = template.replace(
                "<c:each items=\"['apple', 'orange', 'kiwi']\"> $item $item.length()</c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple 5 orange 6 kiwi 4#", "#"+results+"#");

    }

    @Test
    public void methodCallMustache() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace(

                "{{#each items=\"['apple', 'orange', 'kiwi']\"}} {{item}} {{item.length()}}{{/each}}"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple 5 orange 6 kiwi 4#", "#"+results+"#");

    }

    @Test
    public void methodCallReplace() {

        final String results = template.replace(
                "<c:each items=\"['apple', 'orange', 'kiwi']\"> $item $item.replaceAll(i,a)</c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple apple orange orange kiwi kawa#", "#"+results+"#");

    }


    @Test
    public void methodCallReplaceMustache() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace(
                "{{#each ['apple', 'orange', 'kiwi'] }} {{item}} {{item.replaceAll(i,a)}}{{/each}}"

        );


        template.displayTokens();
        Boon.equalsOrDie("# apple apple orange orange kiwi kawa#", "#"+results+"#");

    }

    //@Test Create method object that handles overload type matching
    public void methodCallReplace2() {

        final String results = template.replace(
                "<c:each items=\"['apple', 'orange', 'kiwi']\"> $item $item.replace(i,a)</c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple apple orange orange kiwi kawa#", "#"+results+"#");

    }

    @Test
    public void jsonList3() {

        final String results = template.replace(
                "<c:each items='[\"apple\", \"orange\", \"kiwi\"]' var='x'> $x </c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  kiwi #", "#"+results+"#");

    }

    @Test
    public void jsonList4() {

        final String results = template.replace(
                "<c:each items=[apple,orange,kiwi] var='x'> $x </c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  kiwi #", "#"+results+"#");

    }

    @Test
    public void jsonList5() {

        final String results = template.replace(
                "<c:each items='[apple,orange,kiwi]' var='x'> $x </c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  kiwi #", "#"+results+"#");

    }

    @Test
    public void random() {

        final String results = template.replace(
                "<c:set var='foo' value=[apple,orange,pear] />" +
                        "<c:each items={{foo}} var='x'> $x </c:each>"
        );


        template.displayTokens();
        Boon.equalsOrDie("# apple  orange  pear #", "#"+results+"#");

    }

    @Test
    public void jsonMap() {

        final String results = template.replace(
                "<c:set var='employee' " +
                        "value=\"" +
                        "{" +
                        "      name:'Rick', \n" +
                        "      age:100," +
                        "      job:programmer" +
                        "}\" />" +
                        "" +
                        "${employee.name} ${employee.age}  ${employee.job}"
        );


        template.displayTokens();
        Boon.equalsOrDie("#Rick 100  programmer#", "#"+results+"#");

    }

    @Test
    public void jsonMapMustache() {

        template = new BoonTemplate(new BoonModernTemplateParser());
        final String results = template.replace(
                "{{#set var='employee' " +
                        "value=\"" +
                        "{" +
                        "      name:'Rick', \n" +
                        "      age:100," +
                        "      job:programmer" +
                        "}\" }} {{/set}}" +
                        "" +
                        "{{employee.name}} {{employee.age}}  {{employee.job}}"
        );


        template.displayTokens();
        Boon.equalsOrDie("#Rick 100  programmer#", "#"+results+"#");

    }


    @Test
    public void jsonMapStrict() {

        final String results = template.replace(
                "<c:set var='employee' " +
                        "value=\'" +
                        "{" +
                        "      \"name\":\"Rick\", \n" +
                        "      \"age\":100,\n" +
                        "      \"job\":\"programmer\"" +
                        "}\' />" +
                        "" +
                        "${employee.name} ${employee.age}  ${employee.job}"
        );


        template.displayTokens();
        Boon.equalsOrDie("#Rick 100  programmer#", "#"+results+"#");

    }


    @Test
    public void testCSetNoEndTag() {

        final String results = template.replace(
                "<c:set var=\"workplace\" value='${company}'/>" +
                        "${workplace.name}"
                ,

                Maps.map("string", "moon", "company", company));


        template.displayTokens();
        Boon.equalsOrDie("#Mammatus#", "#"+results+"#");

    }

    @Test
    public void testCSetNoEndTag2() {

        final String results = template.replace(
                "<c:set var=\"workplace\" value='${company}'> </c:set>" +
                        "${workplace.name}"
                ,

                Maps.map("string", "moon", "company", company));


        template.displayTokens();
        Boon.equalsOrDie("#Mammatus#", "#"+results+"#");

    }

    @Test
    public void testCSet2() {

        final String results = template.replace(
                "<c:set var=\"workplace\" value='${company}'" +
                        " target='${map.map2}' property='company' " +
                        ">  </c:set>" +
                        "${workplace.name} \n " +
                        "${map.map2.company.name}"
                ,
                Maps.map("string", "moon",
                        "company", company,
                        "map", Maps.map(
                                "map2", Maps.map("name", "map2")
                        )
                )
        );


        Boon.equalsOrDie("#Mammatus \n" +
                " Mammatus#", "#"+results+"#");

    }

    @Test
    public void testCSet2Mustache() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace(
                "{{#set var=\"workplace\" value='${company}'" +
                        " target='${map.map2}' property='company' " +
                        "}}  {{/set}}" +
                        "{{workplace.name}} \n " +
                        "{{map.map2.company.name}}"
                ,
                Maps.map("string", "moon",
                        "company", company,
                        "map", Maps.map(
                                "map2", Maps.map("name", "map2")
                        )
                )
        );


        Boon.equalsOrDie("#Mammatus \n" +
                " Mammatus#", "#"+results+"#");

    }

    @Test
    public void testCSet() {

        final String results = template.replace(
                "<c:set var=\"workplace\" value='${company}'>  </c:set>" +
                        "${workplace.name}"
                        ,

                Maps.map("string", "moon", "company", company));


        Boon.equalsOrDie("#Mammatus#", "#"+results+"#");

    }



    @Test
    public void testContains() {

        final String results = template.replace("<c:if test=$fn:contains(string,oon)>$string</c:if>",

                Maps.map("string", "moon"));


        Boon.equalsOrDie("moon", results);

    }

    @Test
    public void testContainsMustache() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace("{{#if test=$fn:contains(string,oon)}}{{$string}}{{/if}}",

                Maps.map("string", "moon"));


        Boon.equalsOrDie("moon", results);

    }

    @Test
    public void testContainsMustacheNoTest() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace("{{#if $fn:contains(string,oon)}}{{$string}}{{/if}}",

                Maps.map("string", "moon"));


        Boon.equalsOrDie("moon", results);

    }
    @Test
    public void testContainsQuote() {

        final String results = template.replace("<c:if test='$fn:contains(string, oon)'>$string</c:if>",

                Maps.map("string", "moon"));


        Boon.equalsOrDie("moon", results);

    }


    @Test
    public void testContains2() {

        final String results = template.replace("<c:if test='$fn:contains(string, zap)'>$string</c:if>",

                Maps.map("string", "moon"));


        Boon.equalsOrDie("", results);

    }


    @Test
    public void testContains4() {

        final String results = template.replace("<c:if test=$fn:contains(string,zap)>$string</c:if>",

                Maps.map("string", "moon"));


        Boon.equalsOrDie("", results);

    }


    @Test
    public void testContains5() {

        final String results = template.replace("<c:if test=${fn:contains(string, zap)}>$string</c:if>",

                Maps.map("string", "moon"));


        Boon.equalsOrDie("", results);

    }


    @Test
    public void companyObject() {

        final String results = template.replace("${this}", company);


        Boon.equalsOrDie(company.toString(), results);

    }

    @Test
    public void companyObjectMustache() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace("{{this}}", company);


        Boon.equalsOrDie(company.toString(), results);

    }


    @Test
    public void companyObjectBoonWay() {

        final String results = template.replace("$this", company);


        Boon.equalsOrDie(company.toString(), results);

    }

    @Test
    public void deptNames() {

        final String results = template.replace("${this.depts.name}", company);


        Boon.equalsOrDie("[Engineering, HR]", results);

    }

    @Test
    public void deptNamesMustache() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace("{{this.depts.name}}", company);


        Boon.equalsOrDie("[Engineering, HR]", results);

    }


    @Test
    public void deptNamesBoonWay() {

        final String results = template.replace("$this.depts.name", company);


        Boon.equalsOrDie("[Engineering, HR]", results);

    }

    @Test
    public void deptNamesForEach() {

        final String results = template.replace("<c:for items='$depts.name'>${item} </c:for>", company);


        Boon.equalsOrDie("#Engineering HR #", "#" + results + "#");

    }

    @Test
    public void deptNamesForEachBoonWay() {

        final String results = template.replace("<c:for items=$depts.name>$item </c:for>", company);


        Boon.equalsOrDie("#Engineering HR #", "#" + results + "#");

    }



    @Test
    public void deptNamesForEachBoonWayMustache() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace("{{#each items=depts.name }}" +
                "{{item}} " +
                "{{/each}}", company);


        Boon.equalsOrDie("#Engineering HR #", "#" + results + "#");

    }


    @Test
    public void deptNamesForEachBoonWayMustache1() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace("{{#each items=$depts.name }}" +
                "{{item}} " +
                "{{/each}}", company);


        Boon.equalsOrDie("#Engineering HR #", "#" + results + "#");

    }



    @Test
    public void deptNamesForEachBoonWayMustache2() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        final String results = template.replace("{{#each items=${depts.name} }}" +
                "{{item}} " +
                "{{/each}}", company);


        Boon.equalsOrDie("#Engineering HR #", "#" + results + "#");

    }



    @Test
    public void deptNamesForEachAgain() {

        final String results = template.replace("<c:for items='$depts'>${item.name} </c:for>", company);


        Boon.equalsOrDie("#Engineering HR #", "#" + results + "#");

    }


    @Test
    public void deptNamesForEachAgainBoonWay() {

        final String results = template.replace("<c:for items=$depts>$item.name </c:for>", company);


        Boon.equalsOrDie("#Engineering HR #", "#" + results + "#");

    }

    @Test
    public void manyNums() {

        final String results = template.replace("<c:for items='$list' step='3' >$item </c:for>",

                Maps.map("list", Lists.list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)));


        Boon.equalsOrDie("#0 3 6 9 12 15 18 #", "#" + results + "#");

    }

    @Test
    public void manyNumsJasonMaps() {

        final String results = template.replace("<c:for { items:$list, step:3 } >$item </c:for>",

                Maps.map("list", Lists.list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)));


        Boon.equalsOrDie("#0 3 6 9 12 15 18 #", "#" + results + "#");

    }

    @Test
    public void manyNumsJasonMapWithList() {

        final String results = template.replace("" +
                        "<c:for " +
                        "{ " +
                        "items:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, " +
                        "       11, 12, 13, 14, 15, 16, 17, 18], " +
                        "step:3 } >" +
                        "$item " +
                        "</c:for>");




        Boon.equalsOrDie("#0 3 6 9 12 15 18 #", "#" + results + "#");

    }

    @Test
    public void manyNumsUsingThis() {

        final String results = template.replace("<c:for items='$this' step='3' >$item </c:for>",
            Lists.list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18));


        Boon.equalsOrDie("#0 3 6 9 12 15 18 #", "#" + results + "#");

    }

    @Test
    public void deptNamesForEachAgainBegin() {

        final String results = template.replace("<c:for begin='1' items='$depts'>${item.name} </c:for>", company);


        Boon.equalsOrDie("#HR #", "#" + results + "#");

    }

    @Test
    public void deptNamesForEachAgainBeginBoonWay() {

        final String results = template.replace("<c:for items='$depts' begin=1 end=2>${item.name} </c:for>", company);


        Boon.equalsOrDie("#HR #", "#" + results + "#");

    }

    @Test
    public void deptNamesForEachWithEnd() {

        final String results = template.replace("<c:for end='1' items='$depts'>${item.name} </c:for>", company);


        Boon.equalsOrDie("#Engineering #", "#" + results + "#");

    }

    @Test
    public void deptNamesForEachAgainWithIndex() {

        final String results = template.replace("<c:for items='$depts'>$index $item.name" +
                "\n </c:for>", company);


        Boon.equalsOrDie("#0 Engineering\n" +
                " 1 HR\n" +
                " #", "#" + results + "#");

    }

    @Test
    public void deptNamesForEachAgainWithIndexAndEmployeesFirstName() {

        final String results = template.replace("\n<c:for items='$depts'>" +
                "   $index $item.name" +
                "\n " +
                "<c:for var='emp' items='$item.employees'>\t\t$index $emp.firstName\n</c:for>" +
                "</c:for>\n", company);


        Boon.equalsOrDie("#\n" +
                "   0 Engineering\n" +
                " \t\t0 Rick\n" +
                "\n" +
                "   1 HR\n" +
                " \t\t0 Diana\n" +
                "\n" +
                "#", "#" + results + "#");

    }

    @Test
    public void cForSortEmployees() {


        String results = template.replace("\n<c:for items='employees'> $this.firstName </c:for>",
                Maps.map("employees", employees));


        Boon.equalsOrDie("#\n" +
                " Rick  Adam  Zed  Lucas  Ryan #", "#" + results + "#");

        results = template.replace(
                "<c:for items='${fn:sortBy(employees,firstName)}'>$this.firstName</c:for>",
                Maps.map("employees", employees));

        Boon.equalsOrDie("#AdamLucasRickRyanZed#", "#" + results + "#");

    }


    @Test
    public void cForSortEmployeesWithURI() {

        employees = Lists.list(
                new Employee("Rick", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Adam", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Zed", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Ryan", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Smith", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES)

        );


        String results = template.replaceFromURI(
                "classpath://templates/employeeTemplate.jsp",
                Maps.map("employees", employees));

        Boon.equalsOrDie("#\n" +
                "    Adam, Hightower\n" +
                "\n" +
                "    Lucas, Hightower\n" +
                "\n" +
                "    Lucas, Smith\n" +
                "\n" +
                "    Rick, Hightower\n" +
                "\n" +
                "    Ryan, Hightower\n" +
                "\n" +
                "    Zed, Hightower\n" +
                "#", "#" + results + "#");


    }


    @Test
    public void cForSortEmployeesWithURI2() {

        employees = Lists.list(
                new Employee("Rick", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Adam", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Zed", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Ryan", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Smith", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES)

        );


        File file = new File(".");
        file.getAbsolutePath();

        String results = template.replaceFromURI(
                "file:" + file.getAbsolutePath() +
                        "/src//test/resources/templates/employeeTemplate.jsp",
                Maps.map("employees", employees));

        Boon.equalsOrDie("#\n" +
                "    Adam, Hightower\n" +
                "\n" +
                "    Lucas, Hightower\n" +
                "\n" +
                "    Lucas, Smith\n" +
                "\n" +
                "    Rick, Hightower\n" +
                "\n" +
                "    Ryan, Hightower\n" +
                "\n" +
                "    Zed, Hightower\n" +
                "#", "#" + results + "#");


    }

    @Test
    public void cForSortEmployeesWithResouce() {

        employees = Lists.list(
                new Employee("Rick", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Adam", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Zed", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Ryan", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Smith", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES)

        );


        String results = template.replaceFromResource(
                "/templates/employeeTemplate.jsp",
                Maps.map("employees", employees));

        Boon.equalsOrDie("#\n" +
                "    Adam, Hightower\n" +
                "\n" +
                "    Lucas, Hightower\n" +
                "\n" +
                "    Lucas, Smith\n" +
                "\n" +
                "    Rick, Hightower\n" +
                "\n" +
                "    Ryan, Hightower\n" +
                "\n" +
                "    Zed, Hightower\n" +
                "#", "#" + results + "#");


    }

    @Test
    public void cForSortEmployees2() {


        employees = Lists.list(
                new Employee("Rick", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Adam", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Zed", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Ryan", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES),
                new Employee("Lucas", "Smith", 1,
                    new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES)

        );

        String results = template.replace(
                "\n<c:for items='${fn:sortBy(employees, firstName, lastName)}'> ${this.firstName}, ${this.lastName}\n</c:for>",
                Maps.map("employees", employees));

        Boon.equalsOrDie("#\n" +
                " Adam, Hightower\n" +
                " Lucas, Hightower\n" +
                " Lucas, Smith\n" +
                " Rick, Hightower\n" +
                " Ryan, Hightower\n" +
                " Zed, Hightower\n" +
                "#", "#" + results + "#");


        results = template.replace(
                "\n<c:for items='${fn:sortBy(employees, firstName_desc, lastName)}'> ${this.firstName}, ${this.lastName}\n</c:for>",
                Maps.map("employees", employees));

        Boon.equalsOrDie("#\n" +
                " Zed, Hightower\n" +
                " Ryan, Hightower\n" +
                " Rick, Hightower\n" +
                " Lucas, Hightower\n" +
                " Lucas, Smith\n" +
                " Adam, Hightower\n" +
                "#", "#" + results + "#");


        results = template.replace(
                "\n<c:for items='${fn:sortBy(employees, firstName_desc, lastName_desc)}'> ${this.firstName}, ${this.lastName}\n</c:for>",
                Maps.map("employees", employees));

        Boon.equalsOrDie("#\n" +
                " Zed, Hightower\n" +
                " Ryan, Hightower\n" +
                " Rick, Hightower\n" +
                " Lucas, Smith\n" +
                " Lucas, Hightower\n" +
                " Adam, Hightower\n" +
                "#", "#" + results + "#");

    }



    @Test
    public void deptNamesForEachAgainWithIndexAndEmployeesFirstName2() {

        final String results = template.replace("\n<c:for var='dept'' items='$depts'>" +
                "   $index $dept.name" +
                "\n " +
                "<c:for  items='$dept.employees'>\t\t$index $item.firstName\n</c:for>" +
                "</c:for>\n", company);


        Boon.equalsOrDie("#\n" +
                "   0 Engineering\n" +
                " \t\t0 Rick\n" +
                "\n" +
                "   1 HR\n" +
                " \t\t0 Diana\n" +
                "\n" +
                "#", "#" + results + "#");

    }
    @Test
    public void test() {

        final String results = template.replace("${this.name}", company);


        Boon.equalsOrDie("Mammatus", results);

    }

    @Test
    public void test2() {

        final String results = template.replace("${this.name}", company);


        Boon.equalsOrDie("Mammatus", results);

    }

    @Test
    public void test3() {

        final String results = template.replace("${this.depts.name[0]}, ${this.depts.name[1]}", company);


        Boon.equalsOrDie("Engineering, HR", results);

    }


    @Test
    public void test4() {

        final String results = template.replace("<c:if test='true'> ${name} </c:if>" , company);


        Boon.equalsOrDie("# Mammatus #", "#"+results+"#");

    }


    @Test
    public void test5() {

        final String results = template.replace("<c:if test='false'> ${name} </c:if>" , company);


        template.displayTokens();

        Boon.equalsOrDie("##", "#"+results+"#");

    }

    @Test
    public void test6() {

        final String results = template.replace("<c:if test='${flag}'> ${name} </c:if>" ,
                Maps.map("flag", false), company);


        Boon.equalsOrDie("##", "#"+results+"#");

    }


    @Test
    public void test7() {

        final String results = template.replace("<c:if test='flag'> ${name} </c:if>" ,

                Maps.map("flag", true), company);


        Boon.equalsOrDie("# Mammatus #", "#"+results+"#");

    }

    @Test
    public void test8() {

        final String results = template.replace("<c:if test='flag'> $name </c:if>" ,
                Maps.map("flag", true), company);


        Boon.equalsOrDie("# Mammatus #", "#"+results+"#");

    }

    @Test
    public void test9() {

        final String results = template.replace("<c:if test='$flag'>$name</c:if>" ,
                Maps.map("flag", false), company);


        Boon.equalsOrDie("##", "#"+results+"#");

    }
    @Test
    public void test10() {

        final String results = template.replace("<c:if test='$flag'>$name</c:if>" ,

                    Maps.map("flag", true), company);


        Boon.equalsOrDie("#Mammatus#", "#"+results+"#");

    }


    @Test
    public void testNoQuotes() {

        final String results = template.replace("<c:if test=$flag> ${name} </c:if>" ,
                Maps.map("flag", false), company);


        Boon.equalsOrDie("##", "#"+results+"#");

    }

    @Test
    public void test8TightIf() {

        final String results = template.replace("<c:if test='flag'>${name}</c:if>" ,
            Maps.map("flag", true), company);


        Boon.equalsOrDie("#Mammatus#", "#"+results+"#");

    }


    @Test
    public void nestedIfIf() {

        final String results = template.replace(
                "<c:if test='${flag}'>" +
                        "<c:if test='${flag}'>bobby</c:if>" +
                "</c:if> sue" ,

                Lists.list(Maps.map("flag", true), company));


        Boon.equalsOrDie("#bobby sue#", "#"+results+"#");

    }



    @Test
    public void nestedIfNoExpression() {

        final String results = template.replace(
                "<c:if test='${flag}'>" +
                        "<c:if test='flag'>bobby</c:if>" +
                        "</c:if> sue" ,

                Lists.list(Maps.map("flag", true), company));


        Boon.equalsOrDie("#bobby sue#", "#"+results+"#");

    }

    @Test
    public void nestedIfIfWithExpression() {

        final String results = template.replace(
                "<c:if test='${flag}'>" +
                        "123\n 123~<c:if test='${flag}'> 123 123 ~${name}</c:if>" +
                        "</c:if> sue" ,

                Maps.map("flag", true), company);


        Boon.equalsOrDie("#123\n" +
                " 123~ 123 123 ~Mammatus sue#", "#"+results+"#");

    }

    @Test
    public void testDefaultExpressionValueFound() {

        final String results = template.replace(
                "${boo|BAZ}" ,
                Maps.map("boo", true), company);


        Boon.equalsOrDie("#true#", "#"+results+"#");

    }


    @Test
    public void testDefaultExpressionValueNOTFound() {

        final String results = template.replace(
                "${boo|BAZ}" ,
                Maps.map("baz", true), company);

        Boon.equalsOrDie("#BAZ#", "#"+results+"#");

    }



    @Test
    public void cFor() {

        final String results = template.replace(
                "<c:loop items='${list}'}' var='foo'>${foo} </c:loop>" ,

                Maps.map("list", Lists.list("apple", "orange", "b")));


        Boon.equalsOrDie("#apple orange b #", "#"+results +"#");

    }

    @Test
    public void cForSort() {

        final String results = template.replace(
                "<c:loop items='${fn:sort(list)}'}' var='foo'>${foo} </c:loop>" ,

                Maps.map("list", Lists.list("apple", "orange", "b")));


        Boon.equalsOrDie("#apple b orange #", "#"+results +"#");

    }
    @Test
    public void cIf() {

        final String results = template.replace(
                "<c:if list> list </c:if>" ,

                Maps.map("list", Lists.list("apple", "orange", "b")));


        Boon.equalsOrDie("# list #", "#"+results +"#");

    }

    @Test
    public void cIf2() {

        final String results = template.replace(
                "<c:if myTest list> $list </c:if>" ,

                Maps.map("list", Lists.list("apple", "orange", "b"), "myTest", "true"));


        Boon.equalsOrDie("# [apple, orange, b] #", "#"+results +"#");

    }


    @Test
    public void cIf3() {

        final String results = template.replace(
                "<c:if myTest list myBoo> $list </c:if>" ,

                Maps.map("list",
                        Lists.list("apple", "orange", "b"),
                        "myTest", "true", "myBoo", "false"));


        Boon.equalsOrDie("##", "#"+results +"#");

    }


    @Test
    public void cIf4() {

        final String results = template.replace(
                "<c:if myTest list myBoo> $list </c:if>" ,

                Maps.map("list",
                        Lists.list("apple", "orange", "b"),
                        "myTest", "true", "myBoo", Lists.list(1)));


        Boon.equalsOrDie("# [apple, orange, b] #", "#"+results +"#");

    }

    @Test
    public void cIf5() {

        final String results = template.replace(
                "<c:if myTest list myBoo> $list </c:if>" ,

                Maps.map("list",
                        Lists.list("apple", "orange", "b"),
                        "myTest", "true", "myBoo", Lists.list(null, null, null)));


        Boon.equalsOrDie("##", "#"+results +"#");

    }

    @Test
    public void cIf6() {

        final String results = template.replace(
                "<c:if myTest list myBoo> $list </c:if>" ,

                Maps.map("list",
                        Lists.list("apple", "orange", "b"),
                        "myTest", "true", "myBoo", Lists.list()));


        Boon.equalsOrDie("##", "#"+results +"#");

    }

    @Test
    public void cForStatus() {

        final String results = template.replace(
                "<c:loop items='${list}'}'>\n${status.index} ${status.count} ${item} ${status.last} ${status.first}\n</c:loop>" ,

                Maps.map("list", Lists.list("apple", "orange", "banana")));


        Boon.equalsOrDie("#\n" +
                "0 3 apple false true\n" +
                "\n" +
                "1 3 orange false false\n" +
                "\n" +
                "2 3 banana true false\n" +
                "#", "#" +results +"#");

    }




    @Test
    public void cForWithVar() {

        final String results = template.replace(
                "<c:loop var=\"fruit\" items='${list}'}'>${fruit} </c:loop>" ,
        Maps.map("list", Lists.list("apple", "orange", "b")));


        Boon.equalsOrDie("#apple orange b #", "#"+results +"#");

    }



    @Test
    public void cForNested() {

        final String results = template.replace(
                "<c:forEach var=\"currentList\" items='${lists}'}'>" +
                        "\nList: ${currentList}\n" +
                        "<c:forEach var='currentItem' items='${currentList}'>" +
                        "   \tItem: ${currentItem}\t" +
                        "</c:forEach>" +
                        "\n------------\n"+
                 "</c:for>" ,

                 Maps.map(
                                    "lists",
                                    Lists.list(
                                        Lists.list("a1", "a2", "a3"),
                                        Lists.list("b1", "b2", "b3")

                                    )


                            )
                        )
                ;


        Boon.equalsOrDie("#\n" +
                "List: [a1, a2, a3]\n" +
                "   \tItem: a1\t   \tItem: a2\t   \tItem: a3\t\n" +
                "------------\n" +
                "\n" +
                "List: [b1, b2, b3]\n" +
                "   \tItem: b1\t   \tItem: b2\t   \tItem: b3\t\n" +
                "------------\n" +
                "#", "#"+results +"#");

    }





    public static enum Fruit {
        ORANGES,
        APPLES,
        STRAWBERRIES
    }
    public static class Phone {
        String areaCode;
        String countryCode;
        String number;

        public Phone(String areaCode, String countryCode, String number) {
            this.areaCode = areaCode;
            this.countryCode = countryCode;
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Phone)) return false;

            Phone phone = (Phone) o;

            if (areaCode != null ? !areaCode.equals(phone.areaCode) : phone.areaCode != null) return false;
            if (countryCode != null ? !countryCode.equals(phone.countryCode) : phone.countryCode != null) return false;
            if (number != null ? !number.equals(phone.number) : phone.number != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = areaCode != null ? areaCode.hashCode() : 0;
            result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
            result = 31 * result + (number != null ? number.hashCode() : 0);
            return result;
        }
    }


    public static class Employee {
        List<Fruit> fruits;

        String firstName;
        String lastName;
        int empNum;
        Phone phone;

        public Employee(String firstName, String lastName, int empNum, Phone phone, Fruit... fruits) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.empNum = empNum;
            this.phone = phone;
            this.fruits = Lists.list(fruits);

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Employee)) return false;

            Employee employee = (Employee) o;

            if (empNum != employee.empNum) return false;
            if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
            if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;
            if (phone != null ? !phone.equals(employee.phone) : employee.phone != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = fruits != null ? fruits.hashCode() : 0;
            result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            result = 31 * result + empNum;
            result = 31 * result + (phone != null ? phone.hashCode() : 0);
            return result;
        }
    }

    public static class Dept {
        String name;

        Employee[] employees;

        public Dept(String name, Employee... employees) {
            this.name = name;
            this.employees = employees;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Dept)) return false;

            Dept dept = (Dept) o;

            if (!Arry.equals(employees, dept.employees)) return false;
            if (name != null ? !name.equals(dept.name) : dept.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (employees != null ? Arrays.hashCode(employees) : 0);
            return result;
        }
    }



    public static class Company {
        String name;

        List<Dept> depts;

        public Company(String name, List<Dept> depts) {
            this.name = name;
            this.depts = depts;
        }

        @Override
        public String toString() {
            return "Company{" +
                    "name='" + name + '\'' +
                    ", depts=" + depts +
                    '}';
        }
    }


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
    public void testDefault3() {

        template = new BoonTemplate(new BoonModernTemplateParser());



        replace = template.replace("{{1/name/firstName|Bob}}",
                map("a", 1), list(
                        new Person(new Name("Diana", "Hightower")),
                        new Person(new Name("Rick", "Hightower"))

                )
        );


        equalsOrDie("Rick", replace);

    }

    @Test
    public void testDefault2() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        replace = template.replace("{{name.firstName|Bob}}",
                new Person(new Name("Rick", "Hightower")));

        equalsOrDie("Rick", replace);


        replace = template.replace("{{this/name/firstName|Bob}}",
                new Person(new Name("Sam", "Hightower")));

        equalsOrDie("Sam", replace);


        replace = template.replace("{{name/firstName|Bob}}",
                new Person(new Name("RickyBobby", "Hightower")));

        equalsOrDie("RickyBobby", replace);



        replace = template.replace("{{name/firstName|Bob}}",
                niceJson("{ 'name': { 'firstName':'RickyBobby', 'lastName':'Hightower' } }"));

        equalsOrDie("RickyBobby", replace);

    }


    @Test
    public void testDefault() {


        template = new BoonTemplate(new BoonModernTemplateParser());
        replace = template.replace("{{name|Rick}}", null);
        equalsOrDie("Rick", replace.toString());

        replace = template.replace("{{name|Bob}}", map("name", "Rick"));
        equalsOrDie("Rick", replace);

        replace = template.replace("{{name|Bob}}", niceJson("{'name':'Rick'}"));
        equalsOrDie("Rick", replace);

    }







    String listTemplate = "\n{{#each items}}" +

            " this {{this}}, index {{@index}}, key {{@key}}, first {{@first}}, last {{@last}}\n" +

            "{{/each}}";

    @Test
    public void testIterationList() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        replace = template.replace(listTemplate,
                map("items", list("apple", "oranges", "pears")));

        equalsOrDie("\n" +
                " this apple, index 0, key @key, first true, last false\n" +

                " this oranges, index 1, key @key, first false, last false\n" +

                " this pears, index 2, key @key, first false, last true\n"
                , replace);
    }

    @Test
    public void testIterationMap() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        replace = template.replace(listTemplate,
                map("items", map("apple", 1, "oranges", 2, "pears", 4)));
        String test = "\n" +
                " this 1, index 0, key apple, first true, last false\n" +
                " this 2, index 1, key oranges, first false, last false\n" +
                " this 4, index 2, key pears, first false, last true\n" +
                "";
        equalsOrDie(test, replace);
    }

    @Test
    public void testIterationJSON1() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        Object context =
                niceJson("{'items' : ['apple', 'oranges', 'pears']}");

        replace = template.replace(listTemplate, context);
        String test = "\n" +
                " this apple, index 0, key @key, first true, last false\n" +
                " this oranges, index 1, key @key, first false, last false\n" +
                " this pears, index 2, key @key, first false, last true\n";
        equalsOrDie(test, replace);
    }

    @Test
    public void testIterationJSON2() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        String listTemplate = "\n{{#each ['apple', 'oranges', 'pears']}}" +

                " this {{this}}, index {{@index}}, key {{@key}}, first {{@first}}, last {{@last}}\n" +

                "{{/each}}";


        replace = template.replace(listTemplate);
        String test = "\n" +
                " this apple, index 0, key @key, first true, last false\n" +
                " this oranges, index 1, key @key, first false, last false\n" +
                " this pears, index 2, key @key, first false, last true\n";
        equalsOrDie(test, replace);
    }


    String jstListTemplate = "\n<c:forEach items=\"items\">" +
            " this ${this}, index ${@index}, key ${@key}, first ${@first}, last ${@last}" +
            "\n</c:forEach>";


    @Test
    public void testIterationListJSTL() {


        replace = template.replace(jstListTemplate,
                map("items", list("apple", "oranges", "pears")));

        equalsOrDie("\n" +
                " this apple, index 0, key @key, first true, last false\n" +

                " this oranges, index 1, key @key, first false, last false\n" +

                " this pears, index 2, key @key, first false, last true\n"
                , replace);
    }


    @Test
    public void testIterationMap_JSTL() {


        replace = template.replace(jstListTemplate,
                map("items", map("apple", 1, "oranges", 2, "pears", 4)));
        String test = "\n" +
                " this 1, index 0, key apple, first true, last false\n" +
                " this 2, index 1, key oranges, first false, last false\n" +
                " this 4, index 2, key pears, first false, last true\n" +
                "";
        equalsOrDie(test, replace);
    }

    @Test
    public void testIterationJSON1_JSTL() {


        Object context =
                niceJson("{'items' : ['apple', 'oranges', 'pears']}");

        replace = template.replace(jstListTemplate, context);
        String test = "\n" +
                " this apple, index 0, key @key, first true, last false\n" +
                " this oranges, index 1, key @key, first false, last false\n" +
                " this pears, index 2, key @key, first false, last true\n";
        equalsOrDie(test, replace);
    }


    @Test
    public void testIterationJSON2_JSTL() {



        String jstListTemplate = "\n<c:forEach ['apple', 'oranges', 'pears']>" +
                " this ${this}, index ${@index}, key ${@key}, first ${@first}, last ${@last}" +
                "\n</c:forEach>";


        replace = template.replace(jstListTemplate);
        String test = "\n" +
                " this apple, index 0, key @key, first true, last false\n" +
                " this oranges, index 1, key @key, first false, last false\n" +
                " this pears, index 2, key @key, first false, last true\n";
        equalsOrDie(test, replace);
    }


    @Test
    public void forloopNoSpaces() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        replace = template.replace("{{#each items}}{{this}}{{/each}}", map("items", list(1, 2, 3, 4, 5)));
        equalsOrDie("12345", replace);

    }

    @Test
    public void forloopNoSpaceDoubleThis() {

        template = new BoonTemplate(new BoonModernTemplateParser());

        replace = template.replace("{{#each this}}{{this}}{{/each}}", list(1, 2, 3, 4, 5));
        equalsOrDie("12345", replace);

    }


    @Test
    public void simpleIf() {

        template = new BoonTemplate(new BoonModernTemplateParser());


        replace = template.replace("{{#if name}}\n" +
                "{{name}}\n" +
                "{{/if}}", map("name", "Rick"));


        equalsOrDie("\nRick\n", replace);

    }

    @Test
    public void simpleJstlIf() {


        replace = template.replace("<c:if test=true>\n${name}\n\n</c:if>",
                map("name", "Rick"));


        equalsOrDie("\nRick\n\n", replace);

    }

    @Test
    public void simpleJstlIf2() {


        replace = template.replace("<c:if test='true'>\n${name}\n\n</c:if>",
                map("name", "Rick"));


        equalsOrDie("\nRick\n\n", replace);

    }

    @Test
    public void simpleJstlIf3() {


        replace = template.replace("<c:if test=\"true\">\n${name}\n\n</c:if>",
                map("name", "Rick"));


        equalsOrDie("\nRick\n\n", replace);

    }

    @Test
    public void simpleJstlIf4() {


        replace = template.replace("<c:if test=\"name\">\n${name}\n\n</c:if>",
                map("name", "Rick"));


        equalsOrDie("\nRick\n\n", replace);

    }

    @Test
    public void simpleJstlIf5() {


        replace = template.replace("<c:if test=\"name2\">\n${name}\n\n</c:if>",
                map("name", "Rick", "name2", ""));


        ok = Str.isEmpty(replace) || die(replace);
    }

    @Test
    public void simpleJstlIf6() {


        replace = template.replace("<c:if test=\"name2\">\n${name}\n\n</c:if>",
                map("name", "Rick", "name2", null));


        ok = Str.isEmpty(replace) || die(replace);
    }





    @Test
    public void simpleJstlIf7() {


        replace = template.replace("<c:if test=\"name\">${name}</c:if>", map("name", "Rick"));


        equalsOrDie(replace, "Rick");

    }



    @Test
    public void ifTestTextAfterCloseIf() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        replace = template.replace("{{#if name}}\n" +
                "Hello {{name}}!\n" +
                "How are you {{name}}?" +
                "{{/if}}\n Glad to hear it", map("name", "Rick"));

        equalsOrDie("\nHello Rick!\n" +
                "How are you Rick?\n" +
                " Glad to hear it", replace);

    }


    @Test
    public void happyDay() {

        template = new BoonTemplate(new BoonModernTemplateParser());
        replace = template.replace("Hello {{name}}! \n How are you {{name}}?", map("name", "Rick"));

        equalsOrDie("Hello Rick! \n" +
                " How are you Rick?", replace);


    }

    @Test
    public void tripleThreat() {


        template = new BoonTemplate(new BoonModernTemplateParser());
        replace = template.replace("Hello {{{name}}}! \n How are you {{{name}}}?", map("name", "Rick"));

        ok = replace.equals("Hello Rick! \n" +
                " How are you Rick?") || die(replace);

    }


    @Test
    public void tripleThreat3() {


        template = new BoonTemplate(new BoonModernTemplateParser());
        replace = template.replace("Hello {{{name}}}! \n How are you {{{name}}}?", map("name", "<Rick"));

        ok = replace.equals("Hello &lt;Rick! \n" +
                " How are you &lt;Rick?") || die(replace);

    }


    @Test
    public void eachAfterIf() {


        template = new BoonTemplate(new BoonModernTemplateParser());

        replace = template.replace("{{#if name}}\n" +
                        "Hello {{name}}!\n" +
                        "How are you {{name}}? more text here\n" +
                        "{{/if}}" +
                        "\n" +
                        "{{#each fruits}}" +
                        "       {{this}}\n" +
                        "{{/each}}"
                ,

                map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));

        String test = "\nHello Rick!\n" +
                "How are you Rick? more text here\n" +
                "\n" +
                "       apples\n" +
                "       pairs\n" +
                "       tangerines\n";

        equalsOrDie(test, replace);


    }


    /* ELSE is not implemented yet so I commented all of these old tests out. */

// Broken test.. if is an or... if is now an and
//    @Test
//    public void testMultiArgs() {
//
//        template = new BoonTemplate(new BoonModernTemplateParser());
//
//        replace = template.replace("{{#if name flea}}\n" +
//                "{{name}}\n" +
//                "{{else}}\n" +
//                "duck\n" +
//                "{{/if}}", map("name", "Rick", "flea", "bee"));
//
//        equalsOrDie("Rick\n", replace);
//
//
//    }
//
//
//
//
//
//    @Test
//    public void ifElseTest1() {
//
//
//
//        replace = template().replace("" +
//                "\n{{#if name}}\n" +
//                "{{name}}\n" +
//                "{{else}}\n" +
//                "duck\n" +
//                "{{/if}}", map("name", "Rick"));
//
//
//        equalsOrDie(replace, "\nRick\n");
//
//    }
//
//
//    @Test
//    public void ifElseTest2() {
//
//
//
//
//        replace = template().replace("" +
//                "\n{{#if name}}\n" +
//                "{{name}}\n" +
//                "{{else}}\n" +
//                "duck\n" +
//                "{{/if}}", map("duck", "Rick"));
//
//        equalsOrDie("\nduck\n", replace);
//
//    }
//
//    @Test
//    public void testMultiArgsExpression() {
//        replace = template().replace("{{#if name ${flea} }}\n" +
//                "{{name}}\n" +
//                "{{else}}\n" +
//                "duck\n" +
//                "{{/if}}", map("name", "Rick", "flea", "boo", "boo", "baz"));
//
//
//        equalsOrDie("Rick\n", replace);
//
//    }
//
//
//    @Test
//    public void testMultiArgsExpression3() {
//        replace = template().replace("{{#if name gt ${flea} }}\n" +
//                "{{name}} {{test}}\n" +
//                "{{else}}\n" +
//                "duck {{test}}\n" +
//                "{{/if}}", map("name", "Rick", "flea", "boo"));
//
//        equalsOrDie("Rick [name, gt, boo, Rick, gt, boo]\n", replace);
//
//
//    }
//
//    @Test
//    public void testMultiArgsExpression2() {
//        replace = template().replace("{{#if ['name', '${flea}'] }}\n" +
//                "{{name}} {{test}}\n" +
//                "{{else}}\n" +
//                "duck\n" +
//                "{{/if}}", map("name", "Rick", "flea", "boo", "boo", "baz"));
//
//        equalsOrDie("Rick [name, boo]\n", replace);
//
//
//    }


    //NOT SURE IF I WANT TO IMPLEMENT THIS FEATURE
//
//    @Test
//    public void eachFromJSON() {
//        replace = template().replace("{{#each ['name', '${flea}'] }}\n" +
//                "\n{{item}}\n" +
//
//                "{{/each}}", map("name", "Rick", "flea", "boo", "boo", "baz"));
//
//        equalsOrDie("\nname\n\nboo\n", replace);
//
//
//    }

    //CHANGE DELIMS WILL BE A FEATURE OF THE MODERN PARSER, aka handlebar, aka mustache

//    @Test
//    public void changeDelimiters() {
//        replace = template("[[", "]]").replace("Hello [[name]]! \n How are you [[name]]?", map("name", "Rick"));
//
//        equalsOrDie("Hello Rick! \n" +
//                " How are you Rick?", replace);
//
//    }


//
//    @Test
//    public void moreBasicTest() {
//        replace = template("${", "}").replace("Hello ${name}! \n How are you ${name}?", map("name", "Rick"));
//        ok = replace.equals("Hello Rick! \n" +
//                " How are you Rick?") || die(replace);
//
//        replace = template("$", " ").replace("Hello $name ! \n How are you $name ?", map("name", "Rick"));
//
//        ok = replace.equals("Hello Rick! \n" +
//                " How are you Rick?") || die(replace);
//
//    }


    //MORE TESTS I have not ported over

//
//    @Test
//    public void each() {
//
//        replace = template().replace(
//                "{{#each fruits}}\n" +
//                        "       {{this}}\n" +
//                        "{{/each}}"
//                ,
//
//                map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));
//
//        equalsOrDie("       apples\n" +
//                "       pairs\n" +
//                "       tangerines\n", replace.toString());
//
//    }
//
//
//    @Test
//    public void eachNoNewLine() {
//
//        replace = template().replace(
//                "{{#each fruits}} {{this}} {{/each}}",
//                map("name", "Rick", "fruits", list("apples", "pairs", "tangerines")));
//
//        equalsOrDie(replace.toString(), " apples " +
//                " pairs " +
//                " tangerines ");
//
//    }
//
//
//    @Test
//    public void moreComplicatedForEachOverMaps() {
//
//        replace = template().replace("{{#if name}}\n" +
//                        "Hello {{name}}!\n" +
//                        "How are you {{name}}? more text here\n" +
//                        "{{/if}}\n" +
//                        "<ol>\n" +
//                        "{{#each foods}}\n" +
//                        "       <li>{{name}}</li>\n" +
//                        "{{/each}}\n" +
//                        "</o>"
//                ,
//
//                map("name", "Rick", "foods", list(
//                        map("name", "pizza"),
//                        map("name", "fish"),
//                        map("name", "fruit")
//                )));
//
//
//
//        String str = "Hello Rick!\n" +
//                "How are you Rick? more text here\n" +
//                "<ol>\n" +
//                "       <li>pizza</li>\n" +
//                "       <li>fish</li>\n" +
//                "       <li>fruit</li>\n" +
//                "</o>";
//
//
//
//
//        equalsOrDie(str, replace);
//    }
//
//
//
//    @Test
//    public void someTestEachExspressionNotInContext() {
//
//        replace = template().replace("{{#if name}}\n" +
//                        "Hello {{name}}!\n" +
//                        "How are you {{name}}? more text here\n" +
//                        "{{/if}}\n" +
//                        "<ol>\n" +
//                        "{{#each foodszzzz}}\n" +
//                        "       <li>{{name}}</li>\n" +
//                        "{{/each}}\n" +
//                        "</o>"
//                ,
//
//                map("name", "Rick", "foods", list(
//                        map("name", "pizza"),
//                        map("name", "fish"),
//                        map("name", "fruit")
//                )));
//
//
//        equalsOrDie("Hello Rick!\n" +
//                "How are you Rick? more text here\n" +
//                "<ol>\n" +
//                "       <li>Rick</li>\n" +
//                "</o>", replace);
//
//    }
//
//
//    @Test
//    public void ifFollowedByEachFollowedByWith() {
//        replace = template().replace("{{#if name}}\n" +
//                        "Hello {{name}}!\n" +
//                        "How are you {{name}}? more text here\n" +
//                        "{{/if}}\n" +
//                        "<ol>\n" +
//                        "{{#each foods}}\n" +
//                        "       <li>{{name}}</li>\n" +
//                        "{{/each}}\n" +
//                        "</o>\n" +
//                        "{{#with rick}}\n" +
//                        "{{name}}\n" +
//                        "{{/with}}"
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
//        equalsOrDie("Hello Rick!\n" +
//                "How are you Rick? more text here\n" +
//                "<ol>\n" +
//                "       <li>pizza</li>\n" +
//                "       <li>fish</li>\n" +
//                "       <li>fruit</li>\n" +
//                "</o>\n" +
//                "Rick\n", replace);
//
//
//    }
//
//
//    @Test
//    public void creatingACommandBodyTagThatTakesABody() {
//        replace = templateWithFunctions(
//                new Object() {
//
//                    String add(int a, int b, String var, CharSequence block, Object context) {
//                        CharSequence blockOutput = template()
//                                .replace(block, list(map(var, a + b), context));
//                        return blockOutput.toString();
//
//                    }
//
//                }
//
//        ).replace("{{#if name}}\n" +
//                        "Hello {{name}}!\n" +
//                        "How are you {{name}}? more text here\n" +
//                        "{{/if}}\n" +
//                        "<ol>\n" +
//                        "{{#each foods}}\n" +
//                        "       <li>{{name}}</li>\n" +
//                        "{{/each}}\n" +
//                        "</o>\n" +
//                        "{{#add [${a} ,${b} ,'out']}}\n" +
//                        "ADD: {{out}}\n" +
//                        "{{/add}}",
//
//                map(    "name", "Rick",
//                        "a", 1,
//                        "b", 5,
//                        "foods",
//                        list(
//                                map("name", "pizza"),
//                                map("name", "fish"),
//                                map("name", "fruit")
//                        ),
//                        "rick", map("name", "Rick Hightower")
//                ));
//
//
//        equalsOrDie("Hello Rick!\n" +
//                "How are you Rick? more text here\n" +
//                "<ol>\n" +
//                "       <li>pizza</li>\n" +
//                "       <li>fish</li>\n" +
//                "       <li>fruit</li>\n" +
//                "</o>\n" +
//                "ADD: 6\n", replace);
//
//
//    }
//
//
//    @Test
//    public void simpleCommandBodyHandler() {
//
//
//
//        replace = templateWithFunctions(
//                new Object() {
//
//                    String add(int a, int b, String var, String block) {
//
//                        return sputs(var, "=", (a + b));
//                    }
//                }
//        ).replace("{{#if name}}\n" +
//                        "Hello {{name}}!\n" +
//                        "How are you {{name}}? more text here\n" +
//                        "{{/if}}\n" +
//                        "<ol>\n" +
//                        "{{#each foods}}\n" +
//                        "       <li>{{name}}</li>\n" +
//                        "{{/each}}\n" +
//                        "</o>\n" +
//                        "{{#add [${a} ,${b} ,'out']}}\n" +
//                        "ADD: {{out}}\n" +
//                        "{{/add}}" +
//                        "<ol>\n" +
//                        "{{#each foods}}\n" +
//                        "       <li>{{name}}</li>\n" +
//                        "{{/each}}\n" +
//                        "</o>\n"
//
//                ,
//
//                map(    "name", "Rick",
//                        "a", 1,
//                        "b", 5,
//                        "foods",
//                        list(
//                                map("name", "pizza"),
//                                map("name", "fish"),
//                                map("name", "fruit")
//                        ),
//                        "rick", map("name", "Rick Hightower")
//                ));
//
//
//
//        equalsOrDie("Hello Rick!\n" +
//                "How are you Rick? more text here\n" +
//                "<ol>\n" +
//                "       <li>pizza</li>\n" +
//                "       <li>fish</li>\n" +
//                "       <li>fruit</li>\n" +
//                "</o>\n" +
//                "out = 6\n" +
//                "       <li>pizza</li>\n" +
//                "       <li>fish</li>\n" +
//                "       <li>fruit</li>\n" +
//                "</o>\n", replace);
//
//    }
//
//    @Test
//    public void workingWithJsonAsContext() {
//        String json = toJson(map(    "name", "Rick",
//                "a", 50,
//                "b", 5,
//                "foods",
//                list(
//                        map("name", "pizza"),
//                        map("name", "fish"),
//                        map("name", "fruit")
//                ),
//                "rick", map("name", "Rick Hightower")
//        ));
//
//
//        replace = templateWithFunctions(
//                new Object() {
//
//                    String add(int a, int b, String var, String block) {
//
//                        return sputs(var, "=", (a + b));
//                    }
//                }
//        ).replace("{{#if name}}\n" +
//                        "Hello {{name}}!\n" +
//                        "How are you {{name}}? more text here\n" +
//                        "{{/if}}\n" +
//                        "<ol>\n" +
//                        "{{#each foods}}\n" +
//                        "       <li>{{name}}</li>\n" +
//                        "{{/each}}\n" +
//                        "</o>\n" +
//                        "{{#add [${a} ,${b} ,'out']}}\n" +
//                        "ADD: {{out}}\n" +
//                        "{{/add}}",
//
//                json
//        );
//
//
//
//
//        String test = "Hello Rick!\n" +
//                "How are you Rick? more text here\n" +
//                "<ol>\n" +
//                "       <li>pizza</li>\n" +
//                "       <li>fish</li>\n" +
//                "       <li>fruit</li>\n" +
//                "</o>\n" +
//                "out = 55\n";
//
//
//
//        equalsOrDie(test, replace);
//
//    }
//
//    @Test public void complexTestThatShowsJSONNestedMapsCustomBodyHandlersAndOtherFunStuff(){
//
//        String contextJson =
//                "{                                                     \n" +
//                        "  'people': [                                         \n" +
//                        "    {'firstName': 'Yehuda', 'lastName': 'Katz'},      \n" +
//                        "    {'firstName': 'Carl',   'lastName': 'Lerche'},    \n" +
//                        "    {'firstName': 'Alan',   'lastName': 'Johnson'}    \n" +
//                        "  ]                                                   \n" +
//                        "}                                                     ";
//
//        contextJson = contextJson.replace('\'', '"');
//
//        String mainTemplate = "HELLO HOW ARE YOU!                  \n" +
//                "     {{#table people firstName lastName }}        \n" +
//                "                                                  \n" +
//                "     {{/table}}                                   \n";
//
//        final String listTemplate =
//                "<table>                                                      \n" +
//                        "{{#each items}}                                      \n" +
//                        "     <tr>                                            \n" +
//                        "     ${body}                                         \n" +
//                        "      </tr>                                          \n" +
//                        "{{/each}}                                            \n" +
//                        "</table>                                             \n";
//
//        final String listItemTemplate =
//                "\n" +
//                        "           <td>                                             \n" +
//                        "                ${body}                                     \n" +
//                        "           </td>                                            \n";
//
//
//        replace = templateWithFunctions(
//                new Object() {
//
//                    String table(String arguments, String block, final Object context) {
//
//                        String[] args;
//                        String[] itemProperties;
//                        String itemsNameInContext;
//                        String listItemBody;
//                        String newBodyTemplate;
//
//                        List listOfTemplates;
//
//                        args = Str.splitBySpace(arguments);
//                        itemsNameInContext = args[0];
//                        itemProperties = slc(args, 1);
//
//                        listOfTemplates = mapBy(itemProperties, new Fn() {
//                            String function(String property) {
//                                /** TODO this test is technically broke... I had to add an extra space to this string
//                                 *  If you change " {{" + property + "}} " to  "{{" + property + "}}"
//                                 *  This break. I need a simpler test that reproduces this.
//                                 *  This bug started after I added JSTL <c:if and <c:forEach support.
//                                 * */
//                                return jstl().replace(listItemTemplate,
//                                        map("body",
//                                                " {{" + property + "}} ")
//                                ).toString();
//                            }
//                        });
//
//                        listItemBody = joinCollection('\n', listOfTemplates);
//                        newBodyTemplate = jstl().replace(listTemplate, map("body", listItemBody)).toString();
//
//                        Object items = getPropertyValue(context, itemsNameInContext);
//
//
//                        String output = template().replace(newBodyTemplate, map("items", items)).toString();
//                        return output;
//                    }
//                }
//        ).replace(mainTemplate,
//
//                contextJson
//        );
//
//        String test = "HELLO HOW ARE YOU!                  \n" +
//                "     <table>                                                      \n" +
//                "     <tr>                                            \n" +
//                "     \n" +
//                "           <td>                                             \n" +
//                "                 Yehuda                                      \n" +
//                "           </td>                                            \n" +
//                "\n" +
//                "\n" +
//                "           <td>                                             \n" +
//                "                 Katz                                      \n" +
//                "           </td>                                            \n" +
//                "                                         \n" +
//                "      </tr>                                          \n" +
//                "     <tr>                                            \n" +
//                "     \n" +
//                "           <td>                                             \n" +
//                "                 Carl                                      \n" +
//                "           </td>                                            \n" +
//                "\n" +
//                "\n" +
//                "           <td>                                             \n" +
//                "                 Lerche                                      \n" +
//                "           </td>                                            \n" +
//                "                                         \n" +
//                "      </tr>                                          \n" +
//                "     <tr>                                            \n" +
//                "     \n" +
//                "           <td>                                             \n" +
//                "                 Alan                                      \n" +
//                "           </td>                                            \n" +
//                "\n" +
//                "\n" +
//                "           <td>                                             \n" +
//                "                 Johnson                                      \n" +
//                "           </td>                                            \n" +
//                "                                         \n" +
//                "      </tr>                                          \n" +
//                "</table>                                             \n";
//
//        equalsOrDie(test, replace.toString());
//
//    }
///*
//
//
//     */
//


}
