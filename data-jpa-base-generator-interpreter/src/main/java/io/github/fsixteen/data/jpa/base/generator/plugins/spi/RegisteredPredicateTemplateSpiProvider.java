package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import io.github.fsixteen.data.jpa.base.generator.plugins.registry.RegisteredPredicateTemplateProvider;

/**
 * 注册式谓词模板 SPI 提供器.
 *
 * <p>
 * 该接口用于通过 {@link java.util.ServiceLoader} 暴露命名的完整谓词模板,
 * 供
 * {@link io.github.fsixteen.data.jpa.base.generator.plugins.registry.RegisteredPredicateTemplateRegistry}
 * 自动注册.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface RegisteredPredicateTemplateSpiProvider extends RegisteredPredicateTemplateProvider {

    /**
     * 模板名称.
     *
     * @return String
     */
    String name();

}
