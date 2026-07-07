package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledAnnotationSpec;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateBuildTarget;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.MetaAnnotationAttributes;
import io.github.fsixteen.data.jpa.base.generator.plugins.utils.BeanUtils;

/**
 * 单个请求参数类型对应的 compiled 注解集合。
 *
 * <p>
 * 该对象承载一个 query model 类型上所有可参与构建的注解编译结果，
 * 包括 selection/existence 两类视图以及类型级分组规则。
 * 它位于“反射扫描”与“运行期创建 Predicate”之间，是当前解释器最核心的中间层之一。
 * </p>
 *
 * <p>
 * 公开稳定链路通常按以下顺序使用：
 * </p>
 * <ol>
 * <li>通过 {@code CollectionCache.getAnnotationCollection(clazz)} 获取本对象</li>
 * <li>通过 {@link #toComputerCollection()} 进入运行期构建器</li>
 * <li>再由 {@code ComputerCollection} 或 {@code CompiledPredicateFacade}
 * 生成最终谓词</li>
 * </ol>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public final class AnnotationCollection {

    private final Logger log = LoggerFactory.getLogger(AnnotationCollection.class);

    private Class<?> clazz = null;

    private GroupComputerRegistry groupRegistry = GroupComputerRegistry.empty();

    /**
     * 注解编译结果统一总表.
     */
    private Collection<CompiledAnnotationSpec<?>> predicateSpecs = new ArrayList<>();

    /**
     * selection 视图缓存。
     */
    private Collection<CompiledAnnotationSpec<?>> selectionPredicateSpecsView;

    /**
     * existence 视图缓存。
     */
    private Collection<CompiledAnnotationSpec<?>> existencePredicateSpecsView;

    /**
     * 获取默认(空)类注解描述信息集合.<br>
     * 
     * @return AnnotationCollection
     */
    private static AnnotationCollection of() {
        return new AnnotationCollection();
    }

    private AnnotationCollection() {
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
    private void setClazz(Class<?> clazz) {
        this.clazz = clazz;
        this.groupRegistry = GroupComputerRegistry.of(this.clazz, this.log);
    }

    /**
     * 获取指定 scope 下的分组声明映射.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @return GroupComputerType
     */
    public Type getComputerType(String scope, String value) {
        return this.groupRegistry.type(scope, value);
    }

    /**
     * 获取指定 scope 与 group 的分组声明.<br>
     * 
     * @param scope 范围查询分组名称
     * @return Map&lt;String, GroupComputerType&gt;
     */
    public Map<String, GroupComputerType> getGroupComputerType(String scope) {
        return this.groupRegistry.groupsByScope(scope);
    }

    /**
     * 获取指定 scope 下的分组声明数组.<br>
     * 
     * @param scope 范围查询分组名称
     * @param value 条件分组名称
     * @return GroupComputerType
     */
    public GroupComputerType getGroupComputerType(String scope, String value) {
        return this.groupRegistry.group(scope, value);
    }

    /**
     * 获取全部分组声明数组.<br>
     * 
     * @param scope 范围查询分组名称
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes(String scope) {
        return this.groupRegistry.declarationsForScope(scope);
    }

    /**
     * 获取全部分组声明数组.<br>
     *
     * @return GroupComputerType[];
     */
    public GroupComputerType[] getGroupComputerTypes() {
        return this.groupRegistry.declarations();
    }

    /**
     * 追加一条已编译的注解规格，并清空 selection/existence 视图缓存。
     *
     * @param spec compiled 注解规格
     */
    public void addSpec(final CompiledAnnotationSpec<?> spec) {
        Optional.ofNullable(spec).ifPresent(it -> {
            this.predicateSpecs.add(it);
            this.invalidateViews();
        });
    }

    /**
     * 判断条件集合是否为空.<br>
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return this.predicateSpecs.isEmpty();
    }

    /**
     * 判断指定公开构建类型下的条件是否为空。<br>
     *
     * <p>
     * 对外仍保留 {@link BuilderType} 作为稳定边界，内部立即切换为
     * {@link PredicateBuildTarget}。
     * </p>
     *
     * @param type 公开构建类型
     * @return boolean
     */
    public boolean isEmpty(final BuilderType type) {
        return this.isEmpty(PredicateBuildTarget.from(type));
    }

    /**
     * 判断 selection 视图是否为空。<br>
     *
     * @return boolean
     */
    public boolean isSelectionEmpty() {
        return this.isEmpty(PredicateBuildTarget.SELECTION);
    }

    /**
     * 判断 existence 视图是否为空。<br>
     *
     * @return boolean
     */
    public boolean isExistenceEmpty() {
        return this.isEmpty(PredicateBuildTarget.EXISTENCE);
    }

    /**
     * 判断指定内部构建目标内的条件是否为空。<br>
     *
     * @param target 内部构建目标
     * @return boolean
     */
    boolean isEmpty(final PredicateBuildTarget target) {
        if (Objects.isNull(target)) {
            return this.isEmpty();
        }
        return this.getPredicateSpecs(target).isEmpty();
    }

    /**
     * compiled 主链路下的 selection 注解规格。<br>
     *
     * @return compiled selection 注解规格
     */
    public Collection<CompiledAnnotationSpec<?>> getSelectionPredicateSpecs() {
        return this.selectionPredicateSpecs();
    }

    /**
     * compiled 主链路下的 existence 注解规格。<br>
     *
     * @return compiled existence 注解规格
     */
    public Collection<CompiledAnnotationSpec<?>> getExistencePredicateSpecs() {
        return this.existencePredicateSpecs();
    }

    Collection<CompiledAnnotationSpec<?>> getPredicateSpecs(final PredicateBuildTarget target) {
        if (Objects.nonNull(target) && target.isExistence()) {
            return this.existencePredicateSpecs();
        }
        return this.selectionPredicateSpecs();
    }

    /**
     * 转换为类注解逻辑描述信息集合构造器.<br>
     *
     * <p>
     * 公开主入口统一直接返回 compiled 主链路构造器。
     * 对外仍保留该方法名，以保证既有外部调用链稳定。
     * </p>
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
         * 基于给定 {@code clazz} 创建一个全新实例.<br>
         * 
         * @param clazz 参与计算的实体类型
         * @return Builder
         */
        public static Builder of(Class<?> clazz) {
            return new Builder().with(clazz);
        }

        private Builder() {
        }

        /**
         * 扫描指定 query model 类型，并收集其中所有可识别的查询注解。
         *
         * <p>
         * 当前会遍历字段上的直接注解和 repeatable 注解容器；
         * 满足 {@link MetaAnnotationAttributes#isSelectionAnnotation(Class)} 或
         * {@link MetaAnnotationAttributes#isExistenceAnnotation(Class)} 的注解，
         * 都会在这里被编译成 {@link CompiledAnnotationSpec}。
         * </p>
         *
         * @param clazz 参与计算的 query model 类型
         * @return Builder
         */
        public Builder with(Class<?> clazz) {
            this.ac.setClazz(clazz);
            for (Field field : BeanUtils.getAllFields(clazz)) {
                this.collectFieldAnnotations(clazz, field);
            }
            for (Method method : BeanUtils.getAllMethods(clazz)) {
                if (BeanUtils.isReadablePropertyMethod(method)) {
                    this.collectMethodAnnotations(clazz, method);
                }
            }
            return this;
        }

        private void collectFieldAnnotations(final Class<?> clazz, final Field field) {
            for (Annotation anno : field.getDeclaredAnnotations()) {
                Class<? extends Annotation> annotationType = anno.annotationType();
                if (this.verifyAnnotation(annotationType)) {
                    Annotation[] repeatedMetaAnnotations = this.resolveRepeatablePredicateMetaAnnotations(annotationType);
                    if (repeatedMetaAnnotations.length > 0) {
                        for (Annotation repeatedMetaAnnotation : repeatedMetaAnnotations) {
                            this.ac.addSpec(CompiledAnnotationSpec.of(clazz, repeatedMetaAnnotation, field));
                        }
                    } else {
                        /* 同一元素, 同一注解只存在一次 */
                        this.ac.addSpec(CompiledAnnotationSpec.of(clazz, anno, field));
                    }
                } else {
                    /* 同一元素, 同一注解存在多次, anno值为该注解的集合体 */
                    for (Annotation ele : this.invokeAnnotationValue(anno)) {
                        this.ac.addSpec(CompiledAnnotationSpec.of(clazz, ele, field));
                    }
                }
            }
        }

        private void collectMethodAnnotations(final Class<?> clazz, final Method method) {
            for (Annotation anno : method.getDeclaredAnnotations()) {
                Class<? extends Annotation> annotationType = anno.annotationType();
                if (this.verifyAnnotation(annotationType)) {
                    Annotation[] repeatedMetaAnnotations = this.resolveRepeatablePredicateMetaAnnotations(annotationType);
                    if (repeatedMetaAnnotations.length > 0) {
                        for (Annotation repeatedMetaAnnotation : repeatedMetaAnnotations) {
                            this.ac.addSpec(CompiledAnnotationSpec.of(clazz, repeatedMetaAnnotation, method));
                        }
                    } else {
                        this.ac.addSpec(CompiledAnnotationSpec.of(clazz, anno, method));
                    }
                } else {
                    for (Annotation ele : this.invokeAnnotationValue(anno)) {
                        this.ac.addSpec(CompiledAnnotationSpec.of(clazz, ele, method));
                    }
                }
            }
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

        private Annotation[] resolveRepeatablePredicateMetaAnnotations(final Class<? extends Annotation> annotationType) {
            List<Annotation> annotations = new ArrayList<Annotation>();
            for (Annotation metaAnnotation : annotationType.getAnnotations()) {
                for (Annotation annotation : this.invokeAnnotationValue(metaAnnotation)) {
                    if (this.verifyAnnotation(annotation.annotationType())) {
                        annotations.add(annotation);
                    }
                }
            }
            return annotations.toArray(new Annotation[annotations.size()]);
        }

        private boolean verifyAnnotation(Class<?> clazz) {
            return clazz.isAnnotation() && (MetaAnnotationAttributes.isSelectionAnnotation(clazz) || MetaAnnotationAttributes.isExistenceAnnotation(clazz));
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

    private Collection<CompiledAnnotationSpec<?>> filterSpecs(final PredicateBuildTarget target) {
        Collection<CompiledAnnotationSpec<?>> filtered = new ArrayList<CompiledAnnotationSpec<?>>();
        for (CompiledAnnotationSpec<?> spec : this.predicateSpecs) {
            if (spec.getRole().matches(target)) {
                filtered.add(spec);
            }
        }
        return filtered;
    }

    Collection<CompiledAnnotationSpec<?>> selectionPredicateSpecs() {
        if (null == this.selectionPredicateSpecsView) {
            this.selectionPredicateSpecsView = this.filterSpecs(PredicateBuildTarget.SELECTION);
        }
        return this.selectionPredicateSpecsView;
    }

    Collection<CompiledAnnotationSpec<?>> existencePredicateSpecs() {
        if (null == this.existencePredicateSpecsView) {
            this.existencePredicateSpecsView = this.filterSpecs(PredicateBuildTarget.EXISTENCE);
        }
        return this.existencePredicateSpecsView;
    }

    private void invalidateViews() {
        this.selectionPredicateSpecsView = null;
        this.existencePredicateSpecsView = null;
    }

}
