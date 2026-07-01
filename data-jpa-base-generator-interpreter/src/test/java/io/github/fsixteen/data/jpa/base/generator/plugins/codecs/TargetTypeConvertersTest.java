package io.github.fsixteen.data.jpa.base.generator.plugins.codecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;

public class TargetTypeConvertersTest {

    @Test
    public void shouldParseSimpleTargetTypes() {
        assertEquals(new BigDecimal("12.34"), TargetTypeConverters.parse("12.34", TargetType.TO_BIGDECIMAL, ""));
        assertEquals(Boolean.TRUE, TargetTypeConverters.parse("true", TargetType.TO_BOOLEAN, ""));
        assertEquals(123L, TargetTypeConverters.parse("123", TargetType.TO_TS, ""));
    }

    @Test
    public void shouldParseTemporalTargetTypes() {
        assertEquals(LocalDate.parse("2026-06-24"), TargetTypeConverters.parse("2026-06-24", TargetType.TO_LD, "yyyy-MM-dd"));
        assertEquals(LocalTime.parse("10:15:30"), TargetTypeConverters.parse("10:15:30", TargetType.TO_LT, "HH:mm:ss"));
        assertEquals(LocalDateTime.parse("2026-06-24T10:15:30"), TargetTypeConverters.parse("2026-06-24 10:15:30", TargetType.TO_LDT, "yyyy-MM-dd HH:mm:ss"));
        assertInstanceOf(Date.class, TargetTypeConverters.parse("1719187200000", TargetType.TO_TS_TO_DATE, ""));
        assertEquals(LocalDate.class, TargetTypeConverters.parse("1719187200000", TargetType.TO_TS_TO_LD, "").getClass());
        assertEquals(LocalTime.class, TargetTypeConverters.parse("1719187200000", TargetType.TO_TS_TO_LT, "").getClass());
        assertEquals(LocalDateTime.class, TargetTypeConverters.parse("1719187200000", TargetType.TO_TS_TO_LDT, "").getClass());
    }

    @Test
    public void shouldParseCollection() {
        List<Object> values = TargetTypeConverters.parseCollection("1,2,3", ",", TargetType.TO_INTEGER, "");
        assertEquals(Arrays.asList(1, 2, 3), values);
    }

}
