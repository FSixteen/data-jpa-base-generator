package io.github.fsixteen.data.jpa.generator.exception;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;

/**
 * 反射数据异常.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public class ReflectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Long code = Status.GENERAL_ERROR.get().code();

    private String msg = "反射数据处理异常";

    public ReflectionException() {
    }

    public ReflectionException(StatusInterface status) {
        this(status.code(), status.msg());
    }

    public ReflectionException(String msg) {
        super(msg);
        this.msg = msg;
    }

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
