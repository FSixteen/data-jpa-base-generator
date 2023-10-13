package io.github.fsixteen.common.json.serializes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonSerializer;

/**
 * {@link Long} 或 {@link Date} 或 {@link LocalDateTime} 或 {@link LocalDate} 类型数据
 * {@link JsonSerializer} 序列化模版.<br>
 * 
 * @see io.github.fsixteen.common.json.serializes.AbstractDateTimeJsonSerializer
 * @author FSixteen
 * @since 1.0.1
 */
public class DateTimeISO8601JsonSerializer extends AbstractDateTimeJsonSerializer {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).withZone(ZoneId.of("UTC"));

    @Override
    public DateTimeFormatter formater() {
        return DTF;
    }

}