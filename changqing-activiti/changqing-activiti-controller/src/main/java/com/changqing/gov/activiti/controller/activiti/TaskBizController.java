package com.changqing.gov.activiti.controller.activiti;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.activiti.TaskHiResDTO;
import com.changqing.gov.activiti.dto.activiti.TaskMergeResDTO;
import com.changqing.gov.activiti.dto.activiti.TaskResDTO;
import com.changqing.gov.activiti.dto.biz.TaskLeaveResDTO;
import com.changqing.gov.activiti.service.activiti.MyTaskService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.injection.core.InjectionCore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务管理
 *
 * @author wz
 * @date 2020-08-07
 */
@Api(value = "TaskBiz", tags = "")
@Slf4j
@RestController
@RequestMapping("task")
public class TaskBizController {
    @Autowired
    private MyTaskService myTaskService;
    @Autowired
    private InjectionCore injectionCore;
    /**
     * 任务转办
     *
     * @param taskId 任务id
     * @param userId 用户id
     * @return
     */
    @ApiOperation(value = "任务转办：更新任务节点assignee")
    @RequestMapping(value = "/updateAssignee", method = RequestMethod.GET)
    public R<Boolean> updateAssignee(@RequestParam(value = "taskId") String taskId, @RequestParam(value = "userId") String userId) {
        Boolean bool = myTaskService.setAssignee(taskId, userId);

        return R.success(bool);
    }

    /**
     * 获取流程当前正执行任务列表
     *
     * @param instId 流程id
     * @return
     */
    @ApiOperation(value = "获取流程当前正执行任务列表")
    @RequestMapping(value = "/getReadyTaskByInst", method = RequestMethod.GET)
    public R<List<TaskLeaveResDTO>> getReadyTaskByInst(@RequestParam(value = "instId") String instId) {
        return R.success(myTaskService.getReadyTaskByInst(instId));
    }

    /**
     * 完成当前任务
     */
    @GetMapping("completeTask")
    @ApiOperation(value = "完成当前任务")
    public R completeTask(@RequestParam(value = "taskId") String taskId) {
        myTaskService.complete(taskId);
        return R.success();
    }

    /**
     * 获取流程发起人和创建流程的人一致，并完成当前节点任务 *****别乱用
     *
     * @param instId
     * @param assigneeId
     * @return
     */
    @RequestMapping(value = "/completeTaskByInstUser", method = RequestMethod.GET)
    public R completeTaskByInstUser(@RequestParam(value = "instId") String instId, @RequestParam(value = "assigneeId") String assigneeId) {
        List<Task> tasks = myTaskService.getReadyTaskByInstUser(instId, assigneeId);
        for (Task task : tasks) {
            myTaskService.complete(task.getId());
        }
        return R.success();
    }

    /**
     * 设置任务的局部变量
     * @param taskId
     * @param map
     * @return
     */
    @ApiOperation(value = "设置任务的局部变量")
    @RequestMapping(value = "/taskVariablesLocal", method = RequestMethod.POST)
    public R taskVariablesLocal(@RequestParam(value = "taskId") String taskId, @RequestBody Map<String, Object> map) {
        myTaskService.setVariablesLocal(taskId, map);
        return R.success();
    }

    /**
     * 获取任务的局部变量
     * @param taskId
     * @return
     */
    @ApiOperation(value = "获取任务的局部变量")
    @RequestMapping(value = "/getTaskVariablesLocal", method = RequestMethod.GET)
    public Map<String, Object> getTaskVariablesLocal(@RequestParam(value = "taskId") String taskId) {
        return myTaskService.getTaskVariablesLocal(taskId);
    }

    /**
     * 根据流程实例和任务节点名获取任务ids
     * @param instId
     * @param taskName
     * @return
     */
    @ApiOperation(value = "根据流程实例和任务节点名获取任务ids")
    @RequestMapping(value = "/getTaskIdByInstTaskName", method = RequestMethod.GET)
    public List<String> getTaskIdByInstTaskName(@RequestParam(value = "instId") String instId, @RequestParam(value = "taskName") String taskName) {
        List<String> ids=new ArrayList<>();
        List<Task> tasks = myTaskService.getTaskIdByInstTaskName(instId, taskName);
        for(Task task:tasks){
            ids.add(task.getId());
        }
        return ids;
    }

    /**
     * 根据流程实例获取第一个执行任务节点Id
     * @param instId
     * @return
     */
    @ApiOperation(value = "根据流程实例获取第一个执行任务节点Id")
    @RequestMapping(value = "/getFirstTaskId", method = RequestMethod.GET)
    public String getFirstTaskId(@RequestParam(value = "instId") String instId) {
        TaskInfo firstTask = myTaskService.getFirstTask(instId);
        return firstTask.getId();
    }

    /**
     * 根据流程实例和用户获取执行中任务节点Ids
     * @param instId
     * @param userId
     * @return
     */
    @ApiOperation(value = "根据流程实例和用户获取执行中任务节点Ids")
    @RequestMapping(value = "/getTaskIdByUser", method = RequestMethod.GET)
    public List<String> getTaskIdByUser(@RequestParam(value = "instId") String instId, @RequestParam(value = "userId") String userId) {
        List<String> taskIds = new ArrayList<>();
        List<Task> tasks = myTaskService.getTaskByUser(instId, userId);
        if (tasks != null && tasks.size() > 0) {
            for (Task task : tasks) {
                taskIds.add(task.getId());
            }
        }
        return taskIds;
    }
    /**
     * 当前待办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageRunTask")
    @ApiOperation(value = "当前待办任务")
    public R pageRunTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {
        // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        // dto.getModel().setKey(baseService.getKey());
        IPage<TaskResDTO> page = myTaskService.pageDealtWithRunTasks(dto);
        injectionCore.injection(page, false);
        return R.success(page);
    }

    /**
     * 历史已办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageHiTask")
    @ApiOperation(value = "历史已办任务")
    public R pageHiTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {

        // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        // dto.getModel().setKey(baseService.getKey());
        IPage<TaskHiResDTO> page = myTaskService.pageDealtWithHiTasks(dto);
        injectionCore.injection(page, false);
        return R.success(page);
    }

    @PostMapping("pageAllTask")
    @ApiOperation(value = "待办和已办任务")
    public R pageAllTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {
        // 暂不分页
        dto.setSize(1000);

        Set<String> dates=new HashSet<>();
        IPage<TaskResDTO> pageRun = myTaskService.pageDealtWithRunTasks(dto);
        injectionCore.injection(pageRun, false);
        // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        // dto.getModel().setKey(baseService.getKey());
        IPage<TaskHiResDTO> pageHistory = myTaskService.pageDealtWithHiTasks(dto);
        injectionCore.injection(pageHistory, false);
        //遍历日期
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        for (TaskResDTO task:pageRun.getRecords()){
            dates.add(sdf.format(task.getStartTime()));
        }
        for (TaskHiResDTO task:pageHistory.getRecords()){
            dates.add(sdf.format(task.getStartTime()));
        }
        //dates.stream().sorted(Comparator.reverseOrder());
        List<String> listDates = dates.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        JSONObject result=new JSONObject();

        // 根据日期归类
        Map<String,List<TaskMergeResDTO>> linkedMap=new LinkedHashMap();
        for (String date:listDates){
            List<TaskMergeResDTO> list=new LinkedList<>();
            for (TaskResDTO task:pageRun.getRecords()){
                if (date.equals(sdf.format(task.getStartTime()))){
                    TaskMergeResDTO taskMerge=new TaskMergeResDTO();
                    taskMerge.setAssignee(task.getAssignee());
                    taskMerge.setId(task.getId());
                    taskMerge.setTaskLocalVariables(task.getTaskLocalVariables());
                    taskMerge.setInst(task.getInst());
                    taskMerge.setType(0);
                    taskMerge.setName(task.getName());
                    taskMerge.setProcessVariables(task.getProcessVariables());
                    taskMerge.setCuser(task.getCuser());
                    taskMerge.setStartTime(task.getStartTime());
                    taskMerge.setTenantId(task.getTenantId());
                    taskMerge.setTaskDefKey(task.getTaskDefKey());
                    list.add(taskMerge);
                }
            }
            for (TaskHiResDTO task:pageHistory.getRecords()){
                if (date.equals(sdf.format(task.getStartTime()))){
                    TaskMergeResDTO taskMerge=new TaskMergeResDTO();
                    taskMerge.setAssignee(task.getAssignee());
                    taskMerge.setId(task.getId());
                    taskMerge.setTaskLocalVariables(task.getTaskLocalVariables());
                    taskMerge.setInst(task.getInst());
                    taskMerge.setType(1);
                    taskMerge.setName(task.getName());
                    taskMerge.setProcessVariables(task.getProcessVariables());
                    taskMerge.setStartTime(task.getStartTime());
                    taskMerge.setTenantId(task.getTenantId());
                    taskMerge.setTaskDefKey(task.getTaskDefKey());
                    list.add(taskMerge);
                }
            }
            linkedMap.put(date,list);
        }

        result.put("data",linkedMap);
        result.put("runTaskCount",pageRun.getTotal());
        result.put("completedTaskCount",pageHistory.getTotal());
        result.put("taskCount",pageRun.getTotal()+pageHistory.getTotal());
        return R.success(result);
    }


    /**
     *
     * @param dto
     * @return
     */
    @PostMapping("pageHistoryAllTask")
    @ApiOperation(value = "获取所有人任务节点列表-测试时使用")
    public R pageHistoryAllTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {

        // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        // dto.getModel().setKey(baseService.getKey());
        IPage<TaskHiResDTO> page = myTaskService.pageHistoryAllTask(dto);
        injectionCore.injection(page, false);
        return R.success(page);
    }
}

