package org.boon.core;

import java.io.PrintStream;
import java.io.PrintWriter;

public class Exceptions {


    public static boolean die(String message) {
        throw new Exception(message);
    }


    public static <T> T  die(Class<T> clazz, String message) {
        throw new Exception(message);
    }

    public static void handle(java.lang.Exception e) {
        throw new Exception(e);
    }


    public static <T> T handle(Class<T> clazz, java.lang.Exception e) {

        if (e instanceof Exception) {
            throw (Exception) e;
        }
        throw new Exception(e);
    }

    public static <T> T handle(Class<T> clazz,  String message, java.lang.Exception e) {

        if (e instanceof Exception) {
            throw (Exception) e;
        }
        throw new Exception(e);
    }


    public static <T> T tryIt (Class<T> clazz, TrialWithReturn<T> tryIt) {
         try {
             return tryIt.tryIt();
         } catch (java.lang.Exception ex) {
             throw new Exception(ex);
         }
    }


    public static void tryIt (Trial tryIt) {
        try {
            tryIt.tryIt();
        } catch (java.lang.Exception ex) {
            throw new Exception(ex);
        }
    }

    public static void handle(String message, Throwable e) {
        throw new Exception(message, e);
    }

    public static void tryIt (String message, Trial tryIt) {
        try {
            tryIt.tryIt();
        } catch (java.lang.Exception ex) {
            throw new Exception(message, ex);
        }
    }


    public static interface Trial {
        void tryIt () throws java.lang.Exception;
    }

    public static  interface TrialWithReturn <T>{
        T tryIt () throws java.lang.Exception;
    }

    public static class Exception extends RuntimeException {

           public Exception(String message) {
               super(message);
           }

           public Exception(String message, Throwable cause) {
               super(message, cause);
           }

           public Exception(Throwable cause) {
               super("Wrapped Exception", cause);
           }


           @Override
           public void printStackTrace(PrintStream s) {
               if (getCause()!=null) {
                   s.println("This Exception was wrapped, the original exception\n" +
                           "stack trace is:\n");
                   getCause().printStackTrace(s);
               }   else {
                   super.printStackTrace(s);
               }

           }

           @Override
           public String getMessage() {
               return super.getMessage() + (getCause() == null ? "" :
                       getCauseMessage());
           }

        private String getCauseMessage() {
            return "\n CAUSE " + getCause().getClass().getName() + " :: " +
                    getCause().getMessage();
        }

        @Override
           public String getLocalizedMessage() {
                return this.getMessage();
           }

           @Override
           public StackTraceElement[] getStackTrace() {
               if (getCause()!=null) {
                   return getCause().getStackTrace();
               }   else {
                   return super.getStackTrace();
               }

           }

           @Override
           public  Throwable getCause() {
               return super.getCause();
           }

           @Override
           public void printStackTrace(PrintWriter s) {
               if (getCause()!=null) {
                   s.println("This Exception was wrapped, the original exception\n" +
                           "stack trace is:\n");
                   getCause().printStackTrace(s);
               } else {
                   super.printStackTrace(s);
               }
           }

           @Override
           public void printStackTrace() {
               if (getCause()!=null) {
                   System.err.println("This Exception was wrapped, the original exception\n" +
                           "stack trace is:\n");
                   getCause().printStackTrace();
               } else {
                   super.printStackTrace();
               }
           }
       }

}
