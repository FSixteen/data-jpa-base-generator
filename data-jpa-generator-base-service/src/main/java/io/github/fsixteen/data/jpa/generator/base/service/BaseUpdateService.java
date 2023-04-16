package io.github.fsixteen.data.jpa.generator.base.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;
import io.github.fsixteen.data.jpa.generator.base.entities.BaseEntity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;
import io.github.fsixteen.data.jpa.generator.exception.AccessDeniedException;
import io.github.fsixteen.data.jpa.generator.exception.DataExistedException;
import io.github.fsixteen.data.jpa.generator.exception.DataNonExistException;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since V1.0.0
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
     * 校验元素是否允许被更新处理器.<br>
     *
     * @return BiPredicate&lt;U, BaseDao&lt;T, ID&gt;&gt;
     */
    default BiPredicate<U, BaseDao<T, ID>> updateExisted() {
        return (args, dao) -> Boolean.FALSE;
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
     * 更新逻辑.<br>
     * 
     * @param args 更新实体实例
     * @see #update(BaseEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
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
     * @see #update(BaseEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
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
     * @see #update(BaseEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
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
     * @see #update(BaseEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
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
     * @see #update(BaseEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
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
     * @see #update(BaseEntity, Consumer, BiPredicate, BiConsumer, BiConsumer)
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
        if (this.updateExisted().test(args, this.getDao())) {
            throw this.updateExistedException();
        }
        Optional<T> eleOptional = this.getDao().findById(args.getId());
        if (!eleOptional.isPresent()) {
            throw this.updateNonDataException();
        }
        T ele = eleOptional.filter(it -> filter.test(it, args)).orElseThrow(() -> new AccessDeniedException(this.accessDeniedExceptionMessage()));
        Set<String> ignoreSet = new HashSet<>();
        JsonIgnoreProperties ignoreProperties = args.getClass().getAnnotation(JsonIgnoreProperties.class);
        if (Objects.nonNull(ignoreProperties) && 0 < ignoreProperties.value().length) {
            ignoreSet.addAll(Arrays.asList(ignoreProperties.value()));
        }
        JsonIncludeProperties includeProperties = args.getClass().getAnnotation(JsonIncludeProperties.class);
        if (Objects.nonNull(includeProperties) && 0 < includeProperties.value().length) {
            List<String> includes = Arrays.asList(includeProperties.value());
            Set<String> fields = Stream.of(io.github.fsixteen.data.jpa.base.generator.plugins.utils.BeanUtils.getAllFields(args.getClass()))
                    .filter(it -> Modifier.isStatic(it.getModifiers())).map(Field::getName).filter(it -> !includes.contains(it)).collect(Collectors.toSet());
            ignoreSet.addAll(fields);
        }
        BeanUtils.copyProperties(args, ele, ignoreSet.toArray(new String[ignoreSet.size()]));
        Optional.ofNullable(processor).ifPresent(it -> it.accept(ele, args));
        T savedEle = this.getDao().save(ele);
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(savedEle, args));
        return savedEle;
    }

}
