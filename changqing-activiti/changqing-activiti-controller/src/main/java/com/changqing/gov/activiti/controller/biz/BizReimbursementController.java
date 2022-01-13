package com.changqing.gov.activiti.controller.biz;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.domain.core.UpdateCollEntity;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.biz.BizReimbursementPageDTO;
import com.changqing.gov.activiti.dto.biz.BizReimbursementResDTO;
import com.changqing.gov.activiti.dto.biz.BizReimbursementSaveDTO;
import com.changqing.gov.activiti.dto.biz.BizReimbursementUpdateDTO;
import com.changqing.gov.activiti.dto.biz.TaskHiReimbursementResDTO;
import com.changqing.gov.activiti.dto.biz.TaskReimbursementResDTO;
import com.changqing.gov.activiti.entity.biz.BizReimbursement;
import com.changqing.gov.activiti.service.activiti.MyProcessDefinitionService;
import com.changqing.gov.activiti.service.activiti.MyTaskService;
import com.changqing.gov.activiti.service.biz.BizReimbursementService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.context.BaseContextHandler;
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
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * 报销流程
 * </p>
 *
 * @author wz
 * @date 2020-08-31
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/bizReimbursement")
@Api(value = "BizReimbursement", tags = "报销流程")
@PreAuth(replace = "bizReimbursement:")
public class BizReimbursementController extends SuperController<BizReimbursementService, Long, BizReimbursement, BizReimbursementPageDTO, BizReimbursementSaveDTO, BizReimbursementUpdateDTO> {
    @Autowired
    private InjectionCore injectionCore;

    @Autowired
    private MyProcessDefinitionService definitionService;
    @Autowired
    private MyTaskService myTaskService;


    /**
     * Excel导入后的操作
     *
     * @param list
     */
    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<BizReimbursement> bizReimbursementList = list.stream().map((map) -> {
            BizReimbursement bizReimbursement = BizReimbursement.builder().build();
            return bizReimbursement;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(bizReimbursementList));
    }


    /**
     * 新增报销流程
     *
     * @param bizReimbursement 新增实体
     * @return
     */
    @PostMapping("save")
    public R<Boolean> save(@RequestBody BizReimbursement bizReimbursement) {
        boolean tag = baseService.saveBiz(bizReimbursement);
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
     * @param params 分页模糊查询参数
     * @return
     */
    @PostMapping(value = "/pageBiz")
    public R<IPage<BizReimbursementResDTO>> pageBiz(@RequestBody PageParams<BizReimbursementPageDTO> params) {
        if (params.getModel().getIsMine()) {
            params.getModel().setCreateUser(Long.valueOf(BaseContextHandler.getUserId()));
        }
        IPage<BizReimbursementResDTO> page = baseService.pageBiz(params);
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
    public R<BizReimbursement> get(@RequestParam(value = "id") Long id) {
        BizReimbursement entity = baseService.getById(id);
        return R.success(entity);
    }

    /**
     * 当前待办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageRunTask")
    public R<IPage<TaskReimbursementResDTO>> pageRunTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {
        dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        dto.getModel().setKey(baseService.getKey());
        IPage<TaskReimbursementResDTO> page = BeanPlusUtil.toBeanPage(myTaskService.pageDealtWithRunTasks(dto), TaskReimbursementResDTO.class);
        page.getRecords().forEach(obj -> obj.setBiz(new RemoteData(obj.getInst().getKey())));
        injectionCore.injection(page, false);
        return R.success(page);
    }

    /**
     * 当前待办任务
     *
     * @param dto
     * @return
     */
    @PostMapping("pageHiTask")
    public R<IPage<TaskHiReimbursementResDTO>> pageHiTask(@RequestBody PageParams<InstantSelectReqDTO> dto) {
        dto.getModel().setUserId(BaseContextHandler.getUserIdStr());
        dto.getModel().setKey(baseService.getKey());
        IPage<TaskHiReimbursementResDTO> page = BeanPlusUtil.toBeanPage(myTaskService.pageDealtWithHiTasks(dto), TaskHiReimbursementResDTO.class);
        page.getRecords().forEach(obj -> obj.setBiz(new RemoteData(obj.getInst().getKey())));
        injectionCore.injection(page, false);
        return R.success(page);
    }
}
