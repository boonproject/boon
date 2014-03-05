package org.boon.logging;


import org.boon.core.Sys;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Rick Hightower
 */
public class Logging {


    private final static Context _context;
    private static WeakReference<Context> weakContext = new WeakReference<>( null );


    private static class Context {


        private static volatile LoggerFactory factory;

        private static final ConcurrentMap<String, LoggerDelegate> loggers = new ConcurrentHashMap<>();


        private static final ConcurrentMap<String, ConfigurableLogger> configurableLoggers
                            = new ConcurrentHashMap<>();


    }

    static {

        boolean noStatics = Boolean.getBoolean( "org.boon.noStatics" );
        if ( noStatics || Sys.inContainer() ) {

            _context = null;
            weakContext = new WeakReference<>( new Context() );

        } else {
            _context = new Context();
        }
    }



    /* Manages weak references. */
    private static Context context() {

        if ( _context != null ) {
            return _context;
        } else {
            Context context = weakContext.get();
            if ( context == null ) {
                context = new Context();
                weakContext = new WeakReference<>( context );
            }
            return context;
        }
    }


    public static final String LOGGER_FACTORY_CLASS_NAME = "org.boon.logger-logger-factory-class-name";


    static {
        init();
    }

    public static synchronized void init() {
        LoggerFactory factory;

        String className = JDKLoggerFactory.class.getName();
        try {
            className = System.getProperty(LOGGER_FACTORY_CLASS_NAME);
        } catch (Exception e) {
        }

        if (className != null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                Class<?> clz = loader.loadClass(className);
                factory = (LoggerFactory) clz.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Error instantiating transformer class \"" +
                        className + "\"", e);
            }
        } else {
            factory = new JDKLoggerFactory();
        }

        context().factory = factory;
    }


    public static ConfigurableLogger configurableLogger(final Class<?> clazz) {
        return configurableLogger(clazz.getName());
    }


    public static ConfigurableLogger configurableLogger(final String name) {
        ConfigurableLogger loggerDelegate = context().configurableLoggers.get(name);

        if (loggerDelegate == null) {

            loggerDelegate = new ConfigurableLogger(logger(name));

            ConfigurableLogger oldLoggerDelegate = context().configurableLoggers.putIfAbsent(name, loggerDelegate);

            if (oldLoggerDelegate != null) {
                loggerDelegate = oldLoggerDelegate;
            }
        }

        return loggerDelegate;
    }


    public static LoggerDelegate logger(final Class<?> clazz) {
        return logger(clazz.getName());
    }

    public static LoggerDelegate logger(final String name) {
        LoggerDelegate loggerDelegate = context().loggers.get(name);

        if (loggerDelegate == null) {

            loggerDelegate = context().factory.logger(name);

            LoggerDelegate oldLoggerDelegate = context().loggers.putIfAbsent(name, loggerDelegate);

            if (oldLoggerDelegate != null) {
                loggerDelegate = oldLoggerDelegate;
            }
        }

        return loggerDelegate;
    }

    public static void setLevel(String name, LogLevel level) {
        logger(name).level(level);
    }


    public static void turnOnInMemoryConfigLoggerAll(String name) {

        ConfigurableLogger configurableLogger = configurableLogger(name);
        configurableLogger.tee(new InMemoryThreadLocalLogger(LogLevel.ALL));
    }


    public static void turnOffInMemoryConfigLoggerAll(String name) {

        ConfigurableLogger configurableLogger = configurableLogger(name);
        configurableLogger.unwrap();
    }

    public static void removeLogger(String name) {
        context().loggers.remove(name);
        context().configurableLoggers.remove(name);
    }


    public static void removeLogger(final Class<?> clazz) {
        context().loggers.remove(clazz.getName());
        context().configurableLoggers.remove(clazz.getName());
    }



    public static Object contextToHold() {
        return context();
    }
}
