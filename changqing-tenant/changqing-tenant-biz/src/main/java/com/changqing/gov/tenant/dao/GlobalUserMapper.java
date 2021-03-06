package com.changqing.gov.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.tenant.entity.GlobalUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 全局账号
 * </p>
 *
 * @author changqing
 * @date 2019-10-25
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface GlobalUserMapper extends SuperMapper<GlobalUser> {

}
