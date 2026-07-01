package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * canonical 函数表达式。
 *
 * <p>
 * 该注解用于描述 compiled 主链路中的函数调用节点，统一声明函数名称、返回类型和参数列表。
 * 参数统一通过 {@link ExprArg} 描述，因此函数参数本身也可以是路径、运行时值、固定字面量或嵌套函数结果。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface ExprFunction {

    /**
     * 函数名称。
     *
     * <p>
     * 通常对应 JPA {@code CriteriaBuilder.function(...)} 的函数名，或内建运行时表达式求值器支持的函数名。
     * </p>
     *
     * @return String
     */
    String name() default "";

    /**
     * 返回值类型。
     *
     * <p>
     * 用于声明当前函数结果的目标 Java 类型，供 JPA 函数编译和运行时表达式求值统一使用。
     * </p>
     *
     * @return Class
     */
    Class<?> type() default Object.class;

    /**
     * 函数参数。
     *
     * <p>
     * 参数顺序参与最终函数调用语义。
     * </p>
     *
     * @return ExprArg[]
     */
    ExprArg[] args() default {};

}
