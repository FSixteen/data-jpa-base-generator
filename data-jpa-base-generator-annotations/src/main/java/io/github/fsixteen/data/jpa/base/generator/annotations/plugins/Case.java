package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * 分支条件(select * from table_name where (xxxxxx)).<br>
 * 用于根据 {@link #value()} 条件, 组装条件.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Documented
@Selectable
public @interface Case {

    /**
     * 执行方式执行器.
     * 
     * @return CaseWhen
     */
    CaseWhen value();

    /**
     * 正则表达式.<br>
     * 存在 {@link #regexp()} 时, 以 {@link #regexp()} 为主, 不存在 {@link #regexp()} 时,
     * 尝试 {@link #testClass()}.<br>
     * 
     * @return 正则表达式
     */
    String regexp() default "";

    /**
     * Predicate的实现类.<br>
     * 
     * @return Class&lt;?&gt;
     */
    Class<?> testClass() default Void.class;

    /**
     * 参与条件计算的字段. 不指定默认为 {@link #field()} 参数字段.<br>
     *
     * @return String
     */
    String testField() default "";

    /**
     * 范围查询分组.<br>
     * 默认同在一组范围查询内.<br>
     * 
     * @return String[]
     */
    String[] scope() default Constant.DEFAULT;

    /**
     * 条件查询分组, 默认独立组<code>@GroupInfo("default", 0)</code>. <br>
     * 当<code>groups</code>值大于<code>1</code>组时, 该条件可以被多条件查询分组复用.
     *
     * @return GroupInfo[]
     */
    GroupInfo[] groups() default { @GroupInfo };

    /**
     * 参与计算的最终字段. 不指定默认为 {@link CaseWhen#field()} 参数字段.<br>
     *
     * @return String
     */
    String field() default "";

}
