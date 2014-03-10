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
import static org.boon.criteria.ObjectFilter.eqNestedAdvanced;


import static org.boon.core.reflection.BeanUtils.copy;

public class BenchMarkUpdate {

    static int numCreations = 100_000;

    @Test
    public void test() {
        numCreations = 100;
    }


    public static void main( String[] args ) throws Exception {


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
                //puts( "Name", run.name(), "Time", run.time() );
            }
        }

        println( "done" );


    }


    private static MeasuredRun test( final List<Employee> employees, final Map<String, List<MeasuredRun>> results ) {
        return new MeasuredRun( "test indexed", 1, 100, results ) {
            Repo repo;

            @Override
            protected void init() {
                                                    /* Create a repo, and decide what to index. */
                repo = Repos.builder().primaryKey( "id" ).searchIndex( "firstName" )
                        .lookupIndex( "firstName" ).nestedIndex( "department", "name" )
                        .useUnsafe( true ).removeDuplication( false )
                        .build( String.class, Employee.class );

                repo.addAll( copy( employees ) );


            }

            @Override
            protected void test() {
                repo.updateByFilter(
                        Update.update( Update.incPercent( "salary", 10 ) ),
                        eqNestedAdvanced( "engineering", "department", "name" ) );
            }
        };
    }

    private static MeasuredRun test1( final List<Employee> employees, final Map<String, List<MeasuredRun>> results ) {
        return new MeasuredRun( "test linear", 1, 100, results ) {
            List<Employee> employeeList;

            @Override
            protected void init() {

                employeeList = copy( employees );


            }

            @Override
            protected void test() {
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
