package org.boon;


import org.boon.Exceptions;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.boon.Exceptions.tryIt;

public class ExceptionsTest {


    public void methodThatThrowsException () throws IOException {

        throw new IOException("Bad stuff happens to good people");

    }



    @Test(expected = Exceptions.Exception.class)
    public void testException()  {

        try {
            methodThatThrowsException();
        } catch (IOException e) {
            Exceptions.handle(e);
        }

    }

    @Test(expected = Exceptions.Exception.class)
    public void die()  {

        try {
            Exceptions.die("Die");
        } catch (Exception e) {
            Exceptions.handle(e);
        }

    }


    @Test(expected = Exceptions.Exception.class)
    public void testExceptionWithMessage()  {

        try {
            methodThatThrowsException();
        } catch (IOException e) {
            Exceptions.handle("well that did not work very well", e);
        }

    }

    @Test(expected = Exceptions.Exception.class)
    public void testTryIt()  {

        tryIt(new Exceptions.Trial() {
            @Override
            public void tryIt() throws Exception {
                methodThatThrowsException();
            }
        });

    }

    @Test
    public void testTryItNoOp()  {

        Exceptions.tryIt(new Exceptions.Trial() {
            @Override
            public void tryIt() throws Exception {
            }
        });

    }

    @Test
    public void testTryItNoOp2WithMessage()  {

        tryIt("no op", new Exceptions.Trial() {
            @Override
            public void tryIt() throws Exception {
            }
        });

    }


    @Test(expected = Exceptions.Exception.class)
    public void testTryItWithMessage()  {

        tryIt("Calling method that throws exception", new Exceptions.Trial() {
            @Override
            public void tryIt() throws Exception {
                methodThatThrowsException();
            }
        });

    }

    @Test     //mostly just checking for null checks
    public void testSupportMethods()  {

        try {
            tryIt(new Exceptions.Trial() {
                @Override
                public void tryIt() throws Exception {
                    methodThatThrowsException();
                }
            });
        }catch (Exception ex) {
            ex.printStackTrace();


            ex.printStackTrace(new PrintWriter(new StringWriter()));

            ex.printStackTrace(System.out);
            StackTraceElement[] stackTrace = ex.getStackTrace();
            for (StackTraceElement aStackTrace : stackTrace) {
                System.out.println("\t\t" + aStackTrace);
            }

            System.out.println(ex.getMessage());
        }

        Exception ex = new Exceptions.Exception("");
        ex.printStackTrace();
        ex.printStackTrace(System.out);


        ex.printStackTrace(new PrintWriter(new StringWriter()));

        StackTraceElement[] stackTrace = ex.getStackTrace();
        for (StackTraceElement aStackTrace : stackTrace) {
            System.out.println("\t\t" + aStackTrace);
        }
        System.out.println(ex.getMessage());


        ex = new Exceptions.Exception("Foobar");

        ex.printStackTrace(new PrintWriter(new StringWriter()));

        ex.printStackTrace();
        ex.printStackTrace(System.out);
        stackTrace = ex.getStackTrace();
        for (StackTraceElement aStackTrace : stackTrace) {
            System.out.println("\t\t" + aStackTrace);
        }
        System.out.println(ex.getMessage());

    }


    @Test
    public void testPrintWriter() {

    }

}
