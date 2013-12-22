package com.examples;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class EchoClient {
    public static void main ( String... args ) throws IOException {

        String host;
        int port;

        if ( args.length != 2 ) {
            System.out.println( "binding to port localhost:9999" );
            host = "localhost";
            port = 9999;
        } else {
            host = args[ 0 ];
            port = Integer.parseInt( args[ 1 ] );

        }


        try (
                Socket echoSocket = new Socket( host, port );
                PrintWriter socketOut =
                        new PrintWriter( echoSocket.getOutputStream(), true );
                Scanner socketIn = new Scanner( echoSocket.getInputStream() );
                Scanner console = new Scanner( System.in );
        ) {
            System.out.println( "Type in some text please." );
            while ( console.hasNextLine() ) {
                String userInput = console.nextLine();
                socketOut.println( userInput );
                System.out.println( "echo: " + socketIn.nextLine() );
            }
        }


    }

}
