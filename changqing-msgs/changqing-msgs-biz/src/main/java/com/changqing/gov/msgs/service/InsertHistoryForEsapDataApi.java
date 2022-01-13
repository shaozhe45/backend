package com.changqing.gov.msgs.service;

import com.changqing.gov.base.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author ：Angular
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.service
 * @InterfaceName: InsertHistoryForEsapDataApi
 * @date ：Created in 2021/1/12 15:34
 * @description：调用ESAP服务将审核信息添加到历史实时数据表
 * @modified By：
 * @version: v1.0.0$
 */
@FeignClient(name = "${tanbao.feign.check-server:tanbao-esap-server}",path ="/environmentAddjson")
public interface InsertHistoryForEsapDataApi {
    @PostMapping("/checkForHistoryData")
    R<String> checkForHistoryData(@RequestBody Map<String,String> data);
}
