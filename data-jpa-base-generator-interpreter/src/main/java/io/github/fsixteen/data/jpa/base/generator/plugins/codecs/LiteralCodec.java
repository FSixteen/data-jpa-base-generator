package io.github.fsixteen.data.jpa.base.generator.plugins.codecs;

/**
 * 单一 Java 类型的字面量解析器。
 *
 * <p>
 * 该接口负责把注解或模板中的字符串字面量转换为目标 Java 类型，
 * 是 {@link LiteralCodecs} 的基础扩展点。
 * </p>
 *
 * @param <T> 目标类型
 * @author FSixteen
 * @since 1.0.3
 */
public interface LiteralCodec<T> {

    /**
     * 解析原始字面量字符串。
     *
     * @param raw 原始字符串
     * @return 目标类型值
     */
    T parse(String raw);

}
