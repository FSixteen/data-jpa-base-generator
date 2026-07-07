package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionCardinality;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.LiteralExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

public class RuntimeExpressionEvaluatorTest {

    @Test
    public void shouldEvaluatePathAndFieldValueExpressions() {
        RuntimeArgs args = new RuntimeArgs();

        assertEquals("ACTIVE", RuntimeExpressionEvaluator.evaluate(PathExpression.of("status"), args, null));
        assertEquals("Demo", RuntimeExpressionEvaluator.evaluate(FieldValueExpression.literal(), args, "Demo"));
        assertEquals("ACTIVE", RuntimeExpressionEvaluator.evaluate(FieldValueExpression.literal("status"), args, null));
        assertEquals("audit.currentStatus", RuntimeExpressionEvaluator.evaluate(FieldValueExpression.path("statusPath"), args, null));
    }

    @Test
    public void shouldEvaluateLiteralExpressionsByCardinality() {
        assertEquals(7, RuntimeExpressionEvaluator.evaluate(LiteralExpression.of("7", Integer.class), null, null));
        assertIterableEquals(Arrays.asList("A", "B"),
            (List<?>) RuntimeExpressionEvaluator.evaluate(LiteralExpression.of("A,B", String.class, ExpressionCardinality.COLLECTION), null, null));
        assertIterableEquals(Arrays.asList(1, 5), RuntimeExpressionEvaluator.evaluateRange(
            LiteralExpression.of("1,5", Integer.class, ExpressionCardinality.RANGE), java.util.Collections.<PredicateExpression>emptyList(), null, null));
    }

    @Test
    public void shouldEvaluateNestedRuntimeFunctionsAndRangeExtraOperand() {
        RuntimeArgs args = new RuntimeArgs();
        FunctionExpression expression = FunctionExpression.of("lower", String.class,
            Arrays.<PredicateExpression>asList(FunctionExpression.of("concat", String.class,
                Arrays.<PredicateExpression>asList(
                    FunctionExpression.of("trim", String.class, Arrays.<PredicateExpression>asList(FieldValueExpression.literal())),
                    LiteralExpression.of("-x", String.class)))));

        Object value = RuntimeExpressionEvaluator.evaluate(expression, args, " Demo ");
        List<Object> range = RuntimeExpressionEvaluator.evaluateRange(FieldValueExpression.literal("scoreRange"),
            Arrays.<PredicateExpression>asList(LiteralExpression.of("9", Integer.class)), args, null);

        assertEquals("demo-x", value);
        assertIterableEquals(Arrays.asList(3, 7, 9), range);
    }

    @Test
    public void shouldEvaluateLengthFunction() {
        FunctionExpression expression = FunctionExpression.of("length", Integer.class, Arrays.<
            PredicateExpression>asList(FunctionExpression.of("trim", String.class, Arrays.<PredicateExpression>asList(FieldValueExpression.literal()))));

        assertEquals(4, RuntimeExpressionEvaluator.evaluate(expression, null, " Demo "));
        assertNull(RuntimeExpressionEvaluator.evaluate(expression, null, null));
    }

    @Test
    public void shouldRejectLengthFunctionForNonStringArgument() {
        FunctionExpression expression = FunctionExpression.of("length", Integer.class, Arrays.<PredicateExpression>asList(FieldValueExpression.literal()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> RuntimeExpressionEvaluator.evaluate(expression, null, 42));

        assertEquals("Function length(...) only supports CharSequence arguments, but got: java.lang.Integer", exception.getMessage());
    }

    @Test
    public void shouldEvaluateCoalesceFunction() {
        RuntimeArgs args = new RuntimeArgs();
        FunctionExpression expression = FunctionExpression.of("coalesce", String.class, Arrays.<PredicateExpression>asList(
            FieldValueExpression.literal("nullable"), FieldValueExpression.literal("status"), LiteralExpression.of("FALLBACK", String.class)));

        assertEquals("ACTIVE", RuntimeExpressionEvaluator.evaluate(expression, args, null));
    }

    private static final class RuntimeArgs {

        private final String status = "ACTIVE";

        private final String statusPath = "audit.currentStatus";

        private final List<Integer> scoreRange = Arrays.asList(3, 7);

        private final String nullable = null;

        @SuppressWarnings("unused")
        public String getStatus() {
            return this.status;
        }

        @SuppressWarnings("unused")
        public String getStatusPath() {
            return this.statusPath;
        }

        @SuppressWarnings("unused")
        public List<Integer> getScoreRange() {
            return this.scoreRange;
        }

        @SuppressWarnings("unused")
        public String getNullable() {
            return this.nullable;
        }

    }

}
