package io.github.fsixteen.data.jpa.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 九元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Tuple9 extends Tuple8 {

    @JsonInclude(value = Include.NON_NULL)
    private Object key9;

    public Tuple9(Object key, Object key2, Object key3, Object key4, Object key5, Object key6, Object key7, Object key8, Object key9, Object value) {
        super(key, key2, key3, key4, key5, key6, key7, key8, value);
        this.key9 = key9;
    }

    public Object getKey9() {
        return key9;
    }

    public void setKey9(Object key9) {
        this.key9 = key9;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getKey3(), this.getKey4(), this.getKey5(), this.getKey6(), this.getKey7(), this.getKey8(),
            this.getKey9(), this.getValue() };
    }

}
