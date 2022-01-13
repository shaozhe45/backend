package com.changqing.gov.authority.controller.common;


import com.changqing.gov.authority.dto.common.ParameterPageDTO;
import com.changqing.gov.authority.dto.common.ParameterSaveDTO;
import com.changqing.gov.authority.dto.common.ParameterUpdateDTO;
import com.changqing.gov.authority.entity.common.Parameter;
import com.changqing.gov.authority.service.common.ParameterService;
import com.changqing.gov.base.controller.SuperController;
import com.changqing.gov.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 参数配置
 * </p>
 *
 * @author changqing
 * @date 2020-02-05
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/parameter")
@Api(value = "Parameter", tags = "参数配置")
@PreAuth(replace = "parameter:")
public class ParameterController extends SuperController<ParameterService, Long, Parameter, ParameterPageDTO, ParameterSaveDTO, ParameterUpdateDTO> {

}
