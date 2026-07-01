package io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

import java.util.Objects;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * JPA 路径编译器。
 *
 * <p>
 * 该工具把点路径字符串统一编译为 JPA {@link Path}/{@link Expression}，
 * 是 canonical 路径表达式进入 Criteria API 的唯一入口。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class JpaPathCompiler {

    private JpaPathCompiler() {
    }

    /**
     * 将点路径编译为 JPA {@link Path}。
     *
     * @param root 当前查询根实体
     * @param path 点路径，例如 {@code user.name}
     * @return 对应的 JPA Path
     */
    public static Path<?> compile(final Root<?> root, final String path) {
        if (Objects.isNull(root)) {
            throw new NullPointerException("root");
        }
        if (Objects.isNull(path) || path.trim().isEmpty()) {
            throw new IllegalArgumentException("path");
        }
        // 统一用点路径编译器处理字段引用，后续即便扩展到更复杂的属性路径，
        // 也不需要再回到各个注解插件里重复拆分 path。
        String[] fields = path.split("\\" + Constant.DOT);
        Path<?> temp = root.get(fields[0]);
        for (int index = 1; index < fields.length; index++) {
            temp = temp.get(fields[index]);
        }
        return temp;
    }

    /**
     * 将点路径编译为 JPA {@link Expression}。
     *
     * @param root 当前查询根实体
     * @param path 点路径
     * @return 对应的 JPA Expression
     */
    public static Expression<?> compileExpression(final Root<?> root, final String path) {
        return compile(root, path);
    }

}
