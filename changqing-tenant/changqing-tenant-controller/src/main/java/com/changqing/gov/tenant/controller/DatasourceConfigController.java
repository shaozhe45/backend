package com.changqing.gov.tenant.controller;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.changqing.gov.base.R;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.log.annotation.SysLog;
import com.changqing.gov.security.annotation.PreAuth;
import com.changqing.gov.tenant.dto.DatasourceConfigPageDTO;
import com.changqing.gov.tenant.dto.DatasourceConfigSaveDTO;
import com.changqing.gov.tenant.dto.DatasourceConfigUpdateDTO;
import com.changqing.gov.tenant.entity.DatasourceConfig;
import com.changqing.gov.tenant.service.DatasourceConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 前端控制器
 * 数据源
 * </p>
 *
 * @author changqing
 * @date 2020-08-21
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/datasourceConfig")
@Api(value = "DatasourceConfig", tags = "数据源")
@PreAuth(replace = "datasourceConfig:", enabled = false)
@SysLog(enabled = false)
public class DatasourceConfigController extends SuperController<DatasourceConfigService, Long, DatasourceConfig, DatasourceConfigPageDTO, DatasourceConfigSaveDTO, DatasourceConfigUpdateDTO> {

    @ApiOperation(value = "测试数据库链接")
    @PostMapping("/testConnect")
    public R<Boolean> testConnect(@RequestBody DataSourceProperty dataSourceProperty) {
        return R.success(baseService.testConnection(dataSourceProperty));
    }
}
