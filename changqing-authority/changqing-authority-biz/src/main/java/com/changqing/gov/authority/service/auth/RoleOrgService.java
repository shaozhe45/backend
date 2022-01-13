package com.changqing.gov.authority.service.auth;

import com.changqing.gov.authority.entity.auth.Role;
import com.changqing.gov.authority.entity.auth.RoleOrg;
import com.changqing.gov.base.service.SuperService;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 角色组织关系
 * </p>
 *
 * @author changqing
 * @date 2019-07-03
 */
public interface RoleOrgService extends SuperService<RoleOrg> {

    /**
     * 根据角色id查询
     *
     * @param id
     * @return
     */
    List<Long> listOrgByRoleId(Long id);

    Role selectByCode(String code);
}
