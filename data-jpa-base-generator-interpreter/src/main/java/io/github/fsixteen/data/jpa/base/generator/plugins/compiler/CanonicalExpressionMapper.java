package io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepNestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.DeepNestedExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprFunction;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.ExpressionCardinality;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FieldValueExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.LiteralExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression;

/**
 * canonical 注解表达式到统一表达式模型的映射器.
 *
 * <p>
 * 该类型负责把注解层的 {@code Expr / ExprArg / ExprFunction} 结构映射为
 * 运行期统一使用的 {@link PredicateExpression} 树.
 * 这是“注解 DSL”进入“内部表达式模型”的唯一桥接入口.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public final class CanonicalExpressionMapper {

    private interface ArgLike {

        ExprType type();

        String path();

        String valueField();

        String literal();

        Class<?> javaType();

        NormalizedFunction function();

    }

    private interface FunctionLike {

        String name();

        Class<?> type();

        List<NormalizedArg> args();

    }

    private interface NormalizedArg {

        ExprType type();

        String path();

        String valueField();

        String literal();

        Class<?> javaType();

        NormalizedFunction function();

    }

    private interface NormalizedFunction {

        String name();

        Class<?> type();

        List<NormalizedArg> args();

    }

    private CanonicalExpressionMapper() {
    }

    /**
     * 判断注解层表达式是否显式配置了任何有效语义.
     *
     * <p>
     * 对于 {@link ExprType#AUTO}, 只有当 path、valueField、literal 或 function 至少有一项真正配置时,
     * 才视为“已配置”；否则仍按零配置模板处理.
     * </p>
     *
     * @param expr 原始注解表达式
     * @return 表达式显式配置了有效语义时返回 {@code true}
     */
    public static boolean isConfigured(final Expr expr) {
        if (Objects.isNull(expr)) {
            return false;
        }
        if (ExprType.AUTO != expr.type()) {
            return true;
        }
        return hasConfiguredValue(expr.path(), expr.valueField(), expr.literal(), normalize(expr.function()));
    }

    /**
     * 将注解层 {@link Expr} 映射为统一的运行时表达式模型.
     *
     * <p>
     * 该方法负责处理 AUTO 类型推断、默认路径回退、字段值表达式与字面量表达式的基数适配,
     * 是注解 DSL 进入内部表达式树的主入口.
     * </p>
     *
     * @param expr        原始注解表达式
     * @param defaultPath 默认路径
     * @param cardinality 目标值基数
     * @return 映射后的表达式；零配置表达式返回 {@code null}
     */
    public static PredicateExpression expression(final Expr expr, final String defaultPath, final ExpressionCardinality cardinality) {
        if (!isConfigured(expr)) {
            return null;
        }
        ExprType type = ExprType.AUTO == expr.type() ? inferType(expr) : expr.type();
        if (ExprType.PATH == type) {
            return PathExpression.of(expr.path().isEmpty() ? defaultPath : expr.path());
        }
        if (ExprType.VALUE == type) {
            return fieldValueLiteral(cardinality, expr.valueField());
        }
        if (ExprType.VALUE_PATH == type) {
            return fieldValuePath(cardinality, expr.valueField());
        }
        if (ExprType.LITERAL == type) {
            return LiteralExpression.of(expr.literal(), literalJavaType(expr.javaType()), cardinality);
        }
        if (ExprType.FUNCTION == type) {
            return functionExpression(expr.function(), defaultPath);
        }
        return null;
    }

    /**
     * 将顶层函数定义映射为统一函数表达式.
     *
     * @param function    顶层函数定义
     * @param defaultPath 默认路径
     * @return 统一函数表达式
     */
    public static FunctionExpression functionExpression(final ExprFunction function, final String defaultPath) {
        return functionExpression(normalize(function), defaultPath);
    }

    /**
     * 将顶层函数参数定义映射为统一表达式.
     *
     * @param arg         顶层函数参数定义
     * @param defaultPath 默认路径
     * @return 统一表达式
     */
    public static PredicateExpression argExpression(final ExprArg arg, final String defaultPath) {
        return argExpression(normalize(arg), defaultPath);
    }

    /**
     * 将深层嵌套函数定义映射为统一函数表达式.
     *
     * @param function    深层嵌套函数定义
     * @param defaultPath 默认路径
     * @return 统一函数表达式
     */
    public static FunctionExpression functionExpression(final DeepNestedExprFunction function, final String defaultPath) {
        return functionExpression(normalize(function), defaultPath);
    }

    /**
     * 将深层嵌套参数定义映射为统一表达式.
     *
     * @param arg         深层嵌套参数定义
     * @param defaultPath 默认路径
     * @return 统一表达式
     */
    public static PredicateExpression argExpression(final DeepNestedExprArg arg, final String defaultPath) {
        return argExpression(normalize(arg), defaultPath);
    }

    /**
     * 将中层嵌套函数定义映射为统一函数表达式.
     *
     * @param function    中层嵌套函数定义
     * @param defaultPath 默认路径
     * @return 统一函数表达式
     */
    public static FunctionExpression functionExpression(final NestedExprFunction function, final String defaultPath) {
        return functionExpression(normalize(function), defaultPath);
    }

    /**
     * 将中层嵌套参数定义映射为统一表达式.
     *
     * @param arg         中层嵌套参数定义
     * @param defaultPath 默认路径
     * @return 统一表达式
     */
    public static PredicateExpression argExpression(final NestedExprArg arg, final String defaultPath) {
        return argExpression(normalize(arg), defaultPath);
    }

    /**
     * 将已归一化的函数定义构造成统一函数表达式.
     *
     * @param function    已归一化函数定义
     * @param defaultPath 默认路径
     * @return 统一函数表达式
     */
    private static FunctionExpression functionExpression(final NormalizedFunction function, final String defaultPath) {
        List<PredicateExpression> args = new ArrayList<PredicateExpression>();
        if (Objects.nonNull(function)) {
            for (NormalizedArg arg : function.args()) {
                args.add(argExpression(arg, defaultPath));
            }
        }
        String functionName = Objects.isNull(function) ? null : function.name();
        Class<?> returnType = Objects.isNull(function) ? Object.class : literalJavaType(function.type());
        return FunctionExpression.of(functionName, returnType, args);
    }

    /**
     * 将已归一化的函数参数定义构造成统一表达式.
     *
     * <p>
     * 当参数类型仍为 AUTO 且无法推断出显式语义时, 最终回退为默认路径表达式.
     * </p>
     *
     * @param arg         已归一化参数定义
     * @param defaultPath 默认路径
     * @return 统一表达式
     */
    private static PredicateExpression argExpression(final NormalizedArg arg, final String defaultPath) {
        if (Objects.isNull(arg)) {
            return null;
        }
        ExprType type = ExprType.AUTO == arg.type() ? inferType(arg) : arg.type();
        if (ExprType.PATH == type) {
            return PathExpression.of(arg.path().isEmpty() ? defaultPath : arg.path());
        }
        if (ExprType.VALUE == type) {
            return FieldValueExpression.literal(arg.valueField());
        }
        if (ExprType.VALUE_PATH == type) {
            return FieldValueExpression.path(arg.valueField());
        }
        if (ExprType.LITERAL == type) {
            return LiteralExpression.of(arg.literal(), literalJavaType(arg.javaType()));
        }
        if (ExprType.FUNCTION == type) {
            return functionExpression(arg.function(), defaultPath);
        }
        return PathExpression.of(defaultPath);
    }

    /**
     * 推断顶层表达式在 AUTO 模式下的真实类型.
     *
     * @param expr 原始表达式
     * @return 推断出的表达式类型
     */
    private static ExprType inferType(final Expr expr) {
        return inferType(expr.path(), expr.valueField(), expr.literal(), normalize(expr.function()));
    }

    /**
     * 推断归一化参数在 AUTO 模式下的真实类型.
     *
     * @param arg 已归一化参数
     * @return 推断出的表达式类型
     */
    private static ExprType inferType(final NormalizedArg arg) {
        return inferType(arg.path(), arg.valueField(), arg.literal(), arg.function());
    }

    /**
     * 根据 path、valueField、literal 和 function 的配置情况推断真实表达式类型.
     *
     * @param path       路径配置
     * @param valueField 字段值配置
     * @param literal    字面量配置
     * @param function   函数配置
     * @return 推断出的表达式类型；无任何配置时返回 {@link ExprType#AUTO}
     */
    private static ExprType inferType(final String path, final String valueField, final String literal, final NormalizedFunction function) {
        if (!path.isEmpty()) {
            return ExprType.PATH;
        }
        if (!valueField.isEmpty()) {
            return ExprType.VALUE;
        }
        if (!literal.isEmpty()) {
            return ExprType.LITERAL;
        }
        if (Objects.nonNull(function) && !function.name().isEmpty()) {
            return ExprType.FUNCTION;
        }
        return ExprType.AUTO;
    }

    /**
     * 判断 AUTO 表达式是否至少声明了一项有效配置.
     *
     * @param path       路径配置
     * @param valueField 字段值配置
     * @param literal    字面量配置
     * @param function   函数配置
     * @return 至少一项配置生效时返回 {@code true}
     */
    private static boolean hasConfiguredValue(final String path, final String valueField, final String literal, final NormalizedFunction function) {
        return !path.isEmpty() || !valueField.isEmpty() || !literal.isEmpty() || Objects.nonNull(function) && !function.name().isEmpty();
    }

    /**
     * 按目标基数构造“字段值作为 literal”语义的表达式.
     *
     * @param cardinality 目标基数
     * @param valueField  字段名
     * @return 对应基数的字段值 literal 表达式
     */
    private static FieldValueExpression fieldValueLiteral(final ExpressionCardinality cardinality, final String valueField) {
        if (ExpressionCardinality.RANGE == cardinality) {
            return FieldValueExpression.literalRange(valueField);
        }
        if (ExpressionCardinality.COLLECTION == cardinality) {
            return FieldValueExpression.literalCollection(valueField);
        }
        return FieldValueExpression.literal(valueField);
    }

    /**
     * 按目标基数构造“字段值作为路径”语义的表达式.
     *
     * @param cardinality 目标基数
     * @param valueField  字段名
     * @return 对应基数的字段值路径表达式
     */
    private static FieldValueExpression fieldValuePath(final ExpressionCardinality cardinality, final String valueField) {
        if (ExpressionCardinality.RANGE == cardinality) {
            return FieldValueExpression.pathRange(valueField);
        }
        if (ExpressionCardinality.COLLECTION == cardinality) {
            return FieldValueExpression.pathCollection(valueField);
        }
        return FieldValueExpression.path(valueField);
    }

    /**
     * 解析字面量表达式最终应使用的 Java 类型.
     *
     * <p>
     * 当注解层仍声明为 {@link Object} 时, 回退为 {@link String},
     * 以保持字面量解析链路具备稳定的默认类型.
     * </p>
     *
     * @param javaType 注解层声明的类型
     * @return 供字面量解析使用的最终 Java 类型
     */
    private static Class<?> literalJavaType(final Class<?> javaType) {
        return Object.class == javaType ? String.class : javaType;
    }

    /**
     * 归一化顶层函数定义.
     *
     * @param function 顶层函数定义
     * @return 归一化后的函数定义；输入为空时返回 {@code null}
     */
    private static NormalizedFunction normalize(final ExprFunction function) {
        if (Objects.isNull(function)) {
            return null;
        }
        return normalizedFunction(new FunctionLike() {

            @Override
            public String name() {
                return function.name();
            }

            @Override
            public Class<?> type() {
                return function.type();
            }

            @Override
            public List<NormalizedArg> args() {
                List<NormalizedArg> args = new ArrayList<NormalizedArg>(function.args().length);
                for (ExprArg arg : function.args()) {
                    args.add(normalize(arg));
                }
                return args;
            }

        });
    }

    /**
     * 归一化中层嵌套函数定义.
     *
     * @param function 中层嵌套函数定义
     * @return 归一化后的函数定义；输入为空时返回 {@code null}
     */
    private static NormalizedFunction normalize(final NestedExprFunction function) {
        if (Objects.isNull(function)) {
            return null;
        }
        return normalizedFunction(new FunctionLike() {

            @Override
            public String name() {
                return function.name();
            }

            @Override
            public Class<?> type() {
                return function.type();
            }

            @Override
            public List<NormalizedArg> args() {
                List<NormalizedArg> args = new ArrayList<NormalizedArg>(function.args().length);
                for (NestedExprArg arg : function.args()) {
                    args.add(normalize(arg));
                }
                return args;
            }

        });
    }

    /**
     * 归一化深层嵌套函数定义.
     *
     * @param function 深层嵌套函数定义
     * @return 归一化后的函数定义；输入为空时返回 {@code null}
     */
    private static NormalizedFunction normalize(final DeepNestedExprFunction function) {
        if (Objects.isNull(function)) {
            return null;
        }
        return normalizedFunction(new FunctionLike() {

            @Override
            public String name() {
                return function.name();
            }

            @Override
            public Class<?> type() {
                return function.type();
            }

            @Override
            public List<NormalizedArg> args() {
                List<NormalizedArg> args = new ArrayList<NormalizedArg>(function.args().length);
                for (DeepNestedExprArg arg : function.args()) {
                    args.add(normalize(arg));
                }
                return args;
            }

        });
    }

    /**
     * 归一化顶层参数定义.
     *
     * @param arg 顶层参数定义
     * @return 归一化后的参数定义；输入为空时返回 {@code null}
     */
    private static NormalizedArg normalize(final ExprArg arg) {
        if (Objects.isNull(arg)) {
            return null;
        }
        return normalizedArg(new ArgLike() {

            @Override
            public ExprType type() {
                return arg.type();
            }

            @Override
            public String path() {
                return arg.path();
            }

            @Override
            public String valueField() {
                return arg.valueField();
            }

            @Override
            public String literal() {
                return arg.literal();
            }

            @Override
            public Class<?> javaType() {
                return arg.javaType();
            }

            @Override
            public NormalizedFunction function() {
                return normalize(arg.function());
            }

        });
    }

    /**
     * 归一化中层嵌套参数定义.
     *
     * @param arg 中层嵌套参数定义
     * @return 归一化后的参数定义；输入为空时返回 {@code null}
     */
    private static NormalizedArg normalize(final NestedExprArg arg) {
        if (Objects.isNull(arg)) {
            return null;
        }
        return normalizedArg(new ArgLike() {

            @Override
            public ExprType type() {
                return arg.type();
            }

            @Override
            public String path() {
                return arg.path();
            }

            @Override
            public String valueField() {
                return arg.valueField();
            }

            @Override
            public String literal() {
                return arg.literal();
            }

            @Override
            public Class<?> javaType() {
                return arg.javaType();
            }

            @Override
            public NormalizedFunction function() {
                return normalize(arg.function());
            }

        });
    }

    /**
     * 归一化深层嵌套参数定义.
     *
     * @param arg 深层嵌套参数定义
     * @return 归一化后的参数定义；输入为空时返回 {@code null}
     */
    private static NormalizedArg normalize(final DeepNestedExprArg arg) {
        if (Objects.isNull(arg)) {
            return null;
        }
        return normalizedArg(new ArgLike() {

            @Override
            public ExprType type() {
                return arg.type();
            }

            @Override
            public String path() {
                return arg.path();
            }

            @Override
            public String valueField() {
                return arg.valueField();
            }

            @Override
            public String literal() {
                return arg.literal();
            }

            @Override
            public Class<?> javaType() {
                return arg.javaType();
            }

            @Override
            public NormalizedFunction function() {
                return null;
            }

        });
    }

    /**
     * 将任意函数形态桥接为统一的归一化函数视图.
     *
     * @param function 函数桥接视图
     * @return 统一的归一化函数视图
     */
    private static NormalizedFunction normalizedFunction(final FunctionLike function) {
        return new NormalizedFunction() {

            @Override
            public String name() {
                return function.name();
            }

            @Override
            public Class<?> type() {
                return function.type();
            }

            @Override
            public List<NormalizedArg> args() {
                return function.args();
            }

        };
    }

    /**
     * 将任意参数形态桥接为统一的归一化参数视图.
     *
     * @param arg 参数桥接视图
     * @return 统一的归一化参数视图
     */
    private static NormalizedArg normalizedArg(final ArgLike arg) {
        return new NormalizedArg() {

            @Override
            public ExprType type() {
                return arg.type();
            }

            @Override
            public String path() {
                return arg.path();
            }

            @Override
            public String valueField() {
                return arg.valueField();
            }

            @Override
            public String literal() {
                return arg.literal();
            }

            @Override
            public Class<?> javaType() {
                return arg.javaType();
            }

            @Override
            public NormalizedFunction function() {
                return arg.function();
            }

        };
    }

}
