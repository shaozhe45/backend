package com.changqing.gov.oauth.controller;


import com.changqing.gov.authority.entity.common.OptLog;
import com.changqing.gov.authority.service.common.OptLogService;
import com.changqing.gov.base.R;
import com.changqing.gov.log.entity.OptLogDTO;
import com.changqing.gov.utils.BeanPlusUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 系统日志
 * </p>
 *
 * @author changqing
 * @date 2019-07-22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/optLog")
@Api(value = "OptLog", tags = "系统日志")
@AllArgsConstructor
public class OauthOptLogController {

    private final OptLogService optLogService;

    /**
     * 保存系统日志
     *
     * @param data 保存对象
     * @return 保存结果
     */
    @PostMapping
    @ApiOperation(value = "保存系统日志", notes = "保存系统日志不为空的字段")
    public R<OptLog> save(@RequestBody OptLogDTO data) {
        optLogService.save(data);
        return R.success(BeanPlusUtil.toBean(data, OptLog.class));
    }

}
