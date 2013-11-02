package org.boon.benchmark.datarepo;

import org.boon.benchmark.datarepo.utils.MeasuredRun;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;
import org.boon.benchmark.datarepo.model.Employee;
import org.boon.benchmark.datarepo.utils.BenchmarkHelper;
import org.boon.criteria.Criteria;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;



import static org.boon.criteria.CriteriaFactory.eq;

public class BenchMark {


    static int numCreations = 100_000;

    @Test
    public void test() {
        numCreations = 100;
    }


    public static void main(String[] args) throws Exception {


        final List<Employee> employees = BenchmarkHelper.createMetricTonOfEmployees(numCreations);
        System.out.println("employees created " + employees.size());

        Map<String, List<MeasuredRun>> testResults = new ConcurrentHashMap<>();

        MeasuredRun run1 = test(employees, testResults);

        List<MeasuredRun> runs = list(run1);


        for (int index = 0; index < 2; index++) {

            for (MeasuredRun run : runs) {
                System.gc();
                Thread.sleep(10);
                run.run();
            }
        }

        Thread.sleep(3_000);
        System.out.println("Waiting...");
        Thread.sleep(1_000);
        System.out.println("Start Now...");
        Thread.sleep(1_000);

        for (int index = 0; index < 5; index++) {

            for (MeasuredRun run : runs) {
                System.gc();
                Thread.sleep(10);
                run.run();
                puts( "Name", run.name(), "Time", run.time());
            }
        }

        System.out.println("done");


    }


    private static MeasuredRun test(final List<Employee> employees, final Map<String, List<MeasuredRun>> results) {
        return new MeasuredRun("test", 1000, 1_000_000, results) {
            Repo repo;
            Criteria exp = eq("firstName", "Mike");

            @Override
            protected void init() {
                                                    /* Create a repo, and decide what to index. */
                repo = Repos.builder().primaryKey("id").searchIndex("firstName").lookupIndex("firstName")
                        .build(String.class, Employee.class);

                repo.addAll(employees);


            }

            @Override
            protected void test() {
                List<Employee> results = repo.query(exp);

                boolean found = false;
                for (Employee employee : results) {
                    if (employee.getFirstName().equals("Mike")
                            && employee.getLastName().equals("Middleoflist")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    die("not found");
                }

            }
        };
    }

}
