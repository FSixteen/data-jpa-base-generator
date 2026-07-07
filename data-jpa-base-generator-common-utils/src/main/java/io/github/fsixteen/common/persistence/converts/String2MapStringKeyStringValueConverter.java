package io.github.fsixteen.common.persistence.converts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link Map}{@code <String, String>} 相互转换
 * {@link AttributeConverter} 接口, {@code Key} 空值自动转换为空字符串.<br>
 * 
 * <pre>
 * eg:
 * {"key1": "value1", "key2": "value2"} -> key1:value1&key2:value2
 * {"key&with": "special:", "normal": "value"} -> key\&with:special\:&normal:value
 * </pre>
 * 
 * @author FSixteen
 * @since 1.0.3
 */
public class String2MapStringKeyStringValueConverter implements AttributeConverter<Map<String, String>, String> {

    private static final char DELIMITER_CHAR = '&';

    private static final char PAIR_DELIMITER_CHAR = ':';

    private static final char ESCAPED_CHAR = '\\';

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        } else if (attribute.isEmpty()) {
            return "";
        } else {
            StringBuilder out = new StringBuilder(attribute.size() * 64);
            boolean notFirst = false;
            Iterator<Entry<String, String>> entries = attribute.entrySet().iterator();
            for (; entries.hasNext();) {
                if (notFirst) {
                    out.append(DELIMITER_CHAR);
                } else {
                    notFirst = true;
                }

                Entry<String, String> entry = entries.next();
                String key = entry.getKey();
                String value = entry.getValue();

                if (Objects.isNull(key)) {
                    out.append(""); // null 值转换为空字符串
                } else {
                    out.append(this.escapeSpecialChars(key));
                }

                if (Objects.isNull(value)) {
                    // 不需要处理, 用于区分 null 值与空字符串
                } else {
                    out.append(PAIR_DELIMITER_CHAR);
                    out.append(this.escapeSpecialChars(value));
                }
            }

            return out.toString();
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        } else if (dbData.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Map<String, String> result = new HashMap<>();
            String[] pairs = this.splitUnescapedDelimiter(dbData, DELIMITER_CHAR);
            for (String pair : pairs) {
                if (pair.isEmpty()) {
                    result.put("", null);
                    continue;
                }
                String[] kv = this.splitUnescapedDelimiter(pair, PAIR_DELIMITER_CHAR);
                if (kv.length == 0) {
                    continue;
                } else if (kv.length == 1) {
                    String key = this.unescapeSpecialChars(kv[0]);
                    result.put(key, null);
                } else {
                    String key = this.unescapeSpecialChars(kv[0]);
                    String value = this.unescapeSpecialChars(kv[1]);
                    result.put(key, value);
                }
            }
            return result;
        }
    }

    /**
     * 查找未转义的分隔符位置.
     * 
     * @param input     数据
     * @param delimiter 分割符
     * @return String[]
     */
    private String[] splitUnescapedDelimiter(String input, char delimiter) {
        if (Objects.isNull(input)) {
            return new String[] {};
        } else {
            List<String> result = new ArrayList<>();
            int start = 0, offset = 0;
            for (; offset < input.length(); offset++) {
                char current = input.charAt(offset);
                // 检查是否是转义字符
                if (current == ESCAPED_CHAR) {
                    // 如果是转义字符, 跳过下一个字符
                    offset++;
                    continue;
                }
                // 检查是否是目标分隔符
                if (current == delimiter) {
                    result.add(input.substring(start, offset));
                    start = offset + 1;
                }
            }
            result.add(input.substring(start));
            return result.toArray(new String[result.size()]);
        }
    }

    /**
     * 转义特殊字符.
     * 
     * @param input 数据
     * @return String
     */
    private String escapeSpecialChars(String input) {
        if (Objects.isNull(input)) {
            return null;
        }
        StringBuilder out = new StringBuilder(input.length() << 2);
        for (int offset = 0; offset < input.length(); offset++) {
            char current = input.charAt(offset);
            switch (current) {
                case ESCAPED_CHAR:
                    out.append(ESCAPED_CHAR).append(ESCAPED_CHAR);
                    break;
                case DELIMITER_CHAR:
                    out.append(ESCAPED_CHAR).append(DELIMITER_CHAR);
                    break;
                case PAIR_DELIMITER_CHAR:
                    out.append(ESCAPED_CHAR).append(PAIR_DELIMITER_CHAR);
                    break;
                default:
                    out.append(current);
                    break;
            }
        }
        return out.toString();
    }

    /**
     * 反转义特殊字符.
     * 
     * @param input 数据
     * @return String
     */
    private String unescapeSpecialChars(String input) {
        if (Objects.isNull(input)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int offset = 0; offset < input.length(); offset++) {
            char current = input.charAt(offset);
            if (current == ESCAPED_CHAR && offset + 1 < input.length()) {
                char next = input.charAt(offset + 1);
                switch (next) {
                    case DELIMITER_CHAR:
                        result.append(DELIMITER_CHAR);
                        offset++;
                        break;
                    case PAIR_DELIMITER_CHAR:
                        result.append(PAIR_DELIMITER_CHAR);
                        offset++;
                        break;
                    case ESCAPED_CHAR:
                        result.append(ESCAPED_CHAR);
                        offset++;
                        break;
                    default:
                        result.append(current);
                        break;
                }
            } else {
                result.append(current);
            }
        }
        return result.toString();
    }

}
