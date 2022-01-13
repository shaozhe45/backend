package com.changqing.gov.msgs.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.changqing.gov.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.changqing.gov.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * @author ：Angular
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.dto
 * @ClassName: MsgsWarningInfoDTO
 * @date ：Created in 2021/1/8 14:01
 * @description：待处理告警DTO
 * @modified By：
 * @version: v1.0.0$
 */

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@ApiModel(value = "MsgsWarningInfo", description = "待处理告警")
@AllArgsConstructor
public class MsgsWarningInfoDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @NotNull(message = "id不能为空", groups = SuperEntity.Update.class)
    private Long id;
    /**
     * 站点编号
     */
    @ApiModelProperty(value = "站点编号")
    @Length(max = 50, message = "站点编号长度不能超过50")
    @TableField(value = "site_code", condition = LIKE)
    @Excel(name = "站点编号")
    private String siteCode;

    @ApiModelProperty(value = "告警编码")
    @Length(max = 50, message = "告警编码长度不能超过50")
    @TableField(value = "warning_code", condition = LIKE)
    @Excel(name = "告警编码")
    private String warningCode;

    @TableField(value = "site_name", condition = LIKE,exist = false)
    @Excel(name = "站点名称")
    private String siteName;

    @TableField(value = "factor_name", condition = LIKE,exist = false)
    @Excel(name = "因子名称")
    private String factorName;

    @TableField(value = "fact_max_value", condition = LIKE,exist = false)
    @Excel(name = "实际值/域值")
    private String factMaxValue;

    /**
     * 因子类型
     */
    @ApiModelProperty(value = "因子类型")
    @Length(max = 50, message = "因子类型长度不能超过50")
    @TableField(value = "factor_code", condition = LIKE)
    @Excel(name = "因子类型")
    private String factorCode;

    /**
     * 异常类型
     */
    @ApiModelProperty(value = "异常类型")
    @Length(max = 50, message = "异常类型长度不能超过50")
    @TableField(value = "warning_type", condition = LIKE)
    @Excel(name = "异常类型")
    private String warningType;

    /**
     * 异常类型
     */
    @ApiModelProperty(value = "指标类型")
    @Length(max = 50, message = "指标类型长度不能超过50")
    @TableField(value = "index_type", condition = LIKE)
    @Excel(name = "指标类型")
    private String indexType;

    /**
     * 异常等级
     */
    @ApiModelProperty(value = "异常等级")
    @Length(max = 50, message = "异常类型长度不能超过50")
    @TableField(value = "warning_level", condition = LIKE)
    @Excel(name = "异常等级")
    private String warningLevel;

    /**
     * 短信类型
     */
    @ApiModelProperty(value = "短信类型")
    @Length(max = 50, message = "短信类型长度不能超过50")
    @TableField(value = "message_type", condition = LIKE)
    @Excel(name = "短信类型")
    private String messageType;

    /**
     * 预警规则
     */
    @ApiModelProperty(value = "预警规则")
    @Length(max = 65535, message = "预警规则长度不能超过65535")
    @TableField("warning_rule")
    @Excel(name = "预警规则")
    private String warningRule;

    /**
     * 监测时间
     */
    @ApiModelProperty(value = "监测时间")
    @TableField("monitor_time")
    @Excel(name = "监测时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime monitorTime;

    /**
     * 监测时间条件查询开始时间
     */
    @ApiModelProperty(value = "监测时间条件查询开始时间")
    @Excel(name = "监测时间条件查询开始时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime monitorTimeStart;

    /**
     * 监测时间条件查询结束时间
     */
    @ApiModelProperty(value = "监测时间条件查询结束时间")
    @Excel(name = "监测时间条件查询结束时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime monitorTimeEnd;

    /**
     * 短信内容
     */
    @ApiModelProperty(value = "短信内容")

    @TableField("message_infor")
    @Excel(name = "短信内容")
    private String messageInfor;

    /**
     * 是否处理
     */
    @ApiModelProperty(value = "是否处理")
    @Length(max = 50, message = "是否处理长度不能超过50")
    @TableField(value = "is_deal", condition = LIKE)
    @Excel(name = "是否处理")
    private String isDeal;

    /**
     * 短信接收人
     */
    @ApiModelProperty(value = "短信接收人")
    @Length(max = 50, message = "短信接收人长度不能超过50")
    @TableField(value = "receivier", condition = LIKE)
    @Excel(name = "短信接收人")
    private String receivier;

    @TableField(value = "managerName", condition = LIKE,exist = false)
    @Excel(name = "短信接收人")
    private String managerName;

    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间")
    @TableField("send_time")
    @Excel(name = "发送时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime sendTime;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    @Length(max = 10, message = "是否启用长度不能超过10")
    @TableField(value = "status", condition = LIKE)
    @Excel(name = "是否启用")
    private String status;

    /**
     * 监测时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    @Excel(name = "创建时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime createTime;

    /**
     * 监测时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    @Excel(name = "更新时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime updateTime;

    /**
     * 更新时间条件查询开始时间
     */
    @ApiModelProperty(value = "更新时间条件查询开始时间")
    @Excel(name = "更新时间条件查询开始时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime updateTimeStart;

    /**
     * 更新时间条件查询结束时间
     */
    @ApiModelProperty(value = "更新时间条件查询结束时间")
    @Excel(name = "更新时间条件查询结束时间", format = DEFAULT_DATE_TIME_FORMAT, width = 20)
    private LocalDateTime updateTimeEnd;

    /**
     * 创建人ID
     */
    @ApiModelProperty(value = "创建人ID")
    @TableField(value = "create_user", condition = LIKE)
    @Excel(name = "创建人ID")
    private Long createUser;

    /**
     * 更新人ID
     */
    @ApiModelProperty(value = "更新人ID")
    @TableField(value = "updata_user", condition = LIKE)
    @Excel(name = "更新人ID")
    private Long updateUser;

    /**
     * 接收人姓名
     */
    @ApiModelProperty(value = "接收人姓名")
    @Excel(name = "接收人姓名")
    private String receivierUserName;

    /**
     * 补足人姓名（更新人姓名）
     */
    @ApiModelProperty(value = "更新人姓名")
    @Excel(name = "更新人姓名")
    private String updateUserName;
}
