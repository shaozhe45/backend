package com.changqing.gov.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 初始化数据库DAO
 *
 * @author changqing
 * @date 2019/09/02
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface InitDatabaseMapper {
    /**
     * 创建数据库
     *
     * @param database
     * @return
     */
    int createDatabase(@Param("database") String database);


    /**
     * 删除数据库
     *
     * @param database
     * @return
     */
    int dropDatabase(@Param("database") String database);

    /**
     * 根据条件查询租户列表
     *
     * @param status 状态
     * @return
     */
    List<String> selectTenantCodeList(@Param("status") String status, @Param("connectType") String connectType);
}
