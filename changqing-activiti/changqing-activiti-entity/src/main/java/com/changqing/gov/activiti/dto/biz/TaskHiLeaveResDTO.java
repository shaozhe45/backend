package com.changqing.gov.activiti.dto.biz;

import com.changqing.gov.activiti.dto.activiti.ProcessInstanceResDTO;
import com.changqing.gov.activiti.dto.activiti.TaskHiResDTO;
import com.changqing.gov.activiti.entity.biz.BizItem;
import com.changqing.gov.activiti.entity.biz.BizLeave;
import com.changqing.gov.injection.annonation.InjectionField;
import com.changqing.gov.model.RemoteData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动任务返回实体
 *
 * @author wz
 * @date 2020-08-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHiLeaveResDTO extends TaskHiResDTO {

    /**
     * 对应业务实例
     */
    @InjectionField(api = "bizLeaveServiceImpl", method = "findBizByInstId")
    protected RemoteData<String, BizLeave> biz;


    @Builder
    public TaskHiLeaveResDTO(String id, String name, String assignee, String tenantId, Boolean isSuspended, String taskDefKey, RemoteData<String, ProcessInstanceResDTO> inst, RemoteData<String, BizLeave> biz, RemoteData<String, BizItem> item, RemoteData<String, BizLeave> biz1) {
        this.id = id;
        this.name = name;
        this.assignee = assignee;
        this.tenantId = tenantId;
        this.isSuspended = isSuspended;
        this.taskDefKey = taskDefKey;
        this.inst = inst;
        this.item = item;
        this.biz = biz;
    }
}
