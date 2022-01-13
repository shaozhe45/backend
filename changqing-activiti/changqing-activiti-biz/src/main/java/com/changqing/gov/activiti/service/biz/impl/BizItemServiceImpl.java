package com.changqing.gov.activiti.service.biz.impl;


import cn.hutool.core.collection.CollUtil;
import com.changqing.gov.activiti.constant.LeaveVarConstant;
import com.changqing.gov.activiti.constant.ResultConstant;
import com.changqing.gov.activiti.dao.biz.BizItemMapper;
import com.changqing.gov.activiti.dto.biz.BizItemResDTO;
import com.changqing.gov.activiti.entity.biz.BizItem;
import com.changqing.gov.activiti.entity.biz.BizLeave;
import com.changqing.gov.activiti.service.activiti.MyProcessInstantService;
import com.changqing.gov.activiti.service.activiti.MyTaskService;
import com.changqing.gov.activiti.service.biz.BizItemService;
import com.changqing.gov.activiti.service.biz.BizLeaveService;
import com.changqing.gov.authority.api.UserBizApi;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.base.R;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.update.LbuWrapper;
import com.changqing.gov.dozer.DozerUtils;
import com.changqing.gov.utils.MapHelper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
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
 * <p>
 * </p>
 *
 * @author wz
 * @date 2020-08-19
 */
@Slf4j
@Service

public class BizItemServiceImpl extends SuperServiceImpl<BizItemMapper, BizItem> implements BizItemService {
    @Autowired
    private MyTaskService myTaskService;

    @Autowired
    private MyProcessInstantService myProcessInstantService;

    @Autowired
    private BizLeaveService bizLeaveService;

    @Autowired
    private UserBizApi userBizApi;

    @Autowired
    private DozerUtils dozerUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveItem(BizItem po) {
        saveOrUpdate(po);
        String taskId = po.getTaskId();
        Map<String, Object> map = new LinkedHashMap<>();
        // 审批通过
        String result;
        if (po.getResult()) {
            result = ResultConstant.PASS;
        } else {
            result = ResultConstant.REJECT;
        }

        map.put(LeaveVarConstant.RESULT_MSG, BaseContextHandler.getName() + "【" + result + "】" + po.getItemRemake());
        myTaskService.setVariablesLocal(taskId, map);
        myTaskService.setVariables(taskId, LeaveVarConstant.RESULT, result);
        myTaskService.complete(taskId);

        // 判断流程是否结束
        Boolean over = myProcessInstantService.isOver(po.getInstId());
        if (over) {
            BizLeave bizLeave = BizLeave.builder().id(po.getBizId()).isOver(true).build();
            bizLeaveService.updateById(bizLeave);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BizItemResDTO> find(String instId) {
        LbuWrapper<BizItem> wrapper = Wraps.<BizItem>lbU().eq(BizItem::getInstId, instId).orderByDesc(BizItem::getCreateTime);
        List<BizItemResDTO> list = dozerUtils.mapList(list(wrapper), BizItemResDTO.class);

        List<Long> userIds = list.stream().map(inst -> inst.getCreateUser()).collect(Collectors.toList());

        R<List<User>> users = userBizApi.findUserById(userIds);
        if (CollUtil.isNotEmpty(users.getData())) {
            List<User> data = users.getData();
            list.forEach(inst -> data.forEach(user -> {
                if (user.getId().equals(inst.getCreateUser())) {
                    inst.setCUser(user);
                }
            }));
        }
        return list;
    }

    /**
     * 转换
     *
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<Serializable, BizItem> findItemByTaskId(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 根据 字典编码查询可用的字典列表
        LbuWrapper<BizItem> wrapper = Wraps.<BizItem>lbU().in(BizItem::getTaskId, ids);
        List<BizItem> list = list(wrapper);

        // 2. 将 list 转换成 Map，Map的key是字典编码，value是字典名称
        ImmutableMap<String, BizItem> typeMap = MapHelper.uniqueIndex(list,
                (item) -> item.getTaskId()
                , (item) -> item);

        // 3. 将 Map<String, String> 转换成 Map<Serializable, Object>
        Map<Serializable, BizItem> typeCodeNameMap = new HashMap<>(typeMap.size());
        typeMap.forEach((key, value) -> typeCodeNameMap.put(key, value));
        return typeCodeNameMap;
    }
}
