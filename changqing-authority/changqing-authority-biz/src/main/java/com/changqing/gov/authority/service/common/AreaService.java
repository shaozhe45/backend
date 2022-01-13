package com.changqing.gov.authority.service.common;

import com.changqing.gov.authority.entity.common.Area;
import com.changqing.gov.base.service.SuperCacheService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务接口
 * 地区表
 * </p>
 *
 * @author changqing
 * @date 2019-07-02
 */
public interface AreaService extends SuperCacheService<Area> {

    /**
     * 递归删除
     *
     * @param ids
     * @return
     */
    boolean recursively(List<Long> ids);

    List<Area> selectFullNameByParentId(Long parentId);

    List<Map<String,String>> selectArea(Long parentId);
}
