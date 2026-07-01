package io.github.fsixteen.data.jpa.base.generator.plugins.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateOperator;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;

public class RegisteredPredicateResolverTest {

    @Test
    public void shouldResolveIgnoreCaseTemplate() {
        RegisteredPredicateTemplateRegistry.clear();
        RegisteredPredicateTemplateRegistry.register("ignoreCaseEqual", new RegisteredPredicateTemplateProvider() {

            @Override
            public RegisteredPredicateTemplate create() {
                return RegisteredPredicateTemplate.of(FunctionExpression.of("lower", String.class, Arrays.asList(PathExpression.of("name"))),
                    FunctionExpression.of("lower", String.class, Arrays.asList(PathExpression.of("keyword"))), ComparableType.EQ);
            }

        });

        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        Predicate predicate = RegisteredPredicateResolver.resolve(RegisteredPredicateTemplateRegistry.require("ignoreCaseEqual").create(), null, root, null,
            cb);

        assertEquals("lower(root.name) = lower(root.keyword)", debug(predicate));
    }

    @Test
    public void shouldResolveDateTruncLikeTemplate() {
        RegisteredPredicateTemplate template = RegisteredPredicateTemplate.of(
            FunctionExpression.of("date", java.util.Date.class, Arrays.asList(PathExpression.of("createTime"))), PathExpression.of("targetDate"),
            ComparableType.EQ);

        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        Predicate predicate = RegisteredPredicateResolver.resolve(template, null, root, null, cb);

        assertEquals("date(root.createTime) = root.targetDate", debug(predicate));
    }

    @Test
    public void shouldResolveLikeOperatorThroughUnifiedPredicateOperator() {
        RegisteredPredicateTemplate template = RegisteredPredicateTemplate.of(PathExpression.of("name"), PathExpression.of("keyword"),
            PredicateOperator.LIKE_STARTS_WITH);

        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        Predicate predicate = RegisteredPredicateResolver.resolve(template, null, root, null, cb);

        assertEquals(PredicateOperator.LIKE_STARTS_WITH, template.getOperator());
        assertEquals("root.name LIKE root.keyword%", debug(predicate));
    }

    private static String debug(final Object proxy) {
        return ((DebugNode) proxy).debug();
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(final Class<T> type, final String debug) {
        Class<?>[] interfaces;
        if (Root.class == type) {
            interfaces = new Class<?>[] { Root.class, DebugNode.class };
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
        return (T) Proxy.newProxyInstance(RegisteredPredicateResolverTest.class.getClassLoader(), interfaces, new DebugInvocationHandler(debug));
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
            if ("literal".equals(name)) {
                return proxy(Expression.class, String.valueOf(args[0]));
            }
            if ("function".equals(name)) {
                Expression<?>[] expArgs = (Expression<?>[]) args[2];
                String joined = Arrays.stream(expArgs).map(RegisteredPredicateResolverTest::debug).collect(Collectors.joining(", "));
                return proxy(Expression.class, args[0] + "(" + joined + ")");
            }
            if ("equal".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " = " + debug(args[1]));
            }
            if ("notEqual".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " != " + debug(args[1]));
            }
            if ("greaterThan".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " > " + debug(args[1]));
            }
            if ("greaterThanOrEqualTo".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " >= " + debug(args[1]));
            }
            if ("lessThan".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " < " + debug(args[1]));
            }
            if ("lessThanOrEqualTo".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " <= " + debug(args[1]));
            }
            if ("concat".equals(name)) {
                return proxy(Expression.class, debugArg(args[0]) + debugArg(args[1]));
            }
            if ("like".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " LIKE " + debug(args[1]));
            }
            if ("notLike".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " NOT LIKE " + debug(args[1]));
            }
            if ("alias".equals(name) || "as".equals(name)) {
                return proxy;
            }
            return defaultValue(method.getReturnType());
        }

        private static String debugArg(final Object arg) {
            if (arg instanceof String) {
                return (String) arg;
            }
            return debug(arg);
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

}
