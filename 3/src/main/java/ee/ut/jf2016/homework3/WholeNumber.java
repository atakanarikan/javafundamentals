package ee.ut.jf2016.homework3;

/**
 * Created by Atakan Arıkan on 14.09.2016.
 */
public class WholeNumber implements Complementable<WholeNumber> {
    private int value;

    public WholeNumber(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WholeNumber that = (WholeNumber) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public WholeNumber complement() {
        return new WholeNumber(-1*value);
    }
}
