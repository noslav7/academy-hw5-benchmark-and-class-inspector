package academy.inspection;

import java.lang.reflect.Modifier;

/**
 * Преобразует модификаторы доступа в человекочитаемые значения.
 */
public final class AccessModifierResolver {

    private AccessModifierResolver() {}

    /**
     * Возвращает строковое представление модификатора доступа.
     *
     * @param modifiers битовая маска модификаторов
     * @return public/protected/private или package-private
     */
    public static String resolve(int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return "public";
        }
        if (Modifier.isProtected(modifiers)) {
            return "protected";
        }
        if (Modifier.isPrivate(modifiers)) {
            return "private";
        }
        return "package-private";
    }
}

