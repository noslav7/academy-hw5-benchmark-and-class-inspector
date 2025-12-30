package academy;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import academy.format.OutputFormat;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ApplicationTest {

    @Test
    void givenValidClassNameWithTextFormat_whenCall_thenReturnsZeroAndOutputsToConsole() throws Exception {
        Application app = new Application();
        setPrivateField(app, "className", "academy.sample.Human");
        setPrivateField(app, "format", OutputFormat.TEXT);
        setPrivateField(app, "createInstance", false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            int result = app.call();

            String output = outputStream.toString();
            String[] lines = output.split("\n");
            assertAll(
                    () -> assertEquals(0, result),
                    () -> assertNotNull(output),
                    () -> assertEquals("Class: academy.sample.Human", lines[0]),
                    () -> assertEquals("Superclass: none", lines[1]),
                    () -> assertEquals(12, lines.length));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void givenValidClassNameWithJsonFormat_whenCall_thenReturnsZeroAndOutputsToConsole() throws Exception {
        Application app = new Application();
        setPrivateField(app, "className", "academy.sample.Human");
        setPrivateField(app, "format", OutputFormat.JSON);
        setPrivateField(app, "createInstance", false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            int result = app.call();

            assertEquals(0, result);
            String output = outputStream.toString();
            assertNotNull(output);
            com.fasterxml.jackson.databind.ObjectMapper mapper = new academy.format.JsonSerializer().objectMapper();
            com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(output);
            assertEquals("academy.sample.Human", jsonNode.get("class").asText());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void givenInvalidClassName_whenCall_thenThrowsException() throws Exception {
        Application app = new Application();
        setPrivateField(app, "className", "NonExistentClass");
        setPrivateField(app, "format", OutputFormat.TEXT);
        setPrivateField(app, "createInstance", false);

        assertThrows(CommandLine.ParameterException.class, app::call);
    }

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
