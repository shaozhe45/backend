package com.changqing.gov.oauth.controller;

import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.authority.service.common.DictionaryItemService;
import com.changqing.gov.authority.service.core.OrgService;
import com.changqing.gov.authority.service.core.StationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 数据注入的查询实现类
 *
 * @author changqing
 * @date 2020/9/25 9:15 上午
 */
@Slf4j
@RestController
@AllArgsConstructor
@Api(value = "数据注入查询接口", tags = "数据注入查询接口， 不建议前端调用")
public class InjectionController {
    private final DictionaryItemService dictionaryItemService;
    private final OrgService orgService;
    private final StationService stationService;
    private final UserService userService;


    @ApiOperation(value = "根据id查询用户", notes = "根据id查询用户")
    @GetMapping("/user/findUserByIds")
    public Map<Serializable, Object> findUserByIds(@RequestParam(value = "ids") Set<Serializable> ids) {
        return userService.findUserByIds(ids);
    }

    @ApiOperation(value = "根据id查询用户名称", notes = "根据id查询用户名称")
    @GetMapping("/user/findUserNameByIds")
    public Map<Serializable, Object> findUserNameByIds(@RequestParam(value = "ids") Set<Serializable> ids) {
        return userService.findUserNameByIds(ids);
    }

    @GetMapping("/station/findStationByIds")
    public Map<Serializable, Object> findStationByIds(@RequestParam("ids") Set<Serializable> ids) {
        return stationService.findStationByIds(ids);
    }

    @GetMapping("/station/findStationNameByIds")
    public Map<Serializable, Object> findStationNameByIds(@RequestParam("ids") Set<Serializable> ids) {
        return stationService.findStationNameByIds(ids);
    }

    @ApiOperation(value = "查询字典项", notes = "根据字典编码查询字典项")
    @GetMapping("/dictionaryItem/findDictionaryItem")
    public Map<Serializable, Object> findDictionaryItem(@RequestParam Set<Serializable> codes) {
        return this.dictionaryItemService.findDictionaryItem(codes);
    }

    @GetMapping("/org/findOrgByIds")
    public Map<Serializable, Object> findOrgByIds(@RequestParam("ids") Set<Serializable> ids) {
        return orgService.findOrgByIds(ids);
    }

    @GetMapping("/org/findOrgNameByIds")
    public Map<Serializable, Object> findOrgNameByIds(@RequestParam("ids") Set<Serializable> ids) {
        return orgService.findOrgNameByIds(ids);
    }


}
