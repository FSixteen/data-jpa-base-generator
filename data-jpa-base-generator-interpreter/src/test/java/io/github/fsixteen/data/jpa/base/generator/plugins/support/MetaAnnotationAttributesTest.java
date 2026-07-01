package io.github.fsixteen.data.jpa.base.generator.plugins.support;

import static java.lang.annotation.ElementType.FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.Existed;
import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.SubqueryMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CollectionPolicy;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Exists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Membership;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NotExists;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NullCheck;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Range;

public class MetaAnnotationAttributesTest {

    @Test
    public void shouldReadCanonicalFieldsFromSelectableMetaAnnotation() throws Exception {
        Field field = Holder.class.getDeclaredField("status");
        MetaSelectableLiteral annotation = field.getAnnotation(MetaSelectableLiteral.class);
        Expr left = MetaAnnotationAttributes.fieldValue(annotation, "left", Expr.class);
        PredicateOptions options = MetaAnnotationAttributes.fieldValue(annotation, "options", PredicateOptions.class);

        assertNotNull(left);
        assertEquals(ExprType.PATH, left.type());
        assertEquals("meta.status", left.path());
        assertNotNull(options);
        assertEquals("meta-scope", options.scope()[0]);
    }

    @Test
    public void shouldReadExtraAndDefaultOptionsFromExistedMetaAnnotation() throws Exception {
        Field field = Holder.class.getDeclaredField("existedStatus");
        MetaExistedLiteral annotation = field.getAnnotation(MetaExistedLiteral.class);
        Expr[] extra = MetaAnnotationAttributes.fieldValue(annotation, "extra", Expr[].class);
        PredicateOptions options = MetaAnnotationAttributes.fieldValue(annotation, "options", PredicateOptions.class);

        assertNotNull(extra);
        assertEquals(1, extra.length);
        assertEquals("meta.end", extra[0].path());
        assertNotNull(options);
        assertEquals(0, options.scope().length);
        assertEquals(0, options.groups().length);
    }

    @Test
    public void shouldReadCanonicalFieldsFromSpecificCompareFamilies() throws Exception {
        Field rangeField = Holder.class.getDeclaredField("rangeStatus");
        MetaRangeLiteral rangeAnnotation = rangeField.getAnnotation(MetaRangeLiteral.class);
        Expr rangeLeft = MetaAnnotationAttributes.fieldValue(rangeAnnotation, "left", Expr.class);
        CompareOp rangeOp = MetaAnnotationAttributes.fieldValue(rangeAnnotation, "op", CompareOp.class);

        Field membershipField = Holder.class.getDeclaredField("membershipStatus");
        MetaMembershipLiteral membershipAnnotation = membershipField.getAnnotation(MetaMembershipLiteral.class);
        Expr membershipRight = MetaAnnotationAttributes.fieldValue(membershipAnnotation, "right", Expr.class);
        CollectionPolicy collection = MetaAnnotationAttributes.fieldValue(membershipAnnotation, "collection", CollectionPolicy.class);

        Field nullCheckField = Holder.class.getDeclaredField("nullStatus");
        MetaNullCheckLiteral nullCheckAnnotation = nullCheckField.getAnnotation(MetaNullCheckLiteral.class);
        Expr nullLeft = MetaAnnotationAttributes.fieldValue(nullCheckAnnotation, "left", Expr.class);
        CompareOp nullOp = MetaAnnotationAttributes.fieldValue(nullCheckAnnotation, "op", CompareOp.class);

        assertEquals("meta.range.start", rangeLeft.path());
        assertEquals(CompareOp.NOT_BETWEEN, rangeOp);
        assertEquals("meta.codes", membershipRight.path());
        assertTrue(collection.split());
        assertEquals("meta.deletedAt", nullLeft.path());
        assertEquals(CompareOp.IS_NOT_NULL, nullOp);
    }

    @Test
    public void shouldResolveSelectableAndExistedRolesFromPredicateRoleMarker() {
        assertEquals(true, MetaAnnotationAttributes.isSelectionAnnotation(Selectable.class));
        assertEquals(false, MetaAnnotationAttributes.isSelectionAnnotation(Existed.class));
        assertEquals(true, MetaAnnotationAttributes.isSelectionAnnotation(Exists.class));
        assertEquals(true, MetaAnnotationAttributes.isSelectionAnnotation(NotExists.class));
        assertEquals(true, MetaAnnotationAttributes.isExistenceAnnotation(Existed.class));
        assertEquals(true, MetaAnnotationAttributes.isSelectionAnnotation(SelectionRoleLiteral.class));
        assertEquals(true, MetaAnnotationAttributes.isExistenceAnnotation(ExistenceRoleLiteral.class));
        assertEquals(true, Selectable.class.isAnnotationPresent(PredicateRole.class));
        assertEquals(true, Existed.class.isAnnotationPresent(PredicateRole.class));
    }

    @Test
    public void shouldResolveCompiledPredicateRoleSpecFromMetaAnnotationTree() {
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(Selectable.class).isSelection());
        assertTrue(!MetaAnnotationAttributes.resolvePredicateRole(Selectable.class).isExistence());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(Existed.class).isExistence());
        assertTrue(!MetaAnnotationAttributes.resolvePredicateRole(Existed.class).isSelection());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(DualRoleLiteral.class).isSelection());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(DualRoleLiteral.class).isExistence());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(SelectionRoleLiteral.class).isSelection());
        assertTrue(!MetaAnnotationAttributes.resolvePredicateRole(SelectionRoleLiteral.class).isExistence());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(ExistenceRoleLiteral.class).isExistence());
        assertTrue(!MetaAnnotationAttributes.resolvePredicateRole(ExistenceRoleLiteral.class).isSelection());
    }

    @Test
    public void shouldTreatLegacyPredicateRoleFlagsAsAliasesOfSemanticRoleFields() {
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(DualRoleLiteral.class).isSelection());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(DualRoleLiteral.class).isExistence());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(LegacySelectionRoleLiteral.class).isSelection());
        assertTrue(!MetaAnnotationAttributes.resolvePredicateRole(LegacySelectionRoleLiteral.class).isExistence());
        assertTrue(MetaAnnotationAttributes.resolvePredicateRole(LegacyExistenceRoleLiteral.class).isExistence());
        assertTrue(!MetaAnnotationAttributes.resolvePredicateRole(LegacyExistenceRoleLiteral.class).isSelection());
    }

    @Test
    public void shouldReadSubqueryModeFromShortcutMetaAnnotations() throws Exception {
        assertEquals(SubqueryMode.EXISTS,
            MetaAnnotationAttributes.fieldValue(ExistsHolder.class.getDeclaredField("exists").getAnnotation(Exists.class), "mode", SubqueryMode.class));
        assertEquals(SubqueryMode.NOT_EXISTS,
            MetaAnnotationAttributes.fieldValue(ExistsHolder.class.getDeclaredField("notExists").getAnnotation(NotExists.class), "mode", SubqueryMode.class));
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @Selectable(left = @Expr(type = ExprType.PATH, path = "meta.status"), options = @PredicateOptions(scope = { "meta-scope" }))
    private @interface MetaSelectableLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @Existed(extra = { @Expr(type = ExprType.PATH, path = "meta.end") })
    private @interface MetaExistedLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @Range(op = CompareOp.NOT_BETWEEN, left = @Expr(type = ExprType.PATH, path = "meta.range.start"))
    private @interface MetaRangeLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @Membership(collection = @CollectionPolicy(split = true), right = @Expr(type = ExprType.PATH, path = "meta.codes"))
    private @interface MetaMembershipLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @NullCheck(op = CompareOp.IS_NOT_NULL, left = @Expr(type = ExprType.PATH, path = "meta.deletedAt"))
    private @interface MetaNullCheckLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @PredicateRole(selectable = true, existed = true)
    private @interface DualRoleLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @PredicateRole(selection = true)
    private @interface SelectionRoleLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @PredicateRole(existence = true)
    private @interface ExistenceRoleLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @PredicateRole(selectable = true)
    private @interface LegacySelectionRoleLiteral {
    }

    @Target({ FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @PredicateRole(existed = true)
    private @interface LegacyExistenceRoleLiteral {
    }

    private static final class Holder {

        @MetaSelectableLiteral
        private String status;

        @MetaExistedLiteral
        private String existedStatus;

        @MetaRangeLiteral
        private String rangeStatus;

        @MetaMembershipLiteral
        private String membershipStatus;

        @MetaNullCheckLiteral
        private String nullStatus;

        @SelectionRoleLiteral
        private String selectionOnlyStatus;

        @ExistenceRoleLiteral
        private String existenceOnlyStatus;

        @LegacySelectionRoleLiteral
        private String legacySelectionOnlyStatus;

        @LegacyExistenceRoleLiteral
        private String legacyExistenceOnlyStatus;

    }

    private static final class ExistsHolder {

        @Exists(targetEntity = Holder.class)
        private Boolean exists;

        @NotExists(targetEntity = Holder.class)
        private Boolean notExists;

    }

}
