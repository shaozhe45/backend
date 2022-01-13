package com.changqing.gov.authority.dao.core;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.changqing.gov.authority.entity.core.Station;
import com.changqing.gov.base.mapper.SuperMapper;
import com.changqing.gov.database.mybatis.auth.DataScope;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 岗位
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Repository
public interface StationMapper extends SuperMapper<Station> {
    /**
     * 分页查询岗位信息（含角色）
     *
     * @param page
     * @param queryWrapper
     * @param dataScope
     * @return
     */
    IPage<Station> findStationPage(IPage page, @Param(Constants.WRAPPER) Wrapper<Station> queryWrapper, DataScope dataScope);

    List<Map> selectStationByName(@Param("name")String name);
}
