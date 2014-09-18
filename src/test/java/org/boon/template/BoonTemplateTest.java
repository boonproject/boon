package org.boon.template;

import org.boon.Boon;
import org.boon.Lists;
import org.boon.Maps;
import org.boon.primitive.Arry;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Richard on 9/15/14.
 */
public class BoonTemplateTest {


    Company company;

    BoonTemplate template;


    @Before
    public void setup() {

        final List<Dept> list = Lists.list(
                new Dept("Engineering", new Employee("Rick", "Hightower", 1,
                        new Phone("320", "555", "1212"), Fruit.ORANGES, Fruit.APPLES, Fruit.STRAWBERRIES)),
                new Dept("HR", new Employee("Diana", "Hightower", 2, new Phone("320", "555", "1212")))

        );

        company = new Company("Mammatus", list);
        template = new BoonTemplate();


    }


    @Test
    public void companyObject() {

        final String results = template.replace("${this}", company);


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
                "<c:loop items='${list}'}'>${item} </c:loop>" ,

                Lists.list(Maps.map("list", Lists.list("apple", "orange", "b"))));


        Boon.equalsOrDie("#apple orange b #", "#"+results +"#");

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




}
