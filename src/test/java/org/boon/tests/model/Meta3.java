package org.boon.tests.model;


public class Meta3 {

    private String name3 = "bar";
    private long num = 5;
    private static long total = 5;


    public Meta3(String name) {
        this.name3 = name;
        num = total++;

    }

    public Meta3() {
        num = total++;
    }

}
