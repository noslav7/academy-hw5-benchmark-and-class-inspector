package academy;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import picocli.CommandLine;

@CommandLine.Command(name = "Benchmark run", version = "1.0", mixinStandardHelpOptions = true)
public class Application implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // реализуйте логику по запуску бенчмарка,
        // ниже пример сравнения конкатениации строк

        Options opt = new OptionsBuilder()
                .include(StringConcatBenchmark.class.getSimpleName())
                .build();

        try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            throw new RuntimeException(e);
        }
    }
}
