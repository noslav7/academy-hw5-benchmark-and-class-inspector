package academy;

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

            assertEquals(0, result);
            String output = outputStream.toString();
            assertEquals("Class: academy.sample.Human", output.split("\n")[0]);
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
