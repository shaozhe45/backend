package com.changqing.gov.authority.dao.auth;

import com.changqing.gov.authority.entity.auth.RoleAuthority;
import com.changqing.gov.base.mapper.SuperMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 角色的资源
 * </p>
 *
 * @author changqing
 * @date 2019-07-03
 */
@Repository
public interface RoleAuthorityMapper extends SuperMapper<RoleAuthority> {
    /**
     * 獲取帶查詢的資源
     * @return
     */
    List<Map> getViewResource();
    List<Map> getMenuResource();
}
