package io.github.fsixteen.common.persistence.converts.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.common.persistence.converts.String2MapStringKeyStringValueConverter;

/**
 * {@link io.github.fsixteen.common.persistence.converts.String2MapStringKeyStringValueConverter}
 * 相关测试内容.<br>
 * 
 * @see io.github.fsixteen.common.persistence.converts.String2MapStringKeyStringValueConverter
 * @author FSixteen
 * @since 1.0.3
 */
public class String2MapStringKeyStringValueConverterTest {

    @Test
    public void string2MapStringKeyStringValueConverterTest() {
        {
            Map<String, String> oldMap = new HashMap<String, String>();
            oldMap.put("123:345&:!@#", "123:345&:!@#");
            String str = new String2MapStringKeyStringValueConverter().convertToDatabaseColumn(oldMap);
            Map<String, String> currMap = new String2MapStringKeyStringValueConverter().convertToEntityAttribute(str);
            assertEquals(oldMap.size(), currMap.size(), String.format("数据转换失败 %s <> %s !", oldMap.size(), currMap.size()));
        }
        {
            Map<String, String> oldMap = new HashMap<String, String>();
            oldMap.put("1d3\\:345&:!@#", "1d3\\:345&:!@#");
            String str = new String2MapStringKeyStringValueConverter().convertToDatabaseColumn(oldMap);
            Map<String, String> currMap = new String2MapStringKeyStringValueConverter().convertToEntityAttribute(str);
            assertEquals(oldMap.size(), currMap.size(), String.format("数据转换失败 %s <> %s !", oldMap.size(), currMap.size()));
        }
    }

}
