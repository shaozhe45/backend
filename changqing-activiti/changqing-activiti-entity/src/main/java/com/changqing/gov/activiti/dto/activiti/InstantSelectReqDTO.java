package com.changqing.gov.activiti.dto.activiti;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 流程实例查询入参
 *
 * @author wz
 * @date 2020-08-07
 */
@Data
@ApiModel(value = "InstantSelectReqDTO", description = "流程实例查询入参")
public class InstantSelectReqDTO {
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * key
     */
    @ApiModelProperty(value = "key")
    private String key;
    /**
     * 用户id(后端权限查询，传值无效)
     */
    @ApiModelProperty(value = "用户id(后端权限查询，传值无效)")
    private String userId;
    @ApiModelProperty(value = "流程实例Id")
    private  String instId;
    @ApiModelProperty(value = "流程实例Id")
    private  String assignee;
    @ApiModelProperty(value = "流程实例Id")
    private  String candidateUser;
    @ApiModelProperty(value = "流程实例Id")
    private  String candidateGroup;

    @ApiModelProperty(value = "新增开始时间，格式 yyyy-MM-dd HH:mm:ss")
    private  String createdStartDate;
    @ApiModelProperty(value = "新增结束时间，格式 yyyy-MM-dd HH:mm:ss")
    private  String createdEndDate;
    @ApiModelProperty(value = "新增开始时间，格式 yyyy-MM-dd HH:mm:ss")
    private  String completedStartDate;
    @ApiModelProperty(value = "新增结束时间，格式 yyyy-MM-dd HH:mm:ss")
    private  String completedEndDate;
}
