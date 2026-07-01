package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ArrayMergeMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.OptionSwitch;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor;
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
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepSubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Equal;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Exists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.FilterIn;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.In;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.InTable;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Membership;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedSubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateRef;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ProcessorRef;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.SubqueryGroup;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionCardinality;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionSource;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;

public class CompiledPredicateSpecsTest {

    private static final String DEFAULT = "default";

    @Test
    public void shouldCompileZeroConfigEqualToPathAndRuntimeLiteral() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("name");
        Equal anno = field.getAnnotation(Equal.class);

        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertEquals(PredicateOperator.EQ, predicateSpec.getOperator());
        assertEquals(ExpressionSource.PATH, predicateSpec.getLeft().getSource());
        assertEquals("name", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals(ExpressionSource.FIELD_VALUE, predicateSpec.getRight().getSource());
        assertEquals(ExpressionCardinality.SINGLE, predicateSpec.getRight().getCardinality());
        assertTrue(predicateSpec.getExtraOperands().isEmpty());
        assertEquals(DEFAULT, spec.getEffectiveOptions().getScope()[0]);
        assertTrue(predicateSpec.getOptions().isIgnoreNull());
    }

    @Test
    public void shouldResolveRootPredicateOptionsFromCanonicalDefaults() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaExistedStatus");
        Cases owner = SpecQueryModel.class.getDeclaredField("branchOptionsCaseStatus").getAnnotation(Cases.class);
        PredicateOptionsSpec options = PredicateOptionsSpec
            .root(CompiledAnnotationSpec.of(SpecQueryModel.class, field.getAnnotation(CanonicalExisted.class), field).getOptions());

        assertEquals(DEFAULT, options.getScope()[0]);
        assertEquals(DEFAULT, options.getGroups()[0].value());
        assertFalse(options.isRequired());
        assertTrue(options.isIgnoreNull());
        assertTrue(options.isTrim());
        assertFalse(PredicateOptionsSpec.root(owner.options()).isRequired());
    }

    @Test
    public void shouldResolveInheritedPredicateOptionsFromParentDefaults() throws Exception {
        Cases owner = SpecQueryModel.class.getDeclaredField("branchOverrideCaseStatus").getAnnotation(Cases.class);
        PredicateOptionsSpec inherited = PredicateOptionsSpec.inherit(PredicateOptionsSpec.root(owner.options()), owner.value()[0].options());

        assertEquals(0, inherited.getScope().length);
        assertFalse(inherited.isRequired());
        assertTrue(inherited.isTrim());
        assertFalse(inherited.isIgnoreBlank());
    }

    @Test
    public void shouldCompileZeroConfigSelectableToPathAndRuntimeLiteral() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("zeroConfigSelectableStatus");
        Selectable anno = field.getAnnotation(Selectable.class);

        CompiledAnnotationSpec<Selectable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertEquals(ExpressionSource.PATH, predicateSpec.getLeft().getSource());
        assertEquals("zeroConfigSelectableStatus", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals(ExpressionSource.FIELD_VALUE, predicateSpec.getRight().getSource());
        assertEquals(ExpressionCardinality.SINGLE, predicateSpec.getRight().getCardinality());
        assertEquals(DEFAULT, spec.getEffectiveOptions().getScope()[0]);
        assertEquals(DEFAULT, spec.getEffectiveOptions().getGroups()[0].value());
    }

    @Test
    public void shouldCompileZeroConfigExistedToPathAndRuntimeLiteral() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("zeroConfigExistedStatus");
        Existed anno = field.getAnnotation(Existed.class);

        CompiledAnnotationSpec<Existed> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertEquals(ExpressionSource.PATH, predicateSpec.getLeft().getSource());
        assertEquals("zeroConfigExistedStatus", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals(ExpressionSource.FIELD_VALUE, predicateSpec.getRight().getSource());
        assertEquals(ExpressionCardinality.SINGLE, predicateSpec.getRight().getCardinality());
        assertEquals(DEFAULT, spec.getEffectiveOptions().getScope()[0]);
        assertEquals(DEFAULT, spec.getEffectiveOptions().getGroups()[0].value());
    }

    @Test
    public void shouldInheritAndOverridePredicateOptionsInsideSubqueryWhereTree() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("optionAwareExists");
        Exists anno = field.getAnnotation(Exists.class);

        CompiledAnnotationSpec<Exists> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec subquerySpec = CompiledSpecializedSpecs.exists(spec);
        PredicateGroupSpec rootGroup = subquerySpec.getPredicateGroupSpec();
        CompiledAnnotationSpec<?> inheritedCompare = rootGroup.getAnnotations().get(0);
        CompiledAnnotationSpec<?> overriddenCompare = rootGroup.getGroups().get(0).getAnnotations().get(0);

        assertEquals("outer-scope", spec.getEffectiveOptions().getScope()[0]);
        assertEquals("outer-scope", inheritedCompare.getEffectiveOptions().getScope()[0]);
        assertTrue(inheritedCompare.getEffectiveOptions().isRequired());
        assertFalse(inheritedCompare.getEffectiveOptions().isTrim());
        assertTrue(inheritedCompare.getEffectiveOptions().isIgnoreBlank());

        assertEquals(0, overriddenCompare.getEffectiveOptions().getScope().length);
        assertFalse(overriddenCompare.getEffectiveOptions().isRequired());
        assertTrue(overriddenCompare.getEffectiveOptions().isTrim());
        assertFalse(overriddenCompare.getEffectiveOptions().isIgnoreBlank());
    }

    @Test
    public void shouldCompileSubqueryModeIntoCompiledSubquerySpec() throws Exception {
        Field existsField = SpecQueryModel.class.getDeclaredField("groupedExists");
        Field inTableField = SpecQueryModel.class.getDeclaredField("status");

        CompiledSubquerySpec existsSpec = CompiledSpecializedSpecs
            .subquery(CompiledAnnotationSpec.of(SpecQueryModel.class, existsField.getAnnotation(Exists.class), existsField));
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs
            .subquery(CompiledAnnotationSpec.of(SpecQueryModel.class, inTableField.getAnnotation(InTable.class), inTableField));

        assertEquals(io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode.EXISTS, existsSpec.getMode());
        assertEquals(io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode.IN, inTableSpec.getMode());
    }

    @Test
    public void shouldInheritAndOverridePredicateOptionsInsideInTableWhereTree() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("optionAwareInTableStatus");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec subquerySpec = CompiledSpecializedSpecs.inTable(spec);
        CompiledAnnotationSpec<?> inheritedCompare = subquerySpec.getPredicateGroupSpec().getAnnotations().get(0);
        CompiledAnnotationSpec<?> overriddenCompare = subquerySpec.getPredicateGroupSpec().getGroups().get(0).getAnnotations().get(0);

        assertEquals("in-table-outer-scope", spec.getEffectiveOptions().getScope()[0]);
        assertEquals("in-table-outer-scope", inheritedCompare.getEffectiveOptions().getScope()[0]);
        assertTrue(inheritedCompare.getEffectiveOptions().isRequired());
        assertFalse(inheritedCompare.getEffectiveOptions().isTrim());
        assertTrue(inheritedCompare.getEffectiveOptions().isIgnoreBlank());

        assertEquals(0, overriddenCompare.getEffectiveOptions().getScope().length);
        assertFalse(overriddenCompare.getEffectiveOptions().isRequired());
        assertTrue(overriddenCompare.getEffectiveOptions().isTrim());
        assertFalse(overriddenCompare.getEffectiveOptions().isIgnoreBlank());
    }

    @Test
    public void shouldInheritAndOverridePredicateOptionsInsideNotExistsWhereTree() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("optionAwareNotExists");
        NotExists anno = field.getAnnotation(NotExists.class);

        CompiledAnnotationSpec<NotExists> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec subquerySpec = CompiledSpecializedSpecs.subquery(spec);
        CompiledAnnotationSpec<?> inheritedCompare = subquerySpec.getPredicateGroupSpec().getAnnotations().get(0);
        CompiledAnnotationSpec<?> overriddenCompare = subquerySpec.getPredicateGroupSpec().getGroups().get(0).getAnnotations().get(0);

        assertEquals("not-exists-outer-scope", spec.getEffectiveOptions().getScope()[0]);
        assertEquals("not-exists-outer-scope", inheritedCompare.getEffectiveOptions().getScope()[0]);
        assertTrue(inheritedCompare.getEffectiveOptions().isRequired());
        assertFalse(inheritedCompare.getEffectiveOptions().isTrim());
        assertTrue(inheritedCompare.getEffectiveOptions().isIgnoreBlank());

        assertEquals(0, overriddenCompare.getEffectiveOptions().getScope().length);
        assertFalse(overriddenCompare.getEffectiveOptions().isRequired());
        assertTrue(overriddenCompare.getEffectiveOptions().isTrim());
        assertFalse(overriddenCompare.getEffectiveOptions().isIgnoreBlank());
    }

    @Test
    public void shouldFallbackCanonicalCompareExprToPathWhenExprIsAuto() {
        Expr resolved = CompiledCanonicalCompareFactory.pathOrFallback(new Expr() {

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Expr.class;
            }

            @Override
            public ExprType type() {
                return ExprType.AUTO;
            }

            @Override
            public String path() {
                return "";
            }

            @Override
            public String valueField() {
                return "";
            }

            @Override
            public String literal() {
                return "";
            }

            @Override
            public Class<?> javaType() {
                return Object.class;
            }

            @Override
            public ExprFunction function() {
                return emptyExprFunction();
            }

        }, "status");

        assertEquals(ExprType.PATH, resolved.type());
        assertEquals("status", resolved.path());
    }

    @Test
    public void shouldKeepLiteralExprMarkedAsConfigured() {
        assertTrue(CompiledCanonicalCompareFactory.isConfigured(literalExpr("ACTIVE")));
    }

    @Test
    public void shouldNormalizeDeepNestedFunctionArgumentsThroughCanonicalMapper() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("nestedFunctionStatus");
        Equal anno = field.getAnnotation(Equal.class);

        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);
        FunctionExpression lower = FunctionExpression.class.cast(predicateSpec.getRight());
        FunctionExpression concat = FunctionExpression.class.cast(lower.getArgs().get(0));
        FunctionExpression trim = FunctionExpression.class.cast(concat.getArgs().get(0));
        FunctionExpression upper = FunctionExpression.class.cast(concat.getArgs().get(1));

        assertEquals("lower", lower.getName());
        assertEquals("concat", concat.getName());
        assertEquals("trim", trim.getName());
        assertEquals("upper", upper.getName());
        assertEquals("keyword", PathExpression.class.cast(trim.getArgs().get(0)).getPath());
        assertEquals("suffix", PathExpression.class.cast(upper.getArgs().get(0)).getPath());
    }

    private static Expr literalExpr(final String literal) {
        return new Expr() {

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Expr.class;
            }

            @Override
            public ExprType type() {
                return ExprType.LITERAL;
            }

            @Override
            public String path() {
                return "";
            }

            @Override
            public String valueField() {
                return "";
            }

            @Override
            public String literal() {
                return literal;
            }

            @Override
            public Class<?> javaType() {
                return Object.class;
            }

            @Override
            public ExprFunction function() {
                return emptyExprFunction();
            }

        };
    }

    private static ExprFunction emptyExprFunction() {
        return new ExprFunction() {

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return ExprFunction.class;
            }

            @Override
            public String name() {
                return "";
            }

            @Override
            public Class<?> type() {
                return Object.class;
            }

            @Override
            public ExprArg[] args() {
                return new ExprArg[0];
            }

        };
    }

    @Test
    public void shouldCompileBetweenColumnRangeToTwoPathOperands() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("createdAt");
        Between anno = field.getAnnotation(Between.class);

        CompiledAnnotationSpec<Between> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.between(PredicateOperator.BETWEEN, spec);

        assertEquals(ExpressionSource.PATH, predicateSpec.getLeft().getSource());
        assertEquals(ExpressionSource.PATH, predicateSpec.getRight().getSource());
        assertEquals("range.startAt", PathExpression.class.cast(predicateSpec.getRight()).getPath());
        assertEquals(1, predicateSpec.getExtraOperands().size());
        assertEquals("range.endAt", PathExpression.class.cast(predicateSpec.getExtraOperands().get(0)).getPath());
    }

    @Test
    public void shouldCompileInTableToDedicatedSubquerySpec() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("status");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs.inTable(spec);

        assertEquals("status", inTableSpec.getSourcePath());
        assertEquals(SpecQueryModel.class, inTableSpec.getFromEntity());
        assertEquals("currentStatus", inTableSpec.getSelectPath());
        assertEquals(Compare.class, inTableSpec.getNestedPredicateSpec().getAnnotationType());
        assertEquals("status", inTableSpec.getNestedPredicateSpec().getBindingPath());
        assertEquals(ExprType.PATH, inTableSpec.getNestedPredicateSpec().getLeft().type());
        assertEquals("status", inTableSpec.getNestedPredicateSpec().getLeft().path());
        assertEquals(ExprType.PATH, inTableSpec.getNestedPredicateSpec().getRight().type());
        assertEquals("currentStatus", inTableSpec.getNestedPredicateSpec().getRight().path());
        assertEquals(PredicateGroupSpec.JunctionType.AND, inTableSpec.getPredicateGroupSpec().getJunctionType());
        assertTrue(inTableSpec.getPredicateGroupSpec().isEmpty());
    }

    @Test
    public void shouldCompileCanonicalInTablePathsToDedicatedSubquerySpec() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("canonicalStatus");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs.inTable(spec);

        assertEquals("outer.status", inTableSpec.getSourcePath());
        assertEquals("inner.currentStatus", inTableSpec.getSelectPath());
    }

    @Test
    public void shouldCompileCanonicalInTablePathsWithoutLegacyColumnShortcuts() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("conflictedCanonicalStatus");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs.inTable(spec);

        assertEquals("outer.status", inTableSpec.getSourcePath());
        assertEquals("inner.currentStatus", inTableSpec.getSelectPath());
    }

    @Test
    public void shouldReadInTableOptionsAndValueFunctionsThroughCompiledSpec() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("configuredInTableStatus");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs.inTable(spec);

        assertEquals("in-table-scope", spec.getEffectiveOptions().getScope()[0]);
        assertTrue(spec.getEffectiveOptions().isRequired());
        assertEquals(ExprType.PATH, inTableSpec.getNestedPredicateSpec().getLeft().type());
        assertEquals("status", inTableSpec.getNestedPredicateSpec().getLeft().path());
        assertEquals(ExprType.FUNCTION, inTableSpec.getNestedPredicateSpec().getRight().type());
        assertEquals("lower", inTableSpec.getNestedPredicateSpec().getRight().function().name());
        assertEquals("upper", inTableSpec.getNestedPredicateSpec().getRight().function().args()[0].function().name());
    }

    @Test
    public void shouldCompileInTableWhereGroupTreeAlongsideDefaultLeafCompare() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("groupedInTableStatus");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs.inTable(spec);

        assertEquals(Compare.class, inTableSpec.getNestedPredicateSpec().getAnnotationType());
        assertEquals(1, inTableSpec.getPredicateGroupSpec().getAnnotations().size());
        assertEquals(1, inTableSpec.getPredicateGroupSpec().getGroups().size());
        assertEquals(PredicateGroupSpec.JunctionType.OR, inTableSpec.getPredicateGroupSpec().getGroups().get(0).getJunctionType());
    }

    @Test
    public void shouldCompileInTableWhereCompareAsCanonicalNestedLeaf() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("canonicalComparedInTableStatus");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs.inTable(spec);

        assertEquals(Compare.class, inTableSpec.getNestedPredicateSpec().getAnnotationType());
        assertEquals(CompareOp.NE, inTableSpec.getNestedPredicateSpec().getOp());
        assertEquals("status", inTableSpec.getNestedPredicateSpec().getLeft().path());
        assertEquals(ExprType.FUNCTION, inTableSpec.getNestedPredicateSpec().getRight().type());
        assertEquals("lower", inTableSpec.getNestedPredicateSpec().getRight().function().name());
    }

    @Test
    public void shouldPreferWhereCompareOverImplicitDefaultLeaf() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("conflictedCanonicalComparedInTableStatus");
        InTable anno = field.getAnnotation(InTable.class);

        CompiledAnnotationSpec<InTable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec inTableSpec = CompiledSpecializedSpecs.inTable(spec);

        assertEquals(CompareOp.NE, inTableSpec.getNestedPredicateSpec().getOp());
        assertEquals("status", inTableSpec.getNestedPredicateSpec().getLeft().path());
        assertEquals(ExprType.FUNCTION, inTableSpec.getNestedPredicateSpec().getRight().type());
        assertEquals("lower", inTableSpec.getNestedPredicateSpec().getRight().function().name());
    }

    @Test
    public void shouldCompileExistsSubqueryWhereGroupTree() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("groupedExists");
        Exists anno = field.getAnnotation(Exists.class);

        CompiledAnnotationSpec<Exists> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec subquerySpec = CompiledSpecializedSpecs.exists(spec);

        assertEquals("id", subquerySpec.getSourcePath());
        assertEquals("userId", subquerySpec.getCorrelatedPath());
        assertEquals("id", subquerySpec.getSelectPath());
        assertEquals(PredicateGroupSpec.JunctionType.AND, subquerySpec.getPredicateGroupSpec().getJunctionType());
        assertEquals(2, subquerySpec.getPredicateGroupSpec().getAnnotations().size());
        assertEquals(1, subquerySpec.getPredicateGroupSpec().getGroups().size());
        assertEquals(PredicateGroupSpec.JunctionType.OR, subquerySpec.getPredicateGroupSpec().getGroups().get(0).getJunctionType());
        assertEquals(2, subquerySpec.getPredicateGroupSpec().getGroups().get(0).getAnnotations().size());
    }

    @Test
    public void shouldCompileDeepSubqueryLeafGroupAsLeafOnly() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("deepGroupedExists");
        Exists anno = field.getAnnotation(Exists.class);

        CompiledAnnotationSpec<Exists> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledSubquerySpec subquerySpec = CompiledSpecializedSpecs.exists(spec);
        PredicateGroupSpec root = subquerySpec.getPredicateGroupSpec();
        PredicateGroupSpec nested = root.getGroups().get(0);
        PredicateGroupSpec deep = nested.getGroups().get(0);

        assertEquals(PredicateGroupSpec.JunctionType.AND, root.getJunctionType());
        assertEquals(PredicateGroupSpec.JunctionType.OR, nested.getJunctionType());
        assertEquals(PredicateGroupSpec.JunctionType.AND, deep.getJunctionType());
        assertEquals(1, deep.getAnnotations().size());
        assertTrue(deep.getGroups().isEmpty());
        assertEquals(Compare.class, deep.getAnnotations().get(0).getAnnotationType());
    }

    @Test
    public void shouldCompileCasesToBranchSpecs() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("caseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals("status", casesSpec.get(0).getFieldName());
        assertTrue(casesSpec.get(0).usesProcessorAction());
        assertTrue(casesSpec.get(0).isReferenceMatch());
        assertEquals(TestPredicateProcessor.class, casesSpec.get(0).getProcessor().getClass());
        assertTrue(null != casesSpec.get(0).getPredicate());
        assertTrue(casesSpec.get(0).getPredicate().test(new Object()));
    }

    @Test
    public void shouldCompileDefaultCanonicalCaseThenWhenNotConfigured() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("defaultCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertNull(casesSpec.get(0).getProcessor());
        assertTrue(casesSpec.get(0).usesCanonicalAction());
        assertTrue(casesSpec.get(0).isReferenceMatch());
        assertEquals(Compare.class, casesSpec.get(0).getCanonicalPredicateSpec().getAnnotationType());
        assertEquals("status", casesSpec.get(0).getCanonicalPredicateSpec().getLeft().path());
    }

    @Test
    public void shouldTreatEmptyCaseWhenAsAlwaysMatch() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("alwaysMatchCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).isAlwaysMatch());
        assertFalse(casesSpec.get(0).isCanonicalMatch());
        assertFalse(casesSpec.get(0).isReferenceMatch());
        assertEquals(Compare.class, casesSpec.get(0).getCanonicalPredicateSpec().getAnnotationType());
    }

    @Test
    public void shouldCompileCanonicalCaseThenWithoutCustomProcessor() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("canonicalThenCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals(Compare.class, casesSpec.get(0).getCanonicalPredicateSpec().getAnnotationType());
        assertEquals("status", casesSpec.get(0).getCanonicalPredicateSpec().getLeft().path());
        assertEquals(ExprType.PATH, casesSpec.get(0).getCanonicalPredicateSpec().getRight().type());
        assertEquals("audit.currentStatus", casesSpec.get(0).getCanonicalPredicateSpec().getRight().path());
        assertNull(casesSpec.get(0).getProcessor());
    }

    @Test
    public void shouldCompileCanonicalCaseWhenWithoutTypedPredicate() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("canonicalWhenCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals(Compare.class, casesSpec.get(0).getCanonicalWhenSpec().getAnnotationType());
        assertEquals("status", casesSpec.get(0).getCanonicalWhenSpec().getLeft().path());
        assertEquals(ExprType.LITERAL, casesSpec.get(0).getCanonicalWhenSpec().getRight().type());
        assertEquals("ACTIVE", casesSpec.get(0).getCanonicalWhenSpec().getRight().literal());
    }

    @Test
    public void shouldResolvePredicateProcessorFromProcessorRefClass() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("caseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals(TestPredicateProcessor.class, casesSpec.get(0).getProcessor().getClass());
    }

    @Test
    public void shouldCompileCasesDefaultFieldFromCanonicalLeftPath() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("canonicalCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals("audit.status", casesSpec.get(0).getFieldName());
    }

    @Test
    public void shouldCompileCasesBranchFieldFromCanonicalLeftPath() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("canonicalBranchCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals("audit.branch.status", casesSpec.get(0).getFieldName());
    }

    @Test
    public void shouldMergeBranchPredicateOptionsFromCasesDefaults() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("branchOptionsCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals("case-scope", casesSpec.get(0).getOptions().getScope()[0]);
        assertTrue(casesSpec.get(0).getOptions().isRequired());
        assertFalse(casesSpec.get(0).getOptions().isTrim());
    }

    @Test
    public void shouldAllowPredicateOptionsToExplicitlyDisableInheritedFlags() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("branchOverrideCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertEquals(0, casesSpec.get(0).getOptions().getScope().length);
        assertFalse(casesSpec.get(0).getOptions().isRequired());
        assertTrue(casesSpec.get(0).getOptions().isTrim());
        assertFalse(casesSpec.get(0).getOptions().isIgnoreBlank());
    }

    @Test
    public void shouldResolveCaseWhenPredicateRef() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("caseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).isReferenceMatch());
        assertTrue(null != casesSpec.get(0).getPredicate());
        assertTrue(casesSpec.get(0).getPredicate().test(new Object()));
    }

    @Test
    public void shouldResolveCaseWhenPredicateFromPredicateRefClass() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("caseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).isReferenceMatch());
        assertTrue(null != casesSpec.get(0).getPredicate());
        assertTrue(casesSpec.get(0).getPredicate().test(new Object()));
    }

    @Test
    public void shouldUseStructuredCaseRefsDirectly() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("caseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).isReferenceMatch());
        assertTrue(casesSpec.get(0).usesProcessorAction());
        assertTrue(null != casesSpec.get(0).getPredicate());
        assertTrue(casesSpec.get(0).getPredicate().test(new Object()));
        assertEquals(TestPredicateProcessor.class, casesSpec.get(0).getProcessor().getClass());
    }

    @Test
    public void shouldKeepCanonicalCaseWhenAlongsideTypedPredicateExtension() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("conflictedCanonicalWhenCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).isCanonicalMatch());
        assertFalse(casesSpec.get(0).isReferenceMatch());
        assertTrue(null != casesSpec.get(0).getCanonicalWhenSpec());
        assertNull(casesSpec.get(0).getPredicate());
    }

    @Test
    public void shouldPreferProcessorActionOverCanonicalCaseThen() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("conflictedCanonicalThenCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).usesProcessorAction());
        assertEquals(Compare.class, casesSpec.get(0).getCanonicalPredicateSpec().getAnnotationType());
        assertEquals(TestPredicateProcessor.class, casesSpec.get(0).getProcessor().getClass());
    }

    @Test
    public void shouldCompileStructuredCaseWhenGroup() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("groupWhenCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).isGroupMatch());
        assertFalse(casesSpec.get(0).isCanonicalMatch());
        assertFalse(casesSpec.get(0).isReferenceMatch());
        assertEquals(PredicateGroupSpec.JunctionType.AND, casesSpec.get(0).getWhenGroupSpec().getJunctionType());
        assertEquals(1, casesSpec.get(0).getWhenGroupSpec().getGroups().size());
    }

    @Test
    public void shouldCompileStructuredCaseThenGroup() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("groupThenCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(1, casesSpec.size());
        assertTrue(casesSpec.get(0).usesGroupAction());
        assertFalse(casesSpec.get(0).usesProcessorAction());
        assertEquals(PredicateGroupSpec.JunctionType.AND, casesSpec.get(0).getThenGroupSpec().getJunctionType());
        assertEquals(2, casesSpec.get(0).getThenGroupSpec().getAnnotations().size());
    }

    @Test
    public void shouldAppendExplicitElseBranchToCasesSpecs() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("elseCaseStatus");
        Cases anno = field.getAnnotation(Cases.class);

        CompiledAnnotationSpec<Cases> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        java.util.List<CompiledCaseBranchSpec> casesSpec = CompiledSpecializedSpecs.cases(spec);

        assertEquals(2, casesSpec.size());
        assertTrue(casesSpec.get(1).isAlwaysMatch());
        assertEquals("elseCaseStatus", casesSpec.get(1).getFieldName());
        assertEquals(Compare.class, casesSpec.get(1).getCanonicalPredicateSpec().getAnnotationType());
        assertEquals("FALLBACK", casesSpec.get(1).getCanonicalPredicateSpec().getRight().literal());
    }

    @Test
    public void shouldCompileSelectableMetaAnnotationWithCanonicalExpressions() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaStatus");
        CanonicalSelectable anno = field.getAnnotation(CanonicalSelectable.class);

        CompiledAnnotationSpec<CanonicalSelectable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertEquals(ExpressionSource.PATH, predicateSpec.getLeft().getSource());
        assertEquals("meta.userStatus", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals(ExpressionSource.PATH, predicateSpec.getRight().getSource());
        assertEquals("meta.audit.currentStatus", PathExpression.class.cast(predicateSpec.getRight()).getPath());
        assertEquals(DEFAULT, predicateSpec.getOptions().getScope()[0]);
        assertTrue(predicateSpec.getOptions().isIgnoreNull());
    }

    @Test
    public void shouldCompileExistedMetaAnnotationWithCanonicalExpressions() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaExistedStatus");
        CanonicalExisted anno = field.getAnnotation(CanonicalExisted.class);

        CompiledAnnotationSpec<CanonicalExisted> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertFalse(spec.getRole().isSelection());
        assertTrue(spec.getRole().isExistence());
        assertEquals(ExpressionSource.PATH, predicateSpec.getLeft().getSource());
        assertEquals("meta.existedStatus", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals(ExpressionSource.PATH, predicateSpec.getRight().getSource());
        assertEquals("meta.audit.existedCurrentStatus", PathExpression.class.cast(predicateSpec.getRight()).getPath());
        assertEquals(DEFAULT, predicateSpec.getOptions().getScope()[0]);
        assertTrue(predicateSpec.getOptions().isIgnoreNull());
    }

    @Test
    public void shouldApplyCompiledDefaultsForEmptyMetaAnnotationOptions() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaExistedStatus");
        CanonicalExisted anno = field.getAnnotation(CanonicalExisted.class);

        CompiledAnnotationSpec<CanonicalExisted> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(0, spec.getPredicateCore().getOptions().scope().length);
        assertEquals(0, spec.getPredicateCore().getOptions().groups().length);
        assertEquals(DEFAULT, spec.getEffectiveOptions().getScope()[0]);
        assertEquals("default", spec.getEffectiveOptions().getGroups()[0].value());
    }

    @Test
    public void shouldReadExtraAndDefaultValueExpressionFromMetaAnnotationIntoCompiledSpec() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaExtendedStatus");
        CanonicalExtendedSelectable anno = field.getAnnotation(CanonicalExtendedSelectable.class);

        CompiledAnnotationSpec<CanonicalExtendedSelectable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(1, spec.getPredicateCore().getExtra().length);
        assertEquals("meta.range.end", spec.getPredicateCore().getExtra()[0].path());
        assertEquals(ExprType.VALUE, spec.getPredicateCore().getRight().type());
    }

    @Test
    public void shouldReadValueFunctionsFromDirectShortcutAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("functionScopedName");
        Equal anno = field.getAnnotation(Equal.class);

        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(ExprType.FUNCTION, spec.getPredicateCore().getRight().type());
        assertEquals("lower", spec.getPredicateCore().getRight().function().name());
        assertEquals(1, spec.getPredicateCore().getRight().function().args().length);
        assertEquals(ExprType.FUNCTION, spec.getPredicateCore().getRight().function().args()[0].type());
        assertEquals("upper", spec.getPredicateCore().getRight().function().args()[0].function().name());
    }

    @Test
    public void shouldCompileRecursiveNestedFunctionArguments() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("nestedFunctionStatus");
        Equal anno = field.getAnnotation(Equal.class);

        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        FunctionExpression lower = FunctionExpression.class.cast(predicateSpec.getRight());
        assertEquals("lower", lower.getName());
        assertEquals(1, lower.getArgs().size());

        FunctionExpression concat = FunctionExpression.class.cast(lower.getArgs().get(0));
        assertEquals("concat", concat.getName());
        assertEquals(2, concat.getArgs().size());

        FunctionExpression trim = FunctionExpression.class.cast(concat.getArgs().get(0));
        assertEquals("trim", trim.getName());
        assertEquals("keyword", PathExpression.class.cast(trim.getArgs().get(0)).getPath());

        FunctionExpression upper = FunctionExpression.class.cast(concat.getArgs().get(1));
        assertEquals("upper", upper.getName());
        assertEquals("suffix", PathExpression.class.cast(upper.getArgs().get(0)).getPath());
    }

    @Test
    public void shouldCompileCanonicalValueFieldOperand() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("crossFieldStatus");
        Equal anno = field.getAnnotation(Equal.class);

        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertEquals(ExpressionSource.FIELD_VALUE, predicateSpec.getRight().getSource());
        assertEquals("keyword", FieldValueExpression.class.cast(predicateSpec.getRight()).getValueField());
    }

    @Test
    public void shouldRecognizeRecursiveComposedCompareAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaComposedStatus");
        ComposedCompareSelectable anno = field.getAnnotation(ComposedCompareSelectable.class);

        CompiledAnnotationSpec<ComposedCompareSelectable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertTrue(spec.getRole().isSelection());
        assertFalse(spec.getRole().isExistence());
        assertEquals("meta.composed.status", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals("meta.composed.currentStatus", PathExpression.class.cast(predicateSpec.getRight()).getPath());
        assertEquals("composed", predicateSpec.getOptions().getScope()[0]);
    }

    @Test
    public void shouldRecognizeRecursiveComposedEqualAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaEqualStatus");
        ComposedEqualSelectable anno = field.getAnnotation(ComposedEqualSelectable.class);

        CompiledAnnotationSpec<ComposedEqualSelectable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertTrue(spec.getRole().isSelection());
        assertEquals("meta.equal.status", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals("meta.equal.currentStatus", PathExpression.class.cast(predicateSpec.getRight()).getPath());
        assertEquals("equal", predicateSpec.getOptions().getScope()[0]);
    }

    @Test
    public void shouldRecognizeRecursiveComposedBetweenAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("metaBetweenStatus");
        ComposedBetweenSelectable anno = field.getAnnotation(ComposedBetweenSelectable.class);

        CompiledAnnotationSpec<ComposedBetweenSelectable> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.between(PredicateOperator.BETWEEN, spec);

        assertTrue(spec.getRole().isSelection());
        assertEquals("meta.between.start", PathExpression.class.cast(predicateSpec.getLeft()).getPath());
        assertEquals("meta.between.lower", PathExpression.class.cast(predicateSpec.getRight()).getPath());
        assertEquals("meta.between.upper", PathExpression.class.cast(predicateSpec.getExtraOperands().get(0)).getPath());
        assertEquals("between", predicateSpec.getOptions().getScope()[0]);
    }

    @Test
    public void shouldReadPredicateOptionsFromCanonicalShortcutAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("scopedName");
        Equal anno = field.getAnnotation(Equal.class);

        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);
        CompiledPredicateSpec predicateSpec = CompiledPredicateSpecs.simple(PredicateOperator.EQ, spec);

        assertEquals("scoped", spec.getEffectiveOptions().getScope()[0]);
        assertTrue(spec.getEffectiveOptions().isRequired());
        assertEquals("scoped", predicateSpec.getOptions().getScope()[0]);
        assertTrue(predicateSpec.getOptions().isRequired());
    }

    @Test
    public void shouldReadFilterPredicateRefFromAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("typedFilteredCodes");
        FilterIn anno = field.getAnnotation(FilterIn.class);

        CompiledAnnotationSpec<FilterIn> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(OnlyActivePredicate.class, spec.getCollectionPolicy().getPredicateFilter().getPredicateClass());
    }

    @Test
    public void shouldReadStructuredPredicateRefOnFilterAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("structuredTypedFilteredCodes");
        FilterIn anno = field.getAnnotation(FilterIn.class);

        CompiledAnnotationSpec<FilterIn> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(OnlyActivePredicate.class, spec.getCollectionPolicy().getPredicateFilter().getPredicateClass());
    }

    @Test
    public void shouldReadStructuredPredicateRefOnMembershipAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("structuredMembershipCodes");
        Membership anno = field.getAnnotation(Membership.class);

        CompiledAnnotationSpec<Membership> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(OnlyActivePredicate.class, spec.getCollectionPolicy().getPredicateFilter().getPredicateClass());
    }

    @Test
    public void shouldReadCollectionPolicyFromInShortcutAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("structuredInCodes");
        In anno = field.getAnnotation(In.class);

        CompiledAnnotationSpec<In> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(OnlyActivePredicate.class, spec.getCollectionPolicy().getPredicateFilter().getPredicateClass());
    }

    @Test
    public void shouldReadSplitCollectionPolicyFromInShortcutAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("splitInCodes");
        In anno = field.getAnnotation(In.class);

        CompiledAnnotationSpec<In> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertTrue(spec.getCollectionPolicy().isSplit());
        assertEquals(io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType.TO_INTEGER, spec.getCollectionPolicy().getTargetType());
    }

    @Test
    public void shouldReadStructuredPredicateRefOnCompareAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("structuredCompareInCodes");
        Compare anno = field.getAnnotation(Compare.class);

        CompiledAnnotationSpec<Compare> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(OnlyActivePredicate.class, spec.getCollectionPolicy().getPredicateFilter().getPredicateClass());
    }

    @Test
    public void shouldReadPredicateRefOnCompareAnnotation() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("structuredCompareInCodes");
        Compare anno = field.getAnnotation(Compare.class);

        CompiledAnnotationSpec<Compare> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(OnlyActivePredicate.class, spec.getCollectionPolicy().getPredicateFilter().getPredicateClass());
    }

    @Test
    public void shouldExposeCollectionPolicyThroughCompiledSpec() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("structuredMembershipCodes");
        Membership anno = field.getAnnotation(Membership.class);

        CompiledAnnotationSpec<Membership> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals(io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType.DEFAULT, spec.getCollectionPolicy().getTargetType());
        assertEquals(",", spec.getCollectionPolicy().getDecollator());
        assertEquals(OnlyActivePredicate.class, spec.getCollectionPolicy().getPredicateFilter().getPredicateClass());
    }

    @Test
    public void shouldReadAndNormalizeFieldValueThroughCompiledSpec() throws Exception {
        Field field = SpecQueryModel.class.getDeclaredField("trimmedName");
        Equal anno = field.getAnnotation(Equal.class);
        SpecQueryModel args = new SpecQueryModel();

        CompiledAnnotationSpec<Equal> spec = CompiledAnnotationSpec.of(SpecQueryModel.class, anno, field);

        assertEquals("  ACTIVE  ", spec.readFieldValue(args));
        assertEquals("ACTIVE", spec.readAndTrim(args));
        assertFalse(spec.shouldIgnore(spec.readAndTrim(args)));
        assertTrue(spec.shouldIgnore("   "));
        assertTrue(spec.shouldIgnore(null));
    }

    @SuppressWarnings("unused")
    private static final class SpecQueryModel {

        @Equal
        private String name = "demo";

        @Equal(options = @PredicateOptions(scope = { "scoped" }, required = true))
        private String scopedName = "demo";

        @Equal
        private String trimmedName = "  ACTIVE  ";

        @Equal(right = @Expr(type = ExprType.FUNCTION, function = @ExprFunction(name = "lower", type = String.class,
            args = { @ExprArg(type = ExprType.FUNCTION, function = @NestedExprFunction(name = "upper", type = String.class)) })))
        private String functionScopedName = "ACTIVE";

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
        private String nestedFunctionStatus = "ACTIVE";

        @Equal(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.VALUE, valueField = "keyword"))
        private String crossFieldStatus = "ACTIVE";

        @Between(right = @Expr(type = ExprType.PATH, path = "range.startAt"), extra = { @Expr(type = ExprType.PATH, path = "range.endAt") })
        private List<String> createdAt;

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "status"),
            right = @Expr(type = ExprType.PATH, path = "currentStatus"))
        private String status = "ACTIVE";

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "outer.status"),
            right = @Expr(type = ExprType.PATH, path = "inner.currentStatus"))
        private String canonicalStatus = "ACTIVE";

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "outer.status"),
            right = @Expr(type = ExprType.PATH, path = "inner.currentStatus"))
        private String conflictedCanonicalStatus = "ACTIVE";

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "status"),
            right = @Expr(type = ExprType.PATH, path = "currentStatus"),
            whereCompare = @Compare(left = @Expr(type = ExprType.PATH, path = "status"),
                right = @Expr(type = ExprType.FUNCTION,
                    function = @ExprFunction(name = "lower", type = String.class,
                        args = { @ExprArg(type = ExprType.FUNCTION, function = @NestedExprFunction(name = "upper", type = String.class)) }))),
            options = @PredicateOptions(scope = { "in-table-scope" }, required = true))
        private String configuredInTableStatus = "ACTIVE";

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "status"),
            right = @Expr(type = ExprType.PATH, path = "currentStatus"),
            where = @SubqueryGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"),
                    right = @Expr(type = ExprType.LITERAL, literal = "7", javaType = Integer.class)) },
                groups = { @NestedSubqueryGroup(junction = io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type.OR,
                    compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                        @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")) }) }))
        private String groupedInTableStatus = "ACTIVE";

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "status"),
            right = @Expr(type = ExprType.PATH, path = "currentStatus"),
            options = @PredicateOptions(scope = { "in-table-outer-scope" }, required = true, trim = false, ignoreBlank = true),
            where = @SubqueryGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId")) },
                groups = { @NestedSubqueryGroup(compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"),
                    right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE"), options = @PredicateOptions(scopeMode = ArrayMergeMode.CLEAR,
                        requiredMode = OptionSwitch.DISABLED, trimMode = OptionSwitch.ENABLED, ignoreBlankMode = OptionSwitch.DISABLED)) }) }))
        private String optionAwareInTableStatus = "ACTIVE";

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "status"),
            right = @Expr(type = ExprType.PATH, path = "currentStatus"),
            whereCompare = @Compare(op = CompareOp.NE, left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "currentStatus") }))))
        private String canonicalComparedInTableStatus = "ACTIVE";

        @InTable(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "status"),
            right = @Expr(type = ExprType.PATH, path = "currentStatus"),
            whereCompare = @Compare(op = CompareOp.NE, left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.FUNCTION,
                function = @ExprFunction(name = "lower", type = String.class, args = { @ExprArg(type = ExprType.PATH, path = "currentStatus") }))))
        private String conflictedCanonicalComparedInTableStatus = "ACTIVE";

        @Exists(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"),
            where = @SubqueryGroup(
                compare = {
                    @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"),
                        right = @Expr(type = ExprType.LITERAL, literal = "7", javaType = Integer.class)),
                    @Compare(left = @Expr(type = ExprType.PATH, path = "deleted"), right = @Expr(type = ExprType.LITERAL, literal = "false")) },
                groups = { @NestedSubqueryGroup(junction = io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type.OR,
                    compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                        @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")) }) }))
        private Boolean groupedExists = Boolean.TRUE;

        @Exists(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"),
            where = @SubqueryGroup(groups = { @NestedSubqueryGroup(junction = io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.Type.OR,
                groups = { @DeepSubqueryGroup(compare = {
                    @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")) }) }) }))
        private Boolean deepGroupedExists = Boolean.TRUE;

        @Exists(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"),
            options = @PredicateOptions(scope = { "outer-scope" }, required = true, trim = false, ignoreBlank = true),
            where = @SubqueryGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId")) },
                groups = { @NestedSubqueryGroup(compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"),
                    right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE"), options = @PredicateOptions(scopeMode = ArrayMergeMode.CLEAR,
                        requiredMode = OptionSwitch.DISABLED, trimMode = OptionSwitch.ENABLED, ignoreBlankMode = OptionSwitch.DISABLED)) }) }))
        private Boolean optionAwareExists = Boolean.TRUE;

        @NotExists(targetEntity = SpecQueryModel.class, left = @Expr(type = ExprType.PATH, path = "id"), right = @Expr(type = ExprType.PATH, path = "userId"),
            select = @Expr(type = ExprType.PATH, path = "id"),
            options = @PredicateOptions(scope = { "not-exists-outer-scope" }, required = true, trim = false, ignoreBlank = true),
            where = @SubqueryGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "tenantId"), right = @Expr(type = ExprType.VALUE, valueField = "tenantId")) },
                groups = { @NestedSubqueryGroup(compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"),
                    right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE"), options = @PredicateOptions(scopeMode = ArrayMergeMode.CLEAR,
                        requiredMode = OptionSwitch.DISABLED, trimMode = OptionSwitch.ENABLED, ignoreBlankMode = OptionSwitch.DISABLED)) }) }))
        private Boolean optionAwareNotExists = Boolean.TRUE;

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class)), left = @Expr(type = ExprType.PATH, path = "status")) })
        private String caseStatus = "ACTIVE";

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            left = @Expr(type = ExprType.PATH, path = "status")) })
        private String defaultCaseStatus = "ACTIVE";

        @Cases(value = { @Case(when = @CaseWhen(), left = @Expr(type = ExprType.PATH, path = "status")) })
        private String alwaysMatchCaseStatus = "ACTIVE";

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(op = io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp.EQ,
                right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")),
            left = @Expr(type = ExprType.PATH, path = "status")) })
        private String canonicalThenCaseStatus = "ACTIVE";

        @Cases(
            value = { @Case(when = @CaseWhen(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                left = @Expr(type = ExprType.PATH, path = "status")) })
        private String canonicalWhenCaseStatus = "ACTIVE";

        @Cases(left = @Expr(type = ExprType.PATH, path = "audit.status"),
            value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
                then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class))) })
        private String canonicalCaseStatus = "ACTIVE";

        @Cases(left = @Expr(type = ExprType.PATH, path = "audit.status"),
            value = { @Case(left = @Expr(type = ExprType.PATH, path = "audit.branch.status"),
                when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
                then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class))) })
        private String canonicalBranchCaseStatus = "ACTIVE";

        @Cases(options = @PredicateOptions(scope = { "outer-scope" }, trim = false),
            value = { @Case(left = @Expr(type = ExprType.PATH, path = "status"), options = @PredicateOptions(scope = { "case-scope" }, required = true),
                when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
                then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class))) })
        private String branchOptionsCaseStatus = "ACTIVE";

        @Cases(options = @PredicateOptions(scope = { "outer-scope" }, required = true, trim = false, ignoreBlank = true),
            value = { @Case(left = @Expr(type = ExprType.PATH, path = "status"),
                options = @PredicateOptions(scopeMode = ArrayMergeMode.CLEAR, requiredMode = OptionSwitch.DISABLED, trimMode = OptionSwitch.ENABLED,
                    ignoreBlankMode = OptionSwitch.DISABLED),
                when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
                then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class))) })
        private String branchOverrideCaseStatus = "ACTIVE";

        @Cases(value = { @Case(when = @CaseWhen(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE"),
            predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)), left = @Expr(type = ExprType.PATH, path = "status")) })
        private String conflictedCanonicalWhenCaseStatus = "ACTIVE";

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(processor = @ProcessorRef(processorClass = TestPredicateProcessor.class), op = CompareOp.EQ,
                right = @Expr(type = ExprType.PATH, path = "audit.currentStatus")),
            left = @Expr(type = ExprType.PATH, path = "status")) })
        private String conflictedCanonicalThenCaseStatus = "ACTIVE";

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
        private String groupWhenCaseStatus = "ACTIVE";

        private String phase = "PENDING";

        public String getGroupWhenCaseStatus() {
            return this.groupWhenCaseStatus;
        }

        public void setGroupWhenCaseStatus(final String groupWhenCaseStatus) {
            this.groupWhenCaseStatus = groupWhenCaseStatus;
        }

        public String getPhase() {
            return this.phase;
        }

        public void setPhase(final String phase) {
            this.phase = phase;
        }

        @Cases(value = { @Case(when = @CaseWhen(predicate = @PredicateRef(predicateClass = AlwaysTruePredicate.class)),
            then = @CaseThen(group = @CaseThenGroup(
                compare = { @Compare(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                    @Compare(left = @Expr(type = ExprType.PATH, path = "phase"), right = @Expr(type = ExprType.LITERAL, literal = "PENDING")) }))) })
        private String groupThenCaseStatus = "ACTIVE";

        public String getGroupThenCaseStatus() {
            return this.groupThenCaseStatus;
        }

        public void setGroupThenCaseStatus(final String groupThenCaseStatus) {
            this.groupThenCaseStatus = groupThenCaseStatus;
        }

        @Cases(otherwise = @CaseElse(enabled = true, then = @CaseThen(op = CompareOp.EQ, right = @Expr(type = ExprType.LITERAL, literal = "FALLBACK"))),
            value = { @Case(when = @CaseWhen(left = @Expr(type = ExprType.PATH, path = "status"), right = @Expr(type = ExprType.LITERAL, literal = "ACTIVE")),
                left = @Expr(type = ExprType.PATH, path = "status")) })
        private String elseCaseStatus = "INACTIVE";

        public String getElseCaseStatus() {
            return this.elseCaseStatus;
        }

        public void setElseCaseStatus(final String elseCaseStatus) {
            this.elseCaseStatus = elseCaseStatus;
        }

        @CanonicalSelectable
        private String metaStatus = "ACTIVE";

        @CanonicalExisted
        private String metaExistedStatus = "ACTIVE";

        @Selectable
        private String zeroConfigSelectableStatus = "ACTIVE";

        @Existed
        private String zeroConfigExistedStatus = "ACTIVE";

        @CanonicalExtendedSelectable
        private String metaExtendedStatus = "ACTIVE";

        @ComposedCompareSelectable
        private String metaComposedStatus = "ACTIVE";

        @ComposedEqualSelectable
        private String metaEqualStatus = "ACTIVE";

        @ComposedBetweenSelectable
        private String metaBetweenStatus = "ACTIVE";

        @FilterIn(collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> typedFilteredCodes;

        @FilterIn(collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> structuredTypedFilteredCodes;

        @Membership(op = CompareOp.IN, left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> structuredMembershipCodes;

        @In(left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> structuredInCodes;

        @In(left = @Expr(type = ExprType.PATH, path = "statusIds"),
            collection = @CollectionPolicy(split = true, targetType = io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType.TO_INTEGER))
        private String splitInCodes;

        @Compare(op = CompareOp.IN, left = @Expr(type = ExprType.PATH, path = "statuses"),
            collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = OnlyActivePredicate.class)))
        private List<String> structuredCompareInCodes;

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getScopedName() {
            return this.scopedName;
        }

        public void setScopedName(final String scopedName) {
            this.scopedName = scopedName;
        }

        public String getTrimmedName() {
            return this.trimmedName;
        }

        public void setTrimmedName(final String trimmedName) {
            this.trimmedName = trimmedName;
        }

        public String getFunctionScopedName() {
            return this.functionScopedName;
        }

        public void setFunctionScopedName(final String functionScopedName) {
            this.functionScopedName = functionScopedName;
        }

        public String getNestedFunctionStatus() {
            return this.nestedFunctionStatus;
        }

        public void setNestedFunctionStatus(final String nestedFunctionStatus) {
            this.nestedFunctionStatus = nestedFunctionStatus;
        }

        public String getCrossFieldStatus() {
            return this.crossFieldStatus;
        }

        public void setCrossFieldStatus(final String crossFieldStatus) {
            this.crossFieldStatus = crossFieldStatus;
        }

        public String getKeyword() {
            return "demo-keyword";
        }

        public String getSuffix() {
            return "tail";
        }

        public List<String> getCreatedAt() {
            return this.createdAt;
        }

        public void setCreatedAt(final List<String> createdAt) {
            this.createdAt = createdAt;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public String getCaseStatus() {
            return this.caseStatus;
        }

        public void setCaseStatus(final String caseStatus) {
            this.caseStatus = caseStatus;
        }

        public String getDefaultCaseStatus() {
            return this.defaultCaseStatus;
        }

        public String getCanonicalThenCaseStatus() {
            return this.canonicalThenCaseStatus;
        }

        public String getCanonicalWhenCaseStatus() {
            return this.canonicalWhenCaseStatus;
        }

        public String getAlwaysMatchCaseStatus() {
            return this.alwaysMatchCaseStatus;
        }

        public void setDefaultCaseStatus(final String defaultCaseStatus) {
            this.defaultCaseStatus = defaultCaseStatus;
        }

        public void setAlwaysMatchCaseStatus(final String alwaysMatchCaseStatus) {
            this.alwaysMatchCaseStatus = alwaysMatchCaseStatus;
        }

        public void setCanonicalThenCaseStatus(final String canonicalThenCaseStatus) {
            this.canonicalThenCaseStatus = canonicalThenCaseStatus;
        }

        public void setCanonicalWhenCaseStatus(final String canonicalWhenCaseStatus) {
            this.canonicalWhenCaseStatus = canonicalWhenCaseStatus;
        }

        public String getCanonicalCaseStatus() {
            return this.canonicalCaseStatus;
        }

        public void setCanonicalCaseStatus(final String canonicalCaseStatus) {
            this.canonicalCaseStatus = canonicalCaseStatus;
        }

        public String getCanonicalBranchCaseStatus() {
            return this.canonicalBranchCaseStatus;
        }

        public void setCanonicalBranchCaseStatus(final String canonicalBranchCaseStatus) {
            this.canonicalBranchCaseStatus = canonicalBranchCaseStatus;
        }

        public String getBranchOptionsCaseStatus() {
            return this.branchOptionsCaseStatus;
        }

        public void setBranchOptionsCaseStatus(final String branchOptionsCaseStatus) {
            this.branchOptionsCaseStatus = branchOptionsCaseStatus;
        }

        public String getBranchOverrideCaseStatus() {
            return this.branchOverrideCaseStatus;
        }

        public void setBranchOverrideCaseStatus(final String branchOverrideCaseStatus) {
            this.branchOverrideCaseStatus = branchOverrideCaseStatus;
        }

        public String getConflictedCanonicalWhenCaseStatus() {
            return this.conflictedCanonicalWhenCaseStatus;
        }

        public void setConflictedCanonicalWhenCaseStatus(final String conflictedCanonicalWhenCaseStatus) {
            this.conflictedCanonicalWhenCaseStatus = conflictedCanonicalWhenCaseStatus;
        }

        public String getConflictedCanonicalThenCaseStatus() {
            return this.conflictedCanonicalThenCaseStatus;
        }

        public void setConflictedCanonicalThenCaseStatus(final String conflictedCanonicalThenCaseStatus) {
            this.conflictedCanonicalThenCaseStatus = conflictedCanonicalThenCaseStatus;
        }

        public String getCanonicalStatus() {
            return this.canonicalStatus;
        }

        public void setCanonicalStatus(final String canonicalStatus) {
            this.canonicalStatus = canonicalStatus;
        }

        public String getConflictedCanonicalStatus() {
            return this.conflictedCanonicalStatus;
        }

        public void setConflictedCanonicalStatus(final String conflictedCanonicalStatus) {
            this.conflictedCanonicalStatus = conflictedCanonicalStatus;
        }

        public String getConfiguredInTableStatus() {
            return this.configuredInTableStatus;
        }

        public void setConfiguredInTableStatus(final String configuredInTableStatus) {
            this.configuredInTableStatus = configuredInTableStatus;
        }

        public String getGroupedInTableStatus() {
            return this.groupedInTableStatus;
        }

        public void setGroupedInTableStatus(final String groupedInTableStatus) {
            this.groupedInTableStatus = groupedInTableStatus;
        }

        public String getOptionAwareInTableStatus() {
            return this.optionAwareInTableStatus;
        }

        public void setOptionAwareInTableStatus(final String optionAwareInTableStatus) {
            this.optionAwareInTableStatus = optionAwareInTableStatus;
        }

        public String getCanonicalComparedInTableStatus() {
            return this.canonicalComparedInTableStatus;
        }

        public void setCanonicalComparedInTableStatus(final String canonicalComparedInTableStatus) {
            this.canonicalComparedInTableStatus = canonicalComparedInTableStatus;
        }

        public String getConflictedCanonicalComparedInTableStatus() {
            return this.conflictedCanonicalComparedInTableStatus;
        }

        public void setConflictedCanonicalComparedInTableStatus(final String conflictedCanonicalComparedInTableStatus) {
            this.conflictedCanonicalComparedInTableStatus = conflictedCanonicalComparedInTableStatus;
        }

        public Boolean getGroupedExists() {
            return this.groupedExists;
        }

        public void setGroupedExists(final Boolean groupedExists) {
            this.groupedExists = groupedExists;
        }

        public Boolean getDeepGroupedExists() {
            return this.deepGroupedExists;
        }

        public void setDeepGroupedExists(final Boolean deepGroupedExists) {
            this.deepGroupedExists = deepGroupedExists;
        }

        public Boolean getOptionAwareExists() {
            return this.optionAwareExists;
        }

        public void setOptionAwareExists(final Boolean optionAwareExists) {
            this.optionAwareExists = optionAwareExists;
        }

        public Boolean getOptionAwareNotExists() {
            return this.optionAwareNotExists;
        }

        public void setOptionAwareNotExists(final Boolean optionAwareNotExists) {
            this.optionAwareNotExists = optionAwareNotExists;
        }

        public String getMetaStatus() {
            return this.metaStatus;
        }

        public void setMetaStatus(final String metaStatus) {
            this.metaStatus = metaStatus;
        }

        public String getMetaExistedStatus() {
            return this.metaExistedStatus;
        }

        public void setMetaExistedStatus(final String metaExistedStatus) {
            this.metaExistedStatus = metaExistedStatus;
        }

        public String getZeroConfigSelectableStatus() {
            return this.zeroConfigSelectableStatus;
        }

        public String getZeroConfigExistedStatus() {
            return this.zeroConfigExistedStatus;
        }

        public String getMetaExtendedStatus() {
            return this.metaExtendedStatus;
        }

        public void setMetaExtendedStatus(final String metaExtendedStatus) {
            this.metaExtendedStatus = metaExtendedStatus;
        }

        public String getMetaComposedStatus() {
            return this.metaComposedStatus;
        }

        public void setMetaComposedStatus(final String metaComposedStatus) {
            this.metaComposedStatus = metaComposedStatus;
        }

        public String getMetaEqualStatus() {
            return this.metaEqualStatus;
        }

        public void setMetaEqualStatus(final String metaEqualStatus) {
            this.metaEqualStatus = metaEqualStatus;
        }

        public String getMetaBetweenStatus() {
            return this.metaBetweenStatus;
        }

        public void setMetaBetweenStatus(final String metaBetweenStatus) {
            this.metaBetweenStatus = metaBetweenStatus;
        }

        public List<String> getTypedFilteredCodes() {
            return this.typedFilteredCodes;
        }

        public void setTypedFilteredCodes(final List<String> typedFilteredCodes) {
            this.typedFilteredCodes = typedFilteredCodes;
        }

        public List<String> getStructuredTypedFilteredCodes() {
            return this.structuredTypedFilteredCodes;
        }

        public void setStructuredTypedFilteredCodes(final List<String> structuredTypedFilteredCodes) {
            this.structuredTypedFilteredCodes = structuredTypedFilteredCodes;
        }

        public List<String> getStructuredMembershipCodes() {
            return this.structuredMembershipCodes;
        }

        public void setStructuredMembershipCodes(final List<String> structuredMembershipCodes) {
            this.structuredMembershipCodes = structuredMembershipCodes;
        }

        public List<String> getStructuredInCodes() {
            return this.structuredInCodes;
        }

        public void setStructuredInCodes(final List<String> structuredInCodes) {
            this.structuredInCodes = structuredInCodes;
        }

        public String getSplitInCodes() {
            return this.splitInCodes;
        }

        public void setSplitInCodes(final String splitInCodes) {
            this.splitInCodes = splitInCodes;
        }

        public List<String> getStructuredCompareInCodes() {
            return this.structuredCompareInCodes;
        }

        public void setStructuredCompareInCodes(final List<String> structuredCompareInCodes) {
            this.structuredCompareInCodes = structuredCompareInCodes;
        }

    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Selectable(left = @Expr(type = ExprType.PATH, path = "meta.userStatus"), right = @Expr(type = ExprType.PATH, path = "meta.audit.currentStatus"),
        options = @PredicateOptions(scope = { DEFAULT }))
    public @interface CanonicalSelectable {
    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Existed(left = @Expr(type = ExprType.PATH, path = "meta.existedStatus"), right = @Expr(type = ExprType.PATH, path = "meta.audit.existedCurrentStatus"))
    public @interface CanonicalExisted {
    }

    @Target({ java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD })
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Selectable(left = @Expr(type = ExprType.PATH, path = "meta.range.start"), extra = { @Expr(type = ExprType.PATH, path = "meta.range.end") },
        right = @Expr(type = ExprType.VALUE))
    public @interface CanonicalExtendedSelectable {
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

    public static final class AlwaysTruePredicate implements java.util.function.Predicate<Object> {

        @Override
        public boolean test(final Object value) {
            return true;
        }

    }

    public static final class TestPredicateProcessor implements PredicateProcessor {

        private static final long serialVersionUID = 1L;

        @Override
        public javax.persistence.criteria.Predicate create(
            final io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessorContext context) {
            return null;
        }

    }

    public static final class MismatchPredicateProcessor implements PredicateProcessor {

        private static final long serialVersionUID = 1L;

        @Override
        public javax.persistence.criteria.Predicate create(
            final io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessorContext context) {
            return null;
        }

    }

    public static final class OnlyActivePredicate implements java.util.function.Predicate<Object> {

        @Override
        public boolean test(final Object value) {
            return "ACTIVE".equals(value);
        }

    }

    public static final class NeverPredicate implements java.util.function.Predicate<Object> {

        @Override
        public boolean test(final Object value) {
            return false;
        }

    }

}
