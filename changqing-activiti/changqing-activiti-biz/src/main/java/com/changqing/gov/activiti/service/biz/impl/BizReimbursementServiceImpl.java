package com.changqing.gov.activiti.service.biz.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.constant.ReimbursementVarConstant;
import com.changqing.gov.activiti.dao.biz.BizReimbursementMapper;
import com.changqing.gov.activiti.domain.core.UpdateCollEntity;
import com.changqing.gov.activiti.dto.biz.BizReimbursementPageDTO;
import com.changqing.gov.activiti.dto.biz.BizReimbursementResDTO;
import com.changqing.gov.activiti.entity.biz.BizReimbursement;
import com.changqing.gov.activiti.exception.MyActivitiExceptionCode;
import com.changqing.gov.activiti.exception.MyException;
import com.changqing.gov.activiti.service.activiti.MyProcessInstantService;
import com.changqing.gov.activiti.service.biz.BizReimbursementService;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.query.QueryWrap;
import com.changqing.gov.database.mybatis.conditions.update.LbuWrapper;
import com.changqing.gov.model.RemoteData;
import com.changqing.gov.utils.BeanPlusUtil;
import com.changqing.gov.utils.MapHelper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 报销流程
 * </p>
 *
 * @author wz
 * @date 2020-08-31
 */
@Slf4j
@Service

public class BizReimbursementServiceImpl extends SuperServiceImpl<BizReimbursementMapper, BizReimbursement> implements BizReimbursementService {
    // 报销流程key
    private String BIZ_KEY = "my_reimbursement";

    @Override
    public String getKey() {
        return this.BIZ_KEY;
    }

    @Autowired
    private MyProcessInstantService myProcessInstantService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBiz(BizReimbursement bizReimbursement) {
        bizReimbursement.setUserId(Long.valueOf(BaseContextHandler.getUserId()));
        bizReimbursement.setName(BaseContextHandler.getName());
        save(bizReimbursement);

        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(ReimbursementVarConstant.REIMBURSEMENT_USER, bizReimbursement.getName());
        map.put(ReimbursementVarConstant.REIMBURSEMENT_TYPE, bizReimbursement.getType());
        map.put(ReimbursementVarConstant.REIMBURSEMENT_REASON, bizReimbursement.getReason());
        map.put(ReimbursementVarConstant.REIMBURSEMENT_NUMBER, bizReimbursement.getNumber());
        map.put(ReimbursementVarConstant.USERNAME, bizReimbursement.getUserId());

        ProcessInstance pi = myProcessInstantService.add(bizReimbursement, BIZ_KEY, map);
        bizReimbursement.setInst(new RemoteData<>(pi.getId()));
        updateById(bizReimbursement);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBiz(UpdateCollEntity<String> entity) {
        List<String> ids = entity.getIds();
        List<BizReimbursement> bizReimbursements = listByIds(ids);

        List<String> instIds = bizReimbursements.stream().map(item -> RemoteData.getKey(item.getInst())).collect(Collectors.toList());
        int count = myProcessInstantService.deleteProcessInstantByIds(instIds);

        if (count == 0) {
            MyException.exception(MyActivitiExceptionCode.DATA_NOT_FOUNT);
        }

        bizReimbursements.forEach(obj -> obj.setIsDelete(true));
        return updateBatchById(bizReimbursements);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<BizReimbursementResDTO> pageBiz(PageParams<BizReimbursementPageDTO> params) {
        IPage<BizReimbursement> page = params.buildPage();
        BizReimbursement model = BeanPlusUtil.toBean(params.getModel(), BizReimbursement.class);
        QueryWrap<BizReimbursement> wrapper = Wraps.q(model);
        page(page, wrapper);
        IPage<BizReimbursementResDTO> res = BeanPlusUtil.toBeanPage(page, BizReimbursementResDTO.class);
        res.getRecords().forEach(obj -> obj.setInst(obj.getInst()));
        return res;
    }

    /**
     * 转换
     *
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<Serializable, BizReimbursement> findBizByInstId(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 根据 字典编码查询可用的字典列表
        LbuWrapper<BizReimbursement> wrapper = Wraps.<BizReimbursement>lbU().in(BizReimbursement::getInst, ids);
        List<BizReimbursement> list = list(wrapper);

        // 2. 将 list 转换成 Map，Map的key是字典编码，value是字典名称
        ImmutableMap<String, BizReimbursement> typeMap = MapHelper.uniqueIndex(list, item -> RemoteData.getKey(item.getInst()), item -> item);

        // 3. 将 Map<String, String> 转换成 Map<Serializable, Object>
        Map<Serializable, BizReimbursement> typeCodeNameMap = new HashMap<>(typeMap.size());
        typeMap.forEach((key, value) -> typeCodeNameMap.put(key, value));
        return typeCodeNameMap;
    }
}
