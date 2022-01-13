package com.changqing.gov.tenant.config.datasource;


import com.changqing.gov.database.datasource.BaseMybatisConfiguration;
import com.changqing.gov.database.properties.DatabaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置一些 Mybatis 常用重用拦截器
 *
 * @author changqing
 * @createTime 2017-11-18 0:34
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({DatabaseProperties.class})
public class TenantMybatisAutoConfiguration extends BaseMybatisConfiguration {

    public TenantMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }

}
