package academy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Узел дерева наследования.
 */
public final class HierarchyNode {

    private final String name;
    private final String fullName;
    private final List<HierarchyNode> children = new ArrayList<>();

    public HierarchyNode(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
    }

    public String name() {
        return name;
    }

    public String fullName() {
        return fullName;
    }

    public List<HierarchyNode> children() {
        return children;
    }

    public void addChild(HierarchyNode child) {
        children.add(child);
    }
}

