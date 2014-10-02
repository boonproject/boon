package org.boon.slumberdb.stores.queue;

import org.boon.slumberdb.service.results.Response;
import org.boon.slumberdb.service.server.ServiceMethod;
import org.boon.slumberdb.stores.DataOutputQueue;

import java.util.concurrent.LinkedTransferQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Richard on 6/27/14.
 */
public class DataOutputQueueTransferQueue implements DataOutputQueue {

    private final int pollWaitMS;
    private LinkedTransferQueue<Response> queue = new LinkedTransferQueue<>();

    public DataOutputQueueTransferQueue(int pollWaitMS) {
        this.pollWaitMS = pollWaitMS;
    }


    @ServiceMethod
    @Override
    public void put(Response result) {

        if (queue.hasWaitingConsumer()) {
            if (!queue.tryTransfer(result)) {
                queue.offer(result);
            }
        } else {

            queue.offer(result);
        }
    }

    @Override
    public Response poll() {
        return queue.poll();
    }

    @Override
    public Response take() {
        Response result = queue.poll();

        if (result != null) {
            return result;
        }


        try {

//            for (int index=0; index<100; index++) {
//                result = queue.poll(20, MICROSECONDS);
//                if (result != null) {
//                    return result;
//                }
//            }
            return queue.poll(pollWaitMS, MILLISECONDS);

        } catch (InterruptedException e) {
            Thread.interrupted();
            result = null;
        }

        return result;
    }

    @Override
    public String toString() {
        return "DataOutputQueueTransferQueue{" +
                "pollWaitMS=" + pollWaitMS +
                ", \nqueue=\n" + queue +
                '}';
    }
}
