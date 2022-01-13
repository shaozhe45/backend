package com.changqing.gov.activiti.entity.activiti;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.changqing.gov.base.entity.Entity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.changqing.gov.common.constant.DictionaryType;
import static com.changqing.gov.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * <p>
 * 实体类
 *
 * </p>
 *
 * @author wz
 * @since 2020-08-11
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("ACT_HI_PROCINST")
@ApiModel(value = "Procinst", description = "")
public class Procinst {

    private static final long serialVersionUID = 1L;

    private String id;

    @ApiModelProperty(value = "")
    @NotEmpty(message = "不能为空")
    @Length(max = 64, message = "长度不能超过64")
    @TableField(value = "PROC_INST_ID_", condition = LIKE)
    @Excel(name = "")
    private String procInstId;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "BUSINESS_KEY_", condition = LIKE)
    @Excel(name = "")
    private String businessKey;

    @ApiModelProperty(value = "")
    @NotEmpty(message = "不能为空")
    @Length(max = 64, message = "长度不能超过64")
    @TableField(value = "PROC_DEF_ID_", condition = LIKE)
    @Excel(name = "")
    private String procDefId;

    @ApiModelProperty(value = "")
    @NotNull(message = "不能为空")
    @TableField("START_TIME_")
    @Excel(name = "", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "")
    @TableField("END_TIME_")
    @Excel(name = "", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime endTime;

    @ApiModelProperty(value = "")
    @TableField("DURATION_")
    @Excel(name = "")
    private Long duration;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "START_USER_ID_", condition = LIKE)
    @Excel(name = "")
    private String startUserId;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "START_ACT_ID_", condition = LIKE)
    @Excel(name = "")
    private String startActId;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "END_ACT_ID_", condition = LIKE)
    @Excel(name = "")
    private String endActId;

    @ApiModelProperty(value = "")
    @Length(max = 64, message = "长度不能超过64")
    @TableField(value = "SUPER_PROCESS_INSTANCE_ID_", condition = LIKE)
    @Excel(name = "")
    private String superProcessInstanceId;

    @ApiModelProperty(value = "")
    @Length(max = 4000, message = "长度不能超过4000")
    @TableField(value = "DELETE_REASON_", condition = LIKE)
    @Excel(name = "")
    private String deleteReason;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "TENANT_ID_", condition = LIKE)
    @Excel(name = "")
    private String tenantId;

    @ApiModelProperty(value = "")
    @Length(max = 255, message = "长度不能超过255")
    @TableField(value = "NAME_", condition = LIKE)
    @Excel(name = "")
    private String name;


    @Builder
    public Procinst(
                    String id, String procInstId, String businessKey, String procDefId, LocalDateTime startTime,
                    LocalDateTime endTime, Long duration, String startUserId, String startActId, String endActId, String superProcessInstanceId,
                    String deleteReason, String tenantId, String name) {
        this.id = id;
        this.procInstId = procInstId;
        this.businessKey = businessKey;
        this.procDefId = procDefId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.startUserId = startUserId;
        this.startActId = startActId;
        this.endActId = endActId;
        this.superProcessInstanceId = superProcessInstanceId;
        this.deleteReason = deleteReason;
        this.tenantId = tenantId;
        this.name = name;
    }

}
