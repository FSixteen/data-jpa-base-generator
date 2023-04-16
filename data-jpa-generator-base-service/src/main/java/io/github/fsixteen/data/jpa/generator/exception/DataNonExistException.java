package io.github.fsixteen.data.jpa.generator.exception;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;

/**
 * 内容不存在.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public class DataNonExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Long code = Status.NONDATA_ERROR.get().code();

    private String msg = Status.NONDATA_ERROR.get().msg();

    public DataNonExistException() {
    }

    public DataNonExistException(StatusInterface status) {
        this(status.code(), status.msg());
    }

    public DataNonExistException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 扩展构造函数.
     * 
     * @param code 提示状态码
     * @param msg  提示内容
     */
    public DataNonExistException(Long code, String msg) {
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
