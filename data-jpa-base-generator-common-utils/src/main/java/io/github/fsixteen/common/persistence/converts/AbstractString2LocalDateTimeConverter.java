package io.github.fsixteen.common.persistence.converts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link String} 与实体数据类型 {@link LocalDateTime} 或
 * {@link LocalDate} 或 {@link LocalTime} 相互转换 {@link AttributeConverter} 接口.<br>
 * 
 * <blockquote>
 * <table class="striped">
 * <caption style="display:none">数据库数据类型与实体数据类型相互转换, 示例对照表.</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">数据库数据类型</th>
 * <th scope="col" style="text-align:left">实体数据类型</th>
 * <th scope="col" style="text-align:left">格式化模板</th>
 * <th scope="col" style="text-align:left">数据转换实现类</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>{@link String}</td>
 * <td>{@link LocalDate}</td>
 * <td>{@code yyyy-MM-dd}</td>
 * <td>{@link Str2LocalDateConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link String}</td>
 * <td>{@link LocalTime}</td>
 * <td>{@code HH:mm:ss.SSS}</td>
 * <td>{@link Str2LocalTimeConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link String}</td>
 * <td>{@link LocalTime}</td>
 * <td>{@code HH:mm:ss}</td>
 * <td>{@link StrStandard2LocalTimeConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link String}</td>
 * <td>{@link LocalDateTime}</td>
 * <td>{@code yyyy-MM-dd HH:mm:ss.SSS}</td>
 * <td>{@link Str2LocalDateTimeConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link String}</td>
 * <td>{@link LocalDateTime}</td>
 * <td>{@code yyyy-MM-dd HH:mm:ss}</td>
 * <td>{@link StrStandard2LocalDateTimeConverter}</td>
 * </tr>
 * </tbody>
 * </table>
 * </blockquote>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public abstract class AbstractString2LocalDateTimeConverter<X, Y> implements AttributeConverter<X, Y> {

    protected LocalDate str2LocalDate(String value) {
        String[] s = value.split("-");
        return LocalDate.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
    }

    protected String localDate2Str(LocalDate value) {
        return value.getYear() + "-" + value.getMonthValue() + "-" + value.getDayOfMonth();
    }

    protected LocalTime str2LocalTime1(String value) {
        String[] s = value.split(":");
        return LocalTime.of(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
    }

    protected LocalTime str2LocalTime2(String value) {
        String[] s = value.split(".");
        return this.str2LocalTime1(s[0]).withNano(Integer.parseInt(s[1]) * 1_000_000);
    }

    protected String localTime2Str1(LocalTime value) {
        return value.getHour() + ":" + value.getMinute() + ":" + value.getSecond();
    }

    protected String localTime2Str2(LocalTime value) {
        int ms = value.getNano() / 1_000_000;
        if (ms > 99) {
            return this.localTime2Str1(value) + "." + ms;
        } else if (ms > 9) {
            return this.localTime2Str1(value) + ".0" + ms;
        } else {
            return this.localTime2Str1(value) + ".00" + ms;
        }
    }

}
