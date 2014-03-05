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


        private static volatile LogWrapperFactory factory;

        private static final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();


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


    public static final String LOGGER_FACTORY_CLASS_NAME = "org.boon.logger-delegate-factory-class-name";


    static {
        init();
    }

    public static synchronized void init() {
        LogWrapperFactory factory;

        String className = JDKLogWrapperFactory.class.getName();
        try {
            className = System.getProperty(LOGGER_FACTORY_CLASS_NAME);
        } catch (Exception e) {
        }

        if (className != null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try {
                Class<?> clz = loader.loadClass(className);
                factory = (LogWrapperFactory) clz.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Error instantiating transformer class \"" +
                        className + "\"", e);
            }
        } else {
            factory = new JDKLogWrapperFactory();
        }

        context().factory = factory;
    }

    public static Logger logger(final Class<?> clazz) {
        return logger(clazz.getName());
    }

    public static Logger logger(final String name) {
        Logger logger = context().loggers.get(name);

        if (logger == null) {

            logger = new JDKLogWrapper(name);

            Logger oldLogger = context().loggers.putIfAbsent(name, logger);

            if (oldLogger != null) {
                logger = oldLogger;
            }
        }

        return logger;
    }

    public static void removeLogger(String name) {
        context().loggers.remove(name);
    }


    public static void removeLogger(final Class<?> clazz) {
        context().loggers.remove(clazz.getName());
    }



    public static Object contextToHold() {
        return context();
    }
}
