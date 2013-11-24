package org.boon;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.boon.Boon.puts;

public class ProcessRunnerTest {

    @Before
    public void init() {

    }


    @Test
    public void run () {
        puts ( Runner.runShell ( "ls -l" ) );
    }

    @Test
    public void runWithTimeout () {
        puts ( Runner.runShell ( 1, "ls -l") );
    }

    @Test
    public void runExec () {
        puts ( Runner.execShell ( "ls -l") );
    }


    @Test
    public void runExecTimeout () {
        puts ( Runner.execShell ( 1, "ls -l") );
    }



    @Test  (expected = Runner.ProcessException.class)
    public void runRunTimeoutFails () {
        puts ( Runner.run ( 1, "sleep 20" ) );
    }


//    @Test  (expected = Runner.ProcessException.class)
//    public void execTimeoutFails () {
//        puts ( Runner.exec ( 1, "sleep 20" ) );
//    }


//    @Test
//    public void execComplex() throws InterruptedException {
//        final Runner.ProcessInOut inOut = Runner.launchProcess ( 20, null, false, "lsof" );
//
//        String str ="";
//
//        while (str!=null && !inOut.isDone ()) {
//            str = inOut.getStdOut ().take ();
//            puts (str);
//        }
//
//        str = inOut.getStdErr ().poll (1L, TimeUnit.SECONDS);
//        System.err.println(str);
//
//        System.out.println(inOut.processOut ().getStdout ());
//
//
//    }


    public static void main (String... args) {
        System.out.println (Runner.run ( "date +%s" ));
    }

}
