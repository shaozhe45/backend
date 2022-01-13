package com.changqing.gov.activiti.api;
import com.changqing.gov.base.request.PageParams;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.api.hystrix.InstanceBizApiFallback;
import com.changqing.gov.activiti.api.hystrix.TaskBizApiFallback;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.activiti.InstantSelectSaveDTO;
import com.changqing.gov.activiti.dto.activiti.TaskHiResDTO;
import com.changqing.gov.activiti.dto.activiti.TaskResDTO;
import com.changqing.gov.activiti.dto.biz.TaskLeaveResDTO;
import com.changqing.gov.base.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Component
@FeignClient(name = "${changqing.feign.activiti-server:changqing-activiti-server}",fallback = TaskBizApiFallback.class
        , path = "/task", qualifier = "taskBizApi")
public interface TaskBizApi {
    /**
     * 完成任务
     *
     * @param taskId
     * @return
     */
    @GetMapping("completeTask")
    public R completeTask(@RequestParam(value = "taskId") String taskId);

    /**
     * 获取流程当前正执行任务列表
     *
     * @param instId 流程id
     * @return
     */
    @RequestMapping(value = "/getReadyTaskByInst", method = RequestMethod.GET)
    public R<List<TaskLeaveResDTO>> getReadyTaskByInst(@RequestParam(value = "instId") String instId);

    /**
     * 任务转办
     *
     * @param taskId 任务id
     * @param userId 用户id
     * @return
     */
    @RequestMapping(value = "/updateAssignee", method = RequestMethod.GET)
    public R<Boolean> updateAssignee(@RequestParam(value = "taskId") String taskId, @RequestParam(value = "userId") String userId);
//

    /**
     * 获取流程发起人 并完成当前节点任务
     *
     * @param instId
     * @param assigneeId
     * @return
     */
    @RequestMapping(value = "/completeTaskByInstUser", method = RequestMethod.GET)
    public R completeTaskByInstUser(@RequestParam(value = "instId") String instId, @RequestParam(value = "assigneeId") String assigneeId);

    @RequestMapping(value = "/taskVariablesLocal", method = RequestMethod.POST)
    public R taskVariablesLocal(@RequestParam(value = "taskId") String taskId, @RequestBody Map<String, Object> map);

    @RequestMapping(value = "/getTaskVariablesLocal", method = RequestMethod.GET)
    public Map<String, Object> getTaskVariablesLocal(@RequestParam(value = "taskId") String taskId);

    @RequestMapping(value = "/getTaskIdByInstTaskName", method = RequestMethod.GET)
    public List<String> getTaskIdByInstTaskName(@RequestParam(value = "instId") String instId, @RequestParam(value = "taskName") String taskName);
    @RequestMapping(value = "/getFirstTaskId", method = RequestMethod.GET)
    public String getFirstTaskId(@RequestParam(value = "instId") String instId);
    /**
     * 根据用户id和流程id获取任务id
     */
    @RequestMapping(value = "/getTaskIdByUser", method = RequestMethod.GET)
    public List<String> getTaskIdByUser(@RequestParam(value = "instId") String instId, @RequestParam(value = "userId") String userId);
    /**
     * 当前待办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageRunTask")
    @ApiOperation(value = "当前待办任务")
    public R pageRunTask(@RequestBody PageParams<InstantSelectReqDTO> dto) ;

    /**
     * 历史已办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageHiTask")
    @ApiOperation(value = "历史已办任务")
    public R pageHiTask(@RequestBody PageParams<InstantSelectReqDTO> dto) ;

    /**
     *
     * @param dto
     * @return
     */
    @PostMapping("pageHistoryAllTask")
    @ApiOperation(value = "获取所有人任务节点列表-测试时使用")
    public R pageHistoryAllTask(@RequestBody PageParams<InstantSelectReqDTO> dto) ;

    @PostMapping("pageAllTask")
    @ApiOperation(value = "待办和已办任务")
    public R pageAllTask(@RequestBody PageParams<InstantSelectReqDTO> dto);
}