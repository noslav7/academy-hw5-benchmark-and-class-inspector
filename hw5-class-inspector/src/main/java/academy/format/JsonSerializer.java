package academy.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Обертка над {@link ObjectMapper} с заранее настроенной конфигурацией.
 */
public class JsonSerializer {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Сериализует объект в JSON.
     *
     * @param value объект для сериализации
     * @return JSON-представление
     */
    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать объект", e);
        }
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }
}

