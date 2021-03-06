package com.changqing.gov.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.tenant.entity.DatasourceConfig;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 数据源
 * </p>
 *
 * @author changqing
 * @date 2020-08-21
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DatasourceConfigMapper extends SuperMapper<DatasourceConfig> {

    /**
     * 查询所有租户的数据源
     *
     * @param applicationName 服务名
     * @param status          状态
     * @param connectType     链接类型
     * @return
     */
    @Select("SELECT dc.id,dc.create_time,dc.create_user,dc.update_time,dc.update_user, " +
            "       dc.name, dc.username, dc.password, dc.url, dc.driver_class_name driverClassName, t.code as pool_name " +
            " from d_datasource_config dc INNER JOIN d_tenant_datasource_config tdc on dc.id = tdc.datasource_config_id " +
            "INNER JOIN d_tenant t on t.id = tdc.tenant_id " +
            "where tdc.application = #{applicationName} " +
            "and t.`status` = #{status} and t.connect_type = #{connectType} ")
    List<DatasourceConfig> listByApplication(@Param("applicationName") String applicationName, @Param("status") String status,
                                             @Param("connectType") String connectType);
}
