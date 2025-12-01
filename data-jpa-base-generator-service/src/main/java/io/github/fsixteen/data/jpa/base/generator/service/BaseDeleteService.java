package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;
import io.github.fsixteen.data.jpa.base.generator.constants.DeleteType;
import io.github.fsixteen.data.jpa.base.generator.entities.BaseEntity;
import io.github.fsixteen.data.jpa.base.generator.entities.BaseTSTypeEntity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.exception.DataNonExistException;
import io.github.fsixteen.data.jpa.base.generator.jpa.BaseDao;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;

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
        return (ele) -> Boolean.TRUE;
    }

    /**
     * 校验元素是否允许被删除处理器.<br>
     *
     * @return Predicate&lt;T&gt;
     */
    default BiPredicate<D, T> deleteBiTest() {
        return (args, ele) -> this.deleteTest().test(ele);
    }

    /**
     * 软删除执行处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<T> deleteProcessor() {
        return (ele) -> {
            if (ele instanceof BaseEntity<?>) {
                BaseEntity<?> e = BaseEntity.class.cast(ele);
                e.setDeleted(true);
                e.setDeleteTime(new Date());
            }
            if (ele instanceof BaseTSTypeEntity<?>) {
                BaseTSTypeEntity<?> e = BaseTSTypeEntity.class.cast(ele);
                e.setDeleted(true);
                e.setDeleteTime(LocalDateTime.now());
            }
        };
    }

    /**
     * 软删除执行处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default BiConsumer<D, T> deleteBiProcessor() {
        return (args, ele) -> this.deleteProcessor().accept(ele);
    }

    /**
     * 元素删除后置处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<T> deletePostprocessor() {
        return (ele) -> {
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
    default BiConsumer<D, T> deleteBiPostprocessor() {
        return (args, ele) -> this.deletePostprocessor().accept(ele);
    }

    /**
     * 元素删除后置处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<Collection<T>> deleteAllPostprocessor() {
        return (eles) -> {
            if (Objects.nonNull(eles) && !eles.isEmpty()) {
                eles.forEach(it -> this.deletePostprocessor().accept(it));
            }
        };
    }

    /**
     * 元素删除后置处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default BiConsumer<D, Collection<T>> deleteAllBiPostprocessor() {
        return (args, eles) -> this.deleteAllPostprocessor().accept(eles);
    }

    /**
     * 删除查询固定条件, 不受注解影响.<br>
     *
     * @return Specification&lt;T&gt;
     * @since 1.0.2
     */
    default Specification<T> deleteSelectFixedPredicate() {
        return (r, q, c) -> null;
    }

    /**
     * 删除获取查询条件.<br>
     *
     * @param args 查询实体
     * @return Specification&lt;T&gt;
     */
    default Specification<T> deleteSelectQuery(D args) {
        if (Objects.isNull(args)) {
            return this.deleteSelectFixedPredicate();
        }
        final List<javax.persistence.criteria.Predicate> list = new ArrayList<>();
        return (root, query, cb) -> {
            if (Objects.nonNull(args.getId())) {
                list.add(cb.equal(root.get(root.getModel().getId(root.getModel().getIdType().getJavaType())), args.getId()));
            }
            final AnnotationCollection computer = CollectionCache.getAnnotationCollection(args.getClass());
            if (!computer.isEmpty(BuilderType.SELECTED)) {
                javax.persistence.criteria.Predicate selectPredicate = computer.toComputerCollection().withArgs(args).withSpecification(root, query, cb)
                    .build(BuilderType.SELECTED).getPredicate(cb);
                if (Objects.nonNull(selectPredicate)) {
                    list.add(selectPredicate);
                }
            }
            javax.persistence.criteria.Predicate fixedPredicate = Optional.ofNullable(this.deleteSelectFixedPredicate()).orElseGet(() -> (r, q, c) -> null)
                .toPredicate(root, query, cb);
            if (Objects.nonNull(fixedPredicate)) {
                list.add(fixedPredicate);
            }
            return cb.and(list.toArray(new javax.persistence.criteria.Predicate[list.size()]));
        };
    }

    /**
     * 删除逻辑.<br>
     *
     * @param args 删除实体实例
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default T delete(D args) {
        Specification<T> spec = this.deleteSelectQuery(args);
        return Objects.nonNull(spec)
            ? this.deleteBySpec(spec, (ele) -> this.deleteBiTest().test(args, ele), (ele) -> this.deleteBiProcessor().accept(args, ele),
                (ele) -> this.deleteBiPostprocessor().accept(args, ele))
            : this.deleteById(args.getId(), (ele) -> this.deleteBiTest().test(args, ele), (ele) -> this.deleteBiProcessor().accept(args, ele),
                (ele) -> this.deleteBiPostprocessor().accept(args, ele));
    }

    /**
     * 删除逻辑.<br>
     *
     * @param id 删除实体主键ID
     * @see #deleteById(Serializable, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
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
    @Transactional(rollbackFor = { Exception.class, Error.class })
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
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default T deleteById(ID id, Predicate<T> filter, Consumer<T> processor) {
        return this.deleteById(id, filter, processor, this.deletePostprocessor());
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
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default T deleteById(ID id, Predicate<T> filter, Consumer<T> processor, Consumer<T> postprocessor) {
        Optional<T> eleOptional = this.getDao().findById(id).filter(filter::test);
        if (!eleOptional.isPresent()) {
            throw this.deleteNonDataException();
        }
        T ele = eleOptional.get();
        switch (this.deleteType()) {
            case HARD:
                this.getDao().delete(ele);
                break;
            case SOFT:
                if (Objects.nonNull(processor)) {
                    processor.accept(ele);
                }
                ele = this.getDao().saveAndFlush(ele);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + this.deleteType());
        }
        if (Objects.nonNull(postprocessor)) {
            postprocessor.accept(ele);
        }
        return ele;
    }

    /**
     * 删除逻辑.<br>
     *
     * @param spec 筛选条件
     * @see #deleteBySpec(Specification, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default T deleteBySpec(Specification<T> spec) {
        return this.deleteBySpec(spec, this.deleteTest());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param spec   筛选条件
     * @param filter 校验元素是否允许被删除处理器
     * @see #deleteBySpec(Specificatio, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default T deleteBySpec(Specification<T> spec, Predicate<T> filter) {
        return this.deleteBySpec(spec, filter, this.deleteProcessor());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param spec      筛选条件
     * @param filter    校验元素是否允许被删除处理器
     * @param processor 删除处理器
     * @see #deleteBySpec(Specificatio, Predicate, Consumer, Consumer)
     * @return T
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default T deleteBySpec(Specification<T> spec, Predicate<T> filter, Consumer<T> processor) {
        return this.deleteBySpec(spec, filter, processor, this.deletePostprocessor());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param spec          筛选条件
     * @param filter        校验元素是否允许被删除处理器
     * @param processor     删除处理器
     * @param postprocessor 删除后置处理器
     * @return T
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default T deleteBySpec(Specification<T> spec, Predicate<T> filter, Consumer<T> processor, Consumer<T> postprocessor) {
        Optional<T> eleOptional = this.getDao().findOne(spec).filter(filter::test);
        if (!eleOptional.isPresent()) {
            throw this.deleteNonDataException();
        }
        T ele = eleOptional.get();
        switch (this.deleteType()) {
            case HARD:
                this.getDao().delete(ele);
                break;
            case SOFT:
                if (Objects.nonNull(processor)) {
                    processor.accept(ele);
                }
                ele = this.getDao().saveAndFlush(ele);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + this.deleteType());
        }
        if (Objects.nonNull(postprocessor)) {
            postprocessor.accept(ele);
        }
        return ele;
    }

    /**
     * 删除逻辑.<br>
     *
     * @param args 删除实体实例
     * @see #deleteAllByIds(Collection)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default List<T> deleteAll(Collection<D> args) {
        if (Objects.isNull(args) || args.isEmpty()) {
            return new ArrayList<>();
        }
        return this.deleteAllByIds(args.stream().map(IdEntity::getId).collect(Collectors.toSet()));
    }

    /**
     * 删除逻辑.<br>
     *
     * @param ids 删除实体主键ID
     * @see #deleteAllByIds(Collection, Predicate)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default List<T> deleteAllByIds(Collection<ID> ids) {
        return this.deleteAllByIds(ids, this.deleteTest());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param ids    删除实体主键ID
     * @param filter 校验元素是否允许被删除处理器
     * @see #deleteAllByIds(Collection, Predicate, Consumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
    default List<T> deleteAllByIds(Collection<ID> ids, Predicate<T> filter) {
        return this.deleteAllByIds(ids, filter, this.deleteProcessor());
    }

    /**
     * 删除逻辑.<br>
     *
     * @param ids       删除实体主键ID
     * @param filter    校验元素是否允许被删除处理器
     * @param processor 删除处理器
     * @see #deleteAllByIds(Collection, Predicate, Consumer, Consumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackFor = { Exception.class, Error.class })
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
    @Transactional(rollbackFor = { Exception.class, Error.class })
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
                ele = this.getDao().saveAllAndFlush(ele);
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
