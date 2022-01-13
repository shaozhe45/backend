package com.changqing.gov.gateway.runner;

import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.database.properties.MultiTenantType;
import com.changqing.gov.gateway.service.BlockListService;
import com.changqing.gov.gateway.service.RateLimiterService;
import com.changqing.gov.tenant.dao.InitDatabaseMapper;
import com.changqing.gov.tenant.enumeration.TenantStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动成功后执行， 该类会在InitDatabaseOnStarted之后执行
 *
 * @author changqing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartedUpRunner implements ApplicationRunner {

    private final InitDatabaseMapper initDbMapper;
    private final BlockListService blockListService;
    private final RateLimiterService rateLimiterService;
    private final DatabaseProperties databaseProperties;

    @Override
    public void run(ApplicationArguments args) {
        if (MultiTenantType.NONE.eq(databaseProperties.getMultiTenantType())) {
            blockListService.loadAllBlockList();
            rateLimiterService.loadAllRateLimiters();
        } else {
            List<String> tenantCodeList = initDbMapper.selectTenantCodeList(TenantStatusEnum.NORMAL.name(), null);
            tenantCodeList.forEach((tenantCode) -> {
                BaseContextHandler.setTenant(tenantCode);
                blockListService.loadAllBlockList();
                rateLimiterService.loadAllRateLimiters();
            });
        }
    }
}
