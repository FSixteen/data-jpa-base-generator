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
 * @since V1.0.0
 */
public final class AnnotationDescriptor<AN extends Annotation> {
    private static final String FIELD = "field";

    /**
     * 实例类.
     */
    private Class<?> objClass;

    /**
     * 注解.
     */
    private AN anno;

    /**
     * 参与计算字段名称.
     */
    private String computerFieldName;

    /**
     * 参与计算字段.
     */
    private Field computerField;

    /**
     * 参与计算字段描述信息.
     */
    private PropertyDescriptor computerFieldPd;

    /**
     * 参与计算值字段名称.
     */
    private String valueFieldName;

    /**
     * 参与计算值字段.
     */
    private Field valueField;

    /**
     * 参与计算值字段描述信息.
     */
    private PropertyDescriptor valueFieldPd;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#scope
     */
    private String[] scope;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#groups
     */
    private GroupInfo[] groups;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#field
     */
    private String field;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueType
     */
    private ValueType valueType;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueProcessor
     */
    private ValueProcessorFunction valueProcessor;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#required
     */
    private boolean required;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#not
     */
    private boolean not = false;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreNull
     */
    private boolean ignoreNull = true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreEmpty
     */
    private boolean ignoreEmpty = true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreBlank
     */
    private boolean ignoreBlank = true;

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#trim
     */
    private boolean trim = true;

    public static <T, AN extends Annotation> AnnotationDescriptor<AN> of(final Class<T> objClass, final AN anno)
            throws IntrospectionException, NoSuchFieldException, SecurityException {
        return of(objClass, anno, null);
    }

    public static <T, AN extends Annotation> AnnotationDescriptor<AN> of(final Class<T> objClass, final AN anno, final Field valueField) {
        return new AnnotationDescriptor<AN>(objClass, anno, valueField);
    }

    private AnnotationDescriptor(final Class<?> objClass, final AN anno) {
        this(objClass, anno, null);
    }

    private AnnotationDescriptor(final Class<?> objClass, final AN anno, final Field valueField) {
        super();
        this.objClass = objClass;
        this.anno = anno;
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
            // 可爱又可笑的修改, 用于解决多版本编译, 报'cannot access com.sun.beans.introspect.PropertyInfo'
            // 'class file for com.sun.beans.introspect.PropertyInfo not found'错误问题.
            // jdk8与jdk9(含)以上, java.beans.PropertyDescriptor 构造函数发生变化所致.
            this.valueFieldPd = new PropertyDescriptor((String) this.valueFieldName, objClass);
        } catch (IntrospectionException e) {
            throw new IntrospectionRuntimeException(e);
        }

        this.computerFieldName = Optional.ofNullable(this.field).filter(it -> !it.isEmpty()).orElseGet(() -> this.valueFieldName);
        this.computerField = this.reflectField(objClass, this.computerFieldName);
        try {
            // 可爱又可笑的修改, 用于解决多版本编译, 报'cannot access com.sun.beans.introspect.PropertyInfo'
            // 'class file for com.sun.beans.introspect.PropertyInfo not found'错误问题.
            // jdk8与jdk9(含)以上, java.beans.PropertyDescriptor 构造函数发生变化所致.
            this.computerFieldPd = new PropertyDescriptor((String) this.computerFieldName, objClass);
        } catch (IntrospectionException e) {
            throw new IntrospectionRuntimeException(e);
        }
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
    private <T> T fieldValue(final AN anno, final String fieldName) {
        try {
            return (T) anno.annotationType().getMethod(fieldName).invoke(anno);
        } catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            return null;
        }
    }

    private String stringFieldValue(final AN anno, final String fieldName) {
        return Optional.ofNullable(this.fieldValue(anno, fieldName)).filter(String.class::isInstance).map(String.class::cast).orElseGet(() -> null);
    }

    private boolean booleanFieldValue(final AN anno, final String fieldName) {
        return Optional.ofNullable(this.fieldValue(anno, fieldName)).filter(Boolean.class::isInstance).map(Boolean.class::cast).orElseGet(() -> false);
    }

    /**
     * 实例类.
     */
    public Class<?> getObjClass() {
        return objClass;
    }

    /**
     * 注解.
     */
    public AN getAnno() {
        return anno;
    }

    /**
     * 参与计算字段名称.
     */
    public String getComputerFieldName() {
        return computerFieldName;
    }

    /**
     * 参与计算字段.
     */
    public Field getComputerField() {
        return computerField;
    }

    /**
     * 参与计算字段描述信息.
     */
    public PropertyDescriptor getComputerFieldPd() {
        return computerFieldPd;
    }

    /**
     * 参与计算值字段名称.
     */
    public String getValueFieldName() {
        return valueFieldName;
    }

    /**
     * 参与计算值字段.
     */
    public Field getValueField() {
        return valueField;
    }

    /**
     * 参与计算值字段描述信息.
     */
    public PropertyDescriptor getValueFieldPd() {
        return valueFieldPd;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#scope
     */
    public String[] getScope() {
        return scope;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#groups
     */
    public GroupInfo[] getGroups() {
        return groups;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#field
     */
    public String getField() {
        return field;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueType
     */
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#valueProcessor
     */
    public ValueProcessorFunction getValueProcessor() {
        return valueProcessor;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#not
     */
    public boolean isNot() {
        return not;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreNull
     */
    public boolean isIgnoreNull() {
        return ignoreNull;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreEmpty
     */
    public boolean isIgnoreEmpty() {
        return ignoreEmpty;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#ignoreBlank
     */
    public boolean isIgnoreBlank() {
        return ignoreBlank;
    }

    /**
     * @see io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal#trim
     */
    public boolean isTrim() {
        return trim;
    }

}
