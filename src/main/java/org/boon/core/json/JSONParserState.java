package org.boon.core.json;

public enum JSONParserState {
    START,
    START_OBJECT, END_OBJECT,
    START_LIST, END_LIST,
}
