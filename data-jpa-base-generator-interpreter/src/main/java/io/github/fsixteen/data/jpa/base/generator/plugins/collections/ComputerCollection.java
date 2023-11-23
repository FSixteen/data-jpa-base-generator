package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.Constraint;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;
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
 * @since 1.0.0
 */
public final class ComputerCollection {

    private BuilderType type = BuilderType.SELECTED;

    private AnnotationCollection annotationCollection;

    private Collection<ComputerDescriptor<Annotation>> computerDescriptors = new ArrayList<>();

    public static ComputerCollection of(BuilderType type, AnnotationCollection ac, Collection<ComputerDescriptor<Annotation>> selectCds) {
        return new ComputerCollection(type, ac, selectCds);
    }

    private ComputerCollection(BuilderType type, AnnotationCollection ac, Collection<ComputerDescriptor<Annotation>> selectCds) {
        super();
        this.type = type;
        this.annotationCollection = ac;
        Optional.ofNullable(selectCds).ifPresent(it -> it.forEach(this.computerDescriptors::add));
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

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @see AnnotationCollection#getGroupComputerType(String)
     * @return Map&lt;String, GroupComputerType&gt;
     */
    public Map<String, GroupComputerType> getConputerType(String scope) {
        return this.annotationCollection.getGroupComputerType(scope);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @return GroupComputerType
     */
    public Type getComputerType(String scope, String value) {
        return this.annotationCollection.getComputerType(scope, value);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @see AnnotationCollection#getGroupComputerType(String, String)
     * @return GroupComputerType
     */
    public GroupComputerType getGroupComputerType(String scope, String value) {
        return this.annotationCollection.getGroupComputerType(scope, value);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes(String scope) {
        return this.annotationCollection.getGroupComputerTypes(scope);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes() {
        return this.annotationCollection.getGroupComputerTypes();
    }

    public Collection<ComputerDescriptor<Annotation>> getComputerDescriptors() {
        return computerDescriptors;
    }

    public void setComputerDescriptors(Collection<ComputerDescriptor<Annotation>> computerDescriptors) {
        this.computerDescriptors = computerDescriptors;
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return Predicate
     */
    public Predicate getPredicate(final CriteriaBuilder cb) {
        if (Type.OR == this.getComputerType(Constant.DEFAULT, Constant.GLOBAL)) {
            return cb.or(this.getPredicateArray(cb, Constant.DEFAULT));
        }
        return cb.and(this.getPredicateArray(cb, Constant.DEFAULT));
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return List&lt;Predicate&gt;
     */
    public List<Predicate> getPredicateList(final CriteriaBuilder cb) {
        return this.getPredicateList(cb, Constant.DEFAULT);
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
     * @param scope 范围查询分组
     * @return List&lt;Predicate&gt;
     */
    public List<Predicate> getPredicateList(final CriteriaBuilder cb, final String scope) {
        return new PredicateBuildProcessor(this, cb, scope).toPredicate();
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb 见{@link javax.persistence.criteria.CriteriaBuilder}
     * @return Predicate[]
     */
    public Predicate[] getPredicateArray(final CriteriaBuilder cb) {
        return this.getPredicateArray(cb, Constant.DEFAULT);
    }

    /**
     * 获取{@link javax.persistence.criteria.Predicate}.<br>
     * 
     * @param cb    见{@link javax.persistence.criteria.CriteriaBuilder}
     * @param scope 范围查询分组
     * @return Predicate[]
     */
    public Predicate[] getPredicateArray(final CriteriaBuilder cb, final String scope) {
        List<Predicate> predicates = this.getPredicateList(cb, scope);
        return predicates.toArray(new Predicate[predicates.size()]);
    }

    /**
     * 判断计算条件是否为空.<br>
     * 
     * @return boolean
     */
    public boolean isEmpty() {
        return this.computerDescriptors.isEmpty() || 0L == this.computerDescriptors.stream().filter(Objects::nonNull).count();
    }

    /**
     * {@link ComputerCollection} 构造器.
     * 
     * @author FSixteen
     * @since 1.0.0
     */
    public static class Builder {

        private static final Logger LOG = LoggerFactory.getLogger(Builder.class);

        private AnnotationCollection annotationCollection;

        private Object args;

        private Root<?> root;

        private CriteriaQuery<?> query;

        private CriteriaBuilder cb;

        private Collection<ComputerDescriptor<Annotation>> computerDescriptors = new ArrayList<>();

        private Builder() {
        }

        public static Builder of() {
            return new Builder();
        }

        /**
         * 设置{@link AnnotationCollection}实体.
         * 
         * @param ac {@link AnnotationCollection} 实体
         * @return Builder
         */
        public Builder withAnnotationCollection(AnnotationCollection ac) {
            this.annotationCollection = ac;
            return this;
        }

        /**
         * 设置实体.<br>
         * 
         * @param args 计算实体.
         * @return Builder
         */
        public Builder withArgs(Object args) {
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
        public Builder withSpecification(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
                    Constraint constraint = type.getAnnotation(Constraint.class);
                    Constructor<BuilderPlugin<? extends Annotation>> cp = (Constructor<
                        BuilderPlugin<? extends Annotation>>) (Void.class != constraint.processorBy() ? constraint.processorBy()
                            : Class.forName(constraint.processorByClassName())).getDeclaredConstructor();
                    PluginsCache.register(type, cp.newInstance());
                }
            } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
                LOG.error(e.getMessage(), e);
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
        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Builder createComputerDescriptor(AnnotationDescriptor<Annotation> ad, Collection<ComputerDescriptor<Annotation>> cds) {
            try {
                cds.add(PluginsCache.reference(this.createConstraint(ad)).toPredicate((AnnotationDescriptor) ad, this.args, root, query, cb));
            } catch (ClassNotFoundException e) {
                LOG.error(e.getMessage(), e);
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
            this.createComputerDescriptor(BuilderType.SELECTED == type ? this.annotationCollection.getSelectAds() : this.annotationCollection.getExistedAds(),
                this.computerDescriptors);
            return new ComputerCollection(type, this.annotationCollection, this.computerDescriptors);
        }

    }

}
