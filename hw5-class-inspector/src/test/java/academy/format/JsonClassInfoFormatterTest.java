package academy.format;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.hierarchy.HierarchyMapBuilder;
import academy.model.ClassInfo;
import academy.model.FieldInfo;
import academy.model.HierarchyNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonClassInfoFormatterTest {

    private JsonClassInfoFormatter formatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        JsonSerializer jsonSerializer = new JsonSerializer();
        formatter = new JsonClassInfoFormatter(jsonSerializer, new HierarchyMapBuilder());
        objectMapper = jsonSerializer.objectMapper();
    }

    @Test
    void givenClassInfo_whenFormat_thenReturnsValidJson() throws Exception {
        ClassInfo info = new ClassInfo(
                "academy.sample.Person",
                "academy.sample.Human",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null);

        String result = formatter.format(info);

        assertNotNull(result);
        JsonNode jsonNode = objectMapper.readTree(result);
        assertAll(
                () -> assertEquals(
                        "academy.sample.Person", jsonNode.get("class").asText()),
                () -> assertEquals(
                        "academy.sample.Human", jsonNode.get("superclass").asText()),
                () -> assertTrue(jsonNode.get("interfaces").isArray()),
                () -> assertEquals(0, jsonNode.get("interfaces").size()),
                () -> assertTrue(jsonNode.get("fields").isArray()),
                () -> assertEquals(0, jsonNode.get("fields").size()),
                () -> assertTrue(jsonNode.get("methods").isArray()),
                () -> assertEquals(0, jsonNode.get("methods").size()),
                () -> assertTrue(jsonNode.get("annotations").isArray()),
                () -> assertEquals(0, jsonNode.get("annotations").size()),
                () -> assertTrue(jsonNode.get("hierarchy").isObject()),
                () -> assertEquals(0, jsonNode.get("hierarchy").size()));
    }

    @Test
    void givenClassInfoWithFields_whenFormat_thenIncludesFields() throws Exception {
        List<FieldInfo> fields = List.of(new FieldInfo("public", "name", "String", Collections.emptyList()));
        ClassInfo info = new ClassInfo(
                "academy.sample.Person",
                null,
                Collections.emptyList(),
                fields,
                Collections.emptyList(),
                Collections.emptyList(),
                null);

        String result = formatter.format(info);

        assertNotNull(result);
        JsonNode jsonNode = objectMapper.readTree(result);
        assertTrue(jsonNode.has("fields"));
        JsonNode fieldsNode = jsonNode.get("fields");
        assertEquals(1, fieldsNode.size());
        assertEquals("name", fieldsNode.get(0).get("name").asText());
    }

    @Test
    void givenClassInfoWithHierarchy_whenFormat_thenIncludesHierarchy() throws Exception {
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

        assertNotNull(result);
        JsonNode jsonNode = objectMapper.readTree(result);
        assertTrue(jsonNode.has("hierarchy"));
    }
}
