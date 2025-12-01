package io.github.fsixteen.data.jpa.base.generator.plugins.constant;

/**
 * Comparable type.<br>
 * 由{@code io.github.fsixteen.data.jpa.base.generator.plugins.ComparableBuilderPlugin.ComparableType
 * },{@code io.github.fsixteen.data.jpa.base.generator.plugins.LikeBuilderPlugin.LikeType
 * }和{@code io.github.fsixteen.data.jpa.base.generator.plugins.InBuilderPlugin.InType
 * }合并至此, 原始内容已删除.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public enum ComparableType {
    /**
     * 大于(Greater than).<br>
     * eg: select * from table_name where c1 &gt; '${param}'.<br>
     */
    GT,
    /**
     * 大于等于(Greater than or equal to).<br>
     * eg: select * from table_name where c1 &gt;= '${param}'.<br>
     */
    GTE,
    /**
     * 小于(Less than).<br>
     * eg: select * from table_name where c1 &lt; '${param}'.<br>
     */
    LT,
    /**
     * 小于等于(Less than or equal to).<br>
     * eg: select * from table_name where c1 &lt;= '${param}'.<br>
     */
    LTE,
    /**
     * 等于(Equal to).<br>
     * eg: select * from table_name where c1 = '${param}'.<br>
     */
    EQ,
    /**
     * 开始包含(Begin with).<br>
     * eg: select * from table_name where c1 like '${param}%'.<br>
     */
    LEFT,
    /**
     * 结尾包含(End with).<br>
     * eg: select * from table_name where c1 like '%${param}'.<br>
     */
    RIGHT,
    /**
     * 包含(Contains).<br>
     * eg: select * from table_name where c1 like '%${param}%'.<br>
     */
    CONTAINS,
    /**
     * 开始包含(Begin with).<br>
     * eg: select * from table_name where c1 like '${param}%'.<br>
     */
    START_WITH,
    /**
     * 结尾包含(End with).<br>
     * eg: select * from table_name where c1 like '%${param}'.<br>
     */
    END_WITH,
    /**
     * 包含(In).<br>
     * eg: select * from table_name where c1 in (${param}).<br>
     */
    IN,
    /**
     * 不包含(Not in).<br>
     * eg: select * from table_name where c1 not in (${param}).<br>
     */
    NOT_IN,
    /**
     * 分割后包含(Split and in).<br>
     * eg: select * from table_name where c1 in (${param.split}).<br>
     */
    SPLIT_IN,
    /**
     * 分割后不包含(Split and not in).<br>
     * eg: select * from table_name where c1 not in (${param.split}).<br>
     */
    SPLIT_NOT_IN,

    /**
     * 为NULL(Is Null).<br>
     * eg: select * from table_name where c1 is null.<br>
     */
    IS_NULL,

    /**
     * 不为NULL(Is Not Null).<br>
     * eg: select * from table_name where c1 is not null.<br>
     */
    IS_NOT_NULL;
}