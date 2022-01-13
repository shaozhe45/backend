package com.changqing.gov.authority.controller.common;


import com.changqing.gov.authority.dto.common.AreaPageDTO;
import com.changqing.gov.authority.dto.common.AreaSaveDTO;
import com.changqing.gov.authority.dto.common.AreaUpdateDTO;
import com.changqing.gov.authority.entity.common.Area;
import com.changqing.gov.authority.service.common.AreaService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperCacheController;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.query.LbqWrapper;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * 地区表
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/area")
@Api(value = "Area", tags = "地区表")
@PreAuth(replace = "area:")
public class AreaController extends SuperCacheController<AreaService, Long, Area, AreaPageDTO, AreaSaveDTO, AreaUpdateDTO> {

    @Autowired
    AreaService areaService;

    @ApiOperation(value = "检测地区编码是否重复", notes = "检测地区编码是否重复")
    @GetMapping("/check/{code}")
    @SysLog("检测地区编码是否重复")
    public R<Boolean> check(@RequestParam(required = false) Long id, @PathVariable String code) {
        int count = baseService.count(Wraps.<Area>lbQ().eq(Area::getCode, code).ne(Area::getId, id));
        return success(count > 0);
    }


    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        //TODO 重点测试递归删除
        return R.success(baseService.recursively(ids));
    }

    /**
     * 级联查询缓存中的地区
     *
     * @param parentId 父ID
     * @return 查询结果
     */
    @ApiOperation(value = "级联查询缓存中的地区", notes = "级联查询缓存中的地区")
    @GetMapping("/linkage")
    @SysLog("级联查询地区")
    public R<List<Area>> linkageQuery(@RequestParam(defaultValue = "0", required = false) Long parentId) {
        //TODO 想办法做缓存
        LbqWrapper<Area> query = Wraps.<Area>lbQ()
                .eq(Area::getParentId, parentId)
                .orderByAsc(Area::getSortValue);
        List<Area> areaListResult = new ArrayList<>();
        List<Map<String,String>> areaValue = new ArrayList<>();
        List<Area> areaList = baseService.list(query);
        /* for (int i = 0; i < areaList.size(); i++){*/
        for (Area area : areaList) {
            List<Area> areaListStr = areaService.selectFullNameByParentId(area.getParentId());
            for(Area area1 : areaListStr){
                area.setParentName(area1.getFullName());
                areaValue = areaService.selectArea(area1.getParentId());
                for(Map map : areaValue){
                    if (map.get("parent_id") != null && !"".equals(map.get("parent_id"))) {
                        List<Area> areaListStr1 = areaService.selectFullNameByParentId(Long.parseLong(map.get("id").toString()));
                        for(Area area2 : areaListStr1){
                            area.setParentBeforeName(area2.getFullName());
                        }
                    }
                }
            }

            areaListResult.add(area);
        }
        return success(areaListResult);
    }

}
