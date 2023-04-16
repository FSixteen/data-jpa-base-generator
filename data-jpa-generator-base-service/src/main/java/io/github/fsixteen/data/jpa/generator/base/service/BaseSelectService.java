package io.github.fsixteen.data.jpa.generator.base.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.generator.base.entities.Entity;
import io.github.fsixteen.data.jpa.generator.base.entities.IdEntity;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;
import io.github.fsixteen.data.jpa.generator.beans.BasePageRequest;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public interface BaseSelectService<T extends IdEntity<ID>, ID extends Serializable, S extends Entity & BasePageRequest> {

    static final Logger log = LoggerFactory.getLogger(BaseSelectService.class);

    public BaseDao<T, ID> getDao();

    /**
     * 获取查询条件.<br>
     * 
     * @param args 查询实体
     * @return Specification&lt;T&gt;
     */
    default Specification<T> selectQuery(S args) {
        final AnnotationCollection computer = CollectionCache.getAnnotationCollection(args.getClass());
        return !computer.isEmpty(BuilderType.SELECTED)
                ? (root, query, cb) -> computer.toComputerCollection().setArgs(args).setSpecification(root, query, cb).build(BuilderType.SELECTED)
                        .getPredicate(cb)
                : (root, query, cb) -> cb.and();
    }

    /**
     * 查询查询前置处理器.<br>
     *
     * @return Consumer&lt;S&gt;
     */
    default Consumer<S> selectPreprocessor() {
        return (args) -> {
            if (log.isDebugEnabled()) {
                log.debug("Select Pre Processor Nothing.");
            }
        };
    }

    /**
     * 查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> selectSort() {
        return () -> Sort.unsorted();
    }

    /**
     * 查询查询分页信息.<br>
     *
     * @return Function&lt;S,PageRequest&gt;
     */
    default Function<S, PageRequest> selectPageRequest() {
        return (args) -> PageRequest.of(args.getPage(), args.getSize(), this.selectSort().get());
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @see #select(Entity, PageRequest, Consumer)
     * @return Page&lt;T&gt;
     */
    default Page<T> select(S args) {
        return this.select(args, this.selectPageRequest().apply(args));
    }

    /**
     * 信息查询.<br>
     *
     * @param args        查询实体
     * @param pageRequest 查询分页信息
     * @see #select(IdEntity, PageRequest, Consumer)
     * @return Page&lt;T&gt;
     */
    default Page<T> select(S args, PageRequest pageRequest) {
        return this.select(args, pageRequest, this.selectPreprocessor());
    }

    /**
     * 信息查询.<br>
     *
     * @param args         查询实体
     * @param pageRequest  查询分页信息
     * @param preprocessor 查询前置处理器
     * @see #select(Specification, PageRequest)
     * @return Page&lt;T&gt;
     */
    default Page<T> select(S args, PageRequest pageRequest, Consumer<S> preprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> it.accept(args));
        return this.select(this.selectQuery(args), pageRequest);
    }

    /**
     * 信息查询.<br>
     *
     * @param specification 查询条件
     * @see #select(Specification, PageRequest)
     * @return List&lt;T&gt;
     */
    default List<T> select(Specification<T> specification) {
        return this.getDao().findAll(specification, this.selectAllSort().get());
    }

    /**
     * 信息查询.<br>
     *
     * @param specification 查询条件
     * @param pageRequest   查询分页信息
     * @return Page&lt;T&gt;
     */
    default Page<T> select(Specification<T> specification, PageRequest pageRequest) {
        return this.getDao().findAll(specification, pageRequest);
    }

    /**
     * 全部查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> selectAllSort() {
        return () -> Sort.unsorted();
    }

    /**
     * 全部查询.<br>
     *
     * @return List&lt;T&gt;
     */
    default List<T> selectAll() {
        return this.getDao().findAll(this.selectAllSort().get());
    }

}
