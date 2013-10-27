package org.boon.tests.model;

import java.util.ArrayList;
import java.util.List;

public class Meta2 {
    private String name2 = "bar";


    public Meta2(String name) {
        this.name2 = name;
    }

    public Meta2() {
    }


    List<Meta3> metas3 = new ArrayList<>();

    {
        metas3.add(new Meta3("3tag1"));
        metas3.add(new Meta3("3tag2"));
        metas3.add(new Meta3("3tag3"));

    }

}
