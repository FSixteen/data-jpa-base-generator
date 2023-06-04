package io.github.fsixteen.data.jpa.generator.exception;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;

/**
 * 内容已存在.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class DataExistedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Long code = Status.EXISTED_ERROR.get().code();

    private String msg = Status.EXISTED_ERROR.get().msg();

    public DataExistedException() {
    }

    public DataExistedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DataExistedException(StatusInterface status, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this(status.msg(), cause, enableSuppression, writableStackTrace);
        this.code = status.code();
        this.msg = status.msg();
    }

    public DataExistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataExistedException(StatusInterface status, Throwable cause) {
        this(status, cause, true, true);
    }

    public DataExistedException(Throwable cause) {
        super(cause);
    }

    public DataExistedException(StatusInterface status) {
        this(status.code(), status.msg());
    }

    /**
     * @param msg 提示内容
     */
    public DataExistedException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 扩展构造函数.
     * 
     * @param code 提示状态码
     * @param msg  提示内容
     */
    public DataExistedException(Long code, String msg) {
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
