package ee.ut.jf2016;


import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class Main {
    static final Lock writeLock = new ReentrantReadWriteLock().writeLock();
    private static List<Account> accounts;
    private static PrintStream out = new PrintStream(new BufferedOutputStream(System.out));

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        createAccounts(Integer.parseInt(args[0]));
        createThreads(Integer.parseInt(args[0]));
    }

    private static void createAccounts(int n) {
        accounts = Collections.synchronizedList(new ArrayList<Account>());
        accounts = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            accounts.add(new Account(n));
        }
    }

    private static void createThreads(int n) throws InterruptedException, ExecutionException {
        Set<Donator> donators = new HashSet<>();
        for (Account account : accounts) {
            // collect the remaining accounts from the stream and shuffle them
            List<Account> recipients = accounts.stream().filter(recipient -> !account.equals(recipient)).collect(Collectors.toList());
            Collections.shuffle(recipients);
            donators.add(new Donator(recipients, account));
        }
        // create the thread pool
        ExecutorService exec = Executors.newFixedThreadPool(n);
        out.println(allBalances());
        ExecutorService printer = Executors.newSingleThreadExecutor();
        // printer thread that will print to the buffer of Main
        printer.execute(() -> {
            // run until every donator thread completes their work.
            while(!exec.isShutdown()) {
                try {
                    out.println(allBalances());
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        printer.shutdown();
        exec.invokeAll(donators);
        exec.shutdown();
        out.println(allBalances());
        out.flush();
    }

    private static String allBalances() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        int sum = 0;
        // lock so no transfer can happen while writing to the buffer
        writeLock.lockInterruptibly();
        try {
            for (Account acc : accounts) {
                int accBalance = acc.getBalance();
                sb.append(accBalance);
                sum+=accBalance;
                sb.append(" ");
            }
        } finally {
            writeLock.unlock();
        }
        return sb.append(sum).toString();
    }
}
