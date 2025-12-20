package academy.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.accessor.AccessorFactory;
import academy.accessor.AccessorStrategy;
import academy.model.Student;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class StudentNameBenchmarkTest {

    private static final String DEFAULT_NAME = "John Doe";
    private static final int DEFAULT_AGE = 21;

    @ParameterizedTest
    @MethodSource("accessorProvider")
    void givenStudentNameBenchmark_whenAccessorCall_thenReturnsCorrectStudentName(AccessorStrategy accessor) {
        Student student = new Student(DEFAULT_NAME, DEFAULT_AGE);

        String result = accessor.get(student);

        assertEquals(DEFAULT_NAME, result);
    }

    private static Stream<AccessorStrategy> accessorProvider() {
        AccessorFactory.AccessorSet accessors = AccessorFactory.createAll();
        return Stream.of(accessors.direct(), accessors.reflection(), accessors.methodHandle(), accessors.lambda());
    }
}
