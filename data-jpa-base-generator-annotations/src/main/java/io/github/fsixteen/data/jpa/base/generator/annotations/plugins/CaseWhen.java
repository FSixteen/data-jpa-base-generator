package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Predicate;

import io.github.fsixteen.data.jpa.base.generator.annotations.Selectable;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultPredicate;

/**
 * TODO :: 规划中.<br>
 * 分支条件(select * from table_name where (xxxxxx)).<br>
 * 用于根据 {@link Cases} 条件, 组装条件.<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
@Selectable
public @interface CaseWhen {

    /**
     * {@linkplain java.util.function.Predicate Predicate} 的实现类, 传入当前请求参数实例.<br>
     * 
     * <p>
     * 当存在 {@link #testClassName()} 时, 以 {@link #testClassName()} 计算, 放弃
     * {@link #testClass()}.<br>
     * </p>
     *
     * <p>
     * 当不存在 {@link #testClassName()} 时, 以 {@link #testClass()} 计算, 放弃
     * {@link #testClassName()}.<br>
     * </p>
     * 
     * @return Class&lt;? extends java.util.function.Predicate&gt;
     */
    Class<? extends Predicate<Object>> testClass() default DefaultPredicate.class;

    /**
     * {@linkplain java.util.function.Predicate Predicate} 的实现类, 传入当前请求参数实例.<br>
     * 
     * <p>
     * 当存在 {@link #testClassName()} 时, 以 {@link #testClassName()} 计算, 放弃
     * {@link #testClass()}.<br>
     * </p>
     *
     * <p>
     * 当不存在 {@link #testClassName()} 时, 以 {@link #testClass()} 计算, 放弃
     * {@link #testClassName()}.<br>
     * </p>
     * 
     * @return Class&lt;? extends java.util.function.Predicate&gt;
     */
    String testClassName() default "";

}
