package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * 字段获取工具-测试.
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class FieldUtilTest {

    private static class User {
        private String name;

        public String getName() {
            return name;
        }
    }

    @Test
    public void test() {
        String fieldName = FieldUtil.getFieldName(User::getName);
        assertEquals("name", fieldName, FieldUtil.class.getName() + " getFieldName failed!");
    }

}
