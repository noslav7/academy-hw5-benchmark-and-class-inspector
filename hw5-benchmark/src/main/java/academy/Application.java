package academy;

import academy.benchmark.StudentNameBenchmark;
import academy.runner.BenchmarkRunner;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

@CommandLine.Command(name = "Benchmark run", version = "1.0", mixinStandardHelpOptions = true)
public class Application implements Callable<Integer> {

    private static final Logger LOG = LogManager.getLogger(Application.class);

    private final BenchmarkRunner benchmarkRunner;

    public Application() {
        this(new BenchmarkRunner());
    }

    Application(BenchmarkRunner benchmarkRunner) {
        this.benchmarkRunner = benchmarkRunner;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            return benchmarkRunner.run(StudentNameBenchmark.class);
        } catch (RuntimeException runtimeException) {
            LOG.error("Бенчмарк не завершился успешно", runtimeException);
            return CommandLine.ExitCode.SOFTWARE;
        }
    }
}
