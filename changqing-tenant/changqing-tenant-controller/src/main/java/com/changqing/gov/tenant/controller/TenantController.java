package com.changqing.gov.tenant.controller;


import com.alibaba.fastjson.JSONObject;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperCacheController;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.tenant.dto.TenantConnectDTO;
import com.changqing.gov.tenant.dto.TenantSaveDTO;
import com.changqing.gov.tenant.dto.TenantUpdateDTO;
import com.changqing.gov.tenant.entity.Tenant;
import com.changqing.gov.tenant.enumeration.TenantStatusEnum;
import com.changqing.gov.tenant.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.changqing.gov.tenant.enumeration.TenantStatusEnum.NORMAL;

/**
 * <p>
 * 前端控制器
 * 企业
 * <p>
 * 创建租户流程：
 * 1. COLUMN模式： 新增租户、初始化内置租户数据
 * 2. SCHEMA模式： 新增租户、初始化库、初始化表、初始化内置租户数据
 * 3. DATASOURCE模式
 * 该模式有2种动态创建租户数据源的方式
 * LOCAL: 租户数据源跟默认数据源在同一个 物理数据库   （启动时程序连192.168.1.1:3306/changqing_defualts库，租户连192.168.1.2:3306/changqing_base_xxxx库）
 * REMOTE：租户数据源跟默认数据源不在同一个 物理数据库（启动时程序连192.168.1.1:3306/changqing_defualts库，租户连192.168.1.2:3306/changqing_base_xxxx库）
 * <p>
 * LOCAL模式会自动使用程序默认库的账号，进行创建租户库操作，所以设置的账号密码必须拥有超级权限，但在程序中配置数据库的超级权限账号是比较危险的事，所以需要谨慎使用。
 * REMOTE模式 考虑到上述问题，决定让新增租户的管理员，手动创建好租户库后，提供账号密码连接信息等，配置到DatasourceConfig表，创建好租户后，在初始化数据源页面，
 * 选择已经创建好的数据源进行初始化操作。
 * <p>
 * 以上2种方式各有利弊，请大家酌情使用。 有更好的意见可以跟我讨论一下。
 * <p>
 * 先调用 POST /datasourceConfig 接口保存数据源
 * 在调用 POST /tenant 接口保存租户信息
 * 然后调用 POST /tenant/connect 接口为每个服务连接自己的数据源，并初始化表和数据
 *
 * @author changqing
 * @date 2019-10-24
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tenant")
@Api(value = "Tenant", tags = "企业")
@SysLog(enabled = false)
public class TenantController extends SuperCacheController<TenantService, Long, Tenant, Tenant, TenantSaveDTO, TenantUpdateDTO> {

    @Autowired
    private TenantService tenantService;

    @ApiOperation(value = "查询所有企业", notes = "查询所有企业")
    @GetMapping("/all")
    public R<List<Tenant>> list() {
        return success(baseService.list(Wraps.<Tenant>lbQ().eq(Tenant::getStatus, NORMAL)));
    }

    @Override
    public R<Tenant> handlerSave(TenantSaveDTO model) {
        Tenant tenant = baseService.save(model);
        return success(tenant);
    }

    @ApiOperation(value = "检测租户是否存在", notes = "检测租户是否存在")
    @GetMapping("/check/{code}")
    public R<Boolean> check(@PathVariable("code") String code) {
        return success(baseService.check(code));
    }


    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        // 这个操作相当的危险，请谨慎操作！！!
        return success(baseService.delete(ids));
    }

    @ApiOperation(value = "修改租户状态", notes = "修改租户状态")
    @PostMapping("/status")
    public R<Boolean> updateStatus(@RequestParam("ids[]") List<Long> ids,
                                   @RequestParam(defaultValue = "FORBIDDEN") @NotNull(message = "状态不能为空") TenantStatusEnum status) {
        return success(baseService.updateStatus(ids, status));
    }

    /**
     * 初始化
     *
     * @param tenantConnect
     * @return
     */
    @ApiOperation(value = "连接数据源", notes = "连接数据源")
    @PostMapping("/initConnect")
    public R<Boolean> initConnect(@RequestBody TenantConnectDTO tenantConnect) {
        return success(baseService.connect(tenantConnect));
    }

    @ApiOperation(value = "公司详情")
    @RequestMapping(value="/tenantDetail",method= RequestMethod.GET)
    public R<JSONObject> GetTenantDetail(@RequestParam String code){
        JSONObject resultObject=tenantService.GetTenantDetail(code);
        return success(resultObject);
    }
}
