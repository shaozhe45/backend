package com.changqing.gov.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.tenant.entity.TenantDatasourceConfig;
import org.springframework.stereotype.Repository;

/**
 * 租户数据源关系 Mapper
 *
 * @author changqing
 * @date 2020/8/27 下午4:48
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface TenantDatasourceConfigMapper extends SuperMapper<TenantDatasourceConfig> {
}
