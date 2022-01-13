package com.changqing.gov.activiti.dto.activiti;

import com.changqing.gov.activiti.entity.biz.BizItem;
import com.changqing.gov.activiti.entity.biz.BizLeave;
import com.changqing.gov.injection.annonation.InjectionField;
import com.changqing.gov.model.RemoteData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 历史任务返回实体
 *
 * @author wz
 * @date 2020-08-07
 */
@Data
@NoArgsConstructor
@ApiModel(value = "TaskHiResDTO", description = "历史任务返回实体")
public class TaskHiResDTO {
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    protected String id;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    protected String name;

    /**
     * 审批人id
     */
    @ApiModelProperty(value = "审批人id")
    protected String assignee;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    protected String tenantId;

    /**
     * 是否挂起
     */
    @ApiModelProperty(value = "是否挂起")
    protected Boolean isSuspended;

    /**
     * 对应定义key
     */
    @ApiModelProperty(value = "对应定义key")
    protected String taskDefKey;

    /**
     * 对应流程实例
     */
    @ApiModelProperty(value = "对应流程实例")
    @InjectionField(api = "myProcessInstantService", method = "findProHiInst")
    protected RemoteData<String, ProcessInstanceResDTO> inst;

    @ApiModelProperty(value = "开始时间")
    protected Date startTime;
    @ApiModelProperty(value = "任务变量")
    protected Map<String, Object> taskLocalVariables;
    @ApiModelProperty(value = "流程变量")
    protected Map<String, Object> processVariables;
    /**
     * 对应业务实例
     */
    @ApiModelProperty(value = "对应业务实例")
    //@InjectionField(api = "bizItemServiceImpl", method = "findItemByTaskId")
    protected RemoteData<String, BizItem> item;

    public TaskHiResDTO(String id, String name, String assignee, String tenantId, Boolean isSuspended, String taskDefKey, RemoteData<String, ProcessInstanceResDTO> inst,  RemoteData<String, BizItem> item) {
        this.id = id;
        this.name = name;
        this.assignee = assignee;
        this.tenantId = tenantId;
        this.isSuspended = isSuspended;
        this.taskDefKey = taskDefKey;
        this.inst = inst;
        this.item = item;
    }

}
