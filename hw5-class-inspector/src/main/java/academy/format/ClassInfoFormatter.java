package academy.format;

import academy.model.ClassInfo;

/**
 * Стратегия форматирования результата анализа.
 */
public interface ClassInfoFormatter {

    /**
     * @return формат, который поддерживает стратегия
     */
    OutputFormat formatType();

    /**
     * Формирует человекочитаемое представление результата анализа.
     *
     * @param info данные анализа
     * @return строка для вывода
     */
    String format(ClassInfo info);
}

