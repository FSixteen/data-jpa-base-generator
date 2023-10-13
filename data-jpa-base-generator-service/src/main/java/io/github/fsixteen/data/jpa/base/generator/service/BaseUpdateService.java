package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.exception.AccessDeniedException;
import io.github.fsixteen.data.jpa.base.generator.exception.DataExistedException;
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
public interface BaseUpdateService<T extends IdEntity<ID>, ID extends Serializable, U extends IdEntity<ID>> {

    static final Logger log = LoggerFactory.getLogger(BaseUpdateService.class);

    public BaseDao<T, ID> getDao();

    /**
     * 元素不存在时提示内容.<br>
     *
     * @return StatusInterface
     */
    default StatusInterface updateNonDataExceptionStatus() {
        return Status.NONDATA_ERROR.get();
    }

    /**
     * 元素不存在时提示内容.<br>
     *
     * @return String
     */
    default String updateNonDataExceptionMessage() {
        return this.updateNonDataExceptionStatus().msg();
    }

    /**
     * 元素不存在时提示内容.<br>
     *
     * @return RuntimeException
     */
    default RuntimeException updateNonDataException() {
        return new DataNonExistException(this.updateNonDataExceptionStatus().code(), this.updateNonDataExceptionMessage());
    }

    /**
     * 状态锁定, 暂无权限提示内容.<br>
     *
     * @return String
     */
    default String accessDeniedExceptionMessage() {
        return "状态锁定, 暂无权限.";
    }

    /**
     * 元素已存在时提示内容(内容被占用等).<br>
     *
     * @return StatusInterface
     */
    default StatusInterface updateExistedExceptionStatus() {
        return Status.EXISTED_ERROR.get();
    }

    /**
     * 元素已存在时提示内容(内容被占用等).<br>
     *
     * @return String
     */
    default String updateExistedExceptionMessage() {
        return updateExistedExceptionStatus().msg();
    }

    /**
     * 元素已存在时提示内容(内容被占用等).<br>
     *
     * @return RuntimeException
     */
    default RuntimeException updateExistedException() {
        return new DataExistedException(this.updateExistedExceptionStatus().code(), this.updateExistedExceptionMessage());
    }

    /**
     * 指定校验元素是否不包含主键ID.<br>
     *
     * @return boolean
     */
    default boolean checkExistedWithNotEqualToId() {
        return Boolean.TRUE;
    }

    /**
     * 校验元素是否允许被更新处理器.<br>
     *
     * @return BiPredicate&lt;U, BaseDao&lt;T, ID&gt;&gt;
     */
    default BiPredicate<U, BaseDao<T, ID>> checkExistedBeforUpdate() {
        return (args, dao) -> {
            AnnotationCollection computer = CollectionCache.getAnnotationCollection(args.getClass());
            if (!computer.isEmpty(BuilderType.EXISTS)) {
                return dao.exists((root, query, cb) -> {
                    List<javax.persistence.criteria.Predicate> list = new ArrayList<>();
                    if (this.checkExistedWithNotEqualToId()) {
                        list.add(cb.notEqual(root.get(root.getModel().getId(root.getModel().getIdType().getJavaType())), args.getId()));
                    }
                    list.add(computer.toComputerCollection().withArgs(args).withSpecification(root, query, cb).build(BuilderType.EXISTS).getPredicate(cb));
                    return cb.and(list.toArray(new javax.persistence.criteria.Predicate[list.size()]));
                });
            } else {
                return Boolean.FALSE;
            }
        };
    }

    /**
     * 更新前是否进行校验.<br>
     * 
     * @return boolean
     */
    default boolean isCheckExistedBeforUpdate() {
        return Boolean.TRUE;
    }

    /**
     * 更新前是否进行校验.<br>
     * 
     * @return boolean
     */
    default boolean isCheckExistedBeforUpdateOne() {
        return this.isCheckExistedBeforUpdate();
    }

    /**
     * 更新插入前是否进行校验.<br>
     * 
     * @return boolean
     */
    default boolean isCheckExistedBeforUpdateAll() {
        return this.isCheckExistedBeforUpdate();
    }

    /**
     * 更新前置处理器.<br>
     *
     * @return Consumer&lt;U&gt;
     */
    default Consumer<U> updatePreprocessor() {
        return (args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Update Pre Processor Nothing.");
            }
        };
    }

    /**
     * 更新判断器.<br>
     *
     * @return Predicate&lt;T&gt;
     */
    default Predicate<T> updateTest() {
        return (args) -> Boolean.TRUE;
    }

    /**
     * 更新判断器.<br>
     *
     * @return BiPredicate&lt;T, U&gt;
     */
    default BiPredicate<T, U> updateBiTest() {
        return (ele, args) -> this.updateTest().test(ele);
    }

    /**
     * 更新处理器.<br>
     *
     * @return Consumer&lt;T&gt;
     */
    default Consumer<T> updateProcessor() {
        return (ele) -> {
            if (log.isDebugEnabled()) {
                log.debug("Update Processor Nothing.");
            }
        };
    }

    /**
     * 更新处理器.<br>
     *
     * @return BiConsumer&lt;T, U&gt;
     */
    default BiConsumer<T, U> updateBiProcessor() {
        return (ele, args) -> this.updateProcessor().accept(ele);
    }

    /**
     * 更新后置处理器.<br>
     *
     * @return BiConsumer&lt;T, U&gt;
     */
    default BiConsumer<T, U> updatePostprocessor() {
        return (ele, args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Update Post Processor Nothing.");
            }
        };
    }

    /**
     * 更新后置处理器.<br>
     *
     * @return BiConsumer&lt;Iterable&lt;T&gt, Iterable&lt;U&gt&gt;
     */
    default BiConsumer<Iterable<T>, Iterable<U>> updateAllPostprocessor() {
        return (ele, args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Insert Post Processor Nothing.");
            }
        };
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args 更新实体实例
     * @see #update(IdEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T update(U args) {
        return update(args, this.updatePreprocessor());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args         更新实体实例
     * @param preprocessor 更新前置处理器
     * @see #update(IdEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T update(U args, Consumer<U> preprocessor) {
        return update(args, preprocessor, this.updateBiTest());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args         更新实体实例
     * @param preprocessor 更新前置处理器
     * @param filter       更新判断器
     * @see #update(IdEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T update(U args, Consumer<U> preprocessor, BiPredicate<T, U> filter) {
        return update(args, preprocessor, filter, this.updateBiProcessor());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args      更新实体实例
     * @param filter    更新判断器
     * @param processor 更新处理器
     * @see #update(IdEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T update(U args, BiPredicate<T, U> filter, BiConsumer<T, U> processor) {
        return update(args, this.updatePreprocessor(), filter, processor, this.updatePostprocessor());
    }

    /**
     * 更新逻辑.<br>
     *
     * @param args      更新实体实例
     * @param filter    更新判断器
     * @param processor 更新处理器
     * @see #update(IdEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T update(U args, BiPredicate<T, U> filter, Consumer<T> processor) {
        return update(args, this.updatePreprocessor(), filter, (ele, temp) -> processor.accept(ele));
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args         更新实体实例
     * @param preprocessor 更新前置处理器
     * @param filter       更新判断器
     * @param processor    更新处理器
     * @see #update(IdEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T update(U args, Consumer<U> preprocessor, BiPredicate<T, U> filter, BiConsumer<T, U> processor) {
        return update(args, preprocessor, filter, processor, this.updatePostprocessor());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args          更新实体实例
     * @param preprocessor  更新前置处理器
     * @param filter        更新判断器
     * @param processor     更新处理器
     * @param postprocessor 更新后置处理器
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T update(U args, Consumer<U> preprocessor, BiPredicate<T, U> filter, BiConsumer<T, U> processor, BiConsumer<T, U> postprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> it.accept(args));
        if (this.isCheckExistedBeforUpdateOne() && this.checkExistedBeforUpdate().test(args, this.getDao())) {
            throw this.updateExistedException();
        }
        Optional<T> eleOptional = this.getDao().findById(args.getId());
        if (!eleOptional.isPresent()) {
            throw this.updateNonDataException();
        }
        T ele = eleOptional.filter(it -> filter.test(it, args)).orElseThrow(() -> new AccessDeniedException(this.accessDeniedExceptionMessage()));
        String[] ignoreSet = BaseCommonService.jsonIgnoreProperties(args);
        BeanUtils.copyProperties(args, ele, ignoreSet);
        Optional.ofNullable(processor).ifPresent(it -> it.accept(ele, args));
        T savedEle = this.getDao().saveAndFlush(ele);
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(savedEle, args));
        return savedEle;
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args 更新实体实例
     * @see #updateAll(List, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> updateAll(List<U> args) {
        return updateAll(args, this.updatePreprocessor());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args         更新实体实例
     * @param preprocessor 更新前置处理器
     * @see #updateAll(List, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> updateAll(List<U> args, Consumer<U> preprocessor) {
        return updateAll(args, preprocessor, this.updateBiTest());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args         更新实体实例
     * @param preprocessor 更新前置处理器
     * @param filter       更新判断器
     * @see #updateAll(List, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> updateAll(List<U> args, Consumer<U> preprocessor, BiPredicate<T, U> filter) {
        return updateAll(args, preprocessor, filter, this.updateBiProcessor());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args      更新实体实例
     * @param filter    更新判断器
     * @param processor 更新处理器
     * @see #updateAll(List, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> updateAll(List<U> args, BiPredicate<T, U> filter, BiConsumer<T, U> processor) {
        return updateAll(args, this.updatePreprocessor(), filter, processor, this.updateAllPostprocessor());
    }

    /**
     * 更新逻辑.<br>
     *
     * @param args      更新实体实例
     * @param filter    更新判断器
     * @param processor 更新处理器
     * @see #updateAll(List, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> updateAll(List<U> args, BiPredicate<T, U> filter, Consumer<T> processor) {
        return updateAll(args, this.updatePreprocessor(), filter, (ele, temp) -> processor.accept(ele));
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args         更新实体实例
     * @param preprocessor 更新前置处理器
     * @param filter       更新判断器
     * @param processor    更新处理器
     * @see #updateAll(List, Consumer, BiPredicate, BiConsumer, BiConsumer)
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> updateAll(List<U> args, Consumer<U> preprocessor, BiPredicate<T, U> filter, BiConsumer<T, U> processor) {
        return updateAll(args, preprocessor, filter, processor, this.updateAllPostprocessor());
    }

    /**
     * 更新逻辑.<br>
     * 
     * @param args          更新实体实例
     * @param preprocessor  更新前置处理器
     * @param filter        更新判断器
     * @param processor     更新处理器
     * @param postprocessor 更新后置处理器
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> updateAll(List<U> args, Consumer<U> preprocessor, BiPredicate<T, U> filter, BiConsumer<T, U> processor,
        BiConsumer<Iterable<T>, Iterable<U>> postprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> args.forEach(ele -> it.accept(ele)));
        if (this.isCheckExistedBeforUpdateAll()) {
            args.forEach(ele -> {
                if (this.checkExistedBeforUpdate().test(ele, this.getDao())) {
                    throw this.updateExistedException();
                }
            });
        }
        Map<U, T> eles = new ConcurrentHashMap<>();
        String[] ignoreSet = BaseCommonService.jsonIgnoreProperties(args);
        for (U arg : args) {
            Optional<T> eleOptional = this.getDao().findById(arg.getId());
            if (!eleOptional.isPresent()) {
                throw this.updateNonDataException();
            }
            T ele = eleOptional.filter(it -> filter.test(it, arg)).orElseThrow(() -> new AccessDeniedException(this.accessDeniedExceptionMessage()));
            BeanUtils.copyProperties(arg, ele, ignoreSet);
        }
        Optional.ofNullable(processor).ifPresent(it -> eles.entrySet().forEach(entry -> it.accept(entry.getValue(), entry.getKey())));
        List<T> savedEle = this.getDao().saveAllAndFlush(eles.values());
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(savedEle, args));
        return savedEle;
    }

}
