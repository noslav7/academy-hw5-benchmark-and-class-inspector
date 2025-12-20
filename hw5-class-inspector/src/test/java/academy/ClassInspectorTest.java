package academy;

import academy.format.JsonSerializer;
import academy.format.OutputFormat;
import academy.sample.Human;
import academy.sample.Person;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClassInspectorTest {

    @Test
    void givenClassAndTextFormat_whenInspect_thenReturnsFormattedString() {
        String result = ClassInspector.inspect(Person.class, OutputFormat.TEXT);

        assertNotNull(result);
        String[] lines = result.split("\n");
        assertEquals("Class: academy.sample.Person", lines[0]);
    }

    @Test
    void givenClassAndJsonFormat_whenInspect_thenReturnsJsonString() throws Exception {
        String result = ClassInspector.inspect(Person.class, OutputFormat.JSON);

        assertNotNull(result);
        ObjectMapper mapper = new JsonSerializer().objectMapper();
        JsonNode jsonNode = mapper.readTree(result);
        assertEquals("academy.sample.Person", jsonNode.get("class").asText());
    }

    @Test
    void givenClassAndNullFormat_whenInspect_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ClassInspector.inspect(Person.class, (OutputFormat) null));
    }

    @Test
    void givenObject_whenToJson_thenReturnsValidJson() throws Exception {
        Person person = new Person();
        person.setName("Test");
        person.setAge(25);

        String result = ClassInspector.toJson(person);

        ObjectMapper mapper = new JsonSerializer().objectMapper();
        JsonNode jsonNode = mapper.readTree(result);
        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(jsonNode),
                () -> assertEquals("Test", jsonNode.get("name").asText()),
                () -> assertEquals(25, jsonNode.get("age").asInt())
        );
    }

    @Test
    void givenClassWithFields_whenCreate_thenReturnsFilledInstance() {
        Person result = ClassInspector.create(Person.class);

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getName()),
                () -> assertNotNull(result.getAge())
        );
    }

}

