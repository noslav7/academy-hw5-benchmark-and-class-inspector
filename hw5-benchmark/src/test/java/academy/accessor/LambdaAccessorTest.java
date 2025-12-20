package academy.accessor;

import academy.model.Student;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

class LambdaAccessorTest extends AbstractAccessorTest {

    @Override
    protected AccessorStrategy createAccessor() throws Throwable {
        defaultStudentName = "Eve Johnson";
        defaultStudentAge = 28;
        differentStudentName = "Frank Taylor";
        differentStudentAge = 29;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.findVirtual(
                Student.class, "name", MethodType.methodType(String.class));
        MethodType instanceGetter = MethodType.methodType(String.class, Student.class);
        CallSite callSite = LambdaMetafactory.metafactory(
                lookup,
                "get",
                MethodType.methodType(LambdaAccessor.StudentNameGetter.class),
                instanceGetter,
                methodHandle,
                instanceGetter);
        LambdaAccessor.StudentNameGetter getter =
                (LambdaAccessor.StudentNameGetter) callSite.getTarget().invokeExact();
        return new LambdaAccessor(getter);
    }
}

