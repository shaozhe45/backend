package com.changqing.gov.activiti.controller.biz;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.domain.core.UpdateCollEntity;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.biz.BizLeavePageDTO;
import com.changqing.gov.activiti.dto.biz.BizLeaveSaveDTO;
import com.changqing.gov.activiti.dto.biz.BizLeaveUpdateDTO;
import com.changqing.gov.activiti.dto.biz.TaskHiLeaveResDTO;
import com.changqing.gov.activiti.dto.biz.TaskLeaveResDTO;
import com.changqing.gov.activiti.entity.biz.BizLeave;
import com.changqing.gov.activiti.service.activiti.MyTaskService;
import com.changqing.gov.activiti.service.biz.BizLeaveService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.conditions.Wraps;
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


/**
 * <p>
 * 前端控制器
 * 请假流程
 * </p>
 *
 * @author wz
 * @date 2020-08-12
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/bizLeave")
@Api(value = "BizLeave", tags = "请假流程")
//@PreAuth(replace = "bizLeave:")
public class BizLeaveController extends SuperController<BizLeaveService, Long, BizLeave, BizLeavePageDTO, BizLeaveSaveDTO, BizLeaveUpdateDTO> {

    @Autowired
    private InjectionCore injectionCore;
    @Autowired
    private MyTaskService myTaskService;

    @PostMapping("save")
    public R<Boolean> save(@RequestBody BizLeave bizLeave) {
        boolean tag = baseService.saveBiz(bizLeave);
        return R.success(tag);
    }

    /**
     * 删除流程实例及模型
     *
     * @param entity 集合修改实体
     * @return
     */
    @PostMapping(value = "/delete")
    public R<Boolean> delete(@RequestBody UpdateCollEntity<String> entity) {
        if (CollUtil.isEmpty(entity.getIds())) {
            return R.fail("删除列表为空!");
        }
        Boolean tag = baseService.deleteBiz(entity);
        return R.success(tag);
    }

    /**
     * 请假实例查询
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/pageBiz")
    public R<IPage<BizLeave>> pageBiz(@RequestBody PageParams<BizLeavePageDTO> params) {
        if (params.getModel().getIsMine()) {
            params.getModel().setCreateUser(BaseContextHandler.getUserId());
        }
//      IPage<BizLeaveResDTO> page = baseService.pageBiz(params);
        IPage<BizLeave> page = params.buildPage();
        BizLeave model = BeanPlusUtil.toBean(params.getModel(), BizLeave.class);
        baseService.page(page, Wraps.lbQ(model).orderByDesc(BizLeave::getCreateTime));
        injectionCore.injection(page, false);
        return R.success(page);
    }

    /**
     * 根据业务id获取实例详情
     *
     * @param id 主键id
     * @return
     */
    @Override
    @GetMapping(value = "/get")
    public R<BizLeave> get(@RequestParam(value = "id") Long id) {
        BizLeave entity = baseService.getById(id);
        return R.success(entity);
    }

    /**
     * 当前待办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageRunTask")
    public R<IPage<TaskLeaveResDTO>> pageRunTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {
        // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        // dto.getModel().setKey(baseService.getKey());
        IPage<TaskLeaveResDTO> page = BeanPlusUtil.toBeanPage(myTaskService.pageDealtWithRunTasks(dto), TaskLeaveResDTO.class);
        page.getRecords().forEach(obj -> obj.setBiz(new RemoteData(obj.getInst().getKey())));
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
    public R<IPage<TaskHiLeaveResDTO>> pageHiTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {
       // dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
       // dto.getModel().setKey(baseService.getKey());
        IPage<TaskHiLeaveResDTO> page = BeanPlusUtil.toBeanPage(myTaskService.pageDealtWithHiTasks(dto), TaskHiLeaveResDTO.class);
        page.getRecords().forEach(obj -> obj.setBiz(new RemoteData(obj.getInst().getKey())));
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
