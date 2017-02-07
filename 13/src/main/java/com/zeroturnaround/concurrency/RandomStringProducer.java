package com.zeroturnaround.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Producer class, will keep generating new random words to serve as the input for the hashing algorithm.
 * The method "run()" should never return, unless a proper shutdown is initiated.
 */
public class RandomStringProducer implements Runnable {
    BlockingQueue queue = null;

    RandomStringProducer(BlockingQueue queue) {
        this.queue = queue;
    }

    /**
     * This should contain the actual String generation logic.
     */
    @Override
    public void run() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        // StringBuilder is not threadsafe, but in this case this sb object is not being shared with other threads. So this is not an issue.
        StringBuilder sb = new StringBuilder();
        while (true) {
            sb.setLength(0);
            int stringLength = rand.nextInt(1, 16);
            for (int i = 0; i < stringLength; i++) {
                sb.append((char) (rand.nextInt(48, 90)));
            }
            try {
                queue.put(sb.toString());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * This should allow you to (thread-safely!) signal this producer to halt its operations,
     * and thus return from the run() method.
     * Implementation is NOT mandatory if you supply an equally thread-safe alternative.
     */
    public void shutdown() {
    }
}
