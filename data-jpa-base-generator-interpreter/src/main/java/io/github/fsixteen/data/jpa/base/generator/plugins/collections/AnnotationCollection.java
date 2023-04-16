package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.utils.BeanUtils;

/**
 * 类注解描述信息集合.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public final class AnnotationCollection {

    private Logger log = LoggerFactory.getLogger(AnnotationCollection.class);

    private Class<?> clazz = null;

    private Map<String, Type> conputerType = new ConcurrentHashMap<>();

    private Collection<AnnotationDescriptor<Annotation>> selectAds = new ArrayList<>();

    private Collection<AnnotationDescriptor<Annotation>> existedAds = new ArrayList<>();

    public static AnnotationCollection of() {
        return new AnnotationCollection();
    }

    public static AnnotationCollection of(Class<?> clazz, Collection<AnnotationDescriptor<Annotation>> selectAds,
            Collection<AnnotationDescriptor<Annotation>> existedAds) {
        return new AnnotationCollection(clazz, selectAds, existedAds);
    }

    private AnnotationCollection() {
    }

    private AnnotationCollection(Class<?> clazz, Collection<AnnotationDescriptor<Annotation>> selectAds,
            Collection<AnnotationDescriptor<Annotation>> existedAds) {
        super();
        this.selectAds = selectAds;
        this.existedAds = existedAds;
        this.setClazz(clazz);
        ;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * 配置参与计算的实体类.<br>
     * 
     * @param clazz 参与计算的实体类
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
        this.conputerType.clear();
        GroupComputerType[] annos = this.clazz.getAnnotationsByType(GroupComputerType.class);
        for (GroupComputerType groupComputerType : annos) {
            if (this.conputerType.containsKey(groupComputerType.value())) {
                log.warn("%s, @GroupComputerType 重复出现.", clazz.getName());
            }
            this.conputerType.put(groupComputerType.value(), groupComputerType.type());
        }
    }

    public Type getConputerType(String name) {
        return conputerType.getOrDefault(name, Type.AND);
    }

    public Map<String, Type> getConputerType() {
        return conputerType;
    }

    public void setConputerType(Map<String, Type> conputerType) {
        this.conputerType = conputerType;
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

    /**
     * 判断计算条件是否为空.<br>
     * 
     * @return boolean
     */
    public boolean isEmpty() {
        return this.selectAds.isEmpty() && this.existedAds.isEmpty();
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
                return this.selectAds.isEmpty();
            case EXISTS:
                return this.existedAds.isEmpty();
            default:
                return this.isEmpty();
        }
    }

    public ComputerCollection.Builder toComputerCollection() {
        return ComputerCollection.Builder.of().setAnnotationCollection(this);
    }

    /**
     * {@link AnnotationCollection} 构造器.
     * 
     * @author FSixteen
     * @since V1.0.0
     */
    public static class Builder {

        private AnnotationCollection ac = AnnotationCollection.of();

        public static Builder of() {
            return new Builder();
        }

        /**
         * 通过给定的参与计算的实体类, 补充完善类注解描述信息集合.<br>
         * 
         * @param clazz 参与计算的实体类
         * @return Builder
         */
        public Builder with(Class<?> clazz) {
            this.ac.setClazz(clazz);
            for (Field field : BeanUtils.getAllFields(clazz)) {
                for (Annotation anno : field.getDeclaredAnnotations()) {
                    Class<? extends Annotation> annotationType = anno.annotationType();
                    if (this.verifyAnnotation(annotationType)) {
                        /* 同一元素, 同一注解只存在一次 */
                        this.ac.addAd(AnnotationDescriptor.of(clazz, anno, field));
                    } else {
                        /* 同一元素, 同一注解存在多次, anno值为该注解的集合体 */
                        for (Annotation ele : this.invokeAnnotationValue(anno)) {
                            this.ac.addAd(AnnotationDescriptor.of(clazz, ele, field));
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
