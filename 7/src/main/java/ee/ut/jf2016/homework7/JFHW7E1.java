package ee.ut.jf2016.homework7;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.IntResult2;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;

@JCStressTest
@Description("Testing assignment of dependant variables")
// the descriptions for these are given below in depth.
@Outcome(id = "[0, 0]", expect = ACCEPTABLE_INTERESTING, desc = ":'(")
@Outcome(id = "[1, 0]", expect = ACCEPTABLE_INTERESTING, desc = "JMM PLS")
@Outcome(id = "[0, 1]", expect = ACCEPTABLE_INTERESTING, desc = "WHY U DO DIS JMM")
@Outcome(id = "[1, 1]", expect = ACCEPTABLE, desc = "Expected value")
@State

/*
 Observed state	|  Occurrence  |         Expectation	      |     Interpretation
-----------------------------------------------------------------------------------------
    [0, 0]	    |   14396571   |    ACCEPTABLE_INTERESTING	  |    :'(
    [1, 0]	    |   32048996   |    ACCEPTABLE_INTERESTING	  |    JMM PLS
    [0, 1]	    |   30169143   |    ACCEPTABLE_INTERESTING	  |    WHY U DO DIS JMM
    [1, 1]	    |      40      |          ACCEPTABLE	      |    This is expected

Let's start.

Row 1:
    If the compiler changes the order of the code to be
        r.r2 = b;
        a = 1;

        AND

        r.r1 = a;
        b = 1;

     instead of what's written below. This case will occur. r1 and r2 will be set before a & b gets their value, boom!
     This can be avoided by declaring a and b as volatile.

Row 2:
    If the compiler changes the order of the code to be
        r.r2 = b;
        a = 1;

        AND

        b = 1;
        r.r1 = a;

    this case might occur. setting r2 to b line might be executed before setting b to 1, so this will happen.
    Also, the OS and JVM can stop the thread from executing in random line(timeout), if actor2's code stop right before setting b, this case will occur.
    There's also the case that the changes made in a or b might not propagate to the main memory in time before reading their value.
Row 3:
    Basically the same case as Row 2.

Row 4:
    Now this is interesting. After declaring a and b as volatile, the number of times this case occurs increases dramatically. (40 -> 884869).
    This could either be because the changes to the variables' values did not propagate to the main memory yet,
    or the compiler changed the order of the execution.
*/

public class JFHW7E1 {
    volatile int a;
    volatile int b;

    @Actor
    public void actor1(IntResult2 r) {
        a = 1;
        r.r2 = b;
    }

    @Actor
    public void actor2(IntResult2 r) {
        b = 1;
        r.r1 = a;
    }

}

