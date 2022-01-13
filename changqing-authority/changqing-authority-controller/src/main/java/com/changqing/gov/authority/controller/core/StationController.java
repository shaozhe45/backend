package com.changqing.gov.authority.controller.core;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.authority.dto.core.StationPageDTO;
import com.changqing.gov.authority.dto.core.StationSaveDTO;
import com.changqing.gov.authority.dto.core.StationUpdateDTO;
import com.changqing.gov.authority.entity.core.Station;
import com.changqing.gov.authority.service.core.StationService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperCacheController;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.model.RemoteData;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * 岗位
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@RestController
@RequestMapping("/station")
@Api(value = "Station", tags = "岗位")
@PreAuth(replace = "station:")
public class StationController extends SuperCacheController<StationService, Long, Station, StationPageDTO, StationSaveDTO, StationUpdateDTO> {

    @Override
    public void query(PageParams<StationPageDTO> params, IPage<Station> page, Long defSize) {
        baseService.findStationPage(page, params);
    }

    @Override
    public R<Boolean> handlerImport(List<Map<String, String>> list) {
        List<Station> stationList = list.stream().map((map) -> {
            Station item = new Station();
            item.setDescribe(map.getOrDefault("描述", ""));
            item.setName(map.getOrDefault("名称", ""));
            item.setOrg(new RemoteData<>(Convert.toLong(map.getOrDefault("组织", ""))));
            item.setStatus(Convert.toBool(map.getOrDefault("状态", "")));
            return item;
        }).collect(Collectors.toList());

        return R.success(baseService.saveBatch(stationList));
    }

    @Override
    public R<Station> handlerSave(StationSaveDTO model) {
        // 检查岗位名称是否存在
        if(baseService.stationNameExists(model.getName())) {
            return R.fail("岗位名称："+model.getName()+"已存在，请重新输入！");
        }else {
            return R.successDef();
        }
    }
}
