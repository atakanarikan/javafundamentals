package ee.ut.jf2016.homework7;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.IntResult3;

import java.util.BitSet;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

/**
 * Created by arikan on 15.10.16.
 */
@JCStressTest
@Description("Testing assignment of dependant variables")

@Outcome(id = "[0, 0, 0]", expect = ACCEPTABLE, desc = "Possible")
@Outcome(id = "[0, 0, 1]", expect = FORBIDDEN, desc = "Can't happen since I'm a cool developer")
@Outcome(id = "[0, 1, 0]", expect = ACCEPTABLE, desc = "Possible")
@Outcome(id = "[0, 1, 1]", expect = FORBIDDEN, desc = "Can't happen since I'm a cool developer")
@Outcome(id = "[1, 0, 0]", expect = ACCEPTABLE, desc = "Possible")
@Outcome(id = "[1, 0, 1]", expect = FORBIDDEN, desc = "Can't happen since I'm a cool developer")
@Outcome(id = "[1, 1, 0]", expect = ACCEPTABLE, desc = "Possible")
@Outcome(id = "[1, 1, 1]", expect = ACCEPTABLE, desc = "Possible")
@State
public class JFHW7E3 {
/* okay here we go:
    The outcomes above are written for case 1. (see actor1 method.)

    My non-so-super original test case is to test the happens-before relationships on volatiles:
    "A write to a volatile field happens-before every subsequent read of that same field."

    If the flow of the code is [writes -> volatile write(field) -> volatile read(field) -> reads] then the writes before the volatile write
    should be visible to the reads after the volatile read.

    -> case 1:
        There shouldn't be any case when r.r3(volatile lock) is set, but x and y doesn't have 1, 1. The list of outcomes says this. Let's see the results:

Observed state	        Occurrence	        Expectation	            Interpretation

  [0, 0, 0]           	45771048	    ACCEPTABLE	        Possible
  [0, 0, 1]	                0	            FORBIDDEN	    Can't happen since I'm a cool developer
  [0, 1, 0]	              56721	        ACCEPTABLE	        Possible
  [0, 1, 1]	                0	            FORBIDDEN	    Can't happen since I'm a cool developer
  [1, 0, 0]	              33509	        ACCEPTABLE	        Possible
  [1, 0, 1]	                0	            FORBIDDEN	    Can't happen since I'm a cool developer
  [1, 1, 0]	             215956	        ACCEPTABLE	        Possible
  [1, 1, 1]	            17990656	    ACCEPTABLE	        Possible

See, I know all about JMM! (I wish)

   -> case 2:
        There shouldn't be any case r.r3 = 1 and x = 0. Let's see the results:

Observed state	        Occurrence	        Expectation	            Interpretation

  [0, 0, 0]           	36597870	        ACCEPTABLE	            Possible
  [0, 0, 1]	                0	            FORBIDDEN	    Can't happen since I'm a cool developer
  [0, 1, 0]	              46278	            ACCEPTABLE	            Possible
  [0, 1, 1]	                0	            FORBIDDEN	    Can't happen since I'm a cool developer
  [1, 0, 0]	              74672	            ACCEPTABLE	            Possible
  [1, 0, 1]	             920121	            ACCEPTABLE	            Possible
  [1, 1, 0]	             238552	            ACCEPTABLE	            Possible
  [1, 1, 1]	            25065487	        ACCEPTABLE	            Possible

    -> case 3:
        What's the point of having a volatile field if you're gonna use it this way? Results:

Observed state	        Occurrence	        Expectation	            Interpretation
  [0, 0, 0]           	40584558	        ACCEPTABLE	            Possible
  [0, 0, 1]	             811529	            ACCEPTABLE	            Possible
  [0, 1, 0]	             85798	            ACCEPTABLE	            Possible
  [0, 1, 1]	             17121	            ACCEPTABLE	            Possible
  [1, 0, 0]	             3083		        ACCEPTABLE	            Possible
  [1, 0, 1]	             76419	            ACCEPTABLE	            Possible
  [1, 1, 0]	             116544	            ACCEPTABLE	            Possible
  [1, 1, 1]	            25730558	        ACCEPTABLE	            Possible


  As can be seen from the results, when used in correct order, volatile write and volatile read provides a really cool feature.

  Last but not least, I didn't change the read order, but the idea (writes->volatile write->volatile read->reads) wouldn't change, so we could only be
  certain about the lines that is after the volatile read.

   -> + points for the hours spent on formatting? -joking

   Best,
   Atakan
 */
    int x, y = 0;
    volatile int lock = 0;
    @Actor
    public void actor1(IntResult3 r) {
        // case 1:
        x = 1;
        y = 1;
        lock = 1;
        /*
        // case 2:
            x = 1;
            lock = 1;
            y = 1;
        // case 3:
            lock = 1;
            x = 1;
            y = 1;

        */
    }

    @Actor
    public void actor2(IntResult3 r) {
        r.r3 = lock;
        r.r2 = y;
        r.r1 = x;
    }
}