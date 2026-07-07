package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepNestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepNestedExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.In;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNull;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Length;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprFunction;

public class RuntimePredicateEvaluatorTest {

    @Test
    public void shouldEvaluateNestedRuntimeFunctions() throws Exception {
        Field field = RuntimeQueryModel.class.getDeclaredField("keyword");
        Equal annotation = field.getAnnotation(Equal.class);
        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, annotation, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        Object value = RuntimeExpressionEvaluator.evaluate(predicateSpec.getRight(), new RuntimeQueryModel(), "Demo");

        assertEquals("demo-x", value);
    }

    @Test
    public void shouldMatchRuntimeEqualityWithFunctionValue() throws Exception {
        Field field = RuntimeQueryModel.class.getDeclaredField("keyword");
        Equal annotation = field.getAnnotation(Equal.class);
        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, annotation, field);

        assertTrue(RuntimePredicateEvaluator.matches(spec, new RuntimeQueryModel(), " Demo "));
        assertFalse(RuntimePredicateEvaluator.matches(spec, new RuntimeQueryModel("Other", Arrays.asList(1, 5), Arrays.asList("PENDING"), null), "Other"));
    }

    @Test
    public void shouldMatchRuntimeBetweenAndInPredicates() throws Exception {
        RuntimeQueryModel args = new RuntimeQueryModel();

        Field betweenField = RuntimeQueryModel.class.getDeclaredField("scoreRange");
        Between between = betweenField.getAnnotation(Between.class);
        CompiledAnnotationSpec<Between> betweenSpec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, between, betweenField);
        assertTrue(RuntimePredicateEvaluator.matches(betweenSpec, args, Arrays.asList(1, 5)));
        assertFalse(RuntimePredicateEvaluator.matches(betweenSpec, args, Arrays.asList(5, 9)));

        Field inField = RuntimeQueryModel.class.getDeclaredField("statuses");
        In in = inField.getAnnotation(In.class);
        CompiledAnnotationSpec<In> inSpec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, in, inField);
        assertTrue(RuntimePredicateEvaluator.matches(inSpec, args, Arrays.asList("ACTIVE", "PENDING")));
        assertFalse(RuntimePredicateEvaluator.matches(inSpec, args, Arrays.asList("DISABLED")));
    }

    @Test
    public void shouldMatchRuntimeNullPredicate() throws Exception {
        Field field = RuntimeQueryModel.class.getDeclaredField("deletedAt");
        IsNull annotation = field.getAnnotation(IsNull.class);
        CompiledAnnotationSpec<IsNull> spec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, annotation, field);

        assertTrue(RuntimePredicateEvaluator.matches(spec, new RuntimeQueryModel(), null));
        assertFalse(RuntimePredicateEvaluator.matches(spec, new RuntimeQueryModel(), "2026-01-01"));
    }

    @Test
    public void shouldMatchRuntimePathToPathPredicate() throws Exception {
        Field field = RuntimeQueryModel.class.getDeclaredField("statusMirror");
        Equal annotation = field.getAnnotation(Equal.class);
        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, annotation, field);

        assertTrue(RuntimePredicateEvaluator.matches(spec, new RuntimeQueryModel(), "ACTIVE"));
        assertFalse(RuntimePredicateEvaluator.matches(spec,
            new RuntimeQueryModel(" Demo ", Arrays.asList(3, 7), Arrays.asList("ACTIVE", "PENDING"), null, "INACTIVE", "active", "ACTIVE", 6, "Length"),
            "INACTIVE"));
    }

    @Test
    public void shouldMatchRuntimeFunctionToFunctionPredicate() throws Exception {
        Field field = RuntimeQueryModel.class.getDeclaredField("normalizedStatus");
        Equal annotation = field.getAnnotation(Equal.class);
        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, annotation, field);

        assertTrue(RuntimePredicateEvaluator.matches(spec, new RuntimeQueryModel(), "ACTIVE"));
        assertFalse(RuntimePredicateEvaluator.matches(spec,
            new RuntimeQueryModel(" Demo ", Arrays.asList(3, 7), Arrays.asList("ACTIVE", "PENDING"), null, "ACTIVE", "disabled", "ACTIVE", 6, "Length"),
            "ACTIVE"));
    }

    @Test
    public void shouldMatchRuntimeLengthPredicate() throws Exception {
        Field field = RuntimeQueryModel.class.getDeclaredField("nameLength");
        Length annotation = field.getAnnotation(Length.class);
        CompiledAnnotationSpec<Length> spec = CompiledAnnotationSpec.of(RuntimeQueryModel.class, annotation, field);

        assertTrue(RuntimePredicateEvaluator.matches(spec, new RuntimeQueryModel(), Integer.valueOf(6)));
        assertFalse(RuntimePredicateEvaluator.matches(spec,
            new RuntimeQueryModel(" Demo ", Arrays.asList(3, 7), Arrays.asList("ACTIVE", "PENDING"), null, "ACTIVE", "active", "ACTIVE", 5, "Length"),
            Integer.valueOf(5)));
    }

    private static final class RuntimeQueryModel {

        @Equal(left = @Expr(type = ExprType.LITERAL, literal = "demo-x"),
            right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class,
                    args = {
                        @ExprArg(type = ExprType.FUNCTION,
                            function = @NestedExprFunction(name = "concat", type = String.class,
                                args = {
                                    @NestedExprArg(type = ExprType.FUNCTION,
                                        function = @DeepNestedExprFunction(name = "trim", type = String.class,
                                            args = { @DeepNestedExprArg(type = ExprType.VALUE) })),
                                    @NestedExprArg(type = ExprType.LITERAL, literal = "-x") })) })))
        private String keyword = " Demo ";

        @Between(left = @Expr(type = ExprType.LITERAL, literal = "4", javaType = Integer.class), right = @Expr(type = ExprType.VALUE))
        private List<Integer> scoreRange = Arrays.asList(3, 7);

        @In(left = @Expr(type = ExprType.LITERAL, literal = "ACTIVE"), right = @Expr(type = ExprType.VALUE))
        private List<String> statuses = Arrays.asList("ACTIVE", "PENDING");

        @IsNull(left = @Expr(type = ExprType.VALUE))
        private String deletedAt;

        @Equal(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.PATH, path = "statusMirror"))
        private String statusMirror = "ACTIVE";

        @Equal(
            left = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "status") })),
            right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "normalizedStatus") })))
        private String normalizedStatus = "active";

        @Length
        private Integer nameLength = Integer.valueOf(6);

        private String name = "Length";

        private RuntimeQueryModel() {
        }

        private RuntimeQueryModel(final String keyword, final List<Integer> scoreRange, final List<String> statuses, final String deletedAt) {
            this(keyword, scoreRange, statuses, deletedAt, "ACTIVE", "active", "ACTIVE", 6, "Length");
        }

        private RuntimeQueryModel(final String keyword, final List<Integer> scoreRange, final List<String> statuses, final String deletedAt,
            final String statusMirror, final String normalizedStatus, final String status, final Integer nameLength, final String name) {
            this.keyword = keyword;
            this.scoreRange = scoreRange;
            this.statuses = statuses;
            this.deletedAt = deletedAt;
            this.statusMirror = statusMirror;
            this.normalizedStatus = normalizedStatus;
            this.status = status;
            this.nameLength = nameLength;
            this.name = name;
        }

        private String status = "ACTIVE";

        @SuppressWarnings("unused")
        public String getKeyword() {
            return this.keyword;
        }

        @SuppressWarnings("unused")
        public List<Integer> getScoreRange() {
            return this.scoreRange;
        }

        @SuppressWarnings("unused")
        public List<String> getStatuses() {
            return this.statuses;
        }

        @SuppressWarnings("unused")
        public String getDeletedAt() {
            return this.deletedAt;
        }

        @SuppressWarnings("unused")
        public String getStatusMirror() {
            return this.statusMirror;
        }

        @SuppressWarnings("unused")
        public String getNormalizedStatus() {
            return this.normalizedStatus;
        }

        @SuppressWarnings("unused")
        public String getStatus() {
            return this.status;
        }

        @SuppressWarnings("unused")
        public Integer getNameLength() {
            return this.nameLength;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return this.name;
        }

    }

}
