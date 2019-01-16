package it.unitn.disi.ds2.whanau.utils;

/**
 * Sample class to store a pair of values.
 * @param <X> type of the first element.
 * @param <Y> type of the second element.
 */
public class Pair<X, Y> {
    public final X first;
    public final Y second;
    public Pair(X x, Y y) {
        this.first = x;
        this.second = y;
    }

    @Override
    public String toString() {
        return String.format("<%s, %s>", this.first, this.second);
    }
}