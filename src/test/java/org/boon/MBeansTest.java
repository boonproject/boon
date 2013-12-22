package org.boon;

import org.junit.Test;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;


public class MBeansTest {

    public static interface HelloMBean {

        public void sayHello();

        public int add( int x, int y );

        public String getName();

    }


    public static class Hello implements HelloMBean {

        private String name = "value";

        public void sayHello() {
            System.out.println( "hello, world" );
        }

        public int add( int x, int y ) {
            return x + y;
        }

        public String getName() {
            return name;
        }
    }


    @Test
    public void test() throws Exception {


        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames = server.queryNames( null, null );

        for ( ObjectName name : objectNames ) {
            System.out.println( name.toString() );
            System.out.println( MBeans.map( server, name ) );

        }

        //Set<ObjectInstance> instances = server.queryMBeans(null, null);


    }


    @Test
    public void createTest() throws Exception {

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        Hello hello = new Hello();
        DynamicMBean dynamicMBean = MBeans.createMBean( hello, HelloMBean.class );

        MBeans.registerMBean( "com.example", "hello", dynamicMBean );
        Set<ObjectName> objectNames = server.queryNames( null, null );


        for ( ObjectName name : objectNames ) {
            System.out.println( name.toString() );
            System.out.println( MBeans.map( server, name ) );

        }

        hello.name = "laskdjfal;ksdjf;laskjdf;laksjdfl;aksjdfl;kajsdf\n\n\n\n\\n\n";


        for ( ObjectName name : objectNames ) {
            System.out.println( name.toString() );
            System.out.println( MBeans.map( server, name ) );

        }


    }


}