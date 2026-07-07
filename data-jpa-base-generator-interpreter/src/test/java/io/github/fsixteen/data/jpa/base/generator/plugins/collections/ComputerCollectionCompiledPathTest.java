package io.github.fsixteen.data.jpa.base.generator.plugins.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.Constraint;
import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.ProviderRef;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessorContext;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Between;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Case;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseElse;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThen;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThenGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhen;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhenGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CollectionPolicy;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepNestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepNestedExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Exists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterNotIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThan;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.GreaterThanOrEqualTo;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Gt;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Gte;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IgnoreCaseEqual;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IgnoreCaseLike;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.In;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNotNull;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.IsNull;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Length;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LessThan;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.LessThanOrEqualTo;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Like;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Lt;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Lte;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Membership;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedSubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotBetween;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotEqual;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotLike;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NullCheck;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateRef;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ProcessorRef;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Range;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.RightLike;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SplitNotIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.StartWith;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryPredicate;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Unique;
import io.github.fsixteen.data.jpa.base.generator.plugins.cache.CollectionCache;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledAnnotationSpec;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledPredicateFacade;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateGroupSpec;
import io.github.fsixteen.data.jpa.base.generator.plugins.constant.BuilderType;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProviderRegistry;
import io.github.fsixteen.data.jpa.base.generator.plugins.spi.ServiceLoaderBootstrap;
import io.github.fsixteen.data.jpa.base.generator.plugins.support.CriteriaDebugProxies;

public class ComputerCollectionCompiledPathTest {

    @Test
    public void shouldBuildCompiledResultsFromAnnotationCollection() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(QueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = collection.toComputerCollection().withArgs(new QueryModel()).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED);

        assertFalse(collection.getSelectionPredicateSpecs().isEmpty());
        assertFalse(computerCollection.getPredicateResults().isEmpty());
        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.status = root.audit.currentStatus", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldSnapshotCompiledAnnotationMetadata() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(QueryModel.class).build();

        assertEquals(1, collection.getSelectionPredicateSpecs().size());
        assertEquals("status", collection.getSelectionPredicateSpecs().iterator().next().getBindingPath());
        assertEquals("status", collection.getSelectionPredicateSpecs().iterator().next().getValueFieldName());
        assertEquals(ExprType.AUTO, collection.getSelectionPredicateSpecs().iterator().next().getPredicateCore().getRight().type());
        assertEquals("audit.currentStatus", collection.getSelectionPredicateSpecs().iterator().next().getPredicateCore().getRight().path());
        assertEquals("default", collection.getSelectionPredicateSpecs().iterator().next().getEffectiveOptions().getScope()[0]);
        assertEquals(GroupInfo.class, collection.getSelectionPredicateSpecs().iterator().next().getEffectiveOptions().getGroups()[0].annotationType());
    }

    @Test
    public void shouldKeepStableExternalChainCompatible() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(QueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        Predicate predicate = computer.toComputerCollection().withArgs(new QueryModel()).withSpecification(root, null, cb).build(BuilderType.SELECTED)
            .getPredicate(cb);

        assertEquals("root.status = root.audit.currentStatus", debug(predicate));
        assertEquals("root.status = root.audit.currentStatus", debug(computer.toComputerCollection().withArgs(new QueryModel())
            .withSpecification(root, null, cb).build(BuilderType.SELECTED).getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldExposeSingleLatestBuilderEntryPoint() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(QueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = collection.toComputerCollection().withArgs(new QueryModel()).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED);

        assertEquals(1, collection.getSelectionPredicateSpecs().size());
        assertEquals("root.status = root.audit.currentStatus", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldKeepStableCollectionEntryPointsOnPureLatestEqualPath() throws Exception {
        AnnotationCollection collection = AnnotationCollection.Builder.of(QueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = collection.toComputerCollection().withArgs(new QueryModel()).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED);

        assertFalse(AnnotationCollection.class.isAnnotationPresent(Deprecated.class));
        assertFalse(ComputerCollection.class.isAnnotationPresent(Deprecated.class));
        assertFalse(AnnotationCollection.class.getMethod("toComputerCollection").isAnnotationPresent(Deprecated.class));
        assertFalse(Equal.class.isAnnotationPresent(Deprecated.class));
        assertFalse(Equal.List.class.isAnnotationPresent(Deprecated.class));
        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.status = root.audit.currentStatus", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
        assertTrue(collection.getSelectionPredicateSpecs().size() > 0);
        assertTrue(collection.getExistencePredicateSpecs().isEmpty());
    }

    @Test
    public void shouldResolveEqualProviderThroughCanonicalCompareMetaAnnotation() {
        assertEquals(CompareOp.EQ, Equal.class.getAnnotation(Compare.class).op());
        assertTrue(null != CompiledPredicateProviderRegistry.reference(Equal.class));
        assertTrue(null != CompiledPredicateProviderRegistry.reference(Compare.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(Equal.class));
    }

    @Test
    public void shouldRegisterBuiltInCompiledProvidersForShortcutAnnotations() {
        CompiledPredicateProviderRegistry.reference(Gt.class);
        CompiledPredicateProviderRegistry.reference(SplitIn.class);
        CompiledPredicateProviderRegistry.reference(Length.class);

        assertTrue(CompiledPredicateProviderRegistry.containsKey(Gt.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(SplitIn.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(Length.class));
    }

    @Test
    public void shouldRegisterBuiltInCompiledProvidersForCanonicalFamilies() {
        assertSame(CompiledPredicateProviderRegistry.reference(Compare.class), CompiledPredicateProviderRegistry.reference(Selectable.class));
        assertSame(CompiledPredicateProviderRegistry.reference(Compare.class), CompiledPredicateProviderRegistry.reference(Existed.class));
        CompiledPredicateProviderRegistry.reference(Range.class);
        CompiledPredicateProviderRegistry.reference(Membership.class);
        CompiledPredicateProviderRegistry.reference(NullCheck.class);
        CompiledPredicateProviderRegistry.reference(Exists.class);
        CompiledPredicateProviderRegistry.reference(NotExists.class);
        CompiledPredicateProviderRegistry.reference(SubqueryPredicate.class);

        assertTrue(CompiledPredicateProviderRegistry.containsKey(Selectable.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(Existed.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(Range.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(Membership.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(NullCheck.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(Exists.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(NotExists.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(SubqueryPredicate.class));
    }

    @Test
    public void shouldBootstrapRegistryCanonicalRootFirstAndResolveShortcutsByMetaReference() {
        CompiledPredicateProviderRegistry.clear();
        ServiceLoaderBootstrap.reload();

        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(Compare.class));
        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(SubqueryPredicate.class));
        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(Cases.class));
        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Null.class));

        assertFalse(CompiledPredicateProviderRegistry.containsRegistered(Equal.class));
        assertFalse(CompiledPredicateProviderRegistry.containsRegistered(Selectable.class));
        assertFalse(CompiledPredicateProviderRegistry.containsRegistered(Existed.class));
        assertFalse(CompiledPredicateProviderRegistry.containsRegistered(Gt.class));
        assertFalse(CompiledPredicateProviderRegistry.containsRegistered(Exists.class));

        assertSame(CompiledPredicateProviderRegistry.reference(Compare.class), CompiledPredicateProviderRegistry.reference(Equal.class));
        assertSame(CompiledPredicateProviderRegistry.reference(Compare.class), CompiledPredicateProviderRegistry.reference(Selectable.class));
        assertSame(CompiledPredicateProviderRegistry.reference(Compare.class), CompiledPredicateProviderRegistry.reference(Existed.class));
        assertSame(CompiledPredicateProviderRegistry.reference(SubqueryPredicate.class), CompiledPredicateProviderRegistry.reference(Exists.class));

        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(Equal.class));
        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(Selectable.class));
        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(Existed.class));
        assertTrue(CompiledPredicateProviderRegistry.containsRegistered(Exists.class));
    }

    @Test
    public void shouldResolveComposedAnnotationsThroughMetaAnnotationProviders() {
        CompiledPredicateProviderRegistry.reference(CanonicalSelectable.class);
        CompiledPredicateProviderRegistry.reference(ComposedCompareSelectable.class);
        CompiledPredicateProviderRegistry.reference(ComposedEqualSelectable.class);
        CompiledPredicateProviderRegistry.reference(ComposedBetweenSelectable.class);
        CompiledPredicateProviderRegistry.reference(ComposedExistsSelectable.class);

        assertTrue(CompiledPredicateProviderRegistry.containsKey(CanonicalSelectable.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(ComposedCompareSelectable.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(ComposedEqualSelectable.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(ComposedBetweenSelectable.class));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(ComposedExistsSelectable.class));
    }

    @Test
    public void shouldBuildMultipleBuiltInPredicatesThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(MultiQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new MultiQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(4, predicates.size());
        assertTrue(predicates.contains("root.status = root.audit.currentStatus"));
        assertTrue(predicates.contains("root.createdAt BETWEEN root.range.startAt AND root.range.endAt"));
        assertTrue(predicates.contains("root.name LIKE %demo%"));
        assertTrue(predicates.contains("root.deletedAt IS NULL"));
    }

    @Test
    public void shouldBuildComposedAnnotationsThroughStableCollectionEntry() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(ComposedQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        List<String> predicates = computer.toComputerCollection().withArgs(new ComposedQueryModel()).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED).getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(4, predicates.size());
        assertTrue(predicates.contains("root.meta.direct.status = root.meta.direct.currentStatus"));
        assertTrue(predicates.contains("root.meta.composed.status = root.meta.composed.currentStatus"));
        assertTrue(predicates.contains("root.meta.equal.status = root.meta.equal.currentStatus"));
        assertTrue(predicates.contains("root.meta.between.start BETWEEN root.meta.between.lower AND root.meta.between.upper"));
    }

    @Test
    public void shouldBuildComposedSubqueryAnnotationThroughStableCollectionEntry() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(ComposedSubqueryQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        List<String> predicates = computer.toComputerCollection().withArgs(new ComposedSubqueryQueryModel()).withSpecification(root, query, cb)
            .build(BuilderType.SELECTED).getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, predicates.size());
        assertTrue(predicates.contains("EXISTS (SELECT subroot.id WHERE subroot.userId = root.id AND subroot.tenantId = 7)"));
    }

    @Test
    public void shouldBuildZeroConfigEqualAsPureLatestEqualityThroughStableExternalChain() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(DefaultQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        List<String> predicates = computer.toComputerCollection().withArgs(new DefaultQueryModel()).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED).getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertTrue(predicates.contains("root.name = demo"));
        assertTrue(predicates.contains("root.profile.name = profile-demo"));
        assertTrue(predicates.contains("root.audit.updatedAt > root.audit.createdAt"));
    }

    @Test
    public void shouldBuildZeroConfigEqualForGetterOnlyQueryModelThroughStableExternalChain() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(GetterOnlyEqualQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        List<String> predicates = computer.toComputerCollection().withArgs(new GetterOnlyEqualQueryModel()).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED).getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, predicates.size());
        assertEquals("root.status = ACTIVE", predicates.get(0));
    }

    @Test
    public void shouldBuildZeroConfigSelectableAndExistedThroughStableExternalChain() {
        AnnotationCollection collection = CollectionCache.getAnnotationCollection(ZeroConfigSelectableExistedQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        List<String> selection = collection.toComputerCollection().withArgs(new ZeroConfigSelectableExistedQueryModel()).withSpecification(root, null, cb)
            .buildSelection().getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());
        List<String> existence = collection.toComputerCollection().withArgs(new ZeroConfigSelectableExistedQueryModel()).withSpecification(root, null, cb)
            .buildExistence().getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, selection.size());
        assertEquals("root.selectableStatus = ACTIVE", selection.get(0));
        assertEquals(1, existence.size());
        assertEquals("root.existedStatus = ENABLED", existence.get(0));
    }

    @Test
    public void shouldKeepExistedAnnotationsOutOfSelectedView() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(SelectedAndExistedQueryModel.class).build();

        assertFalse(collection.isSelectionEmpty());
        assertFalse(collection.isExistenceEmpty());
        assertEquals(1, collection.getSelectionPredicateSpecs().size());
        assertEquals(1, collection.getExistencePredicateSpecs().size());
        assertEquals("name", collection.getSelectionPredicateSpecs().iterator().next().getValueFieldName());
        assertEquals("status", collection.getExistencePredicateSpecs().iterator().next().getValueFieldName());
    }

    @Test
    public void shouldBuildSelectionAndExistenceThroughSemanticEntryPoints() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(SelectedAndExistedQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection selection = collection.toComputerCollection().withArgs(new SelectedAndExistedQueryModel()).withSpecification(root, null, cb)
            .buildSelection();
        ComputerCollection existence = collection.toComputerCollection().withArgs(new SelectedAndExistedQueryModel()).withSpecification(root, null, cb)
            .buildExistence();

        assertTrue(selection.isSelection());
        assertFalse(selection.isExistence());
        assertFalse(selection.isEmpty());
        assertEquals("root.name = demo", debug(selection.getPredicateResults().iterator().next().getPredicate()));

        assertTrue(existence.isExistence());
        assertFalse(existence.isSelection());
        assertFalse(existence.isEmpty());
        assertEquals("root.status = root.audit.currentStatus", debug(existence.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldExposeSemanticFacadeEntryPoints() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(SelectedAndExistedQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection selectionCollection = CompiledPredicateFacade.selection(collection, new SelectedAndExistedQueryModel(), root, null, cb);
        ComputerCollection existenceCollection = CompiledPredicateFacade.existence(collection, new SelectedAndExistedQueryModel(), root, null, cb);
        Predicate selectionPredicate = CompiledPredicateFacade.selectionPredicate(collection, new SelectedAndExistedQueryModel(), root, null, cb);
        Predicate existencePredicate = CompiledPredicateFacade.existencePredicate(collection, new SelectedAndExistedQueryModel(), root, null, cb);
        Predicate[] selectionPredicateArray = CompiledPredicateFacade.selectionPredicateArray(collection, new SelectedAndExistedQueryModel(), root, null, cb);
        Predicate[] existencePredicateArray = CompiledPredicateFacade.existencePredicateArray(collection, new SelectedAndExistedQueryModel(), root, null, cb);

        assertTrue(selectionCollection.isSelection());
        assertTrue(existenceCollection.isExistence());
        assertEquals("root.name = demo", debug(selectionCollection.getPredicateResults().iterator().next().getPredicate()));
        assertEquals("root.status = root.audit.currentStatus", debug(existenceCollection.getPredicateResults().iterator().next().getPredicate()));
        assertEquals("root.name = demo", debug(selectionPredicate));
        assertEquals("root.status = root.audit.currentStatus", debug(existencePredicate));
        assertEquals(1, selectionPredicateArray.length);
        assertEquals(1, existencePredicateArray.length);
        assertEquals("root.name = demo", debug(selectionPredicateArray[0]));
        assertEquals("root.status = root.audit.currentStatus", debug(existencePredicateArray[0]));
    }

    @Test
    public void shouldExposePredicateGroupTreeThroughComputerCollection() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(MultiQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new MultiQueryModel(), root, null, cb, BuilderType.SELECTED);
        PredicateGroupSpec groupSpec = computerCollection.getPredicateGroupSpec();

        assertEquals(PredicateGroupSpec.JunctionType.AND, groupSpec.getJunctionType());
        assertEquals(4, groupSpec.getAnnotations().size());
        assertTrue(groupSpec.getGroups().isEmpty());
    }

    @Test
    public void shouldBuildFunctionBasedComparableThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(FunctionQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new FunctionQueryModel(), root, null, cb, BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("lower(root.keyword) = lower(trim(root.keyword))", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildLengthPredicatesThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(LengthQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new LengthQueryModel(), root, null, cb, BuilderType.SELECTED);
        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("length(root.name) >= 3", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildExtendedLengthPredicatesThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(ExtendedLengthQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new ExtendedLengthQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(7, predicates.size());
        assertTrue(predicates.contains("length(root.name) = 4"), predicates.toString());
        assertTrue(predicates.contains("length(root.alias) != 2"), predicates.toString());
        assertTrue(predicates.contains("length(root.nickName) < 12"), predicates.toString());
        assertTrue(predicates.contains("length(root.code) <= 8"), predicates.toString());
        assertTrue(predicates.contains("length(root.serial) > 6"), predicates.toString());
        assertTrue(predicates.contains("length(root.memo) BETWEEN 3 AND 9"), predicates.toString());
        assertTrue(predicates.contains("length(root.description) NOT BETWEEN 5 AND 10"), predicates.toString());
    }

    @Test
    public void shouldBuildCustomLengthExpressionsThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CustomLengthExpressionQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CustomLengthExpressionQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("length(root.alias) = 5"), predicates.toString());
        assertTrue(predicates.contains("length(root.code) = 7"), predicates.toString());
        assertTrue(predicates.contains("NOT (length(root.summary) >= 11)"), predicates.toString());
    }

    @Test
    public void shouldBuildComposedLengthShortcutThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(ComposedLengthQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new ComposedLengthQueryModel(), root, null, cb, BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("length(root.displayName) >= 6", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildZeroConfigLengthUsingTrimmedFieldSuffix() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(ZeroConfigLengthQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new ZeroConfigLengthQueryModel(), root, null, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("length(root.name) = 4", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildZeroConfigLengthFromGetterAnnotation() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(GetterLengthQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new GetterLengthQueryModel(), root, null, cb, BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("length(root.name) = 4", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildZeroConfigLengthUsingFieldNameWhenNoSuffix() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(InvalidZeroConfigLengthTargetQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new InvalidZeroConfigLengthTargetQueryModel(), root, null, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("length(root.charCount) = 4", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldRejectInvalidLengthBetweenValueInsteadOfIgnoringIt() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(InvalidLengthBetweenValueQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> CompiledPredicateFacade.build(collection, new InvalidLengthBetweenValueQueryModel(), root, null, cb, BuilderType.SELECTED));

        assertTrue(exception.getMessage().contains("requires exactly 2 values"), exception.getMessage());
        assertTrue(exception.getMessage().contains("memoLength"), exception.getMessage());
    }

    @Test
    public void shouldIgnoreNullLengthInCollectionItemsLikeJpaPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(LengthInWithNullItemsQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new LengthInWithNullItemsQueryModel(), root, null, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals(null, computerCollection.getPredicateResults().iterator().next().getPredicate());
    }

    @Test
    public void shouldBuildRepeatableLengthAnnotations() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(RepeatableLengthQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new RepeatableLengthQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(2, predicates.size());
        assertTrue(predicates.contains("length(root.name) >= 3"), predicates.toString());
        assertTrue(predicates.contains("length(root.alias) <= 8"), predicates.toString());
    }

    @Test
    public void shouldBuildLengthInAndNotInPredicates() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(LengthInQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new LengthInQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(2, predicates.size());
        assertTrue(predicates.contains("length(root.name) IN (SELECT [3, 5])"), predicates.toString());
        assertTrue(predicates.contains("length(root.alias) NOT IN (SELECT [2, 4])"), predicates.toString());
    }

    @Test
    public void shouldIgnoreNullLengthValueByDefault() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(NullableLengthQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new NullableLengthQueryModel(), root, null, cb, BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals(null, computerCollection.getPredicateResults().iterator().next().getPredicate());
    }

    @Test
    public void shouldRejectUnsupportedLengthCompareOpDuringCompilation() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> AnnotationCollection.Builder.of(InvalidLengthQueryModel.class).build());

        assertTrue(exception.getMessage().contains("@Length"), exception.getMessage());
        assertTrue(exception.getMessage().contains("CompareOp.LIKE"), exception.getMessage());
    }

    @Test
    public void shouldRejectUnsupportedLengthCompareOpForComposedAnnotations() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> AnnotationCollection.Builder.of(InvalidComposedLengthQueryModel.class).build());

        assertTrue(exception.getMessage().contains("@InvalidComposedLength"), exception.getMessage());
        assertTrue(exception.getMessage().contains("CompareOp.LIKE"), exception.getMessage());
    }

    @Test
    public void shouldBuildZeroConfigAndNestedPathPredicatesThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(DefaultQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new DefaultQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("root.name = demo"));
        assertTrue(predicates.contains("root.profile.name = profile-demo"));
        assertTrue(predicates.contains("root.audit.updatedAt > root.audit.createdAt"));
    }

    @Test
    public void shouldBuildDirectComparableThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(DirectComparableQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new DirectComparableQueryModel(), root, null, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.status = root.audit.currentStatus", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCanonicalTargetInTableWithNestedPaths() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalTargetInTableQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalTargetInTableQueryModel(), root, query, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.audit.status IN (SELECT subroot.inner.currentStatus WHERE subroot.outer.status = subroot.audit.currentStatus)",
            debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildInTableWithAdditionalWhereGroupTree() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(GroupedInTableQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new GroupedInTableQueryModel(), root, query, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals(
            "root.audit.status IN (SELECT subroot.inner.currentStatus WHERE subroot.outer.status = subroot.audit.currentStatus AND subroot.tenantId = 7 AND subroot.status = ACTIVE OR subroot.status = PENDING)",
            debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildInTableThroughCanonicalWhereCompare() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalComparedInTableQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalComparedInTableQueryModel(), root, query, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.audit.status IN (SELECT subroot.inner.currentStatus WHERE subroot.outer.status != lower(subroot.audit.currentStatus))",
            debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldPreferCanonicalInTableConfigurationOverShortcutFieldsAtRuntime() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(ConflictedCanonicalInTableQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new ConflictedCanonicalInTableQueryModel(), root, query, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.outer.status IN (SELECT subroot.inner.currentStatus WHERE subroot.status != lower(subroot.audit.currentStatus))",
            debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesThroughCompiledMainPath() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(CasesQueryModel.class, new CasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("CASE:root.status", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesThroughDefaultCanonicalFallback() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(DefaultCasesQueryModel.class, new DefaultCasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.status = ACTIVE", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesThroughAlwaysMatchWhenCaseWhenIsEmpty() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(AlwaysMatchCasesQueryModel.class, new AlwaysMatchCasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.status = ACTIVE", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesThroughCanonicalCaseThen() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(CanonicalThenCasesQueryModel.class, new CanonicalThenCasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.status = root.audit.currentStatus", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldExposeCanonicalCaseSpecsToPredicateProcessorContext() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(CanonicalContextCasesQueryModel.class, new CanonicalContextCasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("CTX:EQ:EQ", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesThroughCanonicalCaseWhen() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection matched = stableSelected(CanonicalWhenCasesQueryModel.class, new CanonicalWhenCasesQueryModel("ACTIVE"), root, cb);
        ComputerCollection notMatched = stableSelected(CanonicalWhenCasesQueryModel.class, new CanonicalWhenCasesQueryModel("INACTIVE"), root, cb);

        assertEquals(1, matched.getPredicateResults().size());
        assertEquals("root.status = ACTIVE", debug(matched.getPredicateResults().iterator().next().getPredicate()));
        assertEquals(1, notMatched.getPredicateResults().size());
        assertEquals(null, notMatched.getPredicateResults().iterator().next().getPredicate());
    }

    @Test
    public void shouldPreferCanonicalCaseWhenOverTypedPredicateExtension() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection matched = stableSelected(ConflictedCanonicalWhenCasesQueryModel.class, new ConflictedCanonicalWhenCasesQueryModel("ACTIVE"), root,
            cb);
        ComputerCollection notMatched = stableSelected(ConflictedCanonicalWhenCasesQueryModel.class, new ConflictedCanonicalWhenCasesQueryModel("INACTIVE"),
            root, cb);

        assertEquals(1, matched.getPredicateResults().size());
        assertEquals("root.status = ACTIVE", debug(matched.getPredicateResults().iterator().next().getPredicate()));
        assertEquals(1, notMatched.getPredicateResults().size());
        assertEquals(null, notMatched.getPredicateResults().iterator().next().getPredicate());
    }

    @Test
    public void shouldPreferExplicitProcessorOverCanonicalCaseThen() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(ConflictedCanonicalThenCasesQueryModel.class, new ConflictedCanonicalThenCasesQueryModel(), root,
            cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("CASE:root.status", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldApplyBranchOptionsWhenBuildingCasesPredicates() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(NegatedTrimmedCasesQueryModel.class, new NegatedTrimmedCasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("NOT (root.status = ACTIVE)", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldContinueToNextCaseBranchWhenCurrentBranchIsIgnoredByOptions() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(FallbackCasesQueryModel.class, new FallbackCasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.status = FALLBACK", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesFromProcessorRefClass() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(CasesQueryModel.class, new CasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("CASE:root.status", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesWhenPredicateFromPredicateRefClass() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(CasesQueryModel.class, new CasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("CASE:root.status", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldPreferStructuredCaseRefsAtRuntime() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(CasesQueryModel.class, new CasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("CASE:root.status", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesBranchFieldFromCanonicalLeftPath() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(CanonicalBranchCasesQueryModel.class, new CanonicalBranchCasesQueryModel(), root, cb);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("CASE:root.audit.branch.status", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCasesThroughStructuredCaseWhenGroup() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection matched = stableSelected(GroupWhenCasesQueryModel.class, new GroupWhenCasesQueryModel("ACTIVE", "PENDING"), root, cb);
        ComputerCollection notMatched = stableSelected(GroupWhenCasesQueryModel.class, new GroupWhenCasesQueryModel("ACTIVE", "DONE"), root, cb);

        assertEquals("root.status = ACTIVE", debug(matched.getPredicateResults().iterator().next().getPredicate()));
        assertEquals(null, notMatched.getPredicateResults().iterator().next().getPredicate());
    }

    @Test
    public void shouldBuildCasesThroughStructuredCaseThenGroup() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(GroupThenCasesQueryModel.class, new GroupThenCasesQueryModel(), root, cb);

        assertEquals("root.status = ACTIVE AND root.phase = PENDING", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldFallbackToExplicitCaseElseBranch() {
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        ComputerCollection computerCollection = stableSelected(ElseCasesQueryModel.class, new ElseCasesQueryModel("INACTIVE"), root, cb);

        assertEquals("root.status = FALLBACK", debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldResolveConstraintAnnotationThroughCompiledRegistry() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(ConstraintQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        Predicate predicate = computer.toComputerCollection().withArgs(new ConstraintQueryModel()).withSpecification(root, null, cb).build(BuilderType.SELECTED)
            .getPredicate(cb);

        assertEquals("root.status = root.audit.currentStatus", debug(predicate));
        assertTrue(CompiledPredicateProviderRegistry.containsKey(LocalConstraintCompiledSelectable.class));
    }

    @Test
    public void shouldBuildCanonicalCompareThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CompareQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CompareQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("root.name = demo"));
        assertTrue(predicates.contains("root.audit.updatedAt > root.audit.createdAt"));
        assertTrue(predicates.contains("root.createdAt BETWEEN root.range.startAt AND root.range.endAt"));
    }

    @Test
    public void shouldBuildCanonicalExprDslThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalDslQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalDslQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("root.userName = lower(root.keyword)"));
        assertTrue(predicates.contains("root.status = root.audit.currentStatus"));
        assertTrue(predicates.contains("root.createdAt BETWEEN root.range.startAt AND root.range.endAt"));
    }

    @Test
    public void shouldBuildCanonicalCrossFieldValueOperandsThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalValueFieldQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalValueFieldQueryModel(), root, query, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("root.status = keyword-demo"));
        assertTrue(predicates.contains("lower(root.userName) = lower(keyword-demo)"));
        assertTrue(predicates.contains("EXISTS (SELECT subroot.id WHERE subroot.userId = root.id AND subroot.tenantId = 7 AND subroot.deleted = false)"));
    }

    @Test
    public void shouldBuildCanonicalNotBetweenShortcutThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalNotBetweenQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalNotBetweenQueryModel(), root, null, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.createdAt NOT BETWEEN root.range.startAt AND root.range.endAt",
            debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildCanonicalDslThroughShortcutAnnotations() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalShortcutDslQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalShortcutDslQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("root.audit.updatedAt > root.audit.createdAt"));
        assertTrue(predicates.contains("root.userName LIKE %lower(Demo)%"));
        assertTrue(predicates.contains("root.deletedAt IS NULL"));
    }

    @Test
    public void shouldBuildIgnoreCaseShortcutAnnotationsThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(IgnoreCaseShortcutQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new IgnoreCaseShortcutQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("lower(root.status) = lower(ACTIVE)"), predicates.toString());
        assertTrue(predicates.contains("root.userName = lower(Demo)"), predicates.toString());
        assertTrue(predicates.contains("lower(root.keyword) LIKE %lower(Needle)%"), predicates.toString());
    }

    @Test
    public void shouldBuildIgnoreCaseShortcutsThroughStableExternalChain() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(IgnoreCaseShortcutQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        List<String> predicates = computer.toComputerCollection().withArgs(new IgnoreCaseShortcutQueryModel()).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED).getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("lower(root.status) = lower(ACTIVE)"), predicates.toString());
        assertTrue(predicates.contains("root.userName = lower(Demo)"), predicates.toString());
        assertTrue(predicates.contains("lower(root.keyword) LIKE %lower(Needle)%"), predicates.toString());
    }

    @Test
    public void shouldBuildZeroConfigShortcutWrappersThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(ZeroConfigShortcutQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new ZeroConfigShortcutQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(7, predicates.size());
        assertTrue(predicates.contains("root.status != INACTIVE"));
        assertTrue(predicates.contains("root.score >= 90"));
        assertTrue(predicates.contains("root.rank < 10"));
        assertTrue(predicates.contains("root.level <= 2"));
        assertTrue(predicates.contains("root.keyword LIKE %demo%"));
        assertTrue(predicates.contains("root.code LIKE PRE%"));
        assertTrue(predicates.contains("root.createdAt BETWEEN start AND end"));
    }

    @Test
    public void shouldBuildExtendedShortcutAliasFamiliesThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(ExtendedShortcutAliasQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new ExtendedShortcutAliasQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream()
            .map(it -> it.getSpec().getBindingPath() + "=" + (null == it.getPredicate() ? "<null>" : debug(it.getPredicate()))).collect(Collectors.toList());

        assertEquals(8, predicates.size());
        assertTrue(predicates.contains("changedAt=root.updatedAt > root.createdAt"), predicates.toString());
        assertTrue(predicates.contains("score=root.score >= 60"), predicates.toString());
        assertTrue(predicates.contains("rank=root.rank < 99"), predicates.toString());
        assertTrue(predicates.contains("level=root.level <= 5"), predicates.toString());
        assertTrue(predicates.contains("keyword=root.keyword NOT LIKE %demo%"), predicates.toString());
        assertTrue(predicates.contains("code=root.code LIKE %suffix"), predicates.toString());
        assertTrue(predicates.contains("otherStatuses=root.otherStatuses NOT IN (SELECT [DISABLED])"), predicates.toString());
        assertTrue(predicates.contains("deletedAt=<null>") || predicates.contains("deletedAt=root.deletedAt IS NOT NULL"), predicates.toString());
    }

    @Test
    public void shouldBuildCanonicalFamilyAnnotationsThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalFamilyQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalFamilyQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(4, predicates.size());
        assertTrue(predicates.contains("root.createdAt BETWEEN start AND end"));
        assertTrue(predicates.contains("root.audit.updatedAt BETWEEN root.audit.createdAt AND root.audit.finishedAt"));
        assertTrue(predicates.contains("root.status IN (SELECT [1, 2, 3])"));
        assertTrue(predicates.contains("root.deletedAt IS NOT NULL"));
    }

    @Test
    public void shouldNormalizePrimitiveArraysThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(PrimitiveArrayQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new PrimitiveArrayQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(2, predicates.size());
        assertTrue(predicates.contains("root.status IN (SELECT [1, 2, 3])"));
        assertTrue(predicates.contains("root.createdAt BETWEEN 10 AND 20"));
    }

    @Test
    public void shouldBuildCanonicalExistsFamilyThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalExistsQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalExistsQueryModel(), root, query, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(2, predicates.size());
        assertTrue(predicates.contains("EXISTS (SELECT subroot.id WHERE subroot.userId = root.id)"));
        assertTrue(predicates.contains("NOT EXISTS (SELECT subroot.id WHERE subroot.userId = root.id)"));
    }

    @Test
    public void shouldBuildCanonicalExistsFamilyWithNestedWhereGroupThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalExistsGroupQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalExistsGroupQueryModel(), root, query, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(2, predicates.size());
        assertTrue(predicates.contains(
            "EXISTS (SELECT subroot.id WHERE subroot.userId = root.id AND subroot.tenantId = 7 AND subroot.deleted = false AND subroot.status = ACTIVE OR subroot.status = PENDING)"));
        assertTrue(predicates.contains(
            "NOT EXISTS (SELECT subroot.id WHERE subroot.userId = root.id AND subroot.tenantId = 7 AND subroot.deleted = false AND subroot.status = ACTIVE OR subroot.status = PENDING)"));
    }

    @Test
    public void shouldKeepNotExistsPredicateShapeAfterSharedSubqueryRefactor() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(CanonicalExistsGroupQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new CanonicalExistsGroupQueryModel(), root, query, cb,
            BuilderType.SELECTED);
        String notExists = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).filter(it -> it.startsWith("NOT EXISTS"))
            .findFirst().orElse(null);

        assertEquals(
            "NOT EXISTS (SELECT subroot.id WHERE subroot.userId = root.id AND subroot.tenantId = 7 AND subroot.deleted = false AND subroot.status = ACTIVE OR subroot.status = PENDING)",
            notExists);
    }

    @Test
    public void shouldBuildGenericSubqueryPredicateFamilyThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(GenericSubqueryQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");
        AbstractQuery<?> query = proxy(AbstractQuery.class, "query");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new GenericSubqueryQueryModel(), root, query, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(3, predicates.size());
        assertTrue(predicates.contains("EXISTS (SELECT subroot.id WHERE subroot.userId = root.id)"));
        assertTrue(predicates.contains("NOT EXISTS (SELECT subroot.id WHERE subroot.userId = root.id)"));
        assertTrue(predicates.contains("root.audit.status IN (SELECT subroot.inner.currentStatus WHERE subroot.outer.status = subroot.audit.currentStatus)"));
    }

    @Test
    public void shouldBuildRecursiveNestedFunctionDslThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(RecursiveCanonicalDslQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new RecursiveCanonicalDslQueryModel(), root, null, cb,
            BuilderType.SELECTED);

        assertEquals(1, computerCollection.getPredicateResults().size());
        assertEquals("root.userName = lower(concat(trim(root.keyword), upper(root.suffix)))",
            debug(computerCollection.getPredicateResults().iterator().next().getPredicate()));
    }

    @Test
    public void shouldBuildTypedFilterInAndNotInThroughCompiledMainPath() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(TypedFilterQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new TypedFilterQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(2, predicates.size());
        assertTrue(predicates.contains("root.statuses IN (SELECT [ACTIVE])"));
        assertTrue(predicates.contains("root.otherStatuses NOT IN (SELECT [DISABLED])"));
    }

    @Test
    public void shouldBuildStructuredPredicateRefsForTypedFilters() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(StructuredTypedFilterQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new StructuredTypedFilterQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(2, predicates.size());
        assertTrue(predicates.contains("root.statuses IN (SELECT [ACTIVE])"));
        assertTrue(predicates.contains("root.otherStatuses NOT IN (SELECT [DISABLED])"));
    }

    @Test
    public void shouldBuildStructuredPredicateRefForDirectMembershipAnnotation() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(StructuredMembershipQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new StructuredMembershipQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, predicates.size());
        assertEquals("root.statuses IN (SELECT [ACTIVE])", predicates.get(0));
    }

    @Test
    public void shouldBuildStructuredPredicateRefForInShortcutAnnotation() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(StructuredInQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new StructuredInQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, predicates.size());
        assertEquals("root.statuses IN (SELECT [ACTIVE])", predicates.get(0));
    }

    @Test
    public void shouldBuildSplitCollectionPolicyThroughInShortcutAnnotation() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(SplitPolicyInQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new SplitPolicyInQueryModel(), root, null, cb, BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, predicates.size());
        assertEquals("root.statuses IN (SELECT [1, 2, 3])", predicates.get(0));
    }

    @Test
    public void shouldBuildStructuredPredicateRefForDirectCompareInAnnotation() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(StructuredCompareInQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new StructuredCompareInQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, predicates.size());
        assertEquals("root.statuses IN (SELECT [ACTIVE])", predicates.get(0));
    }

    @Test
    public void shouldBuildCompareInFromPredicateRefClassAtRuntime() {
        AnnotationCollection collection = AnnotationCollection.Builder.of(StructuredCompareInQueryModel.class).build();
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        ComputerCollection computerCollection = CompiledPredicateFacade.build(collection, new StructuredCompareInQueryModel(), root, null, cb,
            BuilderType.SELECTED);
        List<String> predicates = computerCollection.getPredicateResults().stream().map(it -> debug(it.getPredicate())).collect(Collectors.toList());

        assertEquals(1, predicates.size());
        assertEquals("root.statuses IN (SELECT [ACTIVE])", predicates.get(0));
    }

    @Test
    public void shouldBuildSplitInThroughStableExternalChain() {
        AnnotationCollection computer = CollectionCache.getAnnotationCollection(SplitQueryModel.class);
        CriteriaBuilder cb = proxy(CriteriaBuilder.class, "cb");
        Root<?> root = proxy(Root.class, "root");

        Predicate predicate = computer.toComputerCollection().withArgs(new SplitQueryModel()).withSpecification(root, null, cb).build(BuilderType.SELECTED)
            .getPredicate(cb);

        assertEquals("root.statuses IN (SELECT [1, 2, 3])", debug(predicate));
    }

    private static ComputerCollection stableSelected(final Class<?> modelClass, final Object args, final Root<?> root, final CriteriaBuilder cb) {
        return CollectionCache.getAnnotationCollection(modelClass).toComputerCollection().withArgs(args).withSpecification(root, null, cb)
            .build(BuilderType.SELECTED);
    }

    private static <T> T proxy(final Class<T> type, final String debug) {
        return CriteriaDebugProxies.proxy(type, debug, ComputerCollectionCompiledPathTest.class);
    }

    private static String debug(final Object proxy) {
        return CriteriaDebugProxies.debug(proxy);
    }

    @SuppressWarnings("unused")
    public static final class QueryModel {

        @Equal(right = @Expr(path = "audit.currentStatus"))
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class SplitQueryModel {

        @SplitIn(left = @Expr(type = ExprType.PATH, path = "statuses"), collection = @CollectionPolicy(split = true, targetType = TargetType.TO_INTEGER))
        private String ids = "1,2,3";

        public String getIds() {
            return this.ids;
        }

        public void setIds(final String ids) {
            this.ids = ids;
        }

    }

    @SuppressWarnings("unused")
    public static final class MultiQueryModel {

        @Equal(right = @Expr(path = "audit.currentStatus"))
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
    public static final class FunctionQueryModel {

        @Equal(
            left = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "keyword") })),
            right = @Expr(type = ExprType.FUNCTION, function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.FUNCTION,
                function = @NestedExprFunction(name = "trim", type = String.class, args = { @NestedExprArg(type = ExprType.PATH, path = "keyword") })) })))
        private String keyword = " Demo ";

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

    }

    @SuppressWarnings("unused")
    public static final class LengthQueryModel {

        @Length(op = CompareOp.GTE, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "name") })))
        private Integer nameLength = Integer.valueOf(3);

        public Integer getNameLength() {
            return this.nameLength;
        }

        public void setNameLength(final Integer nameLength) {
            this.nameLength = nameLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class ExtendedLengthQueryModel {

        @Length(op = CompareOp.EQ, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "name") })))
        private Integer nameLength = Integer.valueOf(4);

        @Length(op = CompareOp.NE, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "alias") })))
        private Integer aliasLength = Integer.valueOf(2);

        @Length(op = CompareOp.LT, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "nickName") })))
        private Integer nickNameLength = Integer.valueOf(12);

        @Length(op = CompareOp.LTE, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "code") })))
        private Integer codeLength = Integer.valueOf(8);

        @Length(op = CompareOp.GT, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "serial") })))
        private Integer serialLength = Integer.valueOf(6);

        @Length(op = CompareOp.BETWEEN, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "memo") })))
        private List<Integer> memoLength = Arrays.asList(Integer.valueOf(3), Integer.valueOf(9));

        @Length(op = CompareOp.NOT_BETWEEN, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "description") })))
        private List<Integer> descriptionLength = Arrays.asList(Integer.valueOf(5), Integer.valueOf(10));

        public Integer getNameLength() {
            return this.nameLength;
        }

        public Integer getAliasLength() {
            return this.aliasLength;
        }

        public Integer getNickNameLength() {
            return this.nickNameLength;
        }

        public Integer getCodeLength() {
            return this.codeLength;
        }

        public Integer getSerialLength() {
            return this.serialLength;
        }

        public List<Integer> getMemoLength() {
            return this.memoLength;
        }

        public List<Integer> getDescriptionLength() {
            return this.descriptionLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class CustomLengthExpressionQueryModel {

        @Length(op = CompareOp.EQ, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "alias") })))
        private Integer aliasLength = Integer.valueOf(5);

        @Length(op = CompareOp.EQ,
            left = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "code") })),
            right = @Expr(type = ExprType.VALUE, valueField = "expectedCodeLength"))
        private Integer ignoredValue = Integer.valueOf(0);

        private Integer expectedCodeLength = Integer.valueOf(7);

        @Length(op = CompareOp.GTE,
            left = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "summary") })),
            options = @PredicateOptions(not = true))
        private Integer summaryLength = Integer.valueOf(11);

        public Integer getAliasLength() {
            return this.aliasLength;
        }

        public Integer getIgnoredValue() {
            return this.ignoredValue;
        }

        public Integer getExpectedCodeLength() {
            return this.expectedCodeLength;
        }

        public Integer getSummaryLength() {
            return this.summaryLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class NullableLengthQueryModel {

        @Length(op = CompareOp.GT, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "name") })))
        private Integer nameLength;

        public Integer getNameLength() {
            return this.nameLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class InvalidLengthQueryModel {

        @Length(op = CompareOp.LIKE)
        private Integer invalidLength = Integer.valueOf(3);

        public Integer getInvalidLength() {
            return this.invalidLength;
        }

        public void setInvalidLength(final Integer invalidLength) {
            this.invalidLength = invalidLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class InvalidComposedLengthQueryModel {

        @InvalidComposedLength
        private Integer invalidLength = Integer.valueOf(3);

        public Integer getInvalidLength() {
            return this.invalidLength;
        }

        public void setInvalidLength(final Integer invalidLength) {
            this.invalidLength = invalidLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class ComposedLengthQueryModel {

        @MinDisplayNameLength
        private Integer displayNameLength = Integer.valueOf(6);

        public Integer getDisplayNameLength() {
            return this.displayNameLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class ZeroConfigLengthQueryModel {

        @Length
        private Integer nameLength = Integer.valueOf(4);

        public Integer getNameLength() {
            return this.nameLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class InvalidZeroConfigLengthTargetQueryModel {

        @Length
        private Integer charCount = Integer.valueOf(4);

        public Integer getCharCount() {
            return this.charCount;
        }

    }

    @SuppressWarnings("unused")
    public static final class GetterLengthQueryModel {

        private final Integer nameLength = Integer.valueOf(4);

        @Length
        public Integer getNameLength() {
            return this.nameLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class InvalidLengthBetweenValueQueryModel {

        @Length(op = CompareOp.BETWEEN, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "memo") })))
        private List<Integer> memoLength = Arrays.asList(Integer.valueOf(3));

        public List<Integer> getMemoLength() {
            return this.memoLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class RepeatableLengthQueryModel {

        @Length(op = CompareOp.GTE,
            left = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "name") })),
            right = @Expr(type = ExprType.VALUE, valueField = "minNameLength"))
        @Length(op = CompareOp.LTE,
            left = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "alias") })),
            right = @Expr(type = ExprType.VALUE, valueField = "maxAliasLength"))
        private Integer ignored = Integer.valueOf(0);

        private Integer minNameLength = Integer.valueOf(3);

        private Integer maxAliasLength = Integer.valueOf(8);

        public Integer getIgnored() {
            return this.ignored;
        }

        public Integer getMinNameLength() {
            return this.minNameLength;
        }

        public Integer getMaxAliasLength() {
            return this.maxAliasLength;
        }

    }

    @SuppressWarnings("unused")
    public static final class LengthInQueryModel {

        @Length(op = CompareOp.IN, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "name") })))
        private List<Integer> nameLengthOptions = Arrays.asList(Integer.valueOf(3), Integer.valueOf(5));

        @Length(op = CompareOp.NOT_IN, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "alias") })))
        private List<Integer> aliasLengthOptions = Arrays.asList(Integer.valueOf(2), Integer.valueOf(4));

        public List<Integer> getNameLengthOptions() {
            return this.nameLengthOptions;
        }

        public List<Integer> getAliasLengthOptions() {
            return this.aliasLengthOptions;
        }

    }

    @SuppressWarnings("unused")
    public static final class LengthInWithNullItemsQueryModel {

        @Length(op = CompareOp.IN, left = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "name") })))
        private List<Integer> nameLengthOptions = Arrays.asList((Integer) null);

        public List<Integer> getNameLengthOptions() {
            return this.nameLengthOptions;
        }

    }

    @SuppressWarnings("unused")
    public static final class DefaultQueryModel {

        @Equal
        private String name = "demo";

        @Equal(left = @Expr(type = ExprType.PATH, path = "profile.name"))
        private String profileName = "profile-demo";

        @GreaterThan(left = @Expr(type = ExprType.PATH, path = "audit.updatedAt"), right = @Expr(type = ExprType.PATH, path = "audit.createdAt"))
        private String changedAt = "ignored";

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getProfileName() {
            return this.profileName;
        }

        public void setProfileName(final String profileName) {
            this.profileName = profileName;
        }

        public String getChangedAt() {
            return this.changedAt;
        }

        public void setChangedAt(final String changedAt) {
            this.changedAt = changedAt;
        }

    }

    @SuppressWarnings("unused")
    public static final class GetterOnlyEqualQueryModel {

        @Equal(left = @Expr(type = ExprType.PATH, path = "status"))
        private final String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

    }

    @SuppressWarnings("unused")
    public static final class ZeroConfigSelectableExistedQueryModel {

        @Selectable
        private String selectableStatus = "ACTIVE";

        @Existed
        private String existedStatus = "ENABLED";

        public String getSelectableStatus() {
            return this.selectableStatus;
        }

        public void setSelectableStatus(final String selectableStatus) {
            this.selectableStatus = selectableStatus;
        }

        public String getExistedStatus() {
            return this.existedStatus;
        }

        public void setExistedStatus(final String existedStatus) {
            this.existedStatus = existedStatus;
        }

    }

    @SuppressWarnings("unused")
    public static final class PrimitiveArrayQueryModel {

        @In(left = @Expr(type = ExprType.PATH, path = "status"))
        private int[] statuses = { 1, 2, 3 };

        @Between(left = @Expr(type = ExprType.PATH, path = "createdAt"))
        private long[] createdAtRange = { 10L, 20L };

        public int[] getStatuses() {
            return this.statuses;
        }

        public void setStatuses(final int[] statuses) {
            this.statuses = statuses;
        }

        public long[] getCreatedAtRange() {
            return this.createdAtRange;
        }

        public void setCreatedAtRange(final long[] createdAtRange) {
            this.createdAtRange = createdAtRange;
        }

    }

    @SuppressWarnings("unused")
    public static final class SelectedAndExistedQueryModel {

        @Equal
        private String name = "demo";

        @Unique(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.PATH, path = "audit.currentStatus"))
        private String status = "ACTIVE";

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class ComposedQueryModel {

        @CanonicalSelectable
        private String canonicalSelectable = "ACTIVE";

        @ComposedCompareSelectable
        private String composedCompare = "ACTIVE";

        @ComposedEqualSelectable
        private String composedEqual = "ACTIVE";

        @ComposedBetweenSelectable
        private java.util.List<String> composedBetween = Arrays.asList("ignored-lower", "ignored-upper");

        public String getCanonicalSelectable() {
            return this.canonicalSelectable;
        }

        public void setCanonicalSelectable(final String canonicalSelectable) {
            this.canonicalSelectable = canonicalSelectable;
        }

        public String getComposedCompare() {
            return this.composedCompare;
        }

        public void setComposedCompare(final String composedCompare) {
            this.composedCompare = composedCompare;
        }

        public String getComposedEqual() {
            return this.composedEqual;
        }

        public void setComposedEqual(final String composedEqual) {
            this.composedEqual = composedEqual;
        }

        public java.util.List<String> getComposedBetween() {
            return this.composedBetween;
        }

        public void setComposedBetween(final java.util.List<String> composedBetween) {
            this.composedBetween = composedBetween;
        }

    }

    @SuppressWarnings("unused")
    public static final class DirectComparableQueryModel {

        @Equal(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.PATH, path = "audit.currentStatus"))
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class ComposedSubqueryQueryModel {

        @ComposedExistsSelectable
        private Boolean existsAudit = Boolean.TRUE;

        private Integer tenantId = Integer.valueOf(7);

        public Boolean getExistsAudit() {
            return this.existsAudit;
        }

        public void setExistsAudit(final Boolean existsAudit) {
            this.existsAudit = existsAudit;
        }

        public Integer getTenantId() {
            return this.tenantId;
        }

        public void setTenantId(final Integer tenantId) {
            this.tenantId = tenantId;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalTargetInTableQueryModel {

        @InTable(targetEntity = MultiQueryModel.class, left = @Expr(type = ExprType.PATH, path = "audit.status"),
            right = @Expr(type = ExprType.PATH, path = "inner.currentStatus"),
            whereCompare = @Compare(left = @Expr(type = ExprType.PATH, path = "outer.status"),
                right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")))
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class GroupedInTableQueryModel {

        @InTable(targetEntity = MultiQueryModel.class, left = @Expr(type = ExprType.PATH, path = "audit.status"),
            right = @Expr(type = ExprType.PATH, path = "inner.currentStatus"),
            whereCompare = @Compare(left = @Expr(type = ExprType.PATH, path = "outer.status"),
                right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")),
            where = @SubqueryGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"),
                    right = @Expr(type = ExprType.LITERAL, literal = "7", javaType = Integer.class)) },
                groups = { @NestedSubqueryGroup(junction = io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type.OR,
                    compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                        @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")) }) }))
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalComparedInTableQueryModel {

        @InTable(targetEntity = MultiQueryModel.class, left = @Expr(type = ExprType.PATH, path = "audit.status"),
            right = @Expr(type = ExprType.PATH, path = "inner.currentStatus"),
            whereCompare = @Compare(op = CompareOp.NE, left = @Expr(type = ExprType.PATH, path = "outer.status"), right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "audit.currentStatus") }))))
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class ConflictedCanonicalInTableQueryModel {

        @InTable(targetEntity = CanonicalComparedInTableQueryModel.class, left = @Expr(type = ExprType.PATH, path = "outer.status"),
            right = @Expr(type = ExprType.PATH, path = "inner.currentStatus"),
            whereCompare = @Compare(op = CompareOp.NE, left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "audit.currentStatus") }))))
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class CompareQueryModel {

        @Compare
        private String name = "demo";

        @Compare(op = CompareOp.GT, left = @Expr(type = ExprType.PATH, path = "audit.updatedAt"), right = @Expr(type = ExprType.PATH, path = "audit.createdAt"))
        private String changedAt = "ignored";

        @Compare(op = CompareOp.BETWEEN, left = @Expr(type = ExprType.PATH, path = "createdAt"), right = @Expr(type = ExprType.PATH, path = "range.startAt"),
            extra = { @Expr(type = ExprType.PATH, path = "range.endAt") })
        private List<String> createdRange = Arrays.asList("start", "end");

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getChangedAt() {
            return this.changedAt;
        }

        public void setChangedAt(final String changedAt) {
            this.changedAt = changedAt;
        }

        public List<String> getCreatedRange() {
            return this.createdRange;
        }

        public void setCreatedRange(final List<String> createdRange) {
            this.createdRange = createdRange;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalDslQueryModel {

        @Equal(left = @Expr(type = ExprType.PATH, path = "userName"), right = @Expr(type = ExprType.FUNCTION,
            function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "keyword") })))
        private String keyword = "Demo";

        @Compare(op = CompareOp.EQ, left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.PATH, path = "audit.currentStatus"),
            options = @PredicateOptions(scope = { "default" }))
        private String status = "ACTIVE";

        @Compare(op = CompareOp.BETWEEN, left = @Expr(type = ExprType.PATH, path = "createdAt"), right = @Expr(type = ExprType.PATH, path = "range.startAt"),
            extra = { @Expr(type = ExprType.PATH, path = "range.endAt") })
        private List<String> createdRange = Arrays.asList("start", "end");

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public List<String> getCreatedRange() {
            return this.createdRange;
        }

        public void setCreatedRange(final List<String> createdRange) {
            this.createdRange = createdRange;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalValueFieldQueryModel {

        @Equal(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.VALUE, valueField = "keyword"))
        private String ignoredStatus = "ACTIVE";

        @Compare(op = CompareOp.EQ,
            left = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "userName") })),
            right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.VALUE, valueField = "keyword") })))
        private String ignoredCompare = "ACTIVE";

        @Exists(targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"),
            where = @SubqueryGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId")),
                    @Compare(left = @Expr(type = ExprType.PATH, path = "deleted"), right = @Expr(type = ExprType.LITERAL, literal = "false")) }))
        private Boolean existsAudit = Boolean.TRUE;

        private String keyword = "keyword-demo";

        private Integer tenantId = 7;

        public String getIgnoredStatus() {
            return this.ignoredStatus;
        }

        public void setIgnoredStatus(final String ignoredStatus) {
            this.ignoredStatus = ignoredStatus;
        }

        public String getIgnoredCompare() {
            return this.ignoredCompare;
        }

        public void setIgnoredCompare(final String ignoredCompare) {
            this.ignoredCompare = ignoredCompare;
        }

        public Boolean getExistsAudit() {
            return this.existsAudit;
        }

        public void setExistsAudit(final Boolean existsAudit) {
            this.existsAudit = existsAudit;
        }

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

        public Integer getTenantId() {
            return this.tenantId;
        }

        public void setTenantId(final Integer tenantId) {
            this.tenantId = tenantId;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalFamilyQueryModel {

        @Range
        private List<String> createdAt = Arrays.asList("start", "end");

        @Range(op = CompareOp.BETWEEN, left = @Expr(type = ExprType.PATH, path = "audit.updatedAt"),
            right = @Expr(type = ExprType.PATH, path = "audit.createdAt"), extra = { @Expr(type = ExprType.PATH, path = "audit.finishedAt") })
        private List<String> ignoredRange = Arrays.asList("x", "y");

        @Membership(collection = @CollectionPolicy(split = true, targetType = TargetType.TO_INTEGER))
        private String status = "1,2,3";

        @NullCheck(op = CompareOp.IS_NOT_NULL, left = @Expr(type = ExprType.PATH, path = "deletedAt"))
        private Boolean deleted = Boolean.TRUE;

        public List<String> getCreatedAt() {
            return this.createdAt;
        }

        public void setCreatedAt(final List<String> createdAt) {
            this.createdAt = createdAt;
        }

        public List<String> getIgnoredRange() {
            return this.ignoredRange;
        }

        public void setIgnoredRange(final List<String> ignoredRange) {
            this.ignoredRange = ignoredRange;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public Boolean getDeleted() {
            return this.deleted;
        }

        public void setDeleted(final Boolean deleted) {
            this.deleted = deleted;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalExistsQueryModel {

        @Exists(targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"))
        private Boolean existsAudit = Boolean.TRUE;

        @NotExists(targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"))
        private Boolean missingAudit = Boolean.TRUE;

        public Boolean getExistsAudit() {
            return this.existsAudit;
        }

        public void setExistsAudit(final Boolean existsAudit) {
            this.existsAudit = existsAudit;
        }

        public Boolean getMissingAudit() {
            return this.missingAudit;
        }

        public void setMissingAudit(final Boolean missingAudit) {
            this.missingAudit = missingAudit;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalExistsGroupQueryModel {

        @Exists(targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"),
            where = @SubqueryGroup(
                compare = {
                    @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"),
                        right = @Expr(type = ExprType.LITERAL, literal = "7", javaType = Integer.class)),
                    @Compare(left = @Expr(type = ExprType.PATH, path = "deleted"), right = @Expr(type = ExprType.LITERAL, literal = "false")) },
                groups = { @NestedSubqueryGroup(junction = io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type.OR,
                    compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                        @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")) }) }))
        private Boolean existsAudit = Boolean.TRUE;

        @NotExists(targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"),
            where = @SubqueryGroup(
                compare = {
                    @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"),
                        right = @Expr(type = ExprType.LITERAL, literal = "7", javaType = Integer.class)),
                    @Compare(left = @Expr(type = ExprType.PATH, path = "deleted"), right = @Expr(type = ExprType.LITERAL, literal = "false")) },
                groups = { @NestedSubqueryGroup(junction = io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type.OR,
                    compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                        @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")) }) }))
        private Boolean missingAudit = Boolean.TRUE;

        public Boolean getExistsAudit() {
            return this.existsAudit;
        }

        public void setExistsAudit(final Boolean existsAudit) {
            this.existsAudit = existsAudit;
        }

        public Boolean getMissingAudit() {
            return this.missingAudit;
        }

        public void setMissingAudit(final Boolean missingAudit) {
            this.missingAudit = missingAudit;
        }

    }

    @SuppressWarnings("unused")
    public static final class GenericSubqueryQueryModel {

        @SubqueryPredicate(mode = SubqueryMode.EXISTS, targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"),
            right = @Expr(type = ExprType.PATH, path = "userId"), select = @Expr(type = ExprType.PATH, path = "id"))
        private Boolean existsAudit = Boolean.TRUE;

        @SubqueryPredicate(mode = SubqueryMode.NOT_EXISTS, targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"),
            right = @Expr(type = ExprType.PATH, path = "userId"), select = @Expr(type = ExprType.PATH, path = "id"))
        private Boolean missingAudit = Boolean.TRUE;

        @SubqueryPredicate(mode = SubqueryMode.IN, targetEntity = MultiQueryModel.class, left = @Expr(type = ExprType.PATH, path = "audit.status"),
            right = @Expr(type = ExprType.PATH, path = "inner.currentStatus"),
            whereCompare = @Compare(left = @Expr(type = ExprType.PATH, path = "outer.status"),
                right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")))
        private String status = "ACTIVE";

        public Boolean getExistsAudit() {
            return this.existsAudit;
        }

        public void setExistsAudit(final Boolean existsAudit) {
            this.existsAudit = existsAudit;
        }

        public Boolean getMissingAudit() {
            return this.missingAudit;
        }

        public void setMissingAudit(final Boolean missingAudit) {
            this.missingAudit = missingAudit;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalShortcutDslQueryModel {

        @Gt(left = @Expr(type = ExprType.PATH, path = "audit.updatedAt"), right = @Expr(type = ExprType.PATH, path = "audit.createdAt"))
        private String changedAt = "ignored";

        @Like(left = @Expr(type = ExprType.PATH, path = "userName"),
            right = @Expr(type = ExprType.FUNCTION, function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.VALUE) })))
        private String keyword = "Demo";

        @IsNull(left = @Expr(type = ExprType.PATH, path = "deletedAt"), options = @PredicateOptions(required = true))
        private Boolean deleted = Boolean.TRUE;

        public String getChangedAt() {
            return this.changedAt;
        }

        public void setChangedAt(final String changedAt) {
            this.changedAt = changedAt;
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
    public static final class ZeroConfigShortcutQueryModel {

        @NotEqual
        private String status = "INACTIVE";

        @Gte
        private Integer score = Integer.valueOf(90);

        @Lt
        private Integer rank = Integer.valueOf(10);

        @Lte
        private Integer level = Integer.valueOf(2);

        @Like
        private String keyword = "demo";

        @StartWith
        private String code = "PRE";

        @Between
        private List<String> createdAt = Arrays.asList("start", "end");

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public Integer getScore() {
            return this.score;
        }

        public void setScore(final Integer score) {
            this.score = score;
        }

        public Integer getRank() {
            return this.rank;
        }

        public void setRank(final Integer rank) {
            this.rank = rank;
        }

        public Integer getLevel() {
            return this.level;
        }

        public void setLevel(final Integer level) {
            this.level = level;
        }

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

        public String getCode() {
            return this.code;
        }

        public void setCode(final String code) {
            this.code = code;
        }

        public List<String> getCreatedAt() {
            return this.createdAt;
        }

        public void setCreatedAt(final List<String> createdAt) {
            this.createdAt = createdAt;
        }

    }

    @SuppressWarnings("unused")
    public static final class ExtendedShortcutAliasQueryModel {

        @GreaterThan(left = @Expr(type = ExprType.PATH, path = "updatedAt"), right = @Expr(type = ExprType.PATH, path = "createdAt"))
        private String changedAt = "ignored";

        @GreaterThanOrEqualTo
        private Integer score = Integer.valueOf(60);

        @LessThan
        private Integer rank = Integer.valueOf(99);

        @LessThanOrEqualTo
        private Integer level = Integer.valueOf(5);

        @NotLike
        private String keyword = "demo";

        @RightLike
        private String code = "suffix";

        @IsNotNull
        private Boolean deletedAt = Boolean.TRUE;

        @SplitNotIn(left = @Expr(type = ExprType.PATH, path = "otherStatuses"),
            collection = @CollectionPolicy(split = true, predicate = @PredicateRef(predicateClass = OnlyDisabledPredicate.class)))
        private String otherStatuses = "ACTIVE,DISABLED";

        public String getChangedAt() {
            return this.changedAt;
        }

        public void setChangedAt(final String changedAt) {
            this.changedAt = changedAt;
        }

        public Integer getScore() {
            return this.score;
        }

        public void setScore(final Integer score) {
            this.score = score;
        }

        public Integer getRank() {
            return this.rank;
        }

        public void setRank(final Integer rank) {
            this.rank = rank;
        }

        public Integer getLevel() {
            return this.level;
        }

        public void setLevel(final Integer level) {
            this.level = level;
        }

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

        public String getCode() {
            return this.code;
        }

        public void setCode(final String code) {
            this.code = code;
        }

        public Boolean getDeletedAt() {
            return this.deletedAt;
        }

        public void setDeletedAt(final Boolean deletedAt) {
            this.deletedAt = deletedAt;
        }

        public String getOtherStatuses() {
            return this.otherStatuses;
        }

        public void setOtherStatuses(final String otherStatuses) {
            this.otherStatuses = otherStatuses;
        }

    }

    @SuppressWarnings("unused")
    public static final class IgnoreCaseShortcutQueryModel {

        @IgnoreCaseEqual
        private String status = "ACTIVE";

        @IgnoreCaseEqual(left = @Expr(type = ExprType.PATH, path = "userName"))
        private String name = "Demo";

        @IgnoreCaseLike
        private String keyword = "Needle";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

    }

    @SuppressWarnings("unused")
    public static final class RecursiveCanonicalDslQueryModel {

        @Equal(left = @Expr(type = ExprType.PATH, path = "userName"),
            right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class,
                    args = { @ExprArg(type = ExprType.FUNCTION,
                        function = @NestedExprFunction(name = "concat", type = String.class,
                            args = {
                                @NestedExprArg(type = ExprType.FUNCTION,
                                    function = @DeepNestedExprFunction(name = "trim", type = String.class,
                                        args = { @DeepNestedExprArg(type = ExprType.PATH, path = "keyword") })),
                                @NestedExprArg(type = ExprType.FUNCTION, function = @DeepNestedExprFunction(name = "upper", type = String.class,
                                    args = { @DeepNestedExprArg(type = ExprType.PATH, path = "suffix") })) })) })))
        private String displayName = "ignored";

        private String keyword = " Demo ";

        private String suffix = "x";

        public String getDisplayName() {
            return this.displayName;
        }

        public void setDisplayName(final String displayName) {
            this.displayName = displayName;
        }

        public String getKeyword() {
            return this.keyword;
        }

        public void setKeyword(final String keyword) {
            this.keyword = keyword;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public void setSuffix(final String suffix) {
            this.suffix = suffix;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalNotBetweenQueryModel {

        @NotBetween(left = @Expr(type = ExprType.PATH, path = "createdAt"), right = @Expr(type = ExprType.PATH, path = "range.startAt"),
            extra = { @Expr(type = ExprType.PATH, path = "range.endAt") })
        private List<String> createdRange = Arrays.asList("start", "end");

        public List<String> getCreatedRange() {
            return this.createdRange;
        }

        public void setCreatedRange(final List<String> createdRange) {
            this.createdRange = createdRange;
        }

    }

    @SuppressWarnings("unused")
    public static final class CasesQueryModel {

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class)), left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class DefaultCasesQueryModel {

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalThenCasesQueryModel {

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(op = CompareOp.EQ, right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")),
            left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalWhenCasesQueryModel {

        @Cases(
            value = { @Case(when = @CaseWhen(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status;

        public CanonicalWhenCasesQueryModel() {
        }

        public CanonicalWhenCasesQueryModel(final String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class ConflictedCanonicalWhenCasesQueryModel {

        @Cases(value = { @Case(when = @CaseWhen(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE"),
            predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)), left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status;

        public ConflictedCanonicalWhenCasesQueryModel() {
        }

        public ConflictedCanonicalWhenCasesQueryModel(final String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class AlwaysMatchCasesQueryModel {

        @Cases(value = { @Case(when = @CaseWhen(), left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalContextCasesQueryModel {

        @Cases(value = { @Case(
            when = @CaseWhen(op = CompareOp.EQ, left = @Expr(type = ExprType.PATH, path = "status"),
                right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
            then = @CaseThen(processor = @ProcessorRef(processorClass = CanonicalAwarePredicateProcessor.class), op = CompareOp.EQ,
                right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")),
            left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class ConflictedCanonicalThenCasesQueryModel {

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class), op = CompareOp.EQ,
                right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")),
            left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class NegatedTrimmedCasesQueryModel {

        @Cases(value = { @Case(left = @Expr(type = ExprType.PATH, path = "status"), options = @PredicateOptions(not = true, trim = true, ignoreBlank = false),
            when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class))) })
        private String status = "  ACTIVE  ";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class FallbackCasesQueryModel {

        @Cases(value = {
            @Case(left = @Expr(type = ExprType.PATH, path = "status"), options = @PredicateOptions(ignoreBlank = true),
                when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class))),
            @Case(left = @Expr(type = ExprType.PATH, path = "status"), options = @PredicateOptions(ignoreBlank = false, ignoreEmpty = false, trim = false),
                then = @CaseThen(op = CompareOp.EQ, right = @Expr(type = ExprType.LITERAL, literal = "FALLBACK")),
                when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class))) })
        private String status = "   ";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class CanonicalBranchCasesQueryModel {

        @Cases(left = @Expr(type = ExprType.PATH, path = "audit.status"),
            value = { @Case(left = @Expr(type = ExprType.PATH, path = "audit.branch.status"),
                when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
                then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class))) })
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class GroupWhenCasesQueryModel {

        @Cases(
            value = {
                @Case(
                    when = @CaseWhen(group = @CaseWhenGroup(
                        compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")) },
                        groups = {
                            @io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedCaseWhenGroup(
                                junction = io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type.OR,
                                compare = {
                                    @Compare(left = @Expr(type = ExprType.PATH, path = "phase"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")),
                                    @Compare(left = @Expr(type = ExprType.PATH, path = "phase"),
                                        right = @Expr(type = ExprType.LITERAL, literal = "WAITING")) }) })),
                    left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status;

        private String phase;

        public GroupWhenCasesQueryModel() {
        }

        public GroupWhenCasesQueryModel(final String status, final String phase) {
            this.status = status;
            this.phase = phase;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public String getPhase() {
            return this.phase;
        }

        public void setPhase(final String phase) {
            this.phase = phase;
        }

    }

    @SuppressWarnings("unused")
    public static final class GroupThenCasesQueryModel {

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(group = @CaseThenGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                    @Compare(left = @Expr(type = ExprType.PATH, path = "phase"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")) }))) })
        private String status = "ACTIVE";

        private String phase = "PENDING";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public String getPhase() {
            return this.phase;
        }

        public void setPhase(final String phase) {
            this.phase = phase;
        }

    }

    @SuppressWarnings("unused")
    public static final class ElseCasesQueryModel {

        @Cases(otherwise = @CaseElse(enabled = true, then = @CaseThen(op = CompareOp.EQ, right = @Expr(type = ExprType.LITERAL, literal = "FALLBACK"))),
            value = { @Case(when = @CaseWhen(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                left = @Expr(type = ExprType.PATH, path = "status")) })
        private String status;

        public ElseCasesQueryModel() {
        }

        public ElseCasesQueryModel(final String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @SuppressWarnings("unused")
    public static final class TypedFilterQueryModel {

        @FilterIn(left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> statuses = Arrays.asList("ACTIVE", "INACTIVE");

        @FilterNotIn(left = @Expr(type = ExprType.PATH, path = "otherStatuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyDisabledPredicate.class)))
        private List<String> otherStatuses = Arrays.asList("DISABLED", "ACTIVE");

        public List<String> getStatuses() {
            return this.statuses;
        }

        public void setStatuses(final List<String> statuses) {
            this.statuses = statuses;
        }

        public List<String> getOtherStatuses() {
            return this.otherStatuses;
        }

        public void setOtherStatuses(final List<String> otherStatuses) {
            this.otherStatuses = otherStatuses;
        }

    }

    @SuppressWarnings("unused")
    public static final class StructuredTypedFilterQueryModel {

        @FilterIn(left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> statuses = Arrays.asList("ACTIVE", "INACTIVE");

        @FilterNotIn(left = @Expr(type = ExprType.PATH, path = "otherStatuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyDisabledPredicate.class)))
        private List<String> otherStatuses = Arrays.asList("DISABLED", "ACTIVE");

        public List<String> getStatuses() {
            return this.statuses;
        }

        public void setStatuses(final List<String> statuses) {
            this.statuses = statuses;
        }

        public List<String> getOtherStatuses() {
            return this.otherStatuses;
        }

        public void setOtherStatuses(final List<String> otherStatuses) {
            this.otherStatuses = otherStatuses;
        }

    }

    @SuppressWarnings("unused")
    public static final class StructuredMembershipQueryModel {

        @Membership(op = CompareOp.IN, left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> statuses = Arrays.asList("ACTIVE", "INACTIVE");

        public List<String> getStatuses() {
            return this.statuses;
        }

        public void setStatuses(final List<String> statuses) {
            this.statuses = statuses;
        }

    }

    @SuppressWarnings("unused")
    public static final class StructuredInQueryModel {

        @In(left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> statuses = Arrays.asList("ACTIVE", "INACTIVE");

        public List<String> getStatuses() {
            return this.statuses;
        }

        public void setStatuses(final List<String> statuses) {
            this.statuses = statuses;
        }

    }

    @SuppressWarnings("unused")
    public static final class SplitPolicyInQueryModel {

        @In(left = @Expr(type = ExprType.PATH, path = "statuses"), collection = @CollectionPolicy(split = true, targetType = TargetType.TO_INTEGER))
        private String statuses = "1,2,3";

        public String getStatuses() {
            return this.statuses;
        }

        public void setStatuses(final String statuses) {
            this.statuses = statuses;
        }

    }

    @SuppressWarnings("unused")
    public static final class StructuredCompareInQueryModel {

        @Compare(op = CompareOp.IN, left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> statuses = Arrays.asList("ACTIVE", "INACTIVE");

        public List<String> getStatuses() {
            return this.statuses;
        }

        public void setStatuses(final List<String> statuses) {
            this.statuses = statuses;
        }

    }

    @SuppressWarnings("unused")
    public static final class AlwaysTruePredicate implements java.util.function.Predicate<Object> {

        @Override
        public boolean test(final Object value) {
            return true;
        }

    }

    public static final class OnlyActivePredicate implements java.util.function.Predicate<Object> {

        @Override
        public boolean test(final Object value) {
            return "ACTIVE".equals(value);
        }

    }

    public static final class OnlyDisabledPredicate implements java.util.function.Predicate<Object> {

        @Override
        public boolean test(final Object value) {
            return "DISABLED".equals(value);
        }

    }

    public static final class NeverPredicate implements java.util.function.Predicate<Object> {

        @Override
        public boolean test(final Object value) {
            return false;
        }

    }

    public static final class TestPredicateProcessor implements PredicateProcessor {

        private static final long serialVersionUID = 1L;

        @Override
        public Predicate create(final PredicateProcessorContext context) {
            return proxy(Predicate.class, "CASE:" + debug(context.getRoot().get(context.getFieldName())));
        }

    }

    public static final class MismatchPredicateProcessor implements PredicateProcessor {

        private static final long serialVersionUID = 1L;

        @Override
        public Predicate create(final PredicateProcessorContext context) {
            return proxy(Predicate.class, "MISMATCH:" + context.getFieldName());
        }

    }

    public static final class CanonicalAwarePredicateProcessor implements PredicateProcessor {

        private static final long serialVersionUID = 1L;

        @Override
        public Predicate create(final PredicateProcessorContext context) {
            String whenOp = null == context.getCanonicalWhen() ? "NONE" : context.getCanonicalWhen().op().name();
            String thenOp = null == context.getCanonicalThen() ? "NONE" : context.getCanonicalThen().op().name();
            return proxy(Predicate.class, "CTX:" + whenOp + ":" + thenOp);
        }

    }

    @SuppressWarnings("unused")
    public static final class ConstraintQueryModel {

        @LocalConstraintCompiledSelectable
        private String status = "ACTIVE";

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

    }

    @Target({ java.lang.annotation.ElementType.FIELD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Documented
    @Selectable
    @Constraint(provider = @ProviderRef(providerClass = LocalTestConstraintCompiledPredicateProvider.class))
    public @interface LocalConstraintCompiledSelectable {
    }

    public static final class LocalTestConstraintCompiledPredicateProvider
        implements io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProvider {

        @Override
        public Class<? extends Annotation> annotationType() {
            return LocalConstraintCompiledSelectable.class;
        }

        @Override
        public Predicate create(final CompiledAnnotationSpec<? extends Annotation> spec, final Object args, final Root<?> root, final AbstractQuery<?> query,
            final CriteriaBuilder cb) {
            return cb.equal(root.get(spec.getBindingPath()), root.get("audit").get("currentStatus"));
        }

    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Selectable(left = @Expr(type = ExprType.PATH, path = "meta.direct.status"), right = @Expr(type = ExprType.PATH, path = "meta.direct.currentStatus"),
        options = @PredicateOptions(scope = { Constant.DEFAULT }))
    public @interface CanonicalSelectable {
    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Compare(left = @Expr(type = ExprType.PATH, path = "meta.composed.status"), right = @Expr(type = ExprType.PATH, path = "meta.composed.currentStatus"),
        options = @PredicateOptions(scope = { "composed" }))
    public @interface ComposedCompareSelectable {
    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Equal(left = @Expr(type = ExprType.PATH, path = "meta.equal.status"), right = @Expr(type = ExprType.PATH, path = "meta.equal.currentStatus"),
        options = @PredicateOptions(scope = { "equal" }))
    public @interface ComposedEqualSelectable {
    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Between(left = @Expr(type = ExprType.PATH, path = "meta.between.start"), right = @Expr(type = ExprType.PATH, path = "meta.between.lower"),
        extra = { @Expr(type = ExprType.PATH, path = "meta.between.upper") }, options = @PredicateOptions(scope = { "between" }))
    public @interface ComposedBetweenSelectable {
    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @SubqueryPredicate(mode = SubqueryMode.EXISTS, targetEntity = QueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"),
        right = @Expr(type = ExprType.PATH, path = "userId"), select = @Expr(type = ExprType.PATH, path = "id"), where = @SubqueryGroup(
            compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId")) }))
    public @interface ComposedExistsSelectable {
    }

    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Length(op = CompareOp.LIKE)
    public @interface InvalidComposedLength {
    }

    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Length(op = CompareOp.GTE, left = @Expr(type = ExprType.FUNCTION,
        function = @ExprFunction(name = "length", type = Integer.class, args = { @ExprArg(type = ExprType.PATH, path = "displayName") })))
    public @interface MinDisplayNameLength {
    }

}
