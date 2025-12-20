package academy.inspection;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.hierarchy.HierarchyBuilder;
import academy.model.ClassInfo;
import academy.sample.Human;
import academy.sample.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassInfoCollectorTest {

    private ClassInfoCollector collector;

    @BeforeEach
    void setUp() {
        collector = new ClassInfoCollector(new HierarchyBuilder());
    }

    @Test
    void givenSimpleClass_whenCollect_thenReturnsClassInfo() {
        ClassInfo result = collector.collect(Human.class);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("academy.sample.Human", result.className()),
                () -> assertNull(result.superclass()));
    }

    @Test
    void givenClassWithSuperclass_whenCollect_thenReturnsSuperclass() {
        ClassInfo result = collector.collect(Person.class);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("academy.sample.Person", result.className()),
                () -> assertEquals("academy.sample.Human", result.superclass()));
    }

    @Test
    void givenClassWithFields_whenCollect_thenReturnsFields() {
        ClassInfo result = collector.collect(Person.class);

        assertTrue(result.fields().stream().anyMatch(f -> "name".equals(f.name())));
    }

    @Test
    void givenClassWithMethods_whenCollect_thenReturnsMethods() {
        ClassInfo result = collector.collect(Person.class);

        assertTrue(result.methods().stream().anyMatch(m -> "getName".equals(m.name())));
    }

    @Test
    void givenClassWithInterfaces_whenCollect_thenReturnsInterfaces() {
        ClassInfo result = collector.collect(Person.class);

        assertTrue(result.interfaces().contains("Named"));
    }

    @Test
    void givenClassWithAnnotations_whenCollect_thenReturnsAnnotations() {
        ClassInfo result = collector.collect(Person.class);

        assertTrue(result.annotations().contains("Entity"));
    }

    @Test
    void givenClass_whenCollect_thenReturnsHierarchy() {
        ClassInfo result = collector.collect(Person.class);

        assertNotNull(result.hierarchyRoot());
    }
}
