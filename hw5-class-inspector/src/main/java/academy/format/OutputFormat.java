package academy.format;

/**
 * Форматы вывода данных инспектора.
 */
public enum OutputFormat {
    TEXT,
    JSON;

    /**
     * Получает формат из строкового представления.
     *
     * @param raw строковое значение
     * @return соответствующий {@link OutputFormat}
     */
    public static OutputFormat from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Формат вывода не должен быть пустым");
        }
        String candidate = raw.trim();
        for (OutputFormat value : values()) {
            if (value.name().equalsIgnoreCase(candidate)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Неподдерживаемый формат: " + raw);
    }
}

