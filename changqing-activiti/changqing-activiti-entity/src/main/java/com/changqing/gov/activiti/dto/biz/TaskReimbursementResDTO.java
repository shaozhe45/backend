package com.changqing.gov.activiti.dto.biz;

import com.changqing.gov.activiti.dto.activiti.ProcessInstanceResDTO;
import com.changqing.gov.activiti.dto.activiti.TaskResDTO;
import com.changqing.gov.activiti.entity.biz.BizReimbursement;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.injection.annonation.InjectionField;
import com.changqing.gov.model.RemoteData;
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
public class TaskReimbursementResDTO extends TaskResDTO {

    /**
     * 对应业务实例
     */
    @InjectionField(api = "bizReimbursementServiceImpl", method = "findBizByInstId")
    protected RemoteData<String, BizReimbursement> biz;

    @Builder
    public TaskReimbursementResDTO(String id, String name, String assignee, User cuser, String tenantId, Boolean isSuspended, String taskDefKey, RemoteData<String, ProcessInstanceResDTO> inst, RemoteData<String, BizReimbursement> biz) {
        this.id = id;
        this.name = name;
        this.assignee = assignee;
        this.cuser = cuser;
        this.tenantId = tenantId;
        this.isSuspended = isSuspended;
        this.taskDefKey = taskDefKey;
        this.inst = inst;
        this.biz = biz;
    }
}
