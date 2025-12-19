package academy.accessor;

import academy.model.Student;

/**
 * Прямой доступ к полю через геттер записи.
 */
final class DirectAccessor implements AccessorStrategy {

    @Override
    public String get(Student student) {
        return student.name();
    }
}

