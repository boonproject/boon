package com.examples.people;

import com.examples.security.model.User;

public class Person {

    private String firstName;
    private String lastName;

    private User user;


    public Person () {
    }


    public Person ( String firstName, String lastName ) {

        this.firstName = firstName;
        this.lastName = lastName;
    }



    public boolean isUser () {
        return user != null;
    }

    public String getFirstName () {

        return firstName;
    }

    public void setFirstName ( String firstName ) {
        this.firstName = firstName;
    }



}
