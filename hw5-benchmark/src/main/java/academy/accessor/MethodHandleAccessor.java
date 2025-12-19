package academy.accessor;

import academy.model.Student;
import java.lang.invoke.MethodHandle;

/**
 * Доступ к полю через {@link MethodHandle}.
 */
final class MethodHandleAccessor implements AccessorStrategy {

    private final MethodHandle methodHandle;

    MethodHandleAccessor(MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }

    @Override
    public String get(Student student) {
        try {
            return (String) methodHandle.invokeExact(student);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Ошибка вызова через MethodHandle", throwable);
        }
    }
}

