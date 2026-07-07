package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * canonical 表达式类型.
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum ExprType {

    /**
     * 未指定, 交给编译器按上下文回退默认规则.
     */
    AUTO,

    /**
     * 固定路径引用, 例如 {@code root.get("user").get("name")}.
     */
    PATH,

    /**
     * 运行时字段值, 作为 literal 使用.
     */
    VALUE,

    /**
     * 运行时字段值, 作为路径字符串使用.
     */
    VALUE_PATH,

    /**
     * 固定字面量.
     */
    LITERAL,

    /**
     * 标准函数表达式.
     */
    FUNCTION
}
