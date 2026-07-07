package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * `Cases` 命中后执行扩展引用.
 *
 * <p>
 * 该注解用于将 processor 扩展能力从 {@link CaseThen} 的 canonical 比较字段中拆开,
 * 让注解面更明确地区分“标准 canonical 规则”和“显式扩展实现”.
 * 当前主链路中, processor 接管的是最终谓词产出；canonical `then` 规格仍会继续保留,
 * 以便通过上下文暴露给扩展实现.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface ProcessorRef {

    /**
     * 显式 predicate processor 实现类.
     *
     * <p>
     * `Void.class` 表示未配置自定义 processor, 此时 `Cases` 分支优先走 canonical
     * compare 规则；若也没有显式 canonical then 配置, 则编译器会补成默认
     * `field = currentValue` 的 canonical 规格.
     * </p>
     *
     * @return Class
     */
    Class<?> processorClass() default Void.class;

}
