package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NullCheck.List;

/**
 * canonical 空值判断注解.
 *
 * <p>
 * 该注解是 compiled 主链路中的统一空值判断输入模型, 用于表达 is-null / is-not-null 语义下的
 * 被判断表达式与公共选项. 零配置时默认针对当前注解绑定字段对应的实体路径做空值判断.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@PredicateRole(selection = true)
@Compare(op = CompareOp.IS_NULL)
@Inherited
public @interface NullCheck {

    /**
     * 空值判断操作符, 仅允许 is-null / is-not-null 语义.
     *
     * @return CompareOp
     */
    CompareOp op() default CompareOp.IS_NULL;

    /**
     * 被判断的 canonical 表达式.
     *
     * <p>
     * 该表达式可表示实体路径、运行时值、固定字面量或函数结果.
     * 未显式配置时, 默认回退为当前注解绑定字段对应的实体路径.
     * </p>
     *
     * @return Expr
     */
    Expr left() default @Expr(type = ExprType.PATH);

    /**
     * 当前空值判断谓词的公共选项.
     *
     * @return PredicateOptions
     */
    PredicateOptions options() default @PredicateOptions();

    /**
     * Defines several {@link NullCheck} annotations on the same element.
     *
     * @see NullCheck
     */
    @Target({ FIELD, METHOD })
    @Retention(RUNTIME)
    @Documented
    @Inherited
    @interface List {

        NullCheck[] value();

    }

}
