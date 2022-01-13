package com.changqing.gov.authority.service.common;

import com.changqing.gov.authority.entity.common.Parameter;
import com.changqing.gov.base.service.SuperService;

/**
 * <p>
 * 业务接口
 * 参数配置
 * </p>
 *
 * @author changqing
 * @date 2020-02-05
 */
public interface ParameterService extends SuperService<Parameter> {
    /**
     * 根据参数键查询参数值
     *
     * @param key    参数键
     * @param defVal 参数值
     * @return
     */
    String getValue(String key, String defVal);
}
