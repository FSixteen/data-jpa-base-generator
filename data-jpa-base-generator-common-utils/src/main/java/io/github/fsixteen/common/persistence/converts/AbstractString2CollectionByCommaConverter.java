package io.github.fsixteen.common.persistence.converts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link T} 通过
 * {@value #DECOLLATOR} 分割元素相互转换 {@link AttributeConverter} 接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.3
 */
public abstract class AbstractString2CollectionByCommaConverter<T extends Collection<String>> implements AttributeConverter<T, String> {

    static final char DELIMITER_CHAR = ',';

    static final char ESCAPED_CHAR = '\\';

    String collection2String(Collection<String> eles) {
        if (Objects.isNull(eles)) {
            return null;
        } else if (eles.isEmpty()) {
            return "";
        } else {
            StringBuilder out = new StringBuilder(Math.max(eles.size() * 16, 2 << 8));
            boolean notFirst = false;
            for (String e : eles) {
                if (notFirst) {
                    out.append(DELIMITER_CHAR);
                } else {
                    notFirst = true;
                }
                out.append(this.escapeSpecialChars(e));
            }
            return out.toString();
        }
    }

    T string2Collection(String input, T collection) {
        if (Objects.isNull(input)) {
            return null;
        } else if (0 == input.length()) {
            return collection;
        } else {
            for (String ele : this.splitUnescapedDelimiter(input)) {
                collection.add(this.unescapeSpecialChars(ele));
            }
            return collection;
        }
    }

    /**
     * 查找未转义的分隔符位置.
     * 
     * @param input     数据
     * @param delimiter 分割符
     * @return String[]
     */
    String[] splitUnescapedDelimiter(String input) {
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
                if (current == DELIMITER_CHAR) {
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
    String escapeSpecialChars(String input) {
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
    String unescapeSpecialChars(String input) {
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
