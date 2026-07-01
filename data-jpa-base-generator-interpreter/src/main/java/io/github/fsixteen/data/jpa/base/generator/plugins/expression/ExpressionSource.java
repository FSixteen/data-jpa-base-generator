package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 表达式来源枚举。
 *
 * <p>
 * 该枚举回答“一个表达式的值从哪里来”，
 * 供编译器、JPA 解析器和纯运行时求值器统一分发执行逻辑。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum ExpressionSource {

    /**
     * 路径引用.
     */
    PATH,

    /**
     * 运行时字段值, 作为字面量.
     */
    FIELD_VALUE,

    /**
     * 运行时字段值, 作为字段路径.
     */
    FIELD_VALUE_PATH,

    /**
     * 注解字面量.
     */
    LITERAL,

    /**
     * 标准函数调用.
     */
    FUNCTION,

    /**
     * 自定义表达式提供器.
     */
    CUSTOM;
}
