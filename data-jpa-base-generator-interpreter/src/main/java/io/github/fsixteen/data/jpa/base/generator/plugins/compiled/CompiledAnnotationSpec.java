package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
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
 * 它还负责把快捷注解上的零配置默认值补齐为稳定 canonical 语义，
 * 例如 `@Length` 的目标路径推断、`PredicateOptions` 的默认值补齐与父级继承合并。
 * </p>
 *
 * @param <A> 原始注解类型
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
        this(objClass, annotation, valueField, null, null);
    }

    private CompiledAnnotationSpec(final Class<?> objClass, final A annotation, final Method valueMethod) {
        this(objClass, annotation, null, CompiledPropertyBindings.propertyName(valueMethod), null);
    }

    private CompiledAnnotationSpec(final Class<?> objClass, final A annotation, final Field valueField, final PredicateOptionsSpec inheritedOptions) {
        this(objClass, annotation, valueField, null, inheritedOptions);
    }

    private CompiledAnnotationSpec(final Class<?> objClass, final A annotation, final Field valueField, final String valueFieldName,
        final PredicateOptionsSpec inheritedOptions) {
        this.objClass = objClass;
        this.annotation = annotation;
        this.annotationType = this.annotation.annotationType();
        this.role = MetaAnnotationAttributes.resolvePredicateRole(this.annotationType);
        this.valueField = valueField;
        this.valueFieldName = Objects.nonNull(this.valueField) ? this.valueField.getName() : valueFieldName;
        this.valueFieldPd = propertyDescriptor(objClass, this.valueFieldName);
        CollectionPolicy collection = defaultIfNull(fieldValue(annotation, "collection", CollectionPolicy.class), SyntheticAnnotations.emptyCollectionPolicy());
        this.collectionPolicy = CompiledCollectionPolicySpec.of(defaultIfNull(collection.decollator(), ""), defaultIfNull(collection.regexp(), ""),
            defaultIfNull(collection.targetType(), TargetType.DEFAULT), defaultIfNull(collection.targetFormat(), ""), collection.split(),
            CompiledPredicateFilterSpec.of(collection.predicate()));
        Expr left = fieldValue(annotation, "left", Expr.class);
        this.predicateCore = CompiledPredicateCoreSpec.of(normalizeLengthLeft(left, this.annotationType, this.valueFieldName),
            fieldValue(annotation, "right", Expr.class), fieldValue(annotation, "extra", Expr[].class),
            fieldValue(annotation, "options", PredicateOptions.class), defaultIfNull(fieldValue(annotation, "op", CompareOp.class), CompareOp.EQ));
        CompiledAnnotationOperatorGuards.validate(this.annotationType, this.predicateCore.getOp());
        this.bindingPath = this.valueFieldName;
        this.effectiveOptions = null == inheritedOptions ? PredicateOptionsSpec.root(this.predicateCore.getOptions())
            : PredicateOptionsSpec.inherit(inheritedOptions, this.predicateCore.getOptions());
    }

    /**
     * 基于参数对象类型、注解实例和绑定字段创建一份 compiled 规格。
     *
     * @param <A>        注解类型
     * @param objClass   参数对象类型
     * @param annotation 注解实例
     * @param valueField 注解绑定字段
     * @return compiled 注解规格
     */
    public static <A extends Annotation> CompiledAnnotationSpec<A> of(final Class<?> objClass, final A annotation, final Field valueField) {
        return new CompiledAnnotationSpec<A>(objClass, annotation, valueField);
    }

    /**
     * 基于参数对象类型、注解实例和绑定 getter 创建一份 compiled 规格。
     *
     * @param <A>         注解类型
     * @param objClass    参数对象类型
     * @param annotation  注解实例
     * @param valueMethod 注解绑定 getter
     * @return compiled 注解规格
     */
    public static <A extends Annotation> CompiledAnnotationSpec<A> of(final Class<?> objClass, final A annotation, final Method valueMethod) {
        return new CompiledAnnotationSpec<A>(objClass, annotation, valueMethod);
    }

    /**
     * 为不依赖具体字段绑定的注解创建 compiled 规格。
     *
     * @param <A>        注解类型
     * @param objClass   参数对象类型
     * @param annotation 注解实例
     * @return compiled 注解规格
     */
    public static <A extends Annotation> CompiledAnnotationSpec<A> of(final Class<?> objClass, final A annotation) {
        return new CompiledAnnotationSpec<A>(objClass, annotation, (Field) null);
    }

    /**
     * 基于父级已生效选项创建一份可继承上下文的 compiled 规格。
     *
     * @param <A>              注解类型
     * @param objClass         参数对象类型
     * @param annotation       注解实例
     * @param valueField       注解绑定字段
     * @param inheritedOptions 父级已生效的公共选项
     * @return compiled 注解规格
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
     *
     * @return compare 核心规格
     */
    public CompiledPredicateCoreSpec getPredicateCore() {
        return this.predicateCore;
    }

    /**
     * 返回当前规格所属的参数对象类型。
     *
     * @return 参数对象类型
     */
    public Class<?> getObjClass() {
        return this.objClass;
    }

    /**
     * 返回原始注解实例。
     *
     * @return 原始注解实例
     */
    public A getAnnotation() {
        return this.annotation;
    }

    /**
     * 返回原始注解类型。
     *
     * @return 原始注解类型
     */
    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    /**
     * 返回当前注解在 selection/existence 语义下的参与角色。
     *
     * @return 角色规格
     */
    public CompiledPredicateRoleSpec getRole() {
        return this.role;
    }

    /**
     * 返回默认绑定到实体路径的参数字段名。
     *
     * @return 绑定路径
     */
    public String getBindingPath() {
        return this.bindingPath;
    }

    /**
     * 返回当前注解绑定的参数字段名称。
     *
     * @return 参数字段名称
     */
    public String getValueFieldName() {
        return this.valueFieldName;
    }

    /**
     * 返回当前注解绑定的参数字段反射对象。
     *
     * @return 参数字段反射对象
     */
    public Field getValueField() {
        return this.valueField;
    }

    /**
     * 返回当前字段的可读属性描述器。
     *
     * @return 可读属性描述器
     */
    public PropertyDescriptor getValueFieldPd() {
        return this.valueFieldPd;
    }

    /**
     * 返回当前规格的集合处理策略。
     *
     * @return 集合处理策略
     */
    CompiledCollectionPolicySpec getCollectionPolicy() {
        return this.collectionPolicy;
    }

    /**
     * 返回已生效的 scope 列表。
     *
     * @return 生效后的 scope 列表
     */
    public String[] getScope() {
        return this.getEffectiveOptions().getScope();
    }

    /**
     * 返回已生效的 group 列表。
     *
     * @return 生效后的 group 列表
     */
    public GroupInfo[] getGroups() {
        return this.getEffectiveOptions().getGroups();
    }

    /**
     * 返回左侧 canonical 表达式。
     *
     * @return 左侧 canonical 表达式
     */
    public Expr getLeft() {
        return this.predicateCore.getLeft();
    }

    /**
     * 返回右侧 canonical 表达式。
     *
     * @return 右侧 canonical 表达式
     */
    public Expr getRight() {
        return this.predicateCore.getRight();
    }

    /**
     * 返回附加表达式列表。
     *
     * @return 附加表达式列表
     */
    public Expr[] getExtra() {
        return this.predicateCore.getExtra();
    }

    /**
     * 返回原始公共选项注解。
     *
     * @return 原始公共选项注解
     */
    public PredicateOptions getOptions() {
        return this.predicateCore.getOptions();
    }

    /**
     * 返回 compare 操作符。
     *
     * @return compare 操作符
     */
    public CompareOp getOp() {
        return this.predicateCore.getOp();
    }

    /**
     * 返回默认值补齐和继承合并后的公共选项。
     *
     * @return 生效后的公共选项
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
     * @throws IllegalStateException 读取失败或字段绑定无效
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
     * <p>
     * 当前只处理 null / 空串 / 空白串 这三类标准忽略语义；
     * 更复杂的集合元素过滤规则由 collection policy 侧单独处理。
     * </p>
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
     *
     * @param objClass       参数对象类型
     * @param valueFieldName 参数字段名
     * @return 只读属性描述器；当字段名为空时返回 {@code null}
     */
    private static PropertyDescriptor propertyDescriptor(final Class<?> objClass, final String valueFieldName) {
        return Objects.nonNull(valueFieldName) ? ReadablePropertySupport.descriptor(objClass, valueFieldName) : null;
    }

    /**
     * 统一读取原始注解或元注解中的指定属性值。
     *
     * @param <T>        期望返回类型
     * @param annotation 原始注解或元注解实例
     * @param fieldName  属性名
     * @param type       期望返回类型
     * @return 属性值；若不存在则返回框架约定的空值
     */
    private static <T> T fieldValue(final Annotation annotation, final String fieldName, final Class<T> type) {
        return MetaAnnotationAttributes.fieldValue(annotation, fieldName, type);
    }

    /**
     * 在注解字段缺省为空时回退到指定默认值。
     *
     * @param <T>          值类型
     * @param value        原始值
     * @param defaultValue 默认值
     * @return value 非空时返回 value，否则返回 defaultValue
     */
    private static <T> T defaultIfNull(final T value, final T defaultValue) {
        return null != value ? value : defaultValue;
    }

    /**
     * 针对 `@Length` 家族在零配置场景下补齐默认左表达式。
     *
     * <p>
     * 当左表达式仍是声明层默认的 `length(path)` 且 path 为空时，
     * compiled 阶段会根据当前绑定属性名推断真实目标路径，
     * 例如 `nameLength -> name`。
     * </p>
     *
     * @param left           原始左表达式
     * @param annotationType 当前注解类型
     * @param valueFieldName 当前绑定属性名
     * @return 归一化后的左表达式
     */
    private static Expr normalizeLengthLeft(final Expr left, final Class<? extends Annotation> annotationType, final String valueFieldName) {
        if (!CompiledAnnotationOperatorGuards.isLengthAnnotationFamily(annotationType) || !isDefaultLengthLeft(left)) {
            return left;
        }
        return SyntheticAnnotations.lengthExpr(inferLengthTargetPath(valueFieldName));
    }

    /**
     * 判断给定表达式是否仍是 `@Length` 声明层的默认空路径表达式。
     *
     * @param expr 表达式
     * @return 是否是待推断目标路径的默认 length 表达式
     */
    private static boolean isDefaultLengthLeft(final Expr expr) {
        return null != expr && ExprType.FUNCTION == expr.type() && null != expr.function() && "length".equals(expr.function().name())
            && 1 == expr.function().args().length && ExprType.PATH == expr.function().args()[0].type() && expr.function().args()[0].path().isEmpty();
    }

    /**
     * 按 `@Length` 的零配置规则推断目标路径。
     *
     * <p>
     * 当字段名以 `Length` 结尾且前缀非空时，去掉后缀并按 JavaBean 规则 decapitalize；
     * 否则直接返回原字段名。
     * </p>
     *
     * @param valueFieldName 当前绑定属性名
     * @return 推断后的目标路径
     */
    private static String inferLengthTargetPath(final String valueFieldName) {
        if (Objects.isNull(valueFieldName) || valueFieldName.isEmpty()) {
            return "";
        }
        if (!valueFieldName.endsWith("Length") || valueFieldName.length() <= "Length".length()) {
            return valueFieldName;
        }
        return Introspector.decapitalize(valueFieldName.substring(0, valueFieldName.length() - "Length".length()));
    }

}
