package io.github.fsixteen.data.jpa.base.generator.plugins.registry;

import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * 表达式模板接口.
 *
 * <p>
 * 该接口用于按名称暴露一个可复用的 {@link PredicateExpression} 片段,
 * 供 registry/SPI 方式的表达式扩展统一接入.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface PredicateExpressionTemplate {

    /**
     * 创建表达式模板实例.
     *
     * @return 表达式节点
     */
    PredicateExpression create();

}
