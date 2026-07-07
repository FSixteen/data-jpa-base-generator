package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleColumn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleInValues;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleNotInValues;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.MetaAnnotationAttributes;

/**
 * tuple-value 注解的 compiled spec 构造入口.
 *
 * <p>
 * 它负责把原始注解或组合注解上的 tuple 字段读取出来, 校验 DSL 结构后转换为稳定的
 * {@link CompiledTuplePredicateSpec}. 运行时值读取与 Predicate 拼装不在这里完成.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTupleValueSpecs {

    private CompiledTupleValueSpecs() {
    }

    public static CompiledTuplePredicateSpec tuplePredicate(final CompiledAnnotationSpec<? extends Annotation> ownerSpec) {
        Annotation annotation = ownerSpec.getAnnotation();
        boolean negate = annotation instanceof TupleNotInValues;
        String tupleField = tupleField(annotation);
        TupleColumn[] tupleColumns = tupleColumns(annotation);
        if (null == tupleColumns || 0 == tupleColumns.length) {
            throw new IllegalArgumentException(ownerSpec.getAnnotationType().getSimpleName() + " requires non-empty columns()");
        }
        List<CompiledTupleColumnSpec> columns = new ArrayList<CompiledTupleColumnSpec>(tupleColumns.length);
        String owner = "@" + ownerSpec.getAnnotationType().getSimpleName() + "(" + ownerSpec.getValueFieldName() + ")";
        for (TupleColumn column : tupleColumns) {
            CompiledTupleColumnSpec compiledColumn = CompiledTupleColumnSpec.of(column.leftPath(), column.itemPath(), column.itemIndex(), column.targetType(),
                column.targetFormat());
            compiledColumn.validate(owner);
            columns.add(compiledColumn);
        }
        return CompiledTuplePredicateSpec.of(ownerSpec, tupleField, negate, columns);
    }

    private static String tupleField(final Annotation annotation) {
        String tupleField = MetaAnnotationAttributes.fieldValue(annotation, "tupleField", String.class);
        return null == tupleField ? "" : tupleField;
    }

    private static TupleColumn[] tupleColumns(final Annotation annotation) {
        if (annotation instanceof TupleInValues) {
            return TupleInValues.class.cast(annotation).columns();
        }
        if (annotation instanceof TupleNotInValues) {
            return TupleNotInValues.class.cast(annotation).columns();
        }
        TupleColumn[] columns = MetaAnnotationAttributes.fieldValue(annotation, "columns", TupleColumn[].class);
        return Objects.isNull(columns) ? new TupleColumn[0] : columns;
    }

}
