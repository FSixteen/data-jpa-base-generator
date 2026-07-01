package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import io.github.fsixteen.data.jpa.base.generator.config.DataJpaGeneratorConfig;
import io.github.fsixteen.data.jpa.base.generator.entities.Entity;
import io.github.fsixteen.data.jpa.base.generator.entities.IdEntity;
import io.github.fsixteen.data.jpa.base.generator.jpa.BaseDao;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledPredicateFacade;
import io.github.fsixteen.data.jpa.base.generator.query.BasePageRequest;
import io.github.fsixteen.data.jpa.base.generator.query.DefaultPageRequest;
import io.github.fsixteen.data.jpa.base.generator.utils.AppContextInitializer;

/**
 * 通用Service处理类.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public interface BaseSelectService<T extends IdEntity<ID>, ID extends Serializable, S extends Entity & BasePageRequest> {

    static final Logger log = LoggerFactory.getLogger(BaseSelectService.class);

    Container<Supplier<Sort>> DEFAULT_SORT_COLUMNS = new Container<>(() -> Sort.unsorted());

    Container<Supplier<Sort>> CUSTOM_SORT_COLUMNS = new Container<>();

    /**
     * 初始化排序规则.
     */
    @PostConstruct
    default void init() {
        DataJpaGeneratorConfig config = AppContextInitializer.getBean(DataJpaGeneratorConfig.class, false);
        if (Objects.nonNull(config)) {
            if (Objects.nonNull(config.getDefaultSortColumns())) {
                CUSTOM_SORT_COLUMNS.setSort(config.getDefaultSortColumns());
            }
        } else {
            log.debug("DataJpaGeneratorConfig is not initialized.");
        }
    }

    public BaseDao<T, ID> getDao();

    /**
     * 查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> defaultSort() {
        return CUSTOM_SORT_COLUMNS.isEmpty() ? DEFAULT_SORT_COLUMNS.getSort() : CUSTOM_SORT_COLUMNS.getSort();
    }

    /**
     * 查询固定条件, 不受注解影响.<br>
     *
     * @return Specification&lt;T&gt;
     * @since 1.0.1
     */
    default Specification<T> selectFixedPredicate() {
        return (root, query, criteriaBuilder) -> null;
    }

    /**
     * 获取查询条件.<br>
     *
     * @param args 查询实体
     * @return Specification&lt;T&gt;
     */
    default Specification<T> selectQuery(Entity args) {
        if (Objects.isNull(args)) {
            return this.selectFixedPredicate();
        }
        final AnnotationCollection computer = CollectionCache.getAnnotationCollection(args.getClass());
        if (computer.isSelectionEmpty()) {
            return this.selectFixedPredicate();
        }
        return (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (!computer.isSelectionEmpty()) {
                Predicate selectPredicate = CompiledPredicateFacade.selectionPredicate(computer, args, root, query, cb);
                if (Objects.nonNull(selectPredicate)) {
                    list.add(selectPredicate);
                }
            }
            Predicate fixedPredicate = this.selectFixedPredicate().toPredicate(root, query, cb);
            if (Objects.nonNull(fixedPredicate)) {
                list.add(fixedPredicate);
            }
            return cb.and(list.toArray(new Predicate[list.size()]));
        };
    }

    // *************** find xxx *************** //

    /**
     * 查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> findSort() {
        return this.defaultSort();
    }

    /**
     * 查询查询分页信息.<br>
     *
     * @return Function&lt;S,PageRequest&gt;
     */
    default Function<BasePageRequest, PageRequest> findPageRequest() {
        return (args) -> PageRequest.of(args.getPage(), args.getSize(), this.findSort().get());
    }

    /**
     * 信息查询.<br>
     *
     * @param id 查询实体主键
     * @return Optional&lt;T&gt;
     */
    default Optional<T> findById(ID id) {
        return this.getDao().findById(id);
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @return Optional&lt;T&gt;
     */
    default Optional<T> findOne(Entity args) {
        List<T> eles = this.getDao().findAll(this.selectQuery(args), PageRequest.of(0, 1, this.findSort().get())).getContent();
        return 0 < eles.size() ? Optional.ofNullable(eles.get(0)) : Optional.empty();
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @param sort 排序规则
     * @return Optional&lt;T&gt;
     */
    default Optional<T> findOne(Entity args, Sort sort) {
        List<T> eles = this.getDao().findAll(this.selectQuery(args), PageRequest.of(0, 1, sort)).getContent();
        return 0 < eles.size() ? Optional.ofNullable(eles.get(0)) : Optional.empty();
    }

    /**
     * 信息查询.<br>
     *
     * @param spec 查询条件
     * @return Optional&lt;T&gt;
     */
    default Optional<T> findOne(@Nullable Specification<T> spec) {
        return this.getDao().findOne(spec);
    }

    /**
     * 信息查询.<br>
     *
     * @return List&lt;T&gt;
     */
    default List<T> findAll() {
        return this.getDao().findAll(this.findSort().get());
    }

    /**
     * 信息查询.<br>
     *
     * @param sort 排序规则
     * @return List&lt;T&gt;
     */
    default List<T> findAll(Sort sort) {
        return this.getDao().findAll(sort);
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @return List&lt;T&gt;
     */
    default List<T> findAll(Entity args) {
        return this.getDao().findAll(this.selectQuery(args), this.findSort().get());
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @param sort 排序规则
     * @return List&lt;T&gt;
     */
    default List<T> findAll(Entity args, Sort sort) {
        return this.getDao().findAll(this.selectQuery(args), sort);
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @return Page&lt;T&gt;
     */
    default Page<T> findAll(BasePageRequest args) {
        return this.getDao().findAll(this.findPageRequest().apply(args));
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @param sort 排序规则
     * @return Page&lt;T&gt;
     */
    default Page<T> findAll(BasePageRequest args, Sort sort) {
        return this.getDao().findAll(PageRequest.of(args.getPage(), args.getSize(), sort));
    }

    /**
     * 信息查询.<br>
     *
     * @param args 查询实体
     * @return Page&lt;T&gt;
     */
    default Page<T> findAll(DefaultPageRequest args) {
        return this.getDao().findAll(this.selectQuery(args), this.findPageRequest().apply(args));
    }

    /**
     * 信息查询.<br>
     *
     * @param spec 查询条件
     * @return List&lt;T&gt;
     */
    default List<T> findAll(@Nullable Specification<T> spec) {
        return this.getDao().findAll(spec, this.findSort().get());
    }

    /**
     * 信息查询.<br>
     *
     * @param spec     查询条件
     * @param pageable 分页信息
     * @return List&lt;T&gt;
     */
    default Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable) {
        return this.getDao().findAll(spec, pageable);
    }

    /**
     * 信息查询.<br>
     *
     * @param spec 查询条件
     * @param sort 排序规则
     * @return List&lt;T&gt;
     */
    default List<T> findAll(@Nullable Specification<T> spec, Sort sort) {
        return this.getDao().findAll(spec, sort);
    }

    /**
     * 信息查询.<br>
     *
     * @param ids 查询实体主键
     * @return List&lt;T&gt;
     */
    default List<T> findAllByIds(Collection<ID> ids) {
        return this.getDao().findAllById(ids);
    }

    // *************** Select xxx *************** //

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
     * 查询查询后置处理器.<br>
     *
     * @return Consumer&lt;S&gt;
     */
    default Consumer<Collection<T>> selectPostprocessor() {
        return (eles) -> {
            if (log.isDebugEnabled()) {
                log.debug("Select Post Processor Nothing.");
            }
        };
    }

    /**
     * 查询查询后置处理器.<br>
     *
     * @return Consumer&lt;S&gt;
     */
    default BiConsumer<S, Collection<T>> selectBiPostprocessor() {
        return (args, eles) -> this.selectPostprocessor().accept(eles);
    }

    /**
     * 查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> selectSort() {
        return this.defaultSort();
    }

    /**
     * 查询查询分页信息.<br>
     *
     * @return Function&lt;S,PageRequest&gt;
     */
    default Function<BasePageRequest, PageRequest> selectPageRequest() {
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
     * @see #select(Entity, PageRequest, Consumer)
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
        return this.select(args, pageRequest, preprocessor, this.selectBiPostprocessor());
    }

    /**
     * 信息查询.<br>
     *
     * @param args          查询实体
     * @param pageRequest   查询分页信息
     * @param preprocessor  查询前置处理器
     * @param postprocessor 查询后置处理器
     * @see #select(Entity, PageRequest, Consumer)
     * @return Page&lt;T&gt;
     */
    default Page<T> select(S args, PageRequest pageRequest, Consumer<S> preprocessor, Consumer<Collection<T>> postprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> it.accept(args));
        Page<T> page = this.select(this.selectQuery(args), pageRequest);
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(page.getContent()));
        return this.select(args, pageRequest, preprocessor, Objects.isNull(postprocessor) ? null : (__, eles) -> postprocessor.accept(eles));
    }

    /**
     * 信息查询.<br>
     *
     * @param args          查询实体
     * @param pageRequest   查询分页信息
     * @param preprocessor  查询前置处理器
     * @param postprocessor 查询后置处理器
     * @see #select(Entity, PageRequest, Consumer)
     * @return Page&lt;T&gt;
     */
    default Page<T> select(S args, PageRequest pageRequest, Consumer<S> preprocessor, BiConsumer<S, Collection<T>> postprocessor) {
        Optional.ofNullable(preprocessor).ifPresent(it -> it.accept(args));
        Page<T> page = this.select(this.selectQuery(args), pageRequest);
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(args, page.getContent()));
        return page;
    }

    /**
     * 信息查询.<br>
     *
     * @param specification 查询条件
     * @see #select(Specification, PageRequest)
     * @return List&lt;T&gt;
     */
    default List<T> select(Specification<T> specification) {
        return this.select(specification, this.selectSort().get());
    }

    /**
     * 信息查询.<br>
     *
     * @param specification 查询条件
     * @param sort          排序规则
     * @see #select(Specification, PageRequest)
     * @return List&lt;T&gt;
     */
    default List<T> select(Specification<T> specification, Sort sort) {
        return this.select(specification, sort, this.selectPostprocessor());
    }

    /**
     * 信息查询.<br>
     *
     * @param specification 查询条件
     * @param sort          排序规则
     * @param postprocessor 查询后置处理器
     * @see #select(Specification, PageRequest)
     * @return List&lt;T&gt;
     */
    default List<T> select(Specification<T> specification, Sort sort, Consumer<Collection<T>> postprocessor) {
        List<T> res = this.getDao().findAll(specification, this.selectAllSort().get());
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(res));
        return res;
    }

    /**
     * 信息查询.<br>
     *
     * @param specification 查询条件
     * @param pageRequest   查询分页信息
     * @return Page&lt;T&gt;
     */
    default Page<T> select(Specification<T> specification, PageRequest pageRequest) {
        return this.select(specification, pageRequest, this.selectPostprocessor());
    }

    /**
     * 信息查询.<br>
     *
     * @param specification 查询条件
     * @param pageRequest   查询分页信息
     * @param postprocessor 查询后置处理器
     * @return Page&lt;T&gt;
     */
    default Page<T> select(Specification<T> specification, PageRequest pageRequest, Consumer<Collection<T>> postprocessor) {
        Page<T> page = this.getDao().findAll(specification, pageRequest);
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(page.getContent()));
        return page;
    }

    /**
     * 全部查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> selectAllSort() {
        return this.defaultSort();
    }

    /**
     * 查询查询前置处理器.<br>
     *
     * @return Consumer&lt;S&gt;
     */
    default Consumer<Collection<T>> selectAllPostprocessor() {
        return this.selectPostprocessor();
    }

    /**
     * 全部查询.<br>
     *
     * @return List&lt;T&gt;
     */
    default List<T> selectAll() {
        return this.selectAll(this.selectAllPostprocessor());
    }

    /**
     * 全部查询.<br>
     * 
     * @param postprocessor 查询后置处理器
     * @return List&lt;T&gt;
     */
    default List<T> selectAll(Consumer<Collection<T>> postprocessor) {
        List<T> res = this.getDao().findAll(this.selectAllSort().get());
        Optional.ofNullable(postprocessor).ifPresent(it -> it.accept(res));
        return res;
    }

}
