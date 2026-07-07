package io.github.fsixteen.data.jpa.base.generator.plugins.support;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.github.fsixteen.data.jpa.base.generator.plugins.exceptions.IntrospectionRuntimeException;

/**
 * 只读属性访问辅助器.
 *
 * <p>
 * JDK 的 {@link PropertyDescriptor#PropertyDescriptor(String, Class)} 默认会同时尝试解析
 * getter/setter；但当前 compiled 读取链路只要求可读, 不应额外强依赖 setter.
 * 因此统一通过 BeanInfo 扫描找到“有 getter 的属性”.
 * </p>
 *
 * <p>
 * 该工具主要服务于
 * {@link io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledAnnotationSpec}
 * 在字段绑定阶段解析参数对象属性描述符, 避免 query model 上的只读属性被误判为不可用.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class ReadablePropertySupport {

    private ReadablePropertySupport() {
    }

    /**
     * 解析指定类型上的可读属性描述器.
     *
     * <p>
     * 与直接构造 {@link PropertyDescriptor} 不同, 这里只要求属性存在 getter,
     * 不要求 setter, 从而兼容只读 query model.
     * </p>
     *
     * @param beanClass    Bean 类型
     * @param propertyName 属性名
     * @return 可读属性描述器；当输入类型或属性名为空时返回 {@code null}
     * @throws IntrospectionRuntimeException 当未找到可读属性, 或 introspection 过程失败时抛出
     */
    public static PropertyDescriptor descriptor(final Class<?> beanClass, final String propertyName) {
        if (null == beanClass || null == propertyName) {
            return null;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (propertyName.equals(descriptor.getName()) && null != descriptor.getReadMethod()) {
                    return descriptor;
                }
            }
            throw new IntrospectionRuntimeException(new IntrospectionException("Readable property not found: " + propertyName));
        } catch (IntrospectionException e) {
            throw new IntrospectionRuntimeException(e);
        }
    }

    /**
     * 读取对象上某个可读属性的值.
     *
     * @param bean         目标对象
     * @param propertyName 属性名
     * @return 属性值；目标对象为空时返回 {@code null}
     * @throws IntrospectionRuntimeException 当属性不存在或不可读时抛出
     * @throws IllegalStateException         当底层 getter 调用失败时抛出
     */
    public static Object read(final Object bean, final String propertyName) {
        if (null == bean) {
            return null;
        }
        PropertyDescriptor descriptor = descriptor(bean.getClass(), propertyName);
        Method readMethod = descriptor.getReadMethod();
        try {
            readMethod.setAccessible(true);
            return readMethod.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to read property '" + propertyName + "' from " + bean.getClass().getName(), e);
        }
    }

}
