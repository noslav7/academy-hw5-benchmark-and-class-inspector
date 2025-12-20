package academy.instance;

import academy.sample.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomInstanceFactoryTest {

    private RandomInstanceFactory factory;

    @BeforeEach
    void setUp() {
        factory = new RandomInstanceFactory();
    }

    @Test
    void givenClassWithFields_whenCreate_thenReturnsFilledInstance() {
        Person result = factory.create(Person.class);

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getName()),
                () -> assertNotNull(result.getAge())
        );
    }

    @Test
    void givenPrimitiveArray_whenCreate_thenReturnsArrayWithElements() {
        int[] result = factory.create(int[].class);

        assertNotNull(result);
        assertEquals(int[].class, result.getClass());
        assertTrue(result.length > 0);
    }
}
