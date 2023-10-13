package io.github.fsixteen.common.json.serializes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonSerializer;

/**
 * {@link Long} 或 {@link Date} 或 {@link LocalDateTime} 或 {@link LocalDate} 类型数据
 * {@link JsonSerializer} 序列化模版.<br>
 * 
 * @see io.github.fsixteen.common.json.serializes.AbstractDateTimeJsonSerializer
 * @author FSixteen
 * @since 1.0.1
 */
public class DateTimeJsonSerializer extends AbstractDateTimeJsonSerializer {
}