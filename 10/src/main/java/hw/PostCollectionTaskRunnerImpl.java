package hw;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PostCollectionTaskRunnerImpl implements PostCollectionTaskRunner {

    /*
     === jvm options ===
        -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx64M


     === GC LOGS ===
        2016-11-06T11:50:34.155+0200:
            [GC (System.gc())
                [PSYoungGen: 2949K->656K(18944K)] 2949K->664K(62976K), 0,0020395 secs]
                /** PSYoungGen: 2949K->656K(18944K) MEANS THAT 2949-656 = 2293K has been freed. **\
            [Times: user=0,01 sys=0,00, real=0,00 secs]

        2016-11-06T11:50:34.157+0200:
            [Full GC (System.gc())
                //* 18944K -> total heap size*\\
                [PSYoungGen: 656K->0K(18944K)]
                //* 656K HAS BEEN FREED *\\
                [ParOldGen: 8K->578K(44032K)] 664K->578K(62976K),
                //* 8K->578K since freed memory from young gen arrived here, but in total 86K freed. *\\
                [Metaspace: 4121K->4121K(1056768K)], 0,0063422 secs]
            [Times: user=0,01 sys=0,00, real=0,01 secs]

        Heap
         PSYoungGen      total 18944K, used 819K [0x00000000feb00000, 0x0000000100000000, 0x0000000100000000)
          eden space 16384K, 5% used [0x00000000feb00000,0x00000000febccec8,0x00000000ffb00000)
          from space 2560K, 0% used [0x00000000ffb00000,0x00000000ffb00000,0x00000000ffd80000)
          to   space 2560K, 0% used [0x00000000ffd80000,0x00000000ffd80000,0x0000000100000000)
         ParOldGen       total 44032K, used 578K [0x00000000fc000000, 0x00000000feb00000, 0x00000000feb00000)
          object space 44032K, 1% used [0x00000000fc000000,0x00000000fc090a50,0x00000000feb00000)
         Metaspace       used 4188K, capacity 4798K, committed 4992K, reserved 1056768K
          class space    used 473K, capacity 539K, committed 640K, reserved 1048576K

     */
    private ConcurrentHashMap<PhantomReference<Object>, List<Runnable>> tasks;
    private ConcurrentHashMap<Integer, PhantomReference<Object>> map;
    private ReferenceQueue<Object> queue;
    private volatile int handledObjects = 0;
    private volatile int registeredObjects = 0;
    private volatile boolean firstObjectArrived = false;
    private ExecutorService finalizer = Executors.newSingleThreadExecutor();
    PostCollectionTaskRunnerImpl() {
        queue = new ReferenceQueue<>();
        tasks = new ConcurrentHashMap<>();
        map = new ConcurrentHashMap<>();
        finalizer.submit(() -> {
            while (!firstObjectArrived || handledObjects < registeredObjects) {
                Reference<?> collected;
                try {
                    collected = queue.remove();
                    if (tasks.containsKey(collected)) {
                        handledObjects++;
                        List<Runnable> currentTasks = tasks.get(collected);
                        currentTasks.forEach(Runnable::run);
                        tasks.remove(collected);
                        map.values().remove(collected);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void register(Object o, Runnable task) {
        firstObjectArrived = true;
        int hash = o.hashCode(); // avoid keeping a reference to the object, use hash instead.
        map.putIfAbsent(hash, new PhantomReference<>(o, queue));
        PhantomReference<Object> phantomReference = map.get(hash);
        tasks.putIfAbsent(phantomReference, Collections.synchronizedList(new ArrayList<>()));
        List<Runnable> current = tasks.get(phantomReference);
        current.add(task);
        tasks.put(phantomReference, current);
        registeredObjects = map.keySet().size();
    }

    @Override
    public void shutdown() throws Exception {
        finalizer.shutdown();
    }
}
