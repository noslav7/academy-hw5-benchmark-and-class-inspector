package academy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Описание поля класса.
 */
public record FieldInfo(
        @JsonProperty("access") String access,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("annotations") List<String> annotations) {
}

