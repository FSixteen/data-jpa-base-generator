package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CollectionPolicy;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.MetaAnnotationAttributes;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.ReadablePropertySupport;

/**
 * 单个注解的编译结果。
 *
 * <p>
 * 该对象把一个注解在运行期真正需要的信息收口为一份 immutable 规格：
 * 包括注解类型、默认绑定字段、集合策略、canonical 左右表达式、操作符、有效公共选项以及角色信息。
 * compiled provider 在执行时只依赖它，而不再重新解析原始注解。
 * </p>
 *
 * <p>
 * 从职责上看，它是“原始注解模型”与“统一谓词规格 {@link CompiledPredicateSpec}”之间的桥接层。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledAnnotationSpec<A extends Annotation> {

    private final Class<? extends Annotation> annotationType;

    private final Class<?> objClass;

    private final A annotation;

    private final CompiledPredicateRoleSpec role;

    /**
     * 当前注解默认绑定的参数字段路径。
     */
    private final String bindingPath;

    private final String valueFieldName;

    private final Field valueField;

    private final PropertyDescriptor valueFieldPd;

    private final CompiledCollectionPolicySpec collectionPolicy;

    private final CompiledPredicateCoreSpec predicateCore;

    private transient PredicateOptionsSpec effectiveOptions;

    private CompiledAnnotationSpec(final CompiledAnnotationSpec<A> source, final Expr left, final Expr right, final Expr[] extra) {
        this.annotationType = source.annotationType;
        this.objClass = source.objClass;
        this.annotation = source.annotation;
        this.role = source.role;
        this.bindingPath = source.bindingPath;
        this.valueFieldName = source.valueFieldName;
        this.valueField = source.valueField;
        this.valueFieldPd = source.valueFieldPd;
        this.collectionPolicy = source.collectionPolicy;
        this.predicateCore = CompiledPredicateCoreSpec.of(left, right, extra, source.predicateCore.getOptions(), source.predicateCore.getOp());
        this.effectiveOptions = source.effectiveOptions;
    }

    private CompiledAnnotationSpec(final Class<?> objClass, final A annotation, final Field valueField) {
        this(objClass, annotation, valueField, null);
    }

    private CompiledAnnotationSpec(final Class<?> objClass, final A annotation, final Field valueField, final PredicateOptionsSpec inheritedOptions) {
        this.objClass = objClass;
        this.annotation = annotation;
        this.annotationType = this.annotation.annotationType();
        this.role = MetaAnnotationAttributes.resolvePredicateRole(this.annotationType);
        this.valueField = valueField;
        this.valueFieldName = Objects.nonNull(this.valueField) ? this.valueField.getName() : null;
        this.valueFieldPd = propertyDescriptor(objClass, this.valueFieldName);
        CollectionPolicy collection = defaultIfNull(fieldValue(annotation, "collection", CollectionPolicy.class), SyntheticAnnotations.emptyCollectionPolicy());
        this.collectionPolicy = CompiledCollectionPolicySpec.of(defaultIfNull(collection.decollator(), ""), defaultIfNull(collection.regexp(), ""),
            defaultIfNull(collection.targetType(), TargetType.DEFAULT), defaultIfNull(collection.targetFormat(), ""), collection.split(),
            CompiledPredicateFilterSpec.of(collection.predicate()));
        this.predicateCore = CompiledPredicateCoreSpec.of(fieldValue(annotation, "left", Expr.class), fieldValue(annotation, "right", Expr.class),
            fieldValue(annotation, "extra", Expr[].class), fieldValue(annotation, "options", PredicateOptions.class),
            defaultIfNull(fieldValue(annotation, "op", CompareOp.class), CompareOp.EQ));
        this.bindingPath = this.valueFieldName;
        this.effectiveOptions = null == inheritedOptions ? PredicateOptionsSpec.root(this.predicateCore.getOptions())
            : PredicateOptionsSpec.inherit(inheritedOptions, this.predicateCore.getOptions());
    }

    /**
     * 基于参数对象类型、注解实例和绑定字段创建一份 compiled 规格。
     */
    public static <A extends Annotation> CompiledAnnotationSpec<A> of(final Class<?> objClass, final A annotation, final Field valueField) {
        return new CompiledAnnotationSpec<A>(objClass, annotation, valueField);
    }

    /**
     * 为不依赖具体字段绑定的注解创建 compiled 规格。
     */
    public static <A extends Annotation> CompiledAnnotationSpec<A> of(final Class<?> objClass, final A annotation) {
        return new CompiledAnnotationSpec<A>(objClass, annotation, null);
    }

    /**
     * 基于父级已生效选项创建一份可继承上下文的 compiled 规格。
     */
    public static <A extends Annotation> CompiledAnnotationSpec<A> of(final Class<?> objClass, final A annotation, final Field valueField,
        final PredicateOptionsSpec inheritedOptions) {
        return new CompiledAnnotationSpec<A>(objClass, annotation, valueField, inheritedOptions);
    }

    /**
     * 基于当前注解规格，覆写 canonical 表达式后返回一份新规格。
     *
     * <p>
     * 主要用于快捷注解、Cases 分支和某些 specialized spec 在编译阶段做表达式重写，
     * 而不破坏原始编译结果。
     * </p>
     *
     * @param left  新左表达式
     * @param right 新右表达式
     * @param extra 新附加表达式
     * @return 新的编译规格
     */
    public CompiledAnnotationSpec<A> withCanonicalExpressions(final Expr left, final Expr right, final Expr[] extra) {
        return new CompiledAnnotationSpec<A>(this, left, right, extra);
    }

    /**
     * 返回当前注解的 compare 核心规格。
     */
    public CompiledPredicateCoreSpec getPredicateCore() {
        return this.predicateCore;
    }

    /**
     * 返回当前规格所属的参数对象类型。
     */
    public Class<?> getObjClass() {
        return this.objClass;
    }

    /**
     * 返回原始注解实例。
     */
    public A getAnnotation() {
        return this.annotation;
    }

    /**
     * 返回原始注解类型。
     */
    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    /**
     * 返回当前注解在 selection/existence 语义下的参与角色。
     */
    public CompiledPredicateRoleSpec getRole() {
        return this.role;
    }

    /**
     * 返回默认绑定到实体路径的参数字段名。
     */
    public String getBindingPath() {
        return this.bindingPath;
    }

    /**
     * 返回当前注解绑定的参数字段名称。
     */
    public String getValueFieldName() {
        return this.valueFieldName;
    }

    /**
     * 返回当前注解绑定的参数字段反射对象。
     */
    public Field getValueField() {
        return this.valueField;
    }

    /**
     * 返回当前字段的可读属性描述器。
     */
    public PropertyDescriptor getValueFieldPd() {
        return this.valueFieldPd;
    }

    /**
     * 返回当前规格的集合处理策略。
     */
    CompiledCollectionPolicySpec getCollectionPolicy() {
        return this.collectionPolicy;
    }

    /**
     * 返回已生效的 scope 列表。
     */
    public String[] getScope() {
        return this.getEffectiveOptions().getScope();
    }

    /**
     * 返回已生效的 group 列表。
     */
    public GroupInfo[] getGroups() {
        return this.getEffectiveOptions().getGroups();
    }

    /**
     * 返回左侧 canonical 表达式。
     */
    public Expr getLeft() {
        return this.predicateCore.getLeft();
    }

    /**
     * 返回右侧 canonical 表达式。
     */
    public Expr getRight() {
        return this.predicateCore.getRight();
    }

    /**
     * 返回附加表达式列表。
     */
    public Expr[] getExtra() {
        return this.predicateCore.getExtra();
    }

    /**
     * 返回原始公共选项注解。
     */
    public PredicateOptions getOptions() {
        return this.predicateCore.getOptions();
    }

    /**
     * 返回 compare 操作符。
     */
    public CompareOp getOp() {
        return this.predicateCore.getOp();
    }

    /**
     * 返回默认值补齐和继承合并后的公共选项。
     */
    public PredicateOptionsSpec getEffectiveOptions() {
        return this.effectiveOptions;
    }

    /**
     * 从请求参数对象中读取当前注解绑定字段的运行时值。
     *
     * <p>
     * 这是 compiled 主链路的标准读取入口。
     * </p>
     *
     * @param args 请求参数对象
     * @return 原始字段值
     * @throws ReflectiveOperationException 读取失败
     */
    public Object readFieldValue(final Object args) throws ReflectiveOperationException {
        java.lang.reflect.Method readMethod = this.getValueFieldPd().getReadMethod();
        readMethod.setAccessible(true);
        return readMethod.invoke(args);
    }

    /**
     * 读取字段值，并在启用 {@code trim} 且值为字符串时执行裁剪。
     *
     * @param args 请求参数对象
     * @return 标准化后的字段值
     */
    public Object readAndTrim(final Object args) {
        try {
            Object fieldValue = this.readFieldValue(args);
            if (this.getEffectiveOptions().isTrim() && fieldValue instanceof String) {
                return String.class.cast(fieldValue).trim();
            }
            return fieldValue;
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 根据当前注解的有效选项判断某个值是否应被忽略。
     *
     * @param fieldValue 值
     * @return 是否忽略
     */
    public boolean shouldIgnore(final Object fieldValue) {
        PredicateOptionsSpec effective = this.getEffectiveOptions();
        if (effective.isIgnoreNull() && Objects.isNull(fieldValue)) {
            return true;
        }
        if (effective.isIgnoreEmpty() && fieldValue instanceof String && String.class.cast(fieldValue).isEmpty()) {
            return true;
        }
        return effective.isIgnoreBlank() && fieldValue instanceof String && String.class.cast(fieldValue).trim().isEmpty();
    }

    /**
     * 解析参数字段对应的只读属性描述器。
     */
    private static PropertyDescriptor propertyDescriptor(final Class<?> objClass, final String valueFieldName) {
        return Objects.nonNull(valueFieldName) ? ReadablePropertySupport.descriptor(objClass, valueFieldName) : null;
    }

    /**
     * 统一读取原始注解或元注解中的指定属性值。
     */
    private static <T> T fieldValue(final Annotation annotation, final String fieldName, final Class<T> type) {
        return MetaAnnotationAttributes.fieldValue(annotation, fieldName, type);
    }

    /**
     * 在注解字段缺省为空时回退到指定默认值。
     */
    private static <T> T defaultIfNull(final T value, final T defaultValue) {
        return null != value ? value : defaultValue;
    }

}
