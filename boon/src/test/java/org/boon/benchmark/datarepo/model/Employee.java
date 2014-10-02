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

package org.boon.benchmark.datarepo.model;

import org.boon.core.Conversions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee {
    private String firstName;
    private String lastName;
    private String id;
    private Date birthDate;
    private int salary;
    private Department department = new Department();
    private long empNum;
    //private int _hashCode = -1;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    List<Tag> tags = new ArrayList<>();

    {
        tags.add( new Tag( "tag1" ) );
        tags.add( new Tag( "tag2" ) );
        tags.add( new Tag( "tag3" ) );

    }

    public static long num = System.currentTimeMillis();

    {
        setEmpNum( num );
        num++;
    }

    public static Employee employee( String f, String l ) {
        return employee( f, l, "" + System.nanoTime(), "05.05.50", 100_000_000, false );

    }

    public static Employee employee( String f, String l, String s, String d, int salary, boolean sales ) {
        Employee e = null;

        if ( sales ) {
            e = new SalesEmployee( 1 );
        } else {
            e = new Employee();
        }
        e.birthDate = Conversions.toDate( d );
        e.lastName = l;
        e.firstName = f;
        e.id = s;
        e.salary = salary;
        return e;

    }

    public static Employee employee( String f, String l, String s, String d, int salary ) {
        Employee e = new Employee();
        e.birthDate = Conversions.toDate( d );
        e.lastName = l;
        e.firstName = f;
        e.id = s;
        e.salary = salary;
        return e;
    }

    public static List<Employee> employees( Employee... _employees ) {
        List<Employee> employees = new ArrayList<Employee>( _employees.length );
        for ( Employee emp : _employees ) {
            employees.add( emp );
        }
        return employees;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getSsn() {
        return id;
    }

    public void setSsn( String ssn ) {
        this.id = ssn;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate( Date birthDate ) {
        this.birthDate = birthDate;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Employee employee = ( Employee ) o;

        if ( birthDate != null ? !birthDate.equals( employee.birthDate ) : employee.birthDate != null ) return false;
        if ( firstName != null ? !firstName.equals( employee.firstName ) : employee.firstName != null ) return false;
        if ( lastName != null ? !lastName.equals( employee.lastName ) : employee.lastName != null ) return false;
        if ( id != null ? !id.equals( employee.id ) : employee.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
//        if (_hashCode != -1) {
//            return _hashCode;
//        }
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + ( lastName != null ? lastName.hashCode() : 0 );
        result = 31 * result + ( id != null ? id.hashCode() : 0 );
        result = 31 * result + ( birthDate != null ? birthDate.hashCode() : 0 );
        return result;
    }


    public int getSalary() {
        return salary;
    }

    public void setSalary( int salary ) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", id='" + id + '\'' +
                ", birthDate=" + birthDate +
                ", salary=" + salary +
                ", department=" + getDepartment() +
                ", empNum=" + getEmpNum() +
                ", tags=" + tags +
                '}';
    }

    public long getEmpNum() {
        return empNum;
    }

    public void setEmpNum( long empNum ) {
        this.empNum = empNum;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment( Department department ) {
        this.department = department;
    }
}
