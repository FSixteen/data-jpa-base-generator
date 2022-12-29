package io.github.fsixteen.data.jpa.generator.exception;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;

/**
 * 内容已存在.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public class DataExistedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Long code = Status.EXISTED_ERROR.get().code();

    private String msg = Status.EXISTED_ERROR.get().msg();

    public DataExistedException() {
    }

    public DataExistedException(StatusInterface status) {
        this(status.code(), status.msg());
    }

    public DataExistedException(String msg) {
        super(msg);
        this.msg = msg;
    }

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
