package com.changqing.gov.msgs.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.changqing.gov.base.R;
import com.changqing.gov.base.request.PageParams;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.msgs.dto.MsgsWarningInfoDTO;
import com.changqing.gov.msgs.entity.MsgsWarningInfo;
import com.changqing.gov.msgs.service.MsgsWarningInfoService;
import com.changqing.gov.security.annotation.LoginUser;
import com.changqing.gov.security.annotation.PreAuth;
import com.changqing.gov.security.model.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：Angular
 * @ProjectName: changqing-admin-cloud
 * @Package: com.changqing.gov.msgs.controller
 * @ClassName: MsgsWarningInfoController
 * @date ：Created in 2021/1/8 11:56
 * @description：待处理告警
 * @modified By：
 * @version: v1.0.0$
 */

@Slf4j
@RestController
@RequestMapping("/msgsDataWarning")
@Api(value = "MsgsWarningInfo", tags = "待处理告警")
@Validated
@PreAuth(replace = "msgs:")
public class MsgsWarningInfoController {
    @Autowired
    private MsgsWarningInfoService msgsWarningInfoService;

    @ApiOperation(value = "待处理告警分页数据查询")
    @RequestMapping(value = "/pageCheckData", method = RequestMethod.POST)
    @SysLog(value = "'分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
//    @PreAuth("hasPermit('{}view')")
    public R<JSONObject> pageCheckData(@RequestBody @Validated PageParams<MsgsWarningInfoDTO> params) {
        // 处理参数
        IPage<MsgsWarningInfo> page = params.buildPage();
        JSONObject resultObject = new JSONObject();
        List<MsgsWarningInfo> pageList = queryResult(params, page).getRecords();
        pageList.sort(Comparator.comparing(MsgsWarningInfo::getCreateTime));
        resultObject.put("records", pageList);
        resultObject.put("total", page.getTotal());
        resultObject.put("size", page.getSize());
        resultObject.put("current", page.getCurrent());
        resultObject.put("orders", page.orders());
        resultObject.put("optimizeCountSql", page.optimizeCountSql());
        resultObject.put("hitCount", page.isHitCount());
        resultObject.put("countId", page.countId());
        resultObject.put("maxLimit", page.maxLimit());
        resultObject.put("searchCount", page.isSearchCount());
        resultObject.put("pages", page.getPages());

        return R.success(resultObject);
    }

    private IPage<MsgsWarningInfo> queryResult(PageParams<MsgsWarningInfoDTO> params, IPage<MsgsWarningInfo> page) {
        MsgsWarningInfoDTO model = params.getModel();
        page = msgsWarningInfoService.queryResult(page, model);
        return page;
    }

    @ApiOperation(value = "待处理告警审核")
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    @SysLog(value = "'待处理告警审核:' + #updateDTO?.id", request = false)
    @DS("#thread.tenant")
//    @PreAuth("hasPermit('{}check')")
    public R<Boolean> check(@RequestBody @Validated Map<String, String> data,
                            @ApiIgnore @LoginUser SysUser user) {
        boolean ischecked = false;
        //获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String updateTime = simpleDateFormat.format(new Date());
        //查询并判断是否已经处理
        if (!"".equals(data.get("warningContent")) && data.get("warningContent") != null) {
            String isDealResult = msgsWarningInfoService.selectWarningCode(data.get("warningCode"), data.get("receivier"));
            if (isDealResult != null && Integer.parseInt(isDealResult) <= 0) {
                msgsWarningInfoService.insertMsgContent(data.get("warningCode"), data.get("warningContent"), updateTime);
                ischecked = msgsWarningInfoService.isDeal(data.get("warningCode"), data.get("receivier"), data.get("isDeal"), user.getId() + "", updateTime);
                return R.success(ischecked, "告警信息处理成功");
            } else {
                return R.success(ischecked, "告警信息已经审核通过");
            }
        } else {
            return R.success(ischecked, "信息审核失败,审核意见不可为空！");
        }

    }
}
