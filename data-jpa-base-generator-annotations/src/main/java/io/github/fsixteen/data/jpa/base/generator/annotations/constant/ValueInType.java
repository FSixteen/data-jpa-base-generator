package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * IN时, 值参数计算方向.<br>
 *
 * @author FSixteen
 * @since V1.0.0
 */
public enum ValueInType {
    /**
     * select * from table_name1 as t1 where t1.column_name in ( select column_name
     * from table_name2 )
     * and t1.column_name = 'xxx';
     */
    SOURCE,

    /**
     * select * from table_name1 as t1 where t1.column_name in ( select column_name
     * from table_name2
     * as t2 where t2.column_name = 'xxx' );
     */
    TARGET;
}
