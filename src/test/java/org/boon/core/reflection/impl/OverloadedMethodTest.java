package org.boon.core.reflection.impl;

import org.boon.core.reflection.ClassMeta;
import org.boon.core.reflection.MethodAccess;
import org.junit.Before;
import org.junit.Test;

import static org.boon.Boon.puts;

/**
 * Created by Richard on 9/22/14.
 */
public class OverloadedMethodTest {
    OverloadedMethod method;

    public static class SomeClass {
        String add(char c, char b) {
            return "addTwoChars_" + c + "_" + b;
        }
        String add(String c, String b) {
            return "addTwoStrings_" + c + "_" + b;
        }
//String add(int c, int b) {
//            return "addTwoInts_" + c + "_" + b;
//        }

    }

    @Before
    public void setup() {

        method = new OverloadedMethod();

        final ClassMeta<SomeClass> classMeta = ClassMeta.classMeta(SomeClass.class);
        for (MethodAccess ma : classMeta.methods("add")) {
            method.add(ma);
        }


    }

    @Test
    public void testTwoChars() {
        String str = (String) method.invokeDynamic(new SomeClass(), 'a', 'b');
        puts(str);
    }


    @Test
    public void testTwoStrings() {
        String str = (String) method.invokeDynamic(new SomeClass(), "a", "b");
        puts(str);
    }
}
