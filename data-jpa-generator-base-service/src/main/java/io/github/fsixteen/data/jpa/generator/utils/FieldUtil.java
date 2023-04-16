package io.github.fsixteen.data.jpa.generator.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 字段获取工具.
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public class FieldUtil {

    /**
     * 表示接受一个参数并产生结果的函数.<br>
     * 
     * @param <T> 入参类型
     * @param <R> 出参类型
     * @author FSixteen
     * @since V1.0.0
     */
    @FunctionalInterface
    public static interface SFunction<T, R> extends Function<T, R>, Serializable {
    }

    /**
     * 将bean属性的get方法, 作为lambda表达式传入时, 获取get方法对应的属性Field.<br>
     *
     * @param <T> 泛型
     * @param fn  lambda表达式, bean的属性的get方法
     * @return String
     */
    public static <T> String getFieldName(SFunction<T, ?> fn) {
        return getField(fn).getName();
    }

    /**
     * 将bean属性的get方法, 作为lambda表达式传入时, 获取get方法对应的属性Field.<br>
     *
     * @param <T> 泛型
     * @param fn  lambda表达式, bean的属性的get方法
     * @return Field
     */
    public static <T> Field getField(SFunction<T, ?> fn) {
        try {
            Method writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
            boolean isAccessible = writeReplaceMethod.isAccessible();
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
            writeReplaceMethod.setAccessible(isAccessible);
            String implMethodName = serializedLambda.getImplMethodName();
            if (!implMethodName.startsWith("is") && !implMethodName.startsWith("get")) {
                throw new RuntimeException("get方法名称: " + implMethodName + ", 不符合java bean规范");
            }
            int prefixLen = implMethodName.startsWith("is") ? 2 : 3;

            String fieldName = implMethodName.substring(prefixLen);
            String firstChar = fieldName.substring(0, 1);
            fieldName = fieldName.replaceFirst(firstChar, firstChar.toLowerCase());
            return Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
