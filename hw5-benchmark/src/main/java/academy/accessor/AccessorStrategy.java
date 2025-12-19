package academy.accessor;

import academy.model.Student;

/**
 * Стратегия доступа к полю объекта.
 */
public interface AccessorStrategy {

    /**
     * Получает значение поля через выбранную стратегию.
     *
     * @param student объект студента
     * @return значение поля
     */
    String get(Student student);
}

