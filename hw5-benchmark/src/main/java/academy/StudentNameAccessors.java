package academy;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Единая точка создания и использования разных способов получения имени студента.
 */
final class StudentNameAccessors {

    private final Method reflectiveName;
    private final MethodHandle methodHandle;
    private final StudentNameAccessor lambdaAccessor;

    private StudentNameAccessors(Method reflectiveName, MethodHandle methodHandle, StudentNameAccessor lambdaAccessor) {
        this.reflectiveName = reflectiveName;
        this.methodHandle = methodHandle;
        this.lambdaAccessor = lambdaAccessor;
    }

    /**
     * Подготавливает все вспомогательные объекты для доступа к имени студента.
     *
     * @return готовый набор аксессоров
     */
    static StudentNameAccessors create() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Method reflectiveName = Student.class.getMethod("name");
            MethodHandle methodHandle = lookup.findVirtual(Student.class, "name", MethodType.methodType(String.class));

            MethodType instanceGetter = MethodType.methodType(String.class, Student.class);
            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "get",
                    MethodType.methodType(StudentNameAccessor.class),
                    instanceGetter,
                    methodHandle,
                    instanceGetter);
            StudentNameAccessor lambdaAccessor = (StudentNameAccessor) callSite.getTarget().invokeExact();

            return new StudentNameAccessors(reflectiveName, methodHandle, lambdaAccessor);
        } catch (NoSuchMethodException | IllegalAccessException | LambdaConversionException e) {
            throw new IllegalStateException("Не удалось подготовить аксессоры для бенчмарка", e);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Не удалось создать lambda-акцессор", throwable);
        }
    }

    /**
     * Прямой вызов геттера записи.
     */
    String direct(Student student) {
        return student.name();
    }

    /**
     * Вызов через рефлексию.
     */
    String viaReflection(Student student) {
        try {
            return (String) reflectiveName.invoke(student);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Ошибка вызова через рефлексию", e);
        }
    }

    /**
     * Вызов через {@link MethodHandle}.
     */
    String viaMethodHandle(Student student) {
        try {
            return (String) methodHandle.invokeExact(student);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Ошибка вызова через MethodHandle", throwable);
        }
    }

    /**
     * Вызов через {@link LambdaMetafactory}.
     */
    String viaLambda(Student student) {
        return lambdaAccessor.get(student);
    }

    @FunctionalInterface
    interface StudentNameAccessor {
        String get(Student student);
    }
}

