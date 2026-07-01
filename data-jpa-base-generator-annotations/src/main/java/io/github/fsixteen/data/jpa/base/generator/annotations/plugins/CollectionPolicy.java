package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;

/**
 * canonical 集合值处理策略。
 *
 * <p>
 * 该注解是 compiled 主链路中“运行时值如何整理为集合”的统一输入模型，
 * 主要服务于 {@link Membership}、{@link In}、{@link NotIn}、{@link SplitIn}、
 * {@link SplitNotIn}、{@link FilterIn}、{@link FilterNotIn} 等成员判断语义。
 * </p>
 *
 * <p>
 * 一个运行时值在进入 {@code in / not-in} 比较前，通常会经历以下阶段：
 * </p>
 * <ol>
 * <li>若 {@link #split()} 为 {@code true}，先按 {@link #decollator()} 拆分字符串</li>
 * <li>若声明了 {@link #regexp()}，再按正则过滤集合元素</li>
 * <li>若声明了 {@link #targetType()} / {@link #targetFormat()}，再将每个元素转换到目标类型</li>
 * <li>若声明了 {@link #predicate()}，最后交由扩展逻辑对集合项做进一步过滤</li>
 * </ol>
 *
 * <p>
 * 例如：
 * </p>
 * 
 * <pre>{@code
 * @In(collection = @CollectionPolicy(split = true, decollator = ","))
 * private String ids = "1,2,3";
 * }</pre>
 *
 * <p>
 * 上述写法会先把字符串值 {@code "1,2,3"} 拆成集合，再参与最终的 {@code in} 比较。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
@Target({})
@Retention(RUNTIME)
@Documented
public @interface CollectionPolicy {

    /**
     * 集合项过滤扩展入口。
     *
     * <p>
     * 当内建的 split / regexp / 类型转换仍不足以描述集合预处理逻辑时，
     * 可通过该字段接入显式扩展实现，对集合项做进一步过滤或判断。
     * </p>
     *
     * <p>
     * 示例：
     * </p>
     * 
     * <pre>{@code
     * @FilterIn(collection = @CollectionPolicy(predicate = @PredicateRef(predicateClass = EnabledElementPredicate.class)))
     * private List<String> tags;
     * }</pre>
     *
     * <p>
     * 上述写法可将集合元素交给自定义 predicate，只保留满足业务规则的元素再参与比较。
     * </p>
     *
     * @return PredicateRef
     */
    PredicateRef predicate() default @PredicateRef();

    /**
     * 是否先将字符串参数拆分为集合。
     *
     * <p>
     * 该字段主要用于“参数本身是单个字符串，但语义上表示一组值”的场景。
     * 例如前端传入 {@code "1,2,3"}，而查询语义需要把它当成三个独立元素参与 {@code in} 比较。
     * </p>
     *
     * <p>
     * 示例：
     * </p>
     * 
     * <pre>{@code
     * @In(collection = @CollectionPolicy(split = true))
     * private String ids = "1,2,3";
     * }</pre>
     *
     * <p>
     * 上述写法会先把 {@code ids} 的运行时值拆成集合，再执行成员判断。
     * </p>
     *
     * @return boolean
     */
    boolean split() default false;

    /**
     * split 模式下的分隔符。
     *
     * <p>
     * 仅当 {@link #split()} 为 {@code true} 时生效，用于指定字符串拆分时使用的分隔符。
     * 默认值来自 {@link Constant#DECOLLATOR}。
     * </p>
     *
     * <p>
     * 示例：
     * </p>
     * 
     * <pre>{@code
     * @SplitIn(collection = @CollectionPolicy(split = true, decollator = "|"))
     * private String ids = "A|B|C";
     * }</pre>
     *
     * <p>
     * 上述写法会按 {@code |} 拆分，而不是使用默认分隔符。
     * </p>
     *
     * @return String
     */
    String decollator() default Constant.DECOLLATOR;

    /**
     * 集合元素正则过滤。
     *
     * <p>
     * 在 split 之后、类型转换之前，对集合元素进行正则筛选。
     * 常用于忽略空串、只保留数字、只保留固定格式编码等场景。
     * </p>
     *
     * <p>
     * 示例：
     * </p>
     * 
     * <pre>{@code
     * @SplitIn(collection = @CollectionPolicy(split = true, regexp = "\\d+"))
     * private String ids = "1,a,2,b,3";
     * }</pre>
     *
     * <p>
     * 上述写法只会保留匹配 {@code \d+} 的元素，也就是 {@code 1、2、3}。
     * </p>
     *
     * @return String
     */
    String regexp() default "";

    /**
     * split 模式下的目标值类型。
     *
     * <p>
     * 在集合元素仍是字符串时，声明每个元素应转换到的目标 Java 类型。
     * 常用于把字符串集合转换为 {@code Integer}、{@code Long}、{@code LocalDate} 等。
     * </p>
     *
     * <p>
     * 示例：
     * </p>
     * 
     * <pre>{@code
     * @SplitIn(collection = @CollectionPolicy(split = true, targetType = TargetType.INTEGER))
     * private String ids = "1,2,3";
     * }</pre>
     *
     * <p>
     * 上述写法会把拆分后的字符串元素转换为整数，再参与 {@code in} 比较。
     * </p>
     *
     * @return TargetType
     */
    TargetType targetType() default TargetType.DEFAULT;

    /**
     * split 模式下的目标格式。
     *
     * <p>
     * 当 {@link #targetType()} 对应的目标类型需要格式信息辅助解析时使用，
     * 典型场景是日期、时间、日期时间等字符串的格式化转换。
     * </p>
     *
     * <p>
     * 示例：
     * </p>
     * 
     * <pre>{@code
     * @SplitIn(collection = @CollectionPolicy(split = true, targetType = TargetType.LOCAL_DATE, targetFormat = "yyyy-MM-dd"))
     * private String days = "2026-06-01,2026-06-15";
     * }</pre>
     *
     * <p>
     * 上述写法会按 {@code yyyy-MM-dd} 把每个字符串元素解析为 {@code LocalDate}；
     * 若不提供正确格式，则日期类目标类型可能无法按预期完成转换。
     * </p>
     *
     * @return String
     */
    String targetFormat() default "";

}
