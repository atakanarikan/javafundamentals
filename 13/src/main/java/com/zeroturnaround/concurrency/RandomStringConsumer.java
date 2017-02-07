package com.zeroturnaround.concurrency;

import com.zeroturnaround.concurrency.data.Hash;
import com.zeroturnaround.concurrency.data.Password;
import com.zeroturnaround.concurrency.data.Salt;
import com.zeroturnaround.concurrency.util.BramHash;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Consumer class, will keep reading in new random words, feeding them to the hashing algorithm,
 * and checking the result for collisions.
 * The method "run()" should never return, unless a proper shutdown is initiated.
 */
public class RandomStringConsumer implements Runnable {
    private BlockingQueue queue = null;
    private ConcurrentHashMap<Integer, String> indexToPassword = null;
    RandomStringConsumer(BlockingQueue queue, ConcurrentHashMap<Integer, String> indexToPassword){
        this.queue = queue;
        this.indexToPassword = indexToPassword;
    }
    /**
     * This should contain the actual hash testing + result recording logic.
     */
    @Override
    public void run() {
        while (true) {
            try {
                String candidatePassword = (String) queue.take();
                for (int i = 0; i < Salt.salts.size(); i++) {
                    String salt = Salt.salts.get(i);
                    String hash = BramHash.hash(salt + candidatePassword);
                    if (Hash.hashes.indexOf(hash) == i) {
                        indexToPassword.putIfAbsent(i, candidatePassword);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * This should allow you to (thread-safely!) signal this consumer to halt its operations,
     * and thus return from the run() method.
     * Implementation is NOT mandatory if you supply an equally thread-safe alternative.
     */
    public void shutdown() {
    }
}
