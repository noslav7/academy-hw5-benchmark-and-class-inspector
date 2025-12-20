package academy.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.model.HierarchyNode;
import org.junit.jupiter.api.Test;

class HierarchyRendererTest {

    private final HierarchyRenderer renderer = new HierarchyRenderer();

    @Test
    void givenNullRoot_whenRender_thenReturnsNoneMessage() {
        String result = renderer.render(null);

        assertEquals("  - none\n", result);
    }

    @Test
    void givenNodeWithChildren_whenRender_thenReturnsTreeStructure() {
        HierarchyNode root = new HierarchyNode("Root", "academy.sample.Root");
        HierarchyNode child1 = new HierarchyNode("Child1", "academy.sample.Child1");
        HierarchyNode child2 = new HierarchyNode("Child2", "academy.sample.Child2");
        root.addChild(child1);
        root.addChild(child2);

        String result = renderer.render(root);

        String[] lines = result.split("\n");
        assertEquals("  Root", lines[0]);
        assertEquals("  ├── Child1", lines[1]);
        assertEquals("  └── Child2", lines[2]);
    }
}
