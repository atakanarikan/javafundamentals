package hw;

import java.lang.ref.WeakReference;

public class Application {

    public static void main(String[] args) throws Exception {
        PostCollectionTaskRunner runner = new PostCollectionTaskRunnerImpl();
        try {
            Object soonToBeGarbage = new Object();
            Object soonToBeGarbage2 = new Object();
            runner.register(soonToBeGarbage, () -> System.out.println("task1 executed"));
            runner.register(soonToBeGarbage, () -> System.out.println("task2 executed"));
            runner.register(soonToBeGarbage2, () -> System.out.println("task3 executed"));
            runner.register(soonToBeGarbage2, () -> System.out.println("task4 executed"));
            runner.register(soonToBeGarbage2, () -> System.out.println("task5 executed"));
            runner.register(soonToBeGarbage2, () -> System.out.println("task6 executed"));
            soonToBeGarbage = null;
            soonToBeGarbage2 = null;
            causeGarbageCollection();
        } finally {
            runner.shutdown();
        }
    }

    private static void causeGarbageCollection() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
        obj = null;
        while(ref.get() != null) {
            System.gc();
        }
    }
}
