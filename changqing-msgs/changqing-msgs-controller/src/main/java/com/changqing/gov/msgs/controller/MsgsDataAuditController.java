package com.changqing.gov.msgs.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.changqing.gov.base.R;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.msgs.dto.MsgsCenterInfoSaveDTO;
import com.changqing.gov.msgs.service.InsertHistoryForEsapDataApi;
import com.changqing.gov.msgs.service.MsgsCenterInfoService;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.controller
 * @ClassName: MsgsDataAuditController
 * @Author: Angular
 * @Description: 数据审核
 * @Date: 2021/1/6 16:49
 * @Version: 1.0
 */

@Slf4j
@RestController
@RequestMapping("/msgsDataAudit")
@Api(value = "MsgsDataAudit", tags = "数据审核")
@Validated
@PreAuth(replace = "msgs:")
public class MsgsDataAuditController {
    @Autowired
    private MsgsCenterInfoService msgsCenterInfoService;
    @Autowired
    private InsertHistoryForEsapDataApi insertHistoryForEsapDataApi;

    @ApiOperation(value = "数据审核")
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    @SysLog(value = "'数据审核:' + #updateDTO?.id", request = false)
    @DS("#thread.tenant")
//    @PreAuth("hasPermit('{}check')")
    public R<Boolean> check(@RequestBody @Validated Map<String, Object> params) {
        boolean ischecked = false;
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkTime = simpleDateFormat.format(new Date());
        params.put("checkTime",checkTime);
        ischecked = msgsCenterInfoService.isChecked(params);
        if (ischecked) {
            List<String> list = (List<String>) params.get("checkCode");
            for (String str : list) {
                Map<String, String> map = new HashMap<>();
                map.put("checkCode", str);
                insertHistoryForEsapDataApi.checkForHistoryData(map);
            }
            return R.success(ischecked, "信息审核成功");
        } else {
            return R.success(ischecked, "信息审核失败");
        }
    }

}
