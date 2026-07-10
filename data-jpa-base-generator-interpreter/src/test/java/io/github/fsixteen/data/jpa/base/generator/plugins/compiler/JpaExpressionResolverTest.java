package io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.LiteralExpression;

public class JpaExpressionResolverTest {

    @Test
    public void shouldNormalizeSingleRuntimeValueAgainstAnchorType() {
        Object normalized = JpaExpressionResolver.normalizeValueForExpression("7", expression(Integer.class, "left"), "");

        assertEquals(Integer.class, normalized.getClass());
        assertEquals(Integer.valueOf(7), normalized);
    }

    @Test
    public void shouldNormalizeCollectionAgainstAnchorType() {
        Collection<Object> normalized = JpaExpressionResolver.normalizeCollectionForExpression(Arrays.<Object>asList("1", Long.valueOf(2L)),
            expression(Integer.class, "left"), "");

        assertTrue(normalized.iterator().next() instanceof Integer);
        assertIterableEquals(Arrays.<Object>asList(Integer.valueOf(1), Integer.valueOf(2)), normalized);
    }

    @Test
    public void shouldResolveLiteralByAnchorTypeWhenDeclaredLiteralTypeIsWider() {
        Expression<?> resolved = JpaExpressionResolver.resolve(LiteralExpression.of("9", String.class), null, null, null, criteriaBuilder(),
            expression(Integer.class, "left"), "");

        assertEquals("9", debug(resolved));
        assertEquals(Integer.class, resolved.getJavaType());
    }

    @Test
    public void shouldResolveFieldValueByAnchorType() {
        Expression<?> resolved = JpaExpressionResolver.resolve(FieldValueExpression.literal(), "11", null, null, criteriaBuilder(),
            expression(Long.class, "left"), "");

        assertEquals("11", debug(resolved));
        assertEquals(Long.class, resolved.getJavaType());
    }

    @SuppressWarnings("unchecked")
    private static Expression<?> expression(final Class<?> javaType, final String debug) {
        return (Expression<?>) Proxy.newProxyInstance(JpaExpressionResolverTest.class.getClassLoader(), new Class<?>[] { Expression.class, DebugNode.class },
            new ExpressionInvocationHandler(javaType, debug));
    }

    @SuppressWarnings("unchecked")
    private static CriteriaBuilder criteriaBuilder() {
        return (CriteriaBuilder) Proxy.newProxyInstance(JpaExpressionResolverTest.class.getClassLoader(), new Class<?>[] { CriteriaBuilder.class },
            new CriteriaBuilderInvocationHandler());
    }

    private static String debug(final Object value) {
        return value instanceof DebugNode ? ((DebugNode) value).debug() : String.valueOf(value);
    }

    private interface DebugNode {

        String debug();

    }

    private static final class ExpressionInvocationHandler implements InvocationHandler {

        private final Class<?> javaType;

        private final String debug;

        private ExpressionInvocationHandler(final Class<?> javaType, final String debug) {
            this.javaType = javaType;
            this.debug = debug;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) {
            String name = method.getName();
            if ("getJavaType".equals(name)) {
                return this.javaType;
            }
            if ("debug".equals(name) || "toString".equals(name)) {
                return this.debug;
            }
            if ("hashCode".equals(name)) {
                return this.debug.hashCode();
            }
            if ("equals".equals(name)) {
                return proxy == args[0];
            }
            return defaultValue(method.getReturnType());
        }
    }

    private static final class CriteriaBuilderInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) {
            if ("literal".equals(method.getName())) {
                Object value = args[0];
                Class<?> javaType = null == value ? Object.class : value.getClass();
                return expression(javaType, String.valueOf(value));
            }
            return defaultValue(method.getReturnType());
        }
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
