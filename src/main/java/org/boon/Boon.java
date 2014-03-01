package org.boon;


import org.boon.config.ContextConfigReader;
import org.boon.core.Conversions;
import org.boon.core.Function;
import org.boon.core.Sys;
import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MethodAccess;
import org.boon.core.reflection.Reflection;
import org.boon.di.Context;
import org.boon.json.JsonFactory;
import org.boon.primitive.CharBuf;
import org.boon.template.BoonTemplate;

import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.*;

import static org.boon.Exceptions.die;
import static org.boon.Lists.toListOrSingletonList;
import static org.boon.Maps.fromMap;
import static org.boon.criteria.ObjectFilter.gte;
import static org.boon.json.JsonFactory.toJson;

public class Boon {


    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static void println( String message ) {
        Sys.println( message );
    }

    public static void println() {
        Sys.println( "" );
    }

    public static void println( Object message ) {

        print( message );
        println();
    }

    public static void print( String message ) {
        Sys.print( message );
    }

    public static void print( Object message ) {

        if ( message == null ) {
            print( "<NULL>" );
        } else if ( message.getClass().isArray() ) {
            print( toListOrSingletonList( message ).toString() );
        } else {
            print( message.toString() );
        }
    }

    public static void puts( Object... messages ) {

        for ( Object message : messages ) {
            print( message );
            print( ' ' );
        }
        println();

    }

    public static void putl( Object... messages ) {

        for ( Object message : messages ) {
            print( message );
            println();
        }
        println();

    }

    public static String sputl( Object... messages ) {
        CharBuf buf = CharBuf.create( 100 );
        return sputl( buf, messages );
    }

    public static String sputs( Object... messages ) {
        StringBuilder buf = new StringBuilder(  );
        return sputs( buf, messages );
    }


    public static String sputl( CharBuf buf, Object... messages ) {

        for ( Object message : messages ) {
            if ( message == null ) {
                buf.add( "<NULL>" );
            } else if ( message.getClass().isArray() ) {
                buf.add( toListOrSingletonList( message ).toString() );
            } else {
                buf.add( message.toString() );
            }
            buf.add( '\n' );
        }
        buf.add( '\n' );

        return buf.toString();


    }

    public static String sputs( CharBuf buf, Object... messages ) {

        int index = 0;
        for ( Object message : messages ) {
            if ( index != 0 ) {
                buf.add( ' ' );
            }
            index++;

            if ( message == null ) {
                buf.add( "<NULL>" );
            } else if ( message.getClass().isArray() ) {
                buf.add( toListOrSingletonList( message ).toString() );
            } else {
                buf.add( message.toString() );
            }
        }
        buf.add( '\n' );

        return buf.toString();

    }

    public static String sputs( StringBuilder buf, Object... messages ) {

        int index = 0;
        for ( Object message : messages ) {
            if ( index != 0 ) {
                buf.append( ' ' );
            }
            index++;

            if ( message == null ) {
                buf.append( "<NULL>" );
            } else if ( message.getClass().isArray() ) {
                buf.append( toListOrSingletonList( message ).toString() );
            } else {
                buf.append( message.toString() );
            }
        }
        buf.append( '\n' );

        return buf.toString();

    }

    public static boolean isArray( Object obj ) {
        return Typ.isArray(obj);
    }

    public static int len( Object obj ) {
        return Conversions.len(obj);
    }


    public static Iterator iterator( final Object o ) {
       return Conversions.iterator(o);
    }

    public static String joinBy( char delim, Object... args ) {
        CharBuf builder = CharBuf.create( 256 );
        int index = 0;
        for ( Object arg : args ) {
            builder.add( arg.toString() );
            if ( !( index == args.length - 1 ) ) {
                builder.add( delim );
            }
            index++;
        }
        return builder.toString();
    }



    public static List<?> mapBy(Iterable<?> objects, Object function) {
        return Lists.mapBy(objects, function);
    }

    public static void each(Iterable<?> objects, Object function) {
         Functional.each(objects, function);
    }

    public static String toJson(Object value) {
        return JsonFactory.toJson(value);
    }

    public static Object fromJson(String value) {
        return JsonFactory.fromJson(value);
    }

    public static <T> T fromJson(String value, Class<T> clazz) {
        return JsonFactory.fromJson(value, clazz);
    }


    public static Object atIndex(Object value, String path) {
        return BeanUtils.idx(value, path);
    }


    public static String gets() {
        Scanner console = new Scanner( System.in );
        String input = console.nextLine();
        return input.trim();
    }

    public static String template(String template, Object context) {
        return BoonTemplate.jstl().replace(template, context).toString();
    }


    public static String  add(String... args) {
        return Str.add(args);
    }

    public static String stringAtIndex(Object value, String path) {
        return Conversions.toString(BeanUtils.idx(value, path));
    }


    public static Object call(Object value, String method) {
        if (value instanceof Class) {
            return Invoker.invoke((Class) value, method);
        } else {
            return Invoker.invoke(value, method);
        }
    }



    public static String sliceOf(String string, int start, int stop) {
            return Str.sliceOf(string, start, stop);
    }


    public static String sliceOf(String string, int start) {
        return Str.sliceOf(string, start);
    }


    public static String endSliceOf(String string, int end) {
        return Str.endSliceOf(string, end);
    }


    public static Context readConfig(String namespace, String path) {

        if (path.startsWith("/")) {
            path = sliceOf(path, 1);
        }

        if (!path.endsWith(".json")) {
            if (!path.endsWith("/")) {
                path = add(path, "/");
            }
        }
        return ContextConfigReader.config().namespace(namespace)
                .resource("classpath://" + path)
                .resource("/etc/" + path)
                .read();
    }


    public static String sysProp(String propertyName, Object defaultValue) {
        return Sys.sysProp(propertyName, defaultValue);
    }


    public static boolean hasSysProp(String propertyName) {
        return Sys.hasSysProp(propertyName);
    }


    public static void putSysProp(String propertyName, Object value) {
         Sys.putSysProp(propertyName, value);
    }

    public static Context readConfig() {
        return readConfig(
                sysProp("BOON.APP.NAMESPACE", "boon.app"),
                sysProp("BOON.APP.CONFIG.PATH", "boon/app"));

    }

    public static void pressEnterKey(String pressEnterKeyMessage) {
        puts (pressEnterKeyMessage);
        gets();
    }


    public static void pressEnterKey() {
        puts ("Press enter key to continue");
        gets();
    }

    public static boolean respondsTo(Object object, String method) {
        if (object instanceof Class) {
            return Reflection.respondsTo((Class)object, method);
        } else {
            return Reflection.respondsTo(object, method);
        }
    }

    public static Object jsonResource(String path) {
        if (!IO.exists(IO.path(path))) {
             path = add ("classpath:/", path);
        }

        String str = IO.read(path);
        if (str!=null) {
            return fromJson(str);
        }
        return null;
    }

    public static Object jsonResourceFromTemplate(String path, Object context) {
        if (!IO.exists(IO.path(path))) {
            path = add ("classpath:/", path);
        }

        String str = IO.read(path);
        if (str!=null) {
            str = Boon.template(str, context);
            puts(str);
            return fromJson(str);
        }
        return null;
    }

    public static Map<String, Object> resourceMap(String path) {
        return (Map<String, Object>)jsonResource(path);
    }

    public static Map<String, Object> resourceMapFromTemplate(String path, Object context) {
        return (Map<String, Object>)jsonResourceFromTemplate(path, context);
    }


    public static <T> T resourceObject(String path, Class<T> type) {
        return fromMap(resourceMap(path), type);
    }


    public static <T> T resourceObjectFromTemplate(String path, Class<T> type, Object context) {
        return fromMap(resourceMapFromTemplate(path, context), type);
    }


    public static List<Object> resourceList(String path) {
        return (List<Object>)jsonResource(path);
    }


    public static List<Object> resourceListFromTemplate(String path, Object context) {
        return (List<Object>)jsonResourceFromTemplate(path, context);
    }



}