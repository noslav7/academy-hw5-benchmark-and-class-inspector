package academy.format;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import academy.model.ClassInfo;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonSerializerTest {

    private JsonSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new JsonSerializer();
    }

    @Test
    void givenClassInfo_whenToJson_thenReturnsJsonString() throws Exception {
        ClassInfo info = new ClassInfo(
                "academy.sample.Person",
                "academy.sample.Human",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                null);

        String result = serializer.toJson(info);

        assertNotNull(result);
        com.fasterxml.jackson.databind.ObjectMapper mapper = serializer.objectMapper();
        com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(result);
        assertAll(
                () -> assertEquals(
                        "academy.sample.Person", jsonNode.get("class").asText()),
                () -> assertEquals(
                        "academy.sample.Human", jsonNode.get("superclass").asText()));
    }
}
