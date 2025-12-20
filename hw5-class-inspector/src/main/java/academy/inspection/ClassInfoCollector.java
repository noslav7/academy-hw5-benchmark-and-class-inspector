package academy.inspection;

import academy.hierarchy.HierarchyBuilder;
import academy.model.ClassInfo;
import academy.model.FieldInfo;
import academy.model.HierarchyNode;
import academy.model.MethodInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Собирает информацию о структуре класса.
 */
public class ClassInfoCollector {

    private final HierarchyBuilder hierarchyBuilder;

    public ClassInfoCollector(HierarchyBuilder hierarchyBuilder) {
        this.hierarchyBuilder = hierarchyBuilder;
    }

    /**
     * Выполняет сбор данных по классу.
     *
     * @param clazz анализируемый класс
     * @return заполненный {@link ClassInfo}
     */
    public ClassInfo collect(Class<?> clazz) {
        String className = clazz.getName();
        String superclass = clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class
                ? clazz.getSuperclass().getName() : null;
        List<String> interfaces = Arrays.stream(clazz.getInterfaces())
                .map(Class::getSimpleName)
                .toList();

        List<FieldInfo> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(this::toFieldInfo)
                .toList();

        List<MethodInfo> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> !method.isSynthetic())
                .map(this::toMethodInfo)
                .toList();

        Set<String> annotations = collectAnnotations(clazz);
        HierarchyNode hierarchyRoot = hierarchyBuilder.build(clazz);

        return new ClassInfo(
                className,
                superclass,
                interfaces,
                fields,
                methods,
                List.copyOf(annotations),
                hierarchyRoot);
    }

    private FieldInfo toFieldInfo(Field field) {
        return new FieldInfo(
                AccessModifierResolver.resolve(field.getModifiers()),
                field.getName(),
                field.getType().getSimpleName(),
                AnnotationExtractor.extract(field.getAnnotations()));
    }

    private MethodInfo toMethodInfo(Method method) {
        List<String> params = Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .toList();
        return new MethodInfo(
                AccessModifierResolver.resolve(method.getModifiers()),
                method.getName(),
                params,
                method.getReturnType().getSimpleName(),
                AnnotationExtractor.extract(method.getAnnotations()));
    }

    private Set<String> collectAnnotations(Class<?> clazz) {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.addAll(AnnotationExtractor.extract(clazz.getAnnotations()));
        Arrays.stream(clazz.getDeclaredFields())
                .forEach(field -> annotations.addAll(AnnotationExtractor.extract(field.getAnnotations())));
        Arrays.stream(clazz.getDeclaredMethods())
                .forEach(method -> annotations.addAll(AnnotationExtractor.extract(method.getAnnotations())));
        return annotations;
    }
}

