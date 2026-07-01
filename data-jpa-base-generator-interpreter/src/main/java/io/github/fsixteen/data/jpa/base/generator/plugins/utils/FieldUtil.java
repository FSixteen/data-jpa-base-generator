package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

/**
 * 字段获取工具.
 * <p>
 * 该工具类的核心用途是: 将方法引用形式的 getter lambda
 * （例如 {@code User::getName}、{@code User::isActive}）反向解析为
 * 对应的字段信息，从而避免在调用方直接写字符串字段名。
 * </p>
 * <p>
 * 整体解析流程如下:
 * </p>
 * <ol>
 * <li>通过 lambda 生成类上的 {@code writeReplace} 方法拿到 {@link SerializedLambda}</li>
 * <li>从 {@link SerializedLambda} 中提取实际实现方法名，例如 {@code getName}</li>
 * <li>根据 Java Bean getter 规范推导出字段名，例如 {@code name}</li>
 * <li>根据实现类和字段名，通过反射获取最终的 {@link Field}</li>
 * </ol>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class FieldUtil {

    /**
     * 表示接受一个参数并产生结果的函数.<br>
     * <p>
     * 这里额外继承 {@link Serializable}，是因为 Java 在将 lambda 序列化时，
     * 可以暴露出内部的 {@link SerializedLambda} 结构；本工具正是利用这一点，
     * 从方法引用中还原出其真实指向的方法名和实现类。
     * </p>
     * 
     * @param <T> 入参类型
     * @param <R> 出参类型
     * @author FSixteen
     * @since 1.0.0
     */
    @FunctionalInterface
    public static interface SFunction<T, R> extends Function<T, R>, Serializable {
    }

    /**
     * 将 bean 属性的 getter 方法作为 lambda 表达式传入时，获取对应字段名称.<br>
     * <p>
     * 例如:
     * </p>
     * 
     * <pre>
     * FieldUtil.getFieldName(User::getName) -> "name"
     * FieldUtil.getFieldName(User::isActive) -> "active"
     * </pre>
     * <p>
     * 该方法本身不直接做解析，而是复用 {@link #getField(SFunction)} 的解析结果，
     * 统一保证字段名推导逻辑只有一份。
     * </p>
     *
     * @param <T> 泛型
     * @param fn  lambda 表达式，通常为 bean 属性 getter 的方法引用
     * @return getter 对应的字段名称
     */
    public static <T> String getFieldName(SFunction<T, ?> fn) {
        return getField(fn).getName();
    }

    /**
     * 将 bean 属性的 getter 方法作为 lambda 表达式传入时，获取对应字段对象.<br>
     * <p>
     * 这里的输入预期是符合 Java Bean 规范的方法引用，例如 {@code getXxx} 或
     * {@code isXxx}。如果传入的是普通实例方法（例如 {@code User::toString}），
     * 会因为不符合 getter 规范而抛出 {@link IllegalArgumentException}。
     * </p>
     * <p>
     * 如果方法名本身能推导出字段名，但目标类中不存在该字段，则会在最终反射查找时
     * 抛出 {@link IllegalStateException}。
     * </p>
     *
     * @param <T> 泛型
     * @param fn  lambda 表达式，通常为 bean 属性 getter 的方法引用
     * @return getter 对应的字段对象
     */
    public static <T> Field getField(SFunction<T, ?> fn) {
        try {
            SerializedLambda serializedLambda = resolveSerializedLambda(fn);
            String fieldName = resolveFieldName(serializedLambda.getImplMethodName());
            return resolveField(serializedLambda, fieldName);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("无法从lambda表达式中解析字段", e);
        }
    }

    /**
     * 从可序列化的 lambda 中提取 {@link SerializedLambda}.
     * <p>
     * Java 编译器会为 lambda 生成一个合成类，该类中通常包含一个
     * {@code writeReplace} 方法。通过反射调用这个方法，可以拿到
     * {@link SerializedLambda}，其中保存了实现类、实现方法名等元数据。
     * </p>
     *
     * @param fn 可序列化 lambda
     * @return lambda 对应的序列化结构
     * @throws ReflectiveOperationException 反射获取失败时抛出
     */
    private static SerializedLambda resolveSerializedLambda(SFunction<?, ?> fn) throws ReflectiveOperationException {
        Objects.requireNonNull(fn, "lambda表达式不能为空");
        Method writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
        // writeReplace 为编译器生成的内部方法，需要提升可见性后才能调用。
        writeReplaceMethod.setAccessible(true);
        return (SerializedLambda) writeReplaceMethod.invoke(fn);
    }

    /**
     * 根据 getter 方法名推导字段名.
     * <p>
     * 支持两种 Java Bean getter 前缀:
     * </p>
     * <ul>
     * <li>{@code getXxx}</li>
     * <li>{@code isXxx}</li>
     * </ul>
     *
     * @param methodName 实际实现方法名
     * @return 对应字段名
     */
    private static String resolveFieldName(String methodName) {
        if (methodName.startsWith("get")) {
            return extractFieldName(methodName, 3);
        }
        if (methodName.startsWith("is")) {
            return extractFieldName(methodName, 2);
        }
        throw new IllegalArgumentException("方法名称: " + methodName + ", 不符合Java Bean getter规范");
    }

    /**
     * 截取 getter 前缀之后的属性片段，并按 Java Bean 规范首字母处理.
     * <p>
     * 这里使用 {@link Introspector#decapitalize(String)}，而不是简单地把首字母转小写，
     * 是为了兼容类似 {@code getURL()} 这样的场景:
     * </p>
     * 
     * <pre>
     * getName -> name
     * getURL  -> URL
     * </pre>
     *
     * @param methodName   getter 方法名
     * @param prefixLength getter 前缀长度，{@code get} 为 3，{@code is} 为 2
     * @return 解析后的字段名
     */
    private static String extractFieldName(String methodName, int prefixLength) {
        if (methodName.length() <= prefixLength) {
            throw new IllegalArgumentException("方法名称: " + methodName + ", 不符合Java Bean getter规范");
        }
        return Introspector.decapitalize(methodName.substring(prefixLength));
    }

    /**
     * 根据 lambda 中记录的实现类和字段名，反射获取真实字段对象.
     *
     * @param serializedLambda lambda 的序列化结构
     * @param fieldName        已解析出的字段名
     * @return 目标字段对象
     * @throws ReflectiveOperationException 目标类或字段不存在时抛出
     */
    private static Field resolveField(SerializedLambda serializedLambda, String fieldName) throws ReflectiveOperationException {
        // SerializedLambda 中的类名使用 JVM 内部格式，例如 a/b/C，需要转换为标准类名。
        String implClassName = serializedLambda.getImplClass().replace('/', '.');
        return Class.forName(implClassName).getDeclaredField(fieldName);
    }

}
