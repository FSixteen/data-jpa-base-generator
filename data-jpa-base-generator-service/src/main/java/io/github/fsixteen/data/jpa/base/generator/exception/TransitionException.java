package io.github.fsixteen.data.jpa.base.generator.exception;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;

/**
 * 数据转换异常.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class TransitionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Long code = Status.GENERAL_ERROR.get().code();

    private String msg = "数据转换处理异常";

    /**
     * Constructs a new runtime exception of a specific type with {@code null}
     * as its detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to {@link #initCause}.
     */
    public TransitionException() {
        super();
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * detail message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     * 
     * @param message            the detail message.
     * @param cause              the cause. (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or
     *                           unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     */
    public TransitionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * detail status, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     * 
     * @param status             the detail status. The detail status is saved
     *                           for later retrieval by the {@link #getCode()}
     *                           method and {@link #getMsg()} method and
     *                           {@link #getMessage()} method.
     * @param cause              the cause. (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or
     *                           unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     */
    public TransitionException(StatusInterface status, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status.msg(), cause, enableSuppression, writableStackTrace);
        this.code = status.code();
        this.msg = status.msg();
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this runtime exception's detail message.
     * </p>
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public TransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * detail status and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this runtime exception's detail message.
     * </p>
     *
     * @param status the detail status. The detail status is saved for
     *               later retrieval by the {@link #getCode()} method and
     *               {@link #getMsg()} method and {@link #getMessage()} method.
     * @param cause  the cause (which is saved for later retrieval by the
     *               {@link #getCause()} method). (A {@code null} value is
     *               permitted, and indicates that the cause is nonexistent or
     *               unknown.)
     */
    public TransitionException(StatusInterface status, Throwable cause) {
        this(status, cause, true, true);
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains
     * the class and detail message of {@code cause}). This constructor is
     * useful for runtime exceptions that are little more than wrappers for
     * other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public TransitionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * detail status. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param status the detail status. The detail status is saved for
     *               later retrieval by the {@link #getCode()} method and
     *               {@link #getMsg()} method and {@link #getMessage()} method.
     */
    public TransitionException(StatusInterface status) {
        this(status.code(), status.msg());
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param msg the detail message. The detail message is saved for
     *            later retrieval by the {@link #getMsg()} method and
     *            {@link #getMessage()} method.
     */
    public TransitionException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * Constructs a new runtime exception of a specific type with the specified
     * detail message and status code. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     * 
     * @param code the detail status code. The detail status code is saved for
     *             later retrieval by the {@link #getCode()} method.
     * @param msg  the detail message. The detail message is saved for later
     *             retrieval by the {@link #getMsg()} method and
     *             {@link #getMessage()} method.
     */
    public TransitionException(Long code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * Return the detail status code.
     * 
     * @return Long
     */
    public Long getCode() {
        return code;
    }

    /**
     * Return the detail status message.
     * 
     * @return String
     */
    public String getMsg() {
        return msg;
    }

}
