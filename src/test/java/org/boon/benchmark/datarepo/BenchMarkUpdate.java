package org.boon.benchmark.datarepo;

import org.boon.Lists;
import org.boon.benchmark.datarepo.utils.MeasuredRun;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;
import org.boon.benchmark.datarepo.model.Employee;
import org.boon.benchmark.datarepo.utils.BenchmarkHelper;
import org.boon.criteria.Update;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Boon.println;
import static org.boon.Boon.puts;
import static org.boon.criteria.CriteriaFactory.eqNestedAdvanced;


import static org.boon.core.reflection.Reflection.copy;

public class BenchMarkUpdate {

    static int numCreations = 100_000;

    @Test
    public void test () {
        numCreations = 100;
    }


    public static void main ( String[] args ) throws Exception {


        final List<Employee> employees = BenchmarkHelper.createMetricTonOfEmployees( numCreations );
        System.out.println( "employees created " + employees.size() );

        Map<String, List<MeasuredRun>> testResults = new ConcurrentHashMap<>();

        MeasuredRun run1 = test( employees, testResults );
        MeasuredRun run2 = test1( employees, testResults );

        List<MeasuredRun> runs = Lists.list( run1, run2 );


        for ( int index = 0; index < 1; index++ ) {

            for ( MeasuredRun run : runs ) {
                System.gc();
                Thread.sleep( 10 );
                run.run();
            }
        }

        Thread.sleep( 3_000 );
        System.out.println( "Waiting..." );
        Thread.sleep( 1_000 );
        System.out.println( "Start Now..." );
        Thread.sleep( 1_000 );

        for ( int index = 0; index < 10; index++ ) {

            for ( MeasuredRun run : runs ) {
                System.gc();
                Thread.sleep( 10 );
                run.run();
                puts( "Name", run.name(), "Time", run.time() );
            }
        }

        println( "done" );


    }


    private static MeasuredRun test ( final List<Employee> employees, final Map<String, List<MeasuredRun>> results ) {
        return new MeasuredRun( "test indexed", 1, 100, results ) {
            Repo repo;

            @Override
            protected void init () {
                                                    /* Create a repo, and decide what to index. */
                repo = Repos.builder().primaryKey( "id" ).searchIndex( "firstName" )
                        .lookupIndex( "firstName" ).nestedIndex( "department", "name" )
                        .useUnsafe( true ).removeDuplication( false )
                        .build( String.class, Employee.class );

                repo.addAll( copy( employees ) );


            }

            @Override
            protected void test () {
                repo.updateByFilter(
                        Update.update( Update.incPercent( "salary", 10 ) ),
                        eqNestedAdvanced( "engineering", "department", "name" ) );
            }
        };
    }

    private static MeasuredRun test1 ( final List<Employee> employees, final Map<String, List<MeasuredRun>> results ) {
        return new MeasuredRun( "test linear", 1, 100, results ) {
            List<Employee> employeeList;

            @Override
            protected void init () {

                employeeList = copy( employees );


            }

            @Override
            protected void test () {
                for ( Employee employee : employeeList ) {
                    if ( employee.getDepartment().getName().equals( "engineering" ) ) {
                        int increase = 10;
                        double dincrease = increase / 100.0;

                        int value = employee.getSalary();

                        double dvalue = value;

                        dvalue = dvalue + ( dvalue * dincrease );
                        employee.setSalary( ( int ) dvalue );
                    }
                }
            }
        };
    }

}
