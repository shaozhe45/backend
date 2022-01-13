package com.changqing.gov.authority.dao.common;

import com.changqing.gov.authority.entity.common.Area;
import com.changqing.gov.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 地区表
 * </p>
 *
 * @author changqing
 * @date 2019-07-02
 */
@Repository
public interface AreaMapper extends SuperMapper<Area> {

    List<Area> selectFullNameByParentId(@Param("parentId") Long parentId);

    List<Map<String,String>> selectArea(@Param("parentId") Long parentId);
}
