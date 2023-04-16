package io.github.fsixteen.data.jpa.base.generator.plugins.exceptions;

import java.beans.IntrospectionException;

/**
 * {@link IntrospectionException}由{@link java.lang.Exception}转为{@link java.lang.RuntimeException}.
 * <p>
 * Thrown when an exception happens during Introspection.
 * </p>
 * <p>
 * Typical causes include not being able to map a string class name
 * to a Class object, not being able to resolve a string method name,
 * or specifying a method name that has the wrong type signature for
 * its intended use.
 * </p>
 * 
 * @author FSixteen
 * @since V1.0.0
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
