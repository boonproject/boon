package org.boon.tests.model;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private String name = "bar";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag() {
    }

    List<Meta> metas = new ArrayList<>();

    {
        metas.add(new Meta("mtag1"));
        metas.add(new Meta("mtag2"));
        metas.add(new Meta("mtag3"));

    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", metas=" + metas +
                '}';
    }
}
