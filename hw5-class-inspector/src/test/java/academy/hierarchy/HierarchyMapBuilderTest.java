package academy.hierarchy;

import academy.model.HierarchyNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HierarchyMapBuilderTest {

    private final HierarchyMapBuilder builder = new HierarchyMapBuilder();

    @Test
    void givenNodeWithChildren_whenToMap_thenReturnsNestedMap() {
        HierarchyNode root = new HierarchyNode("Root", "academy.sample.Root");
        HierarchyNode child1 = new HierarchyNode("Child1", "academy.sample.Child1");
        HierarchyNode child2 = new HierarchyNode("Child2", "academy.sample.Child2");
        root.addChild(child1);
        root.addChild(child2);

        Map<String, Object> result = builder.toMap(root);

        assertNotNull(result);
        @SuppressWarnings("unchecked")
        Map<String, Object> children = (Map<String, Object>) result.get("Root");
        assertNotNull(children);
        assertEquals(2, children.size());
        assertNotNull(children.get("Child1"));
        assertNotNull(children.get("Child2"));
    }
}
