package academy.instance;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/** Создает и заполняет экземпляры классов случайными значениями. */
public class RandomInstanceFactory {

    private static final int MAX_DEPTH = 3;
    private final Random random = new Random();

    /**
     * Создает экземпляр указанного класса.
     *
     * @param clazz тип для создания
     * @param <T> тип экземпляра
     * @return заполненный объект
     */
    public <T> T create(Class<T> clazz) {
        try {
            return instantiate(clazz, 0, new IdentityHashMap<>());
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать экземпляр: " + clazz.getName(), e);
        }
    }

    @SuppressWarnings({"unchecked", "null"})
    private <T> T instantiate(Class<T> clazz, int depth, Map<Class<?>, Object> cache) throws Exception {
        if (clazz.isPrimitive()) {
            return (T) generatePrimitive(clazz);
        }

        Object scalar = generateScalar(clazz);
        if (scalar != null) {
            return (T) scalar;
        }
        if (cache.containsKey(clazz)) {
            return (T) cache.get(clazz);
        }
        if (depth > MAX_DEPTH || clazz == Object.class) {
            return (T) null;
        }
        if (clazz.isArray()) {
            return (T) buildArray(clazz.getComponentType(), depth, cache);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return (T) buildCollection(clazz, null, depth, cache);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return (T) buildMap(clazz, null, null, depth, cache);
        }
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return (T) null;
        }

        Constructor<T> constructor = findConstructor(clazz);
        if (constructor == null) {
            return (T) null;
        }
        constructor.setAccessible(true);
        Object[] args = Arrays.stream(constructor.getParameterTypes())
                .map(param -> instantiateSafely(param, depth + 1, cache))
                .toArray();
        T instance = constructor.newInstance(args);
        cache.put(clazz, instance);
        populateFields(clazz, instance, depth, cache);
        return instance;
    }

    private Object instantiateSafely(Class<?> param, int depth, Map<Class<?>, Object> cache) {
        try {
            return instantiate(param, depth, cache);
        } catch (Exception e) {
            return null;
        }
    }

    private void populateFields(Class<?> clazz, Object instance, int depth, Map<Class<?>, Object> cache)
            throws Exception {
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
    }

    private Object generateValue(Class<?> type, Type genericType, int depth, Map<Class<?>, Object> cache)
            throws Exception {
        if (depth > MAX_DEPTH) {
            return null;
        }
        if (type.isArray()) {
            return buildArray(type.getComponentType(), depth, cache);
        }
        if (Collection.class.isAssignableFrom(type)) {
            return buildCollection(type, genericType, depth, cache);
        }
        if (Map.class.isAssignableFrom(type)) {
            Class<?> keyType = extractGenericType(genericType, 0);
            Class<?> valueType = extractGenericType(genericType, 1);
            return buildMap(type, keyType, valueType, depth, cache);
        }
        return instantiate(type, depth, cache);
    }

    private Object buildArray(Class<?> component, int depth, Map<Class<?>, Object> cache) throws Exception {
        int size = random.nextInt(3) + 1;
        Object array = Array.newInstance(component, size);
        for (int i = 0; i < size; i++) {
            Array.set(array, i, instantiate(component, depth + 1, cache));
        }
        return array;
    }

    private Collection<Object> buildCollection(Class<?> type, Type genericType, int depth, Map<Class<?>, Object> cache)
            throws Exception {
        Collection<Object> collection = collectionInstance(type);
        if (collection == null) {
            return null;
        }
        int size = random.nextInt(3) + 1;
        Class<?> elementType = extractGenericType(genericType, 0);
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            Class<Object> elementClass = (Class<Object>) (elementType != null ? elementType : String.class);
            collection.add(instantiate(elementClass, depth + 1, cache));
        }
        return collection;
    }

    private Map<Object, Object> buildMap(
            Class<?> type, Class<?> keyType, Class<?> valueType, int depth, Map<Class<?>, Object> cache)
            throws Exception {
        Map<Object, Object> map = mapInstance(type);
        if (map == null) {
            return null;
        }
        int size = random.nextInt(3) + 1;
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            Class<Object> keyClass = (Class<Object>) (keyType != null ? keyType : String.class);
            @SuppressWarnings("unchecked")
            Class<Object> valueClass = (Class<Object>) (valueType != null ? valueType : String.class);
            Object key = instantiate(keyClass, depth + 1, cache);
            Object value = instantiate(valueClass, depth + 1, cache);
            map.put(key, value);
        }
        return map;
    }

    private Collection<Object> collectionInstance(Class<?> type) {
        if (!type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
            Collection<Object> concrete = instantiateCollectionClass(type);
            if (concrete != null) {
                return concrete;
            }
        }
        if (Set.class.isAssignableFrom(type)) {
            return new java.util.HashSet<>();
        }
        if (Queue.class.isAssignableFrom(type) || Deque.class.isAssignableFrom(type)) {
            return new ArrayDeque<>();
        }
        if (Collection.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        return null;
    }

    private Map<Object, Object> mapInstance(Class<?> type) {
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

    @SuppressWarnings("unchecked")
    private Collection<Object> instantiateCollectionClass(Class<?> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance();
            if (instance instanceof Collection<?> collection) {
                return (Collection<Object>) collection;
            }
        } catch (Exception ignored) {
            // fall back to defaults
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> instantiateMapClass(Class<?> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance();
            if (instance instanceof Map<?, ?> map) {
                return (Map<Object, Object>) map;
            }
        } catch (Exception ignored) {
            // fall back to defaults
        }
        return null;
    }

    private Class<?> extractGenericType(Type genericType, int index) {
        if (genericType instanceof ParameterizedType parameterizedType) {
            Type[] args = parameterizedType.getActualTypeArguments();
            if (index < args.length && args[index] instanceof Class<?> clazz) {
                return clazz;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findConstructor(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            return null;
        }
        Arrays.sort(constructors, (a, b) -> Integer.compare(a.getParameterCount(), b.getParameterCount()));
        return (Constructor<T>) constructors[0];
    }

    private Object generatePrimitive(Class<?> clazz) {
        if (clazz == int.class) {
            return random.nextInt(100);
        }
        if (clazz == long.class) {
            return random.nextLong();
        }
        if (clazz == short.class) {
            return (short) random.nextInt(Short.MAX_VALUE + 1);
        }
        if (clazz == byte.class) {
            return (byte) random.nextInt(Byte.MAX_VALUE + 1);
        }
        if (clazz == boolean.class) {
            return random.nextBoolean();
        }
        if (clazz == float.class) {
            return random.nextFloat();
        }
        if (clazz == double.class) {
            return random.nextDouble();
        }
        if (clazz == char.class) {
            return (char) ('a' + random.nextInt(26));
        }
        return null;
    }

    private Object generateScalar(Class<?> clazz) {
        if (clazz == String.class) {
            return randomString();
        }
        if (clazz.isEnum()) {
            Object[] constants = clazz.getEnumConstants();
            return constants.length == 0 ? null : constants[random.nextInt(constants.length)];
        }
        if (clazz == Boolean.class) {
            return Boolean.valueOf(random.nextBoolean());
        }
        if (Number.class.isAssignableFrom(clazz)) {
            Object number = generateNumber(clazz);
            if (number != null) {
                return number;
            }
        }
        if (clazz == Character.class) {
            return Character.valueOf((char) ('a' + random.nextInt(26)));
        }
        if (clazz == Date.class) {
            return new Date(positiveRandomLong());
        }
        if (clazz == java.sql.Date.class) {
            return new java.sql.Date(positiveRandomLong());
        }
        if (clazz == java.sql.Time.class) {
            return new java.sql.Time(positiveRandomLong());
        }
        if (clazz == java.sql.Timestamp.class) {
            return new java.sql.Timestamp(positiveRandomLong());
        }
        if (Date.class.isAssignableFrom(clazz)) {
            Object instance = tryDateSubclassCtor(clazz);
            if (instance != null) {
                return instance;
            }
        }
        if (clazz == LocalDate.class) {
            return LocalDate.now().plusDays(random.nextInt(30));
        }
        if (clazz == LocalDateTime.class) {
            return LocalDateTime.now().plusHours(random.nextInt(48));
        }
        if (clazz == LocalTime.class) {
            return LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));
        }
        if (clazz == Instant.class) {
            return Instant.ofEpochMilli(positiveRandomLong());
        }
        return null;
    }

    private Object generateNumber(Class<?> clazz) {
        if (clazz == Integer.class) {
            return random.nextInt(100);
        }
        if (clazz == Long.class) {
            return random.nextLong();
        }
        if (clazz == Short.class) {
            return (short) random.nextInt(Short.MAX_VALUE + 1);
        }
        if (clazz == Byte.class) {
            return (byte) random.nextInt(Byte.MAX_VALUE + 1);
        }
        if (clazz == Float.class) {
            return random.nextFloat();
        }
        if (clazz == Double.class) {
            return random.nextDouble();
        }
        if (clazz == BigInteger.class) {
            return BigInteger.valueOf(positiveRandomLong());
        }
        if (clazz == BigDecimal.class) {
            return BigDecimal.valueOf(random.nextDouble());
        }
        if (clazz == AtomicInteger.class) {
            return new AtomicInteger(random.nextInt());
        }
        if (clazz == AtomicLong.class) {
            return new AtomicLong(random.nextLong());
        }
        return null;
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    private Object tryDateSubclassCtor(Class<?> clazz) {
        try {
            Constructor<?> longCtor = clazz.getDeclaredConstructor(long.class);
            longCtor.setAccessible(true);
            return longCtor.newInstance(positiveRandomLong());
        } catch (NoSuchMethodException ignored) {
            // fallback to no-arg
        } catch (Exception ignored) {
            return null;
        }
        try {
            Constructor<?> defaultCtor = clazz.getDeclaredConstructor();
            defaultCtor.setAccessible(true);
            return defaultCtor.newInstance();
        } catch (Exception ignored) {
            return null;
        }
    }

    private long positiveRandomLong() {
        return random.nextLong() & Long.MAX_VALUE;
    }
}
