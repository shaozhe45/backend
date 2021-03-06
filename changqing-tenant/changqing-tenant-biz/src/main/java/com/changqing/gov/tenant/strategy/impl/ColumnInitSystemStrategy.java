package com.changqing.gov.tenant.strategy.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.changqing.gov.authority.entity.auth.Application;
import com.changqing.gov.authority.entity.auth.Menu;
import com.changqing.gov.authority.entity.auth.Resource;
import com.changqing.gov.authority.entity.auth.Role;
import com.changqing.gov.authority.entity.auth.RoleAuthority;
import com.changqing.gov.authority.entity.auth.User;
import com.changqing.gov.authority.entity.common.Dictionary;
import com.changqing.gov.authority.entity.common.DictionaryItem;
import com.changqing.gov.authority.entity.common.Parameter;
import com.changqing.gov.authority.enumeration.auth.ApplicationAppTypeEnum;
import com.changqing.gov.authority.enumeration.auth.AuthorizeType;
import com.changqing.gov.authority.enumeration.auth.Sex;
import com.changqing.gov.authority.service.auth.ApplicationService;
import com.changqing.gov.authority.service.auth.MenuService;
import com.changqing.gov.authority.service.auth.ResourceService;
import com.changqing.gov.authority.service.auth.RoleAuthorityService;
import com.changqing.gov.authority.service.auth.RoleService;
import com.changqing.gov.authority.service.auth.UserService;
import com.changqing.gov.authority.service.common.DictionaryItemService;
import com.changqing.gov.authority.service.common.DictionaryService;
import com.changqing.gov.authority.service.common.ParameterService;
import com.changqing.gov.common.constant.ParameterKey;
import com.changqing.gov.context.BaseContextHandler;
import com.changqing.gov.database.mybatis.auth.DataScopeType;
import com.changqing.gov.database.properties.DatabaseProperties;
import com.changqing.gov.tenant.dto.TenantConnectDTO;
import com.changqing.gov.tenant.strategy.InitSystemStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ???????????????:
 * ??????????????????
 *
 * @author changqing
 * @date 2020???04???05???13:14:28
 */
@Service("COLUMN")
@Slf4j
public class ColumnInitSystemStrategy implements InitSystemStrategy {
    private static final String ORG = "org";
    private static final String STATION = "station";
    private static final String USER = "user";
    private static final String MENU = "menu";
    private static final String RESOURCE = "resource";
    private static final String ROLE = "role";
    private static final String DICT = "dict";
    private static final String AREA = "area";
    private static final String PARAMETER = "parameter";
    private static final String APPLICATION = "application";
    private static final String DB = "db";
    private static final String OPT_LOG = "optLog";
    private static final String LOGIN_LOG = "loginLog";
    private static final String SMS_MANAGE = "sms:manage";
    private static final String SMS_TEMPLATE = "sms:template";
    private static final String MSGS = "msgs";
    private static final String FILE = "file";
    @Autowired
    private MenuService menuService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private DictionaryItemService dictionaryItemService;
    @Autowired
    private ParameterService parameterService;
    @Autowired
    private DatabaseProperties databaseProperties;
    @Autowired
    private UserService userService;

    /**
     * ???*????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????si ~~~
     * <p>
     * ????????? SCHEMA ????????????????????????????????? ??????id ????????????????????????????????????
     *
     * @param tenantConnect ????????????????????????
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initConnect(TenantConnectDTO tenantConnect) {
        String tenant = tenantConnect.getTenant();
        // ???????????????
        //1, ??????????????? ID TENANT
        DatabaseProperties.HutoolId id = databaseProperties.getHutoolId();
        Snowflake snowflake = IdUtil.getSnowflake(id.getWorkerId(), id.getDataCenterId());

        BaseContextHandler.setTenant(tenant);

        // ?????? ?????? ?????? ??????_?????? ?????? ??????
        List<Menu> menuList = new ArrayList<>();
        Map<String, Long> menuMap = new HashMap<>();
        boolean menuFlag = initMenu(snowflake, menuList, menuMap);

        List<Resource> resourceList = new ArrayList<>();
        boolean resourceFlag = initResource(resourceList, menuMap);

        // ??????
        Long roleId = snowflake.nextId();
        boolean roleFlag = initRole(roleId);

        // ????????????
        boolean roleAuthorityFlag = initRoleAuthority(menuList, resourceList, roleId);

        // ??????
        initDict();

        //??????
        initParameter();

        initApplication();

        // ?????????????????????
        initUser();

        return menuFlag && resourceFlag && roleFlag && roleAuthorityFlag;
    }

    private boolean initApplication() {
        List<Application> list = new ArrayList<>();
        list.add(Application.builder().clientId("changqing_ui").clientSecret("changqing_ui_secret").website("http://tangyh.top:10000/changqing-ui/").name("SaaS?????????????????????").appType(ApplicationAppTypeEnum.PC).status(true).build());
        list.add(Application.builder().clientId("changqing_admin_ui").clientSecret("changqing_admin_ui_secret").website("http://tangyh.top:180/changqing-admin-ui/").name("SaaS?????????????????????").appType(ApplicationAppTypeEnum.PC).status(true).build());
        return applicationService.saveBatch(list);
    }

    private boolean initUser() {
        User user = User.builder()
                .account("admin").name("?????????????????????").password("admin")
                .readonly(true).sex(Sex.M).avatar("cnrhVkzwxjPwAaCfPbdc.png")
                .status(true).passwordErrorNum(0)
                .build();
        return userService.initUser(user);
    }

    private boolean initParameter() {
        List<Parameter> list = new ArrayList<>();
        list.add(Parameter.builder().key(ParameterKey.LOGIN_POLICY).name("????????????").value(ParameterKey.LoginPolicy.MANY.name()).describe("ONLY_ONE:??????????????????????????????; MANY:????????????????????????; ONLY_ONE_CLIENT:?????????????????????????????????????????????").status(true).readonly(true).build());
        return parameterService.saveBatch(list);
    }

    private boolean initRoleAuthority(List<Menu> menuList, List<Resource> resourceList, Long roleId) {
        List<RoleAuthority> roleAuthorityList = new ArrayList<>();
        menuList.forEach(item -> {
            roleAuthorityList.add(RoleAuthority.builder().authorityType(AuthorizeType.MENU).authorityId(item.getId()).roleId(roleId).build());
        });
        resourceList.forEach(item -> {
            roleAuthorityList.add(RoleAuthority.builder().authorityType(AuthorizeType.RESOURCE).authorityId(item.getId()).roleId(roleId).build());
        });
        return roleAuthorityService.saveBatch(roleAuthorityList);
    }

    private boolean initRole(Long roleId) {
        Role role = Role.builder().id(roleId).name("???????????????").code("PT_ADMIN").describe("?????????????????????").dsType(DataScopeType.ALL).readonly(true).build();
        return roleService.save(role);
    }

    private boolean initResource(List<Resource> resourceList, Map<String, Long> menuMap) {
        Long orgId = menuMap.get(ORG);
        resourceList.add(Resource.builder().code("org:add").name("??????").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:delete").name("??????").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:export").name("??????").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:import").name("??????").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:update").name("??????").menuId(orgId).build());
        resourceList.add(Resource.builder().code("org:view").name("??????").menuId(orgId).build());

        Long stationId = menuMap.get(STATION);
        resourceList.add(Resource.builder().code("station:add").name("??????").menuId(stationId).build());
        resourceList.add(Resource.builder().code("station:delete").name("??????").menuId(stationId).build());
        resourceList.add(Resource.builder().code("station:export").name("??????").menuId(stationId).build());
        resourceList.add(Resource.builder().code("station:import").name("??????").menuId(stationId).build());
        resourceList.add(Resource.builder().code("station:update").name("??????").menuId(stationId).build());
        resourceList.add(Resource.builder().code("station:view").name("??????").menuId(stationId).build());

        Long userId = menuMap.get(USER);
        resourceList.add(Resource.builder().code("user:add").name("??????").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:delete").name("??????").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:export").name("??????").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:import").name("??????").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:update").name("??????").menuId(userId).build());
        resourceList.add(Resource.builder().code("user:view").name("??????").menuId(userId).build());

        Long menuId = menuMap.get(MENU);
        resourceList.add(Resource.builder().code("menu:add").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:delete").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:export").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:import").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:update").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("menu:view").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:add").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:update").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:delete").name("??????").menuId(menuId).build());
        resourceList.add(Resource.builder().code("resource:view").name("??????").menuId(menuId).build());

        Long roleId = menuMap.get(ROLE);
        resourceList.add(Resource.builder().code("role:add").name("??????").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:delete").name("??????").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:export").name("??????").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:import").name("??????").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:update").name("??????").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:view").name("??????").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:config").name("??????").menuId(roleId).build());
        resourceList.add(Resource.builder().code("role:auth").name("??????").menuId(roleId).build());

        Long parameterId = menuMap.get(PARAMETER);
        resourceList.add(Resource.builder().code("parameter:add").name("??????").menuId(parameterId).build());
        resourceList.add(Resource.builder().code("parameter:delete").name("??????").menuId(parameterId).build());
        resourceList.add(Resource.builder().code("parameter:export").name("??????").menuId(parameterId).build());
        resourceList.add(Resource.builder().code("parameter:import").name("??????").menuId(parameterId).build());
        resourceList.add(Resource.builder().code("parameter:update").name("??????").menuId(parameterId).build());
        resourceList.add(Resource.builder().code("parameter:view").name("??????").menuId(parameterId).build());

        Long areaId = menuMap.get(AREA);
        resourceList.add(Resource.builder().code("area:add").name("??????").menuId(areaId).build());
        resourceList.add(Resource.builder().code("area:delete").name("??????").menuId(areaId).build());
        resourceList.add(Resource.builder().code("area:export").name("??????").menuId(areaId).build());
        resourceList.add(Resource.builder().code("area:import").name("??????").menuId(areaId).build());
        resourceList.add(Resource.builder().code("area:update").name("??????").menuId(areaId).build());
        resourceList.add(Resource.builder().code("area:view").name("??????").menuId(areaId).build());

        Long dictId = menuMap.get(DICT);
        resourceList.add(Resource.builder().code("dict:add").name("??????").menuId(dictId).build());
        resourceList.add(Resource.builder().code("dict:delete").name("??????").menuId(dictId).build());
        resourceList.add(Resource.builder().code("dict:export").name("??????").menuId(dictId).build());
        resourceList.add(Resource.builder().code("dict:import").name("??????").menuId(dictId).build());
        resourceList.add(Resource.builder().code("dict:update").name("??????").menuId(dictId).build());
        resourceList.add(Resource.builder().code("dict:view").name("??????").menuId(dictId).build());


        Long applicationId = menuMap.get(APPLICATION);
        resourceList.add(Resource.builder().code("application:add").name("??????").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:delete").name("??????").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:export").name("??????").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:update").name("??????").menuId(applicationId).build());
        resourceList.add(Resource.builder().code("application:view").name("??????").menuId(applicationId).build());

        Long loginLogId = menuMap.get(LOGIN_LOG);
        resourceList.add(Resource.builder().code("loginLog:delete").name("??????").menuId(loginLogId).build());
        resourceList.add(Resource.builder().code("loginLog:export").name("??????").menuId(loginLogId).build());
        resourceList.add(Resource.builder().code("loginLog:view").name("??????").menuId(loginLogId).build());

        Long optLogId = menuMap.get(OPT_LOG);
        resourceList.add(Resource.builder().code("optLog:delete").name("??????").menuId(optLogId).build());
        resourceList.add(Resource.builder().code("optLog:export").name("??????").menuId(optLogId).build());
        resourceList.add(Resource.builder().code("optLog:view").name("??????").menuId(optLogId).build());

        Long msgsId = menuMap.get(MSGS);
        resourceList.add(Resource.builder().code("msgs:add").name("??????").menuId(msgsId).build());
        resourceList.add(Resource.builder().code("msgs:delete").name("??????").menuId(msgsId).build());
        resourceList.add(Resource.builder().code("msgs:export").name("??????").menuId(msgsId).build());
        resourceList.add(Resource.builder().code("msgs:import").name("??????").menuId(msgsId).build());
        resourceList.add(Resource.builder().code("msgs:update").name("??????").menuId(msgsId).build());
        resourceList.add(Resource.builder().code("msgs:view").name("??????").menuId(msgsId).build());
        resourceList.add(Resource.builder().code("msgs:mark").name("????????????").menuId(msgsId).build());

        Long smsManageId = menuMap.get(SMS_MANAGE);
        resourceList.add(Resource.builder().code("sms:manage:add").name("??????").menuId(smsManageId).build());
        resourceList.add(Resource.builder().code("sms:manage:delete").name("??????").menuId(smsManageId).build());
        resourceList.add(Resource.builder().code("sms:manage:export").name("??????").menuId(smsManageId).build());
        resourceList.add(Resource.builder().code("sms:manage:import").name("??????").menuId(smsManageId).build());
        resourceList.add(Resource.builder().code("sms:manage:update").name("??????").menuId(smsManageId).build());
        resourceList.add(Resource.builder().code("sms:manage:view").name("??????").menuId(smsManageId).build());

        Long smsTemplateId = menuMap.get(SMS_TEMPLATE);
        resourceList.add(Resource.builder().code("sms:template:add").name("??????").menuId(smsTemplateId).build());
        resourceList.add(Resource.builder().code("sms:template:delete").name("??????").menuId(smsTemplateId).build());
        resourceList.add(Resource.builder().code("sms:template:export").name("??????").menuId(smsTemplateId).build());
        resourceList.add(Resource.builder().code("sms:template:import").name("??????").menuId(smsTemplateId).build());
        resourceList.add(Resource.builder().code("sms:template:update").name("??????").menuId(smsTemplateId).build());
        resourceList.add(Resource.builder().code("sms:template:view").name("??????").menuId(smsTemplateId).build());

        Long fileId = menuMap.get(FILE);
        resourceList.add(Resource.builder().code("file:add").name("??????").menuId(fileId).build());
        resourceList.add(Resource.builder().code("file:delete").name("??????").menuId(fileId).build());
        resourceList.add(Resource.builder().code("file:download").name("??????").menuId(fileId).build());
        resourceList.add(Resource.builder().code("file:update").name("??????").menuId(fileId).build());
        resourceList.add(Resource.builder().code("file:view").name("??????").menuId(fileId).build());

        return resourceService.saveBatch(resourceList);
    }

    private boolean initMenu(Snowflake snowflake, List<Menu> menuList, Map<String, Long> menuMap) {
        Long menuUserCenterId = snowflake.nextId();
        Long authId = snowflake.nextId();
        Long baseId = snowflake.nextId();
        Long developerId = snowflake.nextId();
        Long msgsId = snowflake.nextId();
        Long smsId = snowflake.nextId();
        Long fileId = snowflake.nextId();
        Long gatewayId = snowflake.nextId();
        Long activitiId = snowflake.nextId();
        // 1?????????
        menuList.add(Menu.builder().id(menuUserCenterId).label("????????????").describe("??????????????????").path("/user").component("Layout").icon("el-icon-user-solid").sortValue(1).build());
        menuList.add(Menu.builder().id(authId).label("????????????").describe("??????????????????").path("/auth").component("Layout").icon("el-icon-lock").sortValue(2).build());
        menuList.add(Menu.builder().id(baseId).label("????????????").describe("???????????????").path("/base").component("Layout").icon("el-icon-set-up").sortValue(3).build());
        menuList.add(Menu.builder().id(developerId).label("???????????????").describe("?????????").path("/developer").component("Layout").icon("el-icon-user-solid").sortValue(4).build());
        menuList.add(Menu.builder().id(msgsId).label("????????????").describe("?????????").path("/msgs").component("Layout").icon("el-icon-chat-line-square").sortValue(5).build());
        menuList.add(Menu.builder().id(smsId).label("????????????").describe("????????????").path("/sms").component("Layout").icon("el-icon-chat-line-round").sortValue(6).build());
        menuList.add(Menu.builder().id(fileId).label("????????????").describe("????????????").path("/file").component("Layout").icon("el-icon-folder-add").sortValue(7).build());
        menuList.add(Menu.builder().id(gatewayId).label("????????????").describe("????????????").path("/gateway").component("Layout").icon("el-icon-odometer").sortValue(8).build());
        menuList.add(Menu.builder().id(activitiId).label("????????????").describe("????????????").path("/activiti").component("Layout").icon("el-icon-set-up").sortValue(9).build());

        // 2?????????
        Long orgId = snowflake.nextId();
        menuMap.put(ORG, orgId);
        menuList.add(Menu.builder().id(orgId).parentId(menuUserCenterId).label("????????????").path("/user/org").component("changqing/user/org/Index").sortValue(0).build());
        Long stationId = snowflake.nextId();
        menuMap.put(STATION, stationId);
        menuList.add(Menu.builder().id(stationId).parentId(menuUserCenterId).label("????????????").path("/user/station").component("changqing/user/station/Index").sortValue(1).build());
        Long userId = snowflake.nextId();
        menuMap.put(USER, userId);
        menuList.add(Menu.builder().id(userId).parentId(menuUserCenterId).label("????????????").path("/user/user").component("changqing/user/user/Index").sortValue(2).build());

        Long roleId = snowflake.nextId();
        menuMap.put(ROLE, roleId);
        menuList.add(Menu.builder().id(roleId).parentId(authId).label("????????????").path("/auth/role").component("changqing/auth/role/Index").sortValue(1).build());
        Long menuId = snowflake.nextId();
        menuMap.put(MENU, menuId);
        menuList.add(Menu.builder().id(menuId).parentId(authId).label("????????????").path("/auth/user").component("changqing/auth/menu/Index").sortValue(2).build());

        Long parameterId = snowflake.nextId();
        menuMap.put(PARAMETER, parameterId);
        menuList.add(Menu.builder().id(parameterId).parentId(baseId).label("????????????").path("/base/parameter").component("changqing/base/parameter/Index").sortValue(2).build());
        Long dictId = snowflake.nextId();
        menuMap.put(DICT, dictId);
        menuList.add(Menu.builder().id(dictId).parentId(baseId).label("??????????????????").path("/base/dict").component("changqing/base/dict/Index").sortValue(0).build());
        Long areaId = snowflake.nextId();
        menuMap.put(AREA, areaId);
        menuList.add(Menu.builder().id(areaId).parentId(baseId).label("??????????????????").path("/base/area").component("changqing/base/area/Index").sortValue(1).build());

        Long applicationApi = snowflake.nextId();
        menuMap.put(APPLICATION, applicationApi);
        menuList.add(Menu.builder().id(applicationApi).parentId(developerId).label("????????????").path("/developer/application").component("changqing/developer/application/Index").sortValue(1).build());
        Long optLogId = snowflake.nextId();
        menuMap.put(OPT_LOG, optLogId);
        menuList.add(Menu.builder().id(optLogId).parentId(developerId).label("????????????").path("/developer/optLog").component("changqing/developer/optLog/Index").sortValue(3).build());
        Long loginLogId = snowflake.nextId();
        menuMap.put(LOGIN_LOG, loginLogId);
        menuList.add(Menu.builder().id(loginLogId).parentId(developerId).label("????????????").path("/developer/loginLog").component("changqing/developer/loginLog/Index").sortValue(4).build());
        Long dbId = snowflake.nextId();
        menuMap.put(DB, dbId);
        menuList.add(Menu.builder().id(dbId).parentId(developerId).label("???????????????").path("/developer/db").component("changqing/developer/db/Index").sortValue(5).build());

        Long interId = snowflake.nextId();
        menuList.add(Menu.builder().id(interId).parentId(developerId).label("????????????").path("http://127.0.0.1:8760/api/gate/doc.html").component("Layout").sortValue(6).build());
        Long nacosId = snowflake.nextId();
        menuList.add(Menu.builder().id(nacosId).parentId(developerId).label("??????&????????????").path("http://127.0.0.1:8848/nacos").component("Layout").sortValue(7).build());
        Long redisId = snowflake.nextId();
        menuList.add(Menu.builder().id(redisId).parentId(developerId).label("????????????").path("http://www.baidu.com").component("Layout").sortValue(8).build());
        Long serverId = snowflake.nextId();
        menuList.add(Menu.builder().id(serverId).parentId(developerId).label("???????????????").path("http://127.0.0.1:8762/changqing-monitor").component("Layout").sortValue(9).build());
        Long jobsId = snowflake.nextId();
        menuList.add(Menu.builder().id(jobsId).parentId(developerId).label("??????????????????").path("http://127.0.0.1:8767/changqing-jobs-server").component("Layout").sortValue(10).build());
        Long zipkinId = snowflake.nextId();
        menuList.add(Menu.builder().id(zipkinId).parentId(developerId).label("??????????????????").path("http://127.0.0.1:8772/zipkin").component("Layout").sortValue(11).build());

        Long msgsPushId = snowflake.nextId();
        menuList.add(Menu.builder().id(msgsPushId).parentId(msgsId).label("????????????").path("/msgs/sendMsgs").component("changqing/msgs/sendMsgs/Index").sortValue(1).build());
        Long myMsgsId = snowflake.nextId();
        menuMap.put(MSGS, myMsgsId);
        menuList.add(Menu.builder().id(myMsgsId).parentId(msgsId).label("????????????").path("/msgs/myMsgs").component("changqing/msgs/myMsgs/Index").sortValue(2).build());

        Long smsManageId = snowflake.nextId();
        menuMap.put(SMS_MANAGE, smsManageId);
        menuList.add(Menu.builder().id(smsManageId).parentId(smsId).label("????????????").path("/sms/manage").component("changqing/sms/manage/Index").sortValue(1).build());
        Long templateId = snowflake.nextId();
        menuMap.put(SMS_TEMPLATE, templateId);
        menuList.add(Menu.builder().id(templateId).parentId(smsId).label("????????????").path("/sms/template").component("changqing/sms/template/Index").sortValue(2).build());

        Long attachmentId = snowflake.nextId();
        menuMap.put(FILE, attachmentId);
        menuList.add(Menu.builder().id(attachmentId).parentId(fileId).label("????????????").path("/file/attachment").component("changqing/file/attachment/Index").sortValue(1).build());


        menuList.add(Menu.builder().parentId(gatewayId).label("????????????").path("/gateway/ratelimiter").component("changqing/gateway/ratelimiter/Index").sortValue(1).build());
        menuList.add(Menu.builder().parentId(gatewayId).label("????????????").path("/gateway/blocklist").component("changqing/gateway/blocklist/Index").sortValue(2).build());

        Long leaveId = snowflake.nextId();
        Long reId = snowflake.nextId();
        menuList.add(Menu.builder().parentId(activitiId).label("????????????").path("/activiti/modelManager").component("changqing/activiti/modelManager/Index").sortValue(1).build());
        menuList.add(Menu.builder().parentId(activitiId).label("????????????").path("/activiti/deploymentManager").component("changqing/activiti/deploymentManager/Index").sortValue(2).build());
        menuList.add(Menu.builder().id(leaveId).parentId(activitiId).label("????????????").path("/activiti/leave").component("changqing/activiti/leave/Index").sortValue(3).build());
        menuList.add(Menu.builder().id(reId).parentId(activitiId).label("????????????").path("/activiti/reimbursement").component("changqing/activiti/reimbursement/Index").sortValue(4).build());
        menuList.add(Menu.builder().parentId(leaveId).label("????????????").path("/activiti/leave/instantManager").component("changqing/activiti/leave/instantManager/Index").sortValue(1).build());
        menuList.add(Menu.builder().parentId(leaveId).label("????????????").path("/activiti/leave/ruTask").component("changqing/activiti/leave/ruTask/Index").sortValue(2).build());
        menuList.add(Menu.builder().parentId(reId).label("????????????").path("/activiti/reimbursement/instantManager").component("changqing/activiti/reimbursement/instantManager/Index").sortValue(1).build());
        menuList.add(Menu.builder().parentId(reId).label("????????????").path("/activiti/reimbursement/ruTask").component("changqing/activiti/reimbursement/ruTask/Index").sortValue(2).build());


        return menuService.saveBatch(menuList);
    }


    private boolean initDict() {
        List<Dictionary> dictionaryList = new ArrayList<>();
        dictionaryList.add(Dictionary.builder().type("NATION").name("??????").build());
        dictionaryList.add(Dictionary.builder().type("POSITION_STATUS").name("????????????").build());
        dictionaryList.add(Dictionary.builder().type("EDUCATION").name("??????").build());
        dictionaryList.add(Dictionary.builder().type("AREA_LEVEL").name("????????????").build());
        dictionaryService.saveBatch(dictionaryList);

        List<DictionaryItem> dictionaryItemList = new ArrayList<>();

        Dictionary nation = dictionaryList.get(0);
        Integer nationSort = 1;
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_hanz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_zz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_mz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_hz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_miaoz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_wwez").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_tjz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_yz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_mgz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_zhangz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_byz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_dz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_yaoz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_cxz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_bz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_hnz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_hskz").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_lz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_daiz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_sz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_llz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_glz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_dxz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_gsz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_lhz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_shuiz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_wz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_nxz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_qz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_tz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_zlz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_xbz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_kehzz").name("???????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_dwz").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_jpz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_mlz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_slz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_tjkz").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_acz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_pmz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_ewkz").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_nz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_jz").name("??????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_jnz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_daz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_baz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_elsz").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_ygz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_wzbkz").name("???????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_mbz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_elcz").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_dlz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_tkez").name("????????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_hzz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_lbz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_blz").name("?????????").sortValue(nationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(nation.getId()).dictionaryType(nation.getType())
                .code("mz_qt").name("??????").sortValue(nationSort++).build());


        Dictionary positionStatus = dictionaryList.get(1);
        Integer positionStatusSort = 1;
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(positionStatus.getId()).dictionaryType(positionStatus.getType())
                .code("WORKING").name("??????").sortValue(positionStatusSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(positionStatus.getId()).dictionaryType(positionStatus.getType())
                .code("QUIT").name("??????").sortValue(positionStatusSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(positionStatus.getId()).dictionaryType(positionStatus.getType())
                .code("LEAVE").name("??????").sortValue(positionStatusSort++).build());

        Dictionary education = dictionaryList.get(2);
        Integer educationSort = 1;
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("XIAOXUE").name("??????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder()
                .dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("ZHONGXUE").name("??????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("GAOZHONG").name("??????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("ZHUANKE").name("??????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("COLLEGE").name("??????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("SUOSHI").name("??????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("BOSHI").name("??????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("BOSHIHOU").name("?????????").sortValue(educationSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(education.getId()).dictionaryType(education.getType())
                .code("QITA").name("??????").sortValue(educationSort++).build());

        Dictionary areaLevel = dictionaryList.get(3);
        Integer areaLevelSort = 1;
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(areaLevel.getId()).dictionaryType(areaLevel.getType())
                .code("COUNTRY").name("??????").sortValue(areaLevelSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(areaLevel.getId()).dictionaryType(areaLevel.getType())
                .code("PROVINCE").name("??????/?????????").sortValue(areaLevelSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(areaLevel.getId()).dictionaryType(areaLevel.getType())
                .code("CITY").name("??????").sortValue(areaLevelSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(areaLevel.getId()).dictionaryType(areaLevel.getType())
                .code("COUNTY").name("??????").sortValue(areaLevelSort++).build());
        dictionaryItemList.add(DictionaryItem.builder().dictionaryId(areaLevel.getId()).dictionaryType(areaLevel.getType())
                .code("TOWNS").name("??????").sortValue(areaLevelSort++).build());

        return dictionaryItemService.saveBatch(dictionaryItemList);
    }

    @Override
    public boolean reset(String tenant) {
        //TODO ?????????
        // 1???????????????????????????
        // 2?????????????????? tenant
        // 3?????????????????? ????????????
        //        init(tenant);
        return true;
    }

    @Override
    public boolean delete(List<Long> ids, List<String> tenantCodeList) {
        // ???????????????????????????????????????
        //TODO ?????????
        // 1,???????????????????????????
        // ??????????????????????????????
        return true;
    }
}
