package academy.inspection;

import academy.sample.Person;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotationExtractorTest {

    @Test
    void givenEmptyAnnotations_whenExtract_thenReturnsEmptyList() {
        List<String> result = AnnotationExtractor.extract(new Annotation[0]);

        assertEquals(0, result.size());
    }

    @Test
    void givenSingleAnnotation_whenExtract_thenReturnsAnnotationName() {
        Annotation[] annotations = Person.class.getAnnotations();
        List<String> result = AnnotationExtractor.extract(annotations);

        assertEquals(1, result.size());
        assertEquals("Entity", result.get(0));
    }
}

