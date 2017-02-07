package com.zeroturnaround.concurrency;

import com.zeroturnaround.concurrency.data.Hash;
import com.zeroturnaround.concurrency.data.Salt;
import com.zeroturnaround.concurrency.util.BramHash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Sets up the required number of producer and consumer threads,
 * coordinates their information exchange,
 * keeps track of the discovered collisions (and only stores the FIRST one found!),
 * and takes care of a clean shutdown, once all passwords have a found collision.
 */
public class CollisionFinder {
    private static ConcurrentHashMap<Integer, String> indexToPassword = new ConcurrentHashMap<>();

    /**
     * Read the data provided by the "Salt" and "Hash" classes, and for each find a collision.
     * A collision happens when 2 different inputs generate the same hash.
     * This is dangerous in password-validation systems that use hash-codes (and in many other applications),
     * as the system will wrongfully assume the user to have supplied the correct password, allowing an attacker to login.
     * <p>
     * N.B. 1: If by chance your randomly generate the actual original password, then this is fine too of course!
     * N.B. 2: As you by now, no doubt have seen, the elements in "Password", "Salt" and "Hash" are all in the same order,
     * as in, the first password, with the first salt, results in the first hash.
     * When returning from this method, make sure your password guesses are in the same order!
     */
    public List<String> findCollidingPasswords() {
        BlockingQueue queue = new ArrayBlockingQueue(1000);
        RandomStringProducer producer = new RandomStringProducer(queue);
        RandomStringConsumer consumer = new RandomStringConsumer(queue, indexToPassword);

        ExecutorService producerPool = Executors.newFixedThreadPool(2);
        producerPool.execute(producer);
        producerPool.execute(producer);

        ExecutorService consumerPool = Executors.newFixedThreadPool(4);
        consumerPool.execute(consumer);
        consumerPool.execute(consumer);
        consumerPool.execute(consumer);
        consumerPool.execute(consumer);

        List<String> result = new ArrayList<>(10);
        while (result.size() < 10) {
            result.clear();
            result.addAll(indexToPassword.values());
        }

        producerPool.shutdownNow();
        consumerPool.shutdownNow();

        return result;
    }

}
