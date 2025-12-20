package academy.accessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class AbstractAccessorTest {

    protected AccessorStrategy accessor;
    protected Student student;

    protected abstract AccessorStrategy createAccessor() throws Throwable;

    protected String defaultStudentName;
    protected int defaultStudentAge;
    protected String differentStudentName;
    protected int differentStudentAge;

    @BeforeEach
    void setUp() throws Throwable {
        accessor = createAccessor();
        student = new Student(defaultStudentName, defaultStudentAge);
    }

    @Test
    void givenAccessor_whenGet_thenReturnsStudentName() {
        String result = accessor.get(student);

        assertEquals(defaultStudentName, result);
    }

    @Test
    void givenAccessor_whenGetWithDifferentStudent_thenReturnsCorrectName() {
        Student anotherStudent = new Student(differentStudentName, differentStudentAge);

        String result = accessor.get(anotherStudent);

        assertEquals(differentStudentName, result);
    }
}
