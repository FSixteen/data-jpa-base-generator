package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.Constraint;
import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.ProviderRef;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;

class CanonicalShortcutAnnotationContractTest {

    private static final Set<
        Class<? extends Annotation>> PURE_BINARY_SHORTCUTS = new HashSet<Class<? extends Annotation>>(Arrays.<Class<? extends Annotation>>asList(Equal.class,
            NotEqual.class, Gt.class, Gte.class, Lt.class, Lte.class, GreaterThan.class, GreaterThanOrEqualTo.class, LessThan.class, LessThanOrEqualTo.class,
            Like.class, NotLike.class, StartWith.class, EndWith.class, IgnoreCaseEqual.class, IgnoreCaseLike.class));

    private static final Set<Class<? extends Annotation>> PURE_RANGE_SHORTCUTS = new HashSet<Class<? extends Annotation>>(
        Arrays.<Class<? extends Annotation>>asList(Between.class, NotBetween.class));

    private static final Set<Class<? extends Annotation>> PURE_MEMBERSHIP_SHORTCUTS = new HashSet<Class<? extends Annotation>>(
        Arrays.<Class<? extends Annotation>>asList(In.class, NotIn.class));

    private static final Set<Class<? extends Annotation>> PURE_NULL_SHORTCUTS = new HashSet<Class<? extends Annotation>>(
        Arrays.<Class<? extends Annotation>>asList(IsNull.class, IsNotNull.class, Null.class));

    private static final Set<Class<? extends Annotation>> PURE_CANONICAL_DSL_SHORTCUTS = new HashSet<Class<? extends Annotation>>(
        Arrays.<Class<? extends Annotation>>asList(Compare.class, SplitIn.class, SplitNotIn.class, FilterIn.class, FilterNotIn.class, Unique.class));

    private static final Set<String> LEGACY_METHODS = new HashSet<String>(
        Arrays.asList("scope", "groups", "field", "fieldType", "fieldLiteral", "fieldFunction", "fieldProcessor", "valueType", "valueLiteral", "valueFunction",
            "valueFunctions", "valueProcessor", "required", "not", "ignoreNull", "ignoreEmpty", "ignoreBlank", "trim"));

    @Test
    void shouldKeepCanonicalDslShortcutsPureCanonical() {
        assertExactMethods(Unique.class, "left", "right", "options");
        assertExactMethods(SplitIn.class, "collection", "left", "right", "options");
        assertExactMethods(SplitNotIn.class, "collection", "left", "right", "options");
        assertExactMethods(FilterIn.class, "collection", "left", "right", "options");
        assertExactMethods(FilterNotIn.class, "collection", "left", "right", "options");
        PURE_CANONICAL_DSL_SHORTCUTS.forEach(annotation -> {
            assertNoLegacyMethods(annotation);
            assertNotDeprecatedShortcut(annotation);
        });
        assertExactMethods(Compare.class, "op", "collection", "left", "right", "extra", "options");
        assertExactMethods(Membership.class, "op", "collection", "left", "right", "options");
    }

    @Test
    void shouldKeepBinaryShortcutAnnotationsPureCanonical() {
        PURE_BINARY_SHORTCUTS.forEach(annotation -> {
            assertExactMethods(annotation, "left", "right", "options");
            assertNoLegacyMethods(annotation);
            assertNotDeprecatedShortcut(annotation);
        });
    }

    @Test
    void shouldKeepLegacyDirectionalLikeShortcutsCanonicalButDeprecated() {
        assertExactMethods(LeftLike.class, "left", "right", "options");
        assertExactMethods(RightLike.class, "left", "right", "options");
        assertNoLegacyMethods(LeftLike.class);
        assertNoLegacyMethods(RightLike.class);
        assertDeprecatedShortcut(LeftLike.class);
        assertDeprecatedShortcut(RightLike.class);
    }

    @Test
    void shouldKeepBetweenShortcutsCanonicalFirst() {
        PURE_RANGE_SHORTCUTS.forEach(annotation -> {
            assertExactMethods(annotation, "left", "right", "extra", "options");
            assertNoLegacyMethods(annotation);
            assertNotDeprecatedShortcut(annotation);
        });
    }

    @Test
    void shouldKeepMembershipShortcutAnnotationsPureCanonical() {
        PURE_MEMBERSHIP_SHORTCUTS.forEach(annotation -> {
            assertExactMethods(annotation, "collection", "left", "right", "options");
            assertNoLegacyMethods(annotation);
            assertNotDeprecatedShortcut(annotation);
        });
    }

    @Test
    void shouldKeepNullShortcutsCanonicalFirst() {
        assertExactMethods(IsNull.class, "left", "options");
        assertExactMethods(IsNotNull.class, "left", "options");
        assertExactMethods(Null.class, "whenTrueUse", "whenFalseUse", "left", "options");
        PURE_NULL_SHORTCUTS.forEach(annotation -> {
            assertNoLegacyMethods(annotation);
            assertNotDeprecatedShortcut(annotation);
        });
    }

    @Test
    void shouldKeepCasesCanonicalFirst() {
        assertExactMethods(Cases.class, "value", "left", "options", "otherwise");
        assertNoLegacyMethods(Cases.class);
        assertTrue(!Cases.class.isAnnotationPresent(Selectable.class));

        assertExactMethods(Case.class, "when", "then", "left", "options");
        assertExactMethods(CaseElse.class, "enabled", "then", "left", "options");
        assertNoLegacyMethods(Case.class);
        assertNotDeprecatedShortcut(Cases.class);
        assertNotDeprecatedShortcut(Case.class);
        assertNotDeprecatedShortcut(CaseElse.class);
        assertTrue(!Case.class.isAnnotationPresent(Selectable.class));

        assertExactMethods(CaseWhen.class, "predicate", "group", "op", "left", "right", "extra");
        assertExactMethods(CaseWhenGroup.class, "junction", "compare", "range", "membership", "nullCheck", "groups");
        assertExactMethods(NestedCaseWhenGroup.class, "junction", "compare", "range", "membership", "nullCheck", "groups");
        assertExactMethods(DeepCaseWhenGroup.class, "junction", "compare", "range", "membership", "nullCheck");
        assertExactMethods(CaseThen.class, "processor", "group", "op", "right", "extra");
        assertExactMethods(CaseThenGroup.class, "junction", "compare", "range", "membership", "nullCheck", "groups");
        assertExactMethods(NestedCaseThenGroup.class, "junction", "compare", "range", "membership", "nullCheck", "groups");
        assertExactMethods(DeepCaseThenGroup.class, "junction", "compare", "range", "membership", "nullCheck");
        assertTrue(!CaseWhen.class.isAnnotationPresent(Deprecated.class));
        assertTrue(!CaseThen.class.isAnnotationPresent(Deprecated.class));
        assertTrue(!CaseWhen.class.isAnnotationPresent(Selectable.class));
        assertTrue(!CaseThen.class.isAnnotationPresent(Selectable.class));
    }

    @Test
    void shouldKeepInTableCanonicalOnly() {
        assertExactMethods(InTable.class, "targetEntity", "left", "right", "whereCompare", "where", "options");
        assertNoLegacyMethods(InTable.class);
        assertNotDeprecatedShortcut(InTable.class);
    }

    @Test
    void shouldKeepTupleValueShortcutsCanonicalOnly() {
        assertExactMethods(TupleInValues.class, "columns", "tupleField", "options");
        assertExactMethods(TupleNotInValues.class, "columns", "tupleField", "options");
        assertExactMethods(TupleColumn.class, "leftPath", "itemPath", "itemIndex", "targetType", "targetFormat");
        assertNoLegacyMethods(TupleInValues.class);
        assertNoLegacyMethods(TupleNotInValues.class);
        assertNotDeprecatedShortcut(TupleInValues.class);
        assertNotDeprecatedShortcut(TupleNotInValues.class);
        assertTrue(!TupleColumn.class.isAnnotationPresent(Deprecated.class));
    }

    @Test
    void shouldKeepTupleSubqueryShortcutsCanonicalOnly() {
        assertExactMethods(TupleExists.class, "targetEntity", "pairs", "whereCompare", "where", "options");
        assertExactMethods(TupleNotExists.class, "targetEntity", "pairs", "whereCompare", "where", "options");
        assertExactMethods(TuplePair.class, "left", "right");
        assertNoLegacyMethods(TupleExists.class);
        assertNoLegacyMethods(TupleNotExists.class);
        assertNotDeprecatedShortcut(TupleExists.class);
        assertNotDeprecatedShortcut(TupleNotExists.class);
        assertTrue(!TuplePair.class.isAnnotationPresent(Deprecated.class));
    }

    @Test
    void shouldKeepEqualAsPureCanonicalShortcut() throws Exception {
        assertExactMethods(Equal.class, "left", "right", "options");
        assertNotDeprecatedShortcut(Equal.class);

        Equal equal = PureEqualModel.class.getDeclaredField("name").getAnnotation(Equal.class);
        assertEquals(ExprType.PATH, equal.left().type());
        assertEquals(ExprType.VALUE, equal.right().type());
        assertEquals(0, equal.options().scope().length);
        assertEquals(0, equal.options().groups().length);
    }

    @Test
    void shouldKeepIgnoreCaseShortcutsAsPureCanonicalShortcuts() throws Exception {
        IgnoreCaseEqual ignoreCaseEqual = IgnoreCaseModel.class.getDeclaredField("name").getAnnotation(IgnoreCaseEqual.class);
        IgnoreCaseLike ignoreCaseLike = IgnoreCaseModel.class.getDeclaredField("keyword").getAnnotation(IgnoreCaseLike.class);

        assertExactMethods(IgnoreCaseEqual.class, "left", "right", "options");
        assertExactMethods(IgnoreCaseLike.class, "left", "right", "options");
        assertEquals(ExprType.FUNCTION, ignoreCaseEqual.left().type());
        assertEquals("lower", ignoreCaseEqual.left().function().name());
        assertEquals(ExprType.FUNCTION, ignoreCaseEqual.right().type());
        assertEquals("lower", ignoreCaseEqual.right().function().name());
        assertEquals(ExprType.FUNCTION, ignoreCaseLike.left().type());
        assertEquals("lower", ignoreCaseLike.left().function().name());
        assertEquals(ExprType.FUNCTION, ignoreCaseLike.right().type());
        assertEquals("lower", ignoreCaseLike.right().function().name());
    }

    @Test
    void shouldExposeCanonicalFamilyMetadataThroughMetaAnnotations() {
        assertEquals(CompareOp.EQ, Equal.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.GT, Gt.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.GT, GreaterThan.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.GTE, GreaterThanOrEqualTo.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.LT, LessThan.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.LTE, LessThanOrEqualTo.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.BETWEEN, Between.class.getAnnotation(Range.class).op());
        assertEquals(CompareOp.IN, SplitIn.class.getAnnotation(Membership.class).op());
        assertTrue(SplitIn.class.getAnnotation(Membership.class).collection().split());
        assertEquals(CompareOp.NOT_IN, SplitNotIn.class.getAnnotation(Membership.class).op());
        assertEquals(CompareOp.IN, FilterIn.class.getAnnotation(Membership.class).op());
        assertEquals(CompareOp.NOT_IN, FilterNotIn.class.getAnnotation(Membership.class).op());
        assertEquals(CompareOp.IS_NULL, IsNull.class.getAnnotation(NullCheck.class).op());
        assertEquals(CompareOp.LIKE, Like.class.getAnnotation(TextMatch.class).op());
        assertEquals(CompareOp.NOT_LIKE, NotLike.class.getAnnotation(TextMatch.class).op());
        assertEquals(CompareOp.STARTS_WITH, LeftLike.class.getAnnotation(TextMatch.class).op());
        assertEquals(CompareOp.ENDS_WITH, RightLike.class.getAnnotation(TextMatch.class).op());
        assertEquals(CompareOp.STARTS_WITH, StartWith.class.getAnnotation(TextMatch.class).op());
        assertEquals(CompareOp.ENDS_WITH, EndWith.class.getAnnotation(TextMatch.class).op());
        assertEquals(CompareOp.EQ, IgnoreCaseEqual.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.LIKE, IgnoreCaseLike.class.getAnnotation(TextMatch.class).op());
    }

    @Test
    void shouldKeepShortcutAnnotationsDirectlyBoundToCanonicalRoots() {
        assertDirectCustomMetaAnnotations(Equal.class, Compare.class);
        assertDirectCustomMetaAnnotations(NotEqual.class, Compare.class);
        assertDirectCustomMetaAnnotations(Gt.class, Compare.class);
        assertDirectCustomMetaAnnotations(Gte.class, Compare.class);
        assertDirectCustomMetaAnnotations(Lt.class, Compare.class);
        assertDirectCustomMetaAnnotations(Lte.class, Compare.class);
        assertDirectCustomMetaAnnotations(GreaterThan.class, Compare.class);
        assertDirectCustomMetaAnnotations(GreaterThanOrEqualTo.class, Compare.class);
        assertDirectCustomMetaAnnotations(LessThan.class, Compare.class);
        assertDirectCustomMetaAnnotations(LessThanOrEqualTo.class, Compare.class);
        assertDirectCustomMetaAnnotations(Between.class, Range.class);
        assertDirectCustomMetaAnnotations(NotBetween.class, Range.class);
        assertDirectCustomMetaAnnotations(In.class, Membership.class);
        assertDirectCustomMetaAnnotations(NotIn.class, Membership.class);
        assertDirectCustomMetaAnnotations(SplitIn.class, Membership.class);
        assertDirectCustomMetaAnnotations(SplitNotIn.class, Membership.class);
        assertDirectCustomMetaAnnotations(FilterIn.class, Membership.class);
        assertDirectCustomMetaAnnotations(FilterNotIn.class, Membership.class);
        assertDirectCustomMetaAnnotations(Like.class, TextMatch.class);
        assertDirectCustomMetaAnnotations(NotLike.class, TextMatch.class);
        assertDirectCustomMetaAnnotations(LeftLike.class, TextMatch.class);
        assertDirectCustomMetaAnnotations(RightLike.class, TextMatch.class);
        assertDirectCustomMetaAnnotations(StartWith.class, TextMatch.class);
        assertDirectCustomMetaAnnotations(EndWith.class, TextMatch.class);
        assertDirectCustomMetaAnnotations(IgnoreCaseEqual.class, Compare.class);
        assertDirectCustomMetaAnnotations(IgnoreCaseLike.class, TextMatch.class);
        assertDirectCustomMetaAnnotations(IsNull.class, NullCheck.class);
        assertDirectCustomMetaAnnotations(IsNotNull.class, NullCheck.class);
        assertDirectCustomMetaAnnotations(Exists.class, PredicateRole.class, SubqueryPredicate.class);
        assertDirectCustomMetaAnnotations(NotExists.class, PredicateRole.class, SubqueryPredicate.class);
        assertDirectCustomMetaAnnotations(InTable.class, PredicateRole.class, SubqueryPredicate.class);
        assertDirectCustomMetaAnnotations(TupleInValues.class, PredicateRole.class);
        assertDirectCustomMetaAnnotations(TupleNotInValues.class, PredicateRole.class);
        assertDirectCustomMetaAnnotations(TupleExists.class, PredicateRole.class);
        assertDirectCustomMetaAnnotations(TupleNotExists.class, PredicateRole.class);
        assertDirectCustomMetaAnnotations(Unique.class, Existed.class, Compare.class);
    }

    @Test
    void shouldKeepCanonicalRootsSelfDescribingThroughPredicateRole() {
        assertTrue(Compare.class.isAnnotationPresent(PredicateRole.class));
        assertTrue(Range.class.isAnnotationPresent(PredicateRole.class));
        assertTrue(Membership.class.isAnnotationPresent(PredicateRole.class));
        assertTrue(TextMatch.class.isAnnotationPresent(PredicateRole.class));
        assertTrue(NullCheck.class.isAnnotationPresent(PredicateRole.class));
        assertTrue(SubqueryPredicate.class.isAnnotationPresent(PredicateRole.class));
        assertTrue(Cases.class.isAnnotationPresent(PredicateRole.class));
    }

    @Test
    void shouldPreferSelectionExistenceNamingOnPredicateRoleDeclarations() {
        assertTrue(Selectable.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(!Selectable.class.getAnnotation(PredicateRole.class).selectable());
        assertTrue(Existed.class.getAnnotation(PredicateRole.class).existence());
        assertTrue(!Existed.class.getAnnotation(PredicateRole.class).existed());

        assertTrue(Compare.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(Range.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(Membership.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(TextMatch.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(NullCheck.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(SubqueryPredicate.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(Cases.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(Null.class.getAnnotation(PredicateRole.class).selection());
    }

    @Test
    void shouldKeepShortcutSemanticBoundariesStable() {
        assertDirectCustomMetaAnnotations(Null.class, PredicateRole.class);
        assertDirectCustomMetaAnnotations(IsNull.class, NullCheck.class);
        assertDirectCustomMetaAnnotations(IsNotNull.class, NullCheck.class);
        assertDirectCustomMetaAnnotations(Exists.class, PredicateRole.class, SubqueryPredicate.class);
        assertDirectCustomMetaAnnotations(NotExists.class, PredicateRole.class, SubqueryPredicate.class);
        assertDirectCustomMetaAnnotations(InTable.class, PredicateRole.class, SubqueryPredicate.class);
        assertDirectCustomMetaAnnotations(Unique.class, Existed.class, Compare.class);

        assertTrue(Null.class.getAnnotation(PredicateRole.class).selection());
        assertTrue(!Null.class.getAnnotation(PredicateRole.class).existence());
        assertTrue(Unique.class.isAnnotationPresent(Existed.class));
        assertTrue(!Unique.class.isAnnotationPresent(Selectable.class));
        assertEquals(SubqueryMode.EXISTS, Exists.class.getAnnotation(SubqueryPredicate.class).mode());
        assertEquals(SubqueryMode.NOT_EXISTS, NotExists.class.getAnnotation(SubqueryPredicate.class).mode());
        assertEquals(SubqueryMode.IN, InTable.class.getAnnotation(SubqueryPredicate.class).mode());
    }

    @Test
    void shouldAllowShortcutWrappersToRelyOnSpecificCanonicalMetaWithoutDirectSelectableMarker() {
        assertTrue(!Equal.class.isAnnotationPresent(Selectable.class));
        assertTrue(!Between.class.isAnnotationPresent(Selectable.class));
        assertTrue(!SplitIn.class.isAnnotationPresent(Selectable.class));
        assertTrue(!IsNull.class.isAnnotationPresent(Selectable.class));
        assertTrue(!Unique.class.isAnnotationPresent(Selectable.class));
        assertTrue(!Range.class.isAnnotationPresent(Selectable.class));
        assertTrue(!Membership.class.isAnnotationPresent(Selectable.class));
        assertTrue(!NullCheck.class.isAnnotationPresent(Selectable.class));
        assertTrue(!TextMatch.class.isAnnotationPresent(Selectable.class));
        assertTrue(!Exists.class.isAnnotationPresent(Selectable.class));
        assertTrue(!NotExists.class.isAnnotationPresent(Selectable.class));
        assertTrue(!InTable.class.isAnnotationPresent(Selectable.class));
        assertTrue(!Cases.class.isAnnotationPresent(Selectable.class));
        assertTrue(!Null.class.isAnnotationPresent(Selectable.class));
        assertTrue(Unique.class.isAnnotationPresent(Existed.class));
        assertEquals(CompareOp.EQ, Selectable.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.EQ, Existed.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.BETWEEN, Range.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.IN, Membership.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.IS_NULL, NullCheck.class.getAnnotation(Compare.class).op());
        assertEquals(CompareOp.LIKE, TextMatch.class.getAnnotation(Compare.class).op());
    }

    @Test
    void shouldKeepMetaAnnotationsCanonicalAndAnnotationScoped() throws Exception {
        assertExactMethods(Selectable.class, "left", "right", "extra", "options");
        assertNoLegacyMethods(Selectable.class);
        assertNotDeprecatedShortcut(Selectable.class);

        assertExactMethods(Existed.class, "left", "right", "extra", "options");
        assertNoLegacyMethods(Existed.class);
        assertNotDeprecatedShortcut(Existed.class);

        assertExactMethods(Constraint.class, "provider");
        assertEquals(1, Constraint.class.getAnnotation(java.lang.annotation.Target.class).value().length);
        assertEquals(ElementType.ANNOTATION_TYPE, Constraint.class.getAnnotation(java.lang.annotation.Target.class).value()[0]);
        assertTrue(!Constraint.class.isAnnotationPresent(Deprecated.class));

        assertExactMethods(ProviderRef.class, "providerClass");

        assertExactMethods(CollectionPolicy.class, "predicate", "split", "decollator", "regexp", "targetType", "targetFormat");

        assertExactMethods(PredicateRef.class, "predicateClass");
        assertEquals(Void.class, PredicateRef.class.getDeclaredMethod("predicateClass").getDefaultValue());

        assertExactMethods(ProcessorRef.class, "processorClass");
        assertEquals(Void.class, ProcessorRef.class.getDeclaredMethod("processorClass").getDefaultValue());
    }

    @Test
    void shouldKeepCanonicalBaseAnnotationsStable() {
        assertExactMethods(Range.class, "op", "left", "right", "extra", "options");
        assertExactMethods(TextMatch.class, "op", "left", "right", "options");
        assertExactMethods(SubqueryPredicate.class, "mode", "targetEntity", "left", "right", "select", "whereCompare", "where", "options");
        assertExactMethods(Exists.class, "targetEntity", "left", "right", "select", "where", "options");
        assertExactMethods(NotExists.class, "targetEntity", "left", "right", "select", "where", "options");
        assertExactMethods(PredicateOptions.class, "scope", "scopeMode", "groups", "groupsMode", "required", "requiredMode", "not", "notMode", "ignoreNull",
            "ignoreNullMode", "ignoreEmpty", "ignoreEmptyMode", "ignoreBlank", "ignoreBlankMode", "trim", "trimMode");

        assertNoLegacyMethods(Range.class);
        assertNoLegacyMethods(TextMatch.class);
        assertNoLegacyMethods(SubqueryPredicate.class);
        assertNoLegacyMethods(Exists.class);
        assertNoLegacyMethods(NotExists.class);
        assertNotDeprecatedShortcut(Range.class);
        assertNotDeprecatedShortcut(TextMatch.class);
        assertNotDeprecatedShortcut(SubqueryPredicate.class);
        assertNotDeprecatedShortcut(Exists.class);
        assertNotDeprecatedShortcut(NotExists.class);
        assertTrue(!Exists.class.isAnnotationPresent(Selectable.class));
        assertTrue(!NotExists.class.isAnnotationPresent(Selectable.class));
        assertEquals(SubqueryMode.EXISTS, Exists.class.getAnnotation(SubqueryPredicate.class).mode());
        assertEquals(SubqueryMode.NOT_EXISTS, NotExists.class.getAnnotation(SubqueryPredicate.class).mode());
        assertEquals(SubqueryMode.IN, InTable.class.getAnnotation(SubqueryPredicate.class).mode());
    }

    private static void assertNoLegacyMethods(final Class<? extends Annotation> annotationType) {
        Arrays.stream(annotationType.getDeclaredMethods()).filter(method -> LEGACY_METHODS.contains(method.getName())).findAny().ifPresent(method -> {
            throw new AssertionError(annotationType.getSimpleName() + "." + method.getName() + "() should be removed from pure canonical shortcuts");
        });
    }

    @SafeVarargs
    private static void assertDirectCustomMetaAnnotations(final Class<? extends Annotation> annotationType,
        final Class<? extends Annotation>... expectedMetaTypes) {
        List<String> actual = Arrays.stream(annotationType.getAnnotations()).map(Annotation::annotationType)
            .filter(it -> !isJdkMetaAnnotation(it) && !Deprecated.class.equals(it)).map(Class::getSimpleName).sorted().collect(Collectors.toList());
        List<String> expected = new ArrayList<String>();
        Arrays.stream(expectedMetaTypes).map(Class::getSimpleName).forEach(expected::add);
        Collections.sort(expected);
        assertEquals(expected, actual, () -> annotationType.getSimpleName() + " should point directly at canonical meta annotations");
    }

    private static void assertMethod(final Class<? extends Annotation> annotationType, final String methodName) {
        Method method = Arrays.stream(annotationType.getDeclaredMethods()).filter(it -> methodName.equals(it.getName())).findFirst()
            .orElseThrow(() -> new AssertionError(annotationType.getSimpleName() + "." + methodName + "() is missing"));
        assertTrue(null != method, () -> annotationType.getSimpleName() + "." + methodName + "() is missing");
    }

    private static void assertExactMethods(final Class<? extends Annotation> annotationType, final String... expectedMethodNames) {
        List<String> actual = Arrays.stream(annotationType.getDeclaredMethods()).map(Method::getName).sorted().collect(Collectors.toList());
        List<String> expected = new ArrayList<String>(Arrays.asList(expectedMethodNames));
        expected.sort(String::compareTo);
        assertEquals(expected, actual, () -> annotationType.getSimpleName() + " declared methods changed unexpectedly");
        expected.forEach(method -> assertMethod(annotationType, method));
    }

    private static void assertNotDeprecatedShortcut(final Class<? extends Annotation> annotationType) {
        assertTrue(!annotationType.isAnnotationPresent(Deprecated.class), () -> annotationType.getSimpleName() + " should not be deprecated");
        Arrays.stream(annotationType.getDeclaredClasses()).filter(Class::isAnnotation)
            .forEach(innerType -> assertTrue(!innerType.isAnnotationPresent(Deprecated.class),
                () -> annotationType.getSimpleName() + "." + innerType.getSimpleName() + " should not be deprecated"));
    }

    private static void assertDeprecatedShortcut(final Class<? extends Annotation> annotationType) {
        assertTrue(annotationType.isAnnotationPresent(Deprecated.class), () -> annotationType.getSimpleName() + " should be deprecated");
        Arrays.stream(annotationType.getDeclaredClasses()).filter(Class::isAnnotation)
            .forEach(innerType -> assertTrue(!innerType.isAnnotationPresent(Deprecated.class),
                () -> annotationType.getSimpleName() + "." + innerType.getSimpleName() + " should not be deprecated"));
    }

    private static boolean isJdkMetaAnnotation(final Class<? extends Annotation> annotationType) {
        return null != annotationType.getPackage() && "java.lang.annotation".equals(annotationType.getPackage().getName());
    }

    @SuppressWarnings("unused")
    private static final class PureEqualModel {

        @Equal
        private String name;

    }

    private static final class IgnoreCaseModel {

        @IgnoreCaseEqual
        private String name;

        @IgnoreCaseLike
        private String keyword;

    }

}
