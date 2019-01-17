package it.unitn.disi.ds2.whanau.utils;

import peersim.core.Node;

import java.util.Comparator;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        if (this.second instanceof Node)
            return String.format("<%s, Node>", this.first);
        return String.format("<%s, %s>", this.first, this.second);
    }

    public static class FingersComparator implements Comparator<Pair<Integer, Node>> {
        public int compare(Pair<Integer, Node> object1, Pair<Integer, Node> object2) {
            return object1.first - object2.first;
        }
    }

    public static class SuccComparator implements Comparator<Pair<Integer, String>> {
        public int compare(Pair<Integer, String> object1, Pair<Integer, String> object2) {
            return object1.first - object2.first;
        }
    }

}