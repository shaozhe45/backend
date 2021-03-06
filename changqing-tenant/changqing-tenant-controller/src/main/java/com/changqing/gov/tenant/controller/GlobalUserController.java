package com.changqing.gov.tenant.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.authority.dto.auth.UserUpdatePasswordDTO;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.base.entity.SuperEntity;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.common.constant.BizConstant;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.database.mybatis.conditions.query.LbqWrapper;
import com.changqing.gov.database.mybatis.conditions.query.QueryWrap;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.tenant.dto.GlobalUserPageDTO;
import com.changqing.gov.tenant.dto.GlobalUserSaveDTO;
import com.changqing.gov.tenant.dto.GlobalUserUpdateDTO;
import com.changqing.gov.tenant.entity.GlobalUser;
import com.changqing.gov.tenant.entity.Tenant;
import com.changqing.gov.tenant.enumeration.TenantStatusEnum;
import com.changqing.gov.tenant.service.GlobalUserService;
import com.changqing.gov.tenant.service.TenantService;
import com.changqing.gov.utils.BeanPlusUtil;
import com.changqing.gov.utils.BizAssert;
import com.changqing.gov.utils.StrHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * ???????????????
 * ????????????
 * </p>
 *
 * @author changqing
 * @date 2019-10-25
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/globalUser")
@Api(value = "GlobalUser", tags = "????????????")
@SysLog(enabled = false)
public class GlobalUserController extends SuperController<GlobalUserService, Long, GlobalUser, GlobalUserPageDTO, GlobalUserSaveDTO, GlobalUserUpdateDTO> {

    @Autowired
    private UserService userService;
    @Autowired
    private TenantService tenantService;

    @Override
    public R<GlobalUser> handlerSave(GlobalUserSaveDTO model) {
        if (StrUtil.isEmpty(model.getTenantCode()) || BizConstant.SUPER_TENANT.equals(model.getTenantCode())) {
            return success(baseService.save(model));
        } else {
            Tenant tenant = tenantService.getByCode(model.getTenantCode());
            BizAssert.notNull(tenant, "??????????????????");
            BizAssert.isTrue(TenantStatusEnum.NORMAL.eq(tenant.getStatus()), StrUtil.format("??????[{}]?????????", tenant.getName()));

            BaseContextHandler.setTenant(model.getTenantCode());
            User user = BeanPlusUtil.toBean(model, User.class);
            user.setName(StrHelper.getOrDef(model.getName(), model.getAccount()));
            if (StrUtil.isEmpty(user.getPassword())) {
                user.setPassword(BizConstant.DEF_PASSWORD);
            }
            user.setStatus(true);
            userService.initUser(user);
            return success(BeanPlusUtil.toBean(user, GlobalUser.class));
        }
    }

    @Override
    public R<GlobalUser> handlerUpdate(GlobalUserUpdateDTO model) {
        if (StrUtil.isEmpty(model.getTenantCode()) || BizConstant.SUPER_TENANT.equals(model.getTenantCode())) {
            return success(baseService.update(model));
        } else {
            BaseContextHandler.setTenant(model.getTenantCode());
            User user = BeanPlusUtil.toBean(model, User.class);
            userService.updateUser(user);
            return success(BeanPlusUtil.toBean(user, GlobalUser.class));
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "????????????", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "??????", dataType = "string", paramType = "query"),
    })
    @ApiOperation(value = "????????????????????????", notes = "????????????????????????")
    @GetMapping("/check")
    public R<Boolean> check(@RequestParam String tenantCode, @RequestParam String account) {
        if (StrUtil.isEmpty(tenantCode) || BizConstant.SUPER_TENANT.equals(tenantCode)) {
            return success(baseService.check(account));
        } else {
            BaseContextHandler.setTenant(tenantCode);
            return success(userService.check(account));
        }
    }

    @Override
    public void query(PageParams<GlobalUserPageDTO> params, IPage<GlobalUser> page, Long defSize) {
        GlobalUserPageDTO model = params.getModel();
        if (StrUtil.isEmpty(model.getTenantCode()) || BizConstant.SUPER_TENANT.equals(model.getTenantCode())) {
            QueryWrap<GlobalUser> wrapper = handlerWrapper(null, params);
            wrapper.lambda().eq(GlobalUser::getTenantCode, model.getTenantCode())
                    .like(GlobalUser::getAccount, model.getAccount())
                    .like(GlobalUser::getName, model.getName());
            baseService.page(page, wrapper);
            return;
        }
        BaseContextHandler.setTenant(model.getTenantCode());

        IPage<User> userPage = params.buildPage();
        LbqWrapper<User> wrapper = Wraps.lbq(null, params.getMap(), User.class);
        wrapper.like(User::getAccount, model.getAccount())
                .like(User::getName, model.getName())
                .orderByDesc(User::getCreateTime);

        userService.page(userPage, wrapper);

        page.setCurrent(userPage.getCurrent());
        page.setSize(userPage.getSize());
        page.setTotal(userPage.getTotal());
        page.setPages(userPage.getPages());
        List<GlobalUser> list = BeanPlusUtil.toBeanList(userPage.getRecords(), GlobalUser.class);
        page.setRecords(list);
    }


    @ApiOperation(value = "??????")
    @DeleteMapping("/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantCode", value = "????????????", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "ids[]", value = "??????id", dataType = "array", paramType = "query"),
    })
    public R<Boolean> delete(@RequestParam String tenantCode, @RequestParam("ids[]") List<Long> ids) {
        if (StrUtil.isEmpty(tenantCode) || BizConstant.SUPER_TENANT.equals(tenantCode)) {
            return success(baseService.removeByIds(ids));
        } else {
            BaseContextHandler.setTenant(tenantCode);
            return success(userService.remove(ids));
        }
    }


    /**
     * ????????????
     *
     * @param model ????????????
     * @return
     */
    @ApiOperation(value = "????????????", notes = "????????????")
    @PutMapping("/reset")
    public R<Boolean> updatePassword(@RequestBody @Validated(SuperEntity.Update.class) UserUpdatePasswordDTO model) {
        if (StrUtil.isEmpty(model.getTenantCode()) || BizConstant.SUPER_TENANT.equals(model.getTenantCode())) {
            return success(baseService.updatePassword(model));
        } else {
            BaseContextHandler.setTenant(model.getTenantCode());
            return success(userService.reset(model));
        }
    }
}
