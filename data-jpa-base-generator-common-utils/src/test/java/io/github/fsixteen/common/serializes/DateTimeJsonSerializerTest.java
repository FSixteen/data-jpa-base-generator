package io.github.fsixteen.common.serializes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import io.github.fsixteen.common.json.serializes.AbstractDateTimeJsonSerializer;
import io.github.fsixteen.common.json.serializes.DateTimeGMTJsonSerializer;
import io.github.fsixteen.common.json.serializes.DateTimeISO8601JsonSerializer;
import io.github.fsixteen.common.json.serializes.DateTimeISOJsonSerializer;
import io.github.fsixteen.common.json.serializes.DateTimeJsonSerializer;
import io.github.fsixteen.common.json.serializes.DateTimeStandardJsonSerializer;

/**
 * {@link io.github.fsixteen.common.json.serializes.AbstractDateTimeJsonSerializer}
 * 相关测试内容.<br>
 * 
 * @see io.github.fsixteen.common.json.serializes.AbstractDateTimeJsonSerializer
 * @author FSixteen
 * @since 1.0.1
 */
public class DateTimeJsonSerializerTest {

    // 2023-01-31 15:23:11.123 +08:00
    private static final Long MILLS = 1675149791123L;
    private static final Date DATE = new Date(MILLS);
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.ofInstant(DATE.toInstant(), ZoneId.systemDefault());
    private static final LocalDate LOCAL_DATE = LOCAL_DATE_TIME.toLocalDate();

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("+08:00")));
    }

    @Test
    public void dateTimeJsonSerializerTest() throws IOException {
        AbstractDateTimeJsonSerializer inst = new DateTimeJsonSerializer();
        assertEquals("\"2023-01-31 15:23:11.123\"", test(inst, MILLS), String.format("%s %s 转换失败!", inst.getClass(), "MILLS"));
        assertEquals("\"2023-01-31 15:23:11.123\"", test(inst, DATE), String.format("%s %s 转换失败!", inst.getClass(), "DATE"));
        assertEquals("\"2023-01-31 15:23:11.123\"", test(inst, LOCAL_DATE_TIME), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE_TIME"));
        assertEquals("\"2023-01-31 00:00:00.000\"", test(inst, LOCAL_DATE), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE"));

        inst = new DateTimeStandardJsonSerializer();
        assertEquals("\"2023-01-31 15:23:11\"", test(inst, MILLS), String.format("%s %s 转换失败!", inst.getClass(), "MILLS"));
        assertEquals("\"2023-01-31 15:23:11\"", test(inst, DATE), String.format("%s %s 转换失败!", inst.getClass(), "DATE"));
        assertEquals("\"2023-01-31 15:23:11\"", test(inst, LOCAL_DATE_TIME), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE_TIME"));
        assertEquals("\"2023-01-31 00:00:00\"", test(inst, LOCAL_DATE), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE"));

        inst = new DateTimeISO8601JsonSerializer();
        assertEquals("\"2023-01-31T07:23:11.123Z\"", test(inst, MILLS), String.format("%s %s 转换失败!", inst.getClass(), "MILLS"));
        assertEquals("\"2023-01-31T07:23:11.123Z\"", test(inst, DATE), String.format("%s %s 转换失败!", inst.getClass(), "DATE"));
        assertEquals("\"2023-01-31T07:23:11.123Z\"", test(inst, LOCAL_DATE_TIME), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE_TIME"));
        assertEquals("\"2023-01-30T16:00:00.000Z\"", test(inst, LOCAL_DATE), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE"));

        inst = new DateTimeISOJsonSerializer();
        assertEquals("\"2023-01-31T15:23:11.123+08:00\"", test(inst, MILLS), String.format("%s %s 转换失败!", inst.getClass(), "MILLS"));
        assertEquals("\"2023-01-31T15:23:11.123+08:00\"", test(inst, DATE), String.format("%s %s 转换失败!", inst.getClass(), "DATE"));
        assertEquals("\"2023-01-31T15:23:11.123+08:00\"", test(inst, LOCAL_DATE_TIME), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE_TIME"));
        assertEquals("\"2023-01-31T00:00:00.000+08:00\"", test(inst, LOCAL_DATE), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE"));

        inst = new DateTimeGMTJsonSerializer();
        assertEquals("\"Tue, 31 Jan 2023 07:23:11 GMT\"", test(inst, MILLS), String.format("%s %s 转换失败!", inst.getClass(), "MILLS"));
        assertEquals("\"Tue, 31 Jan 2023 07:23:11 GMT\"", test(inst, DATE), String.format("%s %s 转换失败!", inst.getClass(), "DATE"));
        assertEquals("\"Tue, 31 Jan 2023 07:23:11 GMT\"", test(inst, LOCAL_DATE_TIME), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE_TIME"));
        assertEquals("\"Mon, 30 Jan 2023 16:00:00 GMT\"", test(inst, LOCAL_DATE), String.format("%s %s 转换失败!", inst.getClass(), "LOCAL_DATE"));
    }

    private String test(AbstractDateTimeJsonSerializer jsonSerializer, Object value) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1 << 6);
            JsonGenerator gentor = new JsonFactory().createGenerator(baos, JsonEncoding.UTF8);) {
            jsonSerializer.serialize(value, gentor, null);
            gentor.flush();
            return baos.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
