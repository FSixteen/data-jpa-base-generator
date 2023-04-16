package io.github.fsixteen.data.jpa.generator.beans.groups;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 分组统计实体.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class GroupEntity {

    private Tuple tuple;

    public GroupEntity() {
    }

    public GroupEntity(Object key, Object value) {
        this.tuple = new Tuple(key, value);
    }

    public GroupEntity(Object key, Object key2, Object value) {
        this.tuple = new Tuple2(key, key2, value);
    }

    public GroupEntity(Object key, Object key2, Object key3, Object value) {
        this.tuple = new Tuple3(key, key2, key3, value);
    }

    public GroupEntity(Object key, Object key2, Object key3, Object key4, Object value) {
        this.tuple = new Tuple4(key, key2, key3, key4, value);
    }

    public GroupEntity(Object key, Object key2, Object key3, Object key4, Object key5, Object value) {
        this.tuple = new Tuple5(key, key2, key3, key4, key5, value);
    }

    public GroupEntity(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object value) {
        this.tuple = new Tuple6(key, key2, key3, key4, key5, key6, value);
    }

    public GroupEntity(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object key7, Object value) {
        this.tuple = new Tuple7(key, key2, key3, key4, key5, key6, key7, value);
    }

    public GroupEntity(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object key7, Object key8, Object value) {
        this.tuple = new Tuple8(key, key2, key3, key4, key5, key6, key7, key8, value);
    }

    public GroupEntity(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object key7, Object key8, Object key9, Object value) {
        this.tuple = new Tuple9(key, key2, key3, key4, key5, key6, key7, key8, key9, value);
    }

    public Tuple getTuple() {
        return tuple;
    }

    public Object getKey() {
        return this.tuple.getKey();
    }

    @Nullable
    public Object getKey2() {
        return Tuple2.class.isInstance(this.tuple) ? Tuple2.class.cast(this.tuple).getKey2() : null;
    }

    @Nullable
    public Object getKey3() {
        return Tuple3.class.isInstance(this.tuple) ? Tuple3.class.cast(this.tuple).getKey3() : null;
    }

    @Nullable
    public Object getKey4() {
        return Tuple4.class.isInstance(this.tuple) ? Tuple4.class.cast(this.tuple).getKey4() : null;
    }

    @Nullable
    public Object getKey5() {
        return Tuple5.class.isInstance(this.tuple) ? Tuple5.class.cast(this.tuple).getKey5() : null;
    }

    @Nullable
    public Object getKey6() {
        return Tuple6.class.isInstance(this.tuple) ? Tuple6.class.cast(this.tuple).getKey6() : null;
    }

    @Nullable
    public Object getKey7() {
        return Tuple7.class.isInstance(this.tuple) ? Tuple7.class.cast(this.tuple).getKey7() : null;
    }

    @Nullable
    public Object getKey8() {
        return Tuple8.class.isInstance(this.tuple) ? Tuple8.class.cast(this.tuple).getKey8() : null;
    }

    public Object getKey9() {
        return Tuple9.class.isInstance(this.tuple) ? Tuple9.class.cast(this.tuple).getKey9() : null;
    }

    public Object getValue() {
        return this.tuple.getValue();
    }

    public Object[] toArray() {
        return this.tuple.toArray();
    }

    public static Collector<GroupEntity, ?, ?> toMap(int offset) {
        Collector<GroupEntity, ?, ?> coll = groupingBy(offset);
        for (int index = offset; index > 1; index--) {
            coll = groupingBy(offset, coll);
        }
        return coll;
    }

    private static Collector<GroupEntity, ?, ?> groupingBy(int offset, Collector<GroupEntity, ?, ?> coll) {
        switch (offset) {
            case 2:
                return Collectors.groupingBy(it -> dataCleaning(it.getKey()), coll);
            case 3:
                return Collectors.groupingBy(it -> dataCleaning(it.getKey2()), coll);
            case 4:
                return Collectors.groupingBy(it -> dataCleaning(it.getKey3()), coll);
            case 5:
                return Collectors.groupingBy(it -> dataCleaning(it.getKey4()), coll);
            case 6:
                return Collectors.groupingBy(it -> dataCleaning(it.getKey5()), coll);
            case 7:
                return Collectors.groupingBy(it -> dataCleaning(it.getKey6()), coll);
            case 8:
                return Collectors.groupingBy(it -> dataCleaning(it.getKey7()), coll);
            default:
                return coll;
        }
    }

    private static Collector<GroupEntity, ?, Map<Object, Object>> groupingBy(int offset) {
        switch (offset) {
            case 2:
                return Collectors.toMap(it -> dataCleaning(it.getKey2()), GroupEntity::getValue);
            case 3:
                return Collectors.toMap(it -> dataCleaning(it.getKey3()), GroupEntity::getValue);
            case 4:
                return Collectors.toMap(it -> dataCleaning(it.getKey4()), GroupEntity::getValue);
            case 5:
                return Collectors.toMap(it -> dataCleaning(it.getKey5()), GroupEntity::getValue);
            case 6:
                return Collectors.toMap(it -> dataCleaning(it.getKey6()), GroupEntity::getValue);
            case 7:
                return Collectors.toMap(it -> dataCleaning(it.getKey7()), GroupEntity::getValue);
            case 8:
                return Collectors.toMap(it -> dataCleaning(it.getKey8()), GroupEntity::getValue);
            case 9:
                return Collectors.toMap(it -> dataCleaning(it.getKey9()), GroupEntity::getValue);
            default:
                return Collectors.toMap(it -> dataCleaning(it.getKey()), GroupEntity::getValue);
        }
    }

    private static Object dataCleaning(Object value) {
        return Objects.nonNull(value) ? value : "null";
    }

}
