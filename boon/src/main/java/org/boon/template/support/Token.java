package org.boon.template.support;

/**
* Created by Richard on 9/17/14.
*/
public class Token {

    int start;
    int stop;
    TokenTypes type;

    public int start() {
        return start;
    }


    public int stop() {
        return stop;
    }

    public TokenTypes type() {
        return type;
    }

    public Token(int start, int stop, TokenTypes type) {
        this.start = start;
        this.stop = stop;
        this.type = type;
    }

    public Token() {
    }

    public static Token text(int start, int stop) {
        Token token = new Token();
        token.start = start;
        token.stop = stop;
        token.type = TokenTypes.TEXT;
        return token;
    }


    public static Token commandStart(int start, int stop) {
        Token token = new Token();
        token.start = start;
        token.stop = stop;
        token.type = TokenTypes.COMMAND;
        return token;
    }

    public static Token commandBody(int start, int stop) {
        Token token = new Token();
        token.start = start;
        token.stop = stop;
        token.type = TokenTypes.COMMAND_BODY;
        return token;
    }

    public static Token expression(int start, int stop) {
        Token token = new Token();
        token.start = start;
        token.stop = stop;
        token.type = TokenTypes.EXPRESSION;
        return token;
    }

    @Override
    public String toString() {
        return "Token{" +
                "start=" + start +
                ", stop=" + stop +
                ", type=" + type +
                '}';
    }

    public void stop(int index) {
        stop = index;
    }
}
