package io.github.fsixteen.data.jpa.base.generator.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * canonical provider 扩展元注解。
 *
 * <p>
 * 该注解只服务于“注解类型 -> compiled provider”的绑定关系，因此只允许标注在
 * annotation type 上。运行时 registry 会统一读取 {@link #provider()} 并实例化
 * {@code io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProvider}。
 * </p>
 *
 * <p>
 * 推荐用法：
 * </p>
 *
 * <pre>
 * &#64;Selectable
 * &#64;Constraint(provider = &#64;ProviderRef(providerClass = XxxCompiledPredicateProvider.class))
 * public &#64;interface CustomSelectable {
 * }
 * </pre>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@Documented
@Target({ ANNOTATION_TYPE })
@Retention(RUNTIME)
@Inherited
public @interface Constraint {

    /**
     * compiled predicate provider 的结构化引用。
     *
     * <p>
     * 推荐实现
     * {@code io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProvider}。
     * </p>
     *
     * @return ProviderRef
     */
    ProviderRef provider() default @ProviderRef();

}
