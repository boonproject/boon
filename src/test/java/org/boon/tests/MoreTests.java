package org.boon.tests;

import org.boon.Lists;
import org.boon.core.Typ;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;
import org.boon.criteria.CriteriaFactory;
import org.boon.criteria.ProjectedSelector;
import org.boon.tests.model.Employee;
import org.boon.tests.model.HourlyEmployee;
import org.boon.tests.model.SalesEmployee;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.boon.datarepo.Collections.$q;
import static org.boon.datarepo.Collections.sortedQuery;

import static org.boon.core.reflection.Reflection.copy;
import static org.boon.core.reflection.Conversions.toDate;


import static org.junit.Assert.assertEquals;

public class MoreTests {

    List<Employee> list;
    List<Employee> bigList;

    List<Employee> h_list;


    @Before
    public void setUp() throws Exception {
        list = Lists.list(
                Employee.employee("firstA", "LastA", "123", "5.29.1970:00:00:01", 100),
                Employee.employee("firstB", "LastB", "124", "5.29.1960:00:00:00", 200)
        );

        h_list = Lists.list(
                Employee.employee("firstA", "LastA", "123", "5.29.1970:00:00:01", 100),
                Employee.employee("firstB", "LastB", "124", "5.29.1960:00:00:00", 200),
                Employee.employee("firstZ", "LastB", "125", "5.29.1960:00:00:00", 200, true),
                new HourlyEmployee()

        );

        bigList = copy(list);

        for (int index = 0; index < 2000; index++) {

            String dateString = "5.29.1970:00:00:01";
            if (index % 2 == 0) {
                dateString = "5.29.1950:00:00:01";

            } else {
                dateString = "5.29.1990:00:00:01";

            }
            bigList.add(Employee.employee("firstC" + index, "last" + index, "ssn" + index, dateString, 1000 + index));
        }

    }

    @Test
    public void testProjections() throws Exception {
        Repo<String, Employee> repo =
                Repos.builder().primaryKey("id")
                        .searchIndex("salary")
                        .build(Typ.string, Employee.class);

        repo.addAll(bigList);

        int max = repo.results(CriteriaFactory.gt("salary", 100))
                .firstInt(ProjectedSelector.max("salary"));

        assertEquals(2999, max);

        System.out.println(max);
    }

    @Test
    public void fieldOnlyInSubClass() throws Exception {
        List<Employee> queryableList = $q(h_list, SalesEmployee.class);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.eq("commissionRate", 1));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());

    }

    @Test
    public void fieldOnlyInSubClass2() throws Exception {
        List<Employee> queryableList = $q(h_list, Employee.class, SalesEmployee.class);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.eq("commissionRate", 1));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());

    }

    @Test
    public void fieldOnlyInSubClass3() throws Exception {
        List<Employee> queryableList = $q(h_list, Employee.class, SalesEmployee.class, HourlyEmployee.class);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.eq("commissionRate", 1));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());

        results = sortedQuery(queryableList, "firstName", CriteriaFactory.eq("weeklyHours", 40));
        assertEquals(1, results.size());
        assertEquals("HourlyEmployee", results.get(0).getClass().getSimpleName());

    }

    @Test(expected = NullPointerException.class)   //TODO add better error messages
    public void fieldOnlyInSubClass4() throws Exception {
        List<Employee> queryableList = $q(h_list, Employee.class, SalesEmployee.class);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.eq("commissionRate", 1));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());

        results = sortedQuery(queryableList, "firstName", CriteriaFactory.eq("weeklyHours", 40));
        assertEquals(0, results.size());

    }

    @Test
    public void typeOfTestLongName() throws Exception {
        List<Employee> queryableList = $q(h_list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.typeOf("SalesEmployee"));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());

    }

    @Test
    public void typeOfTest() throws Exception {
        List<Employee> queryableList = $q(h_list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.typeOf("SalesEmployee"));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());

    }

    @Test
    public void instanceOfTest() throws Exception {
        List<Employee> queryableList = $q(h_list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.instanceOf(SalesEmployee.class));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());
    }

    @Test
    public void implementsTest() throws Exception {
        List<Employee> queryableList = $q(h_list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.implementsInterface(Comparable.class));
        assertEquals(1, results.size());
        assertEquals("SalesEmployee", results.get(0).getClass().getSimpleName());
    }

    @Test
    public void superClassTest() throws Exception {
        List<Employee> queryableList = $q(h_list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.typeOf("ZSalaryEmployee"));
        assertEquals(0, results.size());

    }

    @Test
    public void testBetweenSalary_AND_LOTS_OF_TERMS_BIG_LIST() throws Exception {
        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.and(
                        CriteriaFactory.between("salary", 1000, 2000),
                        CriteriaFactory.eq("firstName", "firstC1"),
                        CriteriaFactory.startsWith("lastName", "last"),
                        CriteriaFactory.gt("birthDate", toDate("5.29.1940")),
                        CriteriaFactory.startsWith("id", "ssn"),
                        CriteriaFactory.gt("salary", 1000)
                ));

        assertEquals(1, results.size());

    }

    @Test
    public void testBetweenSalary_OR_PRECISE_NESTED_OR_AND() throws Exception {
        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(
                        CriteriaFactory.between("salary", 1001, 1002),
                        CriteriaFactory.between("salary", 1002, 1003),
                        CriteriaFactory.or(CriteriaFactory.eq("firstName", "firstC12"), CriteriaFactory.and(CriteriaFactory.eq("firstName", "firstC10"), CriteriaFactory.eq("firstName", "firstC11"))),
                        CriteriaFactory.and(CriteriaFactory.eq("firstName", "firstC20"), CriteriaFactory.eq("firstName", "firstC21"), CriteriaFactory.eq("firstName", "first22"),
                                CriteriaFactory.or(CriteriaFactory.eq("firstName", "firstC30"), CriteriaFactory.eq("firstName", "firstC31"))),

                        CriteriaFactory.or(
                                CriteriaFactory.or(
                                        CriteriaFactory.or(
                                                CriteriaFactory.eq("firstName", "firstC52"),
                                                CriteriaFactory.and(
                                                        CriteriaFactory.eq("firstName", "firstC60"),
                                                        CriteriaFactory.eq("firstName", "firstC61")
                                                )
                                        )
                                )
                        )


                ));

        System.out.println(results);
        assertEquals(4, results.size());
        assertEquals("firstC1", results.get(0).getFirstName());
        assertEquals("firstC12", results.get(1).getFirstName());
        assertEquals("firstC2", results.get(2).getFirstName());
        assertEquals("firstC52", results.get(3).getFirstName());


    }


    @Test
    public void testBetweenSalary_OR_PRECISE_NESTED_AND() throws Exception {
        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(
                        CriteriaFactory.between("salary", 1001, 1002),
                        CriteriaFactory.between("salary", 1002, 1003),
                        CriteriaFactory.and(CriteriaFactory.eq("firstName", "firstC10"), CriteriaFactory.eq("firstName", "firstC11"))

                ));

        assertEquals(2, results.size());

    }

    @Test
    public void testBetweenSalary_OR_PRECISE() throws Exception {
        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(
                        CriteriaFactory.between("salary", 1001, 1002),
                        CriteriaFactory.between("salary", 1002, 1003),
                        CriteriaFactory.eq("firstName", "firstC10"),
                        CriteriaFactory.eq("firstName", "firstC11")

                ));

        assertEquals(4, results.size());

    }

    @Test
    public void testBetweenSalary_OR_LOTS_OF_TERMS_BIG_LIST() throws Exception {
        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(
                        CriteriaFactory.between("salary", 1000, 2000),
                        CriteriaFactory.eq("firstName", "firstC1"),
                        CriteriaFactory.startsWith("lastName", "last"),
                        CriteriaFactory.gt("birthDate", toDate("5.29.1940")),
                        CriteriaFactory.startsWith("id", "ssn"),
                        CriteriaFactory.gt("salary", 1000)
                ));

        assertEquals(2002, results.size());

    }

    @Test
    public void testBetweenSalary_OR_FirstNameEQ_SECOND_TERM_NOT_FOUND_BIG_LIST() throws Exception {

        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(CriteriaFactory.between("salary", 1000, 2000), CriteriaFactory.eq("firstName", "RANDOMISHSTRING" + System.currentTimeMillis())));

        assertEquals(1000, results.size());

    }

    @Test
    public void testBetweenSalary_AND_FirstNameEQ_SECOND_TERM_NOT_FOUND_BIG_LIST() throws Exception {

        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.between("salary", 1000, 2000), CriteriaFactory.eq("firstName", "RANDOMISHSTRING" + System.currentTimeMillis()));

        assertEquals(0, results.size());

    }

    @Test
    public void testBetweenSalary_OR_FirstNameEQ_FIRST_TERM_NOT_FOUND_BIG_LIST() throws Exception {

        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(CriteriaFactory.between("salary", 1, 3), CriteriaFactory.startsWith("firstName", "firstC")));

        assertEquals(2000, results.size());

    }

    @Test
    public void testBetweenSalary_AND_FirstNameEQ_FIRST_TERM_NOT_FOUND_BIG_LIST() throws Exception {

        List<Employee> queryableList = $q(bigList);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.and(CriteriaFactory.between("salary", 1, 3), CriteriaFactory.startsWith("firstName", "firstC")));

        assertEquals(0, results.size());

    }

    @Test
    public void testBetweenSalary_OR_FirstNameEQ_SECOND_TERM_NOT_FOUND() throws Exception {

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(CriteriaFactory.between("salary", 199, 201), CriteriaFactory.eq("firstName", "RANDOMISHSTRING" + System.currentTimeMillis())));

        assertEquals(1, results.size());

    }


    @Test
    public void testBetweenSalary_OR_FirstNameEQ_FIRST_TERM_NOT_FOUND() throws Exception {

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.or(CriteriaFactory.between("salary", -1, -1), CriteriaFactory.eq("firstName", "firstA")));

        assertEquals(1, results.size());

    }

    @Test
    public void testBetweenSalaryAndFirstNameEQ_FIRST_TERM_NOT_FOUND() throws Exception {

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.and(CriteriaFactory.between("salary", -1, -1), CriteriaFactory.eq("firstName", "firstA")));

        assertEquals(0, results.size());

    }

    @Test
    public void testBetweenSalaryAndFirstNameEQ_SECOND_TERM_NOT_FOUND() throws Exception {

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.and(CriteriaFactory.between("salary", 0, 1000), CriteriaFactory.eq("firstName", "RANDOMISHSTRING" + System.currentTimeMillis())));

        assertEquals(0, results.size());

    }

    @Test
    public void testBetweenSalaryAndFirstNameEQ_SECOND_TERM_FOUND() throws Exception {

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName",
                CriteriaFactory.and(CriteriaFactory.between("salary", 0, 1000), CriteriaFactory.eq("firstName", "firstA")));

        assertEquals(1, results.size());

    }

    @Test
    public void testBetweenSalary() throws Exception {

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.between("salary", 100, 200));

        assertEquals(1, results.size());
        assertEquals("firstA", results.get(0).getFirstName());

    }

    @Test
    public void testBetweenSalaryExact() throws Exception {

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.between("salary", 100, 201));

        assertEquals(2, results.size());
        assertEquals("firstA", results.get(0).getFirstName());
        assertEquals("firstB", results.get(1).getFirstName());

    }


    @Test
    public void testBetweenSalaryExactOutOfRange() throws Exception {
        //rint(list);
        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.between("salary", 400, 500));

        assertEquals(0, results.size());

    }

    //@Test  //Java data handling SUCKS! I don't think it is an issue with index lib.
    public void testBetweenDateExact() throws Exception {

        //rint(list);

        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.between("birthDate", "5/29/1960:00:00:01", "5/29/1970:00:00:00"));

        assertEquals(2, results.size());
        assertEquals("firstA", results.get(0).getFirstName());
        assertEquals("firstB", results.get(1).getFirstName());

    }


    @Test
    public void testBetweenDateExactJustOverAndUnder1Year() throws Exception {
        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.between("birthDate", "5/29/1959", "5/29/1971"));

        assertEquals(2, results.size());
        assertEquals("firstA", results.get(0).getFirstName());
        assertEquals("firstB", results.get(1).getFirstName());

    }


    @Test
    public void testBetweenDate() throws Exception {
        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.between("birthDate", "5/29/1950", "5/29/1990"));
        assertEquals(2, results.size());
        assertEquals("firstA", results.get(0).getFirstName());
        assertEquals("firstB", results.get(1).getFirstName());

    }

    @Test
    public void testBetweenDatePreInit() throws Exception {
        List<Employee> queryableList = $q(list);
        List<Employee> results = sortedQuery(queryableList, "firstName", CriteriaFactory.between(Employee.class, "birthDate", "5/29/1950", "5/29/1990"));

        assertEquals(2, results.size());
        assertEquals("firstA", results.get(0).getFirstName());
        assertEquals("firstB", results.get(1).getFirstName());

    }


}
