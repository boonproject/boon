package com.examples.model.test;

public class Email {


    private String content;

    public String getEmail() {
        return content;
    }

    public void setEmail( String content ) {
        this.content = content;
    }

    public Email( String content ) {
        this.content = content;
    }


    public Email() {
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Email ) ) return false;

        Email email = ( Email ) o;

        if ( content != null ? !content.equals( email.content ) : email.content != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}
