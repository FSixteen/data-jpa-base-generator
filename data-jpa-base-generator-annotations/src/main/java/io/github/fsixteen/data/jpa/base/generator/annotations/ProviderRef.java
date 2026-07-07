package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * `@Constraint` provider 结构化引用.
 *
 * <p>
 * 注解层只负责声明 provider 类型或类名；真正的实例化与类型校验由 interpreter 的 compiled
 * registry 统一完成.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface ProviderRef {

    Class<?> providerClass() default Void.class;

}
