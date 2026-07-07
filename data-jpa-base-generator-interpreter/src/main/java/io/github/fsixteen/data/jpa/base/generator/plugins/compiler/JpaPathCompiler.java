package io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

import java.util.Objects;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * JPA 路径编译器.
 *
 * <p>
 * 该工具把点路径字符串统一编译为 JPA {@link Path}/{@link Expression},
 * 是 canonical 路径表达式进入 Criteria API 的唯一入口.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class JpaPathCompiler {

    private JpaPathCompiler() {
    }

    /**
     * 将点路径编译为 JPA {@link Path}.
     *
     * <p>
     * 该方法按 `a.b.c` 的层级顺序逐段调用 `Path#get(...)`,
     * 从而把 DSL 中的统一路径字符串安全映射到 Criteria API 的属性访问链.
     * </p>
     *
     * @param root 当前查询根实体
     * @param path 点路径, 例如 {@code user.name}
     * @return 对应的 JPA Path
     * @throws NullPointerException     当根节点为空时抛出
     * @throws IllegalArgumentException 当路径为 `null`、空串或纯空白串时抛出
     */
    public static Path<?> compile(final Root<?> root, final String path) {
        if (Objects.isNull(root)) {
            throw new NullPointerException("root");
        }
        if (Objects.isNull(path) || path.trim().isEmpty()) {
            throw new IllegalArgumentException("path");
        }
        // 统一用点路径编译器处理字段引用, 后续即便扩展到更复杂的属性路径,
        // 也不需要再回到各个注解插件里重复拆分 path.
        String[] fields = path.split("\\" + Constant.DOT);
        Path<?> temp = root.get(fields[0]);
        for (int index = 1; index < fields.length; index++) {
            temp = temp.get(fields[index]);
        }
        return temp;
    }

    /**
     * 将点路径编译为 JPA {@link Expression}.
     *
     * <p>
     * 当前实现直接复用 {@link #compile(Root, String)},
     * 目的是在语义层面区分“调用方只需要 Path”与“调用方只关心可参与表达式运算的 Expression”.
     * </p>
     *
     * @param root 当前查询根实体
     * @param path 点路径
     * @return 对应的 JPA Expression
     * @throws NullPointerException     当根节点为空时抛出
     * @throws IllegalArgumentException 当路径为 `null`、空串或纯空白串时抛出
     */
    public static Expression<?> compileExpression(final Root<?> root, final String path) {
        return compile(root, path);
    }

}
