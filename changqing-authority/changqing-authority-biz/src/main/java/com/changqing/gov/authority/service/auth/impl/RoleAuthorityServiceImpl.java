package com.changqing.gov.authority.service.auth.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.authority.dao.auth.RoleAuthorityMapper;
import com.changqing.gov.authority.dto.auth.RoleAuthoritySaveDTO;
import com.changqing.gov.authority.dto.auth.UserRoleSaveDTO;
import com.changqing.gov.authority.entity.auth.RoleAuthority;
import com.changqing.gov.authority.entity.auth.UserRole;
import com.changqing.gov.authority.enumeration.auth.AuthorizeType;
import com.changqing.gov.authority.service.auth.ResourceService;
import com.changqing.gov.authority.service.auth.RoleAuthorityService;
import com.changqing.gov.authority.service.auth.UserRoleService;
import com.changqing.gov.base.service.SuperServiceImpl;
import com.changqing.gov.common.constant.CacheKey;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import lombok.extern.slf4j.Slf4j;
import net.oschina.j2cache.CacheChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 角色的资源
 * </p>
 *
 * @author changqing
 * @date 2019-07-03
 */
@Slf4j
@Service
@DS("#thread.tenant")
public class RoleAuthorityServiceImpl extends SuperServiceImpl<RoleAuthorityMapper, RoleAuthority> implements RoleAuthorityService {

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private CacheChannel cache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUserRole(UserRoleSaveDTO userRole) {
        userRoleService.remove(Wraps.<UserRole>lbQ().eq(UserRole::getRoleId, userRole.getRoleId()));
        List<UserRole> list = userRole.getUserIdList()
                .stream()
                .map((userId) -> UserRole.builder()
                        .userId(userId)
                        .roleId(userRole.getRoleId())
                        .build())
                .collect(Collectors.toList());
        userRoleService.saveBatch(list);

        //清除 用户拥有的菜单和资源列表
        userRole.getUserIdList().forEach((userId) -> {
            String key = key(userId);
            cache.evict(CacheKey.USER_RESOURCE, key);
            cache.evict(CacheKey.USER_MENU, key);
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRoleAuthority(RoleAuthoritySaveDTO dto) {
        //删除角色和资源的关联
        super.remove(Wraps.<RoleAuthority>lbQ().eq(RoleAuthority::getRoleId, dto.getRoleId()));

        List<RoleAuthority> list = new ArrayList<>();

        // 导航勾选后，没选择操作选项，则添加一个查询权限
        List<Map> viewList = baseMapper.getViewResource();
        Map<Long, List<Long>> menuViewResouce = new HashMap<>();
        if (viewList != null && viewList.size() > 0) {
            for (Map<Long, Long> view : viewList) {
                if (!menuViewResouce.containsKey(view.get("menuId"))) {
                    List<Long> listTemp = new ArrayList<>();
                    listTemp.add(view.get("id"));
                    menuViewResouce.put(view.get("menuId"), listTemp);
                } else {
                    menuViewResouce.get(view.get("menuId")).add(view.get("id"));
                }
            }
        }

        if (dto.getMenuIdList() != null && !dto.getMenuIdList().isEmpty()) {
            if (dto.getResourceIdList() == null) {
                List<Long> listTemp = new ArrayList<>();
                dto.setResourceIdList(listTemp);
            }
            //將查詢按鈕權限添加到資源權限列表中
            for (Long mid : dto.getMenuIdList()) {
                if (menuViewResouce.containsKey(mid)) {
                    List<Long> listTemp = menuViewResouce.get(mid);
                    for (Long rid : listTemp) {
                        if (!dto.getResourceIdList().contains(rid)) {
                            dto.getResourceIdList().add(rid);
                        }
                    }
                }
            }

            // 导航及按钮资源的对应关系, 用于移除未勾选导航的资源按钮
            List<Map> menuRstList = baseMapper.getMenuResource();
            Map<Long, List<Long>> menuResouce = new HashMap<>();
            if (menuRstList != null && menuRstList.size() > 0) {
                for (Map<Long, Long> view : menuRstList) {
                    if (!menuResouce.containsKey(view.get("menuId"))) {
                        List<Long> listTemp = new ArrayList<>();
                        listTemp.add(view.get("id"));
                        menuResouce.put(view.get("menuId"), listTemp);
                    } else {
                        menuResouce.get(view.get("menuId")).add(view.get("id"));
                    }
                }
            }
            List<Long> removeResourceId=new ArrayList<>();
            for(Long mapMid:menuResouce.keySet()){
                boolean flag=false;
                for(Long mid:dto.getMenuIdList()){
                    if (mid.longValue()==mapMid.longValue()){
                        flag=true;
                        break;
                    }
                }
                if (!flag){
                    List<Long> listTemp =menuResouce.get(mapMid);
                    for (Long id:listTemp){
                        removeResourceId.add(id);
                    }
                }
            }
            for (Long removeId:removeResourceId){
                if (dto.getResourceIdList().contains(removeId)){
                    dto.getResourceIdList().remove(removeId);
                }
            }
        }
        if (dto.getResourceIdList() != null && !dto.getResourceIdList().isEmpty()) {
//            List<Long> menuIdList = resourceService.findMenuIdByResourceId(dto.getResourceIdList());
//            if (dto.getMenuIdList() == null || dto.getMenuIdList().isEmpty()) {
//                dto.setMenuIdList(menuIdList);
//            } else {
//                dto.getMenuIdList().addAll(menuIdList);
//            }

            //保存授予的资源
            List<RoleAuthority> resourceList = new HashSet<>(dto.getResourceIdList())
                    .stream()
                    .map((resourceId) -> RoleAuthority.builder()
                            .authorityType(AuthorizeType.RESOURCE)
                            .authorityId(resourceId)
                            .roleId(dto.getRoleId())
                            .build())
                    .collect(Collectors.toList());
            list.addAll(resourceList);
        }
        if (dto.getMenuIdList() != null && !dto.getMenuIdList().isEmpty()) {
            //保存授予的菜单
            List<RoleAuthority> menuList = new HashSet<>(dto.getMenuIdList())
                    .stream()
                    .map((menuId) -> RoleAuthority.builder()
                            .authorityType(AuthorizeType.MENU)
                            .authorityId(menuId)
                            .roleId(dto.getRoleId())
                            .build())
                    .collect(Collectors.toList());
            list.addAll(menuList);
        }
        super.saveBatch(list);

        // 清理
        List<Long> userIdList = userRoleService.listObjs(Wraps.<UserRole>lbQ().select(UserRole::getUserId).eq(UserRole::getRoleId, dto.getRoleId()),
                (userId) -> Convert.toLong(userId, 0L));
        userIdList.stream().collect(Collectors.toSet()).forEach(userId -> {
            log.info("清理了 {} 的菜单/资源", userId);
            cache.evict(CacheKey.USER_RESOURCE, key(userId));
            cache.evict(CacheKey.USER_MENU, key(userId));
            cache.evict(CacheKey.USER_ROLE, key(userId));
        });

        cache.evict(CacheKey.ROLE_RESOURCE, key(dto.getRoleId()));
        cache.evict(CacheKey.ROLE_MENU, key(dto.getRoleId()));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByAuthorityId(List<Long> ids) {
        return remove(Wraps.<RoleAuthority>lbQ().in(RoleAuthority::getAuthorityId, ids));
    }
}
