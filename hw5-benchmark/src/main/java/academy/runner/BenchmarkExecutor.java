package academy.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import picocli.CommandLine;

/** Выполняет JMH-бенчмарки. */
final class BenchmarkExecutor {

    private static final Logger LOG = LogManager.getLogger(BenchmarkExecutor.class);

    private final BenchmarkOptionsBuilder optionsBuilder;

    BenchmarkExecutor(BenchmarkOptionsBuilder optionsBuilder) {
        this.optionsBuilder = optionsBuilder;
    }

    /**
     * Запускает указанный бенчмарк.
     *
     * @param benchmarkClass класс с бенчмарком
     * @return код завершения: 0 при успехе, {@link CommandLine.ExitCode#SOFTWARE} при ошибке
     */
    int execute(Class<?> benchmarkClass) {
        Options options = optionsBuilder.build(benchmarkClass);
        try {
            new Runner(options).run();
            return CommandLine.ExitCode.OK;
        } catch (RunnerException e) {
            LOG.error("Ошибка запуска бенчмарка {}", benchmarkClass.getSimpleName(), e);
            return CommandLine.ExitCode.SOFTWARE;
        }
    }
}
