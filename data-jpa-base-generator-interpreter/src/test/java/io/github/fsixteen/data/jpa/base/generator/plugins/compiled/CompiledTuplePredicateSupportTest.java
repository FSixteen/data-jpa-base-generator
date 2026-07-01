package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleColumn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleInValues;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleNotInValues;

public class CompiledTuplePredicateSupportTest {

    @Test
    public void shouldCompileTupleInValuesAnnotationToDedicatedSpec() throws Exception {
        Field field = TupleQueryModel.class.getDeclaredField("pairs");
        CompiledAnnotationSpec<TupleInValues> ownerSpec = CompiledAnnotationSpec.of(TupleQueryModel.class, field.getAnnotation(TupleInValues.class), field);

        CompiledTuplePredicateSpec tupleSpec = CompiledTupleValueSpecs.tuplePredicate(ownerSpec);

        assertTrue(tupleSpec.usesHostFieldAsTupleSource());
        assertFalse(tupleSpec.isNegate());
        assertEquals(2, tupleSpec.getColumns().size());
        assertEquals("a", tupleSpec.getColumns().get(0).getLeftPath());
        assertEquals("left.value", tupleSpec.getColumns().get(0).getItemPath());
    }

    @Test
    public void shouldBuildTupleInValuesPredicateFromObjectRows() throws Exception {
        Field field = TupleQueryModel.class.getDeclaredField("pairs");
        CompiledAnnotationSpec<TupleInValues> ownerSpec = CompiledAnnotationSpec.of(TupleQueryModel.class, field.getAnnotation(TupleInValues.class), field);

        Predicate predicate = CompiledTuplePredicateSupport.create(ownerSpec, new TupleQueryModel(), proxy(Root.class, "root"), null,
            proxy(CriteriaBuilder.class, "cb"));

        assertEquals("root.a = 1 AND root.b = 2 OR root.a = 3 AND root.b = 4", debug(predicate));
    }

    @Test
    public void shouldBuildTupleInValuesPredicateFromPrimitiveArrayRows() throws Exception {
        Field field = PrimitiveTupleQueryModel.class.getDeclaredField("pairs");
        CompiledAnnotationSpec<
            TupleInValues> ownerSpec = CompiledAnnotationSpec.of(PrimitiveTupleQueryModel.class, field.getAnnotation(TupleInValues.class), field);

        Predicate predicate = CompiledTuplePredicateSupport.create(ownerSpec, new PrimitiveTupleQueryModel(), proxy(Root.class, "root"), null,
            proxy(CriteriaBuilder.class, "cb"));

        assertEquals("root.a = 1 AND root.b = 2 OR root.a = 5 AND root.b = 6", debug(predicate));
    }

    @Test
    public void shouldUseTupleFieldAsExternalSourceAndRespectTriggerField() throws Exception {
        Field field = TriggerTupleQueryModel.class.getDeclaredField("enabled");
        CompiledAnnotationSpec<
            TupleInValues> ownerSpec = CompiledAnnotationSpec.of(TriggerTupleQueryModel.class, field.getAnnotation(TupleInValues.class), field);

        Predicate enabledPredicate = CompiledTuplePredicateSupport.create(ownerSpec, new TriggerTupleQueryModel(), proxy(Root.class, "root"), null,
            proxy(CriteriaBuilder.class, "cb"));
        Predicate disabledPredicate = CompiledTuplePredicateSupport.create(ownerSpec, new TriggerTupleQueryModel(Boolean.FALSE), proxy(Root.class, "root"),
            null, proxy(CriteriaBuilder.class, "cb"));

        assertEquals("root.a = 7 AND root.b = 8 OR root.a = 9 AND root.b = 10", debug(enabledPredicate));
        assertNull(disabledPredicate);
    }

    @Test
    public void shouldNegateTupleNotInValuesAsWholePredicate() throws Exception {
        Field field = NegativeTupleQueryModel.class.getDeclaredField("pairs");
        CompiledAnnotationSpec<
            TupleNotInValues> ownerSpec = CompiledAnnotationSpec.of(NegativeTupleQueryModel.class, field.getAnnotation(TupleNotInValues.class), field);

        Predicate predicate = CompiledTuplePredicateSupport.create(ownerSpec, new NegativeTupleQueryModel(), proxy(Root.class, "root"), null,
            proxy(CriteriaBuilder.class, "cb"));

        assertEquals("NOT (root.a = 11 AND root.b = 12 OR root.a = 13 AND root.b = 14)", debug(predicate));
    }

    @Test
    public void shouldConvertTupleRawValueToConfiguredTargetType() {
        assertEquals(Long.class, TupleValueConverter.convert("7", Long.class, "").getClass());
        assertEquals(Integer.valueOf(9), TupleValueConverter.convert(Long.valueOf(9L), Integer.class, ""));
    }

    @Test
    public void shouldResolveProviderForTupleAnnotations() {
        assertTrue(io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry.containsKey(TupleInValues.class));
        assertTrue(io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry.containsKey(TupleNotInValues.class));
    }

    private static String debug(final Object proxy) {
        return ((DebugNode) proxy).debug();
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(final Class<T> type, final String debug) {
        Class<?>[] interfaces;
        if (Root.class == type) {
            interfaces = new Class<?>[] { Root.class, DebugNode.class };
        } else if (AbstractQuery.class == type) {
            interfaces = new Class<?>[] { AbstractQuery.class, DebugNode.class };
        } else if (Path.class == type) {
            interfaces = new Class<?>[] { Path.class, Expression.class, DebugNode.class };
        } else if (Predicate.class == type) {
            interfaces = new Class<?>[] { Predicate.class, Expression.class, DebugNode.class };
        } else if (Expression.class == type) {
            interfaces = new Class<?>[] { Expression.class, DebugNode.class };
        } else if (CriteriaBuilder.class == type) {
            interfaces = new Class<?>[] { CriteriaBuilder.class, DebugNode.class };
        } else {
            interfaces = new Class<?>[] { type, DebugNode.class };
        }
        return (T) Proxy.newProxyInstance(CompiledTuplePredicateSupportTest.class.getClassLoader(), interfaces, new DebugInvocationHandler(debug));
    }

    private interface DebugNode {

        String debug();

    }

    private static final class DebugInvocationHandler implements InvocationHandler {

        private final String debug;

        private DebugInvocationHandler(final String debug) {
            this.debug = debug;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) {
            String name = method.getName();
            if ("debug".equals(name) || "toString".equals(name)) {
                return this.debug;
            }
            if ("hashCode".equals(name)) {
                return this.debug.hashCode();
            }
            if ("equals".equals(name)) {
                return proxy == args[0];
            }
            if ("get".equals(name) && 1 == method.getParameterCount() && String.class == method.getParameterTypes()[0]) {
                return proxy(Path.class, this.debug.isEmpty() ? (String) args[0] : this.debug + "." + args[0]);
            }
            if ("equal".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " = " + debug(args[1]));
            }
            if ("literal".equals(name)) {
                return proxy(Expression.class, String.valueOf(args[0]));
            }
            if ("and".equals(name) && 1 == method.getParameterCount() && args[0] instanceof Predicate[]) {
                return proxy(Predicate.class,
                    Arrays.stream((Predicate[]) args[0]).map(CompiledTuplePredicateSupportTest::debug).collect(Collectors.joining(" AND ")));
            }
            if ("or".equals(name) && 1 == method.getParameterCount() && args[0] instanceof Predicate[]) {
                return proxy(Predicate.class,
                    Arrays.stream((Predicate[]) args[0]).map(CompiledTuplePredicateSupportTest::debug).collect(Collectors.joining(" OR ")));
            }
            if ("not".equals(name) && 0 == method.getParameterCount()) {
                return proxy(Predicate.class, "NOT (" + this.debug + ")");
            }
            if ("getJavaType".equals(name)) {
                return Object.class;
            }
            return defaultValue(method.getReturnType());
        }

        private static Object defaultValue(final Class<?> type) {
            if (!type.isPrimitive()) {
                return null;
            }
            if (boolean.class == type) {
                return false;
            }
            if (char.class == type) {
                return '\0';
            }
            return 0;
        }

    }

    @SuppressWarnings("unused")
    public static final class TupleQueryModel {

        @TupleInValues(columns = { @TupleColumn(leftPath = "a", itemPath = "left.value"), @TupleColumn(leftPath = "b", itemPath = "right.value") })
        private List<PairRow> pairs = Arrays.asList(PairRow.of("1", "2"), PairRow.of("3", "4"));

        public List<PairRow> getPairs() {
            return this.pairs;
        }
    }

    @SuppressWarnings("unused")
    public static final class PrimitiveTupleQueryModel {

        @TupleInValues(columns = { @TupleColumn(leftPath = "a", itemIndex = 0, targetType = Integer.class),
            @TupleColumn(leftPath = "b", itemIndex = 1, targetType = Integer.class) })
        private int[][] pairs = new int[][] { { 1, 2 }, { 5, 6 } };

        public int[][] getPairs() {
            return this.pairs;
        }
    }

    @SuppressWarnings("unused")
    public static final class TriggerTupleQueryModel {

        @TupleInValues(tupleField = "pairs", columns = { @TupleColumn(leftPath = "a", itemIndex = 0, targetType = Integer.class),
            @TupleColumn(leftPath = "b", itemIndex = 1, targetType = Integer.class) })
        private Boolean enabled = Boolean.TRUE;

        private Set<Long[]> pairs = java.util.Collections.<Long[]>singleton(new Long[] { Long.valueOf(7L), Long.valueOf(8L) });

        private TriggerTupleQueryModel() {
            this(Boolean.TRUE);
        }

        private TriggerTupleQueryModel(final Boolean enabled) {
            this.enabled = enabled;
            if (Boolean.TRUE.equals(enabled)) {
                this.pairs = new java.util.LinkedHashSet<Long[]>(
                    Arrays.asList(new Long[] { Long.valueOf(7L), Long.valueOf(8L) }, new Long[] { Long.valueOf(9L), Long.valueOf(10L) }));
            }
        }

        public Boolean getEnabled() {
            return this.enabled;
        }

        public Set<Long[]> getPairs() {
            return this.pairs;
        }
    }

    @SuppressWarnings("unused")
    public static final class NegativeTupleQueryModel {

        @TupleNotInValues(columns = { @TupleColumn(leftPath = "a", itemIndex = 0, targetType = Integer.class),
            @TupleColumn(leftPath = "b", itemIndex = 1, targetType = Integer.class) })
        private Object[][] pairs = new Object[][] { { "11", "12" }, { "13", "14" } };

        public Object[][] getPairs() {
            return this.pairs;
        }
    }

    public static final class PairRow {

        private ValueHolder left;

        private ValueHolder right;

        private static PairRow of(final String left, final String right) {
            PairRow row = new PairRow();
            row.left = new ValueHolder(left);
            row.right = new ValueHolder(right);
            return row;
        }

        public ValueHolder getLeft() {
            return this.left;
        }

        public ValueHolder getRight() {
            return this.right;
        }
    }

    public static final class ValueHolder {

        private final String value;

        private ValueHolder(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

}
