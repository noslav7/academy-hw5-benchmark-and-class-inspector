package academy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Описание структуры класса, собранное инспектором.
 */
public record ClassInfo(
        @JsonProperty("class") String className,
        @JsonProperty("superclass") String superclass,
        @JsonProperty("interfaces") List<String> interfaces,
        @JsonProperty("fields") List<FieldInfo> fields,
        @JsonProperty("methods") List<MethodInfo> methods,
        @JsonProperty("annotations") List<String> annotations,
        @JsonIgnore HierarchyNode hierarchyRoot) {
}

