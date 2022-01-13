package com.changqing.gov.activiti.api;


import com.changqing.gov.activiti.api.hystrix.InstanceBizApiFallback;
import com.changqing.gov.activiti.dto.activiti.InstantSelectSaveDTO;
import com.changqing.gov.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Component
@FeignClient(name = "${changqing.feign.activiti-server:changqing-activiti-server}", fallback = InstanceBizApiFallback.class
        , path = "/instance", qualifier = "instanceBizApi")
public interface InstanceBizApi {

    @PostMapping(value = "/save")
    public String save(@RequestBody InstantSelectSaveDTO dto);

    @PostMapping(value = "/saveInstant")
    public String saveInstant(@RequestBody InstantSelectSaveDTO dto);

    @RequestMapping(value = "/processInstanceVariables", method = RequestMethod.POST)
    public R processInstanceVariables(@RequestParam(value = "instId") String instId, @RequestBody Map<String, Object> map);

    @RequestMapping(value = "/processInstanceAppendVariables", method = RequestMethod.POST)
    public R processInstanceAppendVariables(@RequestParam(value = "instId") String instId, @RequestBody Map<String, Object> map);

    @RequestMapping(value = "/getProcessInstanceVariables", method = RequestMethod.GET)
    public Map<String, Object> getProcessInstanceVariables(@RequestParam(value = "instId") String instId);


}
