package com.changqing.gov.tenant.api;

import com.changqing.gov.base.R;
import com.changqing.gov.tenant.dto.DataSourcePropertyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * file服务初始化数据源
 *
 * @author changqing
 * @date 2020年04月05日18:18:26
 */
@FeignClient(name = "${changqing.feign.authority-server:changqing-authority-server}", path = "/ds")
public interface AuthorityDsApi {

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
