package io.github.fsixteen.data.jpa.generator.exception;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;

/**
 * 访问拒绝.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public class AccessDeniedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Long code = Status.PERMISSION_ERROR.get().code();

    private String msg = Status.PERMISSION_ERROR.get().msg();

    public AccessDeniedException() {
    }

    public AccessDeniedException(StatusInterface status) {
        this(status.code(), status.msg());
    }

    /**
     * @param msg 提示内容
     */
    public AccessDeniedException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 扩展构造函数.
     * 
     * @param code 提示状态码
     * @param msg  提示内容
     */
    public AccessDeniedException(Long code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 提示状态码.
     * 
     * @return Long
     */
    public Long getCode() {
        return code;
    }

    /**
     * 提示内容.
     * 
     * @return String
     */
    public String getMsg() {
        return msg;
    }

}
