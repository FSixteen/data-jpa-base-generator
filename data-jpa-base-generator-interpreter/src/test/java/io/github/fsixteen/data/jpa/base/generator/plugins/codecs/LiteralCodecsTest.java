package io.github.fsixteen.data.jpa.base.generator.plugins.codecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class LiteralCodecsTest {

    @Test
    public void shouldParseCommonLiteralTypes() {
        assertEquals(Boolean.TRUE, LiteralCodecs.parse("true", Boolean.class));
        assertEquals(new BigDecimal("12.34"), LiteralCodecs.parse("12.34", BigDecimal.class));
        assertEquals(12.34D, LiteralCodecs.parse("12.34", Double.class));
        assertEquals(12.5F, LiteralCodecs.parse("12.5", Float.class));
        assertEquals((short) 7, LiteralCodecs.parse("7", Short.class));
        assertEquals(42, LiteralCodecs.parse("42", Integer.class));
        assertEquals(42L, LiteralCodecs.parse("42", Long.class));
        assertEquals(LocalDate.parse("2026-06-24"), LiteralCodecs.parse("2026-06-24", LocalDate.class));
        assertEquals(LocalTime.parse("10:15:30"), LiteralCodecs.parse("10:15:30", LocalTime.class));
        assertEquals(LocalDateTime.parse("2026-06-24T10:15:30"), LiteralCodecs.parse("2026-06-24T10:15:30", LocalDateTime.class));
        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), LiteralCodecs.parse("123e4567-e89b-12d3-a456-426614174000", UUID.class));
    }

    @Test
    public void shouldParseEnumAndCollectionAndRange() {
        assertEquals(TestEnum.BETA, LiteralCodecs.parse("BETA", TestEnum.class));

        List<Object> range = LiteralCodecs.parseRange("1,2", ",", Integer.class);
        assertEquals(Arrays.asList(1, 2), range);
        assertEquals(Arrays.asList(TestEnum.ALPHA, TestEnum.BETA), LiteralCodecs.parseRange("ALPHA,BETA", ",", TestEnum.class));

        List<Object> collection = LiteralCodecs.parseCollection("alpha,beta", ",", String.class);
        assertEquals(Arrays.asList("alpha", "beta"), collection);
        assertEquals(Arrays.asList(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), UUID.fromString("123e4567-e89b-12d3-a456-426614174001")),
            LiteralCodecs.parseCollection("123e4567-e89b-12d3-a456-426614174000,123e4567-e89b-12d3-a456-426614174001", ",", UUID.class));
    }

    @Test
    public void shouldParseDateFromTimestamp() {
        Object value = LiteralCodecs.parse("1719187200000", Date.class);
        assertInstanceOf(Date.class, value);
    }

    @Test
    public void shouldParseFormattedTemporalLiteralTypes() {
        assertEquals(LocalDate.parse("2026-06-24"), LiteralCodecs.parse("2026/06/24", LocalDate.class, "yyyy/MM/dd"));
        assertEquals(LocalTime.parse("10:15:30"), LiteralCodecs.parse("10|15|30", LocalTime.class, "HH|mm|ss"));
        assertEquals(LocalDateTime.parse("2026-06-24T10:15:30"), LiteralCodecs.parse("2026/06/24 10:15:30", LocalDateTime.class, "yyyy/MM/dd HH:mm:ss"));
        assertInstanceOf(Date.class, LiteralCodecs.parse("2026/06/24 10:15:30", Date.class, "yyyy/MM/dd HH:mm:ss"));
    }

    @Test
    public void shouldParseEpochTemporalLiteralTypes() {
        assertEquals(LocalDate.class, LiteralCodecs.parseEpoch("1719187200000", LocalDate.class).getClass());
        assertEquals(LocalTime.class, LiteralCodecs.parseEpoch("1719187200000", LocalTime.class).getClass());
        assertEquals(LocalDateTime.class, LiteralCodecs.parseEpoch("1719187200000", LocalDateTime.class).getClass());
        assertEquals(1719187200000L, LiteralCodecs.parseEpoch("1719187200000", Long.class));
    }

    @Test
    public void shouldReportCodecSupportForRegisteredTypes() {
        assertTrue(LiteralCodecs.supports(Boolean.class));
        assertTrue(LiteralCodecs.supports(Double.class));
        assertTrue(LiteralCodecs.supports(Float.class));
        assertTrue(LiteralCodecs.supports(Short.class));
        assertTrue(LiteralCodecs.supports(Integer.class));
        assertTrue(LiteralCodecs.supports(Long.class));
        assertTrue(LiteralCodecs.supports(LocalDate.class));
        assertTrue(LiteralCodecs.supports(LocalTime.class));
        assertTrue(LiteralCodecs.supports(LocalDateTime.class));
        assertTrue(LiteralCodecs.supports(Date.class));
        assertTrue(LiteralCodecs.supports(UUID.class));
    }

    private enum TestEnum {
        ALPHA, BETA
    }

}
