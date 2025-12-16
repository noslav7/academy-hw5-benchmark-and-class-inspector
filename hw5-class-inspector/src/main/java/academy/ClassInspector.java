package academy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Date;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ClassInspector {
    private static final int MAX_DEPTH = 3;
    private static final Random RANDOM = new Random();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static String inspect(Class<?> clazz, String format) {
        OutputFormat outputFormat = OutputFormat.from(format);
        ClassInfo info = collectInfo(clazz);
        try {
            return switch (outputFormat) {
                case TEXT -> toText(info);
                case JSON -> OBJECT_MAPPER.writeValueAsString(info);
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать результат", e);
        }
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать объект", e);
        }
    }

    public static <T> T create(Class<T> clazz) {
        try {
            return instantiate(clazz, 0, new IdentityHashMap<>());
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать экземпляр: " + clazz.getName(), e);
        }
    }

    private static ClassInfo collectInfo(Class<?> clazz) {
        String className = clazz.getName();
        String superclass = clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : null;
        List<String> interfaces = Arrays.stream(clazz.getInterfaces())
                .map(Class::getSimpleName)
                .toList();
        List<FieldInfo> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(ClassInspector::toFieldInfo)
                .toList();
        List<MethodInfo> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> !method.isSynthetic())
                .map(ClassInspector::toMethodInfo)
                .toList();

        Set<String> annotations = new LinkedHashSet<>();
        annotations.addAll(extractAnnotationNames(clazz.getAnnotations()));
        Arrays.stream(clazz.getDeclaredFields())
                .forEach(field -> annotations.addAll(extractAnnotationNames(field.getAnnotations())));
        Arrays.stream(clazz.getDeclaredMethods())
                .forEach(method -> annotations.addAll(extractAnnotationNames(method.getAnnotations())));

        HierarchyNode hierarchyRoot = buildHierarchy(clazz);
        Map<String, Object> hierarchyMap = hierarchyRoot != null
                ? hierarchyToMap(hierarchyRoot)
                : Collections.emptyMap();

        return new ClassInfo(className, superclass, interfaces, fields, methods, new ArrayList<>(annotations),
                hierarchyRoot, hierarchyMap);
    }

    private static FieldInfo toFieldInfo(Field field) {
        return new FieldInfo(accessLevel(field.getModifiers()), field.getName(),
                field.getType().getSimpleName(), extractAnnotationNames(field.getAnnotations()));
    }

    private static MethodInfo toMethodInfo(java.lang.reflect.Method method) {
        List<String> params = Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .toList();
        return new MethodInfo(accessLevel(method.getModifiers()), method.getName(), params,
                method.getReturnType().getSimpleName(), extractAnnotationNames(method.getAnnotations()));
    }

    private static List<String> extractAnnotationNames(Annotation[] annotations) {
        return Arrays.stream(annotations)
                .map(annotation -> annotation.annotationType().getSimpleName())
                .toList();
    }

    private static String accessLevel(int modifiers) {
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

    private static String toText(ClassInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("Class: ").append(info.className).append('\n');
        sb.append("Superclass: ").append(info.superclass != null ? info.superclass : "none").append('\n');

        sb.append("Interfaces:\n");
        if (info.interfaces.isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.interfaces.forEach(it -> sb.append("  - ").append(it).append('\n'));
        }

        sb.append("Fields:\n");
        if (info.fields.isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.fields.forEach(field -> sb.append("  - ")
                    .append(field.access)
                    .append(' ')
                    .append(field.name)
                    .append(" (")
                    .append(field.type)
                    .append(')')
                    .append('\n'));
        }

        sb.append("Methods:\n");
        if (info.methods.isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.methods.forEach(method -> {
                sb.append("  - ")
                        .append(method.access)
                        .append(' ')
                        .append(method.name)
                        .append('(')
                        .append(String.join(", ", method.params))
                        .append(") : ")
                        .append(method.returnType)
                        .append('\n');
            });
        }

        sb.append("Annotations:\n");
        if (info.annotations.isEmpty()) {
            sb.append("  - none\n");
        } else {
            info.annotations.forEach(annotation -> sb.append("  - @").append(annotation).append('\n'));
        }

        sb.append("Hierarchy:\n");
        if (info.hierarchyRoot == null) {
            sb.append("  - none\n");
        } else {
            renderHierarchy(info.hierarchyRoot, sb, "", true);
        }

        return sb.toString();
    }

    private static void renderHierarchy(HierarchyNode node, StringBuilder sb, String prefix, boolean last) {
        if (!prefix.isEmpty()) {
            sb.append(prefix).append(last ? "└── " : "├── ");
        } else {
            sb.append("  ");
        }
        sb.append(node.name).append('\n');
        for (int i = 0; i < node.children.size(); i++) {
            HierarchyNode child = node.children.get(i);
            String childPrefix = prefix + (prefix.isEmpty() ? "  " : (last ? "    " : "│   "));
            renderHierarchy(child, sb, childPrefix, i == node.children.size() - 1);
        }
    }

    private static HierarchyNode buildHierarchy(Class<?> clazz) {
        List<Class<?>> chain = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            chain.add(current);
            current = current.getSuperclass();
        }
        if (chain.isEmpty()) {
            return null;
        }
        Collections.reverse(chain);
        Set<Class<?>> visited = new HashSet<>();

        Class<?> rootClass = chain.get(0);
        HierarchyNode root = new HierarchyNode(rootClass.getSimpleName(), rootClass.getName());
        visited.add(chain.get(0));
        HierarchyNode cursor = root;

        for (int i = 1; i < chain.size(); i++) {
            Class<?> type = chain.get(i);
            HierarchyNode child = new HierarchyNode(type.getSimpleName(), type.getName());
            cursor.children.add(child);
            visited.add(type);
            addPermitted(cursor, chain.get(i - 1), visited, type);
            cursor = child;
        }
        addPermitted(cursor, clazz, visited, null);
        return root;
    }

    private static void addPermitted(HierarchyNode parent, Class<?> type, Set<Class<?>> visited, Class<?> chainChild) {
        if (type == null || !type.isSealed()) {
            return;
        }
        for (Class<?> permitted : type.getPermittedSubclasses()) {
            if (visited.contains(permitted)) {
                continue;
            }
            HierarchyNode childNode = new HierarchyNode(permitted.getSimpleName(), permitted.getName());
            visited.add(permitted);
            parent.children.add(childNode);
            addPermitted(childNode, permitted, visited, null);
        }
        if (chainChild != null && !visited.contains(chainChild)) {
            HierarchyNode chainNode = new HierarchyNode(chainChild.getSimpleName(), chainChild.getName());
            visited.add(chainChild);
            parent.children.add(chainNode);
            addPermitted(chainNode, chainChild, visited, null);
        }
    }

    private static Map<String, Object> hierarchyToMap(HierarchyNode node) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(node.name, childrenMap(node.children));
        return map;
    }

    private static Map<String, Object> childrenMap(List<HierarchyNode> children) {
        Map<String, Object> result = new LinkedHashMap<>();
        Set<String> usedKeys = new LinkedHashSet<>();
        Map<String, Integer> duplicates = new LinkedHashMap<>();

        for (HierarchyNode child : children) {
            String key = uniqueKey(child, usedKeys, duplicates);
            usedKeys.add(key);
            duplicates.put(child.name, duplicates.getOrDefault(child.name, 0) + 1);
            result.put(key, childrenMap(child.children));
        }
        return result;
    }

    private static String uniqueKey(HierarchyNode node, Set<String> usedKeys, Map<String, Integer> duplicates) {
        String simple = node.name;
        if (!usedKeys.contains(simple)) {
            return simple;
        }
        String full = node.fullName;
        if (full != null && !usedKeys.contains(full)) {
            return full;
        }
        int count = duplicates.getOrDefault(simple, 1);
        String candidate;
        do {
            count++;
            candidate = simple + "#" + count;
        } while (usedKeys.contains(candidate));
        duplicates.put(simple, count);
        return candidate;
    }

    @SuppressWarnings({"unchecked", "null"})
    private static <T> T instantiate(Class<T> clazz, int depth, Map<Class<?>, Object> cache) throws Exception {
        if (clazz.isPrimitive()) {
            return (T) generatePrimitive(clazz);
        }
        if (clazz == String.class) {
            return (T) randomString();
        }
        if (clazz.isEnum()) {
            T[] constants = clazz.getEnumConstants();
            return constants.length == 0 ? null : constants[RANDOM.nextInt(constants.length)];
        }
        if (clazz == Boolean.class) {
            return (T) Boolean.valueOf(RANDOM.nextBoolean());
        }
        if (Number.class.isAssignableFrom(clazz)) {
            Object number = generateNumber(clazz);
            if (number != null) {
                return (T) number;
            }
            // если конкретный Number не поддержан напрямую — пробуем общую логику ниже
        }
        if (clazz == Character.class) {
            return (T) Character.valueOf((char) ('a' + RANDOM.nextInt(26)));
        }
        if (clazz == Date.class) {
            return (T) new Date(positiveRandomLong());
        }
        if (clazz == java.sql.Date.class) {
            return (T) new java.sql.Date(positiveRandomLong());
        }
        if (clazz == java.sql.Time.class) {
            return (T) new java.sql.Time(positiveRandomLong());
        }
        if (clazz == java.sql.Timestamp.class) {
            return (T) new java.sql.Timestamp(positiveRandomLong());
        }
        if (Date.class.isAssignableFrom(clazz)) {
            // Другие подклассы Date: попробуем найти подходящий конструктор
            T instance = tryDateSubclassCtor(clazz);
            if (instance != null) {
                return instance;
            }
        }
        if (clazz == LocalDate.class) {
            return (T) LocalDate.now().plusDays(RANDOM.nextInt(30));
        }
        if (clazz == LocalDateTime.class) {
            return (T) LocalDateTime.now().plusHours(RANDOM.nextInt(48));
        }
        if (clazz == LocalTime.class) {
            return (T) LocalTime.of(RANDOM.nextInt(24), RANDOM.nextInt(60), RANDOM.nextInt(60));
        }
        if (clazz == Instant.class) {
            return (T) Instant.ofEpochMilli(positiveRandomLong());
        }
        if (cache.containsKey(clazz)) {
            return (T) cache.get(clazz);
        }
        if (depth > MAX_DEPTH || clazz == Object.class) {
            return null;
        }
        if (clazz.isArray()) {
            Class<?> component = clazz.getComponentType();
            int size = RANDOM.nextInt(3) + 1;
            Object array = Array.newInstance(component, size);
            for (int i = 0; i < size; i++) {
                Array.set(array, i, instantiate(component, depth + 1, cache));
            }
            return (T) array;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            Collection<Object> collection = collectionInstance(clazz);
            if (collection == null) {
                collection = new ArrayList<>();
            }
            int size = RANDOM.nextInt(3) + 1;
            for (int i = 0; i < size; i++) {
                collection.add(randomString());
            }
            return (T) collection;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            Map<Object, Object> map = mapInstance(clazz);
            if (map == null) {
                return null;
            }
            int size = RANDOM.nextInt(3) + 1;
            for (int i = 0; i < size; i++) {
                map.put(randomString(), randomString());
            }
            return (T) map;
        }
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }

        Constructor<T> constructor = findConstructor(clazz);
        if (constructor == null) {
            return null;
        }
        constructor.setAccessible(true);
        Object[] args = Arrays.stream(constructor.getParameterTypes())
                .map(param -> {
                    try {
                        return instantiate(param, depth + 1, cache);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .toArray();
        T instance = constructor.newInstance(args);
        cache.put(clazz, instance);
        Class<?> cursor = clazz;
        while (cursor != null && cursor != Object.class) {
            for (Field field : cursor.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                Object value = generateValue(field.getType(), field.getGenericType(), depth + 1, cache);
                field.set(instance, value);
            }
            cursor = cursor.getSuperclass();
        }
        return instance;
    }

    private static <T> Constructor<T> findConstructor(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            return null;
        }
        Arrays.sort(constructors, (a, b) -> Integer.compare(a.getParameterCount(), b.getParameterCount()));
        @SuppressWarnings("unchecked")
        Constructor<T> result = (Constructor<T>) constructors[0];
        return result;
    }

    private static Object generateValue(Class<?> type, Type genericType, int depth, Map<Class<?>, Object> cache) throws Exception {
        if (depth > MAX_DEPTH) {
            return null;
        }
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            int size = RANDOM.nextInt(2) + 1;
            Object array = Array.newInstance(componentType, size);
            for (int i = 0; i < size; i++) {
                Array.set(array, i, instantiate(componentType, depth + 1, cache));
            }
            return array;
        }
        if (Collection.class.isAssignableFrom(type)) {
            Collection<Object> collection = collectionInstance(type);
            if (collection != null) {
                int size = RANDOM.nextInt(2) + 1;
                Class<?> elementType = extractGenericType(genericType, 0);
                for (int i = 0; i < size; i++) {
                    @SuppressWarnings("unchecked")
                    Class<Object> elementClass = (Class<Object>) (elementType != null ? elementType : String.class);
                    collection.add(instantiate(elementClass, depth + 1, cache));
                }
            }
            return collection;
        }
        if (Map.class.isAssignableFrom(type)) {
            Map<Object, Object> map = mapInstance(type);
            if (map != null) {
                Class<?> keyType = extractGenericType(genericType, 0);
                Class<?> valueType = extractGenericType(genericType, 1);
                int size = RANDOM.nextInt(2) + 1;
                for (int i = 0; i < size; i++) {
                    @SuppressWarnings("unchecked")
                    Class<Object> keyClass = (Class<Object>) (keyType != null ? keyType : String.class);
                    @SuppressWarnings("unchecked")
                    Class<Object> valueClass = (Class<Object>) (valueType != null ? valueType : String.class);
                    Object key = instantiate(keyClass, depth + 1, cache);
                    Object value = instantiate(valueClass, depth + 1, cache);
                    map.put(key, value);
                }
            }
            return map;
        }
        return instantiate(type, depth + 1, cache);
    }

    private static Collection<Object> collectionInstance(Class<?> type) {
        if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
            Collection<Object> concrete = instantiateCollectionClass(type);
            if (concrete != null) {
                return concrete;
            }
        }
        if (Set.class.isAssignableFrom(type)) {
            return new HashSet<>();
        }
        if (Queue.class.isAssignableFrom(type) || Deque.class.isAssignableFrom(type)) {
            return new ArrayDeque<>();
        }
        if (List.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        return null;
    }

    private static Map<Object, Object> mapInstance(Class<?> type) {
        if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
            Map<Object, Object> concrete = instantiateMapClass(type);
            if (concrete != null) {
                return concrete;
            }
        }
        if (Map.class.isAssignableFrom(type)) {
            return new LinkedHashMap<>();
        }
        return null;
    }

    private static Class<?> extractGenericType(Type genericType, int index) {
        if (genericType instanceof ParameterizedType parameterizedType) {
            Type[] args = parameterizedType.getActualTypeArguments();
            if (index < args.length && args[index] instanceof Class<?> clazz) {
                return clazz;
            }
        }
        return null;
    }

    private static Object generatePrimitive(Class<?> clazz) {
        if (clazz == int.class) {
            return RANDOM.nextInt(100);
        }
        if (clazz == long.class) {
            return RANDOM.nextLong();
        }
        if (clazz == short.class) {
            return (short) RANDOM.nextInt(Short.MAX_VALUE + 1);
        }
        if (clazz == byte.class) {
            return (byte) RANDOM.nextInt(Byte.MAX_VALUE + 1);
        }
        if (clazz == boolean.class) {
            return RANDOM.nextBoolean();
        }
        if (clazz == float.class) {
            return RANDOM.nextFloat();
        }
        if (clazz == double.class) {
            return RANDOM.nextDouble();
        }
        if (clazz == char.class) {
            return (char) ('a' + RANDOM.nextInt(26));
        }
        return null;
    }

    private static Object generateNumber(Class<?> clazz) {
        if (clazz == Integer.class) {
            return RANDOM.nextInt(100);
        }
        if (clazz == Long.class) {
            return RANDOM.nextLong();
        }
        if (clazz == Short.class) {
            return (short) RANDOM.nextInt(Short.MAX_VALUE + 1);
        }
        if (clazz == Byte.class) {
            return (byte) RANDOM.nextInt(Byte.MAX_VALUE + 1);
        }
        if (clazz == Float.class) {
            return RANDOM.nextFloat();
        }
        if (clazz == Double.class) {
            return RANDOM.nextDouble();
        }
        if (clazz == BigInteger.class) {
            return BigInteger.valueOf(positiveRandomLong());
        }
        if (clazz == BigDecimal.class) {
            return BigDecimal.valueOf(RANDOM.nextDouble());
        }
        if (clazz == AtomicInteger.class) {
            return new AtomicInteger(RANDOM.nextInt());
        }
        if (clazz == AtomicLong.class) {
            return new AtomicLong(RANDOM.nextLong());
        }
        return null;
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

    @SuppressWarnings("null")
    private static <T> T tryDateSubclassCtor(Class<T> clazz) {
        try {
            Constructor<T> longCtor = clazz.getDeclaredConstructor(long.class);
            longCtor.setAccessible(true);
            return longCtor.newInstance(positiveRandomLong());
        } catch (NoSuchMethodException ignored) {
            // fallback to no-arg
        } catch (Exception ignored) {
            return null;
        }
        try {
            Constructor<T> defaultCtor = clazz.getDeclaredConstructor();
            defaultCtor.setAccessible(true);
            return defaultCtor.newInstance();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static long positiveRandomLong() {
        return RANDOM.nextLong() & Long.MAX_VALUE;
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> instantiateCollectionClass(Class<?> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance();
            if (instance instanceof Collection<?>) {
                return (Collection<Object>) instance;
            }
        } catch (Exception ignored) {
            // fall back to defaults
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> instantiateMapClass(Class<?> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance();
            if (instance instanceof Map<?, ?>) {
                return (Map<Object, Object>) instance;
            }
        } catch (Exception ignored) {
            // fall back to defaults
        }
        return null;
    }

    public enum OutputFormat {
        TEXT,
        JSON;

        public static OutputFormat from(String raw) {
            for (OutputFormat value : values()) {
                if (value.name().equalsIgnoreCase(raw)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Неподдерживаемый формат: " + raw);
        }
    }

    private static final class ClassInfo {
        @JsonProperty("class")
        private final String className;
        @JsonProperty("superclass")
        private final String superclass;
        @JsonProperty("interfaces")
        private final List<String> interfaces;
        @JsonProperty("fields")
        private final List<FieldInfo> fields;
        @JsonProperty("methods")
        private final List<MethodInfo> methods;
        @JsonProperty("annotations")
        private final List<String> annotations;
        @JsonIgnore
        private final HierarchyNode hierarchyRoot;
        @JsonProperty("hierarchy")
        private final Map<String, Object> hierarchy;

        private ClassInfo(String className, String superclass, List<String> interfaces, List<FieldInfo> fields,
                          List<MethodInfo> methods, List<String> annotations, HierarchyNode hierarchyRoot,
                          Map<String, Object> hierarchy) {
            this.className = className;
            this.superclass = superclass;
            this.interfaces = interfaces;
            this.fields = fields;
            this.methods = methods;
            this.annotations = annotations;
            this.hierarchyRoot = hierarchyRoot;
            this.hierarchy = hierarchy;
        }
    }

    private static final class FieldInfo {
        @JsonProperty("access")
        private final String access;
        @JsonProperty("name")
        private final String name;
        @JsonProperty("type")
        private final String type;
        @JsonProperty("annotations")
        private final List<String> annotations;

        private FieldInfo(String access, String name, String type, List<String> annotations) {
            this.access = access;
            this.name = name;
            this.type = type;
            this.annotations = annotations;
        }
    }

    private static final class MethodInfo {
        @JsonProperty("access")
        private final String access;
        @JsonProperty("name")
        private final String name;
        @JsonProperty("params")
        private final List<String> params;
        @JsonProperty("returnType")
        private final String returnType;
        @JsonProperty("annotations")
        private final List<String> annotations;

        private MethodInfo(String access, String name, List<String> params, String returnType,
                           List<String> annotations) {
            this.access = access;
            this.name = name;
            this.params = params;
            this.returnType = returnType;
            this.annotations = annotations;
        }
    }

    private static final class HierarchyNode {
        private final String name;
        private final String fullName;
        private final List<HierarchyNode> children = new ArrayList<>();

        private HierarchyNode(String name, String fullName) {
            this.name = name;
            this.fullName = fullName;
        }

    }
}
