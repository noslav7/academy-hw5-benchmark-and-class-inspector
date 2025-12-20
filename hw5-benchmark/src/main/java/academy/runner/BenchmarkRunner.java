package academy.runner;

import picocli.CommandLine;

/** Фасад для запуска JMH-бенчмарков. */
public final class BenchmarkRunner {

    private final BenchmarkExecutor executor;

    public BenchmarkRunner() {
        this(new BenchmarkExecutor(new BenchmarkOptionsBuilder()));
    }

    BenchmarkRunner(BenchmarkExecutor executor) {
        this.executor = executor;
    }

    /**
     * Запускает указанный бенчмарк и возвращает код завершения.
     *
     * @param benchmarkClass класс с бенчмарком
     * @return код завершения в стиле {@link CommandLine.ExitCode}
     */
    public int run(Class<?> benchmarkClass) {
        return executor.execute(benchmarkClass);
    }
}
