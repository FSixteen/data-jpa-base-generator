package io.github.fsixteen.common.persistence.converts.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.common.persistence.converts.Str2DurationConverter;

/**
 * {@link io.github.fsixteen.common.persistence.converts.Str2DurationConverter}
 * 相关测试内容.<br>
 *
 * @see io.github.fsixteen.common.persistence.converts.Str2DurationConverter
 * @author FSixteen
 * @since 1.1.0
 */
public class Str2DurationConverterTest {

    private final Str2DurationConverter converter = new Str2DurationConverter();

    @Test
    public void convertNullToDatabaseColumn() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    public void convertNullToEntityAttribute() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    public void convertEmptyStringToEntityAttribute() {
        assertNull(converter.convertToEntityAttribute(""));
    }

    @Test
    public void roundTripHours() {
        Duration original = Duration.ofHours(48);
        String str = converter.convertToDatabaseColumn(original);
        assertEquals("PT48H", str);
        Duration restored = converter.convertToEntityAttribute(str);
        assertEquals(original, restored);
    }

    @Test
    public void roundTripMinutes() {
        Duration original = Duration.ofMinutes(30);
        String str = converter.convertToDatabaseColumn(original);
        assertEquals("PT30M", str);
        Duration restored = converter.convertToEntityAttribute(str);
        assertEquals(original, restored);
    }

    @Test
    public void roundTripDaysHoursMinutes() {
        Duration original = Duration.ofDays(2).plusHours(3).plusMinutes(4);
        String str = converter.convertToDatabaseColumn(original);
        Duration restored = converter.convertToEntityAttribute(str);
        assertEquals(original, restored);
    }

    @Test
    public void roundTripSeconds() {
        Duration original = Duration.ofSeconds(120);
        String str = converter.convertToDatabaseColumn(original);
        assertEquals("PT2M", str);
        Duration restored = converter.convertToEntityAttribute(str);
        assertEquals(original, restored);
    }

    @Test
    public void roundTripNegativeDuration() {
        Duration original = Duration.ofMinutes(-30);
        String str = converter.convertToDatabaseColumn(original);
        assertEquals("PT-30M", str);
        Duration restored = converter.convertToEntityAttribute(str);
        assertEquals(original, restored);
    }

    @Test
    public void roundTripMillis() {
        Duration original = Duration.ofMillis(1500);
        String str = converter.convertToDatabaseColumn(original);
        assertEquals("PT1.5S", str);
        Duration restored = converter.convertToEntityAttribute(str);
        assertEquals(original, restored);
    }

    @Test
    public void invalidFormatThrowsException() {
        assertThrows(DateTimeParseException.class, () -> {
            converter.convertToEntityAttribute("not-a-duration");
        });
    }

}