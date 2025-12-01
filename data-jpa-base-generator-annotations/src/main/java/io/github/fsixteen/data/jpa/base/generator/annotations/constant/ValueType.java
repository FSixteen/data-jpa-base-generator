package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * 值类型.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public enum ValueType {

    /**
     * 默认值, 根据实际逻辑注解, 其实现有所不同.<br>
     * 
     * <pre>
     * root.&lt;T&gt;get(computerFieldNames[0]).&lt;T&gt;get(computerFieldNames[1])......
     * </pre>
     */
    AUTO,

    /**
     * 字面量, {@link java.lang.String} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) ad.getFieldLiteral())
     * </pre>
     */
    LITERAL,

    /**
     * 字面量, {@link java.lang.Boolean} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) Boolean.valueOf(ad.getFieldLiteral()))
     * </pre>
     */
    LITERAL_BOOLEAN,

    /**
     * 字面量, {@link java.math.BigDecimal} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) new BigDecimal(ad.getFieldLiteral()))
     * </pre>
     */
    LITERAL_BIGDECIMAL,

    /**
     * 字面量, {@link java.lang.Double} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) Double.valueOf(ad.getFieldLiteral()))
     * </pre>
     */
    LITERAL_DOUBLE,

    /**
     * 字面量, {@link java.lang.Float} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) Float.valueOf(ad.getFieldLiteral()))
     * </pre>
     */
    LITERAL_FLOAT,

    /**
     * 字面量, {@link java.lang.Short} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) Short.valueOf(ad.getFieldLiteral()))
     * </pre>
     */
    LITERAL_SHORT,

    /**
     * 字面量, {@link java.lang.Integer} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) Integer.valueOf(ad.getFieldLiteral()))
     * </pre>
     */
    LITERAL_INTEGER,

    /**
     * 字面量, {@link java.lang.Long} 类型.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) Long.valueOf(ad.getFieldLiteral()))
     * </pre>
     */
    LITERAL_LONG,

    /**
     * 字段.<br>
     * 
     * <pre>
     * root.&lt;T&gt;get(Objects.toString(fieldValue))
     * </pre>
     */
    COLUMN,

    /**
     * 字段值.<br>
     * 
     * <pre>
     * cb.&lt;T&gt;literal((T) fieldValue)
     * </pre>
     */
    VALUE,

    /**
     * 函数.<br>
     * 
     * <pre>
     * &#64;Equal(..., valueType = ValueType.FUNCTION,
     *     valueFunction = @Function(
     *         value = "fun_name",
     *         type = Long.class
     *     ),
     * ...)
     * private String userId;
     * 
     * &#64;Equal(..., valueType = ValueType.FUNCTION, 
     *     valueFunction = @Function(
     *         value = "fun_name",
     *         type = Long.class,
     *         args = {@Args(type =FunctionArgsType.LITERAL, value = "the_value")}
     *     ),
     * ...)
     * private String userId;
     * </pre>
     * 
     * <pre>
     * cb.&lt;T&gt;function(function.value(), function.type(), function.args())
     * </pre>
     */
    FUNCTION,

    /**
     * 自定义函数.<br>
     * 
     * @see {@link io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ValueProcessorFunction
     *      ValueProcessorFunction}
     */
    UDFUNCTION;
}
