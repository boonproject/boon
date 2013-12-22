package org.boon.benchmark.datarepo.utils;

import org.boon.Lists;
import org.boon.benchmark.datarepo.model.Department;
import org.boon.benchmark.datarepo.model.Employee;
import org.boon.benchmark.datarepo.model.SalesEmployee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BenchmarkHelper {

    final static String getTestSSN = "777-222-2222";
    final static String getTestFirstName = "AAA";


    public static List<Employee> createMetricTonOfEmployees( int numEmps ) {
        List<Employee> employees = new ArrayList<>();
        List<String> firstNames = Lists.list( RandomWordUtil.generateWords( numEmps ) );
        List<String> lastNames = Lists.list( RandomWordUtil.generateWords( numEmps ) );


        System.out.println( "Creating this many employees " + numEmps );
        Random random = new Random( System.nanoTime() );


        for ( int index = 0; index < numEmps; index++ ) {
            Employee employee = null;

            if ( index % 100 == 0 ) {
                employee = new Employee();
            } else {
                employee = new SalesEmployee();
            }

            if ( index % 1000 == 0 ) {
                System.out.println( "employee count " + index );
            }
            employees.add( employee );
            int firstNameIdx = Math.abs( random.nextInt() % numEmps );
            int lastNameIdx = Math.abs( random.nextInt() % numEmps );
            employee.setFirstName( firstNames.get( firstNameIdx ) );
            employee.setLastName( lastNames.get( lastNameIdx ) );
            employee.setEmpNum( index );
            employee.setSsn( "" + index * 33 + "1234567-" + index );
            employee.setDepartment( new Department() );
            int dept = index % 7;
            switch ( dept ) {
                case 0:
                    employee.getDepartment().setName( "engineering" );
                    employee.setSalary( 100_000 );
                    break;
                case 1:
                    employee.getDepartment().setName( "finance" );
                    employee.setSalary( 300_000 );
                    break;
                case 2:
                    employee.getDepartment().setName( "accounting" );
                    employee.setSalary( 90_000 );
                    break;
                case 3:
                    employee.getDepartment().setName( "sales" );
                    employee.setSalary( 1_300_000 );
                    break;
                case 4:
                    employee.getDepartment().setName( "manufacturing" );
                    employee.setSalary( 30_000 );
                    break;
                case 5:
                    employee.getDepartment().setName( "marketing" );
                    employee.setSalary( 200_000 );
                    break;
                case 6:
                    employee.getDepartment().setName( "IT" );
                    employee.setSalary( 150_000 );
                    break;
                default:
                    employee.getDepartment().setName( "project mgmt" );
                    employee.setSalary( 100_000 );
                    break;

            }


        }


        //A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
        employees.add( Employee.employee( "Mike", "Middleoflist" ) );
        employees.add( Employee.employee( "Asimov", "Asistopoflist" ) );
        employees.add( Employee.employee( "Zed", "Zedisbottomoflist" ) );
        employees.add( Employee.employee( "Xan", "Xiddleofnowhere" ) );
        employees.add( Employee.employee( "Jan", "Janmiddlelowertopoflist" ) );

        System.out.println( "Created this many employees " + employees.size() );


        return employees;
    }

}
