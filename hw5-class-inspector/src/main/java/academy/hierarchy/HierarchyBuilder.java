package academy.hierarchy;

import academy.model.HierarchyNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Строит дерево наследования для переданного класса. */
public class HierarchyBuilder {

    /**
     * Собирает дерево наследования, включая разрешенные подклассы для sealed-иерархий.
     *
     * @param clazz анализируемый класс
     * @return корневой узел дерева или {@code null}, если построить дерево не удалось
     */
    public HierarchyNode build(Class<?> clazz) {
        List<Class<?>> chain = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            chain.add(current);
            current = current.getSuperclass();
        }
        if (chain.isEmpty()) {
            return null;
        }
        Collections.reverse(chain);
        Set<Class<?>> visited = new HashSet<>();

        Class<?> rootClass = chain.get(0);
        HierarchyNode root = new HierarchyNode(rootClass.getSimpleName(), rootClass.getName());
        visited.add(rootClass);
        HierarchyNode cursor = root;

        for (int i = 1; i < chain.size(); i++) {
            Class<?> type = chain.get(i);
            HierarchyNode child = new HierarchyNode(type.getSimpleName(), type.getName());
            cursor.addChild(child);
            visited.add(type);
            addPermitted(cursor, chain.get(i - 1), visited, type);
            cursor = child;
        }
        addPermitted(cursor, clazz, visited, null);
        return root;
    }

    private void addPermitted(HierarchyNode parent, Class<?> type, Set<Class<?>> visited, Class<?> chainChild) {
        if (type == null || !type.isSealed()) {
            return;
        }
        for (Class<?> permitted : type.getPermittedSubclasses()) {
            if (visited.contains(permitted)) {
                continue;
            }
            HierarchyNode childNode = new HierarchyNode(permitted.getSimpleName(), permitted.getName());
            visited.add(permitted);
            parent.addChild(childNode);
            addPermitted(childNode, permitted, visited, null);
        }
        if (chainChild != null && !visited.contains(chainChild)) {
            HierarchyNode chainNode = new HierarchyNode(chainChild.getSimpleName(), chainChild.getName());
            visited.add(chainChild);
            parent.addChild(chainNode);
            addPermitted(chainNode, chainChild, visited, null);
        }
    }
}
