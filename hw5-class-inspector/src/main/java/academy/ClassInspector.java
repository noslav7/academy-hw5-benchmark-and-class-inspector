package academy;

import academy.format.ClassInfoFormatter;
import academy.format.JsonClassInfoFormatter;
import academy.format.JsonSerializer;
import academy.format.OutputFormat;
import academy.format.TextClassInfoFormatter;
import academy.hierarchy.HierarchyBuilder;
import academy.hierarchy.HierarchyMapBuilder;
import academy.hierarchy.HierarchyRenderer;
import academy.inspection.ClassInfoCollector;
import academy.instance.RandomInstanceFactory;
import academy.model.ClassInfo;
import java.util.EnumMap;
import java.util.Map;

/**
 * Фасад, предоставляющий функции инспекции и создания объектов.
 */
public final class ClassInspector {

    private static final ClassInspector INSTANCE = createDefault();

    private final ClassInfoCollector classInfoCollector;
    private final Map<OutputFormat, ClassInfoFormatter> formatters;
    private final RandomInstanceFactory instanceFactory;
    private final JsonSerializer jsonSerializer;

    ClassInspector(
            ClassInfoCollector classInfoCollector,
            Map<OutputFormat, ClassInfoFormatter> formatters,
            RandomInstanceFactory instanceFactory,
            JsonSerializer jsonSerializer) {
        this.classInfoCollector = classInfoCollector;
        this.formatters = formatters;
        this.instanceFactory = instanceFactory;
        this.jsonSerializer = jsonSerializer;
    }

    /**
     * Анализирует класс и возвращает описание в указанном формате.
     *
     * @param clazz  анализируемый класс
     * @param format строковый формат
     * @return результат анализа
     */
    public static String inspect(Class<?> clazz, String format) {
        return inspect(clazz, OutputFormat.from(format));
    }

    /**
     * Анализирует класс и возвращает описание в указанном формате.
     *
     * @param clazz  анализируемый класс
     * @param format формат вывода
     * @return результат анализа
     */
    public static String inspect(Class<?> clazz, OutputFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Формат вывода не задан");
        }
        ClassInfo info = INSTANCE.classInfoCollector.collect(clazz);
        ClassInfoFormatter formatter = INSTANCE.formatters.get(format);
        if (formatter == null) {
            throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
        }
        return formatter.format(info);
    }

    /**
     * Сериализует объект в JSON.
     *
     * @param value объект
     * @return JSON-представление объекта
     */
    public static String toJson(Object value) {
        return INSTANCE.jsonSerializer.toJson(value);
    }

    /**
     * Создает и заполняет экземпляр класса.
     *
     * @param clazz тип для создания
     * @param <T>   тип экземпляра
     * @return созданный объект
     */
    public static <T> T create(Class<T> clazz) {
        return INSTANCE.instanceFactory.create(clazz);
    }

    private static ClassInspector createDefault() {
        JsonSerializer jsonSerializer = new JsonSerializer();
        Map<OutputFormat, ClassInfoFormatter> formatters = createFormatters(jsonSerializer);
        return new ClassInspector(
                new ClassInfoCollector(new HierarchyBuilder()),
                formatters,
                new RandomInstanceFactory(),
                jsonSerializer);
    }

    private static Map<OutputFormat, ClassInfoFormatter> createFormatters(JsonSerializer jsonSerializer) {
        EnumMap<OutputFormat, ClassInfoFormatter> formatters = new EnumMap<>(OutputFormat.class);
        HierarchyRenderer hierarchyRenderer = new HierarchyRenderer();
        HierarchyMapBuilder hierarchyMapBuilder = new HierarchyMapBuilder();
        formatters.put(OutputFormat.TEXT, new TextClassInfoFormatter(hierarchyRenderer));
        formatters.put(OutputFormat.JSON, new JsonClassInfoFormatter(jsonSerializer, hierarchyMapBuilder));
        return Map.copyOf(formatters);
    }
}
