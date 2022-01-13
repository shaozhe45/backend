package com.changqing.gov.activiti.service.biz.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.activiti.constant.LeaveVarConstant;
import com.changqing.gov.activiti.dao.biz.BizLeaveMapper;
import com.changqing.gov.activiti.domain.core.UpdateCollEntity;
import com.changqing.gov.activiti.dto.biz.BizLeavePageDTO;
import com.changqing.gov.activiti.dto.biz.BizLeaveResDTO;
import com.changqing.gov.activiti.entity.biz.BizLeave;
import com.changqing.gov.activiti.exception.MyActivitiExceptionCode;
import com.changqing.gov.activiti.exception.MyException;
import com.changqing.gov.activiti.service.activiti.MyProcessInstantService;
import com.changqing.gov.activiti.service.biz.BizLeaveService;
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
 * 请假流程
 * </p>
 *
 * @author wz
 * @date 2020-08-12
 */
@Slf4j
@Service

public class BizLeaveServiceImpl extends SuperServiceImpl<BizLeaveMapper, BizLeave> implements BizLeaveService {
    /**
     * 请假流程key
     */
    private String BIZ_KEY = "my_leave";

    @Autowired
    private MyProcessInstantService myProcessInstantService;

    @Override
    public String getKey() {
        return this.BIZ_KEY;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveBiz(BizLeave bizLeave) {
        bizLeave.setUserId(BaseContextHandler.getUserId());
        bizLeave.setName(BaseContextHandler.getName());
        save(bizLeave);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put(LeaveVarConstant.LEAVE_USER, bizLeave.getName());
        map.put(LeaveVarConstant.START_TIME, bizLeave.getStartTime());
        map.put(LeaveVarConstant.END_TIME, bizLeave.getEndTime());
        map.put(LeaveVarConstant.LEAVE_LONG, bizLeave.getWhenLong());
        map.put(LeaveVarConstant.LEAVE_TYPE, bizLeave.getType());
        map.put(LeaveVarConstant.LEAVE_REASON, bizLeave.getReason());
        map.put(LeaveVarConstant.USERNAME, bizLeave.getUserId());

        ProcessInstance pi = myProcessInstantService.add(bizLeave, BIZ_KEY, map);
        bizLeave.setInst(new RemoteData<>(pi.getId()));
        updateById(bizLeave);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBiz(UpdateCollEntity<String> entity) {
        List<String> ids = entity.getIds();
        List<BizLeave> bizLeaves = listByIds(ids);

        List<String> instIds = bizLeaves.stream().map(item -> RemoteData.getKey(item.getInst())).collect(Collectors.toList());
        int count = myProcessInstantService.deleteProcessInstantByIds(instIds);

        if (count == 0) {
            MyException.exception(MyActivitiExceptionCode.DATA_NOT_FOUNT);
        }

        bizLeaves.forEach(obj -> obj.setIsDelete(true));
        return updateBatchById(bizLeaves);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<BizLeaveResDTO> pageBiz(PageParams<BizLeavePageDTO> params) {
        IPage<BizLeave> page = params.buildPage();
        BizLeave model = BeanPlusUtil.toBean(params.getModel(), BizLeave.class);
        QueryWrap<BizLeave> wrapper = Wraps.q(model);
        page(page, wrapper);
        IPage<BizLeaveResDTO> res = BeanPlusUtil.toBeanPage(page, BizLeaveResDTO.class);
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
    public Map<Serializable, BizLeave> findBizByInstId(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 根据 字典编码查询可用的字典列表
        LbuWrapper<BizLeave> wrapper = Wraps.<BizLeave>lbU().in(BizLeave::getInst, ids);
        List<BizLeave> list = list(wrapper);

        // 2. 将 list 转换成 Map，Map的key是字典编码，value是字典名称
        ImmutableMap<String, BizLeave> typeMap = MapHelper.uniqueIndex(list, item -> item.getInst().getKey(), item -> item);

        // 3. 将 Map<String, String> 转换成 Map<Serializable, Object>
        Map<Serializable, BizLeave> typeCodeNameMap = new HashMap<>(typeMap.size());
        typeMap.forEach((key, value) -> typeCodeNameMap.put(key, value));
        return typeCodeNameMap;
    }
}
