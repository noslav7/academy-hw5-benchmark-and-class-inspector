package academy;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 8, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2)
public class StudentNameBenchmark {

    private Student student;
    private Method reflectiveName;
    private MethodHandle methodHandle;
    private StudentNameAccessor lambdaAccessor;

    @Setup
    public void setUp() {
        student = new Student("John Doe", 21);
        try {
            reflectiveName = Student.class.getMethod("name");

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType instanceGetter = MethodType.methodType(String.class, Student.class);
            methodHandle = lookup.findVirtual(Student.class, "name", MethodType.methodType(String.class));

            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "get",
                    MethodType.methodType(StudentNameAccessor.class),
                    instanceGetter,
                    methodHandle,
                    instanceGetter);
            lambdaAccessor = (StudentNameAccessor) callSite.getTarget().invokeExact();
        } catch (NoSuchMethodException | IllegalAccessException | LambdaConversionException e) {
            throw new IllegalStateException("Failed to prepare benchmark helpers", e);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to create lambda accessor", throwable);
        }
    }

    @Benchmark
    public void directCall(Blackhole bh) {
        bh.consume(student.name());
    }

    @Benchmark
    public void reflectionCall(Blackhole bh) {
        try {
            bh.consume(reflectiveName.invoke(student));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Benchmark
    public void methodHandleCall(Blackhole bh) {
        try {
            bh.consume((String) methodHandle.invokeExact(student));
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        }
    }

    @Benchmark
    public void lambdaMetafactoryCall(Blackhole bh) {
        bh.consume(lambdaAccessor.get(student));
    }

    @FunctionalInterface
    interface StudentNameAccessor {
        String get(Student student);
    }
}

