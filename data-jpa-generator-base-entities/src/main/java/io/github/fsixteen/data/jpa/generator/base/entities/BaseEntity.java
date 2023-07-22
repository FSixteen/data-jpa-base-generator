package io.github.fsixteen.data.jpa.generator.base.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 通用字段.<br>
 *
 * @author FSixteen
 * @since 1.0.0
 */
@MappedSuperclass
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "deleted", "createTime", "updateTime", "deleteTime" })
@Where(clause = "deleted = false")
public abstract class BaseEntity<ID extends Serializable> implements IdEntity<ID> {
    private static final long serialVersionUID = 1L;

    @Column(name = "deleted", nullable = false, unique = false, insertable = false, updatable = true, length = 0, precision = 0, scale = 0,
        columnDefinition = "boolean NOT NULL DEFAULT 0 COMMENT '删除状态'")
    @Schema(description = "主键", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "false", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "删除状态", required = false, hidden = true, example = "false", accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @JsonInclude(value = Include.NON_NULL)
    protected Boolean deleted = Boolean.FALSE;

    @Column(name = "create_time", nullable = false, unique = false, insertable = true, updatable = false, length = 0, precision = 3, scale = 0,
        columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    @Schema(description = "创建时间(提交时忽略该参数)", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "2020-01-01 00:00:00",
        type = "string", format = "yyyy-MM-dd HH:mm:ss", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "创建时间(提交时忽略该参数)", required = false, hidden = false, example = "2020-01-01 00:00:00", dataType = "string",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @CreationTimestamp
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(value = Include.NON_NULL)
    protected Date createTime;

    @Column(name = "update_time", nullable = false, unique = false, insertable = false, updatable = true, length = 0, precision = 3, scale = 0,
        columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间'")
    @Schema(description = "最后一次更新时间(提交时忽略该参数)", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "2020-01-01 00:00:00",
        type = "string", format = "yyyy-MM-dd HH:mm:ss", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "最后一次更新时间(提交时忽略该参数)", required = false, hidden = false, example = "2020-01-01 00:00:00", dataType = "string",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @UpdateTimestamp
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(value = Include.NON_NULL)
    protected Date updateTime;

    @Column(name = "delete_time", nullable = true, unique = false, insertable = false, updatable = true, length = 0, precision = 3, scale = 0,
        columnDefinition = "datetime DEFAULT NULL COMMENT '删除时间'")
    @Schema(description = "删除时间", required = false, requiredMode = RequiredMode.NOT_REQUIRED, hidden = false, example = "2020-01-01 00:00:00", type = "string",
        format = "yyyy-MM-dd HH:mm:ss", accessMode = AccessMode.AUTO)
    @ApiModelProperty(value = "删除时间", required = false, hidden = false, example = "2020-01-01 00:00:00", dataType = "string",
        accessMode = io.swagger.annotations.ApiModelProperty.AccessMode.AUTO)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonInclude(value = Include.NON_NULL)
    protected Date deleteTime;

    public BaseEntity() {
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

}
