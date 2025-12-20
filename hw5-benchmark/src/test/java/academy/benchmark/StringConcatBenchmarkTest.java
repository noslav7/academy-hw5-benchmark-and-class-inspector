package academy.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringConcatBenchmarkTest {

    private static final int ITERATIONS = 100;

    @Test
    void givenStringConcatBenchmark_whenBenchmarkStringAddition_thenCreatesCorrectConcatenatedString() {
        String result = buildStringAddition();

        String expected = buildExpectedString();
        assertEquals(expected, result);
    }

    @Test
    void givenStringConcatBenchmark_whenBenchmarkStringBuilder_thenCreatesCorrectConcatenatedString() {
        String result = buildStringBuilder();

        String expected = buildExpectedString();
        assertEquals(expected, result);
    }

    private String buildExpectedString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append(i);
        }
        return sb.toString();
    }

    private String buildStringAddition() {
        String s = "";
        for (int i = 0; i < ITERATIONS; i++) {
            s += i;
        }
        return s;
    }

    private String buildStringBuilder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append(i);
        }
        return sb.toString();
    }
}
