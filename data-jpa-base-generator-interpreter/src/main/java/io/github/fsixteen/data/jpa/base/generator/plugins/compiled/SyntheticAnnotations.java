package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;

import io.github.fsixteen.data.jpa.base.generator.annotations.GroupInfo;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ArrayMergeMode;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.Constant;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.OptionSwitch;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.TargetType;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CollectionPolicy;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Compare;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Expr;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.ExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprArg;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.NestedExprFunction;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateOptions;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.PredicateRef;

/**
 * compiled 主链路中的合成注解工厂。
 *
 * <p>
 * 某些能力在注解层会以“快捷字段 + 运行期补全”的方式存在，例如 `InTable` 默认等值叶子、
 * `Cases` 分支内联 canonical 比较。这些场景最终仍要落回统一注解规格，因此集中用这里构造
 * 最小可用的 synthetic annotation，避免各处重复手写匿名实现。
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
final class SyntheticAnnotations {

    private static final ExprArg[] EMPTY_EXPR_ARGS = new ExprArg[0];

    private static final NestedExprArg[] EMPTY_NESTED_EXPR_ARGS = new NestedExprArg[0];

    private static final Expr[] EMPTY_EXPRS = new Expr[0];

    private static final String[] EMPTY_SCOPES = new String[0];

    private static final GroupInfo[] EMPTY_GROUPS = new GroupInfo[0];

    private static final ExprFunction EMPTY_FUNCTION = new ExprFunction() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return ExprFunction.class;
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public Class<?> type() {
            return Object.class;
        }

        @Override
        public ExprArg[] args() {
            return EMPTY_EXPR_ARGS;
        }

    };

    private static final NestedExprFunction EMPTY_NESTED_FUNCTION = new NestedExprFunction() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return NestedExprFunction.class;
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public Class<?> type() {
            return Object.class;
        }

        @Override
        public NestedExprArg[] args() {
            return EMPTY_NESTED_EXPR_ARGS;
        }

    };

    private static final Expr VALUE_EXPR = expr(ExprType.VALUE, "", "", "", Object.class, EMPTY_FUNCTION);

    private static final Expr AUTO_EXPR = expr(ExprType.AUTO, "", "", "", Object.class, EMPTY_FUNCTION);

    private static final PredicateRef EMPTY_PREDICATE_REF = new PredicateRef() {

        @Override
        public Class<?> predicateClass() {
            return Void.class;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return PredicateRef.class;
        }

    };

    private static final CollectionPolicy EMPTY_COLLECTION_POLICY = new CollectionPolicy() {

        @Override
        public PredicateRef predicate() {
            return EMPTY_PREDICATE_REF;
        }

        @Override
        public boolean split() {
            return false;
        }

        @Override
        public String decollator() {
            return Constant.DECOLLATOR;
        }

        @Override
        public String regexp() {
            return "";
        }

        @Override
        public TargetType targetType() {
            return TargetType.DEFAULT;
        }

        @Override
        public String targetFormat() {
            return "";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return CollectionPolicy.class;
        }

    };

    private static final PredicateOptions EMPTY_PREDICATE_OPTIONS = new PredicateOptions() {

        @Override
        public String[] scope() {
            return EMPTY_SCOPES;
        }

        @Override
        public ArrayMergeMode scopeMode() {
            return ArrayMergeMode.INHERIT;
        }

        @Override
        public GroupInfo[] groups() {
            return EMPTY_GROUPS;
        }

        @Override
        public ArrayMergeMode groupsMode() {
            return ArrayMergeMode.INHERIT;
        }

        @Override
        public boolean required() {
            return false;
        }

        @Override
        public OptionSwitch requiredMode() {
            return OptionSwitch.DEFAULT;
        }

        @Override
        public boolean not() {
            return false;
        }

        @Override
        public OptionSwitch notMode() {
            return OptionSwitch.DEFAULT;
        }

        @Override
        public boolean ignoreNull() {
            return false;
        }

        @Override
        public OptionSwitch ignoreNullMode() {
            return OptionSwitch.DEFAULT;
        }

        @Override
        public boolean ignoreEmpty() {
            return false;
        }

        @Override
        public OptionSwitch ignoreEmptyMode() {
            return OptionSwitch.DEFAULT;
        }

        @Override
        public boolean ignoreBlank() {
            return false;
        }

        @Override
        public OptionSwitch ignoreBlankMode() {
            return OptionSwitch.DEFAULT;
        }

        @Override
        public boolean trim() {
            return false;
        }

        @Override
        public OptionSwitch trimMode() {
            return OptionSwitch.DEFAULT;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return PredicateOptions.class;
        }

    };

    private static final Compare AUTO_COMPARE = compare(CompareOp.EQ, AUTO_EXPR, AUTO_EXPR, EMPTY_EXPRS);

    private SyntheticAnnotations() {
    }

    /**
     * 构造一条 path 类型的合成表达式。
     */
    static Expr pathExpr(final String path) {
        return expr(ExprType.PATH, path, "", "", Object.class, EMPTY_FUNCTION);
    }

    /**
     * 构造一条 {@code length(path)} 形式的合成函数表达式。
     */
    static Expr lengthExpr(final String path) {
        return expr(ExprType.FUNCTION, "", "", "", Object.class, function("length", Integer.class, pathArg(path)));
    }

    /**
     * 返回默认的 value 表达式。
     */
    static Expr valueExpr() {
        return VALUE_EXPR;
    }

    /**
     * 返回默认的 auto 表达式。
     */
    static Expr autoExpr() {
        return AUTO_EXPR;
    }

    private static ExprFunction function(final String name, final Class<?> type, final ExprArg... args) {
        return new ExprFunction() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ExprFunction.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public Class<?> type() {
                return type;
            }

            @Override
            public ExprArg[] args() {
                return null == args ? EMPTY_EXPR_ARGS : args.clone();
            }

        };
    }

    private static ExprArg pathArg(final String path) {
        return new ExprArg() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ExprArg.class;
            }

            @Override
            public ExprType type() {
                return ExprType.PATH;
            }

            @Override
            public String path() {
                return path;
            }

            @Override
            public String valueField() {
                return "";
            }

            @Override
            public String literal() {
                return "";
            }

            @Override
            public Class<?> javaType() {
                return Object.class;
            }

            @Override
            public NestedExprFunction function() {
                return EMPTY_NESTED_FUNCTION;
            }

        };
    }

    /**
     * 构造一条最小可用的合成 {@link Expr} 注解实例。
     */
    private static Expr expr(final ExprType type, final String path, final String valueField, final String literal, final Class<?> javaType,
        final ExprFunction function) {
        return new Expr() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Expr.class;
            }

            @Override
            public ExprType type() {
                return type;
            }

            @Override
            public String path() {
                return path;
            }

            @Override
            public String valueField() {
                return valueField;
            }

            @Override
            public String literal() {
                return literal;
            }

            @Override
            public Class<?> javaType() {
                return javaType;
            }

            @Override
            public ExprFunction function() {
                return function;
            }

        };
    }

    /**
     * 构造一条最小可用的合成 {@link Compare} 注解实例。
     */
    static Compare compare(final CompareOp op, final Expr left, final Expr right, final Expr[] extra) {
        return new Compare() {

            @Override
            public CompareOp op() {
                return op;
            }

            @Override
            public CollectionPolicy collection() {
                return EMPTY_COLLECTION_POLICY;
            }

            @Override
            public Expr left() {
                return left;
            }

            @Override
            public Expr right() {
                return right;
            }

            @Override
            public Expr[] extra() {
                return null == extra ? EMPTY_EXPRS : extra.clone();
            }

            @Override
            public PredicateOptions options() {
                return EMPTY_PREDICATE_OPTIONS;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Compare.class;
            }

        };
    }

    /**
     * 返回默认的 auto compare。
     */
    static Compare autoCompare() {
        return AUTO_COMPARE;
    }

    /**
     * 返回空集合策略注解实例。
     */
    static CollectionPolicy emptyCollectionPolicy() {
        return EMPTY_COLLECTION_POLICY;
    }

    /**
     * 返回空 predicate 引用注解实例。
     */
    static PredicateRef emptyPredicateRef() {
        return EMPTY_PREDICATE_REF;
    }

    /**
     * 返回空公共选项注解实例。
     */
    static PredicateOptions emptyPredicateOptions() {
        return EMPTY_PREDICATE_OPTIONS;
    }

}
