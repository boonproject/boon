package com.examples.security.model;


public class Partner extends Subject {

    private final long partnerId;

    public Partner( long partnerId ) {
        super( "Partner:" + partnerId );
        this.partnerId = partnerId;

    }

    public long getPartnerId() {
        return partnerId;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Partner ) ) return false;

        Partner partner = ( Partner ) o;

        if ( partnerId != partner.partnerId ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return ( int ) ( partnerId ^ ( partnerId >>> 32 ) );
    }
}
