package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

/**
 * IN时, 值参数计算方向.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
public enum ValueInType {
    /**
     * <pre>
     * SELECT * FROM table_name1 AS t1
     *   WHERE
     *     t1.column_name IN ( SELECT column_name FROM table_name2 )
     *     AND t1.column_name = 'xxx';
     * </pre>
     */
    SOURCE,

    /**
     * <pre>
     * SELECT * FROM table_name1 AS t1
     * WHERE
     *   t1.column_name IN (
     *     SELECT column_name FROM table_name2 AS t2 WHERE t2.column_name = 'xxx'
     *   );
     * </pre>
     */
    TARGET;
}
