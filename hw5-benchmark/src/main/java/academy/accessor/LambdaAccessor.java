package academy.accessor;

import academy.model.Student;

/**
 * Доступ к полю через {@link java.lang.invoke.LambdaMetafactory}.
 */
final class LambdaAccessor implements AccessorStrategy {

    private final StudentNameGetter getter;

    LambdaAccessor(StudentNameGetter getter) {
        this.getter = getter;
    }

    @Override
    public String get(Student student) {
        return getter.get(student);
    }

    @FunctionalInterface
    interface StudentNameGetter {
        String get(Student student);
    }
}

