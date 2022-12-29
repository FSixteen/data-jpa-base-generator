package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.PluginsCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public final class ComputerCollection {

    private BuilderType type = BuilderType.SELECTED;

    private Collection<ComputerDescriptor<Annotation>> selectCds = new ArrayList<>();

    private Collection<ComputerDescriptor<Annotation>> existedCds = new ArrayList<>();

    private ComputerCollection() {
    }

    private ComputerCollection(BuilderType type, Collection<ComputerDescriptor<Annotation>> selectCds, Collection<ComputerDescriptor<Annotation>> existedCds) {
        super();
        this.type = type;
        Optional.ofNullable(selectCds).ifPresent(it -> it.forEach(this.selectCds::add));
        Optional.ofNullable(existedCds).ifPresent(it -> it.forEach(this.existedCds::add));
    }

    public static ComputerCollection of() {
        return new ComputerCollection();
    }

    public static ComputerCollection of(Collection<ComputerDescriptor<Annotation>> selectCds, Collection<ComputerDescriptor<Annotation>> existedCds) {
        return new ComputerCollection(BuilderType.SELECTED, selectCds, existedCds);
    }

    public static ComputerCollection of(BuilderType type, Collection<ComputerDescriptor<Annotation>> selectCds,
            Collection<ComputerDescriptor<Annotation>> existedCds) {
        return new ComputerCollection(type, selectCds, existedCds);
    }

    public BuilderType getType() {
        return type;
    }

    public void setType(BuilderType type) {
        this.type = type;
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

    public List<Predicate> getPredicateList() {
        return this.getCds().stream().filter(it -> Objects.nonNull(it) && !it.isEmpty()).map(ComputerDescriptor::getPredicate).collect(Collectors.toList());
    }

    public Predicate[] getPredicateArray() {
        List<Predicate> predicates = this.getPredicateList();
        return predicates.toArray(new Predicate[predicates.size()]);
    }

    public boolean isEmpty() {
        return this.isEmpty(BuilderType.SELECTED) && this.isEmpty(BuilderType.EXISTS);
    }

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

        private AnnotationCollection ac;

        private Object args;

        private Root<?> root;

        private CriteriaQuery<?> query;

        private CriteriaBuilder cb;

        private Builder() {
        }

        public static Builder of() {
            return new Builder();
        }

        public AnnotationCollection getAc() {
            return ac;
        }

        public Builder setAc(AnnotationCollection ac) {
            this.ac = ac;
            return this;
        }

        public Object getArgs() {
            return args;
        }

        public Builder setArgs(Object args) {
            this.args = args;
            return this;
        }

        /**
         * @param root
         * @param query
         * @param cb
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
         * @param ad
         * @return Class
         * @see io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin
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
         * @param ad
         * @param cds
         * @return Builder
         * @see io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin
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
         * @param ads
         * @param cds
         * @return Builder
         * @see io.github.fsixteen.data.jpa.base.generator.plugins.BuilderPlugin
         */
        private Builder createComputerDescriptor(Collection<AnnotationDescriptor<Annotation>> ads, Collection<ComputerDescriptor<Annotation>> cds) {
            ads.forEach(it -> this.createComputerDescriptor(it, cds));
            return this;
        }

        /**
         * 创建 <code>ComputerCollection</code> .
         * 
         * @param type
         * @return ComputerCollection
         */
        public ComputerCollection build(BuilderType type) {
            this.createComputerDescriptor(BuilderType.SELECTED == type ? this.getAc().getSelectAds() : this.getAc().getExistedAds(),
                    BuilderType.SELECTED == type ? this.selectCds : this.existedCds);
            return new ComputerCollection(type, this.selectCds, this.existedCds);
        }
    }
}
