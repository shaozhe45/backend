package com.changqing.gov.oauth.api;

import com.changqing.gov.oauth.api.hystrix.OrgApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 岗位API
 *
 * @author changqing
 * @date 2019/08/02
 */
@FeignClient(name = "${changqing.feign.oauth-server:changqing-oauth-server}", path = "/org",
        qualifier = "orgApi", fallback = OrgApiFallback.class)
public interface OrgApi {

    /**
     * 根据 id 查询组织，并转换成Map结构
     *
     * @param ids
     * @return
     */
    @GetMapping("/findOrgByIds")
    Map<Serializable, Object> findOrgByIds(@RequestParam(value = "ids") Set<Serializable> ids);

    /**
     * 根据 id 查询组织名称，并转换成Map结构
     *
     * @param ids
     * @return
     */
    @GetMapping("/findOrgNameByIds")
    Map<Serializable, Object> findOrgNameByIds(@RequestParam(value = "ids") Set<Serializable> ids);

}
