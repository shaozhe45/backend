package com.changqing.gov.tenant.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.tenant.dao.DatasourceConfigMapper;
import com.changqing.gov.tenant.entity.DatasourceConfig;
import com.changqing.gov.tenant.service.DataSourceService;
import com.changqing.gov.tenant.service.DatasourceConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 数据源
 * </p>
 *
 * @author changqing
 * @date 2020-08-21
 */
@Slf4j
@Service
@DS("master")
public class DatasourceConfigServiceImpl extends SuperServiceImpl<DatasourceConfigMapper, DatasourceConfig> implements DatasourceConfigService {

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public Boolean testConnection(DataSourceProperty dataSourceProperty) {
        return dataSourceService.testConnection(dataSourceProperty);
    }

    @Override
    public List<DatasourceConfig> listByApplication(String applicationName, String status, String connectType) {
        return baseMapper.listByApplication(applicationName, status, connectType);
    }
}
