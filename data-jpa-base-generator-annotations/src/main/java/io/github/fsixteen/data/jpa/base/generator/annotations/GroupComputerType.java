package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupComputerType.List;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * 分组计算信息.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
@Target({ TYPE })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
public @interface GroupComputerType {

    /**
     * 当{@code value =
     * io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant.GLOBAL}时,<br>
     * 作用于最后的Predicate条件合并.
     *
     * @return String
     */
    String value() default Constant.DEFAULT;

    /**
     * 计算方式.<br>
     * 
     * @return Type
     */
    Type type() default Type.AND;

    /**
     * 计算方式枚举.<br>
     * 
     * @author FSixteen
     * @since V1.0.0
     */
    public static enum Type {
        /**
         * 并集
         */
        AND,

        /**
         * 或集
         */
        OR;
    }

    /**
     * Defines several {@link GroupComputerType} annotations on the same element.
     *
     * @see GroupComputerType
     */
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * {@link GroupComputerType} 集体.<br>
         * 
         * @return {@link GroupComputerType}[]
         */
        GroupComputerType[] value();

    }

}