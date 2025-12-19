package academy.hierarchy;

import academy.model.HierarchyNode;

/**
 * Рендерит иерархию наследования в человекочитаемом виде.
 */
public class HierarchyRenderer {

    /**
     * Возвращает текстовое представление дерева наследования.
     *
     * @param root корневой узел дерева
     * @return многострочная строка дерева или строка с отсутствием данных
     */
    public String render(HierarchyNode root) {
        if (root == null) {
            return "  - none\n";
        }
        StringBuilder sb = new StringBuilder();
        render(root, sb, "", true);
        return sb.toString();
    }

    private void render(HierarchyNode node, StringBuilder sb, String prefix, boolean last) {
        if (!prefix.isEmpty()) {
            sb.append(prefix).append(last ? "└── " : "├── ");
        } else {
            sb.append("  ");
        }
        sb.append(node.name()).append('\n');
        for (int i = 0; i < node.children().size(); i++) {
            HierarchyNode child = node.children().get(i);
            String childPrefix = prefix + (prefix.isEmpty() ? "  " : (last ? "    " : "│   "));
            render(child, sb, childPrefix, i == node.children().size() - 1);
        }
    }
}

