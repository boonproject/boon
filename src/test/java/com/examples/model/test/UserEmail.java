package com.examples.model.test;


public class UserEmail {

    private Email email;

    public UserEmail ( String email ) {

        this.email = new Email( email );

    }

    public Email getEmail () {
        return email;
    }
}
