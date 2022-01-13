package com.changqing.gov.executor.datasource;


import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.database.datasource.BaseMybatisConfiguration;
import com.changqing.gov.database.mybatis.auth.DataScopeInnerInterceptor;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置一些拦截器
 *
 * @author changqing
 * @createTime 2017-11-18 0:34
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({DatabaseProperties.class})
public class ExecutorMybatisAutoConfiguration extends BaseMybatisConfiguration {


    public ExecutorMybatisAutoConfiguration(DatabaseProperties databaseProperties) {
        super(databaseProperties);
    }

    /**
     * 数据权限插件
     *
     * @return DataScopeInterceptor
     */
    @Override
    protected List<InnerInterceptor> getPaginationBeforeInnerInterceptor() {
        List<InnerInterceptor> list = new ArrayList<>();
        Boolean isDataScope = databaseProperties.getIsDataScope();
        if (isDataScope) {
            list.add(new DataScopeInnerInterceptor(userId -> SpringUtils.getBean(UserService.class).getDataScopeById(userId)));
        }
        return list;
    }

}
