package cn.sennri.inception.util;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * https://www.zhihu.com/question/276455629/answer/1259967560
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 5)
@Threads(4)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Slf4j
public class GameUtilsTest {

    @Param(value = {"4"})
    private int length;

    @Benchmark
    public void getRandomAscendNumArray() {
        GameUtils.getRandomAscendNumArray(length,2);
    }

    @Benchmark
    public void getRandomAscendNumArray2() {
        GameUtils.getRandomAscendNumArray2(length,2);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(GameUtilsTest.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();
    }
}