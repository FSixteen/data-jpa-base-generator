package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * tuple-value 谓词的 compiled 规格。
 *
 * <p>
 * 该类型连接“标准注解宿主规格”和“tuple 列映射规格”。宿主字段仍通过
 * {@link CompiledAnnotationSpec} 统一承接 options、bindingPath 与运行时值读取；tuple
 * 特有的 {@code tupleField / negate / columns} 则在这里补齐。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledTuplePredicateSpec {

    private final CompiledAnnotationSpec<? extends Annotation> ownerSpec;

    private final String tupleField;

    private final boolean negate;

    private final List<CompiledTupleColumnSpec> columns;

    private CompiledTuplePredicateSpec(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final String tupleField, final boolean negate,
        final List<CompiledTupleColumnSpec> columns) {
        this.ownerSpec = ownerSpec;
        this.tupleField = null == tupleField ? "" : tupleField;
        this.negate = negate;
        this.columns = Collections.unmodifiableList(new ArrayList<CompiledTupleColumnSpec>(columns));
    }

    public static CompiledTuplePredicateSpec of(final CompiledAnnotationSpec<? extends Annotation> ownerSpec, final String tupleField, final boolean negate,
        final List<CompiledTupleColumnSpec> columns) {
        return new CompiledTuplePredicateSpec(ownerSpec, tupleField, negate, columns);
    }

    public CompiledAnnotationSpec<? extends Annotation> getOwnerSpec() {
        return this.ownerSpec;
    }

    public String getTupleField() {
        return this.tupleField;
    }

    public boolean isNegate() {
        return this.negate;
    }

    public List<CompiledTupleColumnSpec> getColumns() {
        return this.columns;
    }

    public boolean usesHostFieldAsTupleSource() {
        return this.tupleField.isEmpty();
    }

}
