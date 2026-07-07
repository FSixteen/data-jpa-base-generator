package io.github.fsixteen.data.jpa.base.generator.plugins.expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义表达式占位节点.
 *
 * <p>
 * 当标准路径 / 值 / 字面量 / 函数模型不足以表达某个业务场景时,
 * 可通过该节点记录 provider 名称、目标类型、值基数和扩展元数据,
 * 再交由外部扩展逻辑解释.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CustomExpression implements PredicateExpression {

    private final String provider;

    private final Class<?> javaType;

    private final ExpressionCardinality cardinality;

    private final Map<String, Object> metadata;

    /**
     * 创建一条自定义表达式.
     *
     * @param provider    自定义 provider 名称
     * @param javaType    目标 Java 类型
     * @param cardinality 值基数语义
     * @param metadata    扩展元数据
     */
    private CustomExpression(final String provider, final Class<?> javaType, final ExpressionCardinality cardinality, final Map<String, Object> metadata) {
        this.provider = provider;
        this.javaType = javaType;
        this.cardinality = cardinality;
        this.metadata = Collections.unmodifiableMap(new HashMap<>(metadata));
    }

    /**
     * 创建一条使用默认类型和单值基数的自定义表达式.
     *
     * @param provider 自定义 provider 名称
     * @return 自定义表达式实例
     */
    public static CustomExpression of(final String provider) {
        return new CustomExpression(provider, Object.class, ExpressionCardinality.SINGLE, Collections.<String, Object>emptyMap());
    }

    /**
     * 创建一条完整自定义表达式.
     *
     * @param provider    自定义 provider 名称
     * @param javaType    目标 Java 类型
     * @param cardinality 值基数语义
     * @param metadata    扩展元数据
     * @return 自定义表达式实例
     */
    public static CustomExpression of(final String provider, final Class<?> javaType, final ExpressionCardinality cardinality,
        final Map<String, Object> metadata) {
        return new CustomExpression(provider, javaType, cardinality, metadata);
    }

    /**
     * 返回自定义 provider 名称.
     *
     * @return provider 名称
     */
    public String getProvider() {
        return this.provider;
    }

    /**
     * 返回扩展元数据.
     *
     * @return 不可变元数据映射
     */
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    @Override
    public ExpressionSource getSource() {
        return ExpressionSource.CUSTOM;
    }

    @Override
    public ExpressionCardinality getCardinality() {
        return this.cardinality;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

}
