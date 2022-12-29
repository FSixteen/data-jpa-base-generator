package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.utils.BeanUtils;

/**
 * @author FSixteen
 * @since V1.0.0
 */
public final class AnnotationCollection {

    private Collection<AnnotationDescriptor<Annotation>> selectAds = new ArrayList<>();

    private Collection<AnnotationDescriptor<Annotation>> existedAds = new ArrayList<>();

    public static AnnotationCollection of() {
        return new AnnotationCollection();
    }

    public static AnnotationCollection of(Collection<AnnotationDescriptor<Annotation>> selectAds, Collection<AnnotationDescriptor<Annotation>> existedAds) {
        return new AnnotationCollection(selectAds, existedAds);
    }

    private AnnotationCollection() {
    }

    private AnnotationCollection(Collection<AnnotationDescriptor<Annotation>> selectAds, Collection<AnnotationDescriptor<Annotation>> existedAds) {
        super();
        this.selectAds = selectAds;
        this.existedAds = existedAds;
    }

    public Collection<AnnotationDescriptor<Annotation>> getSelectAds() {
        return selectAds;
    }

    public void setSelectAds(Collection<AnnotationDescriptor<Annotation>> selectAds) {
        this.selectAds = selectAds;
    }

    public void addSelectAd(AnnotationDescriptor<Annotation> ad) {
        Optional.ofNullable(ad).filter(it -> it.getAnno().annotationType().isAnnotationPresent(Selectable.class)).ifPresent(this.selectAds::add);
    }

    public Collection<AnnotationDescriptor<Annotation>> getExistedAds() {
        return existedAds;
    }

    public void setExistedAds(Collection<AnnotationDescriptor<Annotation>> existedAds) {
        this.existedAds = existedAds;
    }

    public void addExistedAd(AnnotationDescriptor<Annotation> ad) {
        Optional.ofNullable(ad).filter(it -> it.getAnno().annotationType().isAnnotationPresent(Existed.class)).ifPresent(this.existedAds::add);
    }

    public void addAd(AnnotationDescriptor<Annotation> ad) {
        this.addSelectAd(ad);
        this.addExistedAd(ad);
    }

    public boolean isEmpty() {
        return this.selectAds.isEmpty() && this.existedAds.isEmpty();
    }

    public boolean isEmpty(BuilderType type) {
        switch (type) {
            case SELECTED:
                return this.selectAds.isEmpty();
            case EXISTS:
                return this.existedAds.isEmpty();
            default:
                return this.isEmpty();
        }
    }

    public static class Builder {
        final AnnotationCollection ac = AnnotationCollection.of();

        public static Builder of() {
            return new Builder();
        }

        public Builder with(Class<?> clazz) {
            for (Field field : BeanUtils.getAllFields(clazz)) {
                for (Annotation anno : field.getDeclaredAnnotations()) {
                    Class<? extends Annotation> annotationType = anno.annotationType();
                    /* 同一元素, 同一注解只存在一次 */
                    if (this.verifyAnnotation(annotationType)) {
                        ac.addAd(AnnotationDescriptor.of(clazz, anno, field));
                    }
                    /* 同一元素, 同一注解存在多次, anno值为该注解的集合体 */
                    else {
                        for (Annotation ele : this.invokeAnnotationValue(anno)) {
                            ac.addAd(AnnotationDescriptor.of(clazz, ele, field));
                        }
                    }
                }
            }
            return this;
        }

        private Annotation[] invokeAnnotationValue(Annotation anno) {
            try {
                Method method = anno.getClass().getMethod("value");
                Class<?> returnType = method.getReturnType();
                if (returnType.isArray() && this.verifyAnnotation(returnType.getComponentType())) {
                    return (Annotation[]) method.invoke(anno);
                }
            } catch (ReflectiveOperationException | SecurityException e) {
                // Nothing
            }
            return new Annotation[0];
        }

        private boolean verifyAnnotation(Class<?> clazz) {
            return clazz.isAnnotation() && clazz.isAnnotationPresent(Selectable.class) || clazz.isAnnotationPresent(Existed.class);
        }

        public AnnotationCollection build() {
            return this.ac;
        }
    }

}
