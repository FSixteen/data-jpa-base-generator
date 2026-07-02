package io.github.fsixteen.common.json.serializes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.UnsupportedTemporalTypeException;
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
 * @see AbstractDateTimeJsonSerializer
 * @author FSixteen
 * @since 1.0.1
 */
public class DateTimeISO8601JsonSerializer extends AbstractDateTimeJsonSerializer {

    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).withZone(ZONE_ID);

    @Override
    public DateTimeFormatter formatter() {
        return DTF;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, IllegalArgumentException {
        if (Objects.nonNull(value)) {
            if (value instanceof LocalDateTime) {
                gen.writeString(this.formatter().format(LocalDateTime.class.cast(value).atZone(ZONE_ID)));
            } else if (value instanceof LocalDate) {
                gen.writeString(this.formatter().format(LocalDate.class.cast(value).atStartOfDay().atZone(ZONE_ID)));
            } else if (value instanceof LocalTime) {
                throw new UnsupportedTemporalTypeException("Cannot format given Object as a 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z''");
            } else {
                super.serialize(value, gen, serializers);
            }
        } else {
            gen.writeNull();
        }
    }

}