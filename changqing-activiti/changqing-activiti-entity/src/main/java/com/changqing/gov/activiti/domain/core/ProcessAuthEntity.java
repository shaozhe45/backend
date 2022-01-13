package com.changqing.gov.activiti.domain.core;

import lombok.Builder;
import lombok.Data;

/**
 * 权限实体
 *
 * @author wz
 * @date 2020-08-07
 */
@Data
@Deprecated
public class ProcessAuthEntity {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 租户id
     */
    private String tenant;

    @Builder
    public ProcessAuthEntity(String userId, String userName, String tenant) {
        this.userId = userId;
        this.userName = userName;
        this.tenant = tenant;
    }
}
