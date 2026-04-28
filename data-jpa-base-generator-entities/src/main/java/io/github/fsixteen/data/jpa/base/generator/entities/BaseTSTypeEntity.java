package io.github.fsixteen.data.jpa.base.generator.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.fsixteen.common.persistence.converts.Timestamp2LocalDateTimeConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * 通用字段, 时间字段对应数据库毫秒级时间戳, BigInt(Long) 类型.<br>
 *
 * @author FSixteen
 * @since 1.0.2
 */
@MappedSuperclass
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "deleted", "createTime", "updateTime", "deleteTime" })
@Where(clause = "deleted = false")
public abstract class BaseTSTypeEntity<ID extends Serializable> implements IdEntity<ID> {

    private static final long serialVersionUID = 1L;

    @Column(name = "deleted", nullable = false, unique = false, insertable = true, updatable = true, length = 0, precision = 0, scale = 0,
        columnDefinition = "boolean NOT NULL DEFAULT 0 COMMENT '删除状态'")
    @Schema(description = "删除状态", requiredMode = RequiredMode.NOT_REQUIRED, example = "false")
    @JsonInclude(value = Include.NON_NULL)
    protected Boolean deleted = Boolean.FALSE;

    @Column(name = "create_time", nullable = false, unique = false, insertable = true, updatable = false, length = 0, precision = 3, scale = 0,
        columnDefinition = "bigint(20) NOT NULL COMMENT '创建时间'")
    @Schema(description = "创建时间(提交时忽略该参数)", requiredMode = RequiredMode.NOT_REQUIRED, example = "2020-01-01 00:00:00", type = "string",
        format = "yyyy-MM-dd HH:mm:ss")
    @Convert(converter = Timestamp2LocalDateTimeConverter.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonInclude(value = Include.NON_NULL)
    protected LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_time", nullable = false, unique = false, insertable = true, updatable = true, length = 0, precision = 3, scale = 0,
        columnDefinition = "bigint(20) NOT NULL COMMENT '最后一次更新时间'")
    @Schema(description = "最后一次更新时间(提交时忽略该参数)", requiredMode = RequiredMode.NOT_REQUIRED, example = "2020-01-01 00:00:00", type = "string",
        format = "yyyy-MM-dd HH:mm:ss")
    @Convert(converter = Timestamp2LocalDateTimeConverter.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonInclude(value = Include.NON_NULL)
    protected LocalDateTime updateTime = LocalDateTime.now();

    @Column(name = "delete_time", nullable = true, unique = false, insertable = false, updatable = true, length = 0, precision = 3, scale = 0,
        columnDefinition = "bigint(20) DEFAULT NULL COMMENT '删除时间'")
    @Schema(description = "删除时间", requiredMode = RequiredMode.NOT_REQUIRED, example = "2020-01-01 00:00:00", type = "string", format = "yyyy-MM-dd HH:mm:ss",
        accessMode = AccessMode.AUTO)
    @Convert(converter = Timestamp2LocalDateTimeConverter.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonInclude(value = Include.NON_NULL)
    protected LocalDateTime deleteTime;

    public BaseTSTypeEntity() {
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }

}
