package academy;

import academy.format.OutputFormat;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(
        name = "class-inspector",
        version = "1.0",
        mixinStandardHelpOptions = true,
        description = "Отображает информацию о классе и создает экземпляры классов.")
public class Application implements Callable<Integer> {

    @Option(names = {"-c", "--class"}, required = true, description = "Полное имя класса для анализа.")
    private String className;

    @Option(
            names = {"-f", "--format"},
            defaultValue = "TEXT",
            description = "Формат вывода: ${COMPLETION-CANDIDATES}")
    private OutputFormat format;

    @Option(names = {"--create"}, description = "Создать экземпляр класса и вывести его содержимое в JSON.")
    private boolean createInstance;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            validateClassName();
            Class<?> clazz = Class.forName(className.trim());
            if (createInstance) {
                Object instance = ClassInspector.create(clazz);
                if (instance == null) {
                    throw new CommandLine.ExecutionException(
                            new CommandLine(this), "Не удалось создать экземпляр класса: " + className);
                }
                System.out.println(ClassInspector.toJson(instance));
            } else {
                String result = ClassInspector.inspect(clazz, format);
                System.out.println(result);
            }
            return 0;
        } catch (CommandLine.ParameterException e) {
            throw e;
        } catch (CommandLine.ExecutionException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Класс не найден: " + className, e);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.ParameterException(new CommandLine(this), e.getMessage(), e);
        } catch (Exception e) {
            throw new CommandLine.ExecutionException(new CommandLine(this), "Ошибка выполнения", e);
        }
    }

    private void validateClassName() {
        if (className == null || className.isBlank()) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Имя класса не должно быть пустым");
        }
    }
}
