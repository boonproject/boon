package org.boon.tests;

import org.boon.datarepo.Repo;
import org.boon.datarepo.RepoBuilder;
import org.boon.datarepo.Repos;
import org.boon.datarepo.modification.ModificationEvent;
import org.boon.datarepo.modification.ModificationListener;
import org.boon.tests.model.Employee;
import org.boon.tests.model.SalesEmployee;
import org.boon.predicates.Function;

import java.util.List;
import java.util.logging.Level;

public class TestHelper {

    final static String getTestSSN = "777-222-2222";
    final static String getTestFirstName = "AAA";


    static List<Employee> employees = Employee.employees(
            Employee.employee("AAA", "Foo", "777-222-2222", "05.29.70", 10_000, true),
            Employee.employee("Boa", "Smith", "666-222-2222", "05.29.70", 10_000),
            Employee.employee("Bobbie", "Smith", "322-222-2222", "05.29.70", 100_000),
            Employee.employee("Bob", "Smith", "122-222-2222", "05.29.70", 99_000, true),
            Employee.employee("Bob", "Smith", "222-222-2222", "05.29.70", 199_000),
            Employee.employee("Bobbzie", "Smith", "422-222-2222", "05.29.70", 666_000, true),
            Employee.employee("Boc", "Smith", "1010-222-2222", "05.29.70", 10_000),
            Employee.employee("Darth", "Sith", "1111-222-2222", "05.29.70", 10_000, true),
            Employee.employee("ZZZ", "Zmm", "777-333-3333", "05.29.70", 10_000));


    static Repo<String, Employee> createBuilderNoReflection() {
        Repo<String, Employee> repo;

        RepoBuilder repoBuilder = Repos.builder();
        repoBuilder.primaryKey("id")
                .searchIndex("firstName").searchIndex("lastName")
                .searchIndex("salary").uniqueSearchIndex("empNum");

        repoBuilder.keyGetter("id", new Function<Employee, String>() {
            @Override
            public String apply(Employee employee) {
                return employee.getSsn();
            }
        });

        repoBuilder.keyGetter("firstName", new Function<Employee, String>() {
            @Override
            public String apply(Employee employee) {
                return employee.getFirstName();
            }
        });

        repoBuilder.keyGetter("lastName", new Function<Employee, String>() {
            @Override
            public String apply(Employee employee) {
                return employee.getLastName();
            }
        });

        repoBuilder.keyGetter("salary", new Function<Employee, Integer>() {
            @Override
            public Integer apply(Employee employee) {
                return employee.getSalary();
            }
        });

        repo = repoBuilder.build(String.class, Employee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;
    }


    //"tags", "metas", "metas2", "name2"

    static Repo<String, Employee> createFromBuilderNestedIndex() {

        /* Create a repo, and decide what to index. */
        RepoBuilder repoBuilder = Repos.builder();

        /* Decide what to index, ssn is primaryKey, firstName, lastName, and salary are indexes. */
        repoBuilder.primaryKey("id")
                .searchIndex("firstName").searchIndex("lastName")
                .searchIndex("salary").uniqueSearchIndex("empNum").nestedIndex("tags", "metas", "metas2", "name2");

        /* Create the repo with the builder. */
        Repo<String, Employee> repo
                = repoBuilder.build(String.class, Employee.class, SalesEmployee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;
    }

    static Repo<String, Employee> createFromBuilder() {

        /* Create a repo, and decide what to index. */
        RepoBuilder repoBuilder = Repos.builder();

        /* Decide what to index, ssn is primaryKey, firstName, lastName, and salary are indexes. */
        repoBuilder.primaryKey("id")
                .searchIndex("firstName").searchIndex("lastName")
                .searchIndex("salary").uniqueSearchIndex("empNum");

        /* Create the repo with the builder. */
        Repo<String, Employee> repo
                = repoBuilder.build(String.class, Employee.class, SalesEmployee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;
    }


    static Repo<String, Employee> createFromBuilderWithTransformAndCollation() {

        /* Create a repo, and decide what to index. */
        RepoBuilder repoBuilder = Repos.builder();

        /* Decide what to index, ssn is primaryKey, firstName, lastName, and salary are indexes. */
        repoBuilder.primaryKey("id")
                .searchIndex("firstName").searchIndex("lastName")
                .upperCaseIndex("firstName").collateIndex("lastName")
                .searchIndex("salary").uniqueSearchIndex("empNum");

        /* Create the repo with the builder. */
        Repo<String, Employee> repo
                = repoBuilder.build(String.class, Employee.class, SalesEmployee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;
    }


    public static Repo<String, Employee> createFromBuilderLogAndClone() {
        /* Create a repo, and decide what to index. */
        RepoBuilder repoBuilder = Repos.builder();

        /* Decide what to index, ssn is primaryKey, firstName, lastName, and salary are indexes. */
        repoBuilder.primaryKey("id")
                .searchIndex("firstName").searchIndex("lastName")
                .searchIndex("salary").uniqueSearchIndex("empNum")
                .debug().level(Level.INFO).cloneEdits(true).events(new ModificationListener() {

            @Override
            public void modification(ModificationEvent event) {
                System.out.print(event);
            }
        });

        /* Create the repo with the builder. */
        Repo<String, Employee> repo
                = repoBuilder.build(String.class, Employee.class, SalesEmployee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;

    }


    public static Repo<String, Employee> createWithNoIndexes() {
        /* Create a repo, and decide what to index. */
        RepoBuilder repoBuilder = Repos.builder();

        /* Decide what to index, ssn is primaryKey, firstName, lastName, and salary are indexes. */
        repoBuilder.primaryKey("id");


        /* Create the repo with the builder. */
        Repo<String, Employee> repo
                = repoBuilder.build(String.class, Employee.class, SalesEmployee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;

    }


    static Repo<String, Employee> createFromBuilderUsingPropertyAccess() {

        /* Create a repo, and decide what to index. */
        RepoBuilder repoBuilder = Repos.builder();

        /* Decide what to index, ssn is primaryKey, firstName, lastName, and salary are indexes. */
        repoBuilder.primaryKey("id")
                .searchIndex("firstName").searchIndex("lastName")
                .searchIndex("salary").uniqueSearchIndex("empNum")
                .usePropertyForAccess(true);

        /* Create the repo with the builder. */
        Repo<String, Employee> repo
                = repoBuilder.build(String.class, Employee.class, SalesEmployee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;
    }

    static Repo<String, Employee> createFromBuilderEvents() {

        /* Create a repo, and decide what to index. */
        RepoBuilder repoBuilder = Repos.builder();

        /* Decide what to index, ssn is primaryKey, firstName, lastName, and salary are indexes. */
        repoBuilder.primaryKey("id")
                .searchIndex("firstName").searchIndex("lastName")
                .searchIndex("salary").uniqueSearchIndex("empNum").events(new ModificationListener() {
            @Override
            public void modification(ModificationEvent event) {
                System.out.printf("event %s %s", event.getKey(), event.getItem());
            }
        })
                .usePropertyForAccess(true);

        /* Create the repo with the builder. */
        Repo<String, Employee> repo
                = repoBuilder.build(String.class, Employee.class, SalesEmployee.class);

        for (Employee employee : employees) {
            repo.add(employee);
        }
        return repo;
    }

}
