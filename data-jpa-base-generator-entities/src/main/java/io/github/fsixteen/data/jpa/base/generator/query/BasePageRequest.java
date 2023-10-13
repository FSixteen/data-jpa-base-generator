package io.github.fsixteen.data.jpa.base.generator.query;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页请求接口.<br>
 * 
 * @author FSixteen
 * @since 1.0.0
 */
public interface BasePageRequest extends Serializable {

    @Min(value = 0, message = "无效分页信息, 有效值 >= {value}")
    public int getPage();

    @Min(value = 1, message = "无效页大小信息, 有效值 >= {value}")
    @Max(value = 9999, message = "无效页大小信息, 有效值 <= {value}")
    public int getSize();

}
