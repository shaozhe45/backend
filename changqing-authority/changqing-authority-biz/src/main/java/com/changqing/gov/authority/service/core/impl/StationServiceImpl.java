package com.changqing.gov.authority.service.core.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.authority.dao.core.StationMapper;
import com.changqing.gov.authority.dto.core.StationPageDTO;
import com.changqing.gov.authority.entity.core.Station;
import com.changqing.gov.authority.service.core.StationService;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.base.service.SuperCacheServiceImpl;
import com.changqing.gov.database.mybatis.auth.DataScope;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.query.LbqWrapper;
import com.changqing.gov.injection.annonation.InjectionResult;
import com.changqing.gov.utils.MapHelper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.changqing.gov.common.constant.CacheKey.STATION;

/**
 * <p>
 * 业务实现类
 * 岗位
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class StationServiceImpl extends SuperCacheServiceImpl<StationMapper, Station> implements StationService {
    @Override
    protected String getRegion() {
        return STATION;
    }

    @Override
    // 启用属性自动注入
    @InjectionResult
    public IPage<Station> findStationPage(IPage page, PageParams<StationPageDTO> params) {
        StationPageDTO data = params.getModel();
        Station station = BeanUtil.toBean(data, Station.class);

        //Wraps.lbQ(station); 这种写法值 不能和  ${ew.customSqlSegment} 一起使用
        LbqWrapper<Station> wrapper = Wraps.lbq(null, params.getMap(), Station.class);

        // ${ew.customSqlSegment} 语法一定要手动eq like 等
        wrapper.like(Station::getName, station.getName())
                .like(Station::getDescribe, station.getDescribe())
                .eq(Station::getOrg, station.getOrg())
                .eq(Station::getStatus, station.getStatus());
        return baseMapper.findStationPage(page, wrapper, new DataScope());
    }

    @Override
    public Map<Serializable, Object> findStationByIds(Set<Serializable> ids) {
        List<Station> list = getStations(ids);

        //key 是 组织id， value 是org 对象
        ImmutableMap<Serializable, Object> typeMap = MapHelper.uniqueIndex(list, Station::getId, (station) -> station);
        return typeMap;
    }

    @Override
    public Map<Serializable, Object> findStationNameByIds(Set<Serializable> ids) {
        List<Station> list = getStations(ids);
        //key 是 组织id， value 是org 对象
        ImmutableMap<Serializable, Object> typeMap = MapHelper.uniqueIndex(list, Station::getId, Station::getName);
        return typeMap;
    }

    private List<Station> getStations(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> idList = ids.stream().mapToLong(Convert::toLong).boxed().collect(Collectors.toList());

        List<Station> list = null;
        if (idList.size() <= 1000) {
            list = idList.stream().map(this::getByIdCache).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            LbqWrapper<Station> query = Wraps.<Station>lbQ()
                    .in(Station::getId, idList)
                    .eq(Station::getStatus, true);
            list = super.list(query);

            if (!list.isEmpty()) {
                list.forEach(item -> {
                    String itemKey = key(item.getId());
                    cacheChannel.set(getRegion(), itemKey, item);
                });
            }
        }
        return list;
    }

    @Override
    public boolean stationNameExists(String stationName){
        List<Map> result=baseMapper.selectStationByName(stationName);
        if (result!=null && result.size()>0){
            return true;
        }else {
            return false;
        }
    }
}
