package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

/**
 * 表达式来源枚举.
 *
 * <p>
 * 该枚举回答“一个表达式的值从哪里来”,
 * 供编译器、JPA 解析器和纯运行时求值器统一分发执行逻辑.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public enum ExpressionSource {

    /**
     * 路径引用.
     *
     * <p>
     * 表示表达式值来自实体路径或根对象路径解析.
     * 在 JPA 执行阶段通常会编译为 `root.get(...)` 一类路径访问.
     * </p>
     */
    PATH,

    /**
     * 运行时字段值, 作为字面量.
     *
     * <p>
     * 表示先从当前请求对象读取字段值, 再把读取结果直接作为比较值或函数参数使用.
     * </p>
     */
    FIELD_VALUE,

    /**
     * 运行时字段值, 作为字段路径.
     *
     * <p>
     * 表示先从当前请求对象读取字段值, 再把该值解释为另一段路径字符串继续解析.
     * </p>
     */
    FIELD_VALUE_PATH,

    /**
     * 注解字面量.
     *
     * <p>
     * 表示表达式值来自注解或模板中直接声明的字符串字面量.
     * </p>
     */
    LITERAL,

    /**
     * 标准函数调用.
     *
     * <p>
     * 表示表达式由内建函数或 DSL 支持的标准函数调用组成.
     * </p>
     */
    FUNCTION,

    /**
     * 自定义表达式提供器.
     *
     * <p>
     * 表示当前表达式需要交由外部 provider 或扩展逻辑解释.
     * </p>
     */
    CUSTOM;
}
