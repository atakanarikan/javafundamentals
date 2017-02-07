package ee.ut.jf2016.homework7;

/**
 * Created by arikan on 13.10.16.
 */

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.BooleanResult2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

/*
 Observed state	 |  Occurrence  |        Expectation
--------------------------------------------------------------
 [false, false]	 |       0      |         FORBIDDEN          |
 [true,  false]	 |    311258    |   ACCEPTABLE_INTERESTING   |
 [false,  true]	 |    839544    |   ACCEPTABLE_INTERESTING   |
 [true,   true]	 |   31223638   |         ACCEPTABLE	     |

Row 1:
    This case never happens since none of the executed lines sets 0(false) to any of the bits. At least not in a deterministic machine

Row 2:
    Since BitSet is not actually a set of objects, and is only just a value, it is possible that one actor reads the data,
    and while this actor is writing a new value, second actor reads the old value from memory and writes a new one.
    Therefore one actor's output overwrites the other's.

    This can be solved by using atomic operations. An implementation of AtomicBitSet will remove this problem.
    However, using an volatile BitSet will not solve this, since it doesn't mean the values held in BitSet will be volatile.

Row 3:
    Same case as Row 2

Row 4:
    Everything works out well.

*/
@JCStressTest
@Description("Testing setting elements of BitSet")
@State
public class JFHW7E2 {

    BitSet bitSet = new BitSet();

    @Actor
    public void actor1(BooleanResult2 r) {
        bitSet.set(0);
    }

    @Actor
    public void actor2(BooleanResult2 r) {
        bitSet.set(1);
    }

    @Arbiter
    public void arbiter(BooleanResult2 r) {
        r.r1 = bitSet.get(0);
        r.r2 = bitSet.get(1);
    }
}
