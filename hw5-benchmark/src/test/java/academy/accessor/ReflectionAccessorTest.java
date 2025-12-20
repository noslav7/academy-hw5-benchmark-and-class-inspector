package academy.accessor;

import academy.model.Student;
import java.lang.reflect.Method;

class ReflectionAccessorTest extends AbstractAccessorTest {

    @Override
    protected AccessorStrategy createAccessor() throws Exception {
        defaultStudentName = "Alice Brown";
        defaultStudentAge = 23;
        differentStudentName = "Bob Wilson";
        differentStudentAge = 24;
        Method method = Student.class.getMethod("name");
        return new ReflectionAccessor(method);
    }
}
