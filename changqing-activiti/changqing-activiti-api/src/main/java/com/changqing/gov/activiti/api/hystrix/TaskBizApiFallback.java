package com.changqing.gov.activiti.api.hystrix;

import com.changqing.gov.activiti.api.InstanceBizApi;
import com.changqing.gov.activiti.api.TaskBizApi;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.activiti.InstantSelectSaveDTO;
import com.changqing.gov.activiti.dto.biz.TaskLeaveResDTO;
import com.changqing.gov.base.R;
import com.changqing.gov.base.request.PageParams;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TaskBizApiFallback implements TaskBizApi {

    @Override
    public R completeTask(String taskId) {
        return null;
    }
//
    @Override
    public R<List<TaskLeaveResDTO>> getReadyTaskByInst(String instId) {
        return null;
    }
//
    @Override
    public R<Boolean> updateAssignee(String taskId, String userId) {
        return null;
    }
//
    @Override
    public R completeTaskByInstUser(String instId, String assigneeId) {
        return null;
    }

    @Override
    public R taskVariablesLocal(String taskId, Map<String, Object> map) {
        return null;
    }

    @Override
    public List<String> getTaskIdByInstTaskName(String instId, String taskName) {
        return null;
    }

    @Override
    public Map<String, Object> getTaskVariablesLocal(String taskId) {
        return null;
    }

    @Override
    public String getFirstTaskId(String instId) {
        return null;
    }

    @Override
    public List<String> getTaskIdByUser(String instId, String userId) {
        return null;
    }

    @Override
    public R pageRunTask(PageParams<InstantSelectReqDTO> dto) {
        return null;
    }

    @Override
    public R pageHiTask(PageParams<InstantSelectReqDTO> dto) {
        return null;
    }

    @Override
    public R pageHistoryAllTask(PageParams<InstantSelectReqDTO> dto) {
        return null;
    }

    @Override
    public R pageAllTask(PageParams<InstantSelectReqDTO> dto) {
        return null;
    }
}
