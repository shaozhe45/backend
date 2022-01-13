package com.changqing.gov.authority.service.auth;

import com.changqing.gov.authority.entity.auth.Application;
import com.changqing.gov.base.service.SuperCacheService;

/**
 * <p>
 * 业务接口
 * 应用
 * </p>
 *
 * @author changqing
 * @date 2019-12-15
 */
public interface ApplicationService extends SuperCacheService<Application> {
    /**
     * 根据 clientid 和 clientSecret 查询
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    Application getByClient(String clientId, String clientSecret);
}
