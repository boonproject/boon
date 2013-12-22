package com.examples.security.model;


import org.boon.Boon;
import org.boon.Str;

public class User extends Subject {

    public static final Class<User> user = User.class;

    public static User user ( String email ) {
        return new User( email );
    }


    public static User[] users ( User... users ) {
        return users;
    }

    private final String email;


    //For serialization only
    public User () {
        this.email = null;
    }

    private User ( final String email ) {

        super( "User:" + generateSubjectNameFromEmail( email ) );

        this.email = email;
    }

    public static String generateSubjectNameFromEmail ( final String email ) {

        String name = null;

        if ( email.endsWith( ".com" ) ) {
            name = "company:" + Str.slc( email, 0, -4 );
        } else if ( email.endsWith( ".org" ) ) {
            name = "organization:" + Str.slc( email, 0, -4 );
        } else {
            name = "country:" + Str.slc( email, -2 ) + ":" + Str.slcEnd( email, -2 );
        }

        name = name.replace( '.', '_' ).replace( '@', '-' );

        return name;
    }

    public static void main ( String... args ) {

        String email = "richardhightower@gmail.com";
        Boon.puts( email, generateSubjectNameFromEmail( email ) );

        String email2 = "marcomilk@gmail.com.br";
        Boon.puts( email2, generateSubjectNameFromEmail( email2 ) );

    }


    public String getEmail () {
        return email;
    }


    @Override
    public String toString () {
        return "User{" +
                "email='" + email + '\'' +
                '}';
    }
}
