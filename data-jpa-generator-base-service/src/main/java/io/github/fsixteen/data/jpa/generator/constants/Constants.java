package io.github.fsixteen.data.jpa.generator.constants;

/**
 * 常量.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class Constants {

    /**
     * 只读标识.
     */
    public static final String READONLY_POSTFIX = "read@@";
    /**
     * 只读标识长度.
     */
    public static final int READONLY_POSTFIX_LENGTH = READONLY_POSTFIX.length();

    /**
     * 函数标识.
     */
    public static final String FUN_PREFIX = "fun@@";
    /**
     * 函数标识长度.
     */
    public static final int FUN_PREFIX_LENGTH = FUN_PREFIX.length();

    /**
     * 函数初始位置标识.
     */
    public static final String ARG_PREFIX = "${";
    /**
     * 函数初始位置标识长度.
     */
    public static final int ARG_PREFIX_LENGTH = ARG_PREFIX.length();

    /**
     * 函数结束位置标识.
     */
    public static final String ARG_POSTFIX = "}";
    /**
     * 函数结束位置标识长度.
     */
    public static final int ARG_POSTFIX_LENGTH = ARG_POSTFIX.length();

    /**
     * 参数分割标识.
     */
    public static final String ARG_SPLIT_POSTFIX = "::";
    /**
     * 参数分割标识长度.
     */
    public static final int ARG_SPLIT_POSTFIX_LENGTH = ARG_SPLIT_POSTFIX.length();

}
