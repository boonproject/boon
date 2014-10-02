package org.boon.slumberdb.leveldb;


import org.boon.Exceptions;
import org.boon.Logger;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.boon.slumberdb.KeyValueStore;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Boon.configurableLogger;


/**
 * Stores key and values in LevelDB
 */
public class LevelDBKeyValueStore implements KeyValueStore<byte[], byte[]> {


    /**
     * The "filename" of the level db file.
     * This is really a directory.
     */
    private final String fileName;

    /**
     * Flag to denote if we are using JNI or not.
     */
    private final boolean usingJNI;

    /**
     * Setup the options for the database.
     * Also used for flush operation which closes the DB
     * and then reopens it.
     */
    private final Options options;
    /**
     * Actual database implementation.
     */
    DB database;
    WriteOptions flush = new WriteOptions();
    {
        flush.sync(true);
    }
    WriteOptions writeOptions = new WriteOptions();
    AtomicLong putAllWriteCount = new AtomicLong();
    /**
     * Logger.
     */
    private Logger logger = configurableLogger(LevelDBKeyValueStore.class);


    /**
     * Creates a level db database with the default options.
     */
    public LevelDBKeyValueStore(String fileName) {
        this(fileName, null, false);
    }

    /**
     * Creates a level db database with the options passed
     * Also allows setting up logging or not.
     *
     * @param fileName fileName
     * @param options  options
     * @param log      turn on logging
     */
    public LevelDBKeyValueStore(String fileName, Options options, boolean log) {
        this.fileName = fileName;
        File file = new File(fileName);


        if (options == null) {
            logger.info("Using default options");
            options = defaultOptions();
        }

        this.options = options;

        if (log) {
            options.logger(new org.iq80.leveldb.Logger() {
                @Override
                public void log(String message) {
                    logger.info("FROM DATABASE LOG", message);
                }
            });
        }


        usingJNI = openDB(file, options);
    }

    /**
     * Configures default options.
     *
     * @return
     */
    private Options defaultOptions() {

        Options options = new Options();
        options.createIfMissing(true);
        options.blockSize(32_768); //32K
        options.cacheSize(67_108_864);//64MB
        return options;
    }

    /**
     * Opens the database
     *
     * @param file    filename to open
     * @param options options
     * @return
     */
    private boolean openDB(File file, Options options) {

        try {
            database = JniDBFactory.factory.open(file, options);
            logger.info("Using JNI Level DB");
            return true;
        } catch (IOException ex1) {
            try {
                database = Iq80DBFactory.factory.open(file, options);
                logger.info("Using Java Level DB");
                return false;
            } catch (IOException ex2) {
                return Exceptions.handle(Boolean.class, ex2);
            }
        }

    }

    /**
     * Puts an item in the key value store.
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void put(byte[] key, byte[] value) {
        database.put(key, value);
    }

    /**
     * Puts values into the key value store in batch mode
     *
     * @param values values
     */
    @Override
    public void putAll(Map<byte[], byte[]> values) {

        WriteBatch batch = database.createWriteBatch();

        try {

            for (Map.Entry<byte[], byte[]> entry : values.entrySet()) {
                batch.put(entry.getKey(), entry.getValue());
            }


            if (putAllWriteCount.addAndGet(values.size()) > 10_000) {
                putAllWriteCount.set(0);
                database.write(batch, flush);
            } else {
                database.write(batch, writeOptions);
            }

        } finally {
            closeBatch(batch);
        }
    }


    public void flush() {

        WriteBatch batch = database.createWriteBatch();

        try {

            database.write(batch, flush);


        } finally {
            closeBatch(batch);
        }
    }

    private void closeBatch(WriteBatch batch) {
        try {
            batch.close();
        } catch (IOException e) {
            Exceptions.handle(e);
        }
    }

    /**
     * Remove all of the keys passed.
     *
     * @param keys keys
     */
    @Override
    public void removeAll(Iterable<byte[]> keys) {

        WriteBatch batch = database.createWriteBatch();

        try {

            for (byte[] key : keys) {
                batch.delete(key);
            }

            database.write(batch);

        } finally {
            closeBatch(batch);
        }

    }


    /**
     * Remove items from list
     *
     * @param key
     */
    @Override
    public void remove(byte[] key) {
        database.delete(key);
    }


    /**
     * Search to a certain location.
     *
     * @param startKey startKey
     * @return
     */
    @Override
    public KeyValueIterable<byte[], byte[]> search(byte[] startKey) {

        final DBIterator iterator = database.iterator();
        iterator.seek(startKey);


        return new KeyValueIterable<byte[], byte[]>() {
            @Override
            public void close() {
                closeIterator(iterator);
            }

            @Override
            public Iterator<Entry<byte[], byte[]>> iterator() {
                return new Iterator<Entry<byte[], byte[]>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<byte[], byte[]> next() {

                        Map.Entry<byte[], byte[]> next = iterator.next();
                        return new Entry<>(next.getKey(), next.getValue());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };

    }

    private void closeIterator(DBIterator iterator) {
        try {
            iterator.close();
        } catch (IOException e) {
            Exceptions.handle(e);
        }
    }

    /**
     * Load all of the key/values from the store.
     *
     * @return
     */
    @Override
    public KeyValueIterable<byte[], byte[]> loadAll() {

        final DBIterator iterator = database.iterator();
        iterator.seekToFirst();


        return new KeyValueIterable<byte[], byte[]>() {
            @Override
            public void close() {
                closeIterator(iterator);
            }

            @Override
            public Iterator<Entry<byte[], byte[]>> iterator() {
                return new Iterator<Entry<byte[], byte[]>>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<byte[], byte[]> next() {

                        Map.Entry<byte[], byte[]> next = iterator.next();
                        return new Entry<>(next.getKey(), next.getValue());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }

    @Override
    public Collection<byte[]> loadAllKeys() {

        List<byte[]> keys = new ArrayList<>();
        DBIterator iterator = null;

        try {
            iterator = database.iterator();

            iterator.seekToFirst();

            while (iterator.hasNext()) {
                final Map.Entry<byte[], byte[]> next = iterator.next();
                keys.add(next.getKey());
            }
        } finally {
            try {
                if (iterator != null) {
                    iterator.close();
                }
            } catch (IOException e) {
                Exceptions.handle(e);
            }
        }
        return keys;
    }

    /**
     * Get the key from the store
     *
     * @param key key
     * @return value from store at location key
     */
    @Override
    public byte[] load(byte[] key) {
        return database.get(key);
    }

    /**
     * Keys are expected to be sorted
     *
     * @param keys
     * @return
     */
    @Override
    public Map<byte[], byte[]> loadAllByKeys(Collection<byte[]> keys) {

        if (keys == null || keys.size() == 0) {
            return Collections.EMPTY_MAP;
        }

        Map<byte[], byte[]> results = new LinkedHashMap<>(keys.size());

        DBIterator iterator = null;

        try {
            iterator = database.iterator();

            iterator.seek(keys.iterator().next());

            while (iterator.hasNext()) {
                final Map.Entry<byte[], byte[]> next = iterator.next();
                results.put(next.getKey(), next.getValue());
            }
        } finally {
            try {
                if (iterator != null) {
                    iterator.close();
                }
            } catch (IOException e) {
                Exceptions.handle(e);
            }
        }
        return results;

    }


    /**
     * Close the database connection.
     */
    @Override
    public void close() {
        try {
            flush();
            database.close();
        } catch (Exception e) {
            Exceptions.handle(e);
        }
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

}
