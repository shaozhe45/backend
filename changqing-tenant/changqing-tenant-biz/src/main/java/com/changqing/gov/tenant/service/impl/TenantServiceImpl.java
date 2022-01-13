package com.changqing.gov.tenant.service.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.base.service.SuperCacheServiceImpl;
import com.changqing.gov.database.mybatis.conditions.Wraps;
import com.changqing.gov.tenant.dao.TenantMapper;
import com.changqing.gov.tenant.dto.TenantConnectDTO;
import com.changqing.gov.tenant.dto.TenantSaveDTO;
import com.changqing.gov.tenant.entity.Tenant;
import com.changqing.gov.tenant.enumeration.TenantStatusEnum;
import com.changqing.gov.tenant.enumeration.TenantTypeEnum;
import com.changqing.gov.tenant.service.TenantService;
import com.changqing.gov.tenant.strategy.InitSystemContext;
import com.changqing.gov.utils.BeanPlusUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.changqing.gov.common.constant.CacheKey.TENANT;
import static com.changqing.gov.common.constant.CacheKey.TENANT_NAME;
import static com.changqing.gov.utils.BizAssert.isFalse;

/**
 * <p>
 * 业务实现类
 * 企业
 * </p>
 *
 * @author changqing
 * @date 2019-10-24
 */
@Slf4j
@Service
@DS("master")
public class TenantServiceImpl extends SuperCacheServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private InitSystemContext initSystemContext;

    @Override
    protected String getRegion() {
        return TENANT;
    }

    /**
     * tanant_name:{tenantcode} -> id 只存租户的id，然后根据id再次查询缓存，这样子的好处是，删除或者修改租户信息时，只需要根据id淘汰缓存即可
     * 缺点就是 每次查询，需要多查一次缓存
     *
     * @param tenant
     * @return
     */
    @Override
    public Tenant getByCode(String tenant) {
        // 优化前
        /*String key = buildKey(tenant);
        CacheObject cacheObject = cacheChannel.get(TENANT_NAME, key, (k) -> {
            Tenant one = super.getOne(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenant));
            return one != null ? one.getId() : null;
        });
        if (cacheObject.getValue() == null) {
            return null;
        }
        Long id = (Long) cacheObject.getValue();
        return getByIdCache(id);*/

        // 优化后
        String key = buildKey(tenant);
        Function<String, Object> loader = (k) -> getObj(Wraps.<Tenant>lbQ().select(Tenant::getId).eq(Tenant::getCode, tenant), Convert::toLong);
        return getByKey(TENANT_NAME, key, loader);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant save(TenantSaveDTO data) {
        // defaults 库
        isFalse(check(data.getCode()), "编码重复，请重新输入");

        // 1， 保存租户 (默认库)
        Tenant tenant = BeanPlusUtil.toBean(data, Tenant.class);
        tenant.setStatus(TenantStatusEnum.WAIT_INIT);
        tenant.setType(TenantTypeEnum.CREATE);
        // defaults 库
        save(tenant);

        String key = buildKey(tenant.getCode());
        cacheChannel.set(TENANT_NAME, key, tenant.getId());

        // 3, 初始化库，表, 数据  2.5.1以后，将初始化数据源和创建租户库逻辑分离 参考 this::connect
//        initSystemContext.init(tenant.getCode());
        return tenant;
    }

    @Override
    public boolean check(String tenantCode) {
        return super.count(Wraps.<Tenant>lbQ().eq(Tenant::getCode, tenantCode)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(List<Long> ids) {
        List<String> tenantCodeList = listObjs(Wraps.<Tenant>lbQ().select(Tenant::getCode).in(Tenant::getId, ids), Convert::toStr);
        if (tenantCodeList.isEmpty()) {
            return true;
        }
        removeByIds(ids);

        return initSystemContext.delete(ids, tenantCodeList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean connect(TenantConnectDTO tenantConnect) {
        boolean flag = initSystemContext.initConnect(tenantConnect);
        if (flag) {
            updateById(Tenant.builder().id(tenantConnect.getId()).connectType(tenantConnect.getConnectType())
                    .status(TenantStatusEnum.NORMAL).build());
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(List<Long> ids, TenantStatusEnum status) {
        boolean update = super.update(Wraps.<Tenant>lbU().set(Tenant::getStatus, status)
                .in(Tenant::getId, ids));

        String[] keys = ids.stream().map(this::key).toArray(String[]::new);
        cacheChannel.evict(getRegion(), keys);
        return update;
    }
    @Override
    public JSONObject GetTenantDetail(String code){
        JSONObject jsonObject = new JSONObject();

        List<Map<String,String>> dt=tenantMapper.GetTenantDetail(code);
        for (Map<String,String> map:dt){
            jsonObject.put("name",map.get("name"));
            jsonObject.put("id",map.get("id"));
            jsonObject.put("code",map.get("code"));
            jsonObject.put("type",map.get("type"));
            jsonObject.put("connectType",map.get("connect_type"));
            jsonObject.put("status",map.get("status"));
            jsonObject.put("duty",map.get("duty"));
            jsonObject.put("expirationTime",map.get("expiration_time"));
            jsonObject.put("logo",map.get("logo"));
            jsonObject.put("describe",map.get("describe_"));
            jsonObject.put("create_time",map.get("create_time"));
        }
        return jsonObject;
    }
}
