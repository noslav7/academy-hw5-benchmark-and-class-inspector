package academy.format;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutputFormatTest {

    @ParameterizedTest
    @MethodSource("validFormatProvider")
    void givenValidFormat_whenFrom_thenReturnsFormat(String input, OutputFormat expected) {
        OutputFormat result = OutputFormat.from(input);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> validFormatProvider() {
        return Stream.of(
                Arguments.of("TEXT", OutputFormat.TEXT),
                Arguments.of("text", OutputFormat.TEXT),
                Arguments.of("JSON", OutputFormat.JSON)
        );
    }

    @Test
    void givenInvalidFormat_whenFrom_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> OutputFormat.from("XML"));
        assertThrows(IllegalArgumentException.class, () -> OutputFormat.from((String) null));
    }
}

