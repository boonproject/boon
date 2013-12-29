package org.boon.json.implementation;


import org.boon.Exceptions;
import org.boon.json.JsonParser;
import org.boon.json.JsonParserFactory;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.boon.Boon.puts;

public class JsonParserConcurrent extends BaseJsonParser implements JsonParser {

    private Worker [] workers;
    private Future [] future;

    private BlockingQueue<Action> workQueue = new LinkedBlockingQueue <>(100);
    private BlockingQueue<Action> actionQueue = new LinkedBlockingQueue <>(100);

    private final ExecutorService pool;


    private static class Worker {

        JsonParser parser;
        JsonParserConcurrent parent;
        AtomicBoolean run = new AtomicBoolean ( true );

        void run () {
            try {
                while (run.get ()) {
                    Action action = parent.workQueue.take ();
                    workOn ( action );
                }
            } catch ( InterruptedException e ) {
                Thread.interrupted ();
            }
        }

        private void workOn ( Action action ) {
            switch ( action.type ) {
                case OBJECT:
                    workOnObject(action);
                    return;
            }
        }

        private void workOnObject ( Action action ) {

            Object object = null;

            try {
               switch ( action.medium ) {
                    case CHARS:
                        object = parser.parse ( (char[])action.payload );
                        break;
                   case STRING:
                       object = parser.parse ( (String)action.payload );
                       break;
                   case INPUT_STREAM:
                       if (action.charSet == null) {
                        object = parser.parse ( (InputStream)action.payload );
                       } else {
                           object = parser.parse ( (InputStream)action.payload, action.charSet );
                       }
                       break;
                   case BYTES:
                       if (action.charSet == null) {
                           object = parser.parse ( (byte[])action.payload );
                       } else {
                           object = parser.parse ( (byte[])action.payload, action.charSet );
                       }
                       break;
                   case READER:
                       object = parser.parse ( (Reader)action.payload );
                       break;

               }

                action.returnQueue.put ( object );
               } catch ( Exception ex ) {

                try {
                    action.returnQueue.put ( ex );
                } catch ( InterruptedException e ) {
                    Thread.interrupted();
                }
            }

            }

        }

    public JsonParserConcurrent (JsonParserFactory factory) {
        int cores = Runtime.getRuntime().availableProcessors();
        workers = new Worker[cores];
        pool = Executors.newFixedThreadPool ( cores );
        future = new Future[cores];

        for (int index = 0; index < cores; index++) {
            final Worker worker = new Worker();
            worker.parser = factory.createFastParser ();
            worker.parent = this;
            final Future<?> future = pool.submit ( new Runnable () {
                @Override
                public void run () {
                    worker.run ();
                }
            } );
        }
    }


    private static class Action {
        ActionType type;
        Medium medium;
        Object payload;
        Charset charSet;
        Class<?> finalType;

        BlockingQueue<Object> returnQueue = new LinkedBlockingQueue<>(1);
    }

    static enum ActionType {
        OBJECT,
        TYPED_OBJECT,
        LIST
    }

    static enum Medium {
        BYTES,
        STRING,
        CHARS,
        INPUT_STREAM,
        READER,
        FILE_NAME
    }


    public static void main (String... args)  {
        JsonParserFactory factory = new JsonParserFactory ();
        JsonParserConcurrent jsonParserConcurrent = new JsonParserConcurrent ( factory );
        final Map<String, Object> map = jsonParserConcurrent.parseMap ( "{\"hello\": \"hello world\" }" );
        puts(map.get ( "hello" ));
    }



    @Override
    public Object parse ( String jsonString ) {

        Action action = createAction();
        action.payload = jsonString;
        action.type = ActionType.OBJECT;
        action.medium = Medium.STRING;
        addJob(action);

        return getResult(Object.class, action);

    }

    private <T> T  getResult ( Class<T> clazz, Action action ) {
        Object object = null;
        try {
            object = action.returnQueue.poll ( 60, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            Thread.interrupted();
        }
          
        action.returnQueue.clear ();
        action.charSet = null;
        action.finalType = null;
        this.actionQueue.offer( action );

        if (object instanceof  Exception) {
            return Exceptions.handle (clazz, (Exception) object);
        } else {
            return (T) object;
        }


    }

    private void addJob ( Action action ) {
        try {
            this.workQueue.put ( action );
        } catch ( InterruptedException e ) {
            Thread.interrupted();
        }

    }

    private Action createAction () {
        Action action = actionQueue.poll ();
        if (action == null) {
            action = new Action();
        }
        return action;
    }

    @Override
    public Object parse ( char[] value ) {
        Action action = createAction();
        action.payload = value;
        action.type = ActionType.OBJECT;
        action.medium = Medium.CHARS;
        addJob(action);

        return getResult(Object.class, action);
    }

    @Override
    public Object parse ( byte[] value ) {
        Action action = createAction();
        action.payload = value;
        action.type = ActionType.OBJECT;
        action.medium = Medium.BYTES;
        action.charSet = null;
        addJob(action);

        return getResult(Object.class, action);
    }

    @Override
    public Object parse ( byte[] value, Charset charset ) {
        Action action = createAction();
        action.payload = value;
        action.type = ActionType.OBJECT;
        action.medium = Medium.BYTES;
        action.charSet = charset;
        addJob(action);
        return getResult(Object.class, action);
    }

    @Override
    public Object  parse ( InputStream value, Charset charset ) {
        Action action = createAction();
        action.payload = value;
        action.type = ActionType.OBJECT;
        action.medium = Medium.INPUT_STREAM;
        action.charSet = charset;
        addJob(action);
        return getResult(Object.class, action);
    }

    @Override
    public Object parse ( CharSequence value ) {
        Action action = createAction();
        action.payload = value.toString ();
        action.type = ActionType.OBJECT;
        action.medium = Medium.STRING;
        addJob(action);
        return getResult(Object.class, action);
    }

    @Override
    public Object  parse ( InputStream value ) {
        Action action = createAction();
        action.payload = value;
        action.type = ActionType.OBJECT;
        action.medium = Medium.INPUT_STREAM;
        action.charSet = null;
        addJob(action);
        return getResult(Object.class, action);
    }

    @Override
    public Object  parse ( Reader value ) {
        Action action = createAction();
        action.payload = value;
        action.type = ActionType.OBJECT;
        action.medium = Medium.READER;
        addJob(action);
        return getResult(Object.class, action);
    }

    @Override
    public Object  parseFile ( String file ) {
        Action action = createAction();
        action.payload = file;
        action.type = ActionType.OBJECT;
        action.medium = Medium.FILE_NAME;
        addJob(action);
        return getResult(Object.class, action);
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, String jsonString ) {
        Action action = createAction();
        action.payload = jsonString;
        action.type = ActionType.LIST;
        action.medium = Medium.STRING;
        action.finalType = componentType;
        addJob(action); 
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, InputStream input ) {
        Action action = createAction();
        action.payload = input;
        action.type = ActionType.LIST;
        action.medium = Medium.INPUT_STREAM;
        action.finalType = componentType;
        addJob(action);
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, InputStream input, Charset charset ) {
        Action action = createAction();
        action.payload = input;
        action.type = ActionType.LIST;
        action.medium = Medium.INPUT_STREAM;
        action.finalType = componentType;
        action.charSet = charset;
        addJob(action);
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, byte[] jsonBytes ) {
        Action action = createAction();
        action.payload = jsonBytes;
        action.type = ActionType.LIST;
        action.medium = Medium.BYTES;
        action.finalType = componentType;
        action.charSet = null;
        addJob(action);
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, byte[] jsonBytes, Charset charset ) {
        Action action = createAction();
        action.payload = jsonBytes;
        action.type = ActionType.LIST;
        action.medium = Medium.BYTES;
        action.finalType = componentType;
        action.charSet = charset;
        addJob(action);
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, char[] chars ) {
        Action action = createAction();
        action.payload = chars;
        action.type = ActionType.LIST;
        action.medium = Medium.CHARS;
        action.finalType = componentType;
        addJob(action);
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, CharSequence jsonSeq ) {
        Action action = createAction();
        action.payload = jsonSeq.toString();
        action.type = ActionType.LIST;
        action.medium = Medium.STRING;
        action.finalType = componentType;
        addJob(action);
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> List<T> parseListFromFile ( Class<T> componentType, String fileName ) {
        Action action = createAction();
        action.payload = fileName;
        action.type = ActionType.LIST;
        action.medium = Medium.FILE_NAME;
        action.finalType = componentType;
        addJob(action);
        List  list  =  getResult(List.class, action);
        return (List<T>) list;
    }

    @Override
    public <T> T parse ( Class<T> type, String jsonString ) {
        Action action = createAction();
        action.payload = jsonString;
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.STRING;
        action.finalType = type;
        addJob(action);
        return getResult(type, action);
    }

    @Override
    public <T> T parse ( Class<T> type, byte[] bytes ) {
        Action action = createAction();
        action.payload = bytes;
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.BYTES;
        action.finalType = type;
        addJob(action);
        return getResult(type, action);
    }

    @Override
    public <T> T parse ( Class<T> type, byte[] bytes, Charset charset ) {
        Action action = createAction();
        action.payload = bytes;
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.BYTES;
        action.charSet = charset;
        action.finalType = type;
        addJob(action);
        return getResult(type, action);
    }

    @Override
    public <T> T parse ( Class<T> type, CharSequence charSequence ) {
        Action action = createAction();
        action.payload = charSequence.toString ();
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.STRING;
        action.finalType = type;
        addJob(action);
        return getResult(type, action);
    }

    @Override
    public <T> T parse ( Class<T> type, char[] chars ) {
        Action action = createAction();
        action.payload = chars;
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.CHARS;
        action.finalType = type;
        addJob(action);
        return getResult(type, action);
    }

    @Override
    public <T> T parse ( Class<T> type, Reader reader ) {
        Action action = createAction();
        action.payload = reader;
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.READER;
        action.finalType = type;
        addJob(action);
        return getResult(type, action);
    }

    @Override
    public <T> T parse ( Class<T> type, InputStream input ) {
        Action action = createAction();
        action.payload = input;
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.INPUT_STREAM;
        action.finalType = type;
        addJob(action);
        return getResult ( type, action );
    }

    @Override
    public <T> T parse ( Class<T> type, InputStream input, Charset charset ) {
        Action action = createAction();
        action.payload = input;
        action.type = ActionType.TYPED_OBJECT;
        action.medium = Medium.INPUT_STREAM;
        action.finalType = type;
        action.charSet = charset;
        addJob(action);
        return getResult ( type, action );
}
    



    @Override
    public <T> T parseDirect ( Class<T> type, byte[] value ) {
        return parse (type, value);
    }

    @Override
    public <T> T parseAsStream ( Class<T> type, byte[] value ) {
        return parse ( type, value );
    }


    @Override
    public void close () {
        this.workQueue.clear ();
        for (int index = 0; index < workers.length; index++) {
            workers[index].run.set ( false );
        }
        for (int index = 0; index < future.length; index++) {
            future[index].cancel ( true );
        }
        this.pool.shutdown ();

    }
}
