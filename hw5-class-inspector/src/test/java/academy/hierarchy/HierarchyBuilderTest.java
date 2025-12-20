package academy.hierarchy;

import academy.model.HierarchyNode;
import academy.sample.Human;
import academy.sample.Person;
import academy.sample.Manager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class HierarchyBuilderTest {

    private final HierarchyBuilder builder = new HierarchyBuilder();

    @Test
    void givenObjectClass_whenBuild_thenReturnsNull() {
        HierarchyNode result = builder.build(Object.class);

        assertNull(result);
    }

    @Test
    void givenClassWithSuperclass_whenBuild_thenReturnsHierarchy() {
        HierarchyNode result = builder.build(Person.class);

        assertNotNull(result);
        assertEquals("Human", result.name());
        assertEquals(1, result.children().size());
        assertEquals("Person", result.children().get(0).name());
    }

    @Test
    void givenClassWithDeepHierarchy_whenBuild_thenReturnsFullHierarchy() {
        HierarchyNode result = builder.build(Manager.class);

        assertNotNull(result);
        assertEquals("Human", result.name());
        HierarchyNode personNode = result.children().get(0);
        assertEquals("Person", personNode.name());
        HierarchyNode employeeNode = personNode.children().get(0);
        assertEquals("Employee", employeeNode.name());
        HierarchyNode managerNode = employeeNode.children().get(0);
        assertEquals("Manager", managerNode.name());
    }
}
