package com.changqing.gov.authority.service.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.authority.dto.core.StationPageDTO;
import com.changqing.gov.authority.entity.core.Station;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperCacheService;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 岗位
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
public interface StationService extends SuperCacheService<Station> {
    /**
     * 按权限查询岗位的分页信息
     *
     * @param page
     * @param params
     * @return
     */
    IPage<Station> findStationPage(IPage page, PageParams<StationPageDTO> params);

    /**
     * 根据id 查询
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findStationByIds(Set<Serializable> ids);

    /**
     * 根据id 查询 岗位名称
     *
     * @param ids
     * @return
     */
    Map<Serializable, Object> findStationNameByIds(Set<Serializable> ids);

    /**
     * 根据岗位名称判断，是否存在
     * @param stationName
     * @return
     */
    boolean stationNameExists(String stationName);
}
