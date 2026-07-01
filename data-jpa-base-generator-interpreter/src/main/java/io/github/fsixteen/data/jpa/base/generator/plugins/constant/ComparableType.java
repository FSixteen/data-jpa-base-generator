package io.github.fsixteen.data.jpa.base.generator.plugins.constant;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.PredicateOperator;

/**
 * 历史比较语义枚举。
 *
 * <p>
 * 该枚举统一承载比较、like、in、between 和 null-check 相关的历史语义分类，
 * 供 compiled 主链路内部继续复用。
 * 它比公开注解层的 {@code CompareOp} 更贴近运行时分类，但仍保留了历史命名习惯。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public enum ComparableType {

    /**
     * 大于(Greater than).<br>
     * eg: select * from table_name where c1 &gt; '${param}'.<br>
     */
    GT(PredicateOperator.GT),

    /**
     * 大于等于(Greater than or equal to).<br>
     * eg: select * from table_name where c1 &gt;= '${param}'.<br>
     */
    GTE(PredicateOperator.GTE),

    /**
     * 小于(Less than).<br>
     * eg: select * from table_name where c1 &lt; '${param}'.<br>
     */
    LT(PredicateOperator.LT),

    /**
     * 小于等于(Less than or equal to).<br>
     * eg: select * from table_name where c1 &lt;= '${param}'.<br>
     */
    LTE(PredicateOperator.LTE),

    /**
     * 等于(Equal to).<br>
     * eg: select * from table_name where c1 = '${param}'.<br>
     */
    EQ(PredicateOperator.EQ),

    /**
     * 不等于(Not Equal to).<br>
     * eg: select * from table_name where c1 <> '${param}'.<br>
     */
    NEQ(PredicateOperator.NEQ),

    /**
     * 开始包含(Begin with).<br>
     * eg: select * from table_name where c1 like '${param}%'.<br>
     */
    LEFT(PredicateOperator.LIKE_STARTS_WITH),

    /**
     * 结尾包含(End with).<br>
     * eg: select * from table_name where c1 like '%${param}'.<br>
     */
    RIGHT(PredicateOperator.LIKE_ENDS_WITH),

    /**
     * 包含(Contains).<br>
     * eg: select * from table_name where c1 like '%${param}%'.<br>
     */
    CONTAINS(PredicateOperator.LIKE_CONTAINS),

    /**
     * 不包含(Not Contains).<br>
     * eg: select * from table_name where c1 not like '%${param}%'.<br>
     */
    NOT_CONTAINS(PredicateOperator.LIKE_NOT_CONTAINS),

    /**
     * 开始包含(Begin with).<br>
     * eg: select * from table_name where c1 like '${param}%'.<br>
     */
    START_WITH(PredicateOperator.LIKE_STARTS_WITH),

    /**
     * 结尾包含(End with).<br>
     * eg: select * from table_name where c1 like '%${param}'.<br>
     */
    END_WITH(PredicateOperator.LIKE_ENDS_WITH),

    /**
     * 包含(In).<br>
     * eg: select * from table_name where c1 in (${param}).<br>
     */
    IN(PredicateOperator.IN),

    /**
     * 不包含(Not in).<br>
     * eg: select * from table_name where c1 not in (${param}).<br>
     */
    NOT_IN(PredicateOperator.NOT_IN),

    /**
     * 分割后包含(Split and in).<br>
     * eg: select * from table_name where c1 in (${param.split}).<br>
     */
    SPLIT_IN(PredicateOperator.IN),

    /**
     * 分割后不包含(Split and not in).<br>
     * eg: select * from table_name where c1 not in (${param.split}).<br>
     */
    SPLIT_NOT_IN(PredicateOperator.NOT_IN),

    /**
     * 为NULL(Is Null).<br>
     * eg: select * from table_name where c1 is null.<br>
     */
    IS_NULL(PredicateOperator.IS_NULL),

    /**
     * 不为NULL(Is Not Null).<br>
     * eg: select * from table_name where c1 is not null.<br>
     */
    IS_NOT_NULL(PredicateOperator.IS_NOT_NULL),

    /**
     * 介于两者之间(Between x And y).<br>
     * eg: select * from table_name where c1 between x and y.<br>
     */
    BETWEEN(PredicateOperator.BETWEEN),

    /**
     * 不介于两者之间(Not Between x And y).<br>
     * eg: select * from table_name where c1 not between x and y.<br>
     */
    NOT_BETWEEN(PredicateOperator.NOT_BETWEEN);

    private final PredicateOperator operator;

    ComparableType(final PredicateOperator operator) {
        this.operator = operator;
    }

    public PredicateOperator getOperator() {
        return this.operator;
    }

    public boolean isRange() {
        return BETWEEN == this || NOT_BETWEEN == this;
    }

    public boolean isCollection() {
        return IN == this || NOT_IN == this || SPLIT_IN == this || SPLIT_NOT_IN == this;
    }

    public boolean isNullCheck() {
        return IS_NULL == this || IS_NOT_NULL == this;
    }

    public boolean isLike() {
        return LEFT == this || RIGHT == this || CONTAINS == this || NOT_CONTAINS == this || START_WITH == this || END_WITH == this;
    }

    public boolean isNegated() {
        return NEQ == this || NOT_CONTAINS == this || NOT_IN == this || SPLIT_NOT_IN == this || NOT_BETWEEN == this;
    }

}
