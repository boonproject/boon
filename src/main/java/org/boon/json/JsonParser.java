package org.boon.json;

public interface JsonParser {


    <T> T  parse (Class <T> type,  String str);


    <T> T  parse (Class <T> type,  byte [] bytes);


    <T> T  parse (Class <T> type,  CharSequence charSequence);


    <T> T  parse (Class <T> type,  char [] chars);



}
