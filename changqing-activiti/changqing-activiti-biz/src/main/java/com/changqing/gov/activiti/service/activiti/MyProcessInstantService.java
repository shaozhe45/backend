package com.changqing.gov.activiti.service.activiti;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.domain.core.ProcessAuthEntity;
import com.changqing.gov.activiti.dto.activiti.InstantSelectReqDTO;
import com.changqing.gov.activiti.dto.activiti.ProcessInstanceResDTO;
import com.changqing.gov.activiti.exception.MyActivitiExceptionCode;
import com.changqing.gov.activiti.exception.MyException;
import com.changqing.gov.activiti.utils.CollHelper;
import com.changqing.gov.authority.api.UserBizApi;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.base.R;
import com.changqing.gov.base.entity.SuperEntity;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.model.RemoteData;
import com.changqing.gov.utils.MapHelper;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.HistoryServiceImpl;
import org.activiti.engine.impl.IdentityServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.TaskServiceImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程示例业务
 *
 * @author wz
 * @date 2020-08-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MyProcessInstantService {

    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final IdentityService identityService;
    private final UserBizApi userBizApi;

    /**
     * 保存实体
     *
     * @param entity    保存实体
     * @param key       流程定义key
     * @param variables 流程变量
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends SuperEntity> ProcessInstance add(T entity, String key, Map<String, Object> variables) {

        identityService.setAuthenticatedUserId(String.valueOf(entity.getCreateUser()));
        String bussKey = String.valueOf(entity.getId());
        try {
            System.out.println("key="+key+" bussKey="+bussKey+" variables="+variables.toString()+" Tenant="+BaseContextHandler.getTenant());
            return runtimeService.startProcessInstanceByKeyAndTenantId(key, bussKey, variables, BaseContextHandler.getTenant());
        } catch (ActivitiException ex) {
            System.out.println(ex.getMessage());

            MyActivitiExceptionCode processinstInsertErr = MyActivitiExceptionCode.PROCESSINST_INSERT_ERR;
            processinstInsertErr.build(processinstInsertErr.getMsg()+":"+ex.getMessage());
            MyException.exception(processinstInsertErr);
            return null;
        }
    }

    /**
     * 分页查询实例信息
     */
    @Transactional(rollbackFor = Exception.class)
    public IPage<ProcessInstanceResDTO> page(PageParams<InstantSelectReqDTO> pageParams) {
        IPage<ProcessInstanceResDTO> page = pageParams.buildPage().setRecords(new ArrayList());
        InstantSelectReqDTO params = pageParams.getModel();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (StrUtil.isNotEmpty(params.getName())) {
            processInstanceQuery.processInstanceName(params.getName());
        }
        if (StrUtil.isNotEmpty(params.getKey())) {
            processInstanceQuery.processInstanceBusinessKey(params.getKey());
        }
        processInstanceQuery.processInstanceTenantId(BaseContextHandler.getTenant());

        List<ProcessInstance> processInstances = processInstanceQuery.listPage((int) page.offset(), (int) page.getSize());

        List<ProcessInstanceResDTO> res = new ArrayList<>();
        processInstances.forEach(obj -> res.add(ProcessInstanceResDTO.builder()
                .id(obj.getId())
                .tenantId(obj.getTenantId())
                .startTime(obj.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .businessKey(obj.getBusinessKey())
                .name(obj.getName())
                .deploymentId(obj.getDeploymentId())
                .processDefinitionId(obj.getProcessDefinitionId())
                .processDefinitionKey(obj.getProcessDefinitionKey())
                .processDefinitionName(obj.getProcessDefinitionName())
                .rootProcessInstanceId(obj.getRootProcessInstanceId())
                .procInstId(obj.getProcessInstanceId())
                .suspendState(obj.isSuspended() ? "2" : "1")
                //.value(obj.getProcessVariables())
                .value(runtimeService.getVariables(obj.getId()))
                .build()));

        // 查询相关变量
//        if (CollUtil.isNotEmpty(res)) {
//            Set<String> ids = res.stream().map(ProcessInstanceResDTO::getId).collect(Collectors.toSet());
//            List<HistoricVariableInstance> executionsVar = historyService.createHistoricVariableInstanceQuery().executionIds(ids).list();
//            res.forEach(obj -> executionsVar.forEach(exe -> {
//                if (exe.getProcessInstanceId().equals(obj.getId())) {
//                    obj.getValue().put(exe.getVariableName(), exe.getValue());
//                }
//            }));
//        }

        page.setTotal(processInstanceQuery.count());
        page.setRecords(res);
        return page;
    }

    /**
     * 获取实例详情
     *
     * @param instantId 实例id
     */
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstanceResDTO getDetail(String instantId) {
        ProcessInstance obj = runtimeService.createProcessInstanceQuery().processInstanceId(instantId).singleResult();
        if (obj == null) {
            MyException.exception(MyActivitiExceptionCode.DATA_NOT_FOUNT);
        }

        ProcessInstanceResDTO res = ProcessInstanceResDTO.builder()
                .id(obj.getId())
                .tenantId(obj.getTenantId())
                //.startUser(new RemoteData<>(Long.valueOf(obj.getStartUserId())))
                .startTime(obj.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .businessKey(obj.getBusinessKey())
                .name(obj.getName())
                .deploymentId(obj.getDeploymentId())
                .processDefinitionId(obj.getProcessDefinitionId())
                .processDefinitionKey(obj.getProcessDefinitionKey())
                .processDefinitionName(obj.getProcessDefinitionName())
                .rootProcessInstanceId(obj.getRootProcessInstanceId())
                .procInstId(obj.getProcessInstanceId())
//                .value(new HashMap<>(16))
                .value(runtimeService.getVariables(obj.getId()))
                .build();

//        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(instantId).list();
//        list.forEach(exe -> {
//            if (exe.getProcessInstanceId().equals(res.getId())) {
//                res.getValue().put(exe.getVariableName(), exe.getValue());
//            }
//        });
        return res;
    }

    /**
     * 修改流程实例状态
     *
     * @param id           实例id
     * @param suspendState 状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void suspendOrActiveApply(String id, String suspendState) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(id)
                .processInstanceTenantId(BaseContextHandler.getTenant()).singleResult();

        if (instance == null) {
            MyException.exception(MyActivitiExceptionCode.LOGIN_HAVE_NOT_AUTH);
        }

        if ("1".equals(suspendState)) {
            runtimeService.suspendProcessInstanceById(id);
        } else if ("2".equals(suspendState)) {
            runtimeService.activateProcessInstanceById(id);
        }
    }

    /**
     * 删除流程实例
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteProcessInstantByIds(List<String> ids) {
        int counter = 0;
        for (String instId : ids) {
            runtimeService.deleteProcessInstance(instId, "");
            counter++;
        }
        return counter;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean isOver(String instId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(instId).singleResult();

        if (pi == null) {
            log.info("流程已经结束");
            return true;
        } else {
            log.info("流程没有结束");
            return false;
        }
    }

    /**
     * 设置流程实例变量
     * @param instId
     * @param map
     */
    public void setProcessInstanceVariables(String instId,Map<String,Object> map){
            runtimeService.setVariables(instId,map);
    }
    public void setProcessInstanceVariable(String instId,Map<String,Object> map){
        for (String key:map.keySet()){
            runtimeService.setVariable(instId,key,map.get(key));
        }
    }
    /**
     * 获取流程实例变量
     * @param instId
     */
    public Map<String, Object> getProcessInstanceVariables(String instId){
       return runtimeService.getVariables(instId);
    }
    /**
     * 转换
     *
     * @param ids 流程实例id集合
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<Serializable, ProcessInstanceResDTO> findProInst(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<String> set = new HashSet();
        ids.forEach(id -> set.add(String.valueOf(id)));

        // 1. 根据 字典编码查询可用的字典列表
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceIds(set).list();
        List<ProcessInstanceResDTO> list = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (ProcessInstance obj : processInstances) {
            ProcessInstanceResDTO.ProcessInstanceResDTOBuilder builder = ProcessInstanceResDTO.builder();
            builder.id(obj.getId());
            builder.startTime(obj.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            if (obj.getStartUserId() != null && !"".equals(obj.getStartUserId()) && !"null".equals(obj.getStartUserId())) {
                builder.startUser(new RemoteData<>(Long.valueOf(obj.getStartUserId())));
                userIds.add(Long.valueOf(obj.getStartUserId()));
            }
            builder.businessKey(obj.getBusinessKey());
            builder.name(obj.getName());
            builder.deploymentId(obj.getDeploymentId());
            builder.processDefinitionId(obj.getProcessDefinitionId());
            builder.processDefinitionKey(obj.getProcessDefinitionKey());
            builder.processDefinitionName(obj.getProcessDefinitionName());
            builder.rootProcessInstanceId(obj.getRootProcessInstanceId());
            builder.procInstId(obj.getProcessInstanceId());
            builder.suspendState(obj.isSuspended() ? "2" : "1");
//            builder.variables(obj.getProcessVariables());
            builder.value(runtimeService.getVariables(obj.getId()));
            builder.tenantId(obj.getTenantId());

            list.add(builder.build());
        }

//        processInstances.forEach(obj -> list.add(ProcessInstanceResDTO.builder()
//                .id(obj.getId())
//                .tenantId(obj.getTenantId())
//                .startTime(obj.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
//                .startUser(new RemoteData<>(Long.valueOf(obj.getStartUserId())))
//                .businessKey(obj.getBusinessKey())
//                .name(obj.getName())
//                .deploymentId(obj.getDeploymentId())
//                .processDefinitionId(obj.getProcessDefinitionId())
//                .processDefinitionKey(obj.getProcessDefinitionKey())
//                .processDefinitionName(obj.getProcessDefinitionName())
//                .rootProcessInstanceId(obj.getRootProcessInstanceId())
//                .procInstId(obj.getProcessInstanceId())
//                .suspendState(obj.isSuspended() ? "2" : "1")
//                .value(obj.getProcessVariables())
//                .build()));

        //    List<Long> userIds = list.stream().map(inst -> inst.getStartUser().getKey()).collect(Collectors.toList());
        if (userIds.size() > 0) {
            R<List<User>> users = userBizApi.findUserById(userIds);
            if (CollUtil.isNotEmpty(users.getData())) {
                List<User> data = users.getData();
                list.forEach(inst -> data.forEach(user -> {
                    if (user.getId().equals(inst.getStartUser().getKey())) {
                        inst.getStartUser().setData(user.getName());
                    }
                }));
            }
        }

        // 2. 将 list 转换成 Map，Map的key是字典编码，value是字典名称
        ImmutableMap<String, ProcessInstanceResDTO> typeMap = CollHelper.uniqueIndex(list,
                ProcessInstanceResDTO::getId
                , (item) -> item);

        // 3. 将 Map<String, String> 转换成 Map<Serializable, Object>
        Map<Serializable, ProcessInstanceResDTO> typeCodeNameMap = new HashMap<>(CollHelper.initialCapacity(typeMap.size()));
        typeMap.forEach(typeCodeNameMap::put);
        return typeCodeNameMap;
    }

    /**
     * 转换
     *
     * @param ids 流程实例id集合
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<Serializable, ProcessInstanceResDTO> findProHiInst(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        HashSet set = new HashSet();
        ids.forEach(id -> set.add(String.valueOf(id)));

        // 1. 根据 字典编码查询可用的字典列表
        List<HistoricProcessInstance> processInstances = historyService.createHistoricProcessInstanceQuery().processInstanceIds(set).list();
        List<ProcessInstanceResDTO> list = new ArrayList<>();

        List<Long> userIds = new ArrayList<>();
        for (HistoricProcessInstance obj : processInstances) {
            ProcessInstanceResDTO.ProcessInstanceResDTOBuilder builder = ProcessInstanceResDTO.builder();
            builder.id(obj.getId());
            builder.startTime(obj.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            if (obj.getStartUserId() != null && !"".equals(obj.getStartUserId()) && !"null".equals(obj.getStartUserId())) {
                builder.startUser(new RemoteData<>(Long.valueOf(obj.getStartUserId())));
                userIds.add(Long.valueOf(obj.getStartUserId()));
            }
            builder.businessKey(obj.getBusinessKey());
            builder.name(obj.getName());
            builder.deploymentId(obj.getDeploymentId());
            builder.processDefinitionId(obj.getProcessDefinitionId());
            builder.processDefinitionKey(obj.getProcessDefinitionKey());
            builder.processDefinitionName(obj.getProcessDefinitionName());
           // builder.rootProcessInstanceId(obj.getRootProcessInstanceId());
            builder.procInstId(obj.getId());
           // builder.suspendState(obj.isSuspended() ? "2" : "1");
            builder.value(getHisProcessInstanceVariables(obj.getId()));
            builder.tenantId(obj.getTenantId());
            list.add(builder.build());
        }

      if (userIds.size()>0) {
          R<List<User>> users = userBizApi.findUserById(userIds);
          if (CollUtil.isNotEmpty(users.getData())) {
              List<User> data = users.getData();
              list.forEach(inst -> data.forEach(user -> {
                  if (user.getId().equals(inst.getStartUser().getKey())) {
                      inst.getStartUser().setData(user.getName());
                  }
              }));
          }
      }
        // 2. 将 list 转换成 Map，Map的key是字典编码，value是字典名称
        ImmutableMap<String, ProcessInstanceResDTO> typeMap = CollHelper.uniqueIndex(list,
                ProcessInstanceResDTO::getId
                , (item) -> item);

        // 3. 将 Map<String, String> 转换成 Map<Serializable, Object>
        Map<Serializable, ProcessInstanceResDTO> typeCodeNameMap = new HashMap<>(CollHelper.initialCapacity(typeMap.size()));
        typeMap.forEach(typeCodeNameMap::put);
        return typeCodeNameMap;
    }

    /**
     * 获取历史流程变量  (来源 act_hi_varinst)
     * @param instId
     * @return
     */
    public Map<String, Object> getHisProcessInstanceVariables(String instId){
        List<HistoricVariableInstance> InsVarlist = historyService.createHistoricVariableInstanceQuery().processInstanceId(instId).list();
        Map<String, Object> proVar = new HashMap<>();
        if (InsVarlist!=null &&InsVarlist.size()>0) {
            for (HistoricVariableInstance hvi : InsVarlist) {
                proVar.put(hvi.getVariableName(), hvi.getValue());
            }
        }
        return proVar;
    }
}
