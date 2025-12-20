package academy.inspection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessModifierResolverTest {

    @ParameterizedTest
    @MethodSource("modifierProvider")
    void givenModifiers_whenResolve_thenReturnsCorrectAccessModifier(int modifiers, String expected) {
        String result = AccessModifierResolver.resolve(modifiers);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> modifierProvider() {
        return Stream.of(
                Arguments.of(Modifier.PUBLIC, "public"),
                Arguments.of(Modifier.PROTECTED, "protected"),
                Arguments.of(Modifier.PRIVATE, "private"),
                Arguments.of(0, "package-private"),
                Arguments.of(Modifier.PUBLIC | Modifier.STATIC, "public")
        );
    }
}

