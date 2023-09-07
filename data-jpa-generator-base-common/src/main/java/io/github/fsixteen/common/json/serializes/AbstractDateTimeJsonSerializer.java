package io.github.fsixteen.common.json.serializes;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * {@link Long} 或 {@link Date} 或 {@link LocalDateTime} 或 {@link LocalDate} 类型数据
 * {@link JsonSerializer} 序列化模版.<br>
 * 
 * <blockquote>
 * <table class="striped">
 * <caption style="display:none">序列化模版格式及序列化实现类, 示例对照表.</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">原始数据类型</th>
 * <th scope="col" style="text-align:left">目标格式化模版</th>
 * <th scope="col" style="text-align:left">模版序列化实现类</th>
 * <th scope="col" style="text-align:left">示例</th>
 * </thead>
 * <tbody>
 * <tr>
 * <td>{@link Long} 或 {@link Date} 或 {@link LocalDateTime} 或
 * {@link LocalDate}</td>
 * <td>{@code yyyy-MM-dd HH:mm:ss.SSS}</td>
 * <td>{@link DateTimeJsonSerializer}</td>
 * <td>{@code 2023-01-01 00:00:00.000}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyy-MM-dd HH:mm:ss}</td>
 * <td>{@link DateTimeStandardJsonSerializer}</td>
 * <td>{@code 2023-01-01 00:00:00}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}</td>
 * <td>{@link DateTimeISO8601JsonSerializer}</td>
 * <td>{@code 2023-01-01T00:00:00.000Z}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyy-MM-dd'T'HH:mm:ss.SSSXXX}</td>
 * <td>{@link DateTimeISOJsonSerializer}</td>
 * <td>{@code 2023-01-01T00:00:00.000+08:00}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code EE, dd MMM yyyy HH:mm:ss z}</td>
 * <td>{@link DateTimeGMTJsonSerializer}</td>
 * <td>{@code Sun, 01 Jan 2023 00:00:00 GMT}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyyMMddHHmmssSSS}</td>
 * <td>{@link DateTimeSimpleJsonSerializer}</td>
 * <td>{@code 20230101000000000}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyyMMddHHmmss}</td>
 * <td>{@link DateTimeSimpleStandardJsonSerializer}</td>
 * <td>{@code 20230101000000}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyy-MM-dd}</td>
 * <td>{@link DateJsonSerializer}</td>
 * <td>{@code 2023-01-01}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyy/MM/dd}</td>
 * <td>{@link DateBySlashJsonSerializer}</td>
 * <td>{@code 2023/01/01}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyyMMdd}</td>
 * <td>{@link DateSimpleJsonSerializer}</td>
 * <td>{@code 20230101}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyy-MM}</td>
 * <td>{@link DateJsonSerializer2yyyyMM}</td>
 * <td>{@code 2023-01}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyy/MM}</td>
 * <td>{@link DateBySlashJsonSerializer2yyyyMM}</td>
 * <td>{@code 2023/01}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code yyyyMM}</td>
 * <td>{@link DateSimpleJsonSerializer2yyyyMM}</td>
 * <td>{@code 202301}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code HH:mm:ss.SSS}</td>
 * <td>{@link TimeJsonSerializer}</td>
 * <td>{@code 00:00:00.000}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code HH:mm:ss}</td>
 * <td>{@link TimeStandardJsonSerializer}</td>
 * <td>{@code 00:00:00}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code HHmmssSSS}</td>
 * <td>{@link TimeSimpleJsonSerializer}</td>
 * <td>{@code 000000000}</td>
 * </tr>
 * <tr>
 * <td>同上</td>
 * <td>{@code HHmmss}</td>
 * <td>{@link TimeSimpleStandardJsonSerializer}</td>
 * <td>{@code 000000}</td>
 * </tr>
 * </tbody>
 * </table>
 * </blockquote>
 * <p>
 * 其他情况.<br>
 * </p>
 * <blockquote>
 * <table class="striped">
 * <caption style="display:none">序列化模版格式及序列化实现类, 示例对照表.</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">原始数据类型</th>
 * <th scope="col" style="text-align:left">目标格式化模版</th>
 * <th scope="col" style="text-align:left">模版序列化实现类</th>
 * <th scope="col" style="text-align:left">示例</th>
 * </thead>
 * <tbody>
 * <tr>
 * <td>{@link Long} 或 {@link Date} 或 {@link LocalDateTime} 或
 * {@link LocalDate}</td>
 * <td>{@code 毫秒时间戳}</td>
 * <td>{@link DateTime2MillisecondJsonSerializer}</td>
 * <td>{@code 1675149791123}</td>
 * </tr>
 * </tbody>
 * </table>
 * </blockquote>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public abstract class AbstractDateTimeJsonSerializer extends JsonSerializer<Object> {
    static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).withZone(ZoneId.systemDefault());

    /**
     * 格式化模版.<br>
     *
     * @return 格式化模版实例 {@link java.time.format.DateTimeFormatter}
     */
    public DateTimeFormatter formater() {
        return DTF;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, IllegalArgumentException {
        if (Objects.nonNull(value)) {
            if (value instanceof Long) {
                ZonedDateTime zonedDateTime = Instant.ofEpochMilli(Long.class.cast(value)).atZone(DEFAULT_ZONE);
                gen.writeString(this.formater().format(zonedDateTime));
            } else if (value instanceof Date) {
                ZonedDateTime zonedDateTime = Date.class.cast(value).toInstant().atZone(DEFAULT_ZONE);
                gen.writeString(this.formater().format(zonedDateTime));
            } else if (value instanceof LocalDateTime) {
                gen.writeString(this.formater().format(LocalDateTime.class.cast(value).atZone(DEFAULT_ZONE)));
            } else if (value instanceof LocalDate) {
                gen.writeString(this.formater().format(LocalDate.class.cast(value).atStartOfDay().atZone(DEFAULT_ZONE)));
            } else {
                throw new IllegalArgumentException("Cannot format given Object as a Date");
            }
        } else {
            gen.writeNull();
        }
    }

}