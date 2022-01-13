package com.changqing.gov.authority.dao.auth;

import com.changqing.gov.authority.entity.auth.UserRole;
import com.changqing.gov.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 角色分配
 * 账号角色绑定
 * </p>
 *
 * @author changqing
 * @date 2019-07-03
 */
@Repository
public interface UserRoleMapper extends SuperMapper<UserRole> {

}
