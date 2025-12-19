package academy.accessor;

import academy.model.Student;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Фабрика для создания различных стратегий доступа к полю студента.
 */
public final class AccessorFactory {

    private static final String METHOD_NAME = "name";

    private AccessorFactory() {}

    /**
     * Создает набор всех доступных стратегий доступа.
     *
     * @return набор стратегий
     */
    public static AccessorSet createAll() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Method reflectiveMethod = createReflectionMethod();
        MethodHandle methodHandle = createMethodHandle(lookup);
        LambdaAccessor.StudentNameGetter lambdaGetter = createLambdaGetter(lookup, methodHandle);

        return new AccessorSet(
                new DirectAccessor(),
                new ReflectionAccessor(reflectiveMethod),
                new MethodHandleAccessor(methodHandle),
                new LambdaAccessor(lambdaGetter));
    }

    /**
     * Создает метод для доступа через рефлексию.
     */
    private static Method createReflectionMethod() {
        try {
            return Student.class.getMethod(METHOD_NAME);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Не удалось получить метод для рефлексии", e);
        }
    }

    /**
     * Создает MethodHandle для доступа к методу.
     */
    private static MethodHandle createMethodHandle(MethodHandles.Lookup lookup) {
        try {
            return lookup.findVirtual(
                    Student.class, METHOD_NAME, MethodType.methodType(String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException("Не удалось создать MethodHandle", e);
        }
    }

    /**
     * Создает lambda-геттер через LambdaMetafactory.
     */
    private static LambdaAccessor.StudentNameGetter createLambdaGetter(
            MethodHandles.Lookup lookup, MethodHandle methodHandle) {
        try {
            MethodType instanceGetter = MethodType.methodType(String.class, Student.class);
            CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "get",
                    MethodType.methodType(LambdaAccessor.StudentNameGetter.class),
                    instanceGetter,
                    methodHandle,
                    instanceGetter);
            return (LambdaAccessor.StudentNameGetter) callSite.getTarget().invokeExact();
        } catch (LambdaConversionException e) {
            throw new IllegalStateException("Не удалось создать lambda-акцессор", e);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Ошибка при создании lambda-акцессора", throwable);
        }
    }

    /**
     * Набор всех стратегий доступа.
     */
    public static final class AccessorSet {
        private final DirectAccessor direct;
        private final ReflectionAccessor reflection;
        private final MethodHandleAccessor methodHandle;
        private final LambdaAccessor lambda;

        AccessorSet(
                DirectAccessor direct,
                ReflectionAccessor reflection,
                MethodHandleAccessor methodHandle,
                LambdaAccessor lambda) {
            this.direct = direct;
            this.reflection = reflection;
            this.methodHandle = methodHandle;
            this.lambda = lambda;
        }

        public AccessorStrategy direct() {
            return direct;
        }

        public AccessorStrategy reflection() {
            return reflection;
        }

        public AccessorStrategy methodHandle() {
            return methodHandle;
        }

        public AccessorStrategy lambda() {
            return lambda;
        }
    }
}

