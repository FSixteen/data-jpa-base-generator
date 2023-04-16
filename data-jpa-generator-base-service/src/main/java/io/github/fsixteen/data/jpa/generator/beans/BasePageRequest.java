package io.github.fsixteen.data.jpa.generator.beans;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页请求接口.<br>
 * 
 * @author FSixteen
 * @since V1.0.0
 */
public interface BasePageRequest extends Serializable {

    @Min(value = 0, message = "分页信息不合法")
    public int getPage();

    @Min(value = 1, message = "分页信息不合法")
    @Max(value = 9999, message = "分页信息不合法")
    public int getSize();

}
