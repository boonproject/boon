package org.boon.benchmark.datarepo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class MeasuredRun implements Runnable {

    private final String key;
    private String name;

    private int warmupCount = 1_000;
    private int iterations = 10_000;

    private long startTime;
    private long endTime;
    private long totalTime;
    private long time;

    public String name() {
        return name;
    }

    public long startMemoryFree() {
        return startMemory;
    }

    public long endMemoryFree() {
        return endMemory;
    }

    private long startMemory;
    private long endMemory;

    public long time() {
        return totalTime / iterations;
    }

    private Map<String, List<MeasuredRun>> results;


    public MeasuredRun(String name, int warmupCount, int iterations,
                       Map<String, List<MeasuredRun>> results) {
        this.name = name;
        this.warmupCount = warmupCount;
        this.iterations = iterations;
        this.results = results;
        this.key = name + System.nanoTime();
    }


    protected abstract void init();

    protected abstract void test();

    @Override
    public void run() {
        this.init();

        for (int index = 0; index < warmupCount; index++) {
            this.test();
        }

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {

        }
        startTime = System.nanoTime();

        startMemory = Runtime.getRuntime().freeMemory();

        for (int index = 0; index < iterations; index++) {
            this.test();
        }

        endTime = System.nanoTime();
        endMemory = Runtime.getRuntime().freeMemory();


        totalTime = endTime - startTime;
        time = totalTime / iterations;
        List<MeasuredRun> runs = results.get(this.name);
        if (runs == null) {
            runs = Collections.synchronizedList(new ArrayList());
            results.put(this.name, runs);
        }
        runs.add(this);
    }

    @Override
    public String toString() {
        return "MeasuredRun{" +
                "name=" + name +
                ", time='" + (totalTime / iterations / 1000.0) + "'     micro seconds" +
                '}';
    }
}
