package academy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import picocli.CommandLine;

/**
 * Отвечает за подготовку и запуск JMH-бенчмарков.
 */
final class BenchmarkRunner {

    private static final Logger LOG = LogManager.getLogger(BenchmarkRunner.class);

    /**
     * Запускает указанный бенчмарк и возвращает код завершения в стиле {@link CommandLine.ExitCode}.
     *
     * @param benchmarkClass класс с бенчмарком
     * @return 0 при успешном запуске, либо {@link CommandLine.ExitCode#SOFTWARE} при ошибке
     */
    int run(Class<?> benchmarkClass) {
        Options options = new OptionsBuilder()
                .include(benchmarkClass.getSimpleName())
                .shouldFailOnError(true)
                .build();

        try {
            new Runner(options).run();
            return CommandLine.ExitCode.OK;
        } catch (RunnerException e) {
            LOG.error("Ошибка запуска бенчмарка {}", benchmarkClass.getSimpleName(), e);
            return CommandLine.ExitCode.SOFTWARE;
        }
    }
}

