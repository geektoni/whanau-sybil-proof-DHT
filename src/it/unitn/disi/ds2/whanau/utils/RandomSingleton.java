package it.unitn.disi.ds2.whanau.utils;

import java.util.Random;

public class RandomSingleton {
    private static RandomSingleton instance;
    private Random rnd;

    private RandomSingleton() {
        rnd = new Random();
    }

    private RandomSingleton(long seed)
    {
        rnd = new Random(seed);
    }

    public static RandomSingleton getInstance(long seed) {
        if(instance == null) {
            instance = new RandomSingleton(seed);
        }
        return instance;
    }

    public double nextDouble() {
        return rnd.nextDouble();
    }

    public int nextInt(int bound)
    {
        return rnd.nextInt(bound);
    }

    public int nextInt()
    {
        return rnd.nextInt();
    }
}