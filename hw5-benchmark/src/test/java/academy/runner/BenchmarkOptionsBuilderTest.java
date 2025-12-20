package academy.runner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import academy.benchmark.StringConcatBenchmark;
import academy.benchmark.StudentNameBenchmark;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openjdk.jmh.runner.options.Options;

class BenchmarkOptionsBuilderTest {

    private BenchmarkOptionsBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new BenchmarkOptionsBuilder();
    }

    @ParameterizedTest
    @MethodSource("benchmarkClassProvider")
    void givenBenchmarkOptionsBuilder_whenBuild_thenReturnsOptions(Class<?> benchmarkClass) {
        Options options = builder.build(benchmarkClass);

        assertNotNull(options);
    }

    private static Stream<Arguments> benchmarkClassProvider() {
        return Stream.of(Arguments.of(StringConcatBenchmark.class), Arguments.of(StudentNameBenchmark.class));
    }
}
