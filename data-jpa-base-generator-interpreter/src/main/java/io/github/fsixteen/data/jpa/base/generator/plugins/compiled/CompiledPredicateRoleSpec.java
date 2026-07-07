package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

/**
 * 编译后谓词规格的参与角色.
 *
 * <p>
 * selection / existence 条件在编译结果内部统一沉淀为一个小值对象, 避免主链路继续散落
 * {@code selection/existence} 两个布尔值.
 * </p>
 *
 * <p>
 * 该对象用于表达“同一个注解是否参与普通查询构建、是否参与 exists 判断构建”,
 * 从而支持像 `@Selectable`、`@Existed` 这样的角色型元注解统一落到 compiled 主链路.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CompiledPredicateRoleSpec {

    private final boolean selection;

    private final boolean existence;

    private CompiledPredicateRoleSpec(final boolean selection, final boolean existence) {
        this.selection = selection;
        this.existence = existence;
    }

    /**
     * 创建一份 selection / existence 角色组合.
     */
    public static CompiledPredicateRoleSpec of(final boolean selection, final boolean existence) {
        return new CompiledPredicateRoleSpec(selection, existence);
    }

    private boolean participatesInSelection() {
        return this.selection;
    }

    private boolean participatesInExistence() {
        return this.existence;
    }

    /**
     * 判断当前规格是否参与普通查询构建.
     */
    public boolean isSelection() {
        return this.participatesInSelection();
    }

    /**
     * 判断当前规格是否参与 existence 构建.
     */
    public boolean isExistence() {
        return this.participatesInExistence();
    }

    /**
     * 判断当前角色是否匹配指定构建目标.
     */
    public boolean matches(final PredicateBuildTarget target) {
        if (null == target) {
            return this.isSelection();
        }
        return target.isExistence() ? this.isExistence() : this.isSelection();
    }

}
