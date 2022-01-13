package com.changqing.gov.tenant.service;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.tenant.entity.DatasourceConfig;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 数据源
 * </p>
 *
 * @author changqing
 * @date 2020-08-21
 */
public interface DatasourceConfigService extends SuperService<DatasourceConfig> {

    /**
     * 测试数据源链接
     *
     * @param dataSourceProperty 数据源信息
     * @return
     */
    Boolean testConnection(DataSourceProperty dataSourceProperty);

    /**
     * 查询所有租户的数据源
     *
     * @param applicationName 服务名
     * @param status          状态
     * @param connectType     链接类型
     * @return
     */
    List<DatasourceConfig> listByApplication(String applicationName, String status, String connectType);
}
