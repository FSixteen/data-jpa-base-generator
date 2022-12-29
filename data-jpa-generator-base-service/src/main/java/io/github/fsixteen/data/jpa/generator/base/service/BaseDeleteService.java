package io.github.fsixteen.data.jpa.generator.base.service;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;
import io.github.fsixteen.data.jpa.generator.base.entities.BaseEntity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;
import io.github.fsixteen.data.jpa.generator.constants.DeleteType;
import io.github.fsixteen.data.jpa.generator.exception.DataNonExistException;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseDeleteService<T extends IdEntity<ID>, ID extends Serializable, D extends IdEntity<ID>> {
    static final Logger log = LoggerFactory.getLogger(BaseDeleteService.class);

    /**
     * 元素不存在时提示内容.
     * 
     * @return StatusInterface
     */
    default StatusInterface deleteNonDataExceptionStatus() {
        return Status.NONDATA_ERROR.get();
    }

    /**
     * 元素不存在时提示内容.
     * 
     * @return String
     */
    default String deleteNonDataExceptionMessage() {
        return this.deleteNonDataExceptionStatus().msg();
    }

    /**
     * 元素不存在时提示内容.
     * 
     * @return RuntimeException
     */
    default RuntimeException deleteNonDataException() {
        return new DataNonExistException(this.deleteNonDataExceptionStatus().code(), this.deleteNonDataExceptionMessage());
    }

    public BaseDao<T, ID> getDao();

    /**
     * 删除方式(默认软删除).
     * 
     * @return DeleteType;
     */
    default DeleteType deleteType() {
        return DeleteType.SOFT;
    }

    /**
     * 校验元素是否允许被删除处理器.
     * 
     * @return Predicate&lt;T&gt;
     */
    default Predicate<T> deleteTest() {
        return (args) -> Boolean.TRUE;
    }

    /**
     * 软删除执行处理器.
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<T> deleteProcessor() {
        return (args) -> {
            if (args instanceof BaseEntity<?>) {
                BaseEntity<?> eles = BaseEntity.class.cast(args);
                eles.setDeleted(true);
                eles.setDeleteTime(new Date());
            }
        };
    }

    /**
     * 元素删除后置处理器.
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<T> deletePostprocessor() {
        return (args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Delete Post Processor Nothing.");
            }
        };
    }

    /**
     * 删除逻辑.
     * 
     * @param args
     * @return T
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T delete(D args) {
        return this.deleteById(args.getId());
    }

    /**
     * 删除逻辑.
     * 
     * @param id
     * @return T
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id) {
        return this.deleteById(id, this.deleteTest());
    }

    /**
     * 删除逻辑.
     * 
     * @param id
     * @param filter
     * @return T
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id, Predicate<T> filter) {
        return this.deleteById(id, filter, this.deleteProcessor());
    }

    /**
     * 删除逻辑.
     * 
     * @param id
     * @param filter
     * @param processor
     * @return T
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id, Predicate<T> filter, Consumer<T> processor) {
        return this.deleteById(id, filter, this.deleteProcessor(), this.deletePostprocessor());
    }

    /**
     * 删除逻辑.
     * 
     * @param id            主键
     * @param filter        校验元素是否允许被删除处理器
     * @param processor     软删除执行处理器
     * @param postprocessor 元素删除后置处理器
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id, Predicate<T> filter, Consumer<T> processor, Consumer<T> postprocessor) {
        T ele = this.getDao().findById(id).filter(filter::test).orElseThrow(this::deleteNonDataException);
        switch (this.deleteType()) {
            case HARD:
                this.getDao().deleteById(id);
                break;
            case SOFT:
                if (Objects.nonNull(processor)) {
                    processor.accept(ele);
                }
                ele = this.getDao().save(ele);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + this.deleteType());
        }
        if (Objects.nonNull(postprocessor)) {
            postprocessor.accept(ele);
        }
        return ele;
    }
}
