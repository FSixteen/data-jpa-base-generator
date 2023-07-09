package io.github.fsixteen.data.jpa.generator.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.github.fsixteen.common.structure.Response.SimpleResponse;
import io.github.fsixteen.common.structure.extend.Err;
import io.github.fsixteen.data.jpa.generator.exception.DataExistedException;
import io.github.fsixteen.data.jpa.generator.exception.DataNonExistException;

/**
 * 自定义 RestControllerAdvice .<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public class FsnRestControllerAdvice {

    /**
     * 数据已存在.
     * 
     * @param e 异常
     * @return SimpleResponse
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(DataExistedException.class)
    public SimpleResponse<?> dataExistedExceptionResponse(DataExistedException e) {
        return Err.existed();
    }

    /**
     * 数据不存在.
     * 
     * @param e 异常
     * @return SimpleResponse
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(DataNonExistException.class)
    public SimpleResponse<?> dataNonExistExceptionResponse(DataNonExistException e) {
        return Err.nondata();
    }

}
