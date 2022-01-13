package com.changqing.gov.tenant.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.changqing.gov.base.R;
import com.changqing.gov.tenant.dto.DataSourcePropertyDTO;

/**
 * 网关服务初始化数据源
 * <p>
 * 若你使用的是zuul:
 * 1. 将下面的 changqing-gateway-server 改成 changqing-zuul-server ！！！！
 * 2. 将path 改成 /api/gate/ds
 *
 * @author changqing
 * @date 2020年04月05日18:18:26
 */
//@FeignClient(name = "${changqing.feign.gateway-server:changqing-zuul-server}", path = "/api/gate/ds")
@FeignClient(name = "${changqing.feign.group-server:changqing-group-server}", path = "/ds")
public interface GroupDsApi {

    /**
     * 初始化数据源
     *
     * @param tenantConnect
     * @return
     */
    @RequestMapping(value = "/initConnect", method = RequestMethod.POST)
    R<Boolean> initConnect(@RequestBody DataSourcePropertyDTO tenantConnect);

    /**
     * 删除数据源
     *
     * @param tenant
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    R<Boolean> remove(@RequestParam(value = "tenant") String tenant);
}
