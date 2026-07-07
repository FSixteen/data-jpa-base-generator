package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import io.github.fsixteen.data.jpa.base.generator.plugins.registry.PredicateExpressionTemplate;

/**
 * 表达式模板 SPI 提供器.
 *
 * <p>
 * 该接口用于通过 {@link java.util.ServiceLoader} 暴露命名表达式模板,
 * 供
 * {@link io.github.fsixteen.data.jpa.base.generator.plugins.registry.PredicateExpressionRegistry}
 * 自动注册.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface PredicateExpressionTemplateProvider {

    /**
     * 模板名称.
     *
     * @return String
     */
    String name();

    /**
     * 模板实例.
     *
     * @return PredicateExpressionTemplate
     */
    PredicateExpressionTemplate template();

}
