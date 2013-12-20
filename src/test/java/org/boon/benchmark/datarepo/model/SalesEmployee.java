package org.boon.benchmark.datarepo.model;

public class SalesEmployee extends Employee implements Comparable<SalesEmployee> {
    private int commissionRate = 1;

    public SalesEmployee () {
    }

    public SalesEmployee ( int commissionRate ) {
        this.commissionRate = commissionRate;
    }


    public float getCommissionRate () {
        return commissionRate;
    }

    public void setCommissionRate ( int commissionRate ) {
        this.commissionRate = commissionRate;
    }

    @Override
    public int compareTo ( SalesEmployee o ) {
        return 0;
    }

    public String getLastName () {
        return super.getLastName ();
    }

}
