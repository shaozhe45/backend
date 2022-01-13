package com.changqing.gov.activiti.api.hystrix;

import com.changqing.gov.activiti.api.InstanceBizApi;
import com.changqing.gov.activiti.dto.activiti.InstantSelectSaveDTO;
import com.changqing.gov.base.R;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InstanceBizApiFallback implements InstanceBizApi {
    @Override
    public String save(InstantSelectSaveDTO dto) {
        return "";
    }

    @Override
    public String saveInstant(InstantSelectSaveDTO dto) {
        return "";
    }

    @Override
    public R processInstanceVariables(String instId, Map<String, Object> map) {
        return null;
    }

    @Override
    public Map<String, Object> getProcessInstanceVariables(String instId) {
        return null;
    }

    @Override
    public R processInstanceAppendVariables(String instId, Map<String, Object> map) {
        return null;
    }
}
