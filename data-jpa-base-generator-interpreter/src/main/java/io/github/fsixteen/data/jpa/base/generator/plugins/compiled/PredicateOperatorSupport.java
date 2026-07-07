package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;

/**
 * 统一的谓词操作符辅助类.
 *
 * <p>
 * built-in compiled 注解链路、注册式模板链路以及未来其他扩展链路都应尽量复用这里的运算符映射与
 * JPA 谓词构造逻辑, 避免同一批比较语义散落在多个 switch/if 分支中.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class PredicateOperatorSupport {

    private PredicateOperatorSupport() {
    }

    /**
     * 将统一比较类别映射为执行层操作符.
     *
     * @param type 统一比较类别
     * @return 对应的执行层操作符；输入为空时回退为 {@link PredicateOperator#EQ}
     */
    public static PredicateOperator fromComparableType(final ComparableType type) {
        return null == type ? PredicateOperator.EQ : type.getOperator();
    }

    /**
     * 将执行层操作符反向映射为统一比较类别.
     *
     * @param operator 执行层操作符
     * @return 对应的统一比较类别；未命中特殊分支时回退为 {@link ComparableType#EQ}
     */
    public static ComparableType toComparableType(final PredicateOperator operator) {
        if (PredicateOperator.NEQ == operator) {
            return ComparableType.NEQ;
        }
        if (PredicateOperator.GT == operator) {
            return ComparableType.GT;
        }
        if (PredicateOperator.GTE == operator) {
            return ComparableType.GTE;
        }
        if (PredicateOperator.LT == operator) {
            return ComparableType.LT;
        }
        if (PredicateOperator.LTE == operator) {
            return ComparableType.LTE;
        }
        if (PredicateOperator.LIKE_CONTAINS == operator) {
            return ComparableType.CONTAINS;
        }
        if (PredicateOperator.LIKE_NOT_CONTAINS == operator) {
            return ComparableType.NOT_CONTAINS;
        }
        if (PredicateOperator.LIKE_STARTS_WITH == operator) {
            return ComparableType.START_WITH;
        }
        if (PredicateOperator.LIKE_ENDS_WITH == operator) {
            return ComparableType.END_WITH;
        }
        if (PredicateOperator.IN == operator) {
            return ComparableType.IN;
        }
        if (PredicateOperator.NOT_IN == operator) {
            return ComparableType.NOT_IN;
        }
        if (PredicateOperator.BETWEEN == operator) {
            return ComparableType.BETWEEN;
        }
        if (PredicateOperator.NOT_BETWEEN == operator) {
            return ComparableType.NOT_BETWEEN;
        }
        if (PredicateOperator.IS_NULL == operator) {
            return ComparableType.IS_NULL;
        }
        if (PredicateOperator.IS_NOT_NULL == operator) {
            return ComparableType.IS_NOT_NULL;
        }
        return ComparableType.EQ;
    }

    /**
     * 按统一操作符创建二元 JPA 谓词.
     *
     * @param operator 二元操作符
     * @param left     左表达式
     * @param right    右表达式
     * @param cb       Criteria 构造器
     * @return 构建完成的二元谓词
     * @throws IllegalArgumentException 当操作符不属于二元比较语义
     */
    public static Predicate createBinary(final PredicateOperator operator, final Expression<?> left, final Expression<?> right, final CriteriaBuilder cb) {
        switch (operator) {
            case GT:
                return compare(cb, ComparisonKind.GT, left, right);
            case GTE:
                return compare(cb, ComparisonKind.GTE, left, right);
            case LT:
                return compare(cb, ComparisonKind.LT, left, right);
            case LTE:
                return compare(cb, ComparisonKind.LTE, left, right);
            case NEQ:
                return cb.notEqual(left, right);
            case LIKE_STARTS_WITH:
                return cb.like(asString(left), cb.concat(asString(right), "%"));
            case LIKE_ENDS_WITH:
                return cb.like(asString(left), cb.concat("%", asString(right)));
            case LIKE_NOT_CONTAINS:
                return cb.notLike(asString(left), cb.concat("%", cb.concat(asString(right), "%")));
            case LIKE_CONTAINS:
                return cb.like(asString(left), cb.concat("%", cb.concat(asString(right), "%")));
            case EQ:
            default:
                return cb.equal(left, right);
        }
    }

    /**
     * 在比较语义已经收敛到大小关系的前提下, 受控调用 CriteriaBuilder 的可比较 API.
     *
     * @param cb    Criteria 构造器
     * @param kind  比较方向
     * @param left  左表达式
     * @param right 右表达式
     * @param <Y>   可比较类型
     * @return 构建完成的大小比较谓词
     */
    @SuppressWarnings({ "unchecked" })
    private static <Y extends Comparable<? super Y>> Predicate compare(final CriteriaBuilder cb, final ComparisonKind kind, final Expression<?> left,
        final Expression<?> right) {
        // CriteriaBuilder 的比较 API 强依赖 Comparable 泛型；而 DSL 在运行前并不知道确切的 Y, 只能在这里集中桥接.
        Expression<? extends Y> leftComparable = (Expression<? extends Y>) left;
        Expression<? extends Y> rightComparable = (Expression<? extends Y>) right;
        switch (kind) {
            case GT:
                return cb.greaterThan(leftComparable, rightComparable);
            case GTE:
                return cb.greaterThanOrEqualTo(leftComparable, rightComparable);
            case LT:
                return cb.lessThan(leftComparable, rightComparable);
            case LTE:
            default:
                return cb.lessThanOrEqualTo(leftComparable, rightComparable);
        }
    }

    /**
     * 将任意表达式受控收窄为字符串表达式.
     *
     * @param expression 原始表达式
     * @return 收窄后的字符串表达式
     * @throws ClassCastException 当调用链错误地把非字符串表达式送入 like/concat 语义时抛出
     */
    @SuppressWarnings("unchecked")
    private static Expression<String> asString(final Expression<?> expression) {
        // like/concat 族操作最终都要求 String Expression, 这里假定调用方已经把语义限制在字符串运算上.
        return (Expression<String>) expression;
    }

    private enum ComparisonKind {
        GT, GTE, LT, LTE
    }

}
