package org.boon.slumberdb.stores.log;

import org.boon.slumberdb.service.config.DataStoreConfig;
import org.boon.IO;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 6/27/14.
 */
public class BatchFileWriterTest {


    boolean die;
    SeekableByteChannel outputStream = new SeekableByteChannel() {

        @Override
        public int read(ByteBuffer dst) throws IOException {
            return 0;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            if (die) {
                throw new IOException("Bang! I died!");
            }

            return 0;
        }

        @Override
        public long position() throws IOException {
            return 0;
        }

        @Override
        public SeekableByteChannel position(long newPosition) throws IOException {
            return null;
        }

        @Override
        public long size() throws IOException {
            return 0;
        }

        @Override
        public SeekableByteChannel truncate(long size) throws IOException {
            return null;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void close() throws IOException {

        }
    };
    BatchFileWriterForTest writer = new BatchFileWriterForTest();

    ByteBuffer buf() {
        return ByteBuffer.allocate(10).put("Mom".getBytes());
    }

    @Before
    public void setup() {

        writer = new BatchFileWriterForTest();


    }

    @Test
    public void simulateATimeout() throws Exception {
        long time = System.currentTimeMillis();

        writer.init(new DataStoreConfig());
        writer.nextBufferToWrite(buf());

        writer.tick(time);

        String fileName = writer.fileName();
        puts(fileName);

        writer.nextBufferToWrite(buf());
        writer.nextBufferToWrite(buf());
        writer.nextBufferToWrite(buf());
        writer.nextBufferToWrite(buf());

        writer.tick(time + (11 * 60 * 1_000));

        writer.nextBufferToWrite(buf());

        boolean ok = true;


        writer.nextBufferToWrite(buf());

        puts(writer.fileName(), fileName);

        ok &= !fileName.equals(writer.fileName()) ||
                die("fileNames were not suppose to be the same", fileName, writer.fileName());

        fileName = writer.fileName();

        writer.tick(time + (11 * 60 * 1_000) + (11 * 60 * 1_000));
        writer.nextBufferToWrite(buf());
        writer.nextBufferToWrite(buf());


        puts(writer.fileName(), fileName);

        ok &= !fileName.equals(writer.fileName()) ||
                die("fileNames were not suppose to be the same");

        puts(writer.fileName());

    }

    @Test(expected = org.boon.Exceptions.SoftenedException.class)
    public void exceptionTest() throws Exception {

        writer.init(new DataStoreConfig());

        die = true;

        writer.nextBufferToWrite(buf());

    }

    @Test(expected = org.boon.Exceptions.SoftenedException.class)
    public void test() throws Exception {
        BatchFileWriter bw = new BatchFileWriter() {
            protected Path outputDirPath() {

                return IO.path("/var/dir_does_not_exist" + System.currentTimeMillis());
            }

        };

        bw.nextBufferToWrite(buf());

        bw.syncToDisk();


    }

    class BatchFileWriterForTest extends BatchFileWriter {
        protected SeekableByteChannel streamCreator() {
            return outputStream;
        }
    }
}