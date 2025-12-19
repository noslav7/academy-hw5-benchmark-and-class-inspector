package academy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Описание метода класса.
 */
public record MethodInfo(
        @JsonProperty("access") String access,
        @JsonProperty("name") String name,
        @JsonProperty("params") List<String> params,
        @JsonProperty("returnType") String returnType,
        @JsonProperty("annotations") List<String> annotations) {
}

