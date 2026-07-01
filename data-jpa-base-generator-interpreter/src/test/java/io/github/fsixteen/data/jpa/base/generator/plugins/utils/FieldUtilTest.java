package io.github.fsixteen.data.jpa.base.generator.plugins.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

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

        private boolean active;

        private String URL;

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public String getURL() {
            return URL;
        }

        public String getDisplayName() {
            return name;
        }

    }

    @Test
    public void shouldGetFieldNameFromGetter() {
        String fieldName = FieldUtil.getFieldName(User::getName);
        assertEquals("name", fieldName, FieldUtil.class.getName() + " getFieldName failed!");
    }

    @Test
    public void shouldGetFieldNameFromBooleanGetter() {
        String fieldName = FieldUtil.getFieldName(User::isActive);
        assertEquals("active", fieldName, FieldUtil.class.getName() + " getFieldName failed!");
    }

    @Test
    public void shouldFollowJavaBeanDecapitalizeRule() {
        String fieldName = FieldUtil.getFieldName(User::getURL);
        assertEquals("URL", fieldName, FieldUtil.class.getName() + " getFieldName failed!");
    }

    @Test
    public void shouldThrowExceptionWhenMethodHasNoBackingField() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> FieldUtil.getField(User::getDisplayName));
        assertEquals("无法从lambda表达式中解析字段", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenMethodIsNotGetter() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> FieldUtil.getField(User::toString));
        assertEquals("方法名称: toString, 不符合Java Bean getter规范", exception.getMessage());
    }

    @Test
    public void shouldReturnFieldInstance() {
        Field field = FieldUtil.getField(User::getName);
        assertEquals("name", field.getName(), FieldUtil.class.getName() + " getField failed!");
    }

}
