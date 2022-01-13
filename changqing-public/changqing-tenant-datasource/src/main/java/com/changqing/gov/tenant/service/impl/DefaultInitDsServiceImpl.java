package com.changqing.gov.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.changqing.gov.base.R;
import com.changqing.gov.common.constant.BizConstant;
import com.changqing.gov.exception.BizException;
import com.changqing.gov.mq.properties.MqProperties;
import com.changqing.gov.tenant.api.AuthorityDsApi;
import com.changqing.gov.tenant.api.FileDsApi;
import com.changqing.gov.tenant.api.GatewayDsApi;
import com.changqing.gov.tenant.api.GroupDsApi;
import com.changqing.gov.tenant.api.MsgsDsApi;
import com.changqing.gov.tenant.api.OauthDsApi;
import com.changqing.gov.tenant.dto.DataSourcePropertyDTO;
import com.changqing.gov.tenant.entity.DatasourceConfig;
import com.changqing.gov.tenant.service.InitDsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 没有开启消息队列就只能轮训了
 *
 * @author changqing
 * @date 2020年04月05日16:27:03
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = MqProperties.PREFIX, name = "enabled", havingValue = "false", matchIfMissing = true)
public class DefaultInitDsServiceImpl implements InitDsService {

    @Autowired
    private AuthorityDsApi authorityDsApi;
    @Autowired
    private OauthDsApi oauthDsApi;
    @Autowired
    private FileDsApi fileDsApi;
    @Autowired
    private MsgsDsApi msgsDsApi;
    @Autowired
    private GatewayDsApi gatewayDsApi;
    @Autowired
    private GroupDsApi groupDsApi;

    @Override
    public boolean removeDataSource(String tenant) {
        // 权限服务
        R<Boolean> authority = authorityDsApi.remove(tenant);
        // SpringBoot项目就不需要调用以下方法了
        // oauth
        R<Boolean> oauth = oauthDsApi.remove(tenant);
        // file
        R<Boolean> file = fileDsApi.remove(tenant);
        // msgs
        R<Boolean> msgs = msgsDsApi.remove(tenant);

        R<Boolean> gateway = gatewayDsApi.remove(tenant);
        log.info("authority={}", authority);
        log.info("gateway={} ", gateway);
        log.info("oauth={}, file={}, msgs={}", oauth, file, msgs);
        return true;
    }

    @Override
    public boolean initConnect(Map<String, DatasourceConfig> typeMap) {

        // 权限服务
        DataSourcePropertyDTO authorityDsp = BeanUtil.toBean(typeMap.get(BizConstant.AUTHORITY), DataSourcePropertyDTO.class);
        R<Boolean> authority = authorityDsApi.initConnect(authorityDsp);

        // SpringBoot项目就不需要调用以下方法了
        // oauth
        DataSourcePropertyDTO oauthDsp = BeanUtil.toBean(typeMap.get(BizConstant.OAUTH), DataSourcePropertyDTO.class);
        R<Boolean> oauth = oauthDsApi.initConnect(oauthDsp);
        // file
        DataSourcePropertyDTO fileDsp = BeanUtil.toBean(typeMap.get(BizConstant.FILE), DataSourcePropertyDTO.class);
        R<Boolean> file = fileDsApi.initConnect(fileDsp);
        // msgs
        DataSourcePropertyDTO msgsDsp = BeanUtil.toBean(typeMap.get(BizConstant.MSGS), DataSourcePropertyDTO.class);
        R<Boolean> msgs = msgsDsApi.initConnect(msgsDsp);
        // 网关
        DataSourcePropertyDTO gateDsp = BeanUtil.toBean(typeMap.get(BizConstant.GATE), DataSourcePropertyDTO.class);
        R<Boolean> gate = gatewayDsApi.initConnect(gateDsp);
        // 其他业务
        DataSourcePropertyDTO groupDsp = BeanUtil.toBean(typeMap.get(BizConstant.GROUP), DataSourcePropertyDTO.class);
        R<Boolean> group = groupDsApi.initConnect(groupDsp);
        log.info("authority={}", authority);
        log.info("gateway={} ", gate);
        log.info("oauth={}, file={}, msgs={}", oauth, file, msgs);
        log.info("group={} ", group);
        if (gate.getIsError() || gate.getData() == null || !gate.getData()) {
            throw new BizException("初始化网关服务数据源异常:" + gate.getMsg());
        }
        if (oauth.getIsError() || oauth.getData() == null || !oauth.getData()) {
            throw new BizException("初始化认证服务数据源异常:" + oauth.getMsg());
        }
        // 需要将全部服务的数据源连接成功，才叫成功，任意一个服务连接失败，都需要重新连接。开发环境，仅仅启动几个服务时，自行将未启动的服务注释掉，保证代码正确执行
        if (authority.getIsError() || authority.getData() == null || !authority.getData()) {
            throw new BizException("初始化权限服务数据源异常:" + authority.getMsg());
        }
        if (file.getIsError() || file.getData() == null || !file.getData()) {
            throw new BizException("初始化文件服务数据源异常:" + file.getMsg());
        }
        if (msgs.getIsError() || msgs.getData() == null || !msgs.getData()) {
            throw new BizException("初始化消息服务数据源异常:" + authority.getMsg());
        }
        if (group.getIsError() || group.getData() == null || !group.getData()) {
            throw new BizException("初始化业务服务数据源异常:" + group.getMsg());
        }
        return true;
    }
}
