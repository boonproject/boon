package org.boon.slumberdb.stores.log;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.Exceptions;
import org.boon.IO;
import org.boon.core.Sys;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.boon.Boon.puts;
import static org.boon.primitive.Lng.str;

/**
 * Created by Richard on 6/27/14.
 */
public class BatchFileWriter implements TimeAware {


    public final static String FORMAT_PATTERN =
            Sys.sysProp("NFL.USER_DATA_TRACKER.FILE_NAME_FORMAT_PATTERN",
                    "%s/user_data_collection_%s_%s_%s.json");
    public final static String SERVER_NAME =
            Sys.sysProp("NFL.USER_DATA_TRACKER.SERVER_NAME",
                    "server1");
    /**
     * Default log size 20,000,000, if beyond 20M create a new log *
     */
    public static int FILE_SIZE_BYTES = Integer.parseInt(System.getProperty("NFL.USER_DATA_TRACKER.FILE_SIZE_BYTES", "" + 10_000_000));
    /**
     * Default log batch time 10 minutes, if beyond ten, then create a new log.  *
     */
    public static int FILE_TIMEOUT_MINUTES = Integer.parseInt(System.getProperty("NFL.USER_DATA_TRACKER.FILE_TIMEOUT_MINUTES", "10"));
    private static int FILE_TIMEOUT_MILISECONDS = FILE_TIMEOUT_MINUTES * 60 * 1_000;
    /**
     * Default log size 20,000,000, if beyond 20M create a new log *
     */
    public static int FLUSH_EVERY_N_BYTES = Integer.parseInt(System.getProperty("NFL.USER_DATA_TRACKER.FLUSH_EVERY_N_BYTES", "" + 20_000_000));
    private volatile long buffersSent = 0;
    private volatile long bytesTransferred = 0;
    private volatile long bytesSinceLastFlush = 0;
    private volatile long totalBytesTransferred = 0;
    private volatile long numFiles = 0;
    /**
     * Tracks the current time, used for log create timeouts and log flush.
     */
    private AtomicLong time = new AtomicLong();
    private AtomicBoolean fileTimeOut = new AtomicBoolean();
    private AtomicLong fileStartTime = new AtomicLong();
    private AtomicBoolean error = new AtomicBoolean(false);
    private Path outputDir;
    private volatile String fileName = String.format(FORMAT_PATTERN,
            outputDir, numFiles, System.currentTimeMillis(), SERVER_NAME);
    private SeekableByteChannel outputStream;
    private ByteBuffer outputBuffer;
    private boolean dirty;


    private DataStoreConfig config;


    /**
     * Recieves a tick from our clock.
     */
    public void tick(long time) {
        this.time.set(time);
        long startTime = fileStartTime.get();
        long duration = time - startTime;

        if (duration > FILE_TIMEOUT_MILISECONDS) {
            fileTimeOut.set(true);
        }
    }

    /**
     * Reads the number of files created for status check.
     */
    public long numFiles() {
        return numFiles;
    }

    /**
     * Reads total bytes transferred for status check.
     */
    public long totalBytesTransferred() {
        return totalBytesTransferred;
    }


    /**
     * flush to disk.
     */
    public boolean syncToDisk() {

        /** if we have a stream and we are dirty then flush. */
        if (outputStream != null && dirty) {

            try {
                //outputStream.flush ();

                if (outputStream instanceof FileChannel) {
                    FileChannel channel = (FileChannel) outputStream;
                    channel.force(true);
                }
                dirty = false;
                return true;
            } catch (Exception ex) {
                cleanupOutputStream();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Attempts to close down log stream.
     */
    private void cleanupOutputStream() {

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {

                e.printStackTrace(System.err);
            } finally {
                outputStream = null;
            }
        }

    }

    /**
     * Used for status message to report the bytes sent to files.
     * So bytesTransferred is copied from a register setting to main memory (sort of).
     */
    public long bytesTransferred() {
        return bytesTransferred;
    }

    public long averageBufferSize() {
        return buffersSent != 0 ? totalBytesTransferred / buffersSent : 0;
    }

    /**
     * Writes a buffer of data to the log system.
     */
    public void nextBufferToWrite(final ByteBuffer bufferOut) throws InterruptedException {
        dirty = true;
        final int size = bufferOut.limit();


        write(bufferOut);

        /* only increment bytes transferred after a successful write. */
        if (!error.get()) {
            totalBytesTransferred += size;
            bytesTransferred += size;
            bytesSinceLastFlush += size;
            buffersSent++;
        }


        if (this.bytesTransferred >= FILE_SIZE_BYTES || fileTimeOut.get()) {

            try {
                outputStream.close();
            } catch (IOException e) {
                cleanupOutputStream();
                e.printStackTrace(System.err);
            } finally {
                outputStream = null;
            }
        }


    }

    /**
     * Write the actual data to disk.
     */
    private void write(final ByteBuffer bufferOut) throws InterruptedException {

        initOutputStream();
        try {
            if (outputStream != null) {

                outputStream.write(bufferOut);

            } else {
                error.set(true);
            }

            if (bytesSinceLastFlush > FLUSH_EVERY_N_BYTES) {
                syncToDisk();
                bytesSinceLastFlush = 0;
            }
        } catch (ClosedByInterruptException cbie) {
            throw new InterruptedException("File closed by interruption");
        } catch (Exception e) {
            cleanupOutputStream();
            error.set(true);
            e.printStackTrace(System.err);

            diagnose();

            Exceptions.handle(e);
        }

    }

    public void diagnose() {

        Objects.requireNonNull(fileName(), "the filename should not be null, " +
                "you have misconfigured this service, fatal error");

        final Path path =
                IO.path(fileName());

        puts("in diagnose");

        puts("Filename           :", path.toAbsolutePath());
        puts("File exists?       :", Files.exists(path));
        puts("File writeable?    :", Files.isWritable(path));


        puts("Output dir                :", outputDir.toAbsolutePath());
        puts("Output dir  exists?       :", Files.exists(outputDir));
        puts("Output dir  writeable?    :", Files.isWritable(outputDir));

        if (!Files.isWritable(outputDir) || !Files.exists(outputDir)) {
            error.set(true);
        }

        try {
            FileStore fileStore = Files.getFileStore(path.getParent());
            puts("Total space           :", str(fileStore.getTotalSpace()));
            puts("Use-able space        :", str(fileStore.getUsableSpace()));
            puts("Free Space            :", str(fileStore.getUnallocatedSpace()));
            puts("type                  :", fileStore.type());
            puts("name                  :", fileStore.name());
            puts("read-only             :", fileStore.isReadOnly());

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public String outputDir() {
        return outputDir.toString();
    }

    /**
     * Initialize the output stream.
     */
    private void initOutputStream() {

        long time = this.time.get();


        if (error.get() || this.totalBytesTransferred == 0) {
            cleanupOutputStream();
            error.set(false);
            time = System.nanoTime() / 1_000_000;
        }

        if (outputStream != null) {
            return;
        }


        fileName = String.format(FORMAT_PATTERN,
                this.outputDirPath().toString(), numFiles, time, SERVER_NAME);


        try {
            fileTimeOut.set(false);
            outputStream = streamCreator();

            fileStartTime.set(time);


            bytesTransferred = 0;
            bytesSinceLastFlush = 0;


        } catch (Exception ex) {
            cleanupOutputStream();
            error.set(true);
            Exceptions.handle(ex);
        } finally {
            numFiles++;
        }
    }

    protected SeekableByteChannel streamCreator() throws Exception {

        SeekableByteChannel byteChannel = Files.newByteChannel(IO.path(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        if (outputBuffer == null) {
            outputBuffer = ByteBuffer.allocateDirect(FILE_SIZE_BYTES + 1_000_000);
        }

        return byteChannel;
    }


    public String fileName() {
        return fileName;
    }


    protected Path outputDirPath() {
        return IO.path(this.outputDir);
    }

    public void setError() {
        this.error.set(true);
    }

    public void init(DataStoreConfig config) {
        this.config = config;

        this.outputDir = IO.path(config.outputDirectory());


    }

}
