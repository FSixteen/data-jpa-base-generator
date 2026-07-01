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
 * canonical 注解表达式到统一表达式模型的映射器。
 *
 * <p>
 * 该类型负责把注解层的 {@code Expr / ExprArg / ExprFunction} 结构映射为
 * 运行期统一使用的 {@link PredicateExpression} 树。
 * 这是“注解 DSL”进入“内部表达式模型”的唯一桥接入口。
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

    public static boolean isConfigured(final Expr expr) {
        if (Objects.isNull(expr)) {
            return false;
        }
        if (ExprType.AUTO != expr.type()) {
            return true;
        }
        return hasConfiguredValue(expr.path(), expr.valueField(), expr.literal(), normalize(expr.function()));
    }

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

    public static FunctionExpression functionExpression(final ExprFunction function, final String defaultPath) {
        return functionExpression(normalize(function), defaultPath);
    }

    public static PredicateExpression argExpression(final ExprArg arg, final String defaultPath) {
        return argExpression(normalize(arg), defaultPath);
    }

    public static FunctionExpression functionExpression(final DeepNestedExprFunction function, final String defaultPath) {
        return functionExpression(normalize(function), defaultPath);
    }

    public static PredicateExpression argExpression(final DeepNestedExprArg arg, final String defaultPath) {
        return argExpression(normalize(arg), defaultPath);
    }

    public static FunctionExpression functionExpression(final NestedExprFunction function, final String defaultPath) {
        return functionExpression(normalize(function), defaultPath);
    }

    public static PredicateExpression argExpression(final NestedExprArg arg, final String defaultPath) {
        return argExpression(normalize(arg), defaultPath);
    }

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

    private static ExprType inferType(final Expr expr) {
        return inferType(expr.path(), expr.valueField(), expr.literal(), normalize(expr.function()));
    }

    private static ExprType inferType(final NormalizedArg arg) {
        return inferType(arg.path(), arg.valueField(), arg.literal(), arg.function());
    }

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

    private static boolean hasConfiguredValue(final String path, final String valueField, final String literal, final NormalizedFunction function) {
        return !path.isEmpty() || !valueField.isEmpty() || !literal.isEmpty() || Objects.nonNull(function) && !function.name().isEmpty();
    }

    private static FieldValueExpression fieldValueLiteral(final ExpressionCardinality cardinality, final String valueField) {
        if (ExpressionCardinality.RANGE == cardinality) {
            return FieldValueExpression.literalRange(valueField);
        }
        if (ExpressionCardinality.COLLECTION == cardinality) {
            return FieldValueExpression.literalCollection(valueField);
        }
        return FieldValueExpression.literal(valueField);
    }

    private static FieldValueExpression fieldValuePath(final ExpressionCardinality cardinality, final String valueField) {
        if (ExpressionCardinality.RANGE == cardinality) {
            return FieldValueExpression.pathRange(valueField);
        }
        if (ExpressionCardinality.COLLECTION == cardinality) {
            return FieldValueExpression.pathCollection(valueField);
        }
        return FieldValueExpression.path(valueField);
    }

    private static Class<?> literalJavaType(final Class<?> javaType) {
        return Object.class == javaType ? String.class : javaType;
    }

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
