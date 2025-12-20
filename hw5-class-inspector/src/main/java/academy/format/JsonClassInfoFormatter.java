package academy.format;

import academy.hierarchy.HierarchyMapBuilder;
import academy.model.ClassInfo;
import academy.model.FieldInfo;
import academy.model.MethodInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/** Форматирует результат инспекции в JSON. */
public class JsonClassInfoFormatter implements ClassInfoFormatter {

    private final JsonSerializer jsonSerializer;
    private final HierarchyMapBuilder hierarchyMapBuilder;

    public JsonClassInfoFormatter(JsonSerializer jsonSerializer, HierarchyMapBuilder hierarchyMapBuilder) {
        this.jsonSerializer = jsonSerializer;
        this.hierarchyMapBuilder = hierarchyMapBuilder;
    }

    @Override
    public OutputFormat formatType() {
        return OutputFormat.JSON;
    }

    @Override
    public String format(ClassInfo info) {
        Map<String, Object> hierarchy = hierarchyMapBuilder.toMap(info.hierarchyRoot());
        Payload payload = new Payload(
                info.className(),
                info.superclass(),
                info.interfaces(),
                info.fields(),
                info.methods(),
                info.annotations(),
                hierarchy);
        return jsonSerializer.toJson(payload);
    }

    private record Payload(
            @JsonProperty("class") String className,
            @JsonProperty("superclass") String superclass,
            @JsonProperty("interfaces") List<String> interfaces,
            @JsonProperty("fields") List<FieldInfo> fields,
            @JsonProperty("methods") List<MethodInfo> methods,
            @JsonProperty("annotations") List<String> annotations,
            @JsonProperty("hierarchy") Map<String, Object> hierarchy) {}
}
