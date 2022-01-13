package com.changqing.gov.activiti.controller.activiti;

import com.changqing.gov.activiti.domain.core.ProcessAuthEntity;
import com.changqing.gov.activiti.dto.activiti.InstantSelectSaveDTO;
import com.changqing.gov.activiti.service.activiti.MyProcessInstantService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.entity.SuperEntity;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.injection.core.InjectionCore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.changqing.gov.base.R.result;
import static com.changqing.gov.base.R.success;

/**
 * 流程实例管理
 *
 * @author wz
 * @date 2020-08-07
 */
@Api(value = "InstanceBiz", tags = "")
@Slf4j
@RestController
@RequestMapping("instance")
public class InstanceBizController {
    @Autowired
    private MyProcessInstantService myProcessInstantService;


    /**
     * 新增流程实例
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "创建新实例")
    @PostMapping(value = "/save")
    public String save(@RequestBody InstantSelectSaveDTO dto) {
        SuperEntity entity = new SuperEntity();
        entity.setId(dto.getBussKey());
        return myProcessInstantService.add(entity, dto.getKey(), dto.getVariables()).getId();
    }
    @PostMapping(value = "/saveInstant")
    public String saveInstant(@RequestBody InstantSelectSaveDTO dto) {
        SuperEntity entity = new SuperEntity();
        entity.setId(dto.getBussKey());
        entity.setCreateUser(dto.getCreateUser());
        return myProcessInstantService.add(entity, dto.getKey(), dto.getVariables()).getId();
    }

    /**
     * 修改流程实例状态
     *
     * @param instId       实例ID
     * @param suspendState 修改状态
     * @return
     */
    @ApiOperation(value = "修改流程实例状态")
    @GetMapping(value = "/updateSuspendStateInst")
    public R<Boolean> updateSuspendStateInst(@RequestParam(value = "instId") String instId, @RequestParam(value = "suspendState") String suspendState) {

        myProcessInstantService.suspendOrActiveApply(instId, suspendState);
        return success(true);
    }

    /**
     * 设置流程实例变量(覆盖模式)
     * @param instId
     * @param map
     * @return
     */
    @ApiOperation(value = "设置流程实例变量(覆盖模式)")
    @RequestMapping(value = "/processInstanceVariables", method = RequestMethod.POST)
    public R processInstanceVariables(@RequestParam(value = "instId") String instId,@RequestBody Map<String,Object> map) {
        myProcessInstantService.setProcessInstanceVariables(instId,map);
        return R.success();
    }

    /**
     * 设置流程实例变量(追加模式)
     * @param instId
     * @param map
     * @return
     */
    @ApiOperation(value = "设置流程实例变量(追加模式)")
    @RequestMapping(value = "/processInstanceAppendVariables", method = RequestMethod.POST)
    public R processInstanceAppendVariables(@RequestParam(value = "instId") String instId,@RequestBody Map<String,Object> map) {
        myProcessInstantService.setProcessInstanceVariable(instId,map);
        return R.success();
    }

    /**
     * 获取流程实例变量
     * @param instId
     * @return
     */
    @ApiOperation(value = "获取流程实例变量")
    @RequestMapping(value = "/getProcessInstanceVariables", method = RequestMethod.GET)
    public Map<String, Object> getProcessInstanceVariables(@RequestParam(value = "instId") String instId) {
        return myProcessInstantService.getProcessInstanceVariables(instId);
    }
}
