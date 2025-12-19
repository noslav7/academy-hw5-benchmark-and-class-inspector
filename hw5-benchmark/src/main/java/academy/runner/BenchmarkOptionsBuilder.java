package academy.runner;

import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Строитель опций для JMH-бенчмарков.
 */
final class BenchmarkOptionsBuilder {

    /**
     * Создает опции для запуска указанного бенчмарка.
     *
     * @param benchmarkClass класс с бенчмарком
     * @return настроенные опции
     */
    Options build(Class<?> benchmarkClass) {
        return new OptionsBuilder()
                .include(benchmarkClass.getSimpleName())
                .shouldFailOnError(true)
                .build();
    }
}

