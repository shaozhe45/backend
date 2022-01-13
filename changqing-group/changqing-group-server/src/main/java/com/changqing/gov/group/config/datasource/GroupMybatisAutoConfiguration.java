package com.changqing.gov.group.config.datasource;


import com.changqing.gov.database.datasource.BaseMybatisConfiguration;
import com.changqing.gov.database.mybatis.auth.DataScopeInterceptor;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.oauth.api.UserApi;
import com.changqing.gov.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 环保标准化管理平台-Mybatis 常用重用拦截器
 *
 * @author tanbao
 * @date 2021-08-24
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({DatabaseProperties.class})
public class GroupMybatisAutoConfiguration extends BaseMybatisConfiguration {

    public GroupMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }

    /**
     * 数据权限插件
     *
     * @return DataScopeInterceptor
     */
    @Order(10)
    @Bean
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "isDataScope", havingValue = "true", matchIfMissing = true)
    public DataScopeInterceptor dataScopeInterceptor() {
        return new DataScopeInterceptor((userId) -> SpringUtils.getBean(UserApi.class).getDataScopeById(userId));
    }

}
