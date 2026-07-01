package io.github.fsixteen.data.jpa.base.generator.plugins.registry;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateOperator;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateOperatorSupport;
import io.github.fsixteen.data.jpa.base.generator.plugins.compiler.JpaExpressionResolver;

/**
 * 注册式谓词模板解析器。
 *
 * <p>
 * 该类型负责把 {@link RegisteredPredicateTemplate} 落成为最终 JPA {@link Predicate}。
 * 与 compiled 注解路径相比，它的输入不是注解规格，而是一份已经预先定义好的模板。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class RegisteredPredicateResolver {

    private RegisteredPredicateResolver() {
    }

    /**
     * 解析一份注册式谓词模板。
     *
     * @param template   谓词模板
     * @param fieldValue 当前字段运行时值
     * @param root       当前查询根实体
     * @param query      当前查询对象
     * @param cb         CriteriaBuilder
     * @return 构建后的谓词
     */
    public static Predicate resolve(final RegisteredPredicateTemplate template, final Object fieldValue, final Root<?> root, final AbstractQuery<?> query,
        final CriteriaBuilder cb) {
        // registry/template 路径最终都会汇聚到同一种表达式求值方式，
        // 差别只在“模板从哪里来”，而不是“怎么落成 Predicate”。
        Expression<?> left = JpaExpressionResolver.resolve(template.getLeft(), fieldValue, root, query, cb);
        Expression<?> right = JpaExpressionResolver.resolve(template.getRight(), fieldValue, root, query, cb);
        PredicateOperator operator = template.getOperator();
        if (PredicateOperator.IN == operator || PredicateOperator.NOT_IN == operator || PredicateOperator.BETWEEN == operator
            || PredicateOperator.NOT_BETWEEN == operator || PredicateOperator.IS_NULL == operator || PredicateOperator.IS_NOT_NULL == operator
            || PredicateOperator.NULL_SWITCH == operator) {
            throw new IllegalArgumentException("Registered template operator is not a binary operator: " + operator);
        }
        return PredicateOperatorSupport.createBinary(operator, left, right, cb);
    }

}
