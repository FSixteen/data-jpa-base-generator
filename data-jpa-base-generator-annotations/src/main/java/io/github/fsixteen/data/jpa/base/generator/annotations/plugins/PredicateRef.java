package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * `Cases` 命中条件扩展引用.
 *
 * <p>
 * 该注解用于将自定义条件扩展从 {@link CaseWhen} 的 canonical 比较字段中拆开,
 * 让注解面更明确地区分“标准 canonical 规则”和“显式扩展实现”.
 * 当前主链路中, canonical `when` 规则优先级更高；本注解只作为未声明 canonical `when`
 * 时的显式扩展回退入口.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface PredicateRef {

    /**
     * 显式条件实现类.
     *
     * <p>
     * `Void.class` 表示未配置扩展, 当前分支条件完全由 canonical `op/left/right/extra`
     * 或调用方默认规则决定.
     * </p>
     *
     * @return Class
     */
    Class<?> predicateClass() default Void.class;

}
