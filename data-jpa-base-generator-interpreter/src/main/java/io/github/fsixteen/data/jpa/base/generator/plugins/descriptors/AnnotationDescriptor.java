package io.github.fsixteen.data.jpa.base.generator.plugins.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.FieldType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FieldProcessorFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Function;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ValueProcessorFunction;
import io.github.fsixteen.data.jpa.base.generator.plugins.exceptions.IntrospectionRuntimeException;

/**
 * 注解描述信息.
 *
 * @author FSixteen
 * @since 1.0.0
 */
public final class AnnotationDescriptor<A extends Annotation> {

    /** 实例类. */
    private Class<?> objClass;

    /** 注解. */
    private A anno;

    /** 参与计算字段名称. */
    private String computerFieldName;

    /** 参与计算值字段名称. */
    private String valueFieldName;

    /** 参与计算值字段. */
    private Field valueField;

    /** 参与计算值字段描述信息. */
    private PropertyDescriptor valueFieldPd;

    /**
     * -- JPA 操作注解类
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitIn
     * SplitIn} 及
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitNotIn
     * SplitNotIn} 中的通用内容. --
     */

    /**
     * 分割符.<br>
     */
    private String decollator;

    /**
     * -- JPA 操作注解类
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterNotIn
     * FilterIn} 及
     * {@linkplain io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterNotIn
     * FilterNotIn} 中的通用内容. --
     */

    /**
     * 正则表达式. 条件计算使用.
     */
    private String regexp;

    /**
     * {@linkplain java.util.function.Predicate Predicate} 的实现类.<br>
     */
    private Class<?> testClass;

    /**
     * {@linkplain java.util.function.Predicate Predicate} 的实现类完整名称.<br>
     */
    private String testClassName;

    /** ---------- JPA 操作注解类通用内容. ----------- */

    /**
     * 范围查询分组.<br>
     * 默认同在一组范围查询内.<br>
     */
    private String[] scope;

    /**
     * 条件查询分组, 默认独立组 {@code @GroupInfo("default", 0)}. <br>
     * 当 {@link #groups} 值大于 {@code 1} 组时, 该条件可以被多条件查询分组复用.
     */
    private GroupInfo[] groups;

    /**
     * 参与计算的最终字段. 不指定默认为当前参数字段.<br>
     */
    private String field;

    /**
     * 字段(列)参与计算方式, 默认为参数字段本身参与计算.<br>
     *
     * @since 1.0.2
     */
    private FieldType fieldType;

    /**
     * 字段(列)参与计算函数.<br>
     * 当且仅当 {@link #fieldType} = {@link FieldType#FUNCTION} 时有效.<br>
     *
     * @since 1.0.2
     */
    private Function fieldFunction;

    /**
     * 字段(列)参与计算自定义函数.<br>
     * 当且仅当 {@link #fieldType} = {@link FieldType#UDFUNCTION} 时有效.<br>
     *
     * @since 1.0.2
     */
    private FieldProcessorFunction fieldProcessor;

    /**
     * 参数字段指向的值类型, 默认为静态数值.<br>
     */
    private ValueType valueType;

    /**
     * 值函数.<br>
     * 当且仅当 {@link #valueType} = {@link ValueType#FUNCTION} 时有效.<br>
     * 
     * @since 1.0.2
     */
    private Function valueFunction;

    /**
     * 自定义值函数.<br>
     * 当且仅当 {@link #valueType} = {@link ValueType#UDFUNCTION} 时有效.<br>
     */
    private ValueProcessorFunction valueProcessor;

    /**
     * 参与计算方式.<br>
     * <br>
     * - 为<code>true</code>时, 任何时机均参与计算.<br>
     * <br>
     * - 为<code>false</code>时, 根据{@link #ignoreNull}, {@link #ignoreEmpty},
     * {@link #ignoreBlank}则机参与计算.<br>
     *
     * @return boolean
     */
    private boolean required = false;

    /**
     * 逻辑反向.<br>
     */
    private boolean not = false;

    /**
     * 忽略空值.<br>
     * 当元素为集合时, 判断每个元素, 忽略空值.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     */
    private boolean ignoreNull = true;

    /**
     * 忽略空字符串值.<br>
     * 当元素为集合, 判断每个元素, 忽略空字符串值.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     */
    private boolean ignoreEmpty = true;

    /**
     * 忽略空白字符值.<br>
     * 当元素为集合, 判断每个元素, 忽略空白字符值.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     */
    private boolean ignoreBlank = true;

    /**
     * 删除所有前导和尾随空白字符.<br>
     * 当元素为集合, 判断每个元素, 删除所有前导和尾随空白字符.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     */
    private boolean trim = true;

    public static <T, A extends Annotation> AnnotationDescriptor<A> of(final Class<T> objClass, final A anno) {
        return of(objClass, anno, null);
    }

    public static <T, A extends Annotation> AnnotationDescriptor<A> of(final Class<T> objClass, final A anno, final Field valueField) {
        return new AnnotationDescriptor<A>(objClass, anno, valueField);
    }

    private AnnotationDescriptor(final Class<?> objClass, final A anno) {
        this(objClass, anno, null);
    }

    private AnnotationDescriptor(final Class<?> objClass, final A anno, final Field valueField) {
        super();
        this.objClass = objClass;
        this.anno = anno;

        this.decollator = this.stringFieldValue(anno, "decollator");
        this.regexp = this.stringFieldValue(anno, "regexp");
        this.testClass = this.fieldValue(anno, "testClass");
        this.testClassName = this.stringFieldValue(anno, "testClassName");
        this.scope = this.fieldValue(anno, "scope");
        this.groups = this.fieldValue(anno, "groups");
        this.field = this.stringFieldValue(anno, "field");
        this.fieldType = this.fieldValue(anno, "fieldType");
        this.fieldFunction = this.fieldValue(anno, "fieldFunction");
        this.fieldProcessor = this.fieldValue(anno, "fieldProcessor");
        this.valueType = this.fieldValue(anno, "valueType");
        this.valueFunction = this.fieldValue(anno, "valueFunction");
        this.valueProcessor = this.fieldValue(anno, "valueProcessor");
        this.required = this.booleanFieldValue(anno, "required");
        this.not = this.booleanFieldValue(anno, "not");
        this.ignoreNull = this.booleanFieldValue(anno, "ignoreNull");
        this.ignoreEmpty = this.booleanFieldValue(anno, "ignoreEmpty");
        this.ignoreBlank = this.booleanFieldValue(anno, "ignoreBlank");
        this.trim = this.booleanFieldValue(anno, "trim");

        this.valueField = valueField;
        this.valueFieldName = Objects.nonNull(this.valueField) ? this.valueField.getName() : null;
        try {
            /**
             * 可爱又可笑的修改, 用于解决多版本编译, 报
             * <code>
             * cannot access com.sun.beans.introspect.PropertyInfo
             * class file for com.sun.beans.introspect.PropertyInfo not found
             * </code>
             * 错误问题.
             * jdk8与jdk9(含)以上, java.beans.PropertyDescriptor 构造函数发生变化所致.
             */
            this.valueFieldPd = Objects.nonNull(this.valueFieldName) ? new PropertyDescriptor((String) this.valueFieldName, objClass) : null;
        } catch (IntrospectionException e) {
            throw new IntrospectionRuntimeException(e);
        }
        this.computerFieldName = Objects.nonNull(this.field) && !this.field.isEmpty() ? this.field : this.valueFieldName;
    }

    @SuppressWarnings("unchecked")
    private <T> T fieldValue(final A anno, final String fieldName) {
        try {
            return (T) anno.annotationType().getMethod(fieldName).invoke(anno);
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return null;
        }
    }

    private String stringFieldValue(final A anno, final String fieldName) {
        return Optional.ofNullable(this.fieldValue(anno, fieldName)).filter(String.class::isInstance).map(String.class::cast).orElseGet(() -> null);
    }

    private boolean booleanFieldValue(final A anno, final String fieldName) {
        return Optional.ofNullable(this.fieldValue(anno, fieldName)).filter(Boolean.class::isInstance).map(Boolean.class::cast).orElseGet(() -> false);
    }

    /**
     * 实例类.
     * 
     * @return Class&lt;?&gt;
     */
    public Class<?> getObjClass() {
        return objClass;
    }

    /**
     * 注解.
     * 
     * @return A
     */
    public A getAnno() {
        return anno;
    }

    /**
     * 参与计算字段名称.
     * 
     * @return String
     */
    public String getComputerFieldName() {
        return computerFieldName;
    }

    /**
     * 参与计算值字段名称.
     * 
     * @return String
     */
    public String getValueFieldName() {
        return valueFieldName;
    }

    /**
     * 参与计算值字段.
     * 
     * @return Field
     */
    public Field getValueField() {
        return valueField;
    }

    /**
     * 参与计算值字段描述信息.
     * 
     * @return PropertyDescriptor
     */
    public PropertyDescriptor getValueFieldPd() {
        return valueFieldPd;
    }

    /**
     * 分割符.
     * 
     * @return String
     */
    public String getDecollator() {
        return decollator;
    }

    /**
     * 正则表达式. 条件计算使用.<br>
     * 存在 {@link #getRegexp()} 时, 以 {@link #getRegexp()} 为主, 不存在
     * {@link #getRegexp()} 时, 尝试 {@link #getTestClass()}.<br>
     * 
     * @return 正则表达式
     */
    public String getRegexp() {
        return regexp;
    }

    /**
     * {@linkplain java.util.function.Predicate Predicate} 的实现类.<br>
     * 
     * @return Class&lt;?&gt;
     */
    public Class<?> getTestClass() {
        return testClass;
    }

    /**
     * {@linkplain java.util.function.Predicate Predicate} 的实现类完整名称.<br>
     * 
     * @return String
     */
    public String getTestClassName() {
        return testClassName;
    }

    /**
     * 范围查询分组.<br>
     * 默认同在一组范围查询内.<br>
     * 
     * @return String[]
     */
    public String[] getScope() {
        return scope;
    }

    /**
     * 条件查询分组, 默认独立组 {@code @GroupInfo("default", 0)}. <br>
     * 当 {@link #groups} 值大于 {@code 1} 组时, 该条件可以被多条件查询分组复用.
     *
     * @return GroupInfo[]
     */
    public GroupInfo[] getGroups() {
        return groups;
    }

    /**
     * 参与计算的最终字段. 不指定默认为当前参数字段.<br>
     *
     * @return String
     */
    public String getField() {
        return field;
    }

    /**
     * 字段(列)参与计算方式, 默认为参数字段本身参与计算.<br>
     * 
     * @since 1.0.2
     * @return FieldType
     */
    public FieldType getFieldType() {
        return fieldType;
    }

    /**
     * 字段(列)参与计算函数.<br>
     * 当且仅当 {@link #fieldType} = {@link FieldType#FUNCTION} 时有效.<br>
     * 
     * @since 1.0.2
     * @return Function
     */
    public Function getFieldFunction() {
        return fieldFunction;
    }

    /**
     * 字段(列)参与计算自定义函数.<br>
     * 当且仅当 {@link #fieldType} = {@link FieldType#UDFUNCTION} 时有效.<br>
     * 
     * @since 1.0.2
     * @return FieldProcessorFunction
     */
    public FieldProcessorFunction getFieldProcessor() {
        return fieldProcessor;
    }

    /**
     * 参数字段指向的值类型, 默认为静态数值.<br>
     *
     * @return ValueType
     */
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * 值函数.<br>
     * 当且仅当 {@link #valueType} = {@link ValueType#FUNCTION} 时有效.<br>
     * 
     * @since 1.0.2
     * @return Function
     */
    public Function getValueFunction() {
        return valueFunction;
    }

    /**
     * 自定义值函数.<br>
     * 当且仅当 {@link #valueType} = {@link ValueType#UDFUNCTION} 时有效.<br>
     *
     * @return Function
     */
    public ValueProcessorFunction getValueProcessor() {
        return valueProcessor;
    }

    /**
     * 参与计算方式.<br>
     * <br>
     * - 为<code>true</code>时, 任何时机均参与计算.<br>
     * <br>
     * - 为<code>false</code>时, 根据{@link #ignoreNull}, {@link #ignoreEmpty},
     * {@link #ignoreBlank}则机参与计算.<br>
     *
     * @return boolean
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * 逻辑反向.<br>
     *
     * @return boolean
     */
    public boolean isNot() {
        return not;
    }

    /**
     * 忽略空值.<br>
     * 当元素为集合时, 判断每个元素, 忽略空值.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     *
     * @return boolean
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * 忽略空字符串值.<br>
     * 当元素为集合, 判断每个元素, 忽略空字符串值.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     *
     * @return boolean
     */
    public boolean isIgnoreEmpty() {
        return ignoreEmpty;
    }

    /**
     * 忽略空白字符值.<br>
     * 当元素为集合, 判断每个元素, 忽略空白字符值.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     *
     * @return boolean
     */
    public boolean isIgnoreBlank() {
        return ignoreBlank;
    }

    /**
     * 删除所有前导和尾随空白字符.<br>
     * 当元素为集合, 判断每个元素, 删除所有前导和尾随空白字符.<br>
     * 当且仅当 {@link #required} = {@link Boolean#FALSE} 时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为 {@link java.lang.String} 或数组/集合元素类型为
     * {@link java.lang.String} 时有效.<br>
     *
     * @return boolean
     */
    public boolean isTrim() {
        return trim;
    }

}
