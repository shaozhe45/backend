package com.changqing.gov.msgs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.changqing.gov.base.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.changqing.gov.utils.DateUtils.DEFAULT_DATE_TIME_FORMAT;

/**
 * @author ：Angular
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.entity
 * @ClassName: MsgsWarningInfo
 * @date ：Created in 2021/1/8 13:36
 * @description：待处理告警实体
 * @modified By：
 * @version: v1.0.0$
 */

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("autowarning_message_detail")
@ApiModel(value = "MsgsWarningInfo", description = "待处理告警")
@AllArgsConstructor
public class MsgsWarningInfo extends Entity<Long> {

    private static final long serialVersionUID = 1L;

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
    @TableField(value = "warning_type", condition = LIKE)
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
     * 处理意见
     */
    @ApiModelProperty(value = "处理意见")
    @TableField(value = "warningContent", condition = LIKE)
    @Excel(name = "处理意见")
    private String warningContent;
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
     * 创建人ID
     */
    @ApiModelProperty(value = "创建人ID")
    @TableField(value = "create_user", condition = LIKE)
    @Excel(name = "创建人ID")
    private Long createUser;
    /**
     * 接收人姓名（接收人姓名）
     */
    @ApiModelProperty(value = "接收人姓名")
    @Excel(name = "接收人姓名")
    private String receiverUserName;
    /**
     * 补足人姓名（更新人姓名）
     */
    @ApiModelProperty(value = "更新人姓名")
    @Excel(name = "更新人姓名")
    @TableField(value = "updateUserName", condition = LIKE,exist = false)
    private String updateUserName;

    /**
     * 更新人ID
     */
    @ApiModelProperty(value = "更新人ID")
    @TableField(value = "update_user", condition = LIKE)
    @Excel(name = "更新人ID")
    private Long updateUser;

    @Builder
    public MsgsWarningInfo(Long id, Long createUser, LocalDateTime createTime, Long updateUser, String receiverUserName,LocalDateTime updateTime,
                                    String siteCode, String warningCode, String factorCode, String warningLevel, String warningType, String indexType, String messageType, String warningRule,
                                    LocalDateTime monitorTime, String messageInfor, String isDeal, String receivier, LocalDateTime sendTime, String status) {
        this.id = id;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = updateUser;
        this.receiverUserName = receiverUserName;
        this.updateTime = updateTime;
        this.siteCode = siteCode;
        this.warningCode = warningCode;
        this.factorCode = factorCode;
        this.warningLevel = warningLevel;
        this.warningType = warningType;
        this.indexType = indexType;
        this.messageType = messageType;
        this.warningRule = warningRule;
        this.monitorTime = monitorTime;
        this.messageInfor = messageInfor;
        this.isDeal = isDeal;
        this.receivier = receivier;
        this.sendTime = sendTime;
        this.status = status;
    }
}
