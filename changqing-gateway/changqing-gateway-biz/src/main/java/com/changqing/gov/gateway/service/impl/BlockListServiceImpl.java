package com.changqing.gov.gateway.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.common.constant.CacheKey;
import com.changqing.gov.gateway.dao.BlockListMapper;
import com.changqing.gov.gateway.entity.BlockList;
import com.changqing.gov.gateway.service.BlockListService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.changqing.gov.common.constant.CacheKey.BLOCKLIST;
import static com.changqing.gov.common.constant.CacheKey.BLOCKLIST_ID;

/**
 * 阻止列表
 *
 * @author changqing
 * @date 2020/8/4 下午12:22
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class BlockListServiceImpl extends SuperServiceImpl<BlockListMapper, BlockList> implements BlockListService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean save(BlockList model) {
        int bool = baseMapper.insert(model);
        this.saveBlockList(model);
        return SqlHelper.retBool(bool);
    }

    public void saveBlockList(BlockList blockList) {
        String idKey = CacheKey.buildTenantKey(BLOCKLIST_ID, blockList.getId());
        redisTemplate.opsForValue().set(idKey, blockList);
        String key = StringUtils.isNotBlank(blockList.getIp()) ? CacheKey.buildTenantKey(BLOCKLIST, blockList.getIp()) : CacheKey.buildTenantKey(BLOCKLIST);
        redisTemplate.opsForSet().add(key, blockList.getId());
    }

    @Override
    public boolean updateById(BlockList blockList) {
        String key = StringUtils.isNotBlank(blockList.getIp()) ? CacheKey.buildTenantKey(BLOCKLIST, blockList.getIp()) : CacheKey.buildTenantKey(BLOCKLIST);
        redisTemplate.opsForSet().remove(key, blockList.getId());

        String idKey = CacheKey.buildTenantKey(BLOCKLIST_ID, blockList.getId());
        redisTemplate.delete(idKey);

        int bool = baseMapper.updateById(blockList);
        redisTemplate.opsForSet().add(key, blockList.getId());
        redisTemplate.opsForValue().set(idKey, blockList);
        return SqlHelper.retBool(bool);
    }


    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        if (idList.isEmpty()) {
            return true;
        }

        List<BlockList> blockLists = listByIds(idList);
        if (blockLists.isEmpty()) {
            return true;
        }
        baseMapper.deleteBatchIds(idList);

        blockLists.stream().forEach(this::removeBlockList);
        return true;
    }

    public void removeBlockList(BlockList blockList) {
        String key = StringUtils.isNotBlank(blockList.getIp()) ?
                CacheKey.buildTenantKey(BLOCKLIST, blockList.getIp()) :
                CacheKey.buildTenantKey(BLOCKLIST);
        redisTemplate.opsForSet().remove(key, blockList.getId());

        String idKey = CacheKey.buildTenantKey(BLOCKLIST_ID, blockList.getId());
        redisTemplate.delete(idKey);
    }

    @Override
    public Set<Object> findBlockList(String ip) {
        String key = CacheKey.buildTenantKey(BLOCKLIST, ip);
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members.isEmpty()) {
            return Collections.emptySet();
        }
        return members.stream().map(id -> redisTemplate.opsForValue().get(CacheKey.buildTenantKey(BLOCKLIST_ID, id)))
                .filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public Set<Object> findBlockList() {
        String key = CacheKey.buildTenantKey(BLOCKLIST);
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members.isEmpty()) {
            return Collections.emptySet();
        }
        return members.stream().map(id -> redisTemplate.opsForValue().get(CacheKey.buildTenantKey(BLOCKLIST_ID, id)))
                .filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public void loadAllBlockList() {
        List<BlockList> list = list();

        list.forEach((blockList) -> {
            String key = StringUtils.isNotBlank(blockList.getIp()) ?
                    CacheKey.buildTenantKey(BLOCKLIST, blockList.getIp()) :
                    CacheKey.buildTenantKey(BLOCKLIST);
            redisTemplate.opsForSet().add(key, blockList.getId());

            String idKey = CacheKey.buildTenantKey(BLOCKLIST_ID, blockList.getId());
            redisTemplate.opsForValue().set(idKey, blockList);
        });
    }
}
