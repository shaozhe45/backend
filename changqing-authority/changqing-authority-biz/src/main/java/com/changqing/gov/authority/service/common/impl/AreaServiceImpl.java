package com.changqing.gov.authority.service.common.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.authority.dao.common.AreaMapper;
import com.changqing.gov.authority.entity.common.Area;
import com.changqing.gov.authority.service.common.AreaService;
import com.changqing.gov.base.service.SuperCacheServiceImpl;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.changqing.gov.common.constant.CacheKey.AREA;

/**
 * <p>
 * 业务实现类
 * 地区表
 * </p>
 *
 * @author changqing
 * @date 2019-07-02
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class AreaServiceImpl extends SuperCacheServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    AreaMapper areaMapper;

    @Override
    protected String getRegion() {
        return AREA;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recursively(List<Long> ids) {
        boolean removeFlag = removeByIds(ids);
        delete(ids);
        return removeFlag;
    }

    private void delete(List<Long> ids) {
        // 查询子节点
        List<Long> childIds = super.listObjs(Wraps.<Area>lbQ().select(Area::getId).in(Area::getParentId, ids), Convert::toLong);
        if (!childIds.isEmpty()) {
            removeByIds(childIds);
            delete(childIds);
        }
        log.debug("退出地区数据递归");
    }

    @Override
    public List<Area> selectFullNameByParentId(Long parentId) {
        return areaMapper.selectFullNameByParentId(parentId);
    }

    @Override
    public List<Map<String,String>> selectArea(Long parentId) {
        return areaMapper.selectArea(parentId);
    }
}
