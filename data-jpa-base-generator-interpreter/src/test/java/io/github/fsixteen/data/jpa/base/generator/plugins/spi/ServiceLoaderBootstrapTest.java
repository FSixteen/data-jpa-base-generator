package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.plugins.collections.AnnotationCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.collections.ComputerCollection;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledPredicateFacade;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateOperator;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.RuntimeExpressionEvaluator;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.PredicateExpressionRegistry;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.RegisteredPredicateResolver;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.RegisteredPredicateTemplate;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.RegisteredPredicateTemplateRegistry;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.CriteriaDebugProxies;

public class ServiceLoaderBootstrapTest {

    @Test
    public void shouldLoadExpressionTemplateProviderFromSpi() {
        ServiceLoaderBootstrap.reload();
        assertTrue(null != PredicateExpressionRegistry.require("spi.ignoreCaseName"));
        FunctionExpression expression = (FunctionExpression) PredicateExpressionRegistry.require("spi.ignoreCaseName").create();

        assertEquals("lower", expression.getName());
        assertEquals("name", PathExpression.class.cast(expression.getArgs().get(0)).getPath());
        assertEquals("demo", RuntimeExpressionEvaluator.evaluate(expression, new TemplateArgs(), null));
    }

    @Test
    public void shouldLoadRegisteredPredicateTemplateProviderFromSpi() {
        ServiceLoaderBootstrap.reload();
        assertTrue(null != RegisteredPredicateTemplateRegistry.require("spi.ignoreCaseEqual"));
        RegisteredPredicateTemplate template = RegisteredPredicateTemplateRegistry.require("spi.ignoreCaseEqual").create();
        CriteriaBuilder cb = CriteriaDebugProxies.proxy(CriteriaBuilder.class, "cb", ServiceLoaderBootstrapTest.class);
        Root<?> root = CriteriaDebugProxies.proxy(Root.class, "root", ServiceLoaderBootstrapTest.class);

        assertEquals(PredicateOperator.EQ, template.getOperator());
        assertEquals("lower", FunctionExpression.class.cast(template.getLeft()).getName());
        assertEquals("lower", FunctionExpression.class.cast(template.getRight()).getName());
        assertEquals("name", PathExpression.class.cast(FunctionExpression.class.cast(template.getLeft()).getArgs().get(0)).getPath());
        assertEquals("keyword", PathExpression.class.cast(FunctionExpression.class.cast(template.getRight()).getArgs().get(0)).getPath());
        assertEquals("lower(root.name) = lower(root.keyword)", CriteriaDebugProxies.debug(RegisteredPredicateResolver.resolve(template, null, root, null, cb)));
    }

    @Test
    public void shouldLoadCompiledPredicateProviderFromSpi() {
        ServiceLoaderBootstrap.reload();
        assertTrue(CompiledPredicateProviderRegistry.containsKey(SpiCompiledSelectable.class));

        AnnotationCollection collection = AnnotationCollection.Builder.of(CompiledQueryModel.class).build();
        CriteriaBuilder cb = CriteriaDebugProxies.proxy(CriteriaBuilder.class, "cb", ServiceLoaderBootstrapTest.class);
        Root<?> root = CriteriaDebugProxies.proxy(Root.class, "root", ServiceLoaderBootstrapTest.class);
        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CompiledQueryModel(), root, null, cb, BuilderType.SELECTED);

        assertEquals("root.status = root.audit.currentStatus",
            CriteriaDebugProxies.debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildSpiCompiledProviderThroughStableCollectionChain() {
        ServiceLoaderBootstrap.reload();
        CriteriaBuilder cb = CriteriaDebugProxies.proxy(CriteriaBuilder.class, "cb", ServiceLoaderBootstrapTest.class);
        Root<?> root = CriteriaDebugProxies.proxy(Root.class, "root", ServiceLoaderBootstrapTest.class);

        Predicate predicate = io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache.getAnnotationCollection(CompiledQueryModel.class)
            .toComputerCollection().withArgs(new CompiledQueryModel()).withSpecification(root, null, cb).build(BuilderType.SELECTED).getPredicate(cb);

        assertEquals("root.status = root.audit.currentStatus", CriteriaDebugProxies.debug(predicate));
    }

    @Test
    public void shouldBuildSpiConstraintProviderThroughStableCollectionChain() {
        ServiceLoaderBootstrap.reload();
        CriteriaBuilder cb = CriteriaDebugProxies.proxy(CriteriaBuilder.class, "cb", ServiceLoaderBootstrapTest.class);
        Root<?> root = CriteriaDebugProxies.proxy(Root.class, "root", ServiceLoaderBootstrapTest.class);

        Predicate predicate = io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache
            .getAnnotationCollection(ConstraintCompiledQueryModel.class).toComputerCollection().withArgs(new ConstraintCompiledQueryModel())
            .withSpecification(root, null, cb).build(BuilderType.SELECTED).getPredicate(cb);

        assertEquals("root.status = root.audit.currentStatus", CriteriaDebugProxies.debug(predicate));
    }

    public static final class CompiledQueryModel {

        @SpiCompiledSelectable
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    public static final class ConstraintCompiledQueryModel {

        @ConstraintCompiledSelectable
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    private static final class TemplateArgs {

        private final String name = "Demo";

        @SuppressWarnings("unused")
        public String getName() {
            return this.name;
        }

    }

}
