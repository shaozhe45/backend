package com.changqing.gov.activiti.controller.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.activiti.TaskHiResDTO;
import com.changqing.gov.activiti.dto.activiti.TaskResDTO;
import com.changqing.gov.activiti.dto.biz.*;
import com.changqing.gov.activiti.entity.biz.BizItem;
import com.changqing.gov.activiti.service.activiti.MyTaskService;
import com.changqing.gov.activiti.service.biz.BizItemService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.query.QueryWrap;
import com.changqing.gov.injection.core.InjectionCore;
import com.changqing.gov.model.RemoteData;
import com.changqing.gov.security.annotation.PreAuth;
import com.changqing.gov.utils.BeanPlusUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * <p>
 * 前端控制器
 *
 * </p>
 *
 * @author wz
 * @date 2020-08-19
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/bizItem")
@Api(value = "BizItem", tags = "")
@PreAuth(replace = "bizItem:")
public class BizItemController extends SuperController<BizItemService, Long, BizItem, BizItemPageDTO, BizItemSaveDTO, BizItemUpdateDTO> {
    @Autowired
    private MyTaskService myTaskService;
    @Autowired
    private InjectionCore injectionCore;
    /**
     * 根据任务id获取事项详情
     *
     * @param taskId
     * @return
     */
    @GetMapping(value = "/get")
    public R<BizItem> get(@RequestParam(value = "taskId") String taskId) {
        QueryWrap<BizItem> wrapper = Wraps.q(BizItem.builder().taskId(taskId).build());
        BizItem entity = baseService.getOne(wrapper, false);
        return R.success(entity);
    }

    /**
     * 根据实例id查询该任务的历史审批情况
     *
     * @param instId
     * @return
     */
    @GetMapping(value = "/find")
    public R<List<BizItemResDTO>> find(@RequestParam(value = "instId") String instId) {
        List<BizItemResDTO> list = baseService.find(instId);
        return R.success(list);
    }


    /**
     * 事项审批
     *
     * @param po
     * @return
     */
    @PostMapping(value = "/save")
    public R<BizItem> saveItem(@RequestBody BizItem po) {
        baseService.saveItem(po);

        return R.success(po);
    }

    /**
     * 当前待办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageRunTask")
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
    public R pageHiTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {

        // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        // dto.getModel().setKey(baseService.getKey());
        IPage<TaskHiResDTO> page = myTaskService.pageDealtWithHiTasks(dto);
        injectionCore.injection(page, false);
        return R.success(page);
    }

    @PostMapping("pageHistoryAllTask")
    public R pageHistoryAllTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {

        // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        // dto.getModel().setKey(baseService.getKey());
        IPage<TaskHiResDTO> page = myTaskService.pageHistoryAllTask(dto);
        injectionCore.injection(page, false);
        return R.success(page);
    }
    /**
     * 完成当前任务
     */
    @GetMapping("completeTask")
    public R completeTask(@RequestParam String taskId) {
        myTaskService.complete(taskId);
        return R.success();
    }
}
