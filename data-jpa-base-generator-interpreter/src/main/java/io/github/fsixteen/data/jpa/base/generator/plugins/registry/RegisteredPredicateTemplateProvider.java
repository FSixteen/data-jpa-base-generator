package io.github.fsixteen.data.jpa.base.generator.plugins.registry;

/**
 * 注册式谓词模板提供器。
 *
 * <p>
 * 该接口用于按名称暴露一整套可复用的谓词模板，
 * 模板中通常已经固定了左右表达式和操作符，
 * 运行期只需补齐字段值和 JPA 上下文即可解析。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface RegisteredPredicateTemplateProvider {

    /**
     * 创建一份注册式谓词模板。
     *
     * @return 谓词模板
     */
    RegisteredPredicateTemplate create();

}
