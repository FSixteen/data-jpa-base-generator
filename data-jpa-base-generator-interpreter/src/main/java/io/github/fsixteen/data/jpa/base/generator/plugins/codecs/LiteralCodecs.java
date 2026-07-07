package io.github.fsixteen.data.jpa.base.generator.plugins.codecs;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;

/**
 * 字面量解析中心.
 *
 * <p>
 * 该类型维护“Java 类型 -> {@link LiteralCodec}”映射,
 * 统一承接字符串字面量向目标类型的转换逻辑.
 * 比较注解、函数参数、集合值和范围值在需要解析固定字面量时, 都会走这里.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class LiteralCodecs {

    /**
     * 当前链路统一先落到 Java 类型, 再由 codec 负责解析. 这样后续新增注解时只需要声明目标类型,
     * 不需要复制解析逻辑.
     */
    private static final Map<Class<?>, LiteralCodec<?>> CODECS = new ConcurrentHashMap<Class<?>, LiteralCodec<?>>();

    static {
        register(String.class, new LiteralCodec<String>() {

            @Override
            public String parse(final String raw) {
                return raw;
            }

        });
        register(Boolean.class, new LiteralCodec<Boolean>() {

            @Override
            public Boolean parse(final String raw) {
                return Boolean.valueOf(raw);
            }

        });
        register(boolean.class, CODECS.get(Boolean.class));
        register(BigDecimal.class, new LiteralCodec<BigDecimal>() {

            @Override
            public BigDecimal parse(final String raw) {
                return new BigDecimal(raw);
            }

        });
        register(Double.class, new LiteralCodec<Double>() {

            @Override
            public Double parse(final String raw) {
                return Double.valueOf(raw);
            }

        });
        register(double.class, CODECS.get(Double.class));
        register(Float.class, new LiteralCodec<Float>() {

            @Override
            public Float parse(final String raw) {
                return Float.valueOf(raw);
            }

        });
        register(float.class, CODECS.get(Float.class));
        register(Short.class, new LiteralCodec<Short>() {

            @Override
            public Short parse(final String raw) {
                return Short.valueOf(raw);
            }

        });
        register(short.class, CODECS.get(Short.class));
        register(Integer.class, new LiteralCodec<Integer>() {

            @Override
            public Integer parse(final String raw) {
                return Integer.valueOf(raw);
            }

        });
        register(int.class, CODECS.get(Integer.class));
        register(Long.class, new LiteralCodec<Long>() {

            @Override
            public Long parse(final String raw) {
                return Long.valueOf(raw);
            }

        });
        register(long.class, CODECS.get(Long.class));
        register(LocalDate.class, new LiteralCodec<LocalDate>() {

            @Override
            public LocalDate parse(final String raw) {
                return LocalDate.parse(raw);
            }

        });
        register(LocalTime.class, new LiteralCodec<LocalTime>() {

            @Override
            public LocalTime parse(final String raw) {
                return LocalTime.parse(raw);
            }

        });
        register(LocalDateTime.class, new LiteralCodec<LocalDateTime>() {

            @Override
            public LocalDateTime parse(final String raw) {
                return LocalDateTime.parse(raw);
            }

        });
        register(Date.class, new LiteralCodec<Date>() {

            @Override
            public Date parse(final String raw) {
                return parseDate(raw);
            }

        });
        register(UUID.class, new LiteralCodec<UUID>() {

            @Override
            public UUID parse(final String raw) {
                return UUID.fromString(raw);
            }

        });
    }

    private LiteralCodecs() {
    }

    /**
     * 注册某个 Java 类型对应的字面量解析器.
     *
     * @param javaType 目标 Java 类型
     * @param codec    对应的解析器
     * @param <T>      类型泛型
     */
    public static <T> void register(final Class<T> javaType, final LiteralCodec<?> codec) {
        CODECS.put(javaType, codec);
    }

    /**
     * 判断当前类型是否存在可用的字面量解析器.
     *
     * @param javaType 目标 Java 类型
     * @return 当前类型可被本解析中心直接处理时返回 {@code true}
     */
    public static boolean supports(final Class<?> javaType) {
        return Objects.nonNull(resolveCodec(javaType));
    }

    /**
     * 按目标 Java 类型解析单个字符串字面量.
     *
     * <p>
     * 当目标类型为空或为 {@link Object} 时直接返回原始字符串；
     * 枚举类型走专用枚举解析分支, 其余类型通过已注册 codec 解析.
     * </p>
     *
     * @param raw      原始字符串字面量
     * @param javaType 目标 Java 类型
     * @return 解析后的目标值
     * @throws IllegalArgumentException 当目标类型没有可用 codec 时抛出
     */
    public static Object parse(final String raw, final Class<?> javaType) {
        if (Objects.isNull(javaType) || Object.class == javaType) {
            return raw;
        }
        if (javaType.isEnum()) {
            return parseEnum(raw, javaType);
        }
        LiteralCodec<?> codec = resolveCodec(javaType);
        if (Objects.isNull(codec)) {
            throw new IllegalArgumentException("Unsupported literal type: " + javaType.getName());
        }
        return codec.parse(raw);
    }

    /**
     * 按目标 Java 类型和显式格式解析单个字符串字面量.
     *
     * <p>
     * 仅日期时间类类型会使用传入格式, 其余类型仍回退到默认解析逻辑.
     * </p>
     *
     * @param raw      原始字符串字面量
     * @param javaType 目标 Java 类型
     * @param format   显式日期时间格式
     * @return 解析后的目标值
     */
    public static Object parse(final String raw, final Class<?> javaType, final String format) {
        if (Objects.isNull(format) || format.isEmpty()) {
            return parse(raw, javaType);
        }
        if (Date.class == javaType) {
            return Date.from(LocalDateTime.parse(raw, DateTimeFormatter.ofPattern(format)).atZone(ZoneId.systemDefault()).toInstant());
        }
        if (LocalDate.class == javaType) {
            return LocalDate.parse(raw, DateTimeFormatter.ofPattern(format));
        }
        if (LocalTime.class == javaType) {
            return LocalTime.parse(raw, DateTimeFormatter.ofPattern(format));
        }
        if (LocalDateTime.class == javaType) {
            return LocalDateTime.parse(raw, DateTimeFormatter.ofPattern(format));
        }
        return parse(raw, javaType);
    }

    /**
     * 解析枚举字面量.
     *
     * @param raw      原始字符串字面量
     * @param javaType 枚举类型
     * @return 对应的枚举值
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Object parseEnum(final String raw, final Class<?> javaType) {
        return Enum.valueOf((Class<? extends Enum>) javaType.asSubclass(Enum.class), raw);
    }

    /**
     * 按 epoch 毫秒值解析日期时间类目标类型.
     *
     * <p>
     * 当前仅对 {@link LocalDate}、{@link LocalTime}、{@link LocalDateTime} 做 epoch 专用转换,
     * 其余类型回退到普通字面量解析.
     * </p>
     *
     * @param raw      epoch 毫秒字符串
     * @param javaType 目标 Java 类型
     * @return 解析后的目标值
     */
    public static Object parseEpoch(final String raw, final Class<?> javaType) {
        long epochMillis = Long.parseLong(raw);
        if (LocalDate.class == javaType) {
            return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (LocalTime.class == javaType) {
            return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalTime();
        }
        if (LocalDateTime.class == javaType) {
            return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return parse(raw, javaType);
    }

    /**
     * 按分隔符拆分并解析集合字面量.
     *
     * @param raw        原始集合字面量
     * @param decollator 分隔符
     * @param javaType   集合元素目标类型
     * @return 解析后的集合值列表
     */
    public static List<Object> parseCollection(final String raw, final String decollator, final Class<?> javaType) {
        List<String> tokens = split(raw, decollator);
        List<Object> values = new ArrayList<Object>(tokens.size());
        for (String token : tokens) {
            values.add(parse(token, javaType));
        }
        return values;
    }

    /**
     * 按分隔符拆分并解析范围字面量.
     *
     * <p>
     * 当范围字面量只提供一个值时, 自动按“左右同值”补齐, 以保持 between 风格输入的兼容性.
     * </p>
     *
     * @param raw        原始范围字面量
     * @param decollator 分隔符
     * @param javaType   范围元素目标类型
     * @return 解析后的范围值列表
     */
    public static List<Object> parseRange(final String raw, final String decollator, final Class<?> javaType) {
        List<String> tokens = split(raw, decollator);
        if (tokens.isEmpty()) {
            return new ArrayList<Object>(0);
        }
        // 范围字面量只给一个值时, 按左右同值处理.
        if (1 == tokens.size()) {
            tokens.add(tokens.get(0));
        }
        List<Object> values = new ArrayList<Object>(2);
        values.add(parse(tokens.get(0), javaType));
        values.add(parse(tokens.get(1), javaType));
        return values;
    }

    /**
     * 按指定分隔符拆分原始字符串.
     *
     * <p>
     * 当调用方未显式提供分隔符时, 回退到框架默认分隔符.
     * 该方法不做 trim, 也不会丢弃空 token.
     * </p>
     *
     * @param raw        原始字符串
     * @param decollator 分隔符
     * @return 拆分后的 token 列表
     */
    public static List<String> split(final String raw, final String decollator) {
        String actualDecollator = Objects.isNull(decollator) || decollator.isEmpty() ? Constant.DECOLLATOR : decollator;
        List<String> values = new ArrayList<String>();
        if (Objects.isNull(raw) || raw.isEmpty()) {
            return values;
        }
        int start = 0;
        int index;
        while ((index = raw.indexOf(actualDecollator, start)) >= 0) {
            values.add(raw.substring(start, index));
            start = index + actualDecollator.length();
        }
        values.add(raw.substring(start));
        return values;
    }

    /**
     * 解析当前类型实际应使用的 codec.
     *
     * @param javaType 目标 Java 类型
     * @return 可用的 codec；若不存在则返回 {@code null}
     */
    private static LiteralCodec<?> resolveCodec(final Class<?> javaType) {
        LiteralCodec<?> codec = CODECS.get(javaType);
        if (Objects.nonNull(codec)) {
            return codec;
        }
        return CODECS.get(primitiveToWrapper(javaType));
    }

    /**
     * 将 primitive 类型转换为对应包装类型.
     *
     * @param javaType 原始类型
     * @return 若输入为 primitive, 则返回其包装类型；否则返回原类型
     */
    private static Class<?> primitiveToWrapper(final Class<?> javaType) {
        if (Objects.isNull(javaType) || !javaType.isPrimitive()) {
            return javaType;
        }
        if (boolean.class == javaType) {
            return Boolean.class;
        }
        if (double.class == javaType) {
            return Double.class;
        }
        if (float.class == javaType) {
            return Float.class;
        }
        if (short.class == javaType) {
            return Short.class;
        }
        if (int.class == javaType) {
            return Integer.class;
        }
        if (long.class == javaType) {
            return Long.class;
        }
        return javaType;
    }

    /**
     * 解析 {@link Date} 类型字面量.
     *
     * <p>
     * 当前解析顺序依次为：
     * 1. 纯数字 epoch 毫秒
     * 2. ISO-8601 instant
     * 3. 默认 {@link LocalDateTime} 文本
     * 4. 默认 {@link LocalDate} 文本
     * </p>
     *
     * @param raw 原始日期字面量
     * @return 解析后的 {@link Date} 值
     */
    private static Date parseDate(final String raw) {
        if (raw.matches("^(\\-|\\+)?\\d+$")) {
            return new Date(Long.parseLong(raw));
        }
        try {
            return Date.from(Instant.parse(raw));
        } catch (DateTimeParseException ignore) {
            // ignore
        }
        try {
            return Date.from(LocalDateTime.parse(raw).atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException ignore) {
            // ignore
        }
        return Date.from(LocalDate.parse(raw).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
