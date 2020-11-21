package com.neo.web;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@State(value = Scope.Benchmark)
public class BenchMarkTest {

    private static final LongAdder LONG_ADDER_VALUE = new LongAdder();
    private static final AtomicLong ATOMIC_LONG_VALUE = new AtomicLong(0);

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchMarkTest.class.getSimpleName())
                .warmupIterations(5) // 预热
                .measurementTime(TimeValue.seconds(3))
                .measurementIterations(10) // 测试x轮测试性能
                .forks(1)
                .threads(8)
                .result("result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void longAddrIncrementTest() {
        for (int i = 0; i < 100000; i++) {
            LONG_ADDER_VALUE.increment();
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void atomicLongIncrementTest() {
        for (int i = 0; i < 100000; i++) {
            ATOMIC_LONG_VALUE.incrementAndGet();
        }
    }
}

