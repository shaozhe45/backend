package com.changqing.gov.gateway.service;

import com.changqing.gov.base.service.SuperService;
import com.changqing.gov.gateway.entity.BlockList;

import java.util.Set;

/**
 * 阻止列表
 *
 * @author changqing
 * @date 2020/8/4 下午12:22
 */
public interface BlockListService extends SuperService<BlockList> {
    /**
     * 根据IP查询阻止列表配置
     *
     * @param ip 请求端ip
     * @return
     */
    Set<Object> findBlockList(String ip);

    /**
     * 查询阻止列表配置
     *
     * @return
     */
    Set<Object> findBlockList();

    /**
     * 加载所有的阻止列表到缓存
     */
    void loadAllBlockList();
}
