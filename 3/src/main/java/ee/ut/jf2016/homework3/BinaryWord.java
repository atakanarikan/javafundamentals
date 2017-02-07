package ee.ut.jf2016.homework3;

import java.util.ArrayList;

/**
 * Created by Atakan Arıkan on 14.09.2016.
 */
public class BinaryWord implements Complementable <BinaryWord> {
    private ArrayList<Boolean> word;
    public BinaryWord (ArrayList<Boolean> word) {
        this.word = word;
    }
    @Override
    public String toString() {
        return "BinaryWord{" +
                "word=" + word.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryWord that = (BinaryWord) o;

        return word.equals(that.word);

    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    public ArrayList<Boolean> getWord() {
        return word;
    }

    public void setWord(ArrayList<Boolean> word) {
        this.word = word;
    }

    @Override
    public BinaryWord complement() {
        ArrayList<Boolean> newOrder = new ArrayList<>();
        for (Boolean x : word) {
            newOrder.add(!x);
        }
        this.word = newOrder;
        return this;
    }
}
