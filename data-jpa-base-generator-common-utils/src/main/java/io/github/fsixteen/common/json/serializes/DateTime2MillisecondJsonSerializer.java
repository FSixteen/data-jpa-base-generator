package io.github.fsixteen.common.json.serializes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * {@link Long} 或 {@link Date} 或 {@link LocalDateTime} 或 {@link LocalDate} 类型数据转
 * {@link Long} 类型毫秒时间戳数据 {@link JsonSerializer} 序列化模版.<br>
 * 
 * @see io.github.fsixteen.common.json.serializes.AbstractDateTimeJsonSerializer
 * @author FSixteen
 * @since 1.0.1
 */
public class DateTime2MillisecondJsonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, IllegalArgumentException {
        if (Objects.nonNull(value)) {
            if (value instanceof Long) {
                gen.writeNumber(Long.class.cast(value));
            } else if (value instanceof Date) {
                gen.writeNumber(Date.class.cast(value).getTime());
            } else if (value instanceof LocalDateTime) {
                gen.writeNumber(LocalDateTime.class.cast(value).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            } else if (value instanceof LocalDate) {
                gen.writeNumber(LocalDate.class.cast(value).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            } else {
                throw new IllegalArgumentException("Cannot format given Object as a Date");
            }
        } else {
            gen.writeNull();
        }
    }

}