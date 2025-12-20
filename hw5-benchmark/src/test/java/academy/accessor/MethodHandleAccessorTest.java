package academy.accessor;

import academy.model.Student;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

class MethodHandleAccessorTest extends AbstractAccessorTest {

    @Override
    protected AccessorStrategy createAccessor() throws Exception {
        defaultStudentName = "Charlie Davis";
        defaultStudentAge = 26;
        differentStudentName = "Diana Miller";
        differentStudentAge = 27;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(
                Student.class, "name", MethodType.methodType(String.class));
        return new MethodHandleAccessor(methodHandle);
    }
}

