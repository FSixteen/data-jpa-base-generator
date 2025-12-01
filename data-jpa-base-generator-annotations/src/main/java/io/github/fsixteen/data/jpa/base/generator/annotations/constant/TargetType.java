package io.github.fsixteen.data.jpa.base.generator.annotations.constant;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 分割后, 分割内容的目标类型. 或其他需要进行目标转换的目标类型<br>
 */
public enum TargetType {

    /**
     * 默认, 保持原始内容.
     */
    DEFAULT(e -> e),

    /**
     * 转 {@link java.lang.String String}.
     */
    TO_STR((e) -> Objects.toString(e)),

    /**
     * 转 {@link java.lang.Boolean Boolean}.
     */
    TO_BOOLEAN(e -> Boolean.valueOf(e)),

    /**
     * 转 {@link java.math.BigDecimal BigDecimal}.
     */
    TO_BIGDECIMAL(e -> new BigDecimal(e)),

    /**
     * 转 {@link java.lang.Double Double}.
     */
    TO_DOUBLE(e -> Double.valueOf(e)),

    /**
     * 转 {@link java.lang.Float Float}.
     */
    TO_FLOAT(e -> Float.valueOf(e)),

    /**
     * 转 {@link java.lang.Short Short}.
     */
    TO_SHORT(e -> Short.valueOf(e)),

    /**
     * 转 {@link java.lang.Integer Integer}.
     */
    TO_INTEGER(e -> Integer.valueOf(e)),

    /**
     * 转 {@link java.lang.Long Long}.
     */
    TO_LONG(e -> Long.valueOf(e)),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}.
     */
    TO_TS(e -> Long.valueOf(e)),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.util.Date Date}.
     */
    TO_TS_TO_DATE(e -> new Date(Long.valueOf(e))),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.time.LocalDate
     * LocalDate}.
     */
    TO_TS_TO_LD(e -> Instant.ofEpochMilli(Long.valueOf(e)).atZone(ZoneId.systemDefault()).toLocalDate()),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.time.LocalTime
     * LocalTime}.
     */
    TO_TS_TO_LT(e -> Instant.ofEpochMilli(Long.valueOf(e)).atZone(ZoneId.systemDefault()).toLocalTime()),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.time.LocalDateTime
     * LocalDateTime}.
     */
    TO_TS_TO_LDT(e -> Instant.ofEpochMilli(Long.valueOf(e)).atZone(ZoneId.systemDefault()).toLocalDateTime()),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.util.Date Date}.
     */
    TO_DATE((e, f) -> Date.from(LocalDateTime.parse(e, DateTimeFormatter.ofPattern(f)).atZone(ZoneId.systemDefault()).toInstant())),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.time.LocalDate
     * LocalDate}.
     */
    TO_LD((e, f) -> LocalDate.parse(e, DateTimeFormatter.ofPattern(f))),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.time.LocalTime
     * LocalTime}.
     */
    TO_LT((e, f) -> LocalTime.parse(e, DateTimeFormatter.ofPattern(f))),

    /**
     * 转毫秒级时间戳 {@link java.lang.Long Long}, 再转 {@link java.time.LocalDateTime
     * LocalDateTime}.
     */
    TO_LDT((e, f) -> LocalDateTime.parse(e, DateTimeFormatter.ofPattern(f)));

    private Function<String, Object> fun;

    private BiFunction<String, String, Object> bifun;

    private TargetType(Function<String, Object> fun) {
        this.fun = fun;
    }

    private TargetType(BiFunction<String, String, Object> bifun) {
        this.bifun = bifun;
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(String e) {
        if (Objects.isNull(this.fun)) {
            throw new NullPointerException("fun");
        }
        return (T) this.fun.apply(e);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(String e, String format) {
        if (Objects.isNull(this.bifun)) {
            throw new NullPointerException("bifun");
        }
        return (T) this.bifun.apply(e, format);
    }

}
