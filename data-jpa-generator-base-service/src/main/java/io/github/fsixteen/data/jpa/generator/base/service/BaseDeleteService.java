package io.github.fsixteen.data.jpa.generator.base.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
 * @since 1.0.0
 */
public interface BaseDeleteService<T extends IdEntity<ID>, ID extends Serializable, D extends IdEntity<ID>> {

    static final Logger log = LoggerFactory.getLogger(BaseDeleteService.class);

    public BaseDao<T, ID> getDao();

    /**
     * 元素不存在时提示内容.<br>
     *
     * @return StatusInterface
     */
    default StatusInterface deleteNonDataExceptionStatus() {
        return Status.NONDATA_ERROR.get();
    }

    /**
     * 元素不存在时提示内容.<br>
     *
     * @return String
     */
    default String deleteNonDataExceptionMessage() {
        return this.deleteNonDataExceptionStatus().msg();
    }

    /**
     * 元素不存在时提示内容.<br>
     *
     * @return RuntimeException
     */
    default RuntimeException deleteNonDataException() {
        return new DataNonExistException(this.deleteNonDataExceptionStatus().code(), this.deleteNonDataExceptionMessage());
    }

    /**
     * 删除方式(默认软删除).<br>
     *
     * @return DeleteType;
     */
    default DeleteType deleteType() {
        return DeleteType.SOFT;
    }

    /**
     * 校验元素是否允许被删除处理器.<br>
     *
     * @return Predicate&lt;T&gt;
     */
    default Predicate<T> deleteTest() {
        return (args) -> Boolean.TRUE;
    }

    /**
     * 软删除执行处理器.<br>
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
     * 元素删除后置处理器.<br>
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
     * 元素删除后置处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<Collection<T>> deleteAllPostprocessor() {
        return (args) -> {
            if (Objects.nonNull(args) && !args.isEmpty()) {
                args.forEach(it -> this.deletePostprocessor().accept(it));
            }
        };
    }

    /**
     * 删除逻辑.<br>
     *
     * @param args 删除实体实例
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T delete(D args) {
        return this.deleteById(args.getId());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param id 删除实体主键ID
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id) {
        return this.deleteById(id, this.deleteTest());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param id     删除实体主键ID
     * @param filter 校验元素是否允许被删除处理器
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id, Predicate<T> filter) {
        return this.deleteById(id, filter, this.deleteProcessor());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param id        删除实体主键ID
     * @param filter    校验元素是否允许被删除处理器
     * @param processor 删除处理器
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id, Predicate<T> filter, Consumer<T> processor) {
        return this.deleteById(id, filter, this.deleteProcessor(), this.deletePostprocessor());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param id            主键
     * @param filter        校验元素是否允许被删除处理器
     * @param processor     删除处理器
     * @param postprocessor 删除后置处理器
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T deleteById(ID id, Predicate<T> filter, Consumer<T> processor, Consumer<T> postprocessor) {
        return this.deleteAllByIds(Arrays.asList(id), filter, processor, (args) -> {
            if (Objects.nonNull(postprocessor) && Objects.nonNull(args) && !args.isEmpty()) {
                args.forEach(it -> postprocessor.accept(it));
            }
        }).get(0);
    }

    /**
     * 删除逻辑.<br>
     *
     * @param args 删除实体实例
     * @see #deleteAllByIds(Serializable, Predicate, Consumer, Consumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> deleteAll(Collection<D> args) {
        return this.deleteAllByIds(args.stream().map(IdEntity::getId).collect(Collectors.toSet()));
    }

    /**
     * 删除逻辑.<br>
     *
     * @param ids 删除实体主键ID
     * @see #deleteAllByIds(Serializable, Predicate, Consumer, Consumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> deleteAllByIds(Collection<ID> ids) {
        return this.deleteAllByIds(ids, this.deleteTest());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param ids    删除实体主键ID
     * @param filter 校验元素是否允许被删除处理器
     * @see #deleteAllByIds(Serializable, Predicate, Consumer, Consumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> deleteAllByIds(Collection<ID> ids, Predicate<T> filter) {
        return this.deleteAllByIds(ids, filter, this.deleteProcessor());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param ids       删除实体主键ID
     * @param filter    校验元素是否允许被删除处理器
     * @param processor 删除处理器
     * @see #deleteAllByIds(Serializable, Predicate, Consumer, Consumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> deleteAllByIds(Collection<ID> ids, Predicate<T> filter, Consumer<T> processor) {
        return this.deleteAllByIds(ids, filter, this.deleteProcessor(), this.deleteAllPostprocessor());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param ids           主键
     * @param filter        校验元素是否允许被删除处理器
     * @param processor     删除处理器
     * @param postprocessor 删除后置处理器
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> deleteAllByIds(Collection<ID> ids, Predicate<T> filter, Consumer<T> processor, Consumer<Collection<T>> postprocessor) {
        List<T> ele = this.getDao().findAllById(ids).stream().filter(filter::test).collect(Collectors.toList());
        if (ele.isEmpty()) {
            throw this.deleteNonDataException();
        }
        switch (this.deleteType()) {
            case HARD:
                this.getDao().deleteAllInBatch(ele);
                break;
            case SOFT:
                if (Objects.nonNull(processor)) {
                    ele.forEach(e -> processor.accept(e));
                }
                ele = this.getDao().saveAll(ele);
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
