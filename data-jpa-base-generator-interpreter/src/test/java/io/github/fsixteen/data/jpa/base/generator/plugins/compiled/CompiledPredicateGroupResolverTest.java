package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNull;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Like;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.ComputerCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;

public class CompiledPredicateGroupResolverTest {

    @Test
    public void shouldResolvePredicateGroupTreeToAndPredicate() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(MultiQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = ComputerCollection.Builder.of().withAnnotationCollection(collection).withArgs(new MultiQueryModel())
            .withSpecification(root, null, cb).build(BuilderType.SELECTED);

        Predicate predicate = CompiledPredicateGroupResolver.create(computerCollection.getPredicateGroupSpec(), new MultiQueryModel(), root, null, cb);

        assertEquals("root.status = root.audit.currentStatus AND root.createdAt BETWEEN root.range.startAt AND root.range.endAt AND root.name LIKE %demo% "
            + "AND root.deletedAt IS NULL", debug(predicate));
    }

    @Test
    public void shouldResolveSubqueryNestedPredicateThroughRegistryByActualAnnotationType() throws Exception {
        Field field = NestedPredicateQueryModel.class.getDeclaredField("status");
        ComposedSubqueryEqual annotation = field.getAnnotation(ComposedSubqueryEqual.class);
        CompiledAnnotationSpec<ComposedSubqueryEqual> nestedSpec = CompiledAnnotationSpec.of(NestedPredicateQueryModel.class, annotation, field);
        CompiledSubquerySpec subquerySpec = CompiledSubquerySpec.of(SubqueryMode.IN, NestedPredicateQueryModel.class, "currentStatus", "status", nestedSpec,
            PredicateGroupSpec.empty(PredicateGroupSpec.JunctionType.AND));

        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> outerRoot = proxy(Root.class, "root");
        Root<?> subRoot = proxy(Root.class, "subroot");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        Predicate predicate = CompiledSubquerySupport.resolveWherePredicate(subquerySpec,
            io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable.class, new NestedPredicateQueryModel(), outerRoot, subRoot, query, cb);

        assertEquals("subroot.subquery.status = subroot.subquery.currentStatus", debug(predicate));
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
        return (T) Proxy.newProxyInstance(CompiledPredicateGroupResolverTest.class.getClassLoader(), interfaces, new DebugInvocationHandler(debug));
    }

    private interface DebugNode {

        String debug();

    }

    private static final class DebugInvocationHandler implements InvocationHandler {

        private String debug;

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
            if ("between".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " BETWEEN " + debug(args[1]) + " AND " + debug(args[2]));
            }
            if ("like".equals(name)) {
                return proxy(Predicate.class, debug(args[0]) + " LIKE " + debug(args[1]));
            }
            if ("concat".equals(name)) {
                return proxy(Expression.class, debugArg(args[0]) + debugArg(args[1]));
            }
            if ("literal".equals(name)) {
                return proxy(Expression.class, String.valueOf(args[0]));
            }
            if ("isNull".equals(name) && 0 == method.getParameterCount()) {
                return proxy(Predicate.class, this.debug + " IS NULL");
            }
            if ("and".equals(name) && 1 == method.getParameterCount() && args[0] instanceof Predicate[]) {
                return proxy(Predicate.class,
                    Arrays.stream((Predicate[]) args[0]).map(CompiledPredicateGroupResolverTest::debug).collect(Collectors.joining(" AND ")));
            }
            if ("or".equals(name) && 1 == method.getParameterCount() && args[0] instanceof Predicate[]) {
                return proxy(Predicate.class,
                    Arrays.stream((Predicate[]) args[0]).map(CompiledPredicateGroupResolverTest::debug).collect(Collectors.joining(" OR ")));
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

        private static String debugArg(final Object arg) {
            return arg instanceof String ? (String) arg : debug(arg);
        }

    }

    @SuppressWarnings("unused")
    public static final class MultiQueryModel {

        @Equal(right = @Expr(type = ExprType.PATH, path = "audit.currentStatus"))
        private String status = "ACTIVE";

        @Between(right = @Expr(type = ExprType.PATH, path = "range.startAt"), extra = { @Expr(type = ExprType.PATH, path = "range.endAt") })
        private List<String> createdAt = Arrays.asList("ignored-start", "ignored-end");

        @Like(left = @Expr(type = ExprType.PATH, path = "name"))
        private String keyword = "demo";

        @IsNull(left = @Expr(type = ExprType.PATH, path = "deletedAt"))
        private Boolean deleted = Boolean.TRUE;

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public List<String> getCreatedAt() {
            return this.createdAt;
        }

        public void setCreatedAt(final List<String> createdAt) {
            this.createdAt = createdAt;
        }

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

        public Boolean getDeleted() {
            return this.deleted;
        }

        public void setDeleted(final Boolean deleted) {
            this.deleted = deleted;
        }

    }

    @SuppressWarnings("unused")
    public static final class NestedPredicateQueryModel {

        @ComposedSubqueryEqual
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Equal(left = @Expr(type = ExprType.PATH, path = "subquery.status"), right = @Expr(type = ExprType.PATH, path = "subquery.currentStatus"),
        options = @PredicateOptions(scope = { Constant.DEFAULT }))
    public @interface ComposedSubqueryEqual {
    }

}
