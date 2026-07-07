package io.github.fsixteen.data.jpa.base.generator.plugins.exceptions;

import java.beans.IntrospectionException;

/**
 * {@link IntrospectionException} 的运行时包装异常.
 *
 * <p>
 * 当前解释器会在可读属性解析、BeanInfo 扫描等场景使用 JDK Introspector.
 * 这些流程本质上属于框架内部基础设施, 不适合作为受检异常继续向外扩散,
 * 因此统一包装为该运行时异常.
 * </p>
 *
 * <p>
 * 常见触发原因包括：属性不存在可读 getter、Bean 元数据无法解析,
 * 或反射出的属性签名不符合当前使用场景.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.0
 * @see java.beans.IntrospectionException
 */
public class IntrospectionRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IntrospectionRuntimeException() {
        super();
    }

    public IntrospectionRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IntrospectionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntrospectionRuntimeException(String message) {
        super(message);
    }

    public IntrospectionRuntimeException(Throwable cause) {
        super(cause);
    }

}
