package com.changqing.gov.tenant.service.impl;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.exception.BizException;
import com.changqing.gov.tenant.dto.DataSourcePropertyDTO;
import com.changqing.gov.tenant.service.DataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.Set;


/**
 * 数据源管理
 * <p>
 * changqing.database.multiTenantType != DATASOURCE 时，该类才会生效
 *
 * @author changqing
 * @date 2020年03月15日11:35:08
 */
@Service
@ConditionalOnExpression("!'DATASOURCE'.equals('${changqing.database.multiTenantType}')")
@Slf4j
public class DefaultDataSourceServiceImpl implements DataSourceService {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Override
    public Set<String> findAll() {
        throw BizException.wrap("%s(%s)模式不允许该操作", databaseProperties.getMultiTenantType().name(), databaseProperties.getMultiTenantType().getDescribe());
    }

    @Override
    public Set<String> remove(String name) {
        throw BizException.wrap("%s(%s)模式不允许该操作", databaseProperties.getMultiTenantType().name(), databaseProperties.getMultiTenantType().getDescribe());
    }

    @Override
    public boolean testConnection(DataSourceProperty dataSourceProperty) {
        return false;
    }


    @Override
    public Set<String> addDynamicRoutingDataSource(DataSourceProperty dto) {
        return null;
    }

    @Override
    public boolean addLocalDynamicRoutingDataSource(String tenant) {
        return false;
    }

    @Override
    public boolean initConnect(DataSourcePropertyDTO dataSourceProperty) {
        return false;
    }
}
