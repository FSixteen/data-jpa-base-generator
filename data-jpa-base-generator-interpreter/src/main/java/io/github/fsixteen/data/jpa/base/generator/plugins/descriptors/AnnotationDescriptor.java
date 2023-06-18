package io.github.fsixteen.data.jpa.base.generator.plugins.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ValueType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ValueProcessorFunction;
import io.github.fsixteen.data.jpa.base.generator.plugins.exceptions.IntrospectionRuntimeException;

/**
 * 注解描述信息.
 *
 * @author FSixteen
 * @since 1.0.0
 */
public final class AnnotationDescriptor<A extends Annotation> {

    private static final String FIELD = "field";

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
     * 正则表达式. 条件计算使用.
     */
    private String regexp;

    /**
     * Predicate的实现类.
     */
    private Class<?> testClass;

    /**
     * 参与条件计算的字段. 不指定默认为 {@link #field()} 参数字段.<br>
     */
    private String testField;

    /**
     * 范围查询分组.<br>
     * 默认同在一组范围查询内.<br>
     */
    private String[] scope;

    /**
     * 条件查询分组, 默认独立组<code>@GroupInfo("default", 0)</code>. <br>
     * 当<code>groups</code>值大于<code>1</code>组时, 该条件可以被多条件查询分组复用.
     */
    private GroupInfo[] groups;

    /**
     * 参与计算的最终字段. 不指定默认为当前参数字段.<br>
     */
    private String field;

    /**
     * 参数字段指向的值类型, 默认为静态数值.<br>
     */
    private ValueType valueType;

    /**
     * 值函数.<br>
     * 当且仅当<code>valueType = ValueType.FUNCTION</code>时有效.<br>
     */
    private ValueProcessorFunction valueProcessor;

    /**
     * 参与计算方式.<br>
     * <br>
     * - 为<code>true</code>时, 任何时机均参与计算.<br>
     * <br>
     * - 为<code>false</code>时, 根据{@link #ignoreNull()}, {@link #ignoreEmpty()},
     * {@link
     * #ignoreBlank()}则机参与计算.<br>
     */
    private boolean required;

    /**
     * 逻辑反向.<br>
     */
    private boolean not = false;

    /**
     * 忽略空值.<br>
     * 当元素为集合, 判断每个元素, 忽略空值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     */
    private boolean ignoreNull = true;

    /**
     * 忽略空字符串值.<br>
     * 当元素为集合, 判断每个元素, 忽略空字符串值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     */
    private boolean ignoreEmpty = true;

    /**
     * 忽略空白字符值.<br>
     * 当元素为集合, 判断每个元素, 忽略空白字符值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     */
    private boolean ignoreBlank = true;

    /**
     * 删除所有前导和尾随空白字符.<br>
     * 当元素为集合, 判断每个元素, 删除所有前导和尾随空白字符.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     */
    private boolean trim = true;

    public static <T, A extends Annotation> AnnotationDescriptor<A> of(final Class<T> objClass, final A anno)
        throws IntrospectionException, NoSuchFieldException, SecurityException {
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
        this.regexp = this.stringFieldValue(anno, "regexp");
        this.testClass = this.fieldValue(anno, "testClass");
        this.testField = this.stringFieldValue(anno, "testField");
        this.scope = this.fieldValue(anno, "scope");
        this.groups = this.fieldValue(anno, "groups");
        this.field = this.stringFieldValue(anno, FIELD);
        this.valueType = this.fieldValue(anno, "valueType");
        this.valueProcessor = this.fieldValue(anno, "valueProcessor");
        this.required = this.booleanFieldValue(anno, "required");
        this.not = this.booleanFieldValue(anno, "not");
        this.ignoreNull = this.booleanFieldValue(anno, "ignoreNull");
        this.ignoreEmpty = this.booleanFieldValue(anno, "ignoreEmpty");
        this.ignoreBlank = this.booleanFieldValue(anno, "ignoreBlank");
        this.trim = this.booleanFieldValue(anno, "trim");

        this.valueField = Optional.ofNullable(valueField).orElseGet(() -> this.reflectField(objClass, FIELD));
        this.valueFieldName = this.valueField.getName();
        try {
            // 可爱又可笑的修改, 用于解决多版本编译, 报'cannot access
            // com.sun.beans.introspect.PropertyInfo'
            // 'class file for com.sun.beans.introspect.PropertyInfo not
            // found'错误问题.
            // jdk8与jdk9(含)以上, java.beans.PropertyDescriptor 构造函数发生变化所致.
            this.valueFieldPd = new PropertyDescriptor((String) this.valueFieldName, objClass);
        } catch (IntrospectionException e) {
            throw new IntrospectionRuntimeException(e);
        }

        this.computerFieldName = Optional.ofNullable(this.field).filter(it -> !it.isEmpty()).orElseGet(() -> this.valueFieldName);
    }

    private Field reflectField(final Class<?> objClass, final String fieldName) {
        try {
            return objClass.getField(fieldName);
        } catch (ReflectiveOperationException | SecurityException e) {
            return this.reflectDeclaredField(objClass, fieldName);
        }
    }

    private Field reflectDeclaredField(final Class<?> objClass, final String fieldName) {
        try {
            return objClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException | SecurityException e) {
            if (Objects.nonNull(objClass.getSuperclass())) {
                return this.reflectDeclaredField(objClass.getSuperclass(), fieldName);
            }
            return null;
        }
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
     * 正则表达式.<br>
     * 存在 {@link #regexp()} 时, 以 {@link #regexp()} 为主, 不存在 {@link #regexp()} 时,
     * 尝试 {@link #testClass()}.<br>
     * 
     * @return 正则表达式
     */
    public String getRegexp() {
        return regexp;
    }

    /**
     * Predicate的实现类.<br>
     * 
     * @return Class&lt;?&gt;
     */
    public Class<?> getTestClass() {
        return testClass;
    }

    /**
     * 参与条件计算的字段. 不指定默认为 {@link #field()} 参数字段.<br>
     *
     * @return String
     */
    public String getTestField() {
        return testField;
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
     * 条件查询分组, 默认独立组<code>@GroupInfo("default", 0)</code>. <br>
     * 当<code>groups</code>值大于<code>1</code>组时, 该条件可以被多条件查询分组复用.
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
     * 参数字段指向的值类型, 默认为静态数值.<br>
     *
     * @return ValueType
     */
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * 值函数.<br>
     * 当且仅当<code>valueType = ValueType.FUNCTION</code>时有效.<br>
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
     * - 为<code>false</code>时, 根据{@link #isIgnoreNull()},
     * {@link #isIgnoreEmpty()},
     * {@link #isIgnoreBlank()}则机参与计算.<br>
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
     * 当元素为集合, 判断每个元素, 忽略空值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     *
     * @return boolean
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * 忽略空字符串值.<br>
     * 当元素为集合, 判断每个元素, 忽略空字符串值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     *
     * @return boolean
     */
    public boolean isIgnoreEmpty() {
        return ignoreEmpty;
    }

    /**
     * 忽略空白字符值.<br>
     * 当元素为集合, 判断每个元素, 忽略空白字符值.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     *
     * @return boolean
     */
    public boolean isIgnoreBlank() {
        return ignoreBlank;
    }

    /**
     * 删除所有前导和尾随空白字符.<br>
     * 当元素为集合, 判断每个元素, 删除所有前导和尾随空白字符.<br>
     * 当且仅当<code>required = false</code>时有效.<br>
     * 当且仅当参与计算值类型或函数返回值类型为{@code java.lang.String}时有效.<br>
     *
     * @return boolean
     */
    public boolean isTrim() {
        return trim;
    }

}
