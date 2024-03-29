package io.github.fsixteen.data.jpa.base.generator.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
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
    static Container<Supplier<Sort>> DEFAULT_SORT_COLUMNS = new Container<>(() -> Sort.unsorted());
    static Container<Supplier<Sort>> CUSTOM_SORT_COLUMNS = new Container<>();

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
     * 查询固定条件, 不受注解影响.<br>
     *
     * @return Specification&lt;T&gt;
     * @since 1.0.1
     */
    default Specification<T> selectFixedPredicate() {
        return (root, query, criteriaBuilder) -> null;
    }

    /**
     * 查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> defaultSort() {
        return CUSTOM_SORT_COLUMNS.isNull() ? DEFAULT_SORT_COLUMNS.getSort() : CUSTOM_SORT_COLUMNS.getSort();
    }

    /**
     * 查询排序规则.<br>
     *
     * @return Supplier&lt;Sort&gt;
     */
    default Supplier<Sort> findSort() {
        return this.defaultSort();
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
     * @return List&lt;T&gt;
     */
    default List<T> findAll() {
        return this.getDao().findAll();
    }

    /**
     * 信息查询.<br>
     * 
     * @param args 查询实体
     * @return List&lt;T&gt;
     */
    default List<T> findAll(Entity args) {
        return this.getDao().findAll(this.selectQuery(args));
    }

    /**
     * 信息查询.<br>
     * 
     * @param args 查询实体
     * @return Page&lt;T&gt;
     */
    default Page<T> findAll(BasePageRequest args) {
        return this.getDao().findAll(this.selectPageRequest().apply(args));
    }

    /**
     * 信息查询.<br>
     * 
     * @param args 查询实体
     * @return Page&lt;T&gt;
     */
    default Page<T> findAll(DefaultPageRequest args) {
        return this.getDao().findAll(this.selectQuery(args), this.selectPageRequest().apply(args));
    }

    /**
     * 信息查询.<br>
     * 
     * @param spec 查询条件
     * @return List&lt;T&gt;
     */
    default List<T> findAll(@Nullable Specification<T> spec) {
        return this.getDao().findAll(spec);
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

    /**
     * 获取查询条件.<br>
     * 
     * @param args 查询实体
     * @return Specification&lt;T&gt;
     */
    default Specification<T> selectQuery(Entity args) {
        final AnnotationCollection computer = CollectionCache.getAnnotationCollection(args.getClass());
        return (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (!computer.isEmpty(BuilderType.SELECTED)) {
                list.add(computer.toComputerCollection().withArgs(args).withSpecification(root, query, cb).build(BuilderType.SELECTED).getPredicate(cb));
            }
            Predicate predicate = this.selectFixedPredicate().toPredicate(root, query, cb);
            if (Objects.nonNull(predicate)) {
                list.add(predicate);
            }
            return cb.and(list.toArray(new Predicate[list.size()]));
        };
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
        return this.defaultSort();
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
