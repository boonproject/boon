package org.boon.template;

public interface Template {


    String replace(String string, Object... context);

    String replaceFromResource(String string, Object... context);


    String replaceFromFile(String string, Object... context);

    String replaceFromURI(String string, Object... context);


    void addFunctions(Class<?> functions);


    void addFunctions(String prefix, Class<?> functions);
}
