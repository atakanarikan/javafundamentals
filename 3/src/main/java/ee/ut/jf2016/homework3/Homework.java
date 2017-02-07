package ee.ut.jf2016.homework3;

import java.util.*;

public class Homework {

    public static void main(String[] args) {

    }
    static List<? extends Object> unique(List<? extends Object>... myLists){
        HashMap<Object, Integer> freqs = new HashMap<>();
        for (List<?> list: myLists) {
            for (Object o: list) {
                if (freqs.containsKey(o)) {
                    freqs.put(o, freqs.get(o)+1);
                }
                freqs.putIfAbsent(o, 1);
            }
        }
        List<Object> result = new ArrayList<>();
        // y u no stream :'(
        for (Object key: freqs.keySet()) {
            if (freqs.get(key) == 1) {
                result.add(key);
            }
        }
        return result;
    }
}
