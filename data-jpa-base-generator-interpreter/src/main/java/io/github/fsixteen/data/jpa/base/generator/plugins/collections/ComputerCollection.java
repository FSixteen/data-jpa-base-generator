package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.Constraint;
import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.PluginsCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * 类注解逻辑描述信息集合.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public final class ComputerCollection {

    private BuilderType type = BuilderType.SELECTED;

    private AnnotationCollection annotationCollection;

    private Collection<ComputerDescriptor<Annotation>> selectCds = new ArrayList<>();

    private Collection<ComputerDescriptor<Annotation>> existedCds = new ArrayList<>();

    private ComputerCollection() {
    }

    private ComputerCollection(BuilderType type, AnnotationCollection annotationCollection, Collection<ComputerDescriptor<Annotation>> selectCds,
            Collection<ComputerDescriptor<Annotation>> existedCds) {
        super();
        this.type = type;
        this.annotationCollection = annotationCollection;
        Optional.ofNullable(selectCds).ifPresent(it -> it.forEach(this.selectCds::add));
        Optional.ofNullable(existedCds).ifPresent(it -> it.forEach(this.existedCds::add));
    }

    public static ComputerCollection of() {
        return new ComputerCollection();
    }

    public static ComputerCollection of(AnnotationCollection annotationCollection, Collection<ComputerDescriptor<Annotation>> selectCds,
            Collection<ComputerDescriptor<Annotation>> existedCds) {
        return new ComputerCollection(BuilderType.SELECTED, annotationCollection, selectCds, existedCds);
    }

    public static ComputerCollection of(BuilderType type, AnnotationCollection ac, Collection<ComputerDescriptor<Annotation>> selectCds,
            Collection<ComputerDescriptor<Annotation>> existedCds) {
        return new ComputerCollection(type, ac, selectCds, existedCds);
    }

    public BuilderType getType() {
        return type;
    }

    public void setType(BuilderType type) {
        this.type = type;
    }

    public AnnotationCollection getAnnotationCollection() {
        return annotationCollection;
    }

    public void setAnnotationCollection(AnnotationCollection annotationCollection) {
        this.annotationCollection = annotationCollection;
    }

    public Type getConputerType(String name) {
        return this.annotationCollection.getConputerType(name);
    }

    public Map<String, Type> getConputerType() {
        return this.annotationCollection.getConputerType();
    }

    public Collection<ComputerDescriptor<Annotation>> getSelectCds() {
        return selectCds;
    }

    public void setSelectCds(Collection<ComputerDescriptor<Annotation>> selectCds) {
        this.selectCds = selectCds;
    }

    public void addSelectCd(ComputerDescriptor<Annotation> cd) {
        Optional.ofNullable(cd).filter(it -> it.getAnnoDesc().getAnno().annotationType().isAnnotationPresent(Selectable.class)).ifPresent(this.selectCds::add);
    }

    public Collection<ComputerDescriptor<Annotation>> getExistedCds() {
        return existedCds;
    }

    public void setExistedCds(Collection<ComputerDescriptor<Annotation>> existedCds) {
        this.existedCds = existedCds;
    }

    public void addExistedCd(ComputerDescriptor<Annotation> cd) {
        Optional.ofNullable(cd).filter(it -> it.getAnnoDesc().getAnno().annotationType().isAnnotationPresent(Existed.class)).ifPresent(this.existedCds::add);
    }

    public void addCd(ComputerDescriptor<Annotation> cd) {
        this.addSelectCd(cd);
        this.addExistedCd(cd);
    }

    public Collection<ComputerDescriptor<Annotation>> getCds() {
        return BuilderType.SELECTED == this.type ? this.selectCds : this.existedCds;
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return Predicate
     */
    public Predicate getPredicate(final CriteriaBuilder cb) {
        if (Type.OR == this.getConputerType(Constant.GLOBAL)) {
            return cb.or(this.getPredicateArray(Constant.DEFAULT, cb));
        }
        return cb.and(this.getPredicateArray(Constant.DEFAULT, cb));
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return List&lt;Predicate&gt;
     */
    public List<Predicate> getPredicateList(final CriteriaBuilder cb) {
        return this.getPredicateList(Constant.DEFAULT, cb);
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param scope 范围查询分组
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return List&lt;Predicate&gt;
     */
    public List<Predicate> getPredicateList(final String scope, final CriteriaBuilder cb) {
        List<ComputerDescriptor<Annotation>> valid = this.getCds().stream().filter(it -> Objects.nonNull(it) && !it.isEmpty() && it.containsScope(scope))
                .collect(Collectors.toList());
        Map<String, List<Tuple>> groups = new ConcurrentHashMap<>();
        for (ComputerDescriptor<Annotation> computerDescriptor : valid) {
            for (GroupInfo groupInfo : computerDescriptor.getAnnoDesc().getGroups()) {
                if (!groups.containsKey(groupInfo.value())) {
                    groups.put(groupInfo.value(), new ArrayList<>());
                }
                groups.get(groupInfo.value()).add(Tuple.of(groupInfo.order(), computerDescriptor));
            }
        }
        List<Predicate> ps = new ArrayList<>();
        for (Entry<String, List<Tuple>> entry : groups.entrySet()) {
            List<Predicate> groupPs = entry.getValue().stream().sorted((l, r) -> l.getFirst().compareTo(r.getFirst())).map(Tuple::getLast)
                    .map(ComputerDescriptor::getPredicate).collect(Collectors.toList());
            if (Type.OR == this.getConputerType(entry.getKey())) {
                ps.add(cb.or(groupPs.toArray(new Predicate[groupPs.size()])));
            } else {
                ps.add(cb.and(groupPs.toArray(new Predicate[groupPs.size()])));
            }
        }
        return ps;
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return Predicate[]
     */
    public Predicate[] getPredicateArray(final CriteriaBuilder cb) {
        return this.getPredicateArray(Constant.DEFAULT, cb);
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param scope 范围查询分组
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return Predicate[]
     */
    public Predicate[] getPredicateArray(final String scope, final CriteriaBuilder cb) {
        List<Predicate> predicates = this.getPredicateList(scope, cb);
        return predicates.toArray(new Predicate[predicates.size()]);
    }

    /**
     * 判断计算条件是否为空.<br>
     * 
     * @return boolean
     */
    public boolean isEmpty() {
        return this.isEmpty(BuilderType.SELECTED) && this.isEmpty(BuilderType.EXISTS);
    }

    /**
     * 判断指定计算方式内的计算条件是否为空.<br>
     * 
     * @param type 计算方式
     * @return boolean
     */
    public boolean isEmpty(BuilderType type) {
        switch (type) {
            case SELECTED:
                return this.selectCds.isEmpty() || 0L == this.selectCds.stream().filter(Objects::nonNull).count();
            case EXISTS:
                return this.existedCds.isEmpty() || 0L == this.existedCds.stream().filter(Objects::nonNull).count();
            default:
                return this.isEmpty();
        }
    }

    /**
     * 内部元组.
     * 
     * @author FSixteen
     * @since V1.0.0
     */
    private static class Tuple {

        private Integer first;
        private ComputerDescriptor<Annotation> last;

        public static Tuple of(Integer first, ComputerDescriptor<Annotation> last) {
            return new Tuple(first, last);
        }

        public Tuple(Integer first, ComputerDescriptor<Annotation> last) {
            this.first = first;
            this.last = last;
        }

        public Integer getFirst() {
            return first;
        }

        public ComputerDescriptor<Annotation> getLast() {
            return last;
        }

    }

    /**
     * {@link ComputerCollection} 构造器.
     * 
     * @author FSixteen
     * @since V1.0.0
     */
    public static class Builder {

        private static final Logger log = LoggerFactory.getLogger(Builder.class);
        private static Method toPredicateMethod;

        static {
            try {
                toPredicateMethod = BuilderPlugin.class.getMethod("toPredicate", AnnotationDescriptor.class, Object.class, Root.class, AbstractQuery.class,
                        CriteriaBuilder.class);
            } catch (NoSuchMethodException | SecurityException e) {
                log.error(e.getMessage(), e);
            }
        }

        private Collection<ComputerDescriptor<Annotation>> selectCds = new ArrayList<>();

        private Collection<ComputerDescriptor<Annotation>> existedCds = new ArrayList<>();

        private AnnotationCollection annotationCollection;

        private Object args;

        private Root<?> root;

        private CriteriaQuery<?> query;

        private CriteriaBuilder cb;

        private Builder() {
        }

        public static Builder of() {
            return new Builder();
        }

        public AnnotationCollection getAnnotationCollection() {
            return annotationCollection;
        }

        /**
         * 设置{@link AnnotationCollection}实体.
         * 
         * @param ac {@link AnnotationCollection} 实体
         * @return Builder
         */
        public Builder setAnnotationCollection(AnnotationCollection ac) {
            this.annotationCollection = ac;
            return this;
        }

        public Object getArgs() {
            return args;
        }

        /**
         * 设置实体.<br>
         * 
         * @param args 计算实体.
         * @return Builder
         */
        public Builder setArgs(Object args) {
            this.args = args;
            return this;
        }

        /**
         * 设置构建{@link javax.persistence.criteria.Predicate}所需要的{@link javax.persistence.criteria.Root},
         * {@link javax.persistence.criteria.CriteriaQuery},
         * {@link javax.persistence.criteria.CriteriaBuilder}.<br>
         * 
         * @param root  见{@link javax.persistence.criteria.Root}
         * @param query 见{@link javax.persistence.criteria.CriteriaQuery}
         * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
         * @return Builder
         */
        public Builder setSpecification(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            this.root = root;
            this.query = query;
            this.cb = cb;
            return this;
        }

        /**
         * 实例化 <code>ComputerPlugin</code> .
         *
         * @param ad {@link AnnotationDescriptor} 实例
         * @see io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin
         * @return Class
         */
        @SuppressWarnings("unchecked")
        private Class<? extends Annotation> createConstraint(AnnotationDescriptor<Annotation> ad) {
            Class<? extends Annotation> type = ad.getAnno().annotationType();
            try {
                if (!PluginsCache.containsKey(type)) {
                    Constructor<BuilderPlugin<? extends Annotation>> cp = (Constructor<BuilderPlugin<? extends Annotation>>) type
                            .getAnnotation(Constraint.class).processorBy().getDeclaredConstructor();
                    PluginsCache.register(type, cp.newInstance());
                }
            } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
                log.error(e.getMessage(), e);
            }
            return type;
        }

        /**
         * 创建 <code>ComputerDescriptor</code> .
         *
         * @param ad  {@link AnnotationDescriptor} 实例
         * @param cds {@link ComputerDescriptor} 实例容器
         * @see io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin
         * @return Builder
         */
        @SuppressWarnings("unchecked")
        private Builder createComputerDescriptor(AnnotationDescriptor<Annotation> ad, Collection<ComputerDescriptor<Annotation>> cds) {
            try {
                Object obj = PluginsCache.reference(this.createConstraint(ad));
                cds.add(ComputerDescriptor.class.cast(toPredicateMethod.invoke(obj, ad, this.getArgs(), root, query, cb)));
            } catch (ReflectiveOperationException | IllegalArgumentException e) {
                log.error(e.getMessage(), e);
            }
            return this;
        }

        /**
         * 创建 <code>ComputerDescriptor</code> .
         *
         * @param ads {@link AnnotationDescriptor} 实例容器
         * @param cds {@link ComputerDescriptor} 实例容器
         * @see io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin
         * @return Builder
         */
        private Builder createComputerDescriptor(Collection<AnnotationDescriptor<Annotation>> ads, Collection<ComputerDescriptor<Annotation>> cds) {
            ads.forEach(it -> this.createComputerDescriptor(it, cds));
            return this;
        }

        /**
         * 创建 <code>ComputerCollection</code> .
         *
         * @param type 计算方式
         * @return ComputerCollection
         */
        public ComputerCollection build(BuilderType type) {
            this.createComputerDescriptor(
                    BuilderType.SELECTED == type ? this.getAnnotationCollection().getSelectAds() : this.getAnnotationCollection().getExistedAds(),
                    BuilderType.SELECTED == type ? this.selectCds : this.existedCds);
            return new ComputerCollection(type, this.annotationCollection, this.selectCds, this.existedCds);
        }

    }

}
