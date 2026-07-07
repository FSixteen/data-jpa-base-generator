package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

import java.util.Objects;

/**
 * 路径表达式.
 *
 * <p>
 * 该节点表示一个实体属性路径或运行时路径引用,
 * 例如 {@code user.name} 或 {@code audit.createdAt}.
 * 在 JPA 场景下会由 {@code JpaPathCompiler} 落成对应的 {@code root.get(...)} 调用链.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class PathExpression implements PredicateExpression {

    private final String path;

    /**
     * 创建一条路径表达式.
     *
     * @param path 实体属性路径或动态路径字符串
     */
    private PathExpression(final String path) {
        this.path = path;
    }

    /**
     * 基于路径字符串创建一条路径表达式.
     *
     * @param path 实体属性路径或动态路径字符串
     * @return 路径表达式实例
     */
    public static PathExpression of(final String path) {
        return new PathExpression(path);
    }

    /**
     * 返回当前表达式持有的原始路径字符串.
     *
     * @return 原始路径字符串
     */
    public String getPath() {
        return this.path;
    }

    @Override
    public ExpressionSource getSource() {
        return ExpressionSource.PATH;
    }

    @Override
    public ExpressionCardinality getCardinality() {
        return ExpressionCardinality.SINGLE;
    }

    @Override
    public Class<?> getJavaType() {
        return Object.class;
    }

    /**
     * 判断当前路径表达式是否具备可解析的有效路径.
     *
     * @return 路径非空且去除首尾空白后仍包含内容时返回 {@code true}
     */
    public boolean isValid() {
        return Objects.nonNull(this.path) && !this.path.trim().isEmpty();
    }

}
