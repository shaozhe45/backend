package com.changqing.gov.demo.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.changqing.gov.demo.entity.CCommonArea;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 地区表
 * </p>
 *
 * @author changqing
 * @date 2019-08-20
 */
@Repository
public interface CCommonAreaMapper extends BaseMapper<CCommonArea> {

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    CCommonArea getXxx(@Param("id") Long id);

    CCommonArea getJoin(@Param("id") Long id);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    CCommonArea getJoinNo(@Param("id") Long id);

    int updateTest2(@Param("id") Long id);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    int updateTest3(@Param("id") Long id);

    @InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
    int save(@Param("area") CCommonArea area);


}
