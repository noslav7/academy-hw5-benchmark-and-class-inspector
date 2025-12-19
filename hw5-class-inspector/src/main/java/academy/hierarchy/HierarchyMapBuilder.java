package academy.hierarchy;

import academy.model.HierarchyNode;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Подготавливает отображение вида {@code name -> children} для сериализации дерева в JSON.
 */
public class HierarchyMapBuilder {

    /**
     * Преобразует дерево в вложенную {@link Map}.
     *
     * @param root корневой узел дерева
     * @return вложенная мапа либо пустая, если данных нет
     */
    public Map<String, Object> toMap(HierarchyNode root) {
        if (root == null) {
            return Map.of();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(root.name(), childrenMap(root.children()));
        return map;
    }

    private Map<String, Object> childrenMap(List<HierarchyNode> children) {
        Map<String, Object> result = new LinkedHashMap<>();
        Set<String> usedKeys = new LinkedHashSet<>();
        Map<String, Integer> duplicates = new LinkedHashMap<>();

        for (HierarchyNode child : children) {
            String key = uniqueKey(child, usedKeys, duplicates);
            usedKeys.add(key);
            duplicates.put(child.name(), duplicates.getOrDefault(child.name(), 0) + 1);
            result.put(key, childrenMap(child.children()));
        }
        return result;
    }

    private String uniqueKey(HierarchyNode node, Set<String> usedKeys, Map<String, Integer> duplicates) {
        String simple = node.name();
        if (!usedKeys.contains(simple)) {
            return simple;
        }
        String full = node.fullName();
        if (full != null && !usedKeys.contains(full)) {
            return full;
        }
        int count = duplicates.getOrDefault(simple, 1);
        String candidate;
        do {
            count++;
            candidate = simple + "#" + count;
        } while (usedKeys.contains(candidate));
        duplicates.put(simple, count);
        return candidate;
    }
}

