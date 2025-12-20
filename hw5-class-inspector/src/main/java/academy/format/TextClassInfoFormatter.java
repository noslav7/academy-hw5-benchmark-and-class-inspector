package academy.format;

import academy.hierarchy.HierarchyRenderer;
import academy.model.ClassInfo;
import academy.model.MethodInfo;
import java.util.StringJoiner;

/** Форматирует результат инспекции в текстовом виде. */
public class TextClassInfoFormatter implements ClassInfoFormatter {

    private final HierarchyRenderer hierarchyRenderer;

    public TextClassInfoFormatter(HierarchyRenderer hierarchyRenderer) {
        this.hierarchyRenderer = hierarchyRenderer;
    }

    @Override
    public OutputFormat formatType() {
        return OutputFormat.TEXT;
    }

    @Override
    public String format(ClassInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Class: ").append(info.className()).append('\n');
        sb.append("Superclass: ")
                .append(info.superclass() != null ? info.superclass() : "none")
                .append('\n');

        sb.append("Interfaces:\n");
        if (info.interfaces().isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.interfaces().forEach(it -> sb.append("  - ").append(it).append('\n'));
        }

        sb.append("Fields:\n");
        if (info.fields().isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.fields().forEach(field -> sb.append("  - ")
                    .append(field.access())
                    .append(' ')
                    .append(field.name())
                    .append(" (")
                    .append(field.type())
                    .append(')')
                    .append('\n'));
        }

        sb.append("Methods:\n");
        if (info.methods().isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.methods().forEach(method -> sb.append("  - ")
                    .append(method.access())
                    .append(' ')
                    .append(method.name())
                    .append('(')
                    .append(joinParams(method))
                    .append(") : ")
                    .append(method.returnType())
                    .append('\n'));
        }

        sb.append("Annotations:\n");
        if (info.annotations().isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.annotations()
                    .forEach(annotation -> sb.append("  - @").append(annotation).append('\n'));
        }

        sb.append("Hierarchy:\n");
        sb.append(hierarchyRenderer.render(info.hierarchyRoot()));
        return sb.toString();
    }

    private String joinParams(MethodInfo method) {
        if (method.params().isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(", ");
        method.params().forEach(joiner::add);
        return joiner.toString();
    }
}
