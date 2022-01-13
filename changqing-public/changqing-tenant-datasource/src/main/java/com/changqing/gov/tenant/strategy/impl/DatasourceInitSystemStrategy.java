package com.changqing.gov.tenant.strategy.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.changqing.gov.common.constant.BizConstant;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.tenant.dao.InitDatabaseMapper;
import com.changqing.gov.tenant.dto.TenantConnectDTO;
import com.changqing.gov.tenant.entity.DatasourceConfig;
import com.changqing.gov.tenant.entity.TenantDatasourceConfig;
import com.changqing.gov.tenant.enumeration.TenantConnectTypeEnum;
import com.changqing.gov.tenant.enumeration.TenantStatusEnum;
import com.changqing.gov.tenant.service.DataSourceService;
import com.changqing.gov.tenant.service.DatasourceConfigService;
import com.changqing.gov.tenant.service.InitDsService;
import com.changqing.gov.tenant.service.TenantDatasourceConfigService;
import com.changqing.gov.tenant.strategy.InitSystemStrategy;
import com.changqing.gov.utils.MapHelper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 初始化系统
 * <p>
 * 初始化规则：
 * changqing-authority-server/src/main/resources/sql 路径存放8个sql文件 (每个库对应一个文件)
 * changqing_base.sql            # 基础库：权限、消息，短信，邮件，文件等
 * data_changqing_base.sql       # 基础库数据： 如初始用户，初始角色，初始菜单
 *
 * @author changqing
 * @date 2019/10/25
 */
@Service("DATASOURCE")
@Slf4j
public class DatasourceInitSystemStrategy implements InitSystemStrategy {

    @Autowired
    private DatasourceConfigService datasourceConfigService;
    @Autowired
    private TenantDatasourceConfigService tenantDatasourceConfigService;

    @Autowired
    private InitDatabaseMapper initDbMapper;
    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
    private InitDsService initDsService;
    @Value("${spring.application.name:changqing-authority-server}")
    private String applicationName;

    /**
     * 启动项目时，调用初始化数据源
     *
     * @return
     */
    @DS("master")
    public boolean initDataSource() {
        // LOCAL 类型的数据源初始化
        List<String> list = initDbMapper.selectTenantCodeList(TenantStatusEnum.NORMAL.name(), TenantConnectTypeEnum.LOCAL.name());
        list.forEach((tenant) -> {
            dataSourceService.addLocalDynamicRoutingDataSource(tenant);
        });

        // REMOTE 类型的数据源初始化
        List<DatasourceConfig> dcList = datasourceConfigService.listByApplication(applicationName, TenantStatusEnum.NORMAL.name(), TenantConnectTypeEnum.REMOTE.name());
        dcList.forEach(dc -> {
            // 权限服务
            DataSourceProperty dataSourceProperty = new DataSourceProperty();
            BeanUtils.copyProperties(dc, dataSourceProperty);
            dataSourceService.addDynamicRoutingDataSource(dataSourceProperty);
        });
        log.debug("初始化租户数据源成功");
        return true;
    }

    /**
     * 求优化！
     *
     * @param tenantConnect 链接信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initConnect(TenantConnectDTO tenantConnect) {
        Map<String, DatasourceConfig> typeMap = new HashMap<>(6);
        if (TenantConnectTypeEnum.REMOTE.eq(tenantConnect.getConnectType())) {

            Long authorityDatasource = tenantConnect.getAuthorityDatasource();
            Long fileDatasource = tenantConnect.getFileDatasource();
            fileDatasource = fileDatasource == null ? authorityDatasource : fileDatasource;
            Long msgsDatasource = tenantConnect.getMsgsDatasource();
            msgsDatasource = msgsDatasource == null ? authorityDatasource : msgsDatasource;
            Long oauthDatasource = tenantConnect.getOauthDatasource();
            oauthDatasource = oauthDatasource == null ? authorityDatasource : oauthDatasource;
            Long gateDatasource = tenantConnect.getGateDatasource();
            gateDatasource = gateDatasource == null ? authorityDatasource : gateDatasource;

            Long groupDatasource = tenantConnect.getGroupDatasource();
            groupDatasource = groupDatasource == null ? authorityDatasource : groupDatasource;
            List<DatasourceConfig> dcList = datasourceConfigService.listByIds(Sets.newHashSet(authorityDatasource, fileDatasource, msgsDatasource, oauthDatasource, gateDatasource));
            dcList.forEach(item -> {
                item.setType(tenantConnect.getConnectType());
                item.setPoolName(tenantConnect.getTenant());
            });
            Map<Long, DatasourceConfig> dcMap = MapHelper.uniqueIndex(dcList, DatasourceConfig::getId, (data) -> data);

            DatasourceConfig authorityDc = dcMap.get(authorityDatasource);
            typeMap.put(BizConstant.AUTHORITY, authorityDc);
            typeMap.put(BizConstant.FILE, dcMap.getOrDefault(fileDatasource, authorityDc));
            typeMap.put(BizConstant.MSGS, dcMap.getOrDefault(msgsDatasource, authorityDc));
            typeMap.put(BizConstant.OAUTH, dcMap.getOrDefault(oauthDatasource, authorityDc));
            typeMap.put(BizConstant.GATE, dcMap.getOrDefault(gateDatasource, authorityDc));
            typeMap.put(BizConstant.GROUP, dcMap.getOrDefault(groupDatasource, authorityDc));

            tenantDatasourceConfigService.remove(Wraps.<TenantDatasourceConfig>lbQ().eq(TenantDatasourceConfig::getTenantId, tenantConnect.getId()));

            List<TenantDatasourceConfig> list = new ArrayList<>();
            list.add(TenantDatasourceConfig.builder().application(BizConstant.AUTHORITY).tenantId(tenantConnect.getId()).datasourceConfigId(authorityDatasource).build());
            list.add(TenantDatasourceConfig.builder().application(BizConstant.FILE).tenantId(tenantConnect.getId()).datasourceConfigId(fileDatasource).build());
            list.add(TenantDatasourceConfig.builder().application(BizConstant.MSGS).tenantId(tenantConnect.getId()).datasourceConfigId(msgsDatasource).build());
            list.add(TenantDatasourceConfig.builder().application(BizConstant.OAUTH).tenantId(tenantConnect.getId()).datasourceConfigId(oauthDatasource).build());
            list.add(TenantDatasourceConfig.builder().application(BizConstant.GATE).tenantId(tenantConnect.getId()).datasourceConfigId(gateDatasource).build());
            list.add(TenantDatasourceConfig.builder().application(BizConstant.GROUP).tenantId(tenantConnect.getId()).datasourceConfigId(groupDatasource).build());
            tenantDatasourceConfigService.saveBatch(list);
        } else {
            String tenant = tenantConnect.getTenant();
            DatasourceConfig dto = new DatasourceConfig();
            dto.setType(tenantConnect.getConnectType());
            dto.setPoolName(tenant);

            typeMap.put(BizConstant.AUTHORITY, dto);
            typeMap.put(BizConstant.FILE, dto);
            typeMap.put(BizConstant.MSGS, dto);
            typeMap.put(BizConstant.OAUTH, dto);
            typeMap.put(BizConstant.GATE, dto);
            typeMap.put(BizConstant.GROUP, dto);
        }

        // 动态初始化数据源
        return initDsService.initConnect(typeMap);
    }

    @Override
    public boolean reset(String tenant) {

        return true;
    }

    @Override
    public boolean delete(List<Long> ids, List<String> tenantCodeList) {
        if (tenantCodeList.isEmpty()) {
            return true;
        }
        tenantDatasourceConfigService.remove(Wraps.<TenantDatasourceConfig>lbQ().in(TenantDatasourceConfig::getTenantId, ids));

        tenantCodeList.forEach(initDsService::removeDataSource);
        return true;
    }
}
