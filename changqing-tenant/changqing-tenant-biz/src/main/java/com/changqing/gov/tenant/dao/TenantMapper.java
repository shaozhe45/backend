package com.changqing.gov.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 企业
 * </p>
 *
 * @author changqing
 * @date 2019-10-25
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface TenantMapper extends SuperMapper<Tenant> {

    /**
     * 根据租户编码查询
     *
     * @param code 租户编码
     * @return
     */
    Tenant getByCode(@Param("code") String code);

    public List<Map<String,String>> GetTenantDetail(@Param("code") String code);

}
