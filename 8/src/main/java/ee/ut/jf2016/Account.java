package ee.ut.jf2016;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by arikan on 21.10.16.
 */
public class Account {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private volatile int balance;

    Account(int balance) {
        this.balance = balance;
    }

    void transfer(Account recipient, int amount) {
        this.setBalance(this.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);
    }

    int getBalance() {
        this.readLock.lock();
        try {
            return this.balance;
        } finally {
            this.readLock.unlock();
        }
    }

    private void setBalance(int balance) {
        this.writeLock.lock();
        try {
            this.balance = balance;
        } finally {
            this.writeLock.unlock();
        }
    }
}
