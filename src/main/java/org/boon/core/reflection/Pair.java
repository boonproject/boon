package org.boon.core.reflection;


public class Pair<T> {

    private T first;
    private T second;
    private T[] both = (T[]) new Object[2];

    public Pair() {
    }

    public Pair(T f, T s) {
        this.first = f;
        this.second = s;
        both[0] = f;
        both[1] = s;
    }


    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }


    public T[] getBoth() {
        return both;
    }

    public void setFirst(T first) {
        this.first = first;
        both[0] = first;

    }

    public void setSecond(T second) {
        this.second = second;
        both[1] = second;

    }

    public void setBoth(T[] both) {
        this.both = both;
        this.first = both[0];
        this.second = both[1];

    }


}
