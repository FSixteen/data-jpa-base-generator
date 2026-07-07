package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Bean 反射辅助工具.
 *
 * <p>
 * 提供两类能力：
 * </p>
 * <ul>
 * <li><b>结构扫描</b> — 遍历类及其父类声明的全部字段和方法，用于注解扫描阶段收集 query model 上的候选属性.</li>
 * <li><b>属性解析</b> — 识别 Java Bean getter 方法并提取属性名，用于注解绑定字段名的推断.</li>
 * </ul>
 *
 * <p>
 * 该工具不处理 getter/setter 可读性判断，也不依赖 {@code java.beans.PropertyDescriptor}；
 * 所有逻辑基于方法签名直接推导，避免反射开销，适合编译期高频调用.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class BeanUtils {

    /**
     * 获取指定类及其全部父类上声明的字段.
     *
     * <p>
     * 包含所有访问修饰符（public/protected/default/private）以及继承而来的字段.
     * 去重基于字段对象引用, 同名不同声明的字段可能保留多个.
     * </p>
     *
     * @param clazz 目标类
     * @return 该类及其祖先类声明的全部字段数组
     */
    public static Field[] getAllFields(Class<?> clazz) {
        Set<Field> fieldList = new HashSet<>();
        while (Objects.nonNull(clazz)) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * 获取指定类及其全部父类上声明的方法.
     *
     * <p>
     * 包含所有访问修饰符的方法, 不含继承类（如 {@code Object}）的默认方法.
     * 去重基于方法对象引用.
     * </p>
     *
     * @param clazz 目标类
     * @return 该类及其祖先类声明的全部方法数组
     */
    public static Method[] getAllMethods(Class<?> clazz) {
        Set<Method> methodList = new HashSet<Method>();
        while (Objects.nonNull(clazz)) {
            methodList.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methodList.toArray(new Method[methodList.size()]);
    }

    /**
     * 判断给定方法是否为合法的 Java Bean 可读属性 getter.
     *
     * <p>
     * 合法 getter 需满足以下条件：
     * </p>
     * <ul>
     * <li>非静态、非合成、非 bridge 方法</li>
     * <li>无参数</li>
     * <li>返回值类型不是 {@code void}</li>
     * <li>方法名以 {@code get} 开头且剩余部分长度 &gt; 0, 或以 {@code is} 开头且返回
     * {@code boolean}</li>
     * </ul>
     *
     * @param method 待判断的方法
     * @return 如果方法是合法 getter 返回 {@code true}, 否则 {@code false}
     * @see #readablePropertyName(Method)
     */
    public static boolean isReadablePropertyMethod(final Method method) {
        return null != readablePropertyName(method);
    }

    /**
     * 从 Java Bean getter 方法中提取属性名.
     *
     * <p>
     * 支持两种标准命名约定：
     * </p>
     * <ul>
     * <li>{@code getXxx()} → 属性名为 {@code xxx}（首字母小写）</li>
     * <li>{@code isXxx()}（返回 {@code boolean}）→ 属性名为 {@code xxx}（首字母小写）</li>
     * </ul>
     *
     * <p>
     * 不符合 getter 约定的方法（静态、有参、返回 void、bridge/合成方法等）返回 {@code null}.
     * </p>
     *
     * @param method 待解析的 getter 方法
     * @return 属性名, 如果不是合法 getter 则返回 {@code null}
     */
    public static String readablePropertyName(final Method method) {
        if (null == method || method.isSynthetic() || method.isBridge() || Modifier.isStatic(method.getModifiers()) || 0 != method.getParameterCount()
            || Void.TYPE == method.getReturnType()) {
            return null;
        }
        String methodName = method.getName();
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return Introspector.decapitalize(methodName.substring(3));
        }
        if (methodName.startsWith("is") && methodName.length() > 2 && (Boolean.TYPE == method.getReturnType() || Boolean.class == method.getReturnType())) {
            return Introspector.decapitalize(methodName.substring(2));
        }
        return null;
    }

}
