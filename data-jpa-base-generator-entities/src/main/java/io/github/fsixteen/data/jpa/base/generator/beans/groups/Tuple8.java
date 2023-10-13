package io.github.fsixteen.data.jpa.base.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 八元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class Tuple8 extends Tuple7 {

    @JsonInclude(value = Include.NON_NULL)
    private Object key8;

    public Tuple8(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object key7, Object key8, Object value) {
        super(key, key2, key3, key4, key5, key6, key7, value);
        this.key8 = key8;
    }

    public Object getKey8() {
        return key8;
    }

    public void setKey8(Object key8) {
        this.key8 = key8;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getKey3(), this.getKey4(), this.getKey5(), this.getKey6(), this.getKey7(), this.getKey8(),
            this.getValue() };
    }

}
