package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import io.github.fsixteen.data.jpa.base.generator.plugins.utils.ArrayUtils;
import io.github.fsixteen.data.jpa.base.generator.plugins.utils.BeanUtils;

/**
 * 类注解描述信息集合.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public final class AnnotationCollection {

    private Logger log = LoggerFactory.getLogger(AnnotationCollection.class);

    private Class<?> clazz = null;

    /**
     * 分组计算信息.<br>
     * Key由范围查询分组名称及条件分组名称组合而成, 格式如下:<br>
     * Key1: ${scope}<br>
     * Key2: ${value}<br>
     * Value: ${GroupComputerType}<br>
     */
    private Map<String, Map<String, GroupComputerType>> conputerTypeMap = new ConcurrentHashMap<>();

    /**
     * 分组计算信息.<br>
     */
    private GroupComputerType[] computerTypes;

    /**
     * 查询类注解描述信息.<br>
     */
    private Collection<AnnotationDescriptor<Annotation>> selectAds = new ArrayList<>();

    /**
     * 判断已存在(Existed)类注解描述信息.<br>
     */
    private Collection<AnnotationDescriptor<Annotation>> existedAds = new ArrayList<>();

    /**
     * 获取默认(空)类注解描述信息集合.<br>
     * 
     * @return AnnotationCollection
     */
    public static AnnotationCollection of() {
        return new AnnotationCollection();
    }

    /**
     * 获取指定Class类类注解描述信息集合.<br>
     * 
     * @param clazz      参与计算的实体类
     * @param selectAds  查询类注解描述信息
     * @param existedAds 判断已存在(Existed)类注解描述信息
     * @return AnnotationCollection
     */
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
    }

    /**
     * 获取参与计算的实体类.<br>
     * 
     * @return Class&lt;?&gt;
     */
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
        this.computerTypes = this.clazz.getAnnotationsByType(GroupComputerType.class);
        this.conputerTypeMap.clear();
        for (GroupComputerType groupComputerType : this.computerTypes) {
            for (String scope : groupComputerType.scope()) {
                if (!this.conputerTypeMap.containsKey(scope)) {
                    this.conputerTypeMap.put(scope, new ConcurrentHashMap<>());
                }
                Map<String, GroupComputerType> groupComputerTypeMap = this.conputerTypeMap.get(scope);
                if (groupComputerTypeMap.containsKey(groupComputerType.value())) {
                    log.warn("%s, @GroupComputerType 重复出现.", clazz.getName());
                }
                groupComputerTypeMap.put(groupComputerType.value(), groupComputerType);
            }
        }
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @return GroupComputerType
     */
    public Type getComputerType(String scope, String value) {
        return Optional.ofNullable(conputerTypeMap.getOrDefault(scope, Collections.emptyMap()).get(value)).map(GroupComputerType::type).orElse(Type.AND);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @return Map&lt;String, GroupComputerType&gt;
     */
    public Map<String, GroupComputerType> getGroupComputerType(String scope) {
        return conputerTypeMap.get(scope);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @return GroupComputerType
     */
    public GroupComputerType getGroupComputerType(String scope, String value) {
        return conputerTypeMap.getOrDefault(scope, Collections.emptyMap()).get(value);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @param scope 范围查询分组名称
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes(String scope) {
        GroupComputerType[] temp = new GroupComputerType[computerTypes.length];
        int offset = 0;
        for (GroupComputerType computerType : computerTypes) {
            if (ArrayUtils.contains(computerType.scope(), scope)) {
                temp[offset++] = computerType;
            }
        }
        return Arrays.copyOf(temp, offset);
    }

    /**
     * 获取分组计算信息.<br>
     * 
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes() {
        return computerTypes;
    }

    /**
     * 获取查询类注解描述信息.<br>
     * 
     * @return Collection&lt;AnnotationDescriptor&lt;Annotation&gt;&gt;
     */
    public Collection<AnnotationDescriptor<Annotation>> getSelectAds() {
        return selectAds;
    }

    /**
     * 重置查询类注解描述信息.<br>
     * 
     * @param selectAds 查询类注解描述信息
     */
    public void setSelectAds(Collection<AnnotationDescriptor<Annotation>> selectAds) {
        this.selectAds = selectAds;
    }

    /**
     * 添加注解描述信息.
     * 
     * @param ad 注解描述信息
     */
    public void addSelectAd(AnnotationDescriptor<Annotation> ad) {
        Optional.ofNullable(ad)
            .filter(it -> it.getAnno().annotationType().isAnnotationPresent(Selectable.class) || it.getAnno().annotationType() == Selectable.class)
            .ifPresent(this.selectAds::add);
    }

    /**
     * 获取判断已存在(Existed)类注解描述信息.<br>
     * 
     * @return Collection&lt;AnnotationDescriptor&lt;Annotation&gt;&gt;
     */
    public Collection<AnnotationDescriptor<Annotation>> getExistedAds() {
        return existedAds;
    }

    /**
     * 重置判断已存在(Existed)类注解描述信息.<br>
     * 
     * @param existedAds 判断已存在(Existed)类注解描述信息
     */
    public void setExistedAds(Collection<AnnotationDescriptor<Annotation>> existedAds) {
        this.existedAds = existedAds;
    }

    /**
     * 添加注解描述信息.
     * 
     * @param ad 注解描述信息
     */
    public void addExistedAd(AnnotationDescriptor<Annotation> ad) {
        Optional.ofNullable(ad).filter(it -> it.getAnno().annotationType().isAnnotationPresent(Existed.class) || it.getAnno().annotationType() == Existed.class)
            .ifPresent(this.existedAds::add);
    }

    /**
     * 添加注解描述信息.
     * 
     * @param ad 注解描述信息
     */
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

    /**
     * 转换为类注解逻辑描述信息集合构造器.<br>
     * 
     * @return ComputerCollection.Builder
     */
    public ComputerCollection.Builder toComputerCollection() {
        return ComputerCollection.Builder.of().withAnnotationCollection(this);
    }

    /**
     * {@link AnnotationCollection} 构造器.
     * 
     * @author FSixteen
     * @since 1.0.0
     */
    public static class Builder {

        private AnnotationCollection ac = AnnotationCollection.of();

        /**
         * 创建一个全新空实例.<br>
         * 
         * @return Builder
         */
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
            return clazz.isAnnotation() && (clazz == Selectable.class || clazz.isAnnotationPresent(Selectable.class) || clazz == Existed.class
                || clazz.isAnnotationPresent(Existed.class));
        }

        /**
         * 获取类注解描述信息集合.<br>
         * 
         * @return AnnotationCollection
         */
        public AnnotationCollection build() {
            return this.ac;
        }

    }

}
