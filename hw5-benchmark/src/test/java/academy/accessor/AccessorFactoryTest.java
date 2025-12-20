package academy.accessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.model.Student;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AccessorFactoryTest {

    private static final String STUDENT_NAME = "Test Student";
    private static final int STUDENT_AGE = 20;

    @ParameterizedTest
    @MethodSource("accessorProvider")
    void givenAccessorFactory_whenCreateAll_thenAccessorReturnsCorrectValue(AccessorStrategy accessor) {
        Student student = new Student(STUDENT_NAME, STUDENT_AGE);

        String result = accessor.get(student);

        assertEquals(STUDENT_NAME, result);
    }

    private static Stream<AccessorStrategy> accessorProvider() {
        AccessorFactory.AccessorSet accessors = AccessorFactory.createAll();
        return Stream.of(accessors.direct(), accessors.reflection(), accessors.methodHandle(), accessors.lambda());
    }
}
