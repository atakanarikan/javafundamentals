package ee.ut.jf2016.homework3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeworkTest {
    @Test
    public void testBinaryWord() {
        ArrayList<Boolean> xx = new ArrayList<>();
        xx.add(false);
        xx.add(true);
        xx.add(false);
        xx.add(false);
        ArrayList<Boolean> yy = new ArrayList<>();
        yy.add(true);
        yy.add(false);
        yy.add(true);
        yy.add(true);
        Complementable<BinaryWord> boringBinaryWord = new BinaryWord(xx);
        Complementable<BinaryWord> überBoringBinaryWord = new BinaryWord(yy);
        assertEquals(überBoringBinaryWord, boringBinaryWord.complement());
        assertEquals(boringBinaryWord, boringBinaryWord.complement().complement());
        assertEquals(boringBinaryWord.complement(), boringBinaryWord.complement().complement().complement());
        xx = new ArrayList<>();
        Complementable<BinaryWord> emptyBinaryWord = new BinaryWord(xx);
        assertEquals(emptyBinaryWord, emptyBinaryWord.complement().complement());
        assertEquals(emptyBinaryWord.complement(), emptyBinaryWord.complement().complement().complement());
    }

    @Test
    public void testWholeNumber() {
        WholeNumber boringWholeNumber = new WholeNumber(1);
        WholeNumber ultraBoringWholeNumber = new WholeNumber(-1);
        WholeNumber kindaCoolWholeNumber = new WholeNumber(0);
        assertEquals(ultraBoringWholeNumber, boringWholeNumber.complement());
        assertEquals(boringWholeNumber, boringWholeNumber.complement().complement());
        assertEquals(kindaCoolWholeNumber, kindaCoolWholeNumber.complement());
        assertEquals(kindaCoolWholeNumber, kindaCoolWholeNumber.complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement().complement());
    }
    @Test
    public void testUnique() {
        List<Object> expectedResult = new ArrayList<>();
        List<? extends Object> actualResult = Homework.unique(Arrays.asList(1, 2, 4), Arrays.asList(1, 2, 3), Arrays.asList(1.0, 2.0), Arrays.asList("a", "b"));
        expectedResult.add(1.0);
        expectedResult.add(2.0);
        expectedResult.add("a");
        expectedResult.add("b");
        expectedResult.add(3);
        expectedResult.add(4);
        assertEquals(expectedResult, actualResult);
        expectedResult = new ArrayList<>();
        actualResult = Homework.unique();
        assertEquals(expectedResult, actualResult);
        actualResult = Homework.unique(Arrays.asList(1, 2, 4), Arrays.asList(1, 2, 4), Arrays.asList(1, 2, 4), Arrays.asList(1, 2, 4));
        assertEquals(expectedResult, actualResult);
    }
}
