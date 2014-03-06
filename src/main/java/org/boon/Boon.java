package org.boon;


import org.boon.config.ContextConfigReader;
import org.boon.core.Conversions;
import org.boon.core.Sys;
import org.boon.core.Typ;
import org.boon.core.reflection.*;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.di.Context;
import org.boon.json.JsonFactory;
import org.boon.logging.LogLevel;
import org.boon.logging.Logging;
import org.boon.logging.TeeLoggerWrapper;
import org.boon.logging.TerminalLogger;
import org.boon.primitive.CharBuf;
import org.boon.template.BoonTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Lists.toListOrSingletonList;
import static org.boon.Maps.fromMap;
import static org.boon.Str.camelCase;
import static org.boon.Str.camelCaseLower;
import static org.boon.Str.underBarCase;

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
        } else if (message instanceof  char[]) {
            print(FastStringUtils.noCopyStringFromChars((char[]) message));
        }
        else if ( message.getClass().isArray() ) {
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
        CharBuf buf = CharBuf.create(80);
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

    public static String jstl(String template, Object context) {
        return BoonTemplate.jstl().replace(template, context).toString();
    }


    public static String handlebars(String template, Object context) {
        return BoonTemplate.template().replace(template, context).toString();
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
        String localConfigDir =
                add( System.getProperty("user.home"), ".",
                        camelCaseLower( underBarCase( namespace) ) );


        return readConfig(namespace, path,
                "/etc/",  //look in /etc/{path}
                localConfigDir, //look in local dir
                "classpath:/"); //look in classpath
    }


    public static Context readConfig(String namespace, String path, String... roots) {

        trace("readConfig(namespace, path, roots)", "IN", namespace, path, roots);

        if (path.startsWith("/")) {
            path = sliceOf(path, 1);

        }

        if (!path.endsWith(".json")) {
            if (!path.endsWith("/")) {
                path = add(path, "/");
            }
        }
        ContextConfigReader contextConfigReader = ContextConfigReader.config().namespace(namespace);

        for (String root : roots) {

            if (!root.endsWith("/")) {
                root = add(root, "/");
            }


            debug("readConfig", "adding root", root);
            contextConfigReader.resource( add (root, path) );
        }

        trace("readConfig(namespace, path, roots)", "OUT", namespace, path, roots);

        return contextConfigReader.read();

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


    public static String resource(String path) {
        if (!IO.exists(IO.path(path))) {
            path = add ("classpath:/", path);
        }

        String str = IO.read(path);
        return str;
    }


    public static String resourceFromHandleBarsTemplate(String path, Object context) {
        if (!IO.exists(IO.path(path))) {
            path = add ("classpath:/", path);
        }

        String str = IO.read(path);

        if (str!=null) {
            str = Boon.handlebars(str, context);
        }

        return str;
    }

    public static String resourceFromTemplate(String path, Object context) {
        if (!IO.exists(IO.path(path))) {
            path = add ("classpath:/", path);
        }

        String str = IO.read(path);

        if (str!=null) {
            str = Boon.jstl(str, context);
        }

        return str;
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
            str = Boon.jstl(str, context);
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


    public static List<?> resourceList(String path) {
        return (List<?>)jsonResource(path);
    }


    public static <T> List<T> resourceListFromTemplate(String path,  Class<T> listOf, Object context) {
        List<Object> list = (List)jsonResourceFromTemplate(path, context);

        return MapObjectConversion.convertListOfMapsToObjects(true, null,
                FieldAccessMode.FIELD_THEN_PROPERTY.create(true), listOf, list, Collections.EMPTY_SET);
    }

    public static <T> List<T> resourceList(String path, Class<T> listOf) {

        List<Object> list = (List)jsonResource(path);

        return MapObjectConversion.convertListOfMapsToObjects(true, null,
                FieldAccessMode.FIELD_THEN_PROPERTY.create(true), listOf, list, Collections.EMPTY_SET);

    }


    public static List<?> resourceListFromTemplate(String path, Object context) {
        return (List<?>)jsonResourceFromTemplate(path, context);
    }

    public static String className(Object object) {
        return object == null ? "CLASS<NULL>" : object.getClass().getName();
    }

    public static String simpleName(Object object) {
        return object == null ? "CLASS<NULL>" : object.getClass().getSimpleName();
    }



    public static Logger logger(final Class<?> clazz) {
        return new Logger(Logging.logger( clazz ));
    }

    public static Logger logger(String name) {
        return new Logger(Logging.logger(name));
    }


    public static Logger configurableLogger(String name) {
        return new Logger(Logging.configurableLogger(name));
    }


    public static Logger configurableLogger(final Class<?> clazz) {
        return new Logger(Logging.configurableLogger( clazz.getName() ));
    }


    private static AtomicBoolean debug = new AtomicBoolean(false);

    public static boolean debugOn() {
        return debug.get();
    }



    public static void turnDebugOn() {
         debug.set(true);
    }


    public static void turnDebugOff() {
        debug.set(false);
    }


    final static Logger logger;

    static  //we do this so it runs in a container like tomcat, resin or jboss.
    {
        if (Sys.inContainer()) {
            logger = null;
        } else {
            logger = configurableLogger(Boon.class);
        }
    }

    private static Logger _log() {
           if (debugOn()) {
               return new Logger(new TerminalLogger().level(LogLevel.DEBUG));
           } else {
               return logger == null ? configurableLogger("BOON.SYSTEM") : logger;
           }
    }


    public static boolean logInfoOn() {
        return _log().infoOn();
    }

    public static boolean logTraceOne() {
        return _log().traceOn();
    }


    public static boolean logDebugOn() {
        return _log().debugOn();
    }



    public static void fatal(Object... messages) {
        _log().fatal(messages);
    }

    public static void error(Object... messages) {
        _log().error(messages);
    }

    public static void warn(Object... messages) {
        _log().warn(messages);
    }

    public static void info(Object... messages) {
        _log().info(messages);
    }


    public static void debug(Object... messages) {
        _log().debug(messages);
    }

    public static void trace(Object... messages) {
        _log().trace(messages);
    }


    public static void config(Object... messages) {
        _log().config(messages);
    }




    public static void fatal(Throwable t, Object... messages) {
        _log().fatal(t, messages);
    }

    public static void error(Throwable t, Object... messages) {
        _log().error(t, messages);
    }

    public static void warn(Throwable t, Object... messages) {
        _log().warn(t, messages);
    }

    public static void info(Throwable t, Object... messages) {
        _log().info(t, messages);
    }


    public static void config(Throwable t, Object... messages) {
        _log().config(t, messages);
    }


    public static void debug(Throwable t, Object... messages) {
        _log().debug(t, messages);
    }


    public static void trace(Throwable t, Object... messages) {
        _log().trace(t, messages);
    }





}