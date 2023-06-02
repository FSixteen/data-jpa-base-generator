package io.github.fsixteen.data.jpa.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 六元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Tuple6 extends Tuple5 {

    @JsonInclude(value = Include.NON_NULL)
    private Object key6;

    public Tuple6(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object value) {
        super(key, key2, key3, key4, key5, value);
        this.key6 = key6;
    }

    public Object getKey6() {
        return key6;
    }

    public void setKey6(Object key6) {
        this.key6 = key6;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getKey3(), this.getKey4(), this.getKey5(), this.getKey6(), this.getValue() };
    }

}
