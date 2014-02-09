package org.boon;

import org.boon.primitive.ByteBuf;
import org.boon.primitive.CharBuf;

import java.io.PrintStream;
import java.io.PrintWriter;

import static org.boon.Boon.sputs;

public class Exceptions {


    public static boolean die() {
        throw new SoftenedException( "died" );
    }

    public static boolean die( String message ) {
        throw new SoftenedException( message );
    }


    public static boolean die( Object... messages ) {
        throw new SoftenedException( sputs(messages) );
    }


    public static <T> T die( Class<T> clazz, String message ) {
        throw new SoftenedException( message );
    }

    public static void handle( java.lang.Exception e ) {
        throw new SoftenedException( e );
    }


    public static <T> T handle( Class<T> clazz, java.lang.Exception e ) {

        if ( e instanceof SoftenedException ) {
            throw ( SoftenedException ) e;
        }
        throw new SoftenedException( e );
    }

    public static <T> T handle( Class<T> clazz, String message, Throwable e ) {

        throw new SoftenedException( message, e );
    }


    public static <T> T tryIt( Class<T> clazz, TrialWithReturn<T> tryIt ) {
        try {
            return tryIt.tryIt();
        } catch ( java.lang.Exception ex ) {
            throw new SoftenedException( ex );
        }
    }


    public static void tryIt( Trial tryIt ) {
        try {
            tryIt.tryIt();
        } catch ( java.lang.Exception ex ) {
            throw new SoftenedException( ex );
        }
    }

    public static void handle( String message, Throwable e ) {
        throw new SoftenedException( message, e );
    }

    public static void tryIt( String message, Trial tryIt ) {
        try {
            tryIt.tryIt();
        } catch ( java.lang.Exception ex ) {
            throw new SoftenedException( message, ex );
        }
    }


    public static interface Trial {
        void tryIt() throws java.lang.Exception;
    }

    public static interface TrialWithReturn<T> {
        T tryIt() throws java.lang.Exception;
    }

    public static class SoftenedException extends RuntimeException {

        public SoftenedException( String message ) {
            super( message );
        }

        public SoftenedException( String message, Throwable cause ) {
            super( message, cause );
        }

        public SoftenedException( Throwable cause ) {
            super( "Wrapped Exception", cause );
        }


        @Override
        public void printStackTrace( PrintStream s ) {

            s.println( this.getMessage() );
            if ( getCause() != null ) {
                s.println( "This Exception was wrapped, the original exception\n" +
                        "stack trace is:\n" );
                getCause().printStackTrace( s );
            } else {
                super.printStackTrace( s );
            }

        }

        @Override
        public String getMessage() {
            return super.getMessage() + ( getCause() == null ? "" :
                    getCauseMessage() );
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
            if ( getCause() != null ) {
                return getCause().getStackTrace();
            } else {
                return super.getStackTrace();
            }

        }

        @Override
        public Throwable getCause() {
            return super.getCause();
        }

        @Override
        public void printStackTrace( PrintWriter s ) {

            s.println( this.getMessage() );

            if ( getCause() != null ) {
                s.println( "This Exception was wrapped, the original exception\n" +
                        "stack trace is:\n" );
                getCause().printStackTrace( s );
            } else {
                super.printStackTrace( s );
            }
        }

        @Override
        public void printStackTrace() {

            System.err.println( this.getMessage() );

            if ( getCause() != null ) {
                System.err.println( "This Exception was wrapped, the original exception\n" +
                        "stack trace is:\n" );
                getCause().printStackTrace();
            } else {
                super.printStackTrace();
            }
        }
    }


    public static String toString( Exception ex ) {
        CharBuf buffer = CharBuf.create( 255 );
        buffer.addLine( ex.getLocalizedMessage() );

        final StackTraceElement[] stackTrace = ex.getStackTrace();
        for ( StackTraceElement element : stackTrace ) {
            buffer.add( element.getClassName() );
            sputs( buffer, "class", element.getClassName(),
                    "method", element.getMethodName(), "line", element.getLineNumber() );
        }

        return buffer.toString();

    }


    public static String toJSON( Exception ex ) {
        ByteBuf buffer = ByteBuf.create( 255 );
        buffer.addByte( '{' );

        buffer.add( "\n    " ).addJSONEncodedString( "message" ).add( " : " )
                .addJSONEncodedString( ex.getMessage() ).add( ",\n" );

        buffer.add( "    " ).addJSONEncodedString( "localizedMessage" ).add( " : " )
                .addJSONEncodedString( ex.getLocalizedMessage() ).add( ",\n" );

        buffer.add( "    " ).addJSONEncodedString( "stackTrace" ).add( " : " )
                .addByte( '[' ).addByte( '\n' );

        final StackTraceElement[] stackTrace = ex.getStackTrace();

        for ( int index = 0; index < ( stackTrace.length > 10 ? 10 : stackTrace.length ); index++ ) {
            StackTraceElement element = stackTrace[ index ];
            if ( index != 0 ) {
                buffer.addByte( ',' );
                buffer.addByte( '\n' );
            }
            index++;
            buffer.add( "           { " );
            buffer.add( "             " ).addJSONEncodedString( "className" ).add( " : " )
                    .addJSONEncodedString( element.getClassName() ).add( ",\n" );

            buffer.add( "             " ).addJSONEncodedString( "methodName" ).add( " : " )
                    .addJSONEncodedString( element.getMethodName() ).add( ",\n" );

            buffer.add( "             " ).addJSONEncodedString( "lineNumber" ).add( " : " )
                    .add( "" + element.getLineNumber() ).add( "}\n" );

        }

        buffer.add( "\n    ]\n}" );
        return buffer.toString();

    }

}
