package com.examples;

import java.net.*;
import java.io.*;

public class EchoServer {
    public static void main( String... args ) throws IOException {

        int port;

        if ( args.length != 1 ) {
            System.out.println( "listening to port 9999" );
            port = 9999;
        } else {
            port = Integer.parseInt( args[ 0 ] );

        }

        while ( true ) {
            try (
                    ServerSocket serverSocket =
                            new ServerSocket( port );
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out =
                            new PrintWriter( clientSocket.getOutputStream(), true );
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader( clientSocket.getInputStream() ) );
            ) {
                String inputLine;
                while ( ( inputLine = in.readLine() ) != null ) {
                    out.println( inputLine );
                }
            }
        }
    }
}