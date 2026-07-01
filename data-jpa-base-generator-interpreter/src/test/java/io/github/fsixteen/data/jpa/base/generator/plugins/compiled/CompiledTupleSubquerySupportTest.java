package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TupleNotExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.TuplePair;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.CriteriaDebugProxies;

public class CompiledTupleSubquerySupportTest {

    @Test
    public void shouldCompileTupleExistsAnnotationToDedicatedSpec() throws Exception {
        Field field = TupleExistsQueryModel.class.getDeclaredField("enabled");
        CompiledAnnotationSpec<TupleExists> spec = CompiledAnnotationSpec.of(TupleExistsQueryModel.class, field.getAnnotation(TupleExists.class), field);

        CompiledTupleSubquerySpec tupleSpec = CompiledTupleSubquerySpecs.tupleSubquery(spec);

        assertEquals(2, tupleSpec.getPairs().size());
        assertEquals("a", tupleSpec.getPairs().get(0).getLeftPath());
        assertEquals("c1", tupleSpec.getPairs().get(0).getRightPath());
        assertEquals(TupleExistsQueryModel.class, tupleSpec.getTargetEntity());
    }

    @Test
    public void shouldCreateTupleExistsPredicateWithWhereCompareAndWhereGroup() throws Exception {
        Field field = TupleExistsQueryModel.class.getDeclaredField("enabled");
        CompiledAnnotationSpec<TupleExists> spec = CompiledAnnotationSpec.of(TupleExistsQueryModel.class, field.getAnnotation(TupleExists.class), field);
        CriteriaBuilder cb = CriteriaDebugProxies.proxy(CriteriaBuilder.class, "cb", getClass());
        Root<?> root = CriteriaDebugProxies.proxy(Root.class, "root", getClass());
        AbstractQuery<?> query = CriteriaDebugProxies.proxy(AbstractQuery.class, "query", getClass());

        Predicate predicate = CompiledTupleSubquerySupport.create(spec, new TupleExistsQueryModel(), root, query, cb);

        assertEquals("EXISTS (SELECT subroot.c1 WHERE subroot.c1 = root.a AND subroot.c2 = root.b AND subroot.status = ACTIVE AND subroot.tenantId = 7)",
            CriteriaDebugProxies.debug(predicate));
    }

    @Test
    public void shouldCreateTupleNotExistsPredicate() throws Exception {
        Field field = TupleNotExistsQueryModel.class.getDeclaredField("enabled");
        CompiledAnnotationSpec<
            TupleNotExists> spec = CompiledAnnotationSpec.of(TupleNotExistsQueryModel.class, field.getAnnotation(TupleNotExists.class), field);
        CriteriaBuilder cb = CriteriaDebugProxies.proxy(CriteriaBuilder.class, "cb", getClass());
        Root<?> root = CriteriaDebugProxies.proxy(Root.class, "root", getClass());
        AbstractQuery<?> query = CriteriaDebugProxies.proxy(AbstractQuery.class, "query", getClass());

        Predicate predicate = CompiledTupleSubquerySupport.create(spec, new TupleNotExistsQueryModel(), root, query, cb);

        assertEquals("NOT EXISTS (SELECT subroot.c1 WHERE subroot.c1 = root.a AND subroot.c2 = root.b)", CriteriaDebugProxies.debug(predicate));
    }

    @Test
    public void shouldIgnoreTupleExistsWhenTriggerIsFalse() throws Exception {
        Field field = TupleExistsQueryModel.class.getDeclaredField("enabled");
        CompiledAnnotationSpec<TupleExists> spec = CompiledAnnotationSpec.of(TupleExistsQueryModel.class, field.getAnnotation(TupleExists.class), field);

        Predicate predicate = CompiledTupleSubquerySupport.create(spec, new TupleExistsQueryModel(Boolean.FALSE),
            CriteriaDebugProxies.proxy(Root.class, "root", getClass()), CriteriaDebugProxies.proxy(AbstractQuery.class, "query", getClass()),
            CriteriaDebugProxies.proxy(CriteriaBuilder.class, "cb", getClass()));

        assertNull(predicate);
    }

    @Test
    public void shouldResolveProviderForTupleSubqueryAnnotations() {
        assertTrue(CompiledPredicateProviderRegistry.containsKey(TupleExists.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(TupleNotExists.class));
    }

    @SuppressWarnings("unused")
    public static final class TupleExistsQueryModel {

        @TupleExists(targetEntity = TupleExistsQueryModel.class,
            pairs = { @TuplePair(left = @Expr(type = ExprType.PATH, path = "a"), right = @Expr(type = ExprType.PATH, path = "c1")),
                @TuplePair(left = @Expr(type = ExprType.PATH, path = "b"), right = @Expr(type = ExprType.PATH, path = "c2")) },
            whereCompare = @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.VALUE, valueField = "status")),
            where = @SubqueryGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId")) }))
        private Boolean enabled = Boolean.TRUE;

        private Integer a = 1;

        private Integer b = 2;

        private String status = "ACTIVE";

        private Integer tenantId = 7;

        private TupleExistsQueryModel() {
            this(Boolean.TRUE);
        }

        private TupleExistsQueryModel(final Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean getEnabled() {
            return this.enabled;
        }

        public Integer getA() {
            return this.a;
        }

        public Integer getB() {
            return this.b;
        }

        public String getStatus() {
            return this.status;
        }

        public Integer getTenantId() {
            return this.tenantId;
        }
    }

    @SuppressWarnings("unused")
    public static final class TupleNotExistsQueryModel {

        @TupleNotExists(targetEntity = TupleNotExistsQueryModel.class,
            pairs = { @TuplePair(left = @Expr(type = ExprType.PATH, path = "a"), right = @Expr(type = ExprType.PATH, path = "c1")),
                @TuplePair(left = @Expr(type = ExprType.PATH, path = "b"), right = @Expr(type = ExprType.PATH, path = "c2")) })
        private Boolean enabled = Boolean.TRUE;

        private Integer a = 1;

        private Integer b = 2;

        public Boolean getEnabled() {
            return this.enabled;
        }

        public Integer getA() {
            return this.a;
        }

        public Integer getB() {
            return this.b;
        }
    }

}
