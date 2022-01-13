package com.changqing.gov.tenant.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.tenant.dao.TenantDatasourceConfigMapper;
import com.changqing.gov.tenant.entity.TenantDatasourceConfig;
import com.changqing.gov.tenant.service.TenantDatasourceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 租户数据源关系
 *
 * @author changqing
 * @date 2020/8/27 下午4:51
 */
@Slf4j
@Service
@DS("master")
public class TenantDatasourceConfigServiceImpl extends SuperServiceImpl<TenantDatasourceConfigMapper, TenantDatasourceConfig> implements TenantDatasourceConfigService {
}
