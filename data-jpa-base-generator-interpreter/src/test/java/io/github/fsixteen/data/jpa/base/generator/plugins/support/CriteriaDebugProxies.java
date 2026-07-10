package io.github.fsixteen.data.jpa.base.generator.plugins.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * 测试专用的 Criteria/JPA proxy 调试工具.
 *
 * <p>
 * 统一把 JPA Criteria 交互投影为稳定字符串, 避免不同测试类各自维护一份近似但不完全一致的
 * debug proxy 逻辑.
 * </p>
 */
public final class CriteriaDebugProxies {

    private CriteriaDebugProxies() {
    }

    public static String debug(final Object proxy) {
        return proxy instanceof DebugNode ? ((DebugNode) proxy).debug() : String.valueOf(proxy);
    }

    @SuppressWarnings("unchecked")
    public static <T> T proxy(final Class<T> type, final String debug, final Class<?> owner) {
        Class<?>[] interfaces;
        if (Root.class == type) {
            interfaces = new Class<?>[] { Root.class, DebugNode.class };
        } else if (AbstractQuery.class == type) {
            interfaces = new Class<?>[] { AbstractQuery.class, DebugNode.class };
        } else if (Subquery.class == type) {
            interfaces = new Class<?>[] { Subquery.class, AbstractQuery.class, Expression.class, DebugNode.class };
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
        return (T) Proxy.newProxyInstance(owner.getClassLoader(), interfaces, new DebugInvocationHandler(debug, owner));
    }

    public interface DebugNode {

        String debug();

    }

    private static final class DebugInvocationHandler implements InvocationHandler {

        private String debug;

        private final Class<?> owner;

        private DebugInvocationHandler(final String debug, final Class<?> owner) {
            this.debug = debug;
            this.owner = owner;
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
                return proxy(Path.class, this.debug.isEmpty() ? (String) args[0] : this.debug + "." + args[0], this.owner);
            }
            if ("subquery".equals(name)) {
                return proxy(Subquery.class, "subquery", this.owner);
            }
            if ("from".equals(name) && 1 == method.getParameterCount()) {
                return proxy(Root.class, "subroot", this.owner);
            }
            if ("select".equals(name) && 1 == method.getParameterCount()) {
                this.debug = debug(args[0]);
                return proxy;
            }
            if ("where".equals(name) && 1 == method.getParameterCount()) {
                this.debug = this.debug + " WHERE " + debug(args[0]);
                return proxy;
            }
            if ("exists".equals(name) && 1 == method.getParameterCount()) {
                return proxy(Predicate.class, "EXISTS (SELECT " + debug(args[0]) + ")", this.owner);
            }
            if ("in".equals(name) && 1 == method.getParameterCount()) {
                Object value = args[0];
                if (value instanceof Object[] && 1 == ((Object[]) value).length) {
                    value = ((Object[]) value)[0];
                }
                return proxy(Predicate.class, this.debug + " IN (SELECT " + debug(value) + ")", this.owner);
            }
            if ("equal".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " = " + debug(args[1]), this.owner);
            }
            if ("between".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " BETWEEN " + debug(args[1]) + " AND " + debug(args[2]), this.owner);
            }
            if ("greaterThan".equals(name) || "gt".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " > " + debug(args[1]), this.owner);
            }
            if ("greaterThanOrEqualTo".equals(name) || "ge".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " >= " + debug(args[1]), this.owner);
            }
            if ("lessThan".equals(name) || "lt".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " < " + debug(args[1]), this.owner);
            }
            if ("lessThanOrEqualTo".equals(name) || "le".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " <= " + debug(args[1]), this.owner);
            }
            if ("notEqual".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " != " + debug(args[1]), this.owner);
            }
            if ("like".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " LIKE " + debug(args[1]), this.owner);
            }
            if ("notLike".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " NOT LIKE " + debug(args[1]), this.owner);
            }
            if ("not".equals(name) && 0 == method.getParameterCount()) {
                if (this.debug.contains(" BETWEEN ")) {
                    return proxy(Predicate.class, this.debug.replace(" BETWEEN ", " NOT BETWEEN "), this.owner);
                }
                if (this.debug.startsWith("EXISTS ")) {
                    return proxy(Predicate.class, "NOT " + this.debug, this.owner);
                }
                if (this.debug.contains(" IN (SELECT ")) {
                    return proxy(Predicate.class, this.debug.replace(" IN (SELECT ", " NOT IN (SELECT "), this.owner);
                }
                return proxy(Predicate.class, "NOT (" + this.debug + ")", this.owner);
            }
            if ("function".equals(name)) {
                Expression<?>[] expArgs = (Expression<?>[]) args[2];
                String joined = Arrays.stream(expArgs).map(CriteriaDebugProxies::debug).collect(Collectors.joining(", "));
                return proxy(Expression.class, args[0] + "(" + joined + ")", this.owner);
            }
            if ("and".equals(name)) {
                if (args[0] instanceof Predicate[]) {
                    Predicate[] predicates = (Predicate[]) args[0];
                    return 0 == predicates.length ? null
                        : proxy(Predicate.class, Arrays.stream(predicates).map(CriteriaDebugProxies::debug).collect(Collectors.joining(" AND ")), this.owner);
                }
                return args[0];
            }
            if ("or".equals(name) && 1 == method.getParameterCount() && args[0] instanceof Predicate[]) {
                Predicate[] predicates = (Predicate[]) args[0];
                return 0 == predicates.length ? null
                    : proxy(Predicate.class, Arrays.stream(predicates).map(CriteriaDebugProxies::debug).collect(Collectors.joining(" OR ")), this.owner);
            }
            if ("concat".equals(name)) {
                return proxy(Expression.class, debugArg(args[0]) + debugArg(args[1]), this.owner);
            }
            if ("literal".equals(name)) {
                return proxy(Expression.class, String.valueOf(args[0]), this.owner);
            }
            if ("isNull".equals(name) && 0 == method.getParameterCount()) {
                return proxy(Predicate.class, this.debug + " IS NULL", this.owner);
            }
            if ("isNotNull".equals(name) && 0 == method.getParameterCount()) {
                return proxy(Predicate.class, this.debug + " IS NOT NULL", this.owner);
            }
            if ("getJavaType".equals(name)) {
                return Object.class;
            }
            if ("alias".equals(name) || "as".equals(name)) {
                return proxy;
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

        private static String debugArg(final Object arg) {
            if (arg instanceof String) {
                return (String) arg;
            }
            return debug(arg);
        }

    }

}
