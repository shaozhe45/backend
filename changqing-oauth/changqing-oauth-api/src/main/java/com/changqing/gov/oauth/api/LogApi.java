package com.changqing.gov.oauth.api;

import com.changqing.gov.oauth.api.hystrix.LogApiHystrix;
import com.changqing.gov.base.R;
import com.changqing.gov.log.entity.OptLogDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 操作日志保存 API
 *
 * @author changqing
 * @date 2019/07/02
 */
@FeignClient(name = "${changqing.feign.oauth-server:changqing-oauth-server}", fallback = LogApiHystrix.class, qualifier = "logApi")
public interface LogApi {
    
    /**
     * 保存日志
     *
     * @param log 日志
     * @return
     */
    @RequestMapping(value = "/optLog", method = RequestMethod.POST)
    R<OptLogDTO> save(@RequestBody OptLogDTO log);

}
