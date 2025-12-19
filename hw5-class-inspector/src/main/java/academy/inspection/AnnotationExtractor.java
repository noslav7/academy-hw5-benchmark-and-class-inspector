package academy.inspection;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * Утилита для извлечения имен аннотаций.
 */
public final class AnnotationExtractor {

    private AnnotationExtractor() {}

    /**
     * Возвращает список коротких имен аннотаций.
     *
     * @param annotations массив аннотаций
     * @return список имен аннотаций
     */
    public static List<String> extract(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .map(annotation -> annotation.annotationType().getSimpleName())
                .toList();
    }
}

