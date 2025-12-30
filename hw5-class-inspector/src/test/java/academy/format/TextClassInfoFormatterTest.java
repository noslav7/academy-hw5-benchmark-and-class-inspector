package academy.format;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.hierarchy.HierarchyRenderer;
import academy.model.ClassInfo;
import academy.model.FieldInfo;
import academy.model.HierarchyNode;
import academy.model.MethodInfo;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextClassInfoFormatterTest {

    private TextClassInfoFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new TextClassInfoFormatter(new HierarchyRenderer());
    }

    private int findLineIndex(String[] lines, String searchText) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals(searchText)) {
                return i;
            }
        }
        return -1;
    }

    @Test
    void givenClassInfo_whenFormat_thenReturnsFormattedString() {
        ClassInfo info = new ClassInfo(
                "academy.sample.Person",
                "academy.sample.Human",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null);

        String result = formatter.format(info);

        String[] lines = result.split("\n");
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("Class: academy.sample.Person", lines[0]),
                () -> assertEquals("Superclass: academy.sample.Human", lines[1]),
                () -> assertEquals(12, lines.length));
    }

    @Test
    void givenClassInfoWithFieldsAndMethods_whenFormat_thenIncludesAll() {
        List<FieldInfo> fields = List.of(new FieldInfo("public", "name", "String", Collections.emptyList()));
        List<MethodInfo> methods = List.of(
                new MethodInfo("public", "getName", Collections.emptyList(), "String", Collections.emptyList()));
        ClassInfo info = new ClassInfo(
                "academy.sample.Person", null, Collections.emptyList(), fields, methods, Collections.emptyList(), null);

        String result = formatter.format(info);

        assertNotNull(result);
        String[] lines = result.split("\n");
        int fieldsIndex = findLineIndex(lines, "Fields:");
        int methodsIndex = findLineIndex(lines, "Methods:");
        assertTrue(fieldsIndex >= 0, "Fields section should be present");
        assertTrue(methodsIndex >= 0, "Methods section should be present");
        assertEquals("  - public name (String)", lines[fieldsIndex + 1]);
        assertEquals("  - public getName() : String", lines[methodsIndex + 1]);
    }

    @Test
    void givenClassInfoWithHierarchy_whenFormat_thenIncludesHierarchy() {
        HierarchyNode root = new HierarchyNode("Root", "academy.sample.Root");
        ClassInfo info = new ClassInfo(
                "academy.sample.Person",
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                root);

        String result = formatter.format(info);

        String[] lines = result.split("\n");
        int hierarchyIndex = findLineIndex(lines, "Hierarchy:");
        assertTrue(hierarchyIndex >= 0, "Hierarchy section should be present");
        assertEquals("  Root", lines[hierarchyIndex + 1]);
    }
}
