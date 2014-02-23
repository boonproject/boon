package org.boon.core;


public class Pair<T> {

    private T first;
    private T second;

    public Pair() {
    }

    public Pair( T f, T s ) {
        this.first = f;
        this.second = s;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public void setFirst( T first ) {
        this.first = first;
    }

    public void setSecond( T second ) {
        this.second = second;
    }

}
