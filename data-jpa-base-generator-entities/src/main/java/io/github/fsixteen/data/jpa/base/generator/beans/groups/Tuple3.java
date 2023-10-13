package io.github.fsixteen.data.jpa.base.generator.beans.groups;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 三元元组分组.<br>
 * 包含固定分组统计值.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer", "handler" })
public class Tuple3 extends Tuple2 {

    @JsonInclude(value = Include.NON_NULL)
    private Object key3;

    public Tuple3(Object key, Object key2, Object key3, Object value) {
        super(key, key2, value);
        this.key3 = key3;
    }

    public Object getKey3() {
        return key3;
    }

    public void setKey3(Object key3) {
        this.key3 = key3;
    }

    @Override
    public Object[] toArray() {
        return new Object[] { this.getKey(), this.getKey2(), this.getKey3(), this.getValue() };
    }

}
