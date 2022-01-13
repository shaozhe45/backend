package com.changqing.gov.jobs.api.fallback;

import com.changqing.gov.base.R;
import com.changqing.gov.jobs.api.JobsTimingApi;
import com.changqing.gov.jobs.dto.XxlJobInfo;
import org.springframework.stereotype.Component;

/**
 * 定时API 熔断
 *
 * @author changqing
 * @date 2019/07/16
 */
@Component
public class JobsTimingApiFallback implements JobsTimingApi {
    @Override
    public R<String> addTimingTask(XxlJobInfo xxlJobInfo) {
        return R.timeout();
    }
}
