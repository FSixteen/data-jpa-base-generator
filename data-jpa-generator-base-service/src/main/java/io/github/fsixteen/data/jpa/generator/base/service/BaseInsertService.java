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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import io.github.fsixteen.common.structure.extend.Status;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.ComputerCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;
import io.github.fsixteen.data.jpa.generator.exception.DataExistedException;
import io.github.fsixteen.data.jpa.generator.exception.ReflectionException;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseInsertService<T extends IdEntity<ID>, ID extends Serializable, I extends Entity> {
    static final Logger log = LoggerFactory.getLogger(BaseInsertService.class);

    public BaseDao<T, ID> getDao();

    default String defaultInsertExistedExceptionMessage() {
        return Status.EXISTED_ERROR.get().msg();
    }

    default RuntimeException defaultInsertExistedException() {
        return new DataExistedException(Status.EXISTED_ERROR.get().code(), this.defaultInsertExistedExceptionMessage());
    }

    default Specification<T> isExisted(I args) {
        final AnnotationCollection computer = CollectionCache.getAnnotationCollection(args.getClass());
        return !computer.isEmpty(BuilderType.EXISTS) ? (root, query, cb) -> {
            ComputerCollection cc = ComputerCollection.Builder.of().setAc(computer).setArgs(args).setSpecification(root, query, cb).build(BuilderType.EXISTS);
            Predicate[] eles = cc.getPredicateArray();
            return cb.and(eles);
        } : (root, query, cb) -> cb.equal(cb.literal(0), cb.literal(1));
    }

    /**
     * 添加预处理器.
     *
     * @param args
     */
    default Consumer<I> insertPreprocessor() {
        return (args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Insert Processor Nothing.");
            }
        };
    }

    /**
     * 添加处理器.
     *
     * @param args
     */
    default Consumer<T> insertProcessor() {
        return (ele) -> {
            // Nothing.
            if (log.isDebugEnabled()) {
                log.debug("Insert Processor Nothing.");
            }
        };
    }

    /**
     * 添加处理器.
     *
     * @param args
     */
    default BiConsumer<T, I> insertBiProcessor() {
        return (ele, args) -> this.insertProcessor().accept(ele);
    }

    /**
     * 添加后处理器.
     *
     * @param args
     */
    default BiConsumer<T, I> insertPostprocessor() {
        return (ele, args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Insert Post Processor Nothing.");
            }
        };
    }

    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T insert(I args) {
        return insert(args, this.insertPreprocessor());
    }

    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T insert(I args, Consumer<I> preprocessor) {
        return insert(args, preprocessor, this.insertBiProcessor());
    }

    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T insert(I args, Consumer<I> preprocessor, BiConsumer<T, I> processor) {
        return insert(args, preprocessor, processor, this.insertPostprocessor());
    }

    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    @SuppressWarnings("unchecked")
    default T insert(I args, Consumer<I> preprocessor, BiConsumer<T, I> processor, BiConsumer<T, I> postprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> it.accept(args));
        if (this.getDao().exists(this.isExisted(args))) {
            throw this.defaultInsertExistedException();
        }
        try {
            T news = (T) BaseCommonService.getTableClass(this.getDao().getClass()).getDeclaredConstructor().newInstance();
            Set<String> ignoreSet = new HashSet<>();
            JsonIgnoreProperties ignoreProperties = args.getClass().getAnnotation(JsonIgnoreProperties.class);
            if (Objects.nonNull(ignoreProperties) && 0 < ignoreProperties.value().length) {
                ignoreSet.addAll(Arrays.asList(ignoreProperties.value()));
            }
            JsonIncludeProperties includeProperties = args.getClass().getAnnotation(JsonIncludeProperties.class);
            if (Objects.nonNull(includeProperties) && 0 < includeProperties.value().length) {
                List<String> includes = Arrays.asList(includeProperties.value());
                Set<String> fields = Stream.of(io.github.fsixteen.data.jpa.generator.utils.BeanUtils.getAllFields(args.getClass()))
                        .filter(it -> Modifier.isStatic(it.getModifiers())).map(Field::getName).filter(it -> !includes.contains(it))
                        .collect(Collectors.toSet());
                ignoreSet.addAll(fields);
            }
            BeanUtils.copyProperties(args, news, ignoreSet.toArray(new String[ignoreSet.size()]));
            Optional.ofNullable(processor).ifPresent(it -> it.accept(news, args));
            T savedEle = this.getDao().save(news);
            Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(savedEle, args));
            return savedEle;
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            throw new ReflectionException(e.getMessage());
        }
    }
}
