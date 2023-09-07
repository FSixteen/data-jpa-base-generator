package io.github.fsixteen.data.jpa.generator.utils;

import static io.github.fsixteen.data.jpa.generator.constants.Constants.ARG_POSTFIX;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.ARG_POSTFIX_LENGTH;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.ARG_PREFIX;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.ARG_PREFIX_LENGTH;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.ARG_SPLIT_POSTFIX;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.FUN_PREFIX;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.FUN_PREFIX_LENGTH;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.READONLY_POSTFIX;
import static io.github.fsixteen.data.jpa.generator.constants.Constants.READONLY_POSTFIX_LENGTH;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.generator.constants.GroupColumnType;

/**
 * 分组查询字段处理工具.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public class GroupColumnUtils {

    private GroupColumnUtils() {
    }

    public static Builder newInst() {
        return new Builder();
    }

    public static Column of(final String column) {
        return Column.of(column);
    }

    /**
     * {@link Column} 构造器.
     * 
     * @author FSixteen
     * @since 1.0.0
     */
    public static class Builder {

        List<Column> cs = new ArrayList<>();

        private Builder() {
        }

        public Builder literal(final boolean readOnly, final String literal) {
            cs.add(Column.literal(readOnly, literal));
            return this;
        }

        public Builder field(final boolean readOnly, final String field) {
            cs.add(Column.field(readOnly, field));
            return this;
        }

        public Builder fun(final boolean readOnly, final String funName, final Column... fields) {
            cs.add(Column.fun(readOnly, funName, fields));
            return this;
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            Column[] css = this.cs.toArray(new Column[this.cs.size()]);
            if (0 != css.length) {
                out.append(css[0].toString());
            }
            for (int index = 1; index < css.length; index++) {
                out.append(",");
                out.append(css[index].toString());
            }
            return out.toString();
        }

    }

    /**
     * 分组计算字段.
     * 
     * @author FSixteen
     * @since 1.0.0
     */
    public static class Column {

        private GroupColumnType type = GroupColumnType.VALUE;
        private boolean readOnly;
        private String fieldOrFun;
        private Column[] args;

        public static Column of(final String column) {
            return new Column(column);
        }

        public static Column literal(final String literal) {
            return new Column(false, GroupColumnType.VALUE, literal, null);
        }

        private static Column literal(final boolean readOnly, final String literal) {
            return new Column(readOnly, GroupColumnType.VALUE, literal, null);
        }

        public static Column field(final String field) {
            return new Column(false, GroupColumnType.FIELD, field, null);
        }

        private static Column field(final boolean readOnly, final String field) {
            return new Column(readOnly, GroupColumnType.FIELD, field, null);
        }

        public static Column fun(final String fun, final Column[] args) {
            return new Column(false, GroupColumnType.FIELD, fun, args);
        }

        private static Column fun(final boolean readOnly, final String fun, final Column[] args) {
            return new Column(readOnly, GroupColumnType.FUNCTION, fun, args);
        }

        /**
         * 格式如下: <br>
         * '[read@@]fun@@funname[::(${fieldname}|literal)][::(${fieldname}|literal)]...'
         * <br>
         * '[read@@]fieldname' <br>
         *
         * @param column 字段信息
         */
        public Column(final String column) {
            if (column.startsWith(READONLY_POSTFIX)) {
                this.readOnly = true;
            }
            String columnTemp = column.startsWith(READONLY_POSTFIX) ? column.substring(READONLY_POSTFIX_LENGTH) : column;
            if (columnTemp.startsWith(FUN_PREFIX)) {
                String[] segs = columnTemp.substring(FUN_PREFIX_LENGTH).split(ARG_SPLIT_POSTFIX);
                this.fieldOrFun = segs[0];
                LinkedList<Column> args = new LinkedList<>();
                for (int index = 1; index < segs.length; index++) {
                    if (segs[index].startsWith(ARG_PREFIX) && segs[index].endsWith(ARG_POSTFIX)) {
                        args.add(new Column(segs[index].substring(ARG_PREFIX_LENGTH, segs[index].length() - ARG_POSTFIX_LENGTH), GroupColumnType.FIELD));
                    } else {
                        args.add(new Column(segs[index], GroupColumnType.VALUE));
                    }
                }
                this.args = args.toArray(new Column[args.size()]);
                this.type = GroupColumnType.FUNCTION;
            } else if (columnTemp.startsWith(ARG_PREFIX) && columnTemp.endsWith(ARG_POSTFIX)) {
                this.fieldOrFun = columnTemp.substring(ARG_PREFIX_LENGTH, columnTemp.length() - ARG_POSTFIX_LENGTH);
                this.type = GroupColumnType.FIELD;
            } else {
                this.fieldOrFun = columnTemp;
                this.type = GroupColumnType.VALUE;
            }
        }

        public Column(final String columnm, final GroupColumnType type) {
            this(false, type, columnm, null);
        }

        private Column(final boolean readOnly, final GroupColumnType type, final String fieldOrFun, final Column[] args) {
            super();
            this.readOnly = readOnly;
            this.type = type;
            this.fieldOrFun = fieldOrFun;
            this.args = args;
        }

        private Optional<Expression<Object>> toTupleElement(final Root<?> root, final CriteriaBuilder cb) {
            switch (this.type) {
                case VALUE:
                    return Optional.ofNullable(cb.literal(this.fieldOrFun));
                case FIELD:
                    return Optional.ofNullable(root.get(this.fieldOrFun));
                default:
                    Expression<?>[] params = new Expression[this.args.length];
                    for (int index = 0; index < this.args.length; index++) {
                        Optional<Expression<Object>> arg = this.args[index].toExpression(root, cb);
                        if (arg.isPresent()) {
                            params[index] = arg.get();
                        }
                    }
                    return Optional.ofNullable(cb.function(this.fieldOrFun, Object.class, params));
            }
        }

        public Optional<Expression<Object>> toExpression(final Root<?> root, final CriteriaBuilder cb) {
            return this.readOnly ? Optional.empty() : this.toTupleElement(root, cb);
        }

        public Optional<Expression<Object>> toSelection(final Root<?> root, final CriteriaBuilder cb) {
            return this.toTupleElement(root, cb);
        }

        public GroupColumnType getType() {
            return type;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public String getFieldOrFun() {
            return fieldOrFun;
        }

        public Column[] getArgs() {
            return args;
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            if (this.readOnly) {
                out.append(READONLY_POSTFIX);
            }
            if (GroupColumnType.FUNCTION == this.type) {
                out.append(FUN_PREFIX);
            }
            if (GroupColumnType.FIELD == this.type) {
                out.append(ARG_PREFIX);
            }
            out.append(fieldOrFun);
            if (GroupColumnType.FIELD == this.type) {
                out.append(ARG_POSTFIX);
            }
            if (GroupColumnType.FUNCTION == this.type && Objects.nonNull(this.args)) {
                for (Column column : this.args) {
                    out.append(ARG_SPLIT_POSTFIX);
                    out.append(column.toString());
                }
            }
            return out.toString();
        }

    }

}
