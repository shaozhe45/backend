package com.changqing.gov.oauth.controller;

import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.base.R;
import com.changqing.gov.security.feign.UserQuery;
import com.changqing.gov.security.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 前端控制器
 * 用户
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@Api(value = "User", tags = "用户")
@AllArgsConstructor
public class OauthUserController {
    private final UserService userService;

    /**
     * 单体查询用户
     *
     * @param id 主键id
     * @return 查询结果
     */
    @ApiOperation(value = "查询用户详细", notes = "查询用户详细")
    @PostMapping(value = "/anno/id/{id}")
    public R<SysUser> getById(@PathVariable Long id, @RequestBody UserQuery query) {
        return R.success(userService.getSysUserById(id, query));
    }

    /**
     * 根据用户id，查询用户权限范围
     *
     * @param id 用户id
     * @return
     */
    @ApiOperation(value = "查询用户权限范围", notes = "根据用户id，查询用户权限范围")
    @GetMapping(value = "/ds/{id}")
    public Map<String, Object> getDataScopeById(@PathVariable("id") Long id) {
        return userService.getDataScopeById(id);
    }

}
