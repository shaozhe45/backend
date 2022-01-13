package com.changqing.gov.authority.service.auth.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.authority.dao.auth.RoleMapper;
import com.changqing.gov.authority.dao.auth.RoleOrgMapper;
import com.changqing.gov.authority.entity.auth.RoleOrg;
import com.changqing.gov.authority.entity.auth.Role;
import com.changqing.gov.authority.service.auth.RoleOrgService;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 角色组织关系
 * </p>
 *
 * @author changqing
 * @date 2019-07-03
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class RoleOrgServiceImpl extends SuperServiceImpl<RoleOrgMapper, RoleOrg> implements RoleOrgService {
    @Autowired
    RoleMapper roleMapper;

    @Override
    public List<Long> listOrgByRoleId(Long id) {
        List<RoleOrg> list = super.list(Wraps.<RoleOrg>lbQ().eq(RoleOrg::getRoleId, id));
        List<Long> orgList = list.stream().mapToLong(RoleOrg::getOrgId).boxed().collect(Collectors.toList());
        return orgList;
    }
    @Override
    public Role selectByCode(String code) {
        return roleMapper.selectByCode(code);
    }

}
