package io.github.fsixteen.data.jpa.base.generator.config;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.github.fsixteen.common.structure.Response.SimpleResponse;
import io.github.fsixteen.common.structure.extend.Err;
import io.github.fsixteen.data.jpa.base.generator.exception.AccessDeniedException;
import io.github.fsixteen.data.jpa.base.generator.exception.BadRequestException;
import io.github.fsixteen.data.jpa.base.generator.exception.DataExistedException;
import io.github.fsixteen.data.jpa.base.generator.exception.DataNonExistException;
import io.github.fsixteen.data.jpa.base.generator.exception.ParameterException;

/**
 * 自定义 RestControllerAdvice .<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public class FsnRestControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(FsnRestControllerAdvice.class);

    /**
     * 数据已存在.
     * 
     * @param e 异常
     * @return SimpleResponse
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(DataExistedException.class)
    public SimpleResponse<?> dataExistedExceptionResponse(DataExistedException e) {
        LOG.error(e.getMessage(), e);
        return Err.existed(null, Optional.ofNullable(e.getMessage()).orElseGet(() -> "数据已存在"));
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
        LOG.error(e.getMessage(), e);
        return Err.nondata(null, Optional.ofNullable(e.getMessage()).orElseGet(() -> "暂无数据"));
    }

    /**
     * 查无操作数据.
     * 
     * @param e 异常
     * @return SimpleResponse
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(EntityNotFoundException.class)
    public SimpleResponse<Object> entityNotFoundExceptionResponse(EntityNotFoundException e) {
        LOG.error(e.getMessage(), e);
        return Err.permission(null, Optional.ofNullable(e.getMessage()).orElseGet(() -> "查无操作数据"));
    }

    /**
     * 查无操作权限.
     * 
     * @param e 异常
     * @return SimpleResponse
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(AccessDeniedException.class)
    public SimpleResponse<Object> accessDeniedException(AccessDeniedException e) {
        LOG.error(e.getMessage(), e);
        return Err.permission(null, Optional.ofNullable(e.getMessage()).orElseGet(() -> "查无操作权限"));
    }

    /**
     * 异常请求.
     * 
     * @param e 异常
     * @return SimpleResponse
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public SimpleResponse<Object> badRequestException(BadRequestException e) {
        LOG.error(e.getMessage(), e);
        return Err.permission(null, Optional.ofNullable(e.getMessage()).orElseGet(() -> "异常请求"));
    }

    /**
     * 参数错误请求.
     * 
     * @param e 异常
     * @return SimpleResponse
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(ParameterException.class)
    public SimpleResponse<Object> parameterException(ParameterException e) {
        LOG.error(e.getMessage(), e);
        return Err.args(e.getParams(), Optional.ofNullable(e.getMessage()).orElseGet(() -> "参数错误"));
    }

}
