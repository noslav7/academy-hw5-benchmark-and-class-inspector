package academy.accessor;

import academy.model.Student;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Доступ к полю через рефлексию. */
final class ReflectionAccessor implements AccessorStrategy {

    private final Method method;

    ReflectionAccessor(Method method) {
        this.method = method;
    }

    @Override
    public String get(Student student) {
        try {
            return (String) method.invoke(student);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Ошибка вызова через рефлексию", e);
        }
    }
}
