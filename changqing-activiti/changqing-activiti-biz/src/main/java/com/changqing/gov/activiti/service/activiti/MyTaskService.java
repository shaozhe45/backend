package com.changqing.gov.activiti.service.activiti;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.activiti.TaskHiResDTO;
import com.changqing.gov.activiti.dto.activiti.TaskResDTO;
import com.changqing.gov.activiti.exception.MyActivitiExceptionCode;
import com.changqing.gov.activiti.exception.MyException;
import com.changqing.gov.authority.api.UserBizApi;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.injection.core.InjectionCore;
import com.changqing.gov.model.RemoteData;
import com.changqing.gov.utils.StrHelper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.HistoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程示例业务
 *
 * @author wz
 * @date 2020-08-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MyTaskService {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final UserBizApi userBizApi;
    @Autowired
    private InjectionCore injectionCore;
    @Autowired
    private MyProcessInstantService myProcessInstantService;

    /**
     * 查找已处理的任务
     *
     * @param pageParams 分页参数
     */
    @Transactional(readOnly = true)
    public IPage<TaskHiResDTO> pageDealtWithHiTasks(PageParams<InstantSelectReqDTO> pageParams) {
        IPage<TaskHiResDTO> page = pageParams.buildPage().setRecords(new ArrayList());
        InstantSelectReqDTO data = pageParams.getModel();
        String userId = data.getUserId();
        if (BaseContextHandler.getUserIdStr()!=null&&!"".equals(BaseContextHandler.getUserIdStr())){
            userId=BaseContextHandler.getUserIdStr();
        }

        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskTenantId(BaseContextHandler.getTenant());
        if (data.getKey() != null && StringUtils.isNotBlank(data.getKey())) {
            query.processDefinitionKey(data.getKey());
        }
        query.taskAssignee(userId)
                .finished()
                .orderByTaskCreateTime().desc();
        if (StrUtil.isNotEmpty(data.getName())) {
            query.taskNameLike(StrHelper.fullLike(data.getName()));
        }
        if (StrUtil.isNotEmpty(data.getCreatedStartDate())) {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                query.taskCreatedAfter(sdf.parse(data.getCreatedStartDate()));
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        if (StrUtil.isNotEmpty(data.getCreatedEndDate())) {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                query.taskCreatedBefore(sdf.parse(data.getCreatedEndDate()));
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        if (StrUtil.isNotEmpty(data.getCompletedStartDate())) {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                query.taskCompletedAfter(sdf.parse(data.getCompletedStartDate()));
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        if (StrUtil.isNotEmpty(data.getCompletedEndDate())) {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                query.taskCompletedBefore(sdf.parse(data.getCompletedEndDate()));
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        page.setTotal(query.count());
        if (page.getTotal() > 0) {
            List<HistoricTaskInstance> todoList = query.listPage((int) page.offset(), (int) page.getSize());
            List<TaskHiResDTO> tasks = new ArrayList<>();

            todoList.forEach(task -> tasks.add(getHiTaskRes(task)));
            page.setRecords(tasks);
        }
        return page;
    }

    /**
     * 获取所有流程任务
     * @param pageParams
     * @return
     */
    @Transactional(readOnly = true)
    public IPage<TaskHiResDTO> pageHistoryAllTask(PageParams<InstantSelectReqDTO> pageParams) {
        IPage<TaskHiResDTO> page = pageParams.buildPage().setRecords(new ArrayList());
        InstantSelectReqDTO data = pageParams.getModel();

        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskTenantId(BaseContextHandler.getTenant());
        if (data.getKey() != null && StringUtils.isNotBlank(data.getKey())) {
            query.processDefinitionKey(data.getKey());
        }
        if (data.getInstId() != null && StringUtils.isNotBlank(data.getInstId())) {
            query.processDefinitionId(data.getInstId());
        }
        if (data.getAssignee() != null && StringUtils.isNotBlank(data.getAssignee())) {
            query.taskAssignee(data.getAssignee());
        }
        if (data.getCandidateUser() != null && StringUtils.isNotBlank(data.getCandidateUser())) {
            query.taskCandidateUser(data.getCandidateUser());
        }
        if (data.getCandidateGroup() != null && StringUtils.isNotBlank(data.getCandidateGroup())) {
            query.taskCandidateGroup(data.getCandidateGroup());
        }
        query.orderByTaskCreateTime().asc();
        if (StrUtil.isNotEmpty(data.getName())) {
            query.taskNameLike(StrHelper.fullLike(data.getName()));
        }
//        query.taskCompletedBefore()
//        query.taskCreatedAfter()

        page.setTotal(query.count());
        if (page.getTotal() > 0) {
            List<HistoricTaskInstance> todoList = query.listPage((int) page.offset(), (int) page.getSize());
            List<TaskHiResDTO> tasks = new ArrayList<>();
            todoList.forEach(task -> tasks.add(getHiTaskRes(task)));
            page.setRecords(tasks);
        }
        return page;
    }


    /**
     * 查找待办理的任务
     *
     * @param pageParams 分页实体
     */
    @Transactional(readOnly = true)
    public <T extends TaskResDTO> IPage<T> pageDealtWithRunTasks(PageParams<InstantSelectReqDTO> pageParams) {
        IPage<T> page = pageParams.buildPage().setRecords(new ArrayList());
        InstantSelectReqDTO data = pageParams.getModel();

        if (BaseContextHandler.getUserIdStr()!=null&&!"".equals(BaseContextHandler.getUserIdStr())){
            data.setUserId(BaseContextHandler.getUserIdStr());
        }

        List<T> tasks = new ArrayList<>();
        TaskQuery query = taskService.createTaskQuery().taskTenantId(BaseContextHandler.getTenant());
        if (data.getKey() != null && StringUtils.isNotBlank(data.getKey())) {
            query.processDefinitionKey(data.getKey());
        }
        query.taskCandidateOrAssigned(data.getUserId())
                .orderByTaskCreateTime().desc();

        if (StrUtil.isNotEmpty(data.getName())) {
            query.taskNameLike(StrHelper.fullLike(data.getName()));
        }
        if (StrUtil.isNotEmpty(data.getCreatedStartDate())) {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                query.taskCreatedAfter(sdf.parse(data.getCreatedStartDate()));
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        if (StrUtil.isNotEmpty(data.getCreatedEndDate())) {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                query.taskCreatedBefore(sdf.parse(data.getCreatedEndDate()));
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }

        long total = query.count();
        page.setTotal(total);
        if (total > 0) {
            List<Task> todoList = query.listPage((int) page.offset(), (int) page.getSize());
            todoList.forEach(task -> tasks.add(getTaskRes(task)));
            page.setRecords(tasks);
        }
        return page;
    }

    /**
     * 设置绑定任务流程变量
     *
     * @param taskId 任务
     * @param map    流程变量
     */
    @Transactional(rollbackFor = Exception.class)
    public void setVariablesLocal(String taskId, Map<String, Object> map) {
        try {
            taskService.setVariablesLocal(taskId, map);
        } catch (ActivitiException ex) {
            MyException.exception(MyActivitiExceptionCode.TASK_UPDATE_ERR);
        }
    }

    /**
     * 返回单个执行中的任务节点的变量； 已经处理的任务变量要被删除。
     * @param taskId
     * @return
     */
    public Map<String, Object> getTaskVariablesLocal(String taskId) {
        return taskService.getVariablesLocal(taskId);
    }

    /**
     * 返回已办任务的变量  (来源 act_hi_varinst)
     * @param taskId
     * @return
     */
    public Map<String, Object> getHisTaskVariablesLocal(String taskId) {
        List<HistoricVariableInstance> taskVarlist = historyService.createHistoricVariableInstanceQuery().taskId(taskId).list();
        Map<String, Object> taskVar = new HashMap<>();
        if (taskVarlist!=null &&taskVarlist.size()>0) {
            for (HistoricVariableInstance hvi : taskVarlist) {
                taskVar.put(hvi.getVariableName(), hvi.getValue());
            }
        }
        return taskVar;
    }
    /**
     * 设置流程变量
     *
     * @param taskId 任务id
     * @param key    流程变量KEY
     * @param value  流程变量值
     */
    @Transactional(rollbackFor = Exception.class)
    public void setVariables(String taskId, String key, String value) {
        taskService.setVariable(taskId, key, value);
    }

    /**
     * 完成任务
     *
     * @param taskId 任务id
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(String taskId) {
        taskService.complete(taskId);

    }

    /**
     * 完成当前，并为下一任务节点设置变量
     * @param taskId
     * @param variables
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(String taskId,Map<String, Object> variables) {
        taskService.complete(taskId,variables);
    }
    /**
     * 指派事项代理人
     *
     * @param taskId   任务id
     * @param assignee 用户id
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean setAssignee(String taskId, String assignee) {
        taskService.setAssignee(taskId, assignee);
        return true;
    }

    /**
     * 删除流程变量
     *
     * @param instId 流程id
     * @param key    流程变量key
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeVariable(String instId, String key) {
        runtimeService.removeVariable(instId, key);
    }

    public List<Task> getCurrentTaskByInst(String instId)
    {
        List<Task> task = taskService.createTaskQuery().processInstanceId(instId).list();
        return task;
    }
    /**
     * 获取流程当前正执行任务
     *
     * @param instId 流程id
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends TaskResDTO> List<T> getReadyTaskByInst(String instId) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(instId).list();
        List<T> res = new ArrayList<>();
        task.forEach(obj -> res.add(getTaskRes(obj)));
        if (CollUtil.isNotEmpty(res)) {
            List<Long> ids = res.stream().map(o -> Long.valueOf(o.getAssignee())).collect(Collectors.toList());
            List<User> users = userBizApi.findUserById(new ArrayList<Long>() {{
                addAll(ids);
            }}).getData();
            if (CollUtil.isNotEmpty(users)) {
                res.forEach(obj -> users.forEach(user -> {
                    if (String.valueOf(user.getId()).equals(obj.getAssignee())) {
                        obj.setCuser(user);
                    }
                }));
            }
        }
        return res;
    }
    @Transactional(rollbackFor = Exception.class)
    public List<Task> getReadyTaskByInstUser(String instId,String assigneeId) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(instId).taskAssignee(assigneeId).list();

        return task;
    }

    /**
     *
     * @param instId
     * @param taskName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Task> getTaskIdByInstTaskName(String instId,String taskName) {
        return taskService.createTaskQuery().processInstanceId(instId).taskName(taskName).list();
    }
    private <T extends TaskHiResDTO> T getHiTaskRes(HistoricTaskInstance task) {
        TaskHiResDTO res = new TaskHiResDTO();
        res.setId(task.getId());
        res.setName(task.getName());
        res.setTenantId(task.getTenantId());
        res.setTaskDefKey(task.getTaskDefinitionKey());
        res.setInst(new RemoteData(task.getProcessInstanceId()));
        res.setItem(new RemoteData(task.getId()));
        res.setAssignee(task.getAssignee());
        res.setStartTime(task.getStartTime());
        res.setProcessVariables(myProcessInstantService.getHisProcessInstanceVariables(task.getProcessInstanceId()));
        res.setTaskLocalVariables(getHisTaskVariablesLocal(task.getId()));
      //  injectionCore.injection(res);
        return (T) res;
    }

    private <T extends TaskResDTO> T getTaskRes(Task task) {
        if (task == null) {
            return null;
        }
        TaskResDTO res = new TaskResDTO();
        res.setId(task.getId());
        res.setName(task.getName());
        res.setTenantId(task.getTenantId());
        res.setIsSuspended(task.isSuspended());
        res.setTaskDefKey(task.getTaskDefinitionKey());
        res.setInst(new RemoteData(task.getProcessInstanceId()));
        res.setAssignee(task.getAssignee());
        res.setStartTime(task.getCreateTime());
        //res.setProcessVariables(task.getProcessVariables());
        res.setProcessVariables(runtimeService.getVariables(task.getProcessInstanceId()));
        res.setTaskLocalVariables(taskService.getVariablesLocal(task.getId()));
       // injectionCore.injection(res);
        return (T) res;
    }

    /**
     * 获取第一个任务节点信息
     * @param instId
     * @return
     */
    public TaskInfo getFirstTask(String instId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(instId).orderByTaskCreateTime().asc().list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }
    public List<Task> getTaskByUser(String instId,String userId) {
        List<Task> result =new ArrayList<>();
        List<Task> taskByAssignee = taskService.createTaskQuery().processInstanceId(instId).taskAssignee(userId).orderByTaskCreateTime().asc().list();
        List<Task> taskByCandidateUser = taskService.createTaskQuery().processInstanceId(instId).taskCandidateUser(userId).orderByTaskCreateTime().asc().list();
        if (taskByAssignee!=null && taskByAssignee.size()>0){
            for (Task task:taskByAssignee){
                if (!result.contains(task)){
                    result.add(task);
                }
            }
        }
        if (taskByCandidateUser!=null && taskByCandidateUser.size()>0){
            for (Task task:taskByCandidateUser){
                if (!result.contains(task)){
                    result.add(task);
                }
            }
        }
        return result;
    }
}
