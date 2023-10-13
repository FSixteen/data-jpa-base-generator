package io.github.fsixteen.data.jpa.generator.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.utils.GroupColumnUtils;
import io.github.fsixteen.data.jpa.base.generator.utils.GroupColumnUtils.Column;

/**
 * 分组查询字段处理工具-测试.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public class GroupColumnUtilsTest {

    @Test
    public void test() {
        String param = GroupColumnUtils.newInst().fun(true, "count", Column.field("column01")).toString();
        assertEquals("read@@fun@@count::${column01}", param, GroupColumnUtils.class.getName() + " toString failed!");

        Column column01 = GroupColumnUtils.of(param);
        assertTrue(column01.isReadOnly(), GroupColumnUtils.class.getName() + " fromString failed!");
    }

}
