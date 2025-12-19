package academy.benchmark;

import academy.accessor.AccessorFactory;
import academy.accessor.AccessorStrategy;
import academy.model.Student;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Бенчмарк сравнивает разные способы получения имени студента.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 8, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2)
public class StudentNameBenchmark {

    private static final String DEFAULT_NAME = "John Doe";
    private static final int DEFAULT_AGE = 21;

    private Student student;
    private AccessorStrategy directAccessor;
    private AccessorStrategy reflectionAccessor;
    private AccessorStrategy methodHandleAccessor;
    private AccessorStrategy lambdaAccessor;

    @Setup
    public void setUp() {
        student = new Student(DEFAULT_NAME, DEFAULT_AGE);
        AccessorFactory.AccessorSet accessors = AccessorFactory.createAll();
        directAccessor = accessors.direct();
        reflectionAccessor = accessors.reflection();
        methodHandleAccessor = accessors.methodHandle();
        lambdaAccessor = accessors.lambda();
    }

    @Benchmark
    public void directCall(Blackhole bh) {
        bh.consume(directAccessor.get(student));
    }

    @Benchmark
    public void reflectionCall(Blackhole bh) {
        bh.consume(reflectionAccessor.get(student));
    }

    @Benchmark
    public void methodHandleCall(Blackhole bh) {
        bh.consume(methodHandleAccessor.get(student));
    }

    @Benchmark
    public void lambdaMetafactoryCall(Blackhole bh) {
        bh.consume(lambdaAccessor.get(student));
    }
}

