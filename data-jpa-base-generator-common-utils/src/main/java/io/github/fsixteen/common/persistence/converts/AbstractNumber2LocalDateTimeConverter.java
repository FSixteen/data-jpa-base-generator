package io.github.fsixteen.common.persistence.converts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.AttributeConverter;

/**
 * 数据库数据类型 {@link Integer} 或 {@link Long} 与实体数据类型 {@link LocalDateTime} 或
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
 * <td>{@link Integer}</td>
 * <td>{@link LocalDate}</td>
 * <td>{@code yyyyMMdd}</td>
 * <td>{@link Int2LocalDateConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link Integer}</td>
 * <td>{@link LocalTime}</td>
 * <td>{@code HHmmssSSS}</td>
 * <td>{@link Int2LocalTimeConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link Integer}</td>
 * <td>{@link LocalTime}</td>
 * <td>{@code HHmmss}</td>
 * <td>{@link IntStandard2LocalTimeConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link Long}</td>
 * <td>{@link LocalDateTime}</td>
 * <td>{@code yyyyMMddHHmmssSSS}</td>
 * <td>{@link Long2LocalDateTimeConverter}</td>
 * </tr>
 * <tr>
 * <td>{@link Long}</td>
 * <td>{@link LocalDateTime}</td>
 * <td>{@code yyyyMMddHHmmss}</td>
 * <td>{@link LongStandard2LocalDateTimeConverter}</td>
 * </tr>
 * </tbody>
 * </table>
 * </blockquote>
 * 
 * @author FSixteen
 * @since 1.0.2
 */
public abstract class AbstractNumber2LocalDateTimeConverter<X, Y> implements AttributeConverter<X, Y> {

    protected LocalDate number2LocalDate(int value) {
        return LocalDate.of(value / 10_000, value / 100 % 100, value % 100);
    }

    protected int localDate2Number(LocalDate value) {
        return value.getYear() * 10_000 + value.getMonthValue() * 100 + value.getDayOfMonth();
    }

    protected LocalTime number2LocalTime1(int value) {
        return LocalTime.of(value / 10_000, (value / 100) % 100, value % 100);
    }

    protected LocalTime number2LocalTime2(int value) {
        return this.number2LocalTime1(value / 1_000).withNano((value % 1_000) * 1_000_000);
    }

    protected int localTime2Number1(LocalTime value) {
        return value.getHour() * 10_000 + value.getMinute() * 100 + value.getSecond();
    }

    protected int localTime2Number2(LocalTime value) {
        return this.localTime2Number1(value) * 1_000 + value.getNano() / 1_000_000;
    }

}
