package io.github.fsixteen.data.jpa.generator.exception;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;

/**
 * 反射数据异常.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class ReflectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Long code = Status.GENERAL_ERROR.get().code();

    private String msg = "反射数据处理异常";

    public ReflectionException() {
    }

    public ReflectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ReflectionException(StatusInterface status, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status.msg(), cause, enableSuppression, writableStackTrace);
        this.code = status.code();
        this.msg = status.msg();
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(StatusInterface status, Throwable cause) {
        this(status, cause, true, true);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

    public ReflectionException(StatusInterface status) {
        this(status.code(), status.msg());
    }

    /**
     * @param msg 提示内容
     */
    public ReflectionException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 扩展构造函数.
     * 
     * @param code 提示状态码
     * @param msg  提示内容
     */
    public ReflectionException(Long code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
