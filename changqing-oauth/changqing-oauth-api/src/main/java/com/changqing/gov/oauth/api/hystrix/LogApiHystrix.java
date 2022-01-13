package com.changqing.gov.oauth.api.hystrix;

import com.changqing.gov.oauth.api.LogApi;
import com.changqing.gov.base.R;
import com.changqing.gov.log.entity.OptLogDTO;
import org.springframework.stereotype.Component;

/**
 * 日志操作 熔断
 *
 * @author changqing
 * @date 2019/07/02
 */
@Component
public class LogApiHystrix implements LogApi {
    @Override
    public R<OptLogDTO> save(OptLogDTO log) {
        return R.timeout();
    }
}
