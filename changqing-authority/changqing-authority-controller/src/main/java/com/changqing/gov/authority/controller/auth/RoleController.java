package com.changqing.gov.authority.controller.auth;

import cn.hutool.core.bean.BeanUtil;
import com.changqing.gov.authority.dto.auth.*;
import com.changqing.gov.authority.entity.auth.Role;
import com.changqing.gov.authority.entity.auth.RoleAuthority;
import com.changqing.gov.authority.entity.auth.UserRole;
import com.changqing.gov.authority.enumeration.auth.AuthorizeType;
import com.changqing.gov.authority.service.auth.RoleAuthorityService;
import com.changqing.gov.authority.service.auth.RoleOrgService;
import com.changqing.gov.authority.service.auth.RoleService;
import com.changqing.gov.authority.service.auth.UserRoleService;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperCacheController;
import com.changqing.gov.database.mybatis.auth.DataScopeType;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.security.annotation.PreAuth;
import com.changqing.gov.utils.BeanPlusUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * 角色
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "Role", tags = "角色")
@PreAuth(replace = "role:")
public class RoleController extends SuperCacheController<RoleService, Long, Role, RolePageDTO, RoleSaveDTO, RoleUpdateDTO> {

    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private RoleOrgService roleOrgService;
    @Autowired
    private UserRoleService userRoleService;


    /**
     * 查询角色
     *
     * @param id 主键id
     * @return 查询结果
     */
    @ApiOperation(value = "查询角色", notes = "查询角色")
    @GetMapping("/details/{id}")
    @SysLog("查询角色")
    public R<RoleQueryDTO> getDetails(@PathVariable Long id) {
        Role role = baseService.getByIdCache(id);
        RoleQueryDTO query = BeanPlusUtil.toBean(role, RoleQueryDTO.class);
        if (query.getDsType() != null && DataScopeType.CUSTOMIZE.eq(query.getDsType())) {
            List<Long> orgList = roleOrgService.listOrgByRoleId(role.getId());
            query.setOrgList(orgList);
        }
        return success(query);
    }

    @ApiOperation(value = "检测角色编码", notes = "检测角色编码")
    @GetMapping("/check/{code}")
    @SysLog("新增角色")
    public R<Boolean> check(@PathVariable String code) {
        return success(baseService.check(code));
    }


    @Override
    public R<Role> handlerSave(RoleSaveDTO data) {
        baseService.saveRole(data, getUserId());
        Role role=BeanPlusUtil.toBean(data, Role.class);
        //表示新增
        if (role.getId()==null){
            Role roleTemp=baseService.selectByCode(data.getCode());
            if (roleTemp!=null) {
                RoleAuthoritySaveDTO roleAuthoritySaveDTO = new RoleAuthoritySaveDTO();
                roleAuthoritySaveDTO.setRoleId(roleTemp.getId());
                //导航
                List<Long> menuIds = new ArrayList<>();
                menuIds.add(129766973650366488L); // 顶部菜单
                menuIds.add(129766973650366493L); // 首页
                menuIds.add(133797254997044226L); // 组织机构
                menuIds.add(133797254997044230L); // 组织管理
                menuIds.add(133797254997044235L); //  岗位管理
                menuIds.add(133797254997044239L); //  用户管理

                menuIds.add(103L); //  基础配置
                menuIds.add(605078371293987105L); //  数据字典维护
                menuIds.add(605078463069552993L); //  地区信息维护
                menuIds.add(1225042542827929600L); //  参数配置

                roleAuthoritySaveDTO.setMenuIdList(menuIds);
                // 按钮
                List<Long> resouceIds = new ArrayList<>();
                roleAuthoritySaveDTO.setResourceIdList(resouceIds);
                boolean index = roleAuthorityService.saveRoleAuthority(roleAuthoritySaveDTO);
            }
        }

        return success(role);
    }


    @ApiOperation(value = "新增")
    @PostMapping
    @SysLog(value = "新增", request = false)
    @PreAuth("hasPermit('{}add')")
    @Override
    public R<Role> save(@RequestBody @Validated RoleSaveDTO saveDTO) {
        Role roleDetail = roleOrgService.selectByCode(saveDTO.getCode());
        if (roleDetail == null || "".equals(roleDetail)) {
            R<Role> result = handlerSave(saveDTO);
            if (result.getDefExec()) {
                Role model = BeanUtil.toBean(saveDTO, getEntityClass());
                getBaseService().save(model);
                result.setData(model);
            }
            return result;
        } else {
            return R.fail(444, "该角色已经添加，不可重复添加！");
        }

    }

    @Override
    public R<Role> handlerUpdate(RoleUpdateDTO data) {
        baseService.updateRole(data, getUserId());
        return success(BeanPlusUtil.toBean(data, Role.class));
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        return success(baseService.removeByIdWithCache(ids));
    }

    /**
     * 给用户分配角色
     *
     * @param userRole 用户角色授权对象
     * @return 新增结果
     */
    @ApiOperation(value = "给用户分配角色", notes = "给用户分配角色")
    @PostMapping("/user")
    @SysLog("给角色分配用户")
    public R<Boolean> saveUserRole(@RequestBody UserRoleSaveDTO userRole) {
        return success(roleAuthorityService.saveUserRole(userRole));
    }

    /**
     * 查询角色的用户
     *
     * @param roleId 角色id
     * @return 新增结果
     */
    @ApiOperation(value = "查询角色的用户", notes = "查询角色的用户")
    @GetMapping("/user/{roleId}")
    @SysLog("查询角色的用户")
    public R<List<Long>> findUserIdByRoleId(@PathVariable Long roleId) {
        List<UserRole> list = userRoleService.list(Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, roleId));
        return success(list.stream().mapToLong(UserRole::getUserId).boxed().collect(Collectors.toList()));
    }

    /**
     * 查询角色拥有的资源id
     *
     * @param roleId 角色id
     * @return 新增结果
     */
    @ApiOperation(value = "查询角色拥有的资源id集合", notes = "查询角色拥有的资源id集合")
    @GetMapping("/authority/{roleId}")
    @SysLog("查询角色拥有的资源")
    public R<RoleAuthoritySaveDTO> findAuthorityIdByRoleId(@PathVariable Long roleId) {
        List<RoleAuthority> list = roleAuthorityService.list(Wraps.<RoleAuthority>lbQ().eq(RoleAuthority::getRoleId, roleId));
        List<Long> menuIdList = list.stream().filter(item -> AuthorizeType.MENU.eq(item.getAuthorityType())).mapToLong(RoleAuthority::getAuthorityId).boxed().collect(Collectors.toList());
        List<Long> resourceIdList = list.stream().filter(item -> AuthorizeType.RESOURCE.eq(item.getAuthorityType())).mapToLong(RoleAuthority::getAuthorityId).boxed().collect(Collectors.toList());
        RoleAuthoritySaveDTO roleAuthority = RoleAuthoritySaveDTO.builder()
                .menuIdList(menuIdList).resourceIdList(resourceIdList)
                .build();
        return success(roleAuthority);
    }


    /**
     * 给角色配置权限
     *
     * @param roleAuthoritySaveDTO 角色权限授权对象
     * @return 新增结果
     */
    @ApiOperation(value = "给角色配置权限", notes = "给角色配置权限")
    @PostMapping("/authority")
    @SysLog("给角色配置权限")
    public R<Boolean> saveRoleAuthority(@RequestBody RoleAuthoritySaveDTO roleAuthoritySaveDTO) {
        Boolean index = false;
        if (roleAuthoritySaveDTO.getRoleId() != null && !"".equals(roleAuthoritySaveDTO.getRoleId())) {
            index = roleAuthorityService.saveRoleAuthority(roleAuthoritySaveDTO);
        }
        return success(index);
    }


    /**
     * 根据角色编码查询用户ID
     *
     * @param codes 编码集合
     * @return 查询结果
     */
    @ApiOperation(value = "根据角色编码查询用户ID", notes = "根据角色编码查询用户ID")
    @GetMapping("/codes")
    @SysLog("根据角色编码查询用户ID")
    public R<List<Long>> findUserIdByCode(@RequestParam(value = "codes") String[] codes) {
        return success(baseService.findUserIdByCode(codes));
    }

}
