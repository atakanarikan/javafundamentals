package ee.ut.jf2016;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by arikan on 21.10.16.
 */
public class Donator implements Callable<String> {
    List<Account> recipients;
    Account source;

    public Donator(List<Account> recipients, Account source) {
        this.recipients = recipients;
        this.source = source;
    }

    @Override
    public String call() throws Exception {
        for (Account recipient : recipients) {
            // wait for the writing to the buffer to finish, then start transferring
           Main.writeLock.lock();
            try {
                source.transfer(recipient, 1);
            } finally {
                Main.writeLock.unlock();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
        return "";
    }
}
