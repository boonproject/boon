package org.boon.core.reflection;

import org.junit.Test;

import java.util.Collection;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 3/7/14.
 */
public class ClassMetaTest {


    public static void hitMeStatic() {}
    boolean ok;

    public static interface MyService {

        void hitMe();
        String hitMeAgain(int i);
    }


    @Test
    public void test() {

        Collection collection = ClassMeta.classMeta(MyService.class).instanceMethods();
        puts (collection);

        ok = collection.size() == 2 || die();



        collection = ClassMeta.classMeta(ClassMetaTest.class).instanceMethods();
        puts (collection);


        ok = collection.size() == 1 || die();


        collection = ClassMeta.classMeta(ClassMetaTest.class).classMethods();
        puts (collection);

        ok = collection.size() == 1 || die();

    }
}
