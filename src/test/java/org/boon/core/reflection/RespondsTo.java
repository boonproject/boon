package org.boon.core.reflection;


import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Str.lines;
import static org.boon.core.reflection.Reflection.handles;
import static org.boon.core.reflection.Reflection.invoke;
import static org.boon.core.reflection.Reflection.respondsTo;

public class RespondsTo {

    public static interface FileInterface {
        void open(String fileName);

        String readLine();

        void close();
    }


    public static class FileObject {

        boolean readLine;
        boolean openCalled;
        boolean closeCalled;

        boolean addCalled;

        public void open(String fileName) {

            openCalled = true;
            puts("Open ", fileName);
        }

        public String readLine() {
            readLine = true;
            return "read line";
        }

        public int add(int i, int f) {
           addCalled = true;
           return i + f;
        }

        public void close() {
            closeCalled = true;
        }


    }

    public static class StringReaderThing {

        BufferedReader reader = null;

        String contents;

        public StringReaderThing(String contents) {
            this.contents = contents;
        }

        public void open(String fileName) {

            reader = new BufferedReader(new StringReader(contents));

        }

        public String readLine() throws IOException {
            return reader.readLine();
        }

        public void close() throws IOException {
            reader.close();
        }

    }



    @Test
    public void test() {
        FileObject file = new FileObject();
        StringReaderThing reader = new StringReaderThing(lines("Hi mom", "how are you?"));
        List<Object> list = list(file, reader);


        for (Object object : list) {
            if ( respondsTo(object, "open", String.class) ) invoke(object, "open", "hi");

            if ( respondsTo(object, "add", int.class, int.class) ) puts ("add", invoke(object, "add", 1, 2));

            if ( respondsTo(object, "readLine") ) puts ( invoke(object, "readLine") );

            if ( respondsTo(object, "readLine") ) puts ( invoke(object, "readLine") );

            if ( respondsTo(object, "close" ) ) invoke(object, "close");
        }

        boolean ok = file.closeCalled && file.readLine && file.openCalled && file.addCalled || die();


    }



    @Test
    public void testInvoke() {
        FileObject file = new FileObject();
        StringReaderThing reader = new StringReaderThing(lines("Hi mom", "how are you?"));
        List<Object> list = list(file, reader);


        for (Object object : list) {
            if (respondsTo(object, "open", "hi") ) invoke(object, "open", "hi");

            if (respondsTo(object, "add", 1, 2) ) puts ("add", invoke(object, "add", 1, 2));


            if (respondsTo(object, "readLine") ) puts ( invoke(object, "readLine") );

            if (respondsTo(object, "readLine") ) puts ( invoke(object, "readLine") );

            if (respondsTo(object, "close" ) ) invoke(object, "close");
        }

        boolean ok = file.closeCalled && file.readLine && file.openCalled && file.addCalled || die();



    }


    @Test
    public void testInvokeByList() {
        FileObject file = new FileObject();
        StringReaderThing reader = new StringReaderThing(lines("Hi mom", "how are you?"));
        List<Object> list = list(file, reader);



        List <?> openList = list("hi");
        List <?> addList = list(1, 2);

        for (Object object : list) {
            if (respondsTo(object, "open", openList) ) invoke(object, "open", openList);

            if (respondsTo(object, "add", addList) ) puts ("add", invoke(object, "add", addList));


            if (respondsTo(object, "readLine") ) puts ( invoke(object, "readLine") );

            if (respondsTo(object, "readLine") ) puts ( invoke(object, "readLine") );

            if (respondsTo(object, "close" ) ) invoke(object, "close");
        }

        boolean ok = file.closeCalled && file.readLine && file.openCalled && file.addCalled || die();



    }



    @Test
    public void testHandles() {
        FileObject file = new FileObject();
        StringReaderThing reader = new StringReaderThing(lines("Hi mom", "how are you?"));
        List<Object> list = list(file, reader);



        List <?> openList = list("hi");
        List <?> addList = list(1, 2);

        for (Object object : list) {
            if ( handles(object, FileInterface.class) ) invoke(object, "open", openList);

            if ( respondsTo(object, "add", addList) ) puts ("add", invoke(object, "add", addList));


            if ( handles(object, FileInterface.class) ) {
                puts ( invoke(object, "readLine") );
                puts ( invoke(object, "readLine") );
                invoke(object, "close");
            }
        }

        boolean ok = file.closeCalled && file.readLine && file.openCalled && file.addCalled || die();



    }
}
