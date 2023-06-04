package io.github.fsixteen.data.jpa.generator.base.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;

import io.github.fsixteen.common.structure.StatusInterface;
import io.github.fsixteen.common.structure.extend.Status;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
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
 * @since 1.0.0
 */
public interface BaseInsertService<T extends IdEntity<ID>, ID extends Serializable, I extends Entity> {

    static final Logger log = LoggerFactory.getLogger(BaseInsertService.class);

    public BaseDao<T, ID> getDao();

    /**
     * 元素存在时提示内容.<br>
     *
     * @return StatusInterface
     */
    default StatusInterface insertExistedExceptionStatus() {
        return Status.EXISTED_ERROR.get();
    }

    /**
     * 元素存在时提示内容.<br>
     *
     * @return String
     */
    default String insertExistedExceptionMessage() {
        return this.insertExistedExceptionStatus().msg();
    }

    /**
     * 元素存在时提示内容.<br>
     * 
     * @return RuntimeException
     */
    default RuntimeException insertExistedException() {
        return new DataExistedException(this.insertExistedExceptionStatus().code(), this.insertExistedExceptionMessage());
    }

    /**
     * 判断实例存在逻辑.<br>
     * 
     * @return BiPredicate&lt;U, BaseDao&lt;T, ID&gt;&gt;
     */
    default BiPredicate<I, BaseDao<T, ID>> checkExistedBeforInsert() {
        return (args, dao) -> {
            AnnotationCollection computer = CollectionCache.getAnnotationCollection(args.getClass());
            Specification<
                T> specification = !computer.isEmpty(BuilderType.EXISTS)
                    ? (root, query, cb) -> computer.toComputerCollection().withArgs(args).withSpecification(root, query, cb).build(BuilderType.EXISTS)
                        .getPredicate(cb)
                    : (root, query, cb) -> cb.equal(cb.literal(0), cb.literal(1));
            return dao.exists(specification);
        };
    }

    /**
     * 插入前是否进行校验.<br>
     * 
     * @return boolean
     */
    default boolean isCheckExistedBeforInsert() {
        return Boolean.TRUE;
    }

    /**
     * 插入前是否进行校验.<br>
     * 
     * @return boolean
     */
    default boolean isCheckExistedBeforInsertOne() {
        return this.isCheckExistedBeforInsert();
    }

    /**
     * 批量插入前是否进行校验.<br>
     * 
     * @return boolean
     */
    default boolean isCheckExistedBeforInsertAll() {
        return this.isCheckExistedBeforInsert();
    }

    /**
     * 添加前置处理器.<br>
     * 
     * @return Consumer&lt;I&gt;
     */
    default Consumer<I> insertPreprocessor() {
        return (args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Insert Processor Nothing.");
            }
        };
    }

    /**
     * 添加处理器.<br>
     *
     * @return Consumer&lt;T&gt;
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
     * 添加处理器.<br>
     *
     * @return BiConsumer&lt;T, I&gt;
     */
    default BiConsumer<T, I> insertBiProcessor() {
        return (ele, args) -> this.insertProcessor().accept(ele);
    }

    /**
     * 添加后置处理器.<br>
     *
     * @return BiConsumer&lt;T, I&gt;
     */
    default BiConsumer<T, I> insertPostprocessor() {
        return (ele, args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Insert Post Processor Nothing.");
            }
        };
    }

    /**
     * 添加后置处理器.<br>
     *
     * @return BiConsumer&lt;Iterable&lt;T&gt, Iterable&lt;I&gt&gt;
     */
    default BiConsumer<Iterable<T>, Iterable<I>> insertAllPostprocessor() {
        return (ele, args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Insert Post Processor Nothing.");
            }
        };
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args 添加实体实例
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T insert(I args) {
        return insert(args, this.insertPreprocessor());
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args         添加实体实例
     * @param preprocessor 添加前置处理器
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T insert(I args, Consumer<I> preprocessor) {
        return insert(args, preprocessor, this.insertBiProcessor());
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args         添加实体实例
     * @param preprocessor 添加前置处理器
     * @param processor    添加处理器
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default T insert(I args, Consumer<I> preprocessor, BiConsumer<T, I> processor) {
        return insert(args, preprocessor, processor, this.insertPostprocessor());
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args          添加实体实例
     * @param preprocessor  添加前置处理器
     * @param processor     添加处理器
     * @param postprocessor 添加后置处理器
     * @return T
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    @SuppressWarnings("unchecked")
    default T insert(I args, Consumer<I> preprocessor, BiConsumer<T, I> processor, BiConsumer<T, I> postprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> it.accept(args));
        if (this.isCheckExistedBeforInsertOne() && this.checkExistedBeforInsert().test(args, this.getDao())) {
            throw this.insertExistedException();
        }
        try {
            T news = (T) BaseCommonService.getTableClass(this.getDao().getClass()).getDeclaredConstructor().newInstance();
            String[] ignoreSet = BaseCommonService.jsonIgnoreProperties(args);
            BeanUtils.copyProperties(args, news, ignoreSet);
            Optional.ofNullable(processor).ifPresent(it -> it.accept(news, args));
            T savedEle = this.getDao().save(news);
            Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(savedEle, args));
            return savedEle;
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            throw new ReflectionException(e.getMessage());
        }
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args 添加实体实例
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> insertAll(Iterable<I> args) {
        return insertAll(args, this.insertPreprocessor());
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args         添加实体实例
     * @param preprocessor 添加前置处理器
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> insertAll(Iterable<I> args, Consumer<I> preprocessor) {
        return insertAll(args, preprocessor, this.insertBiProcessor());
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args         添加实体实例
     * @param preprocessor 添加前置处理器
     * @param processor    添加处理器
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    default List<T> insertAll(Iterable<I> args, Consumer<I> preprocessor, BiConsumer<T, I> processor) {
        return insertAll(args, preprocessor, processor, this.insertAllPostprocessor());
    }

    /**
     * 添加逻辑.<br>
     * 
     * @param args          添加实体实例
     * @param preprocessor  添加前置处理器
     * @param processor     添加处理器
     * @param postprocessor 添加后置处理器
     * @return List&lt;T&gt;
     */
    @Transactional(rollbackOn = { RuntimeException.class, Exception.class })
    @SuppressWarnings("unchecked")
    default List<T> insertAll(Iterable<I> args, Consumer<I> preprocessor, BiConsumer<T, I> processor, BiConsumer<Iterable<T>, Iterable<I>> postprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> args.forEach(ele -> it.accept(ele)));
        if (this.isCheckExistedBeforInsertAll()) {
            args.forEach(ele -> {
                if (this.checkExistedBeforInsert().test(ele, this.getDao())) {
                    throw this.insertExistedException();
                }
            });
        }
        try {
            Map<I, T> eles = new ConcurrentHashMap<>();
            String[] ignoreSet = BaseCommonService.jsonIgnoreProperties(args);
            for (I arg : args) {
                T news = (T) BaseCommonService.getTableClass(this.getDao().getClass()).getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(arg, news, ignoreSet);
                eles.put(arg, news);
            }
            Optional.ofNullable(processor).ifPresent(it -> eles.entrySet().forEach(entry -> it.accept(entry.getValue(), entry.getKey())));
            List<T> savedEle = this.getDao().saveAllAndFlush(eles.values());
            Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(savedEle, args));
            return savedEle;
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            throw new ReflectionException(e.getMessage());
        }
    }

}
