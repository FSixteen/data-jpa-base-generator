package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * tuple-value 谓词中的单列映射声明.
 *
 * <p>
 * 当前注解用于把主查询实体上的一个路径 {@link #leftPath()} 绑定到 tuple 数据源中的一个列值.
 * tuple 行既可以是普通对象, 也可以是数组；对象行通过 {@link #itemPath()} 读取,
 * 数组行通过 {@link #itemIndex()} 读取. 运行时值进入 JPA Predicate 前, 可再通过
 * {@link #targetType()} 与 {@link #targetFormat()} 做显式类型对齐.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface TupleColumn {

    /**
     * 主查询实体路径, 支持点路径.
     *
     * @return String
     */
    String leftPath();

    /**
     * tuple 行对象取值路径, 支持点路径.
     *
     * @return String
     */
    String itemPath() default "";

    /**
     * tuple 行数组取值下标.
     *
     * @return int
     */
    int itemIndex() default -1;

    /**
     * tuple 右值目标 Java 类型.
     *
     * @return Class
     */
    Class<?> targetType() default Object.class;

    /**
     * tuple 右值目标格式.
     *
     * @return String
     */
    String targetFormat() default "";

}
